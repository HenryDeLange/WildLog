package wildlog.utils.ui;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.enums.WildLogFileType;
import wildlog.utils.WildLogPaths;
import wildlog.utils.jpegmovie.JpgToMovie;

public final class Utils {
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

    public static ImageIcon getScaledIcon(File inFile, int inSize) {
        try {
            FileImageInputStream inputStream = new FileImageInputStream(inFile);
            Iterator<ImageReader> imageReaderList = ImageIO.getImageReaders(inputStream);
            ImageReader imageReader = imageReaderList.next();
            imageReader.setInput(inputStream);
            int imageWidth = imageReader.getWidth(imageReader.getMinIndex());
            int imageHeight = imageReader.getHeight(imageReader.getMinIndex());
            int finalHeight = inSize;
            int finalWidth = inSize;
            if (imageHeight >= imageWidth) {
                if (imageHeight >= inSize) {
                    double ratio = (double)imageHeight/inSize;
                    finalWidth = (int)(imageWidth/ratio);
                }
                else {
                    double ratio = (double)inSize/imageHeight;
                    finalWidth = (int)(imageWidth*ratio);
                }
            }
            else {
                if (imageWidth >= inSize) {
                    double ratio = (double)imageWidth/inSize;
                    finalHeight = (int)(imageHeight/ratio);
                }
                else {
                    double ratio = (double)imageWidth;
                    finalHeight = (int)(imageHeight*ratio);
                }
            }
            imageReader.dispose();
            inputStream.close();
            return new ImageIcon(getScaledImage(Toolkit.getDefaultToolkit().createImage(inFile.getAbsolutePath()), finalWidth, finalHeight));
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        return getScaledIconForNoImage(inSize);
    }

    public static ImageIcon getScaledIcon(String inPath, int inSize) {
        return getScaledIcon(new File(inPath), inSize);
    }

    public static ImageIcon getScaledIcon(URL inRUL, int inSize) {
        try {
            return getScaledIcon(new File(inRUL.toURI()), inSize);
        }
        catch (URISyntaxException ex) {
            ex.printStackTrace(System.err);
        }
        return getScaledIcon(inRUL.getPath(), inSize);
    }

    public static ImageIcon getScaledIconForNoImage(int inSize) {
        return getScaledIcon(WildLogApp.class.getResource("resources/images/NoImage.gif"), inSize);
    }

    private static Image getScaledImage(Image inImage, int inWidth, int inHeight) {
        return inImage.getScaledInstance(inWidth, inHeight, Image.SCALE_DEFAULT);
    }

    /**
     * Upload a file using a FileChooser dialog.
     */
    public static int uploadImage(String inID, String inFolderName, Component inComponent, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        if (inComponent != null)
            inComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        // Native File Upload Window. Het Thumbnails, maar het nie Multi Select nie :(
//        FileDialog d = new FileDialog(new Frame(), "Select Images", FileDialog.LOAD);
//        d.setDirectory(lastFilePath);
//        d.setVisible(true);
        JFileChooser fileChooser;
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
        int result = fileChooser.showOpenDialog(inComponent);
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
                            ImageIcon thumbnail = getScaledIcon(fromFile, THUMBNAIL_SIZE);
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
        tryAndWaitToShutdownExecutorService(executorService);
        setupFoto(inID, 0, inImageLabel, inSize, inApp);
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
            thumbnailPath = stripRootFromPath(inThumbnailPath, WildLogPaths.getFullWorkspacePrefix());
        inApp.getDBI().createOrUpdate(
                new WildLogFile(
                        inID,
                        toFile_Original.getName(),
                        thumbnailPath,
                        stripRootFromPath(toFile_Original.getAbsolutePath(), WildLogPaths.getFullWorkspacePrefix()),
                        inFileType)
                , false);
    }

    // Methods for the buttons on the panels that work with the images
    public static int previousImage(String inID, int inImageIndex, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > 0) {
            if (inImageIndex > 0) {
                inImageIndex = inImageIndex - 1;
            }
            else {
                inImageIndex = fotos.size() - 1;
            }
            setupFoto(inID, inImageIndex, inImageLabel, inSize, inApp);
        }
        return inImageIndex;
    }

    public static int nextImage(String inID, int inImageIndex, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > 0) {
            if (inImageIndex < fotos.size() - 1) {
                inImageIndex = inImageIndex + 1;
            }
            else {
                inImageIndex = 0;
            }
            setupFoto(inID, inImageIndex, inImageLabel, inSize, inApp);
        }
        return inImageIndex;
    }

    public static int setMainImage(String inID, int inImageIndex, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        for (int t = 0; t < fotos.size(); t++) {
            if (t != inImageIndex)
                fotos.get(t).setDefaultFile(false);
            else
                fotos.get(t).setDefaultFile(true);
            inApp.getDBI().createOrUpdate(fotos.get(t), true);
        }
        inImageIndex = 0;
        return inImageIndex;
    }

    public static int removeImage(String inID, int inImageIndex, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > 0) {
            WildLogFile tempFoto = fotos.get(inImageIndex);
            inApp.getDBI().delete(tempFoto);
            if (fotos.size() > 1) {
                inImageIndex--;
                inImageIndex = nextImage(inID, inImageIndex, inImageLabel, inSize, inApp);
            }
            else {
                inImageLabel.setIcon(Utils.getScaledIconForNoImage(inSize));
            }
        }
        else {
            inImageLabel.setIcon(Utils.getScaledIconForNoImage(inSize));
        }
        return inImageIndex;
    }

    public static void setupFoto(String inID, int inImageIndex, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        if (inImageLabel != null) {
            List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
            if (fotos.size() > inImageIndex) {
                if (fotos.get(inImageIndex).getFotoType() != null) {
                    if (fotos.get(inImageIndex).getFotoType().equals(WildLogFileType.IMAGE))
                        inImageLabel.setIcon(getScaledIcon(fotos.get(inImageIndex).getFileLocation(true), inSize));
                    else
                    if (fotos.get(inImageIndex).getFotoType().equals(WildLogFileType.MOVIE))
                        inImageLabel.setIcon(getScaledIcon(inApp.getClass().getResource("resources/images/Movie.gif"), inSize));
                    else
                    if (fotos.get(inImageIndex).getFotoType().equals(WildLogFileType.OTHER))
                        inImageLabel.setIcon(getScaledIcon(inApp.getClass().getResource("resources/images/OtherFile.gif"), inSize));
                    inImageLabel.setToolTipText(fotos.get(inImageIndex).getFilename());
                }
                else {
                    inImageLabel.setIcon(getScaledIconForNoImage(inSize));
                    inImageLabel.setToolTipText("");
                }
            }
            else {
                inImageLabel.setIcon(getScaledIconForNoImage(inSize));
                inImageLabel.setToolTipText("");
            }
        }
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


    public static boolean checkCharacters(final String s) {
        if (s == null) return false;
        final char[] chars = s.toCharArray();
        for (int x = 0; x < chars.length; x++) {
            final char c = chars[x];
            if ((c >= 'a') && (c <= 'z')) continue; // Lowercase
            if ((c >= 'A') && (c <= 'Z')) continue; // Uppercase
            if ((c >= '0') && (c <= '9')) continue; // Numeric
            if (c == 'ê' || c == 'ë') continue;
            if (c == 'ô' || c == ' ') continue;
            // Check Characters
            if (c == '!') continue;
            if (c == '.' || c == ',') continue;
            if (c == '-' || c == '_') continue;
            if (c == '(' || c == ')') continue;
            if (c == '[' || c == ']') continue;
            if (c == '&' || c == '@') continue;
            if (c == '#' || c == ';') continue;
            if (c == '+' || c == '=') continue;
            if (c == '`' /*|| c == '\''*/) continue; // Die ' gee probleme met saving en file loading
            return false;
        }
        return true;
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

    public static void showExifPopup(File inFile) {
        if (inFile != null) {
            if (inFile.exists()) {
                JFrame frame = new JFrame("EXIF Meta Data: " + inFile.getPath());
                JTextPane txtPane = new JTextPane();
                txtPane.setContentType("text/html");
                txtPane.setEditable(false);
                String temp = "";
                try {
                    Metadata meta = JpegMetadataReader.readMetadata(inFile);
                    Iterator<Directory> directories = meta.getDirectories().iterator();
                    breakAllWhiles: while (directories.hasNext()) {
                        Directory directory = (Directory)directories.next();
                        Collection<Tag> tags = directory.getTags();
                        for (Tag tag : tags) {
                            String name = tag.getTagName();
                            String description = tag.getDescription();
                            temp = temp + "<b>" + name + ":</b> " + description + "<br/>";
                        }
                    }
                    txtPane.setText(temp);
                    txtPane.setCaretPosition(0);
                    JScrollPane scroll = new JScrollPane(txtPane);
                    scroll.setPreferredSize(new Dimension(500, 750));
                    frame.getContentPane().add(scroll);
                    //frame.setLocationRelativeTo(((WildLogApp)Application.getInstance()).getMainView().getComponent());
                    frame.pack();
                    frame.setVisible(true);
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
                catch (JpegProcessingException ex) {
                    ex.printStackTrace(System.err);
                    JOptionPane.showMessageDialog(null, "Could not process the file.", "Trying to show image meta data", JOptionPane.ERROR_MESSAGE);
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "Could not access the file.", "Trying to show image meta data", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Could not access the file.", "Trying to show image meta data", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void showExifPopup(String inID, int inIndex, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > 0) {
            String fileName = fotos.get(inIndex).getOriginalFotoLocation(true);
            showExifPopup(new File(fileName));
        }
    }

    /**
     * This method will strip the first root part from the path.
     * WARNING: This method assumes that both the inPath and inRoot are
     * absolute proper paths.
     */
    public static String stripRootFromPath(String inPath, String inRoot) {
        return inPath.substring(inRoot.length());
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

    public static void kickoffProgressbarTask(Task inTask) {
        ApplicationContext appContext = Application.getInstance().getContext();
        TaskMonitor taskMonitor = appContext.getTaskMonitor();
        TaskService taskService = appContext.getTaskService();
        taskService.execute(inTask);
        taskMonitor.setForegroundTask(inTask);
        taskMonitor.setAutoUpdateForegroundTask(false);
    }

    public static boolean tryAndWaitToShutdownExecutorService(ExecutorService inExecutorService) {
        inExecutorService.shutdown();
        try {
            int count = 0;
            while(!inExecutorService.awaitTermination(6, TimeUnit.MINUTES) && count < 10) {
                count++;
                System.out.println("Bulk Upload: Timer expired while loading images... Resetting... " + count);
            }
            if (!inExecutorService.isTerminated()) {
                System.err.println("Bulk Upload Error: Terminating bulk import... " + count);
                inExecutorService.shutdownNow();
                return false;
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
        return true;
    }

    public static Date getExifDateFromJpeg(File inFile) {
        try {
            return getExifDateFromJpeg(JpegMetadataReader.readMetadata(inFile));
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        catch (JpegProcessingException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    public static Date getExifDateFromJpeg(InputStream inInputStream) {
        try {
            return getExifDateFromJpeg(JpegMetadataReader.readMetadata(inInputStream));
        }
        catch (JpegProcessingException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    private static Date getExifDateFromJpeg(Metadata inMeta) {
        Iterator<Directory> directories = inMeta.getDirectories().iterator();
        while (directories.hasNext()) {
            Directory directory = (Directory)directories.next();
            Collection<Tag> tags = directory.getTags();
            for (Tag tag : tags) {
                if (tag.getTagName().equalsIgnoreCase("Date/Time Original")) {
                    // Not all files store the date in the same format, so I have to try a few known formats...
                    // Try 1:
                    try {
                        // This seems to be by far the most used format
                        return new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").parse(tag.getDescription());
                    }
                    catch (ParseException ex) {
                        System.err.println("[THIS DATE (" + tag.getDescription() + ") COULD NOT BE PARSED USING 'yyyy:MM:dd HH:mm:ss']");
                        ex.printStackTrace(System.err);
                    }
                    // Try 2:
                    try {
                        // Wierd format used by Samsung Galaxy Gio (Android)
                        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").parse(tag.getDescription());
                    }
                    catch (ParseException ex) {
                        System.err.println("[THIS DATE (" + tag.getDescription() + ") COULD NOT BE PARSED USING 'yyyy-MM-dd HH:mm:ss ']");
                        ex.printStackTrace(System.err);
                    }
                }
            }
        }
        return null;
    }

    public static void setDialogToCenter(Component inParentComponent, Component inComponentToCenter) {
        Point point = inParentComponent.getLocation();
        inComponentToCenter.setLocation(
                point.x + (inParentComponent.getWidth() - inComponentToCenter.getWidth())/2,
                point.y + (inParentComponent.getHeight() - inComponentToCenter.getHeight())/2);
    }

    public static void generateSlideshow(List<String> inList, WildLogApp inApp, String inOutputFilename) {
        // Now create the slideshow
        File tempFile = new File(WildLogPaths.WILDLOG_EXPORT_SLIDESHOW.getFullPath());
        tempFile.mkdirs();
        JpgToMovie jpgToMovie = new JpgToMovie();
        if (inList.size() > 0) {
            if (jpgToMovie.createMovieFromJpgs(inApp.getWildLogOptions().getDefaultSlideshowSize(), inApp.getWildLogOptions().getDefaultSlideshowSpeed(), inList, inOutputFilename)) {
                // Lastly launch the file
                Utils.openFile(inOutputFilename);
            }
            else {
                JOptionPane.showMessageDialog(null, "There was a problem generating the slideshow.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Can't generate slideshow if there aren't any images.", "No Images", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void doClipboardCopy(String inText) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection text = new StringSelection(inText);
        clipboard.setContents(text, text);
    }

    public static String doClipboardPaste() throws UnsupportedFlavorException, IOException, ClassNotFoundException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return (String)clipboard.getData(DataFlavor.stringFlavor);
    }

    public static void attachClipboardPopup(final JTextComponent inTextField) {
        attachClipboardPopup(inTextField, false);
    }

    public static void attachClipboardPopup(final JTextComponent inTextField, final boolean inCopyOnly) {
        inTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseClicked(e);
            }
            @Override
            public void mouseClicked(MouseEvent inEvent) {
                if ((inEvent.isPopupTrigger() || SwingUtilities.isRightMouseButton(inEvent))) {
                    // Build the copy popup
                    JPopupMenu clipboardPopup = new JPopupMenu();
                    JMenuItem copyUserNameItem = new JMenuItem("Copy to clipoard.");
                    copyUserNameItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String text = inTextField.getSelectedText();
                            if (text == null || text.isEmpty())
                                text = inTextField.getText();
                            Utils.doClipboardCopy(text);
                        }
                    });
                    clipboardPopup.add(copyUserNameItem);
                    if (!inCopyOnly) {
                        // Build the paste popup
                        JMenuItem copyPasswordItem = new JMenuItem("Paste from clipboard.");
                        copyPasswordItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent inNestedEvent) {
                                try {
                                    inTextField.setText(doClipboardPaste());
                                }
                                catch (UnsupportedFlavorException | IOException | ClassNotFoundException ex) {
                                    ex.printStackTrace(System.err);
                                }
                            }
                        });
                        clipboardPopup.add(copyPasswordItem);
                    }
                    // Wrap up and show up the popup
                    clipboardPopup.pack();
                    clipboardPopup.show(inEvent.getComponent(), inEvent.getPoint().x, inEvent.getPoint().y);
                    clipboardPopup.setVisible(true);
                }
            }
        });
    }

    public static void attachKeyListernerToFilterTableRows(final JTextComponent inTxtSearch, final JTable inTable) {
        inTxtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent inEvent) {
                if (inEvent.getKeyChar() == KeyEvent.VK_ESCAPE) {
                   inTxtSearch.setText("");
                }
                TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)inTable.getRowSorter();
                if (sorter == null) {
                    sorter = new TableRowSorter<>(inTable.getModel());
                }
                // Note: The regexFilter method seems to be able to take optional parameters...
                // The (?i) makes the matching ignore case...
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + inTxtSearch.getText()));
                // Kan dit ook glo so doen:
                //sorter.setRowFilter(RowFilter.regexFilter(Pattern.compile(txtSearchField.getText(), Pattern.CASE_INSENSITIVE).toString()));
                inTable.setRowSorter(sorter);
            }
        });
    }

    public static void attachKeyListernerToSelectKeyedRows(final JTable inTable) {
        inTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent inEvent) {
                if ((inEvent.getKeyChar() >= 'A' && inEvent.getKeyChar() <= 'z') || (inEvent.getKeyChar() >= '0' && inEvent.getKeyChar() <= '9')) {
                    int select = -1;
                    for (int t = 0; t < inTable.getRowSorter().getViewRowCount(); t++) {
                        if (inTable.getValueAt(t, 0).toString().toLowerCase().startsWith((""+inEvent.getKeyChar()).toLowerCase())) {
                            select = t;
                            // A new letter was selected by the user, so go to the first line.
                            if (inTable.getValueAt(inTable.getSelectedRow(), 0).toString().toLowerCase().charAt(0) != inEvent.getKeyChar()) {
                                break;
                            }
                            else {
                                // The same letter was pressed as the selected row, thus go to the next line (if it exists and matches).
                                if (t > inTable.getSelectedRow()) {
                                    break;
                                }
                            }
                        }
                    }
                    if (select >= 0) {
                        inTable.getSelectionModel().setSelectionInterval(select, select);
                        inTable.scrollRectToVisible(inTable.getCellRect(select, 0, true));
                        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                                new MouseEvent(
                                        inTable,
                                        MouseEvent.MOUSE_RELEASED,
                                        new Date().getTime(),
                                        0,
                                        inTable.getMousePosition().x,
                                        inTable.getMousePosition().y,
                                        1,
                                        false));
                    }
                }
            }
        });
    }

}
