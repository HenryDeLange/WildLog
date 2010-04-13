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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Foto;
import wildlog.data.dataobjects.interfaces.HasFotos;
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

    public static int uploadImage(HasFotos inHasFotos, String inFolderName, Component inComponent, JLabel inImageLabel, int inSize, WildLogApp inApp) {
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
                        if (inHasFotos.getFotos() == null) inHasFotos.setFotos(new ArrayList<Foto>());
                        inHasFotos.getFotos().add(new Foto(toFile_Thumbnail.getName(), toFile_Thumbnail.getAbsolutePath(), toFile_Original.getAbsolutePath(), FotoType.IMAGE));
                        big.dispose();
                        setupFoto(inHasFotos, inHasFotos.getFotos().size() - 1, inImageLabel, inSize, inApp);
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
                        if (inHasFotos.getFotos() == null) inHasFotos.setFotos(new ArrayList<Foto>());
                        inHasFotos.getFotos().add(new Foto(toFile.getName(), "No Thumbnail", toFile.getAbsolutePath(), FotoType.MOVIE));
                        setupFoto(inHasFotos, inHasFotos.getFotos().size() - 1, inImageLabel, inSize, inApp);
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
                        if (inHasFotos.getFotos() == null) inHasFotos.setFotos(new ArrayList<Foto>());
                        inHasFotos.getFotos().add(new Foto(toFile.getName(), "No Thumbnail", toFile.getAbsolutePath(), FotoType.OTHER));
                        setupFoto(inHasFotos, inHasFotos.getFotos().size() - 1, inImageLabel, inSize, inApp);
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
        if (inHasFotos.getFotos().size() - 1 > 0) return inHasFotos.getFotos().size() - 1;
        else return 0;
    }

    // Methods for the buttons on the panels that work with the images
    public static int previousImage(HasFotos inHasFotos, int inImageIndex, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        if (inHasFotos.getFotos().size() > 0) {
            if (inImageIndex > 0) {
                inImageIndex = inImageIndex - 1;
            }
            else {
                inImageIndex = inHasFotos.getFotos().size() - 1;
            }
            setupFoto(inHasFotos, inImageIndex, inImageLabel, inSize, inApp);
        }
        return inImageIndex;
    }

    public static int nextImage(HasFotos inHasFotos, int inImageIndex, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        if (inHasFotos.getFotos().size() > 0) {
            if (inImageIndex < inHasFotos.getFotos().size() - 1) {
                inImageIndex = inImageIndex + 1;
            }
            else {
                inImageIndex = 0;
            }
            setupFoto(inHasFotos, inImageIndex, inImageLabel, inSize, inApp);
        }
        return inImageIndex;
    }

    public static int setMainImage(HasFotos inHasFotos, int inImageIndex) {
        if (inHasFotos.getFotos().size() > 0) {
            inHasFotos.getFotos().add(0, inHasFotos.getFotos().get(inImageIndex++));
            inHasFotos.getFotos().remove(inImageIndex);
            inImageIndex = 0;
        }
        return inImageIndex;
    }

    public static int removeImage(HasFotos inHasFotos, int inImageIndex, JLabel inImageLabel, DBI inDBI, URL inDefaultImageURL, int inSize, WildLogApp inApp) {
        if (inHasFotos.getFotos().size() > 0) {
            Foto tempFoto = inHasFotos.getFotos().get(inImageIndex);
            inHasFotos.getFotos().remove(tempFoto);
            inDBI.delete(tempFoto);
            File tempFile = new File(tempFoto.getFileLocation());
            tempFile.delete();
            tempFile = new File(tempFoto.getOriginalFotoLocation());
            tempFile.delete();
            if (inHasFotos.getFotos().size() >= 1) {
                // Behave like moving back button was pressed
                inImageIndex = previousImage(inHasFotos, inImageIndex, inImageLabel, inSize, inApp);
            }
            else {
                inImageLabel.setIcon(Utils.getScaledIcon(new ImageIcon(inDefaultImageURL), inSize));
            }
        }
        return inImageIndex;
    }

    public static void setupFoto(HasFotos inHasFotos, int inImageIndex, JLabel inImageLabel, int inSize, WildLogApp inApp) {
        if (inHasFotos.getFotos().get(inImageIndex).getFotoType() != null) {
            if (inHasFotos.getFotos().get(inImageIndex).getFotoType().equals(FotoType.IMAGE))
                inImageLabel.setIcon(getScaledIcon(new ImageIcon(inHasFotos.getFotos().get(inImageIndex).getFileLocation()), inSize));
            else
            if (inHasFotos.getFotos().get(inImageIndex).getFotoType().equals(FotoType.MOVIE))
                inImageLabel.setIcon(getScaledIcon(new ImageIcon(inApp.getClass().getResource("resources/images/Movie.gif")), inSize));
            else
            if (inHasFotos.getFotos().get(inImageIndex).getFotoType().equals(FotoType.OTHER))
                inImageLabel.setIcon(getScaledIcon(new ImageIcon(inApp.getClass().getResource("resources/images/OtherFile.gif")), inSize));
        }
        else {
            inImageLabel.setIcon(getScaledIcon(new ImageIcon(inApp.getClass().getResource("resources/images/NoImage.gif")), inSize));
        }
    }

    public static void openImage(HasFotos inHasFotos, int inIndex) {
        if (inHasFotos != null) {
            if (System.getProperty("os.name").equals("Windows XP")) {
                try {
                    if (inHasFotos.getFotos().size() > 0) {
                        String fileName = inHasFotos.getFotos().get(inIndex).getOriginalFotoLocation();
                        String[] commands = {"cmd", "/c", "start", "\"DoNothing\"", fileName};
                        Runtime.getRuntime().exec(commands);
                    }
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void openImage(String inPath) {
        if (inPath != null) {
            if (System.getProperty("os.name").equals("Windows XP")) {
                try {
                    String[] commands = {"cmd", "/c", "start", "\"DoNothing\"", inPath};
                    Runtime.getRuntime().exec(commands);
                }
                catch (IOException ex) {
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
