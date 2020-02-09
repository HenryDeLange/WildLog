package wildlog.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogFileCore;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.system.WildLogDataType;
import wildlog.data.enums.system.WildLogFileType;
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.ui.helpers.ProgressbarTask;

public class UtilsCheckAndClean {

    private UtilsCheckAndClean() {
    }

    public static void doCheckAndClean(WildLogApp inApp, ProgressbarTask inProgressbarTask, 
            Set<Integer> inSelectedSteps, int inRecreateThumbnailsResult) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        inProgressbarTask.setTaskProgress(0);
        inProgressbarTask.setMessage("Workspace Cleanup starting...");
        Path feedbackFile = null;
        PrintWriter feedback = null;
        // Start cleanup
        try {
            // Setup the feedback file
            Files.createDirectories(WildLogPaths.WILDLOG_PROCESSES.getAbsoluteFullPath());
            feedbackFile = WildLogPaths.WILDLOG_PROCESSES.getAbsoluteFullPath().resolve(
                    "WorkspaceCleanupFeedback_" + UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(LocalDateTime.now()) + ".txt");
            feedback = new PrintWriter(new FileWriter(feedbackFile.toFile()), true);
            feedback.println("------------------------------------------------");
            feedback.println("---------- STARTING WORKSPACE CLEANUP ----------");
            feedback.println("------------------------------------------------");
            feedback.println("     (See the end of the file for a summary)    ");
            feedback.println("------------------------------------------------");
            feedback.println("");
            // Create a final reference to the feedback writer for use in inner classes, etc.
            final PrintWriter finalHandleFeedback = feedback;
            final CleanupHelper cleanupHelper = new CleanupHelper(inApp, inProgressbarTask, finalHandleFeedback);
            // Do a quick DB backup
            inApp.getDBI().doBackup(WildLogPaths.WILDLOG_BACKUPS_CHECK_AND_CLEAN.getAbsoluteFullPath()
                    .resolve(UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(LocalDateTime.now())));
            inProgressbarTask.setTaskProgress(1);
            finalHandleFeedback.println("** Starting Workspace Cleanup: " + UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(LocalDateTime.now()));
            // ---------------------1---------------------
            // Check the rest of the data for inconsistencies (all Locations, Visits, Creatures and Observations link correctly)
            int badDataLinks = 0;
            if (inSelectedSteps.contains(1)) {
                inProgressbarTask.setMessage("Cleanup Step 1: Check links between records in the database... " + inProgressbarTask.getProgress() + "%");
                finalHandleFeedback.println("");
                finalHandleFeedback.println("1) Make sure Places, Periods, Creatures and Observations all have correct links to each other.");
                // Check Elements
                List<Element> allElements = inApp.getDBI().listElements(null, null, null, false, Element.class);
                for (Element element : allElements) {
                    // Validate the ID is larger than 0
                    if (element.getID() <= 0) {
                        badDataLinks++;
                        finalHandleFeedback.println("PROBLEM:     The Creature had an incorrect ID. "
                                + "Creature: " + element.getPrimaryName());
                        finalHandleFeedback.println("  +RESOLVED: Generated a new ID.");
                        element.setID(inApp.getDBI().generateID());
                        inApp.getDBI().updateElement(element, element.getPrimaryName(), false);
                    }
                }
                // Check Locations
                List<Location> allLocations = inApp.getDBI().listLocations(null, false, Location.class);
                for (Location location : allLocations) {
                    // Validate the ID is larger than 0
                    if (location.getID() <= 0) {
                        badDataLinks++;
                        finalHandleFeedback.println("PROBLEM:     The Place had an incorrect ID. "
                                + "Place: " + location.getName());
                        finalHandleFeedback.println("  +RESOLVED: Generated a new ID.");
                        location.setID(inApp.getDBI().generateID());
                        inApp.getDBI().updateLocation(location, location.getName(), false);
                    }
                }
                // Check Visits
                List<Visit> allVisits = inApp.getDBI().listVisits(null, 0, null, true, Visit.class);
                int countVisits = 0;
                for (Visit visit : allVisits) {
                    // Validate the ID is larger than 0
                    if (visit.getID() <= 0) {
                        badDataLinks++;
                        finalHandleFeedback.println("PROBLEM:     The Period had an incorrect ID. "
                                + "Period: " + visit.getName());
                        finalHandleFeedback.println("  +RESOLVED: Generated a new ID.");
                        visit.setID(inApp.getDBI().generateID());
                        inApp.getDBI().updateVisit(visit, visit.getName(), false);
                    }
                    // Validate the Visit to Location link
                    Location temp = inApp.getDBI().findLocation(visit.getLocationID(), null, false, Location.class);
                    if (temp == null) {
                        badDataLinks++;
                        finalHandleFeedback.println("PROBLEM:     Could not find link between Period and Place. "
                                + "Period: " + visit.getName() + ", Place: " + visit.getCachedLocationName());
                        finalHandleFeedback.println("  +RESOLVED: Moved Period to a new Place called 'WildLog_lost_and_found'.");
                        Location newLocation = inApp.getDBI().findLocation(0, "WildLog_lost_and_found", false, Location.class);
                        if (newLocation == null) {
                            newLocation = new Location(0, "WildLog_lost_and_found");
                            inApp.getDBI().createLocation(newLocation, false);
                        }
                        visit.setLocationID(newLocation.getID());
                        // Still an issue with sightings not going to point to the correct place... (handled in the code below)
                        inApp.getDBI().updateVisit(visit, visit.getName(), false);
                    }
                    countVisits++;
                    inProgressbarTask.setTaskProgress(1 + (int) (countVisits / (double) allVisits.size() * 3));
                    inProgressbarTask.setMessage("Cleanup Step 1: Check links between records in the database... " + inProgressbarTask.getProgress() + "%");
                }
                // Check Sightings
                List<Sighting> allSightings = inApp.getDBI().listSightings(0, 0, 0, true, Sighting.class);
                int countSightings = 0;
                for (Sighting sighting : allSightings) {
                    // Validate the Sighting to Location link
                    Location tempLocation = inApp.getDBI().findLocation(sighting.getLocationID(), null, false, Location.class);
                    if (tempLocation == null) {
                        badDataLinks++;
                        finalHandleFeedback.println("PROBLEM:     Could not find link between Observation and Place. "
                                + "Observation: " + sighting.getID() + ", Place: " + sighting.getCachedLocationName());
                        finalHandleFeedback.println("  +RESOLVED: Moved Observation to a new Place called 'WildLog_lost_and_found'.");
                        Location newLocation = inApp.getDBI().findLocation(0, "WildLog_lost_and_found", false, Location.class);
                        if (newLocation == null) {
                            newLocation = new Location(0, "WildLog_lost_and_found");
                            inApp.getDBI().createLocation(newLocation, false);
                        }
                        sighting.setLocationID(newLocation.getID());
                        inApp.getDBI().updateSighting(sighting, false);
                    }
                    // Validate the Sighting to Element link
                    Element tempElement = inApp.getDBI().findElement(sighting.getElementID(), null, false, Element.class);
                    if (tempElement == null) {
                        badDataLinks++;
                        finalHandleFeedback.println("PROBLEM:     Could not find link between Observation and Creature. "
                                + "Observation: " + sighting.getID() + ", Creature: " + sighting.getCachedElementName());
                        finalHandleFeedback.println("  +RESOLVED: Moved Observation to a new Creature called 'WildLog_lost_and_found'.");
                        Element newElement = inApp.getDBI().findElement(0, "WildLog_lost_and_found", false, Element.class);
                        if (newElement == null) {
                            newElement = new Element(0, "WildLog_lost_and_found");
                            inApp.getDBI().createElement(newElement, false);
                        }
                        sighting.setElementID(newElement.getID());
                        inApp.getDBI().updateSighting(sighting, false);
                    }
                    // Validate the Sighting to Visit link
                    Visit tempVisit = inApp.getDBI().findVisit(sighting.getVisitID(), null, false, Visit.class);
                    if (tempVisit == null) {
                        badDataLinks++;
                        finalHandleFeedback.println("PROBLEM:     Could not find link between Observation and Period. "
                                + "Observation: " + sighting.getID() + ", Period: " + sighting.getCachedVisitName());
                        finalHandleFeedback.println("  +RESOLVED: Moved Observation to a new Period called 'WildLog_lost_and_found'.");
                        // Location
                        Location newLocation = inApp.getDBI().findLocation(0, "WildLog_lost_and_found", false, Location.class);
                        if (newLocation == null) {
                            newLocation = new Location(0, "WildLog_lost_and_found");
                            inApp.getDBI().createLocation(newLocation, false);
                        }
                        sighting.setLocationID(newLocation.getID());
                        // Visit
                        Visit newVisit = inApp.getDBI().findVisit(0, "WildLog_lost_and_found", false, Visit.class);
                        if (newVisit == null) {
                            newVisit = new Visit(0, "WildLog_lost_and_found");
                            newVisit.setLocationID(newLocation.getID());
                            inApp.getDBI().createVisit(newVisit, false);
                        }
                        sighting.setVisitID(newVisit.getID());
                        inApp.getDBI().updateSighting(sighting, false);
                    }
                    // Make sure the Sighting is using a legitimate Location-Visit pair
                    Visit checkSightingVisit = inApp.getDBI().findVisit(sighting.getVisitID(), null, true, Visit.class);
                    if (checkSightingVisit.getLocationID() != sighting.getLocationID()) {
                        badDataLinks++;
                        finalHandleFeedback.println("PROBLEM:     The Observation and Period references different Places. "
                                + "Observation: " + sighting.getCachedLocationName() + ", Period: " + checkSightingVisit.getCachedLocationName());
                        finalHandleFeedback.println("  +RESOLVED: Moved Observation and Period to a new Place called 'WildLog_lost_and_found'.");
                        Location newLocation = inApp.getDBI().findLocation(0, "WildLog_lost_and_found", false, Location.class);
                        if (newLocation == null) {
                            newLocation = new Location(0, "WildLog_lost_and_found");
                            inApp.getDBI().createLocation(newLocation, false);
                        }
                        // Update sighting
                        sighting.setLocationID(newLocation.getID());
                        inApp.getDBI().updateSighting(sighting, false);
                        // Update visit
                        checkSightingVisit.setLocationID(newLocation.getID());
                        inApp.getDBI().updateVisit(checkSightingVisit, checkSightingVisit.getName(), false);
                    }
                    countSightings++;
                    inProgressbarTask.setTaskProgress(4 + (int) (countSightings / (double) allSightings.size() * 4));
                    inProgressbarTask.setMessage("Cleanup Step 1: Check links between records in the database... " + inProgressbarTask.getProgress() + "%");
                }
            }
            inProgressbarTask.setTaskProgress(8);
            // ---------------------2---------------------
            // First check database files
            // Maak seker alle files in die tabel wys na 'n location/element/ens wat bestaan (geen "floaters" mag teenwoordig wees nie)
            int filesWithoutID = 0;
            int filesWithoutPath = 0;
            int filesNotOnDisk = 0;
            int filesWithMissingData = 0;
            int filesWithBadType = 0;
            int filesWithBadID = 0;
            int countImages = 0;
            int countMovies = 0;
            int countOther = 0;
            int badStashes = 0;
            int fileProcessCounter = 0;
            CleanupCounter filesMoved = new CleanupCounter();
            if (inSelectedSteps.contains(2)) {
                inProgressbarTask.setMessage("Cleanup Step 2: Validate database references to the files in the Workspace... " + inProgressbarTask.getProgress() + "%");
                finalHandleFeedback.println("");
                finalHandleFeedback.println("2) Make sure the File records in the database contain valid values and correctly link to existing data and Workspace files.");
                List<WildLogFile> allFiles = inApp.getDBI().listWildLogFiles(-1, null, WildLogFile.class);
                for (WildLogFile wildLogFile : allFiles) {
                    // Check the WildLogFile's content
                    if (wildLogFile.getLinkID() == 0) {
                        finalHandleFeedback.println("PROBLEM:     File record without a LinkID. FileID: " + wildLogFile.getID() + " | FilePath: " + wildLogFile.getDBFilePath() + " | FileLinkID: " + wildLogFile.getLinkID());
                        finalHandleFeedback.println("  +RESOLVED: Delete the database file record and file on disk.");
                        inApp.getDBI().deleteWildLogFile(wildLogFile.getID());
                        filesWithoutID++;
                        continue;
                    }
                    if (wildLogFile.getDBFilePath() == null) {
                        finalHandleFeedback.println("PROBLEM:     File path missing from database record. FileID: " + wildLogFile.getID() + " | FilePath: " + wildLogFile.getDBFilePath() + " | FileLinkID: " + wildLogFile.getLinkID());
                        finalHandleFeedback.println("  +RESOLVED: Deleted the database file record.");
                        inApp.getDBI().deleteWildLogFile(wildLogFile.getID());
                        filesWithoutPath++;
                        continue;
                    }
                    if (!Files.exists(wildLogFile.getAbsolutePath())) {
                        finalHandleFeedback.println("PROBLEM:     File record in the database can't be found on disk. FileID: " + wildLogFile.getID() + " | FilePath: " + wildLogFile.getDBFilePath() + " | FileLinkID: " + wildLogFile.getLinkID());
                        finalHandleFeedback.println("  +RESOLVED: Deleted the database file record.");
                        inApp.getDBI().deleteWildLogFile(wildLogFile.getID());
                        filesNotOnDisk++;
                        continue;
                    }
                    if (wildLogFile.getFilename() == null || wildLogFile.getFilename().isEmpty() || wildLogFile.getUploadDate() == null) {
                        finalHandleFeedback.println("WARNING:    Database file record missing data. FilePath: " + wildLogFile.getAbsolutePath()
                                + ", Filename: " + wildLogFile.getFilename()
                                + ", UploadDate: " + wildLogFile.getUploadDate());
                        finalHandleFeedback.println("  -UNRESOLVED: No action taken...");
                        filesWithMissingData++;
                    }
                    if (wildLogFile.getFileType() == null || WildLogFileType.NONE.equals(WildLogFileType.getEnumFromText(wildLogFile.getFileType().toString()))) {
                        finalHandleFeedback.println("PROBLEM:     Unknown FileType of database file record. FilePath: " + wildLogFile.getDBFilePath()
                                + ", FileType: " + wildLogFile.getFileType());
                        finalHandleFeedback.println("  +RESOLVED: Changed FileType to " + WildLogFileType.OTHER + ".");
                        wildLogFile.setFileType(WildLogFileType.OTHER);
                        inApp.getDBI().updateWildLogFile(wildLogFile, false);
                        filesWithBadType++;
                    }
                    // Check the WildLogFile's linkage
                    if (wildLogFile.getLinkType() == WildLogDataType.ELEMENT) {
                        // Make sure it is linked
                        final Element temp = inApp.getDBI().findElement(wildLogFile.getLinkID(), null, false, Element.class);
                        if (temp == null) {
                            finalHandleFeedback.println("PROBLEM:     Could not find linked Creature for this file record. FilePath: " + wildLogFile.getDBFilePath()
                                    + ", ID: " + wildLogFile.getID() + ", CreatureID Used: " + wildLogFile.getLinkID());
                            finalHandleFeedback.println("  +RESOLVED: Deleted the file database record and file from disk.");
                            inApp.getDBI().deleteWildLogFile(wildLogFile.getID());
                            filesWithBadID++;
                            continue;
                        }
                        // Make sure the file path is correct
                        cleanupHelper.moveFilesToCorrectFolders(temp,
                                wildLogFile,
                                Paths.get(Element.WILDLOG_FOLDER_PREFIX, temp.getPrimaryName()).normalize(), WildLogDataType.ELEMENT,
                                filesMoved);
                    }
                    else 
                    if (wildLogFile.getLinkType() == WildLogDataType.LOCATION) {
                        // Make sure it is linked
                        final Location temp = inApp.getDBI().findLocation(wildLogFile.getLinkID(), null, false, Location.class);
                        if (temp == null) {
                            finalHandleFeedback.println("PROBLEM:     Could not find linked Place for this file. FilePath: " + wildLogFile.getDBFilePath()
                                    + ", ID: " + wildLogFile.getID() + ", PlaceID Used: " + wildLogFile.getLinkID());
                            finalHandleFeedback.println("  +RESOLVED: Deleted the file database record and file from disk.");
                            inApp.getDBI().deleteWildLogFile(wildLogFile.getID());
                            filesWithBadID++;
                            continue;
                        }
                        // Make sure the file path is correct
                        cleanupHelper.moveFilesToCorrectFolders(temp,
                                wildLogFile,
                                Paths.get(Location.WILDLOG_FOLDER_PREFIX, temp.getName()).normalize(), WildLogDataType.LOCATION,
                                filesMoved);
                    }
                    else 
                    if (wildLogFile.getLinkType() == WildLogDataType.VISIT) {
                        // Make sure it is linked
                        final Visit temp = inApp.getDBI().findVisit(wildLogFile.getLinkID(), null, true, Visit.class);
                        if (temp == null) {
                            finalHandleFeedback.println("PROBLEM:     Could not find linked Period for this file record. FilePath: " + wildLogFile.getDBFilePath()
                                    + ", ID: " + wildLogFile.getID() + ", PeriodID Used: " + wildLogFile.getLinkID());
                            finalHandleFeedback.println("  +RESOLVED: Deleted the file databse record and file from disk.");
                            inApp.getDBI().deleteWildLogFile(wildLogFile.getID());
                            filesWithBadID++;
                            continue;
                        }
                        // Make sure the file path is correct
                        cleanupHelper.moveFilesToCorrectFolders(temp,
                                wildLogFile,
                                Paths.get(Visit.WILDLOG_FOLDER_PREFIX, temp.getCachedLocationName(), temp.getName()).normalize(), WildLogDataType.VISIT,
                                filesMoved);
                    }
                    else 
                    if (wildLogFile.getLinkType() == WildLogDataType.SIGHTING) {
                        // Make sure it is linked
                        Sighting temp = null;
                        try {
                            temp = inApp.getDBI().findSighting(wildLogFile.getLinkID(), true, Sighting.class);
                        }
                        catch (NumberFormatException ex) {
                            finalHandleFeedback.println("PROBLEM:       Can't get linked Observation's ID.");
                            finalHandleFeedback.println("  -UNRESOLVED: Try to continue to delete the file.");
                        }
                        if (temp == null) {
                            finalHandleFeedback.println("PROBLEM:     Could not find linked Observation for this file. FilePath: " + wildLogFile.getDBFilePath()
                                    + ", ID: " + wildLogFile.getID() + ", ObservationID Used: " + wildLogFile.getLinkID());
                            finalHandleFeedback.println("  +RESOLVED: Deleted the file database record and file from disk.");
                            inApp.getDBI().deleteWildLogFile(wildLogFile.getID());
                            filesWithBadID++;
                            continue;
                        }
                        // Make sure the file path is correct
                        cleanupHelper.moveFilesToCorrectFolders(temp,
                                wildLogFile,
                                Paths.get(Sighting.WILDLOG_FOLDER_PREFIX).resolve(temp.toPath()).normalize(), WildLogDataType.SIGHTING,
                                filesMoved);
                    }
                    else {
                        finalHandleFeedback.println("PROBLEM:     File ID [" + wildLogFile.getID() + "] does not have a correct LinkType [" + wildLogFile.getLinkType() + "], with LinkID [" + wildLogFile.getLinkID() + "].");
                        finalHandleFeedback.println("  +RESOLVED: Deleted the file database record and file from disk.");
                        inApp.getDBI().deleteWildLogFile(wildLogFile.getID());
                        filesWithBadID++;
                    }
                    fileProcessCounter++;
                    if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType())) {
                        countImages++;
                    }
                    else
                    if (WildLogFileType.MOVIE.equals(wildLogFile.getFileType())) {
                        countMovies++;
                    }
                    else
                    if (WildLogFileType.OTHER.equals(wildLogFile.getFileType())) {
                        countOther++;
                    }
                    inProgressbarTask.setTaskProgress(8 + (int) (fileProcessCounter / (double) allFiles.size() * 18));
                    inProgressbarTask.setMessage("Cleanup Step 2: Validate database references to the files in the Workspace... " + inProgressbarTask.getProgress() + "%");
                }
                inProgressbarTask.setTaskProgress(26);
                inProgressbarTask.setMessage("Cleanup Step 2: Validate database references to the stashed files in the Workspace... " + inProgressbarTask.getProgress() + "%");
// TODO: If a file wasn't found then try to use the existing name and FileDate and FileSize to guess the link (or is it better just to move the files to the lost folder???)
                // Maak seker die Stashed Visits het 'n Stashed Files folder.
                List<Visit> lstStashedVisits = inApp.getDBI().listVisits(null, 0, VisitType.STASHED, false, Visit.class);
                for (Visit visit : lstStashedVisits) {
                    if (!Files.isDirectory(WildLogPaths.WILDLOG_FILES_STASH.getAbsoluteFullPath().resolve(visit.getName()))) {
                        finalHandleFeedback.println("PROBLEM:     The Stashed Period in the database does not have corresponding Stashed Files: " + visit.getName());
                        finalHandleFeedback.println("  +RESOLVED: The Stashed Period was deleted. [" + visit.getID() + "]");
                        inApp.getDBI().deleteVisit(visit.getID());
                        badStashes++;
                    }
                }
            }
            inProgressbarTask.setTaskProgress(27);
            // ---------------------3---------------------
            // Validate that the Workspace files are in the database
            CleanupCounter filesNotInDB = new CleanupCounter();
            if (inSelectedSteps.contains(3)) {
                inProgressbarTask.setMessage("Cleanup Step 3: Validate that the Workspace files are in the database... " + inProgressbarTask.getProgress() + "%");
                finalHandleFeedback.println("");
                finalHandleFeedback.println("3) Make sure the files in the Workspace are present in the database.");
                // Secondly check the files on disk
                try {
                    // Kyk of al die files op die hardeskyf in die database bestaan
                    cleanupHelper.checkDiskFilesAreInDB(WildLogPaths.WILDLOG_FILES_IMAGES, filesNotInDB, countImages, (int) (countImages / (double) fileProcessCounter * 20));
                    cleanupHelper.checkDiskFilesAreInDB(WildLogPaths.WILDLOG_FILES_MOVIES, filesNotInDB, countMovies, (int) (countMovies / (double) fileProcessCounter * 20));
                    cleanupHelper.checkDiskFilesAreInDB(WildLogPaths.WILDLOG_FILES_OTHER, filesNotInDB, countOther, (int) (countOther / (double) fileProcessCounter * 20));
                    // Kyk of die Stashed Files folders 'n bestaande Visit het
                    File fileStashes = WildLogPaths.WILDLOG_FILES_STASH.getAbsoluteFullPath().toFile();
                    if (fileStashes != null && fileStashes.listFiles() != null) {
                        for (File stash : fileStashes.listFiles()) {
                            if (inApp.getDBI().countVisits(stash.getName(), 0) == 0) {
                                finalHandleFeedback.println("PROBLEM:     Stashed Files in Workspace are not linked to a Stashed Period in the database: " + stash.getAbsolutePath());
                                finalHandleFeedback.println("  +RESOLVED: Moved the Stashed Files from the Workspace to the LostFiles folder.");
                                // Move the file to the LostFiles folder (don't delete, because we might want the file back to re-upload, etc.) 
                                List<Path> lstPaths = UtilsFileProcessing.getPathsFromSelectedFile(new File[]{stash});
                                final List<Path> lstAllFiles = UtilsFileProcessing.getListOfFilesToImport(lstPaths, true);
                                for (Path stashedFile : lstAllFiles) {
                                    Path destination = WildLogPaths.WILDLOG_LOST_FILES.getAbsoluteFullPath().resolve(WildLogPaths.getFullWorkspacePrefix().relativize(stashedFile));
                                    while (Files.exists(destination)) {
                                        destination = destination.getParent().resolve("wl_" + destination.getFileName());
                                    }
                                    UtilsFileProcessing.copyFile(stashedFile, destination, false, false);
                                }
                                UtilsFileProcessing.deleteRecursive(stash);
                                badStashes++;
                            }
                        }
                    }
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, "Could not check all files on disk.");
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    finalHandleFeedback.println("PROBLEM:       Could not check all files on disk.");
                    finalHandleFeedback.println("  -UNRESOLVED: Unexpected error accessing file...");
                }
            }
            inProgressbarTask.setTaskProgress(47);
            // ---------------------4---------------------
            // By this time the file link should be "trusted" enough to use it to update inconsistent FileDate and FileSize values (not just empty ones)
            int filesWithIncorrectDate = 0;
            int filesWithIncorrectSize = 0;
            if (inSelectedSteps.contains(4)) {
                inProgressbarTask.setMessage("Cleanup Step 4: Check the file size and dates... " + inProgressbarTask.getProgress() + "%");
                finalHandleFeedback.println("");
                finalHandleFeedback.println("4) Compare the size and modified date of the files on disk to the values stored in the database.");
                List<WildLogFile> allFiles = inApp.getDBI().listWildLogFiles(-1, null, WildLogFile.class);
                fileProcessCounter = 0;
                for (WildLogFile wildLogFile : allFiles) {
                    try {
                        LocalDateTime actualFileDate = UtilsTime.getLocalDateTimeFromDate(UtilsImageProcessing.getDateFromFileDate(wildLogFile.getAbsolutePath()));
                        long actualFileSize = Files.size(wildLogFile.getAbsolutePath());
                        if (actualFileDate != null) {
                            if (wildLogFile.getFileDate() == null || !actualFileDate.isEqual(UtilsTime.getLocalDateTimeFromDate(wildLogFile.getFileDate()))) {
                                finalHandleFeedback.println("PROBLEM:     The Modified Date of the file on disk is not the same as the value stored in the database. "
                                        + "FilePath: " + wildLogFile.getAbsolutePath());
                                String oldDate;
                                if (wildLogFile.getFileDate() == null) {
                                    oldDate = "null";
                                }
                                else {
                                    oldDate = UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(UtilsTime.getLocalDateTimeFromDate(wildLogFile.getFileDate()));
                                }
                                finalHandleFeedback.println("  +RESOLVED: Updated the database Modified Date value of the file record. "
                                        + "Changed " + oldDate
                                        + " to " + UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(actualFileDate));
                                wildLogFile.setFileDate(UtilsTime.getDateFromLocalDateTime(actualFileDate));
                                inApp.getDBI().updateWildLogFile(wildLogFile, false);
                                filesWithIncorrectDate++;
                            }
                        }
                        if (actualFileSize > 0) {
                            if (actualFileSize != wildLogFile.getFileSize()) {
                                finalHandleFeedback.println("PROBLEM:     The File Size of the file on disk is not the same as the value stored in the database. "
                                        + "FilePath: " + wildLogFile.getAbsolutePath());
                                finalHandleFeedback.println("  +RESOLVED: Updated the database File Size value of the file record. "
                                        + "Changed " + wildLogFile.getFileSize() + " to " + actualFileSize);
                                wildLogFile.setFileSize(actualFileSize);
                                inApp.getDBI().updateWildLogFile(wildLogFile, false);
                                filesWithIncorrectSize++;
                            }
                        }
                    }
                    catch (IOException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, "Could not check the file size and date.");
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        finalHandleFeedback.println("PROBLEM:       Could not check the file size and date.");
                        finalHandleFeedback.println("  -UNRESOLVED: Unexpected error accessing file...");
                    }
                    inProgressbarTask.setTaskProgress(47 + (int) (fileProcessCounter++ / (double) allFiles.size() * 12));
                    inProgressbarTask.setMessage("Cleanup Step 4: Check the file size and dates... " + inProgressbarTask.getProgress() + "%");
                }
            }
            inProgressbarTask.setTaskProgress(69);
            // ---------------------5---------------------
            // As alles klaar is delete alle lee en temporary folders
            if (inSelectedSteps.contains(5)) {
                inProgressbarTask.setMessage("Cleanup Step 5: Delete empty folders in WildLog\\Files\\... " + inProgressbarTask.getProgress() + "%");
                finalHandleFeedback.println("");
                finalHandleFeedback.println("5) Delete all empty folders in the Workspace's Images, Movies and Other files folders.");
                try {
                    UtilsFileProcessing.deleteRecursiveOnlyEmptyFolders(WildLogPaths.WILDLOG_FILES.getAbsoluteFullPath().toFile());
                }
                catch (final IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, "Could not delete all empty folders.");
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    finalHandleFeedback.println("PROBLEM:       Could not delete all empty folders.");
                    finalHandleFeedback.println("  -UNRESOLVED: Unexpected error accessing file...");
                }
            }
            inProgressbarTask.setTaskProgress(71);
            // ---------------------6---------------------
            // Delete alle temporary/onnodige files en folders
            if (inSelectedSteps.contains(6)) {
                inProgressbarTask.setMessage("Cleanup Step 6: Delete exports... " + inProgressbarTask.getProgress() + "%");
                finalHandleFeedback.println("");
                finalHandleFeedback.println("6) Delete all exports.");
                try {
                    UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_EXPORT.getAbsoluteFullPath().toFile());
                }
                catch (final IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, "Could not delete export folders.");
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    finalHandleFeedback.println("PROBLEM:       Could not delete export folders.");
                    finalHandleFeedback.println("  -UNRESOLVED: Unexpected error accessing file...");
                }
            }
// FIXME: Die progress sprong is te groot (nou dat die thumbnail delete skuif na stap 9)
            inProgressbarTask.setTaskProgress(79);
            // ---------------------7---------------------
            // Check GPS Accuracy
            int badGPSAccuracy = 0;
            if (inSelectedSteps.contains(7)) {
                inProgressbarTask.setMessage("Cleanup Step 7: Check the GPS Accuracy values... " + inProgressbarTask.getProgress() + "%");
                finalHandleFeedback.println("");
                finalHandleFeedback.println("7) Check the GPS Accuracy values.");
                List<Sighting> allSightings = inApp.getDBI().listSightings(0, 0, 0, false, Sighting.class);
                int countGPSAccuracy = 0;
                for (Sighting sighting : allSightings) {
                    if ((sighting.getGPSAccuracy() == null || GPSAccuracy.NONE.equals(sighting.getGPSAccuracy()))
                            && sighting.getLatitude() != null && !Latitudes.NONE.equals(sighting.getLatitude())
                            && sighting.getLongitude() != null && !Longitudes.NONE.equals(sighting.getLongitude())) {
                        sighting.setGPSAccuracy(GPSAccuracy.AVERAGE);
                        sighting.setGPSAccuracyValue(GPSAccuracy.AVERAGE.getMaxMeters());
                        inApp.getDBI().updateSighting(sighting, false);
                        badGPSAccuracy++;
                        finalHandleFeedback.println("PROBLEM:     GPS information found without GPS Accuracy for Observation (" + sighting.getID() + ").");
                        finalHandleFeedback.println("  +RESOLVED: Set the GPS Accuracy to a default value of AVERAGE.");
                    }
                    else {
                        if ((sighting.getGPSAccuracy() != null && !GPSAccuracy.NONE.equals(sighting.getGPSAccuracy()))
                                && (sighting.getLatitude() == null || Latitudes.NONE.equals(sighting.getLatitude())
                                || sighting.getLongitude() == null || Longitudes.NONE.equals(sighting.getLongitude()))) {
                            sighting.setGPSAccuracy(GPSAccuracy.NONE);
                            sighting.setGPSAccuracyValue(GPSAccuracy.NONE.getMaxMeters());
                            inApp.getDBI().updateSighting(sighting, false);
                            badGPSAccuracy++;
                            finalHandleFeedback.println("PROBLEM:     GPS Accuracy information found without GPS location for Observation (" + sighting.getID() + ").");
                            finalHandleFeedback.println("  +RESOLVED: Set the GPS Accuracy to a value of NONE.");
                        }
                    }
                    if (sighting.getGPSAccuracy() != null && (sighting.getGPSAccuracyValue() < sighting.getGPSAccuracy().getMinMeters()
                            || sighting.getGPSAccuracyValue() > sighting.getGPSAccuracy().getMaxMeters())) {
                        sighting.setGPSAccuracyValue(sighting.getGPSAccuracy().getMaxMeters());
                        inApp.getDBI().updateSighting(sighting, false);
                        badGPSAccuracy++;
                        finalHandleFeedback.println("PROBLEM:     GPS Accuracy category information found with a GPS Accuracy Value outside its bounds for Observation (" + sighting.getID() + ").");
                        finalHandleFeedback.println("  +RESOLVED: Set the GPS Accuracy Value to the maximum value associated with the GPS Accuracy category.");
                    }
                    countGPSAccuracy++;
                    inProgressbarTask.setTaskProgress(79 + (int) (countGPSAccuracy / (double) allSightings.size() * 2));
                    inProgressbarTask.setMessage("Cleanup Step 7: Check the GPS Accuracy values... " + inProgressbarTask.getProgress() + "%");
                }
                List<Location> allLocations = inApp.getDBI().listLocations(null, false, Location.class);
                countGPSAccuracy = 0;
                for (Location location : allLocations) {
                    if ((location.getGPSAccuracy() == null || GPSAccuracy.NONE.equals(location.getGPSAccuracy()))
                            && location.getLatitude() != null && !Latitudes.NONE.equals(location.getLatitude())
                            && location.getLongitude() != null && !Longitudes.NONE.equals(location.getLongitude())) {
                        location.setGPSAccuracy(GPSAccuracy.AVERAGE);
                        location.setGPSAccuracyValue(GPSAccuracy.AVERAGE.getMaxMeters());
                        inApp.getDBI().updateLocation(location, location.getName(), false);
                        badGPSAccuracy++;
                        finalHandleFeedback.println("PROBLEM:     GPS information found without GPS Accuracy for Place (" + location.getName() + ").");
                        finalHandleFeedback.println("  +RESOLVED: Set the GPS Accuracy to a default value of AVERAGE.");
                    }
                    else {
                        if ((location.getGPSAccuracy() != null && !GPSAccuracy.NONE.equals(location.getGPSAccuracy()))
                                && (location.getLatitude() == null || Latitudes.NONE.equals(location.getLatitude())
                                || location.getLongitude() == null || Longitudes.NONE.equals(location.getLongitude()))) {
                            location.setGPSAccuracy(GPSAccuracy.NONE);
                            location.setGPSAccuracyValue(GPSAccuracy.NONE.getMaxMeters());
                            inApp.getDBI().updateLocation(location, location.getName(), false);
                            badGPSAccuracy++;
                            finalHandleFeedback.println("PROBLEM:     GPS Accuracy information found without GPS location for Place (" + location.getName() + ").");
                            finalHandleFeedback.println("  +RESOLVED: Set the GPS Accuracy to a value of NONE.");
                        }
                    }
                    if (location.getGPSAccuracy() != null && (location.getGPSAccuracyValue() < location.getGPSAccuracy().getMinMeters()
                            || location.getGPSAccuracyValue() > location.getGPSAccuracy().getMaxMeters())) {
                        location.setGPSAccuracyValue(location.getGPSAccuracy().getMaxMeters());
                        inApp.getDBI().updateLocation(location, location.getName(), false);
                        badGPSAccuracy++;
                        finalHandleFeedback.println("PROBLEM:     GPS Accuracy category information found with a GPS Accuracy Value outside its bounds for Place (" + location.getName() + ").");
                        finalHandleFeedback.println("  +RESOLVED: Set the GPS Accuracy Value to the maximum value associated with the GPS Accuracy category.");
                    }
                    countGPSAccuracy++;
                    inProgressbarTask.setTaskProgress(81 + (int) (countGPSAccuracy / (double) allSightings.size() * 2));
                    inProgressbarTask.setMessage("Cleanup Step 7: Check the GPS Accuracy values... " + inProgressbarTask.getProgress() + "%");
                }
            }
            inProgressbarTask.setTaskProgress(83);
            // ---------------------8---------------------
            // Checks Visit dates
            int badVisitDates = 0;
            if (inSelectedSteps.contains(8)) {
                inProgressbarTask.setMessage("Cleanup Step 8: Check the Period and linked Observation date ranges... " + inProgressbarTask.getProgress() + "%");
                finalHandleFeedback.println("");
                finalHandleFeedback.println("8) Check the Period and linked Observation date ranges.");
                List<Sighting> allSightings = inApp.getDBI().listSightings(0, 0, 0, false, Sighting.class);
                Collections.sort(allSightings, new Comparator<Sighting>() {
                    @Override
                    public int compare(Sighting inSighting1, Sighting inSighting2) {
                        return Long.compare(inSighting1.getVisitID(), inSighting2.getVisitID());
                    }
                });
                int linkCount = 0;
                Set<Long> processedVisits = new HashSet<>();
                for (Sighting sighting : allSightings) {
                    Visit visit = inApp.getDBI().findVisit(sighting.getVisitID(), null, true, Visit.class);
                    if (processedVisits.add(visit.getID())) {
                        if (visit.getStartDate() == null) {
                            finalHandleFeedback.println("WARNING:   The Period (" + visit.getName() + ") does not have a Start Date.");
                            finalHandleFeedback.println("  -UNRESOLVED: It is recommended for all Periods to have atleast a Start Date. The dates are used by the Charts and Maps.");
                            badVisitDates++;
                        }
                        if (visit.getStartDate() == null && visit.getEndDate() != null) {
                            finalHandleFeedback.println("WARNING:   The Period (" + visit.getName() + ") has an End Date but does not have a Start Date.");
                            finalHandleFeedback.println("  -UNRESOLVED: It is recommended for all Periods with an End Date to also have a Start Date.");
                            badVisitDates++;
                        }
                        if (visit.getStartDate() != null && visit.getEndDate() != null && visit.getEndDate().before(visit.getStartDate())) {
                            finalHandleFeedback.println("WARNING:   The Period (" + visit.getName() + ") has an End Date that is before the Start Date.");
                            finalHandleFeedback.println("  -UNRESOLVED: It is recommended for all Periods to have a valid date range.");
                            badVisitDates++;
                        }
                    }
                    LocalDate sightingDate = UtilsTime.getLocalDateFromDate(sighting.getDate());
                    if ((visit.getStartDate() != null && sightingDate.isBefore(UtilsTime.getLocalDateFromDate(visit.getStartDate())))
                            || (visit.getEndDate() != null && sightingDate.isAfter(UtilsTime.getLocalDateFromDate(visit.getEndDate())))) {
                        finalHandleFeedback.println("WARNING:   The date for Observation (" + sighting.getID() + ") does not fall within the dates from Period (" + visit.getName() + ").");
                        finalHandleFeedback.println("  -UNRESOLVED: It is recommended for all Observations to use dates that fall within the Start date and End Date of the linked Period.");
                        badVisitDates++;
                    }
                    linkCount++;
                    inProgressbarTask.setTaskProgress(83 + (int) (linkCount / (double) allSightings.size() * 2));
                    inProgressbarTask.setMessage("Cleanup Step 8: Check the Period and linked Observation date ranges... " + inProgressbarTask.getProgress() + "%");
                }
            }
            inProgressbarTask.setTaskProgress(85);
            // ---------------------9---------------------
            // Re-create die default thumbnails
            if (inSelectedSteps.contains(9)) {
// FIXME: Die delete moet ook progress wys
                inProgressbarTask.setMessage("Cleanup Step 9: Delete thumbnails... " + inProgressbarTask.getProgress() + "%");
                if (inRecreateThumbnailsResult >= 0 && inRecreateThumbnailsResult <= 2) {
                    try {

                        UtilsFileProcessing.deleteRecursive(WildLogPaths.WILDLOG_THUMBNAILS.getAbsoluteFullPath().toFile());
                    }
                    catch (final IOException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, "Could not delete thumbnail folders.");
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        finalHandleFeedback.println("PROBLEM:       Could not delete thumbnail folders.");
                        finalHandleFeedback.println("  -UNRESOLVED: Unexpected error accessing file...");
                    }
                }
                if (inRecreateThumbnailsResult == 0) {
                    // Recreate essential thumbnails
                    inProgressbarTask.setMessage("Cleanup Step 9: Recreating essential default thumbnails (finding files)... " + inProgressbarTask.getProgress() + "%");
                    finalHandleFeedback.println("");
                    finalHandleFeedback.println("9) Recreate the default thumbnails for essential images.");
                    List<WildLogFile> listFiles = new ArrayList<>(128);
                    List<Element> lstElements = inApp.getDBI().listElements(null, null, null, false, Element.class);
                    for (Element element : lstElements) {
                        WildLogFile image = inApp.getDBI().findWildLogFile(0, element.getWildLogFileID(), WildLogFileType.IMAGE, null, WildLogFile.class);
                        if (image != null) {
                            listFiles.add(image);
                        }
                    }
                    List<Location> lstLocation = inApp.getDBI().listLocations(null, false, Location.class);
                    for (Location location : lstLocation) {
                        WildLogFile image = inApp.getDBI().findWildLogFile(0, location.getWildLogFileID(), WildLogFileType.IMAGE, null, WildLogFile.class);
                        if (image != null) {
                            listFiles.add(image);
                        }
                    }
                    List<Visit> lstVisit = inApp.getDBI().listVisits(null, 0, null, false, Visit.class);
                    for (Visit visit : lstVisit) {
                        WildLogFile image = inApp.getDBI().findWildLogFile(0, visit.getWildLogFileID(), WildLogFileType.IMAGE, null, WildLogFile.class);
                        if (image != null) {
                            listFiles.add(image);
                        }
                    }
                    inProgressbarTask.setMessage("Cleanup Step 9: Recreating essential default thumbnails (setup workers)... " + inProgressbarTask.getProgress() + "%");
                    ExecutorService executorService = Executors.newFixedThreadPool(
                            (int) Math.round(((double) inApp.getThreadCount()) / 2.0), new NamedThreadFactory("WL_CleanWorkspace"));
                    final CleanupCounter countThumbnails = new CleanupCounter();
                    for (final WildLogFile wildLogFile : listFiles) {
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                // Maak net die nodigste thumbnails, want anders vat dinge donners lank
                                wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.S0060_VERY_SMALL);
                                wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.S0100_SMALL);
                                wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.S0150_MEDIUM_SMALL);
                                wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.S0200_MEDIUM);
                                wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.S0300_NORMAL);
                                // Not going to bother with synchornization here, since it's just the progress bar
                                countThumbnails.counter++;
                                inProgressbarTask.setTaskProgress(85 + (int) (countThumbnails.counter / (double) listFiles.size() * 14));
                                inProgressbarTask.setMessage("Cleanup Step 9: Recreating essential default thumbnails... " + inProgressbarTask.getProgress() + "%");
                            }
                        });
                    }
                    for (WildLogSystemImages systemFile : WildLogSystemImages.values()) {
                        for (WildLogThumbnailSizes size : WildLogThumbnailSizes.values()) {
                            systemFile.getWildLogFile().getAbsoluteThumbnailPath(size);
                        }
                    }
                    // Don't use UtilsConcurency.waitForExecutorToShutdown(executorService), because this might take much-much longer
                    executorService.shutdown();
                    if (!executorService.awaitTermination(2, TimeUnit.DAYS)) {
                        finalHandleFeedback.println("PROBLEM:       Processing the thumbnails took too long.");
                        finalHandleFeedback.println("  -UNRESOLVED: Thumbnails can be created on demand by the application.");
                    }
                    inProgressbarTask.setTaskProgress(99);
                }
                else 
                if (inRecreateThumbnailsResult == 1 || inRecreateThumbnailsResult == 3) {
                    // Recreate all thumbnails or create missing thumbnails
                    inProgressbarTask.setMessage("Cleanup Step 9: Recreating all default thumbnails (finding files)... " + inProgressbarTask.getProgress() + "%");
                    finalHandleFeedback.println("");
                    finalHandleFeedback.println("9) Recreate the default thumbnails for all images.");
                    List<WildLogFile> listFiles = inApp.getDBI().listWildLogFiles(-1, WildLogFileType.IMAGE, WildLogFile.class);
                    // Sort the files by name to put the related location, element, visit and sighting images close to one another, 
                    // otherwise the loading from disk actually gets worse because the files are all over the place.
                    // This way the read-ahead will get more hits and be faster.
                    inProgressbarTask.setMessage("Cleanup Step 9: Recreating all default thumbnails (sorting files)... " + inProgressbarTask.getProgress() + "%");
                    Collections.sort(listFiles, new Comparator<WildLogFileCore>() {
                        @Override
                        public int compare(WildLogFileCore wlFile1, WildLogFileCore wlFile2) {
                            String prioritisedID1 = wlFile1.getLinkType().toString() + wlFile1.getDBFilePath();
                            String prioritisedID2 = wlFile2.getLinkType().toString() + wlFile2.getDBFilePath();
                            // Maak seker Visits word voor Sightings gedoen
                            if (prioritisedID1.charAt(0) == 'V') {
                                prioritisedID1 = "LZZZ" + prioritisedID1;
                            }
                            if (prioritisedID2.charAt(0) == 'V') {
                                prioritisedID2 = "LZZZ" + prioritisedID2;
                            }
                            return prioritisedID1.compareTo(prioritisedID2);
                        }
                    });
                    inProgressbarTask.setMessage("Cleanup Step 9: Recreating all default thumbnails (setup workers)... " + inProgressbarTask.getProgress() + "%");
                    ExecutorService executorService = Executors.newFixedThreadPool(
                            (int) Math.round(((double) inApp.getThreadCount()) / 2.0), new NamedThreadFactory("WL_CleanWorkspace"));
                    final CleanupCounter countThumbnails = new CleanupCounter();
                    for (final WildLogFile wildLogFile : listFiles) {
                        executorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                // Maak net die nodigste thumbnails, want anders vat dinge donners lank
                                wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.S0060_VERY_SMALL);
                                wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.S0100_SMALL);
                                wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.S0150_MEDIUM_SMALL);
                                wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.S0200_MEDIUM);
                                wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.S0300_NORMAL);
                                // Not going to bother with synchornization here, since it's just the progress bar
                                countThumbnails.counter++;
                                inProgressbarTask.setTaskProgress(85 + (int) (countThumbnails.counter / (double) listFiles.size() * 14));
                                inProgressbarTask.setMessage("Cleanup Step 9: Recreating all default thumbnails... " + inProgressbarTask.getProgress() + "%");
                            }
                        });
                    }
                    for (WildLogSystemImages systemFile : WildLogSystemImages.values()) {
                        for (WildLogThumbnailSizes size : WildLogThumbnailSizes.values()) {
                            systemFile.getWildLogFile().getAbsoluteThumbnailPath(size);
                        }
                    }
                    // Don't use UtilsConcurency.waitForExecutorToShutdown(executorService), because this might take much-much longer
                    executorService.shutdown();
                    if (!executorService.awaitTermination(5, TimeUnit.DAYS)) {
                        finalHandleFeedback.println("PROBLEM:       Processing the thumbnails took too long.");
                        finalHandleFeedback.println("  -UNRESOLVED: Thumbnails can be created on demand by the application.");
                    }
                }
                else {
                    inProgressbarTask.setTaskProgress(99);
                    inProgressbarTask.setMessage("Cleanup Step 9: Recreating all default thumbnails... SKIPPED " + inProgressbarTask.getProgress() + "%");
                    finalHandleFeedback.println("");
                    finalHandleFeedback.println("9) Recreate the default thumbnails for all images. SKIPPED");
                }
            }
            inProgressbarTask.setTaskProgress(99);
            // ---------------------10---------------------
            // For WEI, set the camera model as a tag
            if (inSelectedSteps.contains(10)) {
                if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_ADMIN
                        || WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
                    inProgressbarTask.setTaskProgress(99);
                    inProgressbarTask.setMessage("Cleanup Step 10: Set the camera model as the Observation's Tag... " + inProgressbarTask.getProgress() + "%");
                    finalHandleFeedback.println("");
                    finalHandleFeedback.println("10) Set the camera model as the Observation's Tag.");
                    List<Sighting> allSightings = inApp.getDBI().listSightings(0, 0, 0, false, Sighting.class);
                    for (Sighting sighting : allSightings) {
                        try {
                            String oldTag = sighting.getTag();
                            if (oldTag == null || oldTag.trim().isEmpty()) {
                                oldTag = "";
                            }
                            sighting.setTag("");
                            List<WildLogFile> lstFiles = inApp.getDBI().listWildLogFiles(sighting.getID(), WildLogFileType.IMAGE, WildLogFile.class);
                            Set<String> setCameraNames = new HashSet<>();
                            for (WildLogFile wildLogFile : lstFiles) {
                                String cameraName = UtilsImageProcessing.getExifCameraNameFromJpeg(wildLogFile.getAbsolutePath());
                                if (cameraName != null && !cameraName.trim().isEmpty()) {
                                    setCameraNames.add(cameraName);
                                }
                            }
                            for (String cameraName : setCameraNames) {
                                sighting.setTag((sighting.getTag() + " " + cameraName).trim());
                            }
                            if (!oldTag.equals(sighting.getTag())) {
                                inApp.getDBI().updateSighting(sighting, false);
                                badDataLinks++;
                                finalHandleFeedback.println("PROBLEM:     The Observation's Tag field did not show the correct camera name. "
                                        + "Observation: " + sighting.getID() + ", Old Tag: " + oldTag);
                                finalHandleFeedback.println("  +RESOLVED: Changed the value to: " + sighting.getTag());
                            }
                        }
                        catch (Exception ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, "Could not get the camera model for the Sighting.");
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            finalHandleFeedback.println("PROBLEM:       Could not get the camera model for the Observation.");
                            finalHandleFeedback.println("  -UNRESOLVED: Unexpected error...");
                        }
                    }
                }
            }
            // --------------------- DONE ---------------------
            // Write out the summary
            finalHandleFeedback.println("");
            finalHandleFeedback.println("** Finished Workspace Cleanup: " + UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(LocalDateTime.now()));
            finalHandleFeedback.println("");
            finalHandleFeedback.println("+++++++++++++++++++ SUMMARY ++++++++++++++++++++");
            finalHandleFeedback.println("Files on disk moved to new folder: " + filesMoved.counter);
            finalHandleFeedback.println("Database file records with no reference to a file on disk: " + filesWithoutPath);
            finalHandleFeedback.println("Database file records with a reference to a file on disk, but the file was not found: " + filesNotOnDisk);
            finalHandleFeedback.println("Database file records with no ID: " + filesWithoutID);
            finalHandleFeedback.println("Database file records with incorrect ID: " + filesWithBadID);
            finalHandleFeedback.println("Database file records with incorrect type: " + filesWithBadType);
            finalHandleFeedback.println("Database file records with missing non-essential data: " + filesWithMissingData);
            finalHandleFeedback.println("Workspace files not found in the database: " + filesNotInDB.counter);
            finalHandleFeedback.println("Problems with stashed files: " + badStashes);
            finalHandleFeedback.println("Database file records with an incorrect File Modified Date stored in the database: " + filesWithIncorrectDate);
            finalHandleFeedback.println("Database file records with an incorrect File Size stored in the database: " + filesWithIncorrectSize);
            finalHandleFeedback.println("Incorrect links between database records: " + badDataLinks);
            finalHandleFeedback.println("Records with incorrect GPS Accuracy: " + badGPSAccuracy);
            finalHandleFeedback.println("Periods with problematic Start Date and End Date values: " + badVisitDates);
            finalHandleFeedback.println("");
            finalHandleFeedback.println("+++++++++++++++++++ DURATION +++++++++++++++++++");
            long duration = System.currentTimeMillis() - startTime;
            int hours = (int) (((double) duration) / (1000.0 * 60.0 * 60.0));
            int minutes = (int) (((double) duration - (hours * 60 * 60 * 1000)) / (1000.0 * 60.0));
            int seconds = (int) (((double) duration - (hours * 60 * 60 * 1000) - (minutes * 60 * 1000)) / (1000.0));
            feedback.println(hours + " hours, " + minutes + " minutes, " + seconds + " seconds");
            // Print info to logs aswell (for upload)
            WildLogApp.LOGGER.log(Level.INFO, "+++++++++++++++++++ SUMMARY ++++++++++++++++++++");
            WildLogApp.LOGGER.log(Level.INFO, "Files on disk moved to new folder: {}", filesMoved.counter);
            WildLogApp.LOGGER.log(Level.INFO, "Database file records with no reference to a file on disk: {}", filesWithoutPath);
            WildLogApp.LOGGER.log(Level.INFO, "Database file records with a reference to a file on disk, but the file was not found: {}", filesNotOnDisk);
            WildLogApp.LOGGER.log(Level.INFO, "Database file records with no ID: {}", filesWithoutID);
            WildLogApp.LOGGER.log(Level.INFO, "Database file records with incorrect ID: {}", filesWithBadID);
            WildLogApp.LOGGER.log(Level.INFO, "Database file records with incorrect type: {}", filesWithBadType);
            WildLogApp.LOGGER.log(Level.INFO, "Database file records with missing non-essential data: {}", filesWithMissingData);
            WildLogApp.LOGGER.log(Level.INFO, "Workspace files not found in the database: {}", filesNotInDB.counter);
            WildLogApp.LOGGER.log(Level.INFO, "Problems with stashed files: " + badStashes);
            WildLogApp.LOGGER.log(Level.INFO, "Database file records with an incorrect File Modified Date: {}", filesWithIncorrectDate);
            WildLogApp.LOGGER.log(Level.INFO, "Database file records with an incorrect File Size: {}", filesWithIncorrectSize);
            WildLogApp.LOGGER.log(Level.INFO, "Incorrect links between database records: {}", badDataLinks);
            WildLogApp.LOGGER.log(Level.INFO, "Records with incorrect GPS Accuracy: {}", badGPSAccuracy);
            WildLogApp.LOGGER.log(Level.INFO, "Periods with problematic Start Date and End Date values: {}", badVisitDates);
            WildLogApp.LOGGER.log(Level.INFO, "+++++++++++++++++++ DURATION +++++++++++++++++++");
            WildLogApp.LOGGER.log(Level.INFO, "{} hours, {} minutes, {} seconds", new Object[]{hours, minutes, seconds});
            inProgressbarTask.setMessage("Finished Workspace Cleanup...");
            inProgressbarTask.setTaskProgress(100);
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "An exception occured while cleaning the Workspace!!");
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            if (feedback != null) {
                feedback.println("PROBLEM:       An exception occured while cleaning the Workspace!!");
                feedback.println("  -UNRESOLVED: Unexpected error... " + ex.getMessage());
            }
        }
        finally {
            if (feedback != null) {
                feedback.println("");
                feedback.println("------------------------------------------------");
                feedback.println("---------- FINISHED WORKSPACE CLEANUP ----------");
                feedback.println("------------------------------------------------");
                feedback.println("");
                feedback.flush();
                feedback.close();
            }
        }
        // Open the summary document
        if (feedbackFile != null) {
            UtilsFileProcessing.openFile(feedbackFile);
        }
    }

    // Setup helper classes (Op hierdie stadium wil ek al die code op een plek hou, ek kan dit later in Util methods in skuif of iets...)
    private static class CleanupCounter {
        public int counter = 0;
    }

    private static class CleanupHelper {
        private final WildLogApp app;
        private final ProgressbarTask progressbarTask;
        private final PrintWriter finalHandleFeedback;

        public CleanupHelper(WildLogApp inApp, ProgressbarTask inProgressbarTask, PrintWriter inFinalHandleFeedback) {
            app = inApp;
            progressbarTask = inProgressbarTask;
            finalHandleFeedback = inFinalHandleFeedback;
        }

        private void doTheMove(DataObjectWithWildLogFile inDAOWithFile, Path inExpectedPath, Path inExpectedPrefix, WildLogDataType inLinkType,
                WildLogFile inWildLogFile, final CleanupCounter fileCount) {
            Path shouldBePath = inExpectedPath.resolve(inExpectedPrefix);
            Path currentPath = inWildLogFile.getAbsolutePath().getParent();
            boolean renameBasedOnSighting = false;
            String fileName = inWildLogFile.getRelativePath().getFileName().toString();
            if (inDAOWithFile instanceof Sighting) {
                String currentName = fileName.substring(0, fileName.lastIndexOf('.'));
                // Remove the sequence number (if present) and original filename from the name,
                // to make sure we compare the expected names without the sequence number interfering
                int indexSeq = currentName.lastIndexOf(UtilsFileProcessing.INDICATOR_SEQ);
                if (indexSeq > 0) {
                    currentName = currentName.substring(0, indexSeq);
                }
                int indexFile = currentName.lastIndexOf(UtilsFileProcessing.INDICATOR_FILE);
                if (indexFile > 0) {
                    currentName = currentName.substring(0, indexFile);
                }
                // Get the dates to use for the expected name
                LocalDateTime fileDate = UtilsTime.getLocalDateTimeFromDate(UtilsImageProcessing.getDateFromFile(inWildLogFile.getAbsolutePath()));
                List<WildLogFile> lstGroupedFiles = app.getDBI().listWildLogFiles(inDAOWithFile.getWildLogFileID(), null, WildLogFile.class);
                LocalDateTime firstFileDate = null;
                for (WildLogFile wildLogFile : lstGroupedFiles) {
                    LocalDateTime groupedFileDate = UtilsTime.getLocalDateTimeFromDate(UtilsImageProcessing.getDateFromFile(wildLogFile.getAbsolutePath()));
                    if (firstFileDate == null || (groupedFileDate != null && groupedFileDate.isBefore(firstFileDate))) {
                        firstFileDate = groupedFileDate;
                    }
                }
                // Compare the actual and expected names
                if (!((Sighting) inDAOWithFile).getCustomFileName(firstFileDate, fileDate).equals(currentName)) {
                    renameBasedOnSighting = true;
                    fileName = ((Sighting) inDAOWithFile).getCustomFileName(firstFileDate, fileDate) + fileName.substring(fileName.lastIndexOf('.'));
                }
            }
            if (!shouldBePath.equals(currentPath) || renameBasedOnSighting) {
                finalHandleFeedback.println("PROBLEM:     Incorrect or outdated file path   : " + inWildLogFile.getAbsolutePath());
                finalHandleFeedback.println("  +RESOLVED: Moved the file to the correct path: " + shouldBePath.resolve(fileName));
                // "Re-upload" the file to the correct location
                UtilsFileProcessing.performFileUpload(
                        inDAOWithFile,
                        inExpectedPrefix, inLinkType,
                        new File[]{inWildLogFile.getAbsolutePath().toFile()},
                        null,
                        app, false, null, false, true);
                // Delete the wrong entry
                app.getDBI().deleteWildLogFile(inWildLogFile.getID());
                fileCount.counter++;
            }
        }

        public void moveFilesToCorrectFolders(DataObjectWithWildLogFile inDAOWithFile, WildLogFile inWildLogFile, Path inPrefix, WildLogDataType inLinkType, final CleanupCounter fileCount) {
            // Check to make sure the parent paths are correct, if not then move the file to the correct place and add a new DB entry before deleting the old one
            // Maak seker alle DB paths is relative (nie absolute nie) en begin met propper WL roots
            if (WildLogFileType.IMAGE.equals(inWildLogFile.getFileType())) {
                doTheMove(inDAOWithFile,
                        WildLogPaths.WILDLOG_FILES_IMAGES.getAbsoluteFullPath(),
                        inPrefix, inLinkType,
                        inWildLogFile,
                        fileCount);
            }
            else if (WildLogFileType.MOVIE.equals(inWildLogFile.getFileType())) {
                doTheMove(inDAOWithFile,
                        WildLogPaths.WILDLOG_FILES_MOVIES.getAbsoluteFullPath(),
                        inPrefix, inLinkType,
                        inWildLogFile,
                        fileCount);
            }
            else {
                doTheMove(inDAOWithFile,
                        WildLogPaths.WILDLOG_FILES_OTHER.getAbsoluteFullPath(),
                        inPrefix, inLinkType,
                        inWildLogFile,
                        fileCount);
            }
        }

        public void checkDiskFilesAreInDB(WildLogPaths inWildLogPaths, final CleanupCounter fileCount, final int inFileProcessCounter, final int inTotalToIncrease) throws IOException {
            final int baseProgress = progressbarTask.getProgress();
            final CleanupCounter counter = new CleanupCounter();
            Files.walkFileTree(inWildLogPaths.getAbsoluteFullPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path originalFile, BasicFileAttributes attrs) throws IOException {
                    if (attrs.isRegularFile()) {
                        // Kyk in DB of die path bestaan, as dit nie daar is nie delete die file
// TODO: As die file bestaan maar nie in die DB link nie kan mens dalk probeer "slim" wees en die link afly deur ander info soos size, date en die path...
                        WildLogFile wildLogFile = new WildLogFile();
                        wildLogFile.setDBFilePath(WildLogPaths.getFullWorkspacePrefix().relativize(originalFile).toString());
                        if (app.getDBI().findWildLogFile(0, 0, null, wildLogFile.getDBFilePath(), WildLogFile.class) == null) {
                            finalHandleFeedback.println("PROBLEM:     File in Workspace not present in the database: " + wildLogFile.getAbsolutePath());
                            finalHandleFeedback.println("  +RESOLVED: Moved the file from the Workspace to the LostFiles folder: " + wildLogFile.getDBFilePath());
                            // Move the file to the LostFiles folder (don't delete, because we might want the file back to re-upload, etc.) 
                            Path destination = WildLogPaths.WILDLOG_LOST_FILES.getAbsoluteFullPath().resolve(WildLogPaths.getFullWorkspacePrefix().relativize(originalFile));
                            while (Files.exists(destination)) {
                                destination = destination.getParent().resolve("wl_" + destination.getFileName());
                            }
                            UtilsFileProcessing.copyFile(originalFile, destination, false, true);
                            UtilsFileProcessing.deleteRecursiveOnlyEmptyFolders(originalFile.toFile());
                            fileCount.counter++;
                        }
                    }
                    // Assuming there are more or less as many files left as what was processed in step 1
                    if (counter.counter < inFileProcessCounter) {
                        counter.counter++;
                    }
                    progressbarTask.setTaskProgress(baseProgress + (int) (counter.counter / (double) inFileProcessCounter * inTotalToIncrease));
                    progressbarTask.setMessage("Cleanup Step 3: Validate that the Workspace files are in the database... " + progressbarTask.getProgress() + "%");
                    return FileVisitResult.CONTINUE;
                }
            });
        }

    }

}
