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

package wildlog.ui.util;

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
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import wildlog.data.dataobjects.Foto;
import wildlog.data.dataobjects.interfaces.HasFotos;

/* Utils.java is used by FileChooserDemo2.java. */
public class Utils {
    protected final static String jpeg = "jpeg";
    protected final static String jpg = "jpg";
    protected final static String gif = "gif";
    protected final static String tiff = "tiff";
    protected final static String tif = "tif";
    protected final static String png = "png";

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
                double ratio = inIcon.getImage().getHeight(null)/inSize;
                finalWidth = (int)(inIcon.getImage().getWidth(null)/ratio);
            }
            else {
                double ratio = inSize/inIcon.getImage().getHeight(null);
                finalWidth = (int)(inIcon.getImage().getWidth(null)*ratio);
            }
        }
        else {
            if (inIcon.getImage().getWidth(null) >= inSize) {
                double ratio = inIcon.getImage().getWidth(null)/inSize;
                finalHeight = (int)(inIcon.getImage().getHeight(null)/ratio);
            }
            else {
                double ratio = inSize/inIcon.getImage().getWidth(null);
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

    public static void uploadImage(HasFotos inDataObject, String inFolderName, Component inComponent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new ImageFilter());
        fileChooser.setAccessory(new ImagePreview(fileChooser));
        int result = fileChooser.showOpenDialog(inComponent);
        if ((result != JFileChooser.ERROR_OPTION) && (result == JFileChooser.APPROVE_OPTION)) {
            File fromFile = fileChooser.getSelectedFile();
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
                image = getScaledIcon(image, 300);
                BufferedImage bi = new BufferedImage(image.getIconWidth(), image.getIconHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D big = bi.createGraphics();
                big.drawImage(image.getImage(), 0, 0, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(bi, "jpg", os);
                byte[] newBytes = os.toByteArray();
                fileOutput.write(newBytes);
                fileOutput.flush();
                if (inDataObject.getFotos() == null) inDataObject.setFotos(new ArrayList<Foto>());
                inDataObject.getFotos().add(new Foto(toFile_Thumbnail.getName(), toFile_Thumbnail.getAbsolutePath(), toFile_Original.getAbsolutePath()));
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
