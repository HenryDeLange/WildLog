package wildlog.utils;

import wildlog.ui.helpers.ImagePreview;
import wildlog.ui.helpers.ImageFilter;
import wildlog.ui.helpers.MovieFilter;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.WildLogFileType;
import wildlog.ui.dialogs.utils.UtilsDialog;

public final class UtilsFileProcessing {
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";
    private static final int THUMBNAIL_SIZE = 300;
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
    public static int uploadImage(String inID, String inFolderName, Component inComponent, JLabel inImageLabel, int inSize, final WildLogApp inApp) {
        if (inComponent != null)
            inComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
        if ((result != JFileChooser.ERROR_OPTION) && (result == JFileChooser.APPROVE_OPTION)) {
            performFileUpload(inID, inFolderName, fileChooser.getSelectedFiles(), inImageLabel, inSize, inApp);
        }
        if (inComponent != null)
            inComponent.setCursor(Cursor.getDefaultCursor());
        // return new image index
        return 0;
    }

    /**
     * Upload a file using a List of Files. (Used with FileDrop.)
     */
    public static int uploadImage(String inID, String inFolderName, Component inComponent, JLabel inImageLabel, int inSize, WildLogApp inApp, List<File> inFiles) {
        if (inComponent != null)
            inComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        performFileUpload(inID, inFolderName, inFiles.toArray(new File[inFiles.size()]), inImageLabel, inSize, inApp);
        if (inComponent != null)
            inComponent.setCursor(Cursor.getDefaultCursor());
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
                            // Get the thumbnail first
                            // Make the folder
                            new File(WildLogPaths.WILDLOG_IMAGES_THUMBNAILS.getFullPath() + inFolderName).mkdirs();
                            // Setup the output files
                            File toFile_Thumbnail = new File(WildLogPaths.concatPaths(WildLogPaths.WILDLOG_IMAGES_THUMBNAILS.getFullPath(), inFolderName, fromFile.getName()));
                            // Check that the filename is unique
                            while (toFile_Thumbnail.exists()) {
                                toFile_Thumbnail = new File(WildLogPaths.concatPaths(toFile_Thumbnail.getParent(), "wl_" + toFile_Thumbnail.getName()));
                            }
                            // Resize the file and then save the thumbnail to into WildLog's folders
                            ImageIcon thumbnail = UtilsImageProcessing.getScaledIcon(fromFile, THUMBNAIL_SIZE);
                            try {
                                BufferedImage bufferedImage = new BufferedImage(thumbnail.getIconWidth(), thumbnail.getIconHeight(), BufferedImage.TYPE_INT_RGB);
                                Graphics2D graphics2D = bufferedImage.createGraphics();
                                graphics2D.drawImage(thumbnail.getImage(), 0, 0, null);
                                ImageIO.write(bufferedImage, "jpg", toFile_Thumbnail);
                                graphics2D.dispose();
                            }
                            catch (IOException ex) {
                                ex.printStackTrace(System.err);
                            }
                            saveOriginalFile(WildLogPaths.WILDLOG_IMAGES, WildLogFileType.IMAGE, inFolderName, fromFile, inApp, inID, toFile_Thumbnail.getAbsolutePath());
                        }
                        else
                        // Is a movie
                        if (new MovieFilter().accept(fromFile)) {
                            saveOriginalFile(WildLogPaths.WILDLOG_MOVIES, WildLogFileType.MOVIE, inFolderName, fromFile, inApp, inID, null);
                        }
                        else {
                            saveOriginalFile(WildLogPaths.WILDLOG_OTHER, WildLogFileType.OTHER, inFolderName, fromFile, inApp, inID, null);
                        }
                    }
                }
            });
        }
        UtilsConcurency.tryAndWaitToShutdownExecutorService(executorService);
        UtilsImageProcessing.setupFoto(inID, 0, inImageLabel, inSize, inApp);
    }

    private static void saveOriginalFile(WildLogPaths inFilePaths, WildLogFileType inFileType, String inFolderName, File inFromFile, WildLogApp inApp, String inID, String inThumbnailPath) {
        // Make the folder
        new File(inFilePaths.getFullPath() + inFolderName).mkdirs();
        // Setup the output files
        File toFile_Original = new File(WildLogPaths.concatPaths(inFilePaths.getFullPath(), inFolderName, inFromFile.getName()));
        // Check that the filename is unique
        while (toFile_Original.exists()) {
            toFile_Original = new File(WildLogPaths.concatPaths(toFile_Original.getParent(), "wl_" + toFile_Original.getName()));
        }
        // Copy the original file into WildLog's folders
        copyFile(inFromFile, toFile_Original);
        // Save the database entry
        String thumbnailPath = "No Thumbnail";
        if (inThumbnailPath != null)
            thumbnailPath = WildLogPaths.stripRootFromPath(inThumbnailPath, WildLogPaths.getFullWorkspacePrefix());
        inApp.getDBI().createOrUpdate(
                new WildLogFile(
                        inID,
                        toFile_Original.getName(),
                        thumbnailPath,
                        WildLogPaths.stripRootFromPath(toFile_Original.getAbsolutePath(), WildLogPaths.getFullWorkspacePrefix()),
                        inFileType)
                , false);
    }

    public static void openFile(String inID, int inIndex, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > 0) {
            String fileName = fotos.get(inIndex).getOriginalFotoLocation(true);
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
                    ex.printStackTrace(System.err);
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
