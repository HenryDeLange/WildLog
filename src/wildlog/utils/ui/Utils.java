/*
 * Utils.java is part of WildLog
 *
 * Copyright (C) 2009 Henry James de Lange
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wildlog.utils.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Foto;
import wildlog.data.dbi.DBI;
import wildlog.data.enums.FotoType;

/* Utils.java is used by FileChooserDemo2.java. */
public class Utils {
    protected final static String jpeg = "jpeg";
    protected final static String jpg = "jpg";
    protected final static String gif = "gif";
    protected final static String tiff = "tiff";
    protected final static String tif = "tif";
    protected final static String png = "png";
    private static final int THUMBNAIL_SIZE = 300;
    private static String lastFilePath = "";

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = Utils.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
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
    
    private static Image getScaledImage(Image inImage, int inWidth, int inHeight) {
        BufferedImage resizedImg = new BufferedImage(inWidth, inHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(inImage, 0, 0, inWidth, inHeight, null);
        g2.dispose();
        return resizedImg;
    }

    public static int uploadImage(String inID, String inFolderName, Component inComponent, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        inComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        // Native File Upload Window. Het Thumbnails, maar het nie Multi Selct nie :(
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
            File[] files = fileChooser.getSelectedFiles();
            for (int t = 0; t < files.length; t++) {
                File fromFile = files[t];
                lastFilePath = fromFile.getPath();
                // Is an image
                if (new ImageFilter().accept(fromFile)) {
                    File toDir = new File(File.separatorChar + "WildLog" + File.separatorChar + "Images" + File.separatorChar + inFolderName);
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
                        inApp.getDBI().createOrUpdate(new Foto(inID, toFile_Thumbnail.getName(), toFile_Thumbnail.getAbsolutePath(), toFile_Original.getAbsolutePath(), FotoType.IMAGE), false);
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
                    File toDir = new File(File.separatorChar + "WildLog" + File.separatorChar + "Movies" + File.separatorChar + inFolderName);
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
                        inApp.getDBI().createOrUpdate(new Foto(inID, toFile.getName(), "No Thumbnail", toFile.getAbsolutePath(), FotoType.MOVIE), false);
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
                    File toDir = new File(File.separatorChar + "WildLog" + File.separatorChar + "Other Uploads" + File.separatorChar + inFolderName);
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
                        inApp.getDBI().createOrUpdate(new Foto(inID, toFile.getName(), "No Thumbnail", toFile.getAbsolutePath(), FotoType.OTHER), false);
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
        inComponent.setCursor(Cursor.getDefaultCursor());
        // return new image index
        return 0;
    }

    // Methods for the buttons on the panels that work with the images
    public static int previousImage(String inID, int inImageIndex, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        List<Foto> fotos = inApp.getDBI().list(new Foto(inID));
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
        List<Foto> fotos = inApp.getDBI().list(new Foto(inID));
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
        List<Foto> fotos = inApp.getDBI().list(new Foto(inID));
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
        List<Foto> fotos = inApp.getDBI().list(new Foto(inID));
        if (fotos.size() > 0) {
            Foto tempFoto = fotos.get(inImageIndex);
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
        List<Foto> fotos = inApp.getDBI().list(new Foto(inID));
        if (fotos.size() > inImageIndex) {
            if (fotos.get(inImageIndex).getFotoType() != null) {
                if (fotos.get(inImageIndex).getFotoType().equals(FotoType.IMAGE))
                    inImageLabel.setIcon(getScaledIcon(new ImageIcon(fotos.get(inImageIndex).getFileLocation()), inSize));
                else
                if (fotos.get(inImageIndex).getFotoType().equals(FotoType.MOVIE))
                    inImageLabel.setIcon(getScaledIcon(new ImageIcon(inApp.getClass().getResource("resources/images/Movie.gif")), inSize));
                else
                if (fotos.get(inImageIndex).getFotoType().equals(FotoType.OTHER))
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
        List<Foto> fotos = inApp.getDBI().list(new Foto(inID));
        if (fotos.size() > 0) {
            String fileName = fotos.get(inIndex).getOriginalFotoLocation();
            try {
                Desktop.getDesktop().open(new File(fileName));
            }
            catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                // Backup Plan - Because of Java 6 bug for avi files
                try {
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.indexOf("mac") != -1)
                    {
                        String[] commands = {"open", "%s", fileName};
                        Runtime.getRuntime().exec(commands);
                    }
                    else
                    if ((os.indexOf("windows") != -1 || os.indexOf("nt") != -1) && (os.equals("windows 95") || os.equals("windows 98")))
                    {
                        String[] commands = {"command.com", "/C", "start", "%s", fileName};
                        Runtime.getRuntime().exec(commands);
                    }
                    else
                    if (os.indexOf("windows") != -1 || os.indexOf("nt") != -1)
                    {
                        String[] commands = {"cmd", "/c", "start", "\"DoNothing\"", fileName};
                        Runtime.getRuntime().exec(commands);
                    }
                }
                catch (IOException e) {
                    ex.printStackTrace();
                }
            }
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
            if (c == '`' || c == '\'') continue;
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


}
