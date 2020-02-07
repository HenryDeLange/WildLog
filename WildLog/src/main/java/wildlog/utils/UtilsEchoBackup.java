package wildlog.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.ui.helpers.ProgressbarTask;

public class UtilsEchoBackup {

    private UtilsEchoBackup() {
    }

    public static boolean doEchoBackup(WildLogApp inApp, ProgressbarTask inProgressbarTask, Path inEchoPath) throws InterruptedException {
        boolean hasError = false;
        try {
            long startTime = System.currentTimeMillis();
            // Make a DB backup, just to be safe
            inProgressbarTask.setTaskProgress(0);
            inProgressbarTask.setMessage("Starting the Echo Workspace Backup");
            WildLogApp.LOGGER.log(Level.INFO, "Echo Backup Progress: {}%", inProgressbarTask.getProgress());
            inProgressbarTask.setMessage("Starting the Echo Workspace Backup (performing database backup)");
            inApp.getDBI().doBackup(WildLogPaths.WILDLOG_BACKUPS.getAbsoluteFullPath()
                    .resolve("Backup (" + UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(LocalDateTime.now()) + ")"));
            // Need to close the databse in order to be allowed to copy it
            inApp.getDBI().close();
            inProgressbarTask.setTaskProgress(1);
            inProgressbarTask.setMessage("Busy with the Echo Workspace Backup " + inProgressbarTask.getProgress() + "%");
            WildLogApp.LOGGER.log(Level.INFO, "Echo Backup Progress: {}%", inProgressbarTask.getProgress());
            // Setup the report
            Path feedbackFile = null;
            PrintWriter feedback = null;
            try {
                Files.createDirectories(WildLogPaths.WILDLOG_PROCESSES.getAbsoluteFullPath());
                feedbackFile = WildLogPaths.WILDLOG_PROCESSES.getAbsoluteFullPath().resolve(
                        "EchoWorkspaceReport_" + UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(LocalDateTime.now()) + ".txt");
                feedback = new PrintWriter(new FileWriter(feedbackFile.toFile()), true);
                feedback.println("--------------------------------------------------");
                feedback.println("---------- Echo Workspace Backup Report ----------");
                feedback.println("--------------------------------------------------");
                feedback.println("");
                // Start walking the folders and building a list of what needs to be copied / deleted
                final List<Path> lstPathsToDelete = new ArrayList<>();
                final List<Path> lstPathsToCopyFrom = new ArrayList<>();
                final List<Path> lstPathsToCopyTo = new ArrayList<>();
                Path workspacePath = WildLogPaths.getFullWorkspacePrefix();
                Path echoPath = inEchoPath;
                // Walk the echo path and delete all folders and files that aren't in the active Workspace
                inProgressbarTask.setTaskProgress(2);
                inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Compiling list of changes... " + inProgressbarTask.getProgress() + "%");
                WildLogApp.LOGGER.log(Level.INFO, "Echo Backup Progress: {}%", inProgressbarTask.getProgress());
                Files.walkFileTree(echoPath, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(final Path inFolderPath, final BasicFileAttributes inAttributes) throws IOException {
// TODO: In plaas van elke keer exists op die file te roep, bou twee lyste / sets en doen dan "contains"
                        if (!Files.exists(workspacePath.resolve(echoPath.relativize(inFolderPath)))) {
                            lstPathsToDelete.add(inFolderPath.normalize().toAbsolutePath().normalize());
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        // Always delete some folders (if somehow present)
                        if (inFolderPath.endsWith(WildLogPaths.WILDLOG_EXPORT.getRelativePath())
                                || inFolderPath.endsWith(WildLogPaths.WILDLOG_THUMBNAILS.getRelativePath())) {
                            lstPathsToDelete.add(inFolderPath.normalize().toAbsolutePath().normalize());
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(final Path inFilePath, final BasicFileAttributes inAttributes) throws IOException {
                        if (!Files.exists(workspacePath.resolve(echoPath.relativize(inFilePath)))) {
                            lstPathsToDelete.add(inFilePath.normalize().toAbsolutePath().normalize());
                        }
                        return FileVisitResult.CONTINUE;
                    }

                });
                inProgressbarTask.setTaskProgress(3);
                inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Compiling list of changes... " + inProgressbarTask.getProgress() + "%");
                WildLogApp.LOGGER.log(Level.INFO, "Echo Backup Progress: {}%", inProgressbarTask.getProgress());
                // Walk the active workspace and copy all files that aren't already present in the echo path
                final Path finalFeedbackFile = feedbackFile;
                Files.walkFileTree(workspacePath, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(final Path inFolderPath, final BasicFileAttributes inAttributes) throws IOException {
                        // Skip some folders
                        if (inFolderPath.endsWith(WildLogPaths.WILDLOG_EXPORT.getRelativePath())
                                || inFolderPath.endsWith(WildLogPaths.WILDLOG_THUMBNAILS.getRelativePath())) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(final Path inFilePath, final BasicFileAttributes inAttributes) throws IOException {
                        // Skip the report file
                        if (inFilePath.equals(finalFeedbackFile)) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }
                        Path echoFile = echoPath.resolve(workspacePath.relativize(inFilePath));
                        if (!Files.exists(echoFile)) {
                            lstPathsToCopyFrom.add(inFilePath.normalize().toAbsolutePath().normalize());
                            lstPathsToCopyTo.add(echoFile.normalize().toAbsolutePath().normalize());
                        }
                        else if (Files.size(inFilePath) != Files.size(echoFile)) {
                            lstPathsToCopyFrom.add(inFilePath.normalize().toAbsolutePath().normalize());
                            lstPathsToCopyTo.add(echoFile.normalize().toAbsolutePath().normalize());
                        }
                        return FileVisitResult.CONTINUE;
                    }

                });
                inProgressbarTask.setTaskProgress(4);
                inProgressbarTask.setMessage("Busy with the Echo Workspace Backup " + inProgressbarTask.getProgress() + "%");
                WildLogApp.LOGGER.log(Level.INFO, "Echo Backup Progress: {}%", inProgressbarTask.getProgress());
                // To the actual file processing based on the built up lists
                double totalActions = lstPathsToDelete.size() + lstPathsToCopyTo.size();
                for (int t = 0; t < lstPathsToDelete.size(); t++) {
                    Path pathToDelete = lstPathsToDelete.get(t);
                    // Delete the folder or file
                    UtilsFileProcessing.deleteRecursive(pathToDelete.toFile());
                    // Update report and progress
                    feedback.println("Deleted   : " + pathToDelete.toString());
                    inProgressbarTask.setTaskProgress(4 + (int) (((double) t) / totalActions * 95.0));
                    inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Deleting files... " + inProgressbarTask.getProgress() + "%");
                    WildLogApp.LOGGER.log(Level.INFO, "Echo Backup Progress: {}%", inProgressbarTask.getProgress());
                }
                for (int t = 0; t < lstPathsToCopyFrom.size(); t++) {
                    Path pathToCopyFrom = lstPathsToCopyFrom.get(t);
                    Path pathToCopyTo = lstPathsToCopyTo.get(t);
                    // Make sure the folders exist
                    Files.createDirectories(pathToCopyTo.getParent());
                    // The daily backup might run in the background and delete a folder, 
                    // so make sure the source folder still exists
                    if (Files.exists(pathToCopyFrom)) {
                        // Perfrom the action
                        if (!Files.exists(pathToCopyTo)) {
                            // Copy the file
                            UtilsFileProcessing.copyFile(pathToCopyFrom, pathToCopyTo, false, false);
                            // Update report and progress
                            feedback.println("Copied    : " + pathToCopyTo.toString());
                        }
                        else {
                            // Replace the file
                            UtilsFileProcessing.copyFile(pathToCopyFrom, pathToCopyTo, true, true);
                            // Update report and progress
                            feedback.println("Replaced  : " + pathToCopyTo.toString());
                        }
                    }
                    else {
                        WildLogApp.LOGGER.log(Level.WARN, "Can't copy file, the source is no longer available: {}%", pathToCopyFrom);
                        feedback.println("Skipped   : " + pathToCopyFrom.toString());
                    }
                    inProgressbarTask.setTaskProgress(4 + (int) (((double) (lstPathsToDelete.size() + t)) / totalActions * 95.0));
                    inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Copying files... " + inProgressbarTask.getProgress() + "%");
                    WildLogApp.LOGGER.log(Level.INFO, "Echo Backup Progress: {}%", inProgressbarTask.getProgress());
                }
            }
            // Finish the report
            catch (Exception ex) {
                hasError = true;
                if (feedback != null) {
                    feedback.println("");
                    feedback.println("--------------------------------------");
                    feedback.println("--------------- ERROR ----------------");
                    feedback.println(ex.toString());
                    feedback.println("--------------------------------------");
                    feedback.println("");
                }
                throw ex;
            }
            finally {
                if (feedback != null) {
                    feedback.println("");
                    feedback.println("--------------- DURATION ----------------");
                    long duration = System.currentTimeMillis() - startTime;
                    int hours = (int) (((double) duration) / (1000.0 * 60.0 * 60.0));
                    int minutes = (int) (((double) duration - (hours * 60 * 60 * 1000)) / (1000.0 * 60.0));
                    int seconds = (int) (((double) duration - (hours * 60 * 60 * 1000) - (minutes * 60 * 1000)) / (1000.0));
                    feedback.println(hours + " hours, " + minutes + " minutes, " + seconds + " seconds");
                    WildLogApp.LOGGER.log(Level.INFO, "Echo Backup Duration: {} hours, {} minutes, {} seconds", hours, minutes, seconds);
                    feedback.println("");
                    feedback.println("--------------------------------------");
                    feedback.println("-------------- FINISHED --------------");
                    feedback.println("--------------------------------------");
                    feedback.println("");
                    feedback.flush();
                    feedback.close();
                    // Copy the report to the echo folder
                    if (feedbackFile != null) {
                        try {
                            Path echoPath = inEchoPath;
                            UtilsFileProcessing.copyFile(feedbackFile, echoPath.resolve(
                                    WildLogPaths.WILDLOG_PROCESSES.getRelativePath()).resolve(feedbackFile.getFileName()), true, true);
                        }
                        catch (Exception ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            hasError = true;
                        }
                    }
                    // Open the summary document
                    UtilsFileProcessing.openFile(feedbackFile);
                }
            }
            inProgressbarTask.setTaskProgress(100);
            inProgressbarTask.setMessage("Done with the Echo Workspace Backup");
            WildLogApp.LOGGER.log(Level.INFO, "Echo Backup Progress: {}%", inProgressbarTask.getProgress());
        }
        catch (Exception ex) {
            hasError = true;
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return hasError;
    }

}
