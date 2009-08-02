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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import wildlog.data.dataobjects.Foto;
import wildlog.data.dataobjects.interfaces.HasFotos;
import wildlog.data.dbi.DBI;

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

    public static int uploadImage(HasFotos inHasFotos, String inFolderName, Component inComponent, JLabel inImageLabel, int inSize) {
        JFileChooser fileChooser;
        if (lastFilePath.length() > 0)
            fileChooser = new JFileChooser(lastFilePath);
        else
            fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new ImageFilter());
        fileChooser.setAccessory(new ImagePreview(fileChooser));
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(inComponent);
        if ((result != JFileChooser.ERROR_OPTION) && (result == JFileChooser.APPROVE_OPTION)) {
            File[] files = fileChooser.getSelectedFiles();
            for (int t = 0; t < files.length; t++) {
                File fromFile = files[t];
                lastFilePath = fromFile.getPath();
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
                    inHasFotos.getFotos().add(new Foto(toFile_Thumbnail.getName(), toFile_Thumbnail.getAbsolutePath(), toFile_Original.getAbsolutePath()));
                    big.dispose();
                    setupFoto(inHasFotos, inHasFotos.getFotos().size() - 1, inImageLabel, inSize);
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
        if (inHasFotos.getFotos().size() - 1 > 0) return inHasFotos.getFotos().size() - 1;
        else return 0;
    }

    // Methods for the buttons on the panels that work with the images
    public static int previousImage(HasFotos inHasFotos, int inImageIndex, JLabel inImageLabel, int inSize) {
        if (inHasFotos.getFotos().size() > 0) {
            if (inImageIndex > 0) {
                inImageIndex = inImageIndex - 1;
                inImageLabel.setIcon(getScaledIcon(new ImageIcon(inHasFotos.getFotos().get(inImageIndex).getFileLocation()), inSize));
            }
            else {
                inImageIndex = inHasFotos.getFotos().size() - 1;
                inImageLabel.setIcon(getScaledIcon(new ImageIcon(inHasFotos.getFotos().get(inImageIndex).getFileLocation()), inSize));
            }
        }
        return inImageIndex;
    }

    public static int nextImage(HasFotos inHasFotos, int inImageIndex, JLabel inImageLabel, int inSize) {
        if (inHasFotos.getFotos().size() > 0) {
            if (inImageIndex < inHasFotos.getFotos().size() - 1) {
                inImageIndex = inImageIndex + 1;
                inImageLabel.setIcon(getScaledIcon(new ImageIcon(inHasFotos.getFotos().get(inImageIndex).getFileLocation()), inSize));
            }
            else {
                inImageIndex = 0;
                inImageLabel.setIcon(getScaledIcon(new ImageIcon(inHasFotos.getFotos().get(inImageIndex).getFileLocation()), inSize));
            }
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

    public static int removeImage(HasFotos inHasFotos, int inImageIndex, JLabel inImageLabel, DBI inDBI, URL inDefaultImageURL, int inSize) {
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
                inImageIndex = previousImage(inHasFotos, inImageIndex, inImageLabel, inSize);
            }
            else {
                inImageLabel.setIcon(Utils.getScaledIcon(new ImageIcon(inDefaultImageURL), inSize));
            }
        }
        return inImageIndex;
    }

    public static void setupFoto(HasFotos inHasFotos, int inImageIndex, JLabel inImageLabel, int inSize) {
        inImageLabel.setIcon(getScaledIcon(new ImageIcon(inHasFotos.getFotos().get(inImageIndex).getFileLocation()), inSize));
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

    public static void openImage(URL inURL) {
        if (inURL != null) {
            if (System.getProperty("os.name").equals("Windows XP")) {
                try {
                    URI uri = new URI(inURL.getPath());
                    String[] commands = {"cmd", "/c", "start", "\"DoNothing\"", uri.getPath().substring(3)};
                    Runtime.getRuntime().exec(commands);
                } catch (URISyntaxException ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
