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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.ui.helpers.filters.ImageFilter;
import wildlog.ui.helpers.filters.MovieFilter;
import wildlog.ui.maps.implementations.helpers.UtilsMaps;
import wildlog.ui.utils.UtilsTime;

public final class UtilsFileProcessing {
    private static final ExecutorService executorService = 
            Executors.newFixedThreadPool(WildLogApp.getApplication().getThreadCount(), new NamedThreadFactory("WL_FileUpload"));
    private static final Map<String, Object> fileLocks = Collections.synchronizedMap(new HashMap<>(50));
    private static final char[] SEQ = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X' ,'Y', 'Z'};
    public static final String INDICATOR_SEQ = "_WLSEQ_";
    public static final String INDICATOR_FILE = "_WLFILE_";
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
// TODO: Ek is nie seker of die filters reg werk nie... GEBRUIK EERDER JavaFx se FileChooser eendag :D
        fileChooser.setFilenameFilter(new MovieFilter());
//        fileChooser.setFile("*.jpg");
        fileChooser.setFilenameFilter(new ImageFilter());
//        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setMultipleMode(true);
//        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setPreferredSize(new Dimension(950, 550));
        if (inParent != null) {
            inParent.getGlassPane().setVisible(true);
        }
        fileChooser.setVisible(true);
//                return fileChooser.showOpenDialog(inApp.getMainFrame().getContentPane());
        if (inParent != null) {
            inParent.getGlassPane().setVisible(false);
        }
//        return Arrays.asList(fileChooser.getSelectedFiles());
        return Arrays.asList(fileChooser.getFiles());
    }
    
    private static class SequenceCounter {
        public int counter = 1;
    }

    public static void performFileUpload(final DataObjectWithWildLogFile inDAOWithID, final Path inPrefixFolder, final File[] inFiles, 
            final Runnable inRunWhenDone, 
            final WildLogApp inApp, boolean inWithSlowProcessPopup, JDialog inParent, 
            final boolean inCreateThumbnails, final boolean inHandleSyncIssuesForPossibleDuplicatesInList) {
        // Get the date of the first file 
        // (if files already exist in the workspace then use those, otherwise use the list of files being uploaded)
        LocalDateTime firstFileDate = null;
        if (inDAOWithID instanceof Sighting) {
            List<Path> lstPathsToGetFirstDateFrom;
            List<WildLogFile> lstExistingFiles = inApp.getDBI().listWildLogFiles(inDAOWithID.getWildLogFileID(), null, WildLogFile.class);
            if (lstExistingFiles != null && !lstExistingFiles.isEmpty()) {
                lstPathsToGetFirstDateFrom = new ArrayList<>(lstExistingFiles.size());
                for (WildLogFile wildLogFile : lstExistingFiles) {
                    lstPathsToGetFirstDateFrom.add(wildLogFile.getAbsolutePath());
                }
            }
            else {
                lstPathsToGetFirstDateFrom = new ArrayList<>(inFiles.length);
                for (File file : inFiles) {
                    lstPathsToGetFirstDateFrom.add(file.toPath().toAbsolutePath());
                }
            }
            for (Path path : lstPathsToGetFirstDateFrom) {
                LocalDateTime fileDate = UtilsTime.getLocalDateTimeFromDate(UtilsImageProcessing.getDateFromFile(path));
                if (firstFileDate == null || (fileDate != null && fileDate.isBefore(firstFileDate))) {
                    firstFileDate = fileDate;
                }
            }
        }
        final LocalDateTime finalFirstFileDate = firstFileDate;
        // Setup the lock
        final Object theLock;
        if (inHandleSyncIssuesForPossibleDuplicatesInList) {
            theLock = new Object();
        }
        else {
            theLock = null;
        }
        // Submit the work to the executor
        Collection<Callable<Object>> listCallables = new ArrayList<>(inFiles.length);
        final SequenceCounter sequenceCounter = new SequenceCounter();
        for (File file : inFiles) {
            if (file != null) {
                final File fromFile = file;
                listCallables.add(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        Path fromPath = fromFile.toPath();
                        if (Files.isRegularFile(fromPath)) {
                            lastFilePath = fromPath;
                            // Is an image
                            if (WildLogFileExtentions.Images.isKnownExtention(fromPath)) {
                                saveOriginalFile(WildLogPaths.WILDLOG_FILES_IMAGES, WildLogFileType.IMAGE, inPrefixFolder, fromPath, 
                                        sequenceCounter.counter++, inApp, inDAOWithID, inCreateThumbnails, theLock, finalFirstFileDate);
                            }
                            else
                            // Is a movie
                            if (WildLogFileExtentions.Movies.isKnownExtention(fromPath)) {
                                saveOriginalFile(WildLogPaths.WILDLOG_FILES_MOVIES, WildLogFileType.MOVIE, inPrefixFolder, fromPath, 
                                        sequenceCounter.counter++, inApp, inDAOWithID, inCreateThumbnails, theLock, finalFirstFileDate);
                            }
                            else {
                                saveOriginalFile(WildLogPaths.WILDLOG_FILES_OTHER, WildLogFileType.OTHER, inPrefixFolder, fromPath, 
                                        sequenceCounter.counter++, inApp, inDAOWithID, inCreateThumbnails, theLock, finalFirstFileDate);
                            }
                        }
                        return null;
                    }
                });
            }
        }
        if (inWithSlowProcessPopup) {
            // Wait to finish the work
            UtilsConcurency.waitForExecutorToRunTasksWithPopup(executorService, listCallables, inRunWhenDone, inParent, 
                    "  Busy processing. Please wait...  ");
        }
        else {
            UtilsConcurency.waitForExecutorToRunTasks(executorService, listCallables);
            if (inRunWhenDone != null) {
                SwingUtilities.invokeLater(inRunWhenDone);
            }
        }
    }

    private static void saveOriginalFile(WildLogPaths inWorkspacePath, WildLogFileType inFileType, Path inPrefixFolder, Path inFromFile, 
            int inSequenceIndex, WildLogApp inApp, DataObjectWithWildLogFile inDAOWithID, boolean inCreateThumbnails, Object inTheLock, 
            LocalDateTime inFirstFileDate) {
        // Make the folder
        Path toFolder = inWorkspacePath.getAbsoluteFullPath().resolve(inPrefixFolder).normalize().toAbsolutePath();
        try {
            Files.createDirectories(toFolder);
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        WildLogFile wildLogFile;
        if (inTheLock != null) {
            synchronized (inTheLock) {
                wildLogFile = doTheFileSave(toFolder, inFromFile, inDAOWithID, inFileType, 0, inApp, inFirstFileDate);
            }
        }
        else {
            wildLogFile = doTheFileSave(toFolder, inFromFile, inDAOWithID, inFileType, inSequenceIndex, inApp, inFirstFileDate);
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

    private static WildLogFile doTheFileSave(Path toFolder, Path inFromFile, DataObjectWithWildLogFile inDAOWithID, 
            WildLogFileType inFileType, int inSequenceIndex, WildLogApp inApp, LocalDateTime inFirstFileDate) {
        // Setup the output files
        String fromFileName = inFromFile.getFileName().toString();
        if (inDAOWithID instanceof Sighting && fromFileName.lastIndexOf('.') > 0) {
            // Rename the Sighting files to reflect the date of the Sighting (useful for external camera trap data tools)
            LocalDateTime fileDate = UtilsTime.getLocalDateTimeFromDate(UtilsImageProcessing.getDateFromFile(inFromFile));
            String customFileName = ((Sighting) inDAOWithID).getCustomFileName(inFirstFileDate, fileDate);
            // Sluit die oorspronklikke naam in sodat die volgorde nie verlore gaan as die fotos 
            // dieselfde EXIF datum het nie. (soos die geval is met sommige camera trap)
            // Vir bestaande files met reeds veranderde name, probeer om dan net die sequence naam / file naam te hou.
            String origFileNameOrSequence = fromFileName.substring(0, fromFileName.lastIndexOf('.'));
            int indexFile = fromFileName.lastIndexOf(INDICATOR_FILE);
            if (indexFile > 0) {
                origFileNameOrSequence = fromFileName.substring(indexFile + INDICATOR_FILE.length());
                origFileNameOrSequence = origFileNameOrSequence.substring(0, origFileNameOrSequence.lastIndexOf('.'));
            }
            int indexSeq = origFileNameOrSequence.lastIndexOf(INDICATOR_SEQ);
            if (indexSeq > 0) {
                origFileNameOrSequence = origFileNameOrSequence.substring(0, indexSeq);
            }
            fromFileName = customFileName + INDICATOR_FILE + origFileNameOrSequence + fromFileName.substring(fromFileName.lastIndexOf('.'));
        }
        Path toFile = toFolder.resolve(fromFileName);
        // Check that the filename is unique
        while (Files.exists(toFile)) {
            String tempFileName = fromFileName.substring(0, fromFileName.lastIndexOf('.'));
            toFile = toFolder.resolve(tempFileName + getFormattedSequence(++inSequenceIndex) + fromFileName.substring(fromFileName.lastIndexOf('.')));
        }
        // Copy the original file into WildLog's folders. (Don't overwrite other files, and give an error if it already exists.)
        copyFile(inFromFile, toFile, false, false);
        // Save the database entry
        long fileSize = 0;
        try {
            // This may fail or not be 100% accurate accross operating systems, harddrives after being moved etc.
            fileSize = Files.size(inFromFile);
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        WildLogFile wildLogFile = new WildLogFile(
                inDAOWithID.getWildLogFileID(),
                toFile.getFileName().toString(),
                WildLogPaths.getFullWorkspacePrefix().relativize(toFile).toString(),
                inFileType,
                Calendar.getInstance().getTime(),
                UtilsImageProcessing.getDateFromFileDate(inFromFile),
                fileSize);
        inApp.getDBI().createWildLogFile(wildLogFile);
        return wildLogFile;
    }
    
    public static String getFormattedSequence(int inCount) {
        int count = inCount;
        String sequence = "";
        do {
            if (count < SEQ.length) {
                sequence = sequence + SEQ[count % SEQ.length];
            }
            else {
                sequence = sequence + SEQ[SEQ.length - 1];
            }
            count = count - SEQ.length;
        }
        while (count >= 0);
        return INDICATOR_SEQ + sequence;
    }

    public static void openFile(String inID, int inIndex, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().listWildLogFiles(inID, null, WildLogFile.class);
        if (fotos.size() > inIndex) {
            openFile(fotos.get(inIndex).getAbsolutePath());
        }
    }

    public static void openFile(Path inPath) {
        if (inPath != null) {
            try {
                boolean usedBundledViewers = false;
                if (WildLogApp.getApplication().getWildLogOptions().isBundledPlayers()) {
                    // From what I understand from the statement below, what I'm doing here in WildLog should be fine.
                    /** 
                     * https://www.gnu.org/licenses/gpl-faq.html#GPLInProprietarySystem
                     * I'd like to incorporate GPL-covered software in my proprietary system. I have no permission to use that 
                     * software except what the GPL gives me. Can I do this?
                     * You cannot incorporate GPL-covered software in a proprietary system. The goal of the GPL is to grant 
                     * everyone the freedom to copy, redistribute, understand, and modify a program. If you could 
                     * incorporate GPL-covered software into a non-free system, it would have the effect of making the GPL-covered 
                     * software non-free too.
                     * A system incorporating a GPL-covered program is an extended version of that program. The GPL says that any 
                     * extended version of the program must be released under the GPL if it is released at all. This is for two reasons: 
                     * to make sure that users who get the software get the freedom they should have, 
                     * and to encourage people to give back improvements that they make.
                     * However, in many cases you can distribute the GPL-covered software alongside your proprietary system. 
                     * To do this validly, you must make sure that the free and non-free programs communicate at arms length, that 
                     * they are not combined in a way that would make them effectively a single program.
                     * The difference between this and “incorporating” the GPL-covered software is partly a matter of substance and 
                     * partly form. The substantive part is this: if the two programs are combined so that they become effectively 
                     * two parts of one program, then you can't treat them as two separate programs. 
                     * So the GPL has to cover the whole thing.
                     * If the two programs remain well separated, like the compiler and the kernel, or like an editor and a shell, 
                     * then you can treat them as two separate programs—but you have to do it properly. The issue is simply one of form: 
                     * how you describe what you are doing. Why do we care about this? Because we want to make sure the users clearly 
                     * understand the free status of the GPL-covered software in the collection.
                     * If people were to distribute GPL-covered software calling it “part of” a system that users know is partly 
                     * proprietary, users might be uncertain of their rights regarding the GPL-covered software. But if they know that 
                     * what they have received is a free program plus another program, side by side, their rights will be clear.
                     */
                    if (WildLogFileExtentions.Images.isKnownExtention(inPath)) {
                        if (Files.exists(WildLogPaths.OPEN_JPEGVIEW.getAbsoluteFullPath())) {
                            ProcessBuilder processBuilder = new ProcessBuilder(WildLogPaths.OPEN_JPEGVIEW.getAbsoluteFullPath().toString(), 
                                    inPath.normalize().toAbsolutePath().toString());
                            Process process = processBuilder.start();
                            usedBundledViewers = true;
                        }
                    }
                    else
                    if (WildLogFileExtentions.Movies.isKnownExtention(inPath)) {
                        if (Files.exists(WildLogPaths.OPEN_MEDIA_PLAYER_CLASSIC.getAbsoluteFullPath())) {
                            ProcessBuilder processBuilder = new ProcessBuilder(WildLogPaths.OPEN_MEDIA_PLAYER_CLASSIC.getAbsoluteFullPath().toString(), 
                                    inPath.normalize().toAbsolutePath().toString());
                            Process process = processBuilder.start();
                            usedBundledViewers = true;
                        }
                    }
                }
                // Use the default way of openng if no bundled viewer was used
                if (!usedBundledViewers) {
                    Desktop.getDesktop().open(inPath.normalize().toAbsolutePath().toFile());
                }
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.WARN, "Problem opening file in {}. Trying fallback method now...", System.getProperty("os.name"));
                WildLogApp.LOGGER.log(Level.WARN, ex.toString(), ex);
                // Backup Plan - Because of Java 6 bug for avi files
                try {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("mac")) {
                        String[] commands = {"open", "%s", inPath.normalize().toAbsolutePath().toFile().toString()};
                        Runtime.getRuntime().exec(commands);
                    }
                    else
                    if (os.contains("windows") || os.contains("nt")) {
                        String[] commands = {"cmd", "/c", "start", "\"DoNothing\"", inPath.normalize().toAbsolutePath().toFile().toString()};
                        Runtime.getRuntime().exec(commands);
                    }
                    else
                    if ((os.contains("windows") || os.contains("nt")) && (os.equals("windows 95") || os.equals("windows 98"))) {
                        String[] commands = {"command.com", "/C", "start", "%s", inPath.normalize().toAbsolutePath().toFile().toString()};
                        Runtime.getRuntime().exec(commands);
                    }
                }
                catch (IOException ex2) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex2.toString(), ex2);
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
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                // More than one thread can try to create these files at once, or very quickly after each other, resulting in IO errors, 
                // thus I need to add some synchronization to make it throw less exceptions.
                Object lock = fileLocks.get(inFileToWrite.toAbsolutePath().toString());
                if (lock == null) {
                    lock = new Object();
                    fileLocks.put(inFileToWrite.toAbsolutePath().toString(), lock);
                }
                synchronized (lock) {
                    Files.createDirectories(inFileToWrite.getParent());
// FIXME: Die gee steeds soms 'n error...
                    Files.copy(inFileToRead, inFileToWrite, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            finally {
                try {
                    if (inFileToRead != null) {
                        inFileToRead.close();
                    }
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
    
    public static void copyMapLayersWithPopup() {
        try {
            Files.createDirectories(WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_MAPS_CUSTOM.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_MAPS_SPECIES.getAbsoluteFullPath());
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        Collection<Callable<Object>> listCallables = new ArrayList<>(1);
        listCallables.add(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                UtilsMaps.copyMapLayers();
                return null;
            }
        });
        UtilsConcurency.waitForExecutorToRunTasksWithPopup(executorService, listCallables, null, null, "  Setting up initial mapping data...  ");
    }
    
    /**
     * <b>For Bulk Upload. </b>
     * This method will take a list of Files and then convert it to a list of Paths. 
     */
    public static List<Path> getPathsFromSelectedFile(File[] inFiles) {
        if (inFiles != null && inFiles.length > 0) {
            List<Path> lstSelectedPaths = new ArrayList<>(inFiles.length);
            for (File selectedFile : inFiles) {
                lstSelectedPaths.add(selectedFile.toPath());
            }
            return lstSelectedPaths;
        }
        return new ArrayList<>(0);
    }

}
