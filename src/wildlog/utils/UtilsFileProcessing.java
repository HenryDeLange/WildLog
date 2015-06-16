package wildlog.utils;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.filters.ImageFilter;
import wildlog.ui.helpers.filters.MovieFilter;

public final class UtilsFileProcessing {
    private static final ExecutorService executorService = 
            Executors.newFixedThreadPool(WildLogApp.getApplication().getThreadCount(), new NamedThreadFactory("WL_FileUpload"));
    private static Path lastFilePath = null;

    private UtilsFileProcessing() {
    }

    /** Get the extension of a file.
     * @param inFile
     * @return
     */
    public static String getExtension(File inFile) {
        String ext = null;
        int i = inFile.getName().lastIndexOf('.');
        if (i > 0 &&  i < inFile.getName().length() - 1) {
            ext = inFile.getName().substring(i+1).toLowerCase();
        }
        return ext;
    }

    /**
     * Upload a file using a FileChooser dialog.
     * @param inApp
     * @param inParent
     * @return
     */
    public static List<File> showFileUploadDialog(final WildLogApp inApp, RootPaneContainer inParent) {
        FileDialog fileChooser;
        if (inParent instanceof JDialog) {
            fileChooser = new FileDialog((JDialog) inParent, "Select Files", FileDialog.LOAD);
        }
        else {
            fileChooser = new FileDialog((JFrame) inParent, "Select Files", FileDialog.LOAD);
        }
//        fileChooser.setIconImage(new ImageIcon(inApp.getClass().getResource("resources/icons/WildLog Icon Small.gif")).getImage());
//        final JFileChooser fileChooser = new JFileChooser();
        if (lastFilePath != null) {
//            fileChooser.setCurrentDirectory(Paths.get(lastFilePath).toFile());
            fileChooser.setDirectory(lastFilePath.toString());
        }
//        fileChooser.setAcceptAllFileFilterUsed(true);
//        fileChooser.setFileFilter(new MovieFilter());
//        fileChooser.setFileFilter(new ImageFilter());
//        fileChooser.setAccessory(new ImagePreview(fileChooser));
// TODO: Ek nie seker of die filters reg werk nie... GEBRUIK EERDER JavaFx se FileChooser eendag :D
        fileChooser.setFilenameFilter(new MovieFilter());
//        fileChooser.setFile("*.jpg");
        fileChooser.setFilenameFilter(new ImageFilter());
//        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setMultipleMode(true);
//        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setPreferredSize(new Dimension(950, 550));
        UtilsDialog.showDialogBackgroundWrapper(inParent, new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
//                return fileChooser.showOpenDialog(inApp.getMainFrame().getContentPane());
                fileChooser.setVisible(true);
                return 0;
            }
        });
//        return Arrays.asList(fileChooser.getSelectedFiles());
        return Arrays.asList(fileChooser.getFiles());
    }

    public static void performFileUpload(final String inID, final Path inPrefixFolder, final File[] inFiles, 
            final JLabel inImageLabel, final Runnable inRunWhenDone, 
            final WildLogApp inApp, boolean inWithSlowProcessPopup, JDialog inParent, 
            final boolean inCreateThumbnails, final boolean inHandleSyncIssuesForPossibleDuplicatesInList) {
        final Object theLock;
        if (inHandleSyncIssuesForPossibleDuplicatesInList) {
            theLock = new Object();
        }
        else {
            theLock = null;
        }
        // Submit the work to the executor
        Collection<Callable<Object>> listCallables = new ArrayList<>(inFiles.length);
        for (File inFile : inFiles) {
            if (inFile != null) {
                final File fromFile = inFile;
                listCallables.add(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        Path fromPath = fromFile.toPath();
                        if (Files.isRegularFile(fromPath)) {
                            lastFilePath = fromPath;
                            // Is an image
                            if (WildLogFileExtentions.Images.isKnownExtention(fromPath)) {
                                saveOriginalFile(WildLogPaths.WILDLOG_FILES_IMAGES, WildLogFileType.IMAGE, inPrefixFolder, fromPath, inApp, inID, 
                                        inCreateThumbnails, theLock);
                            }
                            else
                            // Is a movie
                            if (WildLogFileExtentions.Movies.isKnownExtention(fromPath)) {
                                saveOriginalFile(WildLogPaths.WILDLOG_FILES_MOVIES, WildLogFileType.MOVIE, inPrefixFolder, fromPath, inApp, inID, 
                                        inCreateThumbnails, theLock);
                            }
                            else {
                                saveOriginalFile(WildLogPaths.WILDLOG_FILES_OTHER, WildLogFileType.OTHER, inPrefixFolder, fromPath, inApp, inID, 
                                        inCreateThumbnails, theLock);
                            }
                        }
                        return null;
                    }
                });
            }
        }
        if (inWithSlowProcessPopup) {
            // Wait to finish the work
            UtilsConcurency.waitForExecutorToRunTasksWithPopup(executorService, listCallables, inRunWhenDone, inParent);
        }
        else {
            UtilsConcurency.waitForExecutorToRunTasks(executorService, listCallables);
            if (inRunWhenDone != null) {
                SwingUtilities.invokeLater(inRunWhenDone);
            }
        }
    }

    private static void saveOriginalFile(WildLogPaths inWorkspacePath, WildLogFileType inFileType, Path inPrefixFolder, Path inFromFile, 
            WildLogApp inApp, String inID, boolean inCreateThumbnails, Object inTheLock) {
        // Make the folder
        Path toFolder = inWorkspacePath.getAbsoluteFullPath().resolve(inPrefixFolder).normalize().toAbsolutePath();
        try {
            Files.createDirectories(toFolder);
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        WildLogFile wildLogFile;
        if (inTheLock != null) {
            synchronized (inTheLock) {
                wildLogFile = doTheFileSave(toFolder, inFromFile, inID, inFileType, inApp);
            }
        }
        else {
            wildLogFile = doTheFileSave(toFolder, inFromFile, inID, inFileType, inApp);
        }
        // Create the default thumbnails if it is an image
        // (Dit sal dan hopelik 'n beter user experience gee as die thumbnails klaar daar is teen die tyd dat mens dit in die app view...)
        if (inCreateThumbnails) {
            if (WildLogFileType.IMAGE.equals(wildLogFile.getFileType())) {
                if (inTheLock != null) {
                    synchronized (inTheLock) {
                        // Maak net die kritiese thumbnails vooruit, want anders vat dinge te lank
                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_SMALL);
                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.SMALL);
                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM_SMALL);
                        wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL);
                    }
                }
                else {
                    // Maak net die kritiese thumbnails vooruit, want anders vat dinge te lank
                    wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.VERY_SMALL);
                    wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.SMALL);
                    wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM_SMALL);
                    wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL);
                }
            }
        }
    }

    private static WildLogFile doTheFileSave(Path toFolder, Path inFromFile, String inID, WildLogFileType inFileType, WildLogApp inApp) {
        // Setup the output files
        Path toFile = toFolder.resolve(inFromFile.getFileName());
        // Check that the filename is unique
        while (Files.exists(toFile)) {
            toFile = toFolder.resolve("wl_" + toFile.getFileName());
        }
        // Copy the original file into WildLog's folders. (Don't overwrite other files, and give an error if it already exists.)
        copyFile(inFromFile, toFile, false, false);
        // Save the database entry
        WildLogFile wildLogFile = new WildLogFile(
                inID,
                toFile.getFileName().toString(),
                WildLogPaths.getFullWorkspacePrefix().relativize(toFile).toString(),
                inFileType);
        inApp.getDBI().createOrUpdate(wildLogFile, false);
        return wildLogFile;
    }

    public static void openFile(String inID, int inIndex, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > inIndex) {
            openFile(fotos.get(inIndex).getAbsolutePath());
        }
    }

    public static void openFile(Path inPath) {
        if (inPath != null) {
            try {
                Desktop.getDesktop().open(inPath.normalize().toAbsolutePath().toFile());
            }
            catch (IOException ex) {
                System.out.println("Problem opening file in the OS. Trying other method now...");
                ex.printStackTrace(System.err);
                // Backup Plan - Because of Java 6 bug for avi files
                try {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.indexOf("mac") != -1)
                    {
                        String[] commands = {"open", "%s", inPath.normalize().toAbsolutePath().toFile().toString()};
                        Runtime.getRuntime().exec(commands);
                    }
                    else
                    if ((os.indexOf("windows") != -1 || os.indexOf("nt") != -1) && (os.equals("windows 95") || os.equals("windows 98")))
                    {
                        String[] commands = {"command.com", "/C", "start", "%s", inPath.normalize().toAbsolutePath().toFile().toString()};
                        Runtime.getRuntime().exec(commands);
                    }
                    else
                    if (os.indexOf("windows") != -1 || os.indexOf("nt") != -1)
                    {
                        String[] commands = {"cmd", "/c", "start", "\"DoNothing\"", inPath.normalize().toAbsolutePath().toFile().toString()};
                        Runtime.getRuntime().exec(commands);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    /**
     * Makes a copy of the ToRead file to the ToWrite file.
     * @param inFileToRead
     * @param inFileToWrite
     * @param inOverwriteExisting
     * @param inPreventExistsErrors
     */
    public static void copyFile(Path inFileToRead, Path inFileToWrite, boolean inOverwriteExisting, boolean inPreventExistsErrors) {
        try {
            Files.createDirectories(inFileToWrite.getParent());
            if (!inOverwriteExisting) {
                if (inPreventExistsErrors) {
                    if (!Files.exists(inFileToWrite)) {
                        Files.copy(inFileToRead, inFileToWrite);
                    }
                }
                else {
                    // This will throw an exception if the file already exists
                    Files.copy(inFileToRead, inFileToWrite);
                }
            }
            else {
                Files.copy(inFileToRead, inFileToWrite, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * This method is used to copy internal files (inside the JAR) into the workspace for further use.
     * @param inFileToRead
     * @param inFileToWrite
     */
    public static void createFileFromStream(InputStream inFileToRead, Path inFileToWrite) {
        if (!Files.exists(inFileToWrite)) {
            try {
                Files.createDirectories(inFileToWrite.getParent());
                Files.copy(inFileToRead, inFileToWrite);
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            finally {
                try {
                    if (inFileToRead != null) {
                        inFileToRead.close();
                    }
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    /**
     * This method will create a file at the specified path containing the provided bytes, replacing any existing files.
     * @param inBytesToWrite
     * @param inFileToWrite
     */
    public static void createFileFromBytes(byte[] inBytesToWrite, Path inFileToWrite) {
        try {
            Files.createDirectories(inFileToWrite.getParent());
            Files.write(inFileToWrite, inBytesToWrite, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Delete the specified file, or if it is a folder then delete all of it's content and then the folder itself.
     * @param inFile
     * @throws IOException
     */
    public static void deleteRecursive(File inFile) throws IOException {
// TODO: Verander die dalk eendag om Paths en 'n walker te gebruik
        if (inFile.isDirectory()) {
            for (File content : inFile.listFiles()) {
                deleteRecursive(content);
            }
        }
        if (inFile.exists() && !inFile.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + inFile);
        }
    }

    /**
     * Delete the specified file, or if it is a folder only delete it (and it's content) if it is empty.
     * @param inFile
     * @throws IOException
     */
    public static void deleteRecursiveOnlyEmptyFolders(File inFile) throws IOException {
// TODO: Verander die dalk eendag om Paths en 'n walker te gebruik
        if (inFile.isDirectory()) {
            for (File content : inFile.listFiles()) {
                deleteRecursiveOnlyEmptyFolders(content);
            }
            if (inFile.listFiles().length == 0) {
                if (!inFile.delete()) {
                    throw new FileNotFoundException("Failed to delete folder: " + inFile);
                }
            }
        }
    }

    public static String getAlphaNumericVersion(String inString) {
        // Regex black magic van die web af...
        return inString.replaceAll("[^A-Za-z0-9.]+", "");
    }

}
