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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dbi.DBI;
import wildlog.data.enums.WildLogFileType;
import wildlog.utils.FilePaths;

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

    public static ImageIcon getScaledIcon(ImageIcon inIcon, int inSize) {
        int finalHeight = inSize;
        int finalWidth = inSize;
        if (inIcon.getImage().getHeight(null) >= inIcon.getImage().getWidth(null)) {
            if (inIcon.getImage().getHeight(null) >= inSize) {
                double ratio = (double)inIcon.getImage().getHeight(null)/inSize;
                finalWidth = (int)(inIcon.getImage().getWidth(null)/ratio);
            }
            else {
                double ratio = (double)inSize/inIcon.getImage().getHeight(null);
                finalWidth = (int)(inIcon.getImage().getWidth(null)*ratio);
            }
        }
        else {
            if (inIcon.getImage().getWidth(null) >= inSize) {
                double ratio = (double)inIcon.getImage().getWidth(null)/inSize;
                finalHeight = (int)(inIcon.getImage().getHeight(null)/ratio);
            }
            else {
                double ratio = (double)inSize/inIcon.getImage().getWidth(null);
                finalWidth = (int)(inIcon.getImage().getHeight(null)*ratio);
            }
        }
        inIcon.setImage(Utils.getScaledImage(inIcon.getImage(), finalWidth, finalHeight));
        return inIcon;
    }
    
    public static Image getScaledImage(Image inImage, int inWidth, int inHeight) {
        BufferedImage resizedImg = new BufferedImage(inWidth, inHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(inImage, 0, 0, inWidth, inHeight, null);
        g2.dispose();
        return resizedImg;
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
    
    private static void performFileUpload(String inID, String inFolderName, File[] inFiles, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        for (int t = 0; t < inFiles.length; t++) {
            File fromFile = inFiles[t];
            if (fromFile != null && fromFile.isFile()) {
                lastFilePath = fromFile.getPath();
                // Is an image
                if (new ImageFilter().accept(fromFile)) {
                    File toDir = new File(FilePaths.WILDLOG_IMAGES.getFullPath() + inFolderName);
                    toDir.mkdirs();
                    File toFile_Original = new File(toDir.getAbsolutePath() + File.separatorChar + "Original_"+fromFile.getName());
                    File toFile_Thumbnail = new File(toDir.getAbsolutePath() + File.separatorChar + fromFile.getName());
                    while (toFile_Original.exists()) {
                        toFile_Original = new File(toFile_Original.getPath().substring(0, toFile_Original.getPath().length() - 4) + "x" + toFile_Original.getPath().substring(toFile_Original.getPath().length() - 4));
                    }
                    while (toFile_Thumbnail.exists()) {
                        toFile_Thumbnail = new File(toFile_Thumbnail.getPath().substring(0, toFile_Thumbnail.getPath().length() - 4) + "x" + toFile_Thumbnail.getPath().substring(toFile_Thumbnail.getPath().length() - 4));
                    }
                    FileInputStream fileInput = null;
                    FileOutputStream fileOutput = null;
                    try {
                        // Write Original
                        fileInput = new FileInputStream(fromFile);
                        fileOutput = new FileOutputStream(toFile_Original);
                        byte[] tempBytes = new byte[(int)fromFile.length()];
                        fileInput.read(tempBytes);
                        fileOutput.write(tempBytes);
                        fileOutput.flush();
                        // Write Thumbnail
                        fileInput.close();
                        fileOutput.close();
                        fileInput = new FileInputStream(fromFile);
                        fileOutput = new FileOutputStream(toFile_Thumbnail);
                        tempBytes = new byte[(int)fromFile.length()];
                        fileInput.read(tempBytes);
                        ImageIcon image = new ImageIcon(tempBytes);
                        image = getScaledIcon(image, THUMBNAIL_SIZE);
                        BufferedImage bi = new BufferedImage(image.getIconWidth(), image.getIconHeight(), BufferedImage.TYPE_INT_RGB);
                        Graphics2D big = bi.createGraphics();
                        big.drawImage(image.getImage(), 0, 0, null);
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write(bi, "jpg", os);
                        byte[] newBytes = os.toByteArray();
                        fileOutput.write(newBytes);
                        fileOutput.flush();
                        big.dispose();
                        inApp.getDBI().createOrUpdate(
                                new WildLogFile(inID, toFile_Thumbnail.getName(),
                                    stripRootFromPath(toFile_Thumbnail.getAbsolutePath(), FilePaths.getFullWorkspacePrefix()),
                                    stripRootFromPath(toFile_Original.getAbsolutePath(), FilePaths.getFullWorkspacePrefix()),
                                    WildLogFileType.IMAGE)
                                , false);
                        setupFoto(inID, 0, inImageLabel, inSize, inApp);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    finally {
                        try {
                            fileInput.close();
                            fileOutput.close();
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                else
                // Is a movie
                if (new MovieFilter().accept(fromFile)) {
                    File toDir = new File(FilePaths.WILDLOG_MOVIES.getFullPath() + inFolderName);
                    toDir.mkdirs();
                    File toFile = new File(toDir.getAbsolutePath() + File.separatorChar + fromFile.getName());
                    while (toFile.exists()) {
                        toFile = new File(toFile.getPath().substring(0, toFile.getPath().length() - 4) + "x" + toFile.getPath().substring(toFile.getPath().length() - 4));
                    }
                    FileInputStream fileInput = null;
                    FileOutputStream fileOutput = null;
                    try {
                        // Write Original
                        fileInput = new FileInputStream(fromFile);
                        fileOutput = new FileOutputStream(toFile);
                        byte[] tempBytes = new byte[(int)fromFile.length()];
                        fileInput.read(tempBytes);
                        fileOutput.write(tempBytes);
                        fileOutput.flush();
                        inApp.getDBI().createOrUpdate(
                                new WildLogFile(inID, toFile.getName(),
                                    "No Thumbnail",
                                    stripRootFromPath(toFile.getAbsolutePath(), FilePaths.getFullWorkspacePrefix()),
                                    WildLogFileType.MOVIE)
                                , false);
                        setupFoto(inID, 0, inImageLabel, inSize, inApp);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    finally {
                        try {
                            fileInput.close();
                            fileOutput.close();
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                else {
                    File toDir = new File(FilePaths.WILDLOG_OTHER.getFullPath() + inFolderName);
                    toDir.mkdirs();
                    File toFile = new File(toDir.getAbsolutePath() + File.separatorChar + fromFile.getName());
                    while (toFile.exists()) {
                        toFile = new File(toFile.getPath().substring(0, toFile.getPath().length() - 4) + "x" + toFile.getPath().substring(toFile.getPath().length() - 4));
                    }
                    FileInputStream fileInput = null;
                    FileOutputStream fileOutput = null;
                    try {
                        // Write Original
                        fileInput = new FileInputStream(fromFile);
                        fileOutput = new FileOutputStream(toFile);
                        byte[] tempBytes = new byte[(int)fromFile.length()];
                        fileInput.read(tempBytes);
                        fileOutput.write(tempBytes);
                        fileOutput.flush();
                        inApp.getDBI().createOrUpdate(
                                new WildLogFile(inID, toFile.getName(),
                                    "No Thumbnail",
                                    stripRootFromPath(toFile.getAbsolutePath(), FilePaths.getFullWorkspacePrefix()),
                                    WildLogFileType.OTHER)
                                , false);
                        setupFoto(inID, 0, inImageLabel, inSize, inApp);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    finally {
                        try {
                            fileInput.close();
                            fileOutput.close();
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
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

    public static int removeImage(String inID, int inImageIndex, JLabel inImageLabel, DBI inDBI, URL inDefaultImageURL, int inSize, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > 0) {
            WildLogFile tempFoto = fotos.get(inImageIndex);
            inDBI.delete(tempFoto);
            if (fotos.size() > 1) {
                inImageIndex--;
                inImageIndex = nextImage(inID, inImageIndex, inImageLabel, inSize, inApp);
            }
            else {
                inImageLabel.setIcon(Utils.getScaledIcon(new ImageIcon(inDefaultImageURL), inSize));
            }
        }
        else {
            inImageLabel.setIcon(Utils.getScaledIcon(new ImageIcon(inDefaultImageURL), inSize));
        }
        return inImageIndex;
    }

    public static void setupFoto(String inID, int inImageIndex, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > inImageIndex) {
            if (fotos.get(inImageIndex).getFotoType() != null) {
                if (fotos.get(inImageIndex).getFotoType().equals(WildLogFileType.IMAGE))
                    inImageLabel.setIcon(getScaledIcon(new ImageIcon(fotos.get(inImageIndex).getFileLocation(true)), inSize));
                else
                if (fotos.get(inImageIndex).getFotoType().equals(WildLogFileType.MOVIE))
                    inImageLabel.setIcon(getScaledIcon(new ImageIcon(inApp.getClass().getResource("resources/images/Movie.gif")), inSize));
                else
                if (fotos.get(inImageIndex).getFotoType().equals(WildLogFileType.OTHER))
                    inImageLabel.setIcon(getScaledIcon(new ImageIcon(inApp.getClass().getResource("resources/images/OtherFile.gif")), inSize));
                inImageLabel.setToolTipText(fotos.get(inImageIndex).getFilename());
            }
            else {
                inImageLabel.setIcon(getScaledIcon(new ImageIcon(inApp.getClass().getResource("resources/images/NoImage.gif")), inSize));
                inImageLabel.setToolTipText("");
            }
        }
        else {
            inImageLabel.setIcon(getScaledIcon(new ImageIcon(inApp.getClass().getResource("resources/images/NoImage.gif")), inSize));
            inImageLabel.setToolTipText("");
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
                ex.printStackTrace();
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
                    ex.printStackTrace();
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
                ex.printStackTrace();
            }
            finally {
                try {
                    if (fileInput != null)
                        fileInput.close();
                    if (fileOutput != null)
                        fileOutput.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
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
                    ex.printStackTrace();
                }
                catch (JpegProcessingException ex) {
                    ex.printStackTrace();
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

}
