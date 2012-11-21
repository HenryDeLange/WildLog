package wildlog.utils;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
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
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.WildLogFileType;


public class UtilsImageProcessing {
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
        return getScaledIcon(WildLogApp.class.getResource("resources/images/NoFile.png"), inSize);
    }

    private static Image getScaledImage(Image inImage, int inWidth, int inHeight) {
        return inImage.getScaledInstance(inWidth, inHeight, Image.SCALE_DEFAULT);
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
                inImageLabel.setIcon(getScaledIconForNoImage(inSize));
            }
        }
        else {
            inImageLabel.setIcon(getScaledIconForNoImage(inSize));
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
                        inImageLabel.setIcon(getScaledIcon(inApp.getClass().getResource("resources/images/Movie.png"), inSize));
                    else
                    if (fotos.get(inImageIndex).getFotoType().equals(WildLogFileType.OTHER))
                        inImageLabel.setIcon(getScaledIcon(inApp.getClass().getResource("resources/images/OtherFile.png"), inSize));
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

}
