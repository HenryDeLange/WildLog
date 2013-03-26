package wildlog.utils;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
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
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.WildLogFileType;


public class UtilsImageProcessing {
    // Thumbnail sizes
    /** 100px */
    public static final int THUMBNAIL_SIZE_SMALL = 100;
    /** 200px */
    public static final int THUMBNAIL_SIZE_MEDIUM_SMALL = 200;
    /** 300px */
    public static final int THUMBNAIL_SIZE_MEDIUM = 300;
    /** 500px */
    public static final int THUMBNAIL_SIZE_LARGE = 500;
    /** 850px */
    public static final int THUMBNAIL_SIZE_EXTRA_LARGE = 850;


    public static ImageIcon getScaledIcon(File inFile, int inSize) {
        ImageReader imageReader = null;
        try (FileImageInputStream inputStream = new FileImageInputStream(inFile)) {
            Iterator<ImageReader> imageReaderList = ImageIO.getImageReaders(inputStream);
            imageReader = imageReaderList.next();
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
            // FIXME: Mens kan een van die values negatief hou dan sal hy self die image kleiner maak en die aspect ratio hou, so ek hoef dit nie dan self uit te werk nie...
            return new ImageIcon(getScaledImage(Toolkit.getDefaultToolkit().createImage(inFile.getAbsolutePath()), finalWidth, finalHeight));
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            if (imageReader != null)
                imageReader.dispose();
        }
        return getScaledIconForNoImage(inSize);
    }

    public static ImageIcon getScaledIcon(String inPath, int inSize) {
        return getScaledIcon(new File(inPath), inSize);
    }

    public static ImageIcon getScaledIcon(URL inURL, int inSize) {
        try {
            return getScaledIcon(new File(inURL.toURI()), inSize);
        }
        catch (URISyntaxException ex) {
            ex.printStackTrace(System.err);
        }
        return getScaledIcon(inURL.getPath(), inSize);
    }

    public static ImageIcon getScaledIconForNoImage(int inSize) {
        File tempFile = new File(WildLogPaths.concatPaths(true, WildLogPaths.WILDLOG_THUMBNAILS_IMAGES.getFullPath(), "System", "NoFile.png"));
        if (!tempFile.exists()) {
            tempFile.getParentFile().mkdirs();
            InputStream templateStream = WildLogApp.class.getResourceAsStream("resources/icons/NoFile.png");
            UtilsFileProcessing.copyFile(templateStream, tempFile);
        }
        return getScaledIcon(tempFile, inSize);
    }

    public static ImageIcon getScaledIconForMovies(int inSize) {
        File tempFile = new File(WildLogPaths.concatPaths(true, WildLogPaths.WILDLOG_THUMBNAILS_IMAGES.getFullPath(), "System", "Movie.png"));
        if (!tempFile.exists()) {
            tempFile.getParentFile().mkdirs();
            InputStream templateStream = WildLogApp.class.getResourceAsStream("resources/icons/Movie.png");
            UtilsFileProcessing.copyFile(templateStream, tempFile);
        }
        return getScaledIcon(tempFile, inSize);
    }

    public static ImageIcon getScaledIconForOtherFiles(int inSize) {
        File tempFile = new File(WildLogPaths.concatPaths(true, WildLogPaths.WILDLOG_THUMBNAILS_IMAGES.getFullPath(), "System", "OtherFile.png"));
        if (!tempFile.exists()) {
            tempFile.getParentFile().mkdirs();
            InputStream templateStream = WildLogApp.class.getResourceAsStream("resources/icons/OtherFile.png");
            UtilsFileProcessing.copyFile(templateStream, tempFile);
        }
        return getScaledIcon(tempFile, inSize);
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
                if (fotos.get(inImageIndex).getFileType() != null) {
                    if (fotos.get(inImageIndex).getFileType().equals(WildLogFileType.IMAGE))
                        inImageLabel.setIcon(new ImageIcon(fotos.get(inImageIndex).getThumbnailPath(inSize)));
                    else
                    if (fotos.get(inImageIndex).getFileType().equals(WildLogFileType.MOVIE))
                        inImageLabel.setIcon(getScaledIconForMovies(inSize));
                    else
                    if (fotos.get(inImageIndex).getFileType().equals(WildLogFileType.OTHER))
                        inImageLabel.setIcon(getScaledIconForOtherFiles(inSize));
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

    public static Date getDateFromImage(File inFile) {
        Metadata metadata = null;
        try {
            metadata = JpegMetadataReader.readMetadata(inFile);
        }
        catch (JpegProcessingException | IOException ex) {
            ex.printStackTrace(System.err);
        }
        return getDateFromImage(metadata, inFile);
    }

    public static Date getDateFromImage(Metadata inMeta, File inFile) {
        Date date = UtilsImageProcessing.getExifDateFromJpeg(inMeta);
        if (date == null) {
            try {
                date = new Date(inFile.lastModified());
            }
            catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
        return date;
    }

    private static Date getExifDateFromJpeg(Metadata inMeta) {
        // FIXME: Die ou Moultrie images het 'n issue waar hulle 'n EXIF value het wat altyd na dieselfde datum point, ek moet 'n reel maak wat daai files kan optel en dan die file.lastmodified gebruik...
        if (inMeta != null) {
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
        }
        return null;
    }

    public static DataObjectWithGPS getExifGpsFromJpeg(Metadata inMeta) {
        DataObjectWithGPS tempDataObjectWithGPS = new DataObjectWithGPS() {};
        Iterator<Directory> directories = inMeta.getDirectories().iterator();
        while (directories.hasNext()) {
            Directory directory = (Directory)directories.next();
            Collection<Tag> tags = directory.getTags();
            for (Tag tag : tags) {
                try {
                    if (tag.getTagName().equalsIgnoreCase("GPS Latitude Ref")) {
                        // Voorbeeld S
                        tempDataObjectWithGPS.setLatitude(Latitudes.getEnumFromText(tag.getDescription()));
                    }
                    else
                    if (tag.getTagName().equalsIgnoreCase("GPS Longitude Ref")) {
                        // Voorbeeld E
                        tempDataObjectWithGPS.setLongitude(Longitudes.getEnumFromText(tag.getDescription()));
                    }
                    else
                    if (tag.getTagName().equalsIgnoreCase("GPS Latitude")) {
                        // Voorbeeld -33°44'57.0"
                        String temp = tag.getDescription();
                        tempDataObjectWithGPS.setLatDegrees((int)Math.abs(Double.parseDouble(temp.substring(0, temp.indexOf("°")).trim())));
                        tempDataObjectWithGPS.setLatMinutes((int)Math.abs(Double.parseDouble(temp.substring(temp.indexOf("°")+1, temp.indexOf("'")).trim())));
                        tempDataObjectWithGPS.setLatSeconds(Math.abs(Double.parseDouble(temp.substring(temp.indexOf("'")+1, temp.indexOf("\"")).trim())));
                    }
                    else
                    if (tag.getTagName().equalsIgnoreCase("GPS Longitude")) {
                        // Voorbeeld 26°28'7.0"
                        String temp = tag.getDescription();
                        tempDataObjectWithGPS.setLonDegrees((int)Math.abs(Double.parseDouble(temp.substring(0, temp.indexOf("°")).trim())));
                        tempDataObjectWithGPS.setLonMinutes((int)Math.abs(Double.parseDouble(temp.substring(temp.indexOf("°")+1, temp.indexOf("'")).trim())));
                        tempDataObjectWithGPS.setLonSeconds(Math.abs(Double.parseDouble(temp.substring(temp.indexOf("'")+1, temp.indexOf("\"")).trim())));
                    }
                }
                catch (NumberFormatException ex) {
                    System.err.println("Could not parse GPS info from image EXIF data: " + tag.getTagName() + " = " + tag.getDescription());
                }
            }
        }
        return tempDataObjectWithGPS;
     }

    public static DataObjectWithGPS getExifGpsFromJpeg(File inFile) {
        try {
            return getExifGpsFromJpeg(JpegMetadataReader.readMetadata(inFile));
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        catch (JpegProcessingException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    private static void createThumbnailOnDisk(File inOriginalFile, int inSize) {
        // Get the thumbnail name
        File toFileThumbnail = new File(getThumbnailPath(inOriginalFile, inSize));
        // Make the folder
        toFileThumbnail.getParentFile().mkdirs();
        // Resize the file and then save the thumbnail to into WildLog's folders
        ImageIcon thumbnail = UtilsImageProcessing.getScaledIcon(inOriginalFile, inSize);
        try {
            BufferedImage bufferedImage = new BufferedImage(thumbnail.getIconWidth(), thumbnail.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.drawImage(thumbnail.getImage(), 0, 0, null);
            ImageIO.write(bufferedImage, "jpg", toFileThumbnail);
            graphics2D.dispose();
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private static String getThumbnailPath(File inOriginalFile, int inSize) {
        File thumbnail = new File(WildLogPaths.concatPaths(true, 
                WildLogPaths.WILDLOG_THUMBNAILS_IMAGES.getFullPath(),
                WildLogPaths.stripRootFromPath(
                    inOriginalFile.getParent(),
                    WildLogPaths.WILDLOG_FILES_IMAGES.getFullPath()),
                inOriginalFile.getName()
            ));
        String path = thumbnail.getAbsolutePath();
        return path.substring(0, path.lastIndexOf('.')) + "_" + inSize + "px" + path.substring(path.lastIndexOf('.'));
    }

    public static String getThumbnail(File inOriginalFile, int inSize) {
        File thumbnail = new File(getThumbnailPath(inOriginalFile, inSize));
        if (!thumbnail.exists()) {
            createThumbnailOnDisk(inOriginalFile, inSize);
        }
        return thumbnail.getAbsolutePath();
    }

    public static String getThumbnail(String inOriginalPath, int inSize) {
        return getThumbnail(new File(inOriginalPath), inSize);
    }

}
