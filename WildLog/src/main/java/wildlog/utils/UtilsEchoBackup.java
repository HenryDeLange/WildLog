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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.ui.helpers.ProgressbarTask;

public class UtilsEchoBackup {
    private static int lastUpdatedProgress = 0;

    private UtilsEchoBackup() {
    }

    public static boolean doEchoBackup(WildLogApp inApp, ProgressbarTask inProgressbarTask, Path inEchoPath, boolean inSkipSizeComparison) throws InterruptedException {
        boolean hasError = false;
        try {
            long startTime = System.currentTimeMillis();
            // Make a DB backup, just to be safe
            inProgressbarTask.setTaskProgress(0);
            inProgressbarTask.setMessage("Starting the Echo Workspace Backup");
            writeProgressToLogs(inProgressbarTask);
            inProgressbarTask.setMessage("Starting the Echo Workspace Backup (performing database backup)");
            inApp.getDBI().doBackup(WildLogPaths.WILDLOG_BACKUPS.getAbsoluteFullPath()
                    .resolve("Backup (" + UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(LocalDateTime.now()) + ")"));
            // Need to close the databse in order to be allowed to copy it
            inApp.getDBI().close();
            inProgressbarTask.setTaskProgress(1);
            inProgressbarTask.setMessage("Busy with the Echo Workspace Backup " + inProgressbarTask.getProgress() + "%");
            writeProgressToLogs(inProgressbarTask);
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
// FIXME: Ek is bietjie bang ek kan uit heap memory uit hardloop want die sets kan vrek baie Strings bevat...
                final Set<String> lstAllEchoRelativePaths = new HashSet<>(75000);
                final Set<String> lstAllWorkspaceRelativesPaths = new HashSet<>(75000);
                final List<String> lstPathsToDeleteFromEcho = new ArrayList<>(500);
                final List<String> lstPathsToCopyToEcho = new ArrayList<>(1000);
                Path workspacePath = WildLogPaths.getFullWorkspacePrefix();
                Path echoPath = inEchoPath;
                // Get all Echo (relative) Paths
                inProgressbarTask.setTaskProgress(2);
                inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Compiling list of Echo files... " + inProgressbarTask.getProgress() + "%");
                writeProgressToLogs(inProgressbarTask);
                Files.walkFileTree(echoPath, new SimpleFileVisitor<Path>() {
                    
                    @Override
                    public FileVisitResult visitFile(final Path inFilePath, final BasicFileAttributes inAttributes) throws IOException {
                        lstAllEchoRelativePaths.add(echoPath.relativize(inFilePath).toString());
                        return FileVisitResult.CONTINUE;
                    }
                    
                });
                // Get all Workspace (relative) Paths
                inProgressbarTask.setTaskProgress(3);
                inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Compiling list of Workspace files... " + inProgressbarTask.getProgress() + "%");
                writeProgressToLogs(inProgressbarTask);
                final Path finalFeedbackFile = feedbackFile;
                Files.walkFileTree(workspacePath, new SimpleFileVisitor<Path>() {
                    
                    @Override
                    public FileVisitResult preVisitDirectory(final Path inFolderPath, final BasicFileAttributes inAttributes) throws IOException {
                        // Skip Export and Tumbnails folders
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
                        lstAllWorkspaceRelativesPaths.add(workspacePath.relativize(inFilePath).toString());
                        return FileVisitResult.CONTINUE;
                    }

                });
                // Walk the echo paths and to find all paths to delete if they aren't in the active Workspace
                inProgressbarTask.setTaskProgress(4);
                inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Compiling list of changes... " + inProgressbarTask.getProgress() + "%");
                writeProgressToLogs(inProgressbarTask);
                double counter = 0.0;
                for (String tempEchoPath : lstAllEchoRelativePaths) {
                    if (!lstAllWorkspaceRelativesPaths.contains(tempEchoPath)) {
                        lstPathsToDeleteFromEcho.add(tempEchoPath);
                    }
                    inProgressbarTask.setTaskProgress(4 + (int) ((double) (counter++ / (double) lstAllEchoRelativePaths.size()) * 3.0));
                    inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Compiling list of changes... " + inProgressbarTask.getProgress() + "%");
                    writeProgressToLogs(inProgressbarTask);
                }
                // Walk the active workspace and to find all paths to copy if they aren't already present or a different size in the echo path
                counter = 0.0;
                for (String tempWorkspacePath : lstAllWorkspaceRelativesPaths) {
                    boolean foundAndSameSize = false;
                    if (lstAllEchoRelativePaths.contains(tempWorkspacePath)) {
                        Path fullWorkspacePath = workspacePath.resolve(tempWorkspacePath);
                        Path fullEchoPath = echoPath.resolve(tempWorkspacePath);
                        if ((inSkipSizeComparison && !tempWorkspacePath.startsWith(WildLogPaths.WILDLOG_DATA.getRelativePath().toString())) // Skip only non-data files
                                || (Files.getLastModifiedTime(fullWorkspacePath).equals(Files.getLastModifiedTime(fullEchoPath)) // If not skipping, first check modified date
                                && Files.size(fullWorkspacePath) == Files.size(fullEchoPath))) { // Then check size
                            foundAndSameSize = true;
                        }
                    }
                    if (!foundAndSameSize) {
                        lstPathsToCopyToEcho.add(tempWorkspacePath);
                    }
                    inProgressbarTask.setTaskProgress(7 + (int) ((double) (counter++ / (double) lstAllWorkspaceRelativesPaths.size()) * 3.0));
                    inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Compiling list of changes... " + inProgressbarTask.getProgress() + "%");
                    writeProgressToLogs(inProgressbarTask);
                }
                // Kan noual die groot lyste se memory los maak
                lstAllEchoRelativePaths.clear();
                lstAllWorkspaceRelativesPaths.clear();
                // Sort die lyste want hulle kom vanaf 'n Set
                Collections.sort(lstPathsToDeleteFromEcho);
                Collections.sort(lstPathsToCopyToEcho);
                // To the actual file processing based on the built up lists
                inProgressbarTask.setTaskProgress(10);
                inProgressbarTask.setMessage("Busy with the Echo Workspace Backup... " + inProgressbarTask.getProgress() + "%");
                writeProgressToLogs(inProgressbarTask);
                double totalActions = lstPathsToDeleteFromEcho.size() + lstPathsToCopyToEcho.size();
                // Delete
                for (int t = 0; t < lstPathsToDeleteFromEcho.size(); t++) {
                    Path pathToDelete = echoPath.resolve(lstPathsToDeleteFromEcho.get(t)).normalize().toAbsolutePath().normalize();
                    // Delete the folder or file
                    UtilsFileProcessing.deleteRecursive(pathToDelete.toFile());
                    // Update report and progress
                    feedback.println("Deleted   : " + pathToDelete.toString());
                    inProgressbarTask.setTaskProgress(10 + (int) (((double) t) / totalActions * 89.0));
                    inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Deleting files... " + inProgressbarTask.getProgress() + "%");
                    writeProgressToLogs(inProgressbarTask);
                }
                // Copy
                for (int t = 0; t < lstPathsToCopyToEcho.size(); t++) {
                    Path pathToCopyFrom = workspacePath.resolve(lstPathsToCopyToEcho.get(t)).normalize().toAbsolutePath().normalize();
                    Path pathToCopyTo = echoPath.resolve(lstPathsToCopyToEcho.get(t)).normalize().toAbsolutePath().normalize();
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
                    inProgressbarTask.setTaskProgress(10 + (int) (((double) (lstPathsToDeleteFromEcho.size() + t)) / totalActions * 89.0));
                    inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Copying files... " + inProgressbarTask.getProgress() + "%");
                    writeProgressToLogs(inProgressbarTask);
                }
                // Cleanup
                // Empty folders might remain in the Echo path (when files were deleted)
                inProgressbarTask.setTaskProgress(99);
                inProgressbarTask.setMessage("Busy with the Echo Workspace Backup: Removing empty folders... " + inProgressbarTask.getProgress() + "%");
                writeProgressToLogs(inProgressbarTask);
                UtilsFileProcessing.deleteRecursiveOnlyEmptyFolders(echoPath.toFile());
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
            writeProgressToLogs(inProgressbarTask);
        }
        catch (Exception ex) {
            hasError = true;
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return hasError;
    }

    private static void writeProgressToLogs(ProgressbarTask inProgressbarTask) {
        // Log net elke persentasie (nie elke progress update nie)
        if (lastUpdatedProgress != inProgressbarTask.getProgress()) {
            WildLogApp.LOGGER.log(Level.INFO, "Echo Backup Progress: {}%", inProgressbarTask.getProgress());
            lastUpdatedProgress = inProgressbarTask.getProgress();
        }
    }

}
