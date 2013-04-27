package wildlog.utils;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.WildLogFileType;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ImageFilter;
import wildlog.ui.helpers.ImagePreview;
import wildlog.ui.helpers.MovieFilter;

public final class UtilsFileProcessing {
    // Extentions
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    // private variables
    private static String lastFilePath = "";

    /** Get the extension of a file. */
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
     */
    public static List<File> showFileUploadDialog(final WildLogApp inApp) {
//        // Native File Upload Window. Het Thumbnails, maar het nie Multi Select nie :(
//        FileDialog d = new FileDialog(new Frame(), "Select Images", FileDialog.LOAD);
//        d.setDirectory(lastFilePath);
//        d.setVisible(true);
        final JFileChooser fileChooser;
        if (lastFilePath.length() > 0)
            fileChooser = new JFileChooser(lastFilePath);
        else
            fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(new MovieFilter());
        fileChooser.setFileFilter(new ImageFilter());
        fileChooser.setAccessory(new ImagePreview(fileChooser));
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setPreferredSize(new Dimension(950, 550));
        int result = UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return fileChooser.showOpenDialog(inApp.getMainFrame().getContentPane());
            }
        });
        return Arrays.asList(fileChooser.getSelectedFiles());
    }

    /**
     * Upload a file using a List of Files. (Used with FileDrop.)
     */
    public static int uploadFilesUsingList(String inID, String inFolderName, JLabel inImageLabel, int inSize, WildLogApp inApp, List<File> inFiles) {
        inApp.getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        performFileUpload(inID, inFolderName, inFiles.toArray(new File[inFiles.size()]), inImageLabel, inSize, inApp);
        inApp.getMainFrame().setCursor(Cursor.getDefaultCursor());
        // return new image index
        return 0;
    }

    public static void performFileUpload(final String inID, final String inFolderName, final File[] inFiles, JLabel inImageLabel, int inSize, final WildLogApp inApp) {
        ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
        for (int t = 0; t < inFiles.length; t++) {
            final File fromFile = inFiles[t];
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    if (fromFile != null && fromFile.isFile()) {
                        lastFilePath = fromFile.getPath();
                        // Is an image
                        if (new ImageFilter().accept(fromFile)) {
                            saveOriginalFile(WildLogPaths.WILDLOG_FILES_IMAGES, WildLogFileType.IMAGE, inFolderName, fromFile, inApp, inID);
                        }
                        else
                        // Is a movie
                        if (new MovieFilter().accept(fromFile)) {
                            saveOriginalFile(WildLogPaths.WILDLOG_FILES_MOVIES, WildLogFileType.MOVIE, inFolderName, fromFile, inApp, inID);
                        }
                        else {
                            saveOriginalFile(WildLogPaths.WILDLOG_FILES_OTHER, WildLogFileType.OTHER, inFolderName, fromFile, inApp, inID);
                        }
                    }
                }
            });
        }
        UtilsConcurency.tryAndWaitToShutdownExecutorService(executorService);
        UtilsImageProcessing.setupFoto(inID, 0, inImageLabel, inSize, inApp);
    }

    private static void saveOriginalFile(WildLogPaths inFilePaths, WildLogFileType inFileType, String inFolderName, File inFromFile, WildLogApp inApp, String inID) {
        // Make the folder
        new File(inFilePaths.getFullPath() + inFolderName).mkdirs();
        // Setup the output files
        File toFile_Original = new File(WildLogPaths.concatPaths(true, inFilePaths.getFullPath(), inFolderName, inFromFile.getName()));
        // Check that the filename is unique
        while (toFile_Original.exists()) {
            toFile_Original = new File(WildLogPaths.concatPaths(true, toFile_Original.getParent(), "wl_" + toFile_Original.getName()));
        }
        // Copy the original file into WildLog's folders
        copyFile(inFromFile, toFile_Original);
        // Save the database entry
        inApp.getDBI().createOrUpdate(
                new WildLogFile(
                        inID,
                        toFile_Original.getName(),
                        WildLogPaths.stripRootFromPath(toFile_Original.getAbsolutePath(), WildLogPaths.getFullWorkspacePrefix()),
                        inFileType)
                , false);
    }

    public static void openFile(String inID, int inIndex, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > 0) {
            String fileName = fotos.get(inIndex).getFilePath(true);
            openFile(fileName);
        }
    }

    public static void openFile(String inPath) {
        if (inPath != null) {
            try {
                Desktop.getDesktop().open(new File(inPath));
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
                // Backup Plan - Because of Java 6 bug for avi files
                try {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.indexOf("mac") != -1)
                    {
                        String[] commands = {"open", "%s", inPath};
                        Runtime.getRuntime().exec(commands);
                    }
                    else
                    if ((os.indexOf("windows") != -1 || os.indexOf("nt") != -1) && (os.equals("windows 95") || os.equals("windows 98")))
                    {
                        String[] commands = {"command.com", "/C", "start", "%s", inPath};
                        Runtime.getRuntime().exec(commands);
                    }
                    else
                    if (os.indexOf("windows") != -1 || os.indexOf("nt") != -1)
                    {
                        String[] commands = {"cmd", "/c", "start", "\"DoNothing\"", inPath};
                        Runtime.getRuntime().exec(commands);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    public static void copyFile(File inFileToRead, File inFileToWrite) {
        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;
        try {
            outputStream = new FileOutputStream(inFileToWrite);
            inputStream = new FileInputStream(inFileToRead);
            inputStream.getChannel().transferTo(0, inFileToRead.length(), outputStream.getChannel());
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            if (outputStream != null)
                try {
                    outputStream.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
        }
    }

    public static void copyFile(InputStream inFileToRead, File inFileToWrite) {
        if (!inFileToWrite.exists()) {
            InputStream fileInput = null;
            FileOutputStream fileOutput = null;
            try {
                fileInput = inFileToRead;
                if (fileInput != null) {
                    fileOutput = new FileOutputStream(inFileToWrite);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = fileInput.read(buf)) > 0) {
                        fileOutput.write(buf, 0, len);
                    }
                    fileOutput.flush();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            finally {
                try {
                    if (fileInput != null)
                        fileInput.close();
                    if (fileOutput != null)
                        fileOutput.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    public static void deleteRecursive(File inFile) throws IOException {
        if (inFile.isDirectory()) {
            for (File content : inFile.listFiles())
                deleteRecursive(content);
        }
        if (inFile.exists() && !inFile.delete())
            throw new FileNotFoundException("Failed to delete file: " + inFile);
    }

    public static void deleteRecursiveOnlyEmptyFolders(File inFile) throws IOException {
        if (inFile.isDirectory()) {
            for (File content : inFile.listFiles())
                deleteRecursiveOnlyEmptyFolders(content);
            if (inFile.listFiles().length == 0)
                if (!inFile.delete())
                    throw new FileNotFoundException("Failed to delete folder: " + inFile);
        }
    }

}
