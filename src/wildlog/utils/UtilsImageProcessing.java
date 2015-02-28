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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import wildlog.data.dataobjects.wrappers.WildLogSystemFile;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.ui.utils.UtilsTime;


public class UtilsImageProcessing {
    private static final DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
    private static final DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private UtilsImageProcessing() {
    }

    public static ImageIcon getScaledIcon(Path inAbsolutePathToScale, int inSize) {
        ImageReader imageReader = null;
        FileImageInputStream inputStream = null;
        try {
//            long startTime = Calendar.getInstance().getTimeInMillis();
            inputStream = new FileImageInputStream(inAbsolutePathToScale.toFile());
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
                    double ratio = (double)inSize/imageWidth;
                    finalHeight = (int)(imageHeight*ratio);
                }
            }
//            System.out.println("----Size calculations took " + (Calendar.getInstance().getTimeInMillis() - startTime) + " ms                             " + inAbsolutePathToScale);
            // Mens kan een van die values negatief hou dan sal hy self die image kleiner maak en die aspect ratio hou,
            // maar ek sal in elk geval moet uitwerk of dit landscape of portriate is, so vir eers hou ek maar die kode soos hierbo.
            Image image = Toolkit.getDefaultToolkit().createImage(inAbsolutePathToScale.toAbsolutePath().normalize().toString());
//            System.out.println("---Loading took " + (Calendar.getInstance().getTimeInMillis() - startTime) + " ms                             " + inAbsolutePathToScale);
            Image img = getScaledImage(image, finalWidth, finalHeight);
//            System.out.println("**Before new ImageIcon " + (Calendar.getInstance().getTimeInMillis() - startTime) + " ms                             " + inAbsolutePathToScale);
// TODO: verander al die JLabels om eerder JavaFx se ImageView te gebruik. 'n Vinnige toets wys dat dit dalk vinniger mag wees... Die ImageIcon is baie stadig om die icon te maak vir die label, veral sekere nonstandard colour modes in jpg files.
            ImageIcon temp = new ImageIcon(img);
//            System.out.println("**After new ImageIcon " + (Calendar.getInstance().getTimeInMillis() - startTime) + " ms                             " + inAbsolutePathToScale);
//            System.out.println("--Calculating scale " + inSize + "px took " + (Calendar.getInstance().getTimeInMillis() - startTime) + " ms                             " + inAbsolutePathToScale);
            return temp;
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            if (imageReader != null) {
                imageReader.dispose();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
        return getScaledIconForNoFiles(WildLogThumbnailSizes.NORMAL);
    }

    private static Image getScaledImage(Image inImage, int inWidth, int inHeight) {
        return inImage.getScaledInstance(inWidth, inHeight, Image.SCALE_SMOOTH);
    }

//    public static ImageIcon getScaledIcon(String inPath, int inSize) {
//        return getScaledIcon(Paths.get(inPath).normalize().toAbsolutePath(), inSize);
//    }

    // Die URI's werk nie lekker met die nuwe Java 7 Path nie (NIO)
//    public static ImageIcon getScaledIcon(URL inURL, int inSize) {
//        try {
//            return getScaledIcon(Paths.get(inURL.toURI()), inSize);
//        }
//        catch (URISyntaxException ex) {
//            ex.printStackTrace(System.err);
//        }
//        return getScaledIcon(Paths.get(inURL.getPath()), inSize);
//    }

    public static ImageIcon getScaledIconForNoFiles(WildLogThumbnailSizes inSize) {
        return getScaledIconForPlaceholder(WildLogSystemImages.NO_FILES.getWildLogFile(), inSize);
    }

    public static ImageIcon getScaledIconForMovies(WildLogThumbnailSizes inSize) {
        return getScaledIconForPlaceholder(WildLogSystemImages.MOVIES.getWildLogFile(), inSize);
    }

    public static ImageIcon getScaledIconForOtherFiles(WildLogThumbnailSizes inSize) {
        return getScaledIconForPlaceholder(WildLogSystemImages.OTHER_FILES.getWildLogFile(), inSize);
    }

    private static ImageIcon getScaledIconForPlaceholder(WildLogSystemFile inWildLogSystemFile, WildLogThumbnailSizes inSize) {
        return new ImageIcon(inWildLogSystemFile.getAbsoluteThumbnailPath(inSize).toString());
    }

    public static int previousImage(String inID, int inImageIndex, JLabel inImageLabel, WildLogThumbnailSizes inSize, WildLogApp inApp) {
        int newImageIndex = inImageIndex;
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > 0) {
            if (newImageIndex > 0) {
                newImageIndex = newImageIndex - 1;
            }
            else {
                if (inApp.getWildLogOptions().isEnableSounds()) {
                    Toolkit.getDefaultToolkit().beep();
                }
                newImageIndex = fotos.size() - 1;
            }
            setupFoto(inID, newImageIndex, inImageLabel, inSize, inApp);
        }
        return newImageIndex;
    }

    public static int nextImage(String inID, int inImageIndex, JLabel inImageLabel, WildLogThumbnailSizes inSize, WildLogApp inApp) {
        int newImageIndex = inImageIndex;
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > 0) {
            if (newImageIndex < fotos.size() - 1) {
                newImageIndex = newImageIndex + 1;
            }
            else {
                if (inApp.getWildLogOptions().isEnableSounds()) {
                    Toolkit.getDefaultToolkit().beep();
                }
                newImageIndex = 0;
            }
            setupFoto(inID, newImageIndex, inImageLabel, inSize, inApp);
        }
        return newImageIndex;
    }

    public static int setMainImage(String inID, int inImageIndex, WildLogApp inApp) {
        int newImageIndex = inImageIndex;
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        for (int t = 0; t < fotos.size(); t++) {
            if (t != newImageIndex) {
                fotos.get(t).setDefaultFile(false);
            }
            else {
                fotos.get(t).setDefaultFile(true);
            }
            inApp.getDBI().createOrUpdate(fotos.get(t), true);
        }
        newImageIndex = 0;
        return newImageIndex;
    }

    public static int removeImage(String inID, int inImageIndex, JLabel inImageLabel, WildLogThumbnailSizes inSize, WildLogApp inApp) {
        int newImageIndex = inImageIndex;
        if (inImageLabel != null) {
            List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
            if (fotos.size() > 0) {
                WildLogFile tempFoto = fotos.get(newImageIndex);
                inApp.getDBI().delete(tempFoto);
                if (fotos.size() > 1) {
                    newImageIndex = newImageIndex - 1;
                    newImageIndex = nextImage(inID, newImageIndex, inImageLabel, inSize, inApp);
                }
                else {
                    inImageLabel.setIcon(getScaledIconForNoFiles(inSize));
                }
            }
            else {
                inImageLabel.setIcon(getScaledIconForNoFiles(inSize));
            }
        }
        return newImageIndex;
    }

    public static void setupFoto(String inID, int inImageIndex, JLabel inImageLabel, WildLogThumbnailSizes inSize, WildLogApp inApp) {
        if (inImageLabel != null) {
            List<WildLogFile> files = inApp.getDBI().list(new WildLogFile(inID));
            if (files.size() > inImageIndex) {
                if (files.get(inImageIndex).getFileType() != null) {
                    if (files.get(inImageIndex).getFileType().equals(WildLogFileType.IMAGE)) {
                        inImageLabel.setIcon(new ImageIcon(files.get(inImageIndex).getAbsoluteThumbnailPath(inSize).toString()));
                    }
                    else
                    if (files.get(inImageIndex).getFileType().equals(WildLogFileType.MOVIE)) {
                        inImageLabel.setIcon(getScaledIconForMovies(inSize));
                    }
                    else
                    if (files.get(inImageIndex).getFileType().equals(WildLogFileType.OTHER)) {
                        inImageLabel.setIcon(getScaledIconForOtherFiles(inSize));
                    }
                    inImageLabel.setToolTipText(files.get(inImageIndex).getFilename());
                }
                else {
                    inImageLabel.setIcon(getScaledIconForNoFiles(inSize));
                    inImageLabel.setToolTipText("");
                }
            }
            else {
                inImageLabel.setIcon(getScaledIconForNoFiles(inSize));
                inImageLabel.setToolTipText("");
            }
        }
    }

    /**
     * Convenience method for getDateFromImage(Metadata inMeta, Path inPath).
     * @param inPath
     * @return
     */
    public static Date getDateFromImage(Path inPath) {
        Metadata metadata = null;
        try {
            if (WildLogFileExtentions.Images.isJPG(inPath)) {
                metadata = JpegMetadataReader.readMetadata(inPath.toFile());
            }
        }
        catch (JpegProcessingException | IOException ex) {
            System.err.println("Error showing EXIF data for: " + inPath);
            System.err.println("The file extention might be wrong...");
            ex.printStackTrace(System.err);
        }
        return getDateFromImage(metadata, inPath);
    }

    /**
     * Try to load the date from the EXIF data, if no date could be loaded use the
     * Last Modified date instead.
     * @param inMeta
     * @param inPath
     * @return
     */
    public static Date getDateFromImage(Metadata inMeta, Path inPath) {
        Date date = getExifDateFromJpeg(inMeta);
        if (date == null) {
            try {
                date = new Date(inPath.toFile().lastModified());
            }
            catch (Exception ex) {
                System.err.println("Error reading EXIF data for: " + inPath);
                ex.printStackTrace(System.err);
            }
        }
        return date;
    }

    /**
     * This method does not treat Images differently. The Last Modified date is
     * returned for all file types.
     * @param inPath
     * @return
     */
    public static Date getDateFromFileDate(Path inPath) {
        return new Date(inPath.toFile().lastModified());
    }

    /**
     * This method will use the FileChooser's filters to determine whether a
     * file is an Image or not and will use the appropriate way to calculate the
     * date.
     * @param inPath
     * @return
     */
    public static Date getDateFromFile(Path inPath) {
        if (inPath != null) {
            Date fileDate;
            if (WildLogFileExtentions.Images.isKnownExtention(inPath)) {
                // Get the date form the image
                fileDate = getDateFromImage(inPath);
            }
            else {
                // Get the date form the file
                fileDate = getDateFromFileDate(inPath);
            }
            return fileDate;
        }
        return null;
    }

    /**
     * Looks at the WildLogFileType and chooses the most appropriate way of
     * calculating the date.
     * @param inWildLogFile
     * @return
     */
    public static Date getDateFromWildLogFile(WildLogFile inWildLogFile) {
        if (inWildLogFile != null) {
            if (WildLogFileType.IMAGE.equals(inWildLogFile.getFileType())) {
                return getDateFromImage(inWildLogFile.getAbsolutePath());
            }
            else {
                return getDateFromFileDate(inWildLogFile.getAbsolutePath());
            }
        }
        return null;
    }

    private static Date getExifDateFromJpeg(Metadata inMeta) {
// FIXME: Die ou Moultrie images het 'n issue waar hulle 'n EXIF value het wat altyd na dieselfde datum point, ek moet 'n reel maak wat daai files kan optel en dan die file.lastmodified gebruik...
        if (inMeta != null) {
            Iterator<Directory> directories = inMeta.getDirectories().iterator();
            while (directories.hasNext()) {
                Directory directory = directories.next();
                Collection<Tag> tags = directory.getTags();
                for (Tag tag : tags) {
                    try {
                        if (tag.getTagName().equalsIgnoreCase("Date/Time Original")) {
                            // Not all files store the date in the same format, so I have to try a few known formats...
                            // Try 1:
                            try {
                                // This seems to be by far the most used format
                                LocalDateTime localDateTime = LocalDateTime.parse(tag.getDescription().trim(), dateFormatter1);
                                return UtilsTime.getDateFromLocalDateTime(localDateTime);
                            }
                            catch (DateTimeParseException ex) {
                                System.out.println("Try1: [THIS DATE (" + tag.getDescription() + ") COULD NOT BE PARSED USING 'yyyy:MM:dd HH:mm:ss']");
                                ex.printStackTrace(System.out);
                            }
                            // Try 2:
                            try {
                                // Wierd format used by Samsung Galaxy Gio (Android)
                                LocalDateTime localDateTime = LocalDateTime.parse(tag.getDescription().trim(), dateFormatter2);
                                return UtilsTime.getDateFromLocalDateTime(localDateTime);
                            }
                            catch (DateTimeParseException ex) {
                                System.out.println("Try2: [THIS DATE (" + tag.getDescription() + ") COULD NOT BE PARSED USING 'yyyy-MM-dd HH:mm:ss ']");
                                ex.printStackTrace(System.out);
                            }
                        }
                    }
                    catch (NumberFormatException ex) {
                        System.err.println("Could not parse Date info from image EXIF data: " + tag.getTagName() + " = " + tag.getDescription());
                        ex.printStackTrace(System.err);
                    }
                }
            }
        }
        return null;
    }

    public static DataObjectWithGPS getExifGpsFromJpeg(Metadata inMeta) {
        DataObjectWithGPS tempDataObjectWithGPS = new DataObjectWithGPS() {};
        if (inMeta != null) {
            Iterator<Directory> directories = inMeta.getDirectories().iterator();
            while (directories.hasNext()) {
                Directory directory = directories.next();
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
                            if (temp != null) {
                                tempDataObjectWithGPS.setLatDegrees((int)Math.abs(Double.parseDouble(temp.substring(0, temp.indexOf('°')).trim())));
                                tempDataObjectWithGPS.setLatMinutes((int)Math.abs(Double.parseDouble(temp.substring(temp.indexOf('°')+1, temp.indexOf('\'')).trim())));
                                tempDataObjectWithGPS.setLatSeconds(Math.abs(Double.parseDouble(temp.substring(temp.indexOf('\'')+1, temp.indexOf('"')).trim())));
                            }
                        }
                        else
                        if (tag.getTagName().equalsIgnoreCase("GPS Longitude")) {
                            // Voorbeeld 26°28'7.0"
                            String temp = tag.getDescription();
                            if (temp != null) {
                                tempDataObjectWithGPS.setLonDegrees((int)Math.abs(Double.parseDouble(temp.substring(0, temp.indexOf('°')).trim())));
                                tempDataObjectWithGPS.setLonMinutes((int)Math.abs(Double.parseDouble(temp.substring(temp.indexOf('°')+1, temp.indexOf('\'')).trim())));
                                tempDataObjectWithGPS.setLonSeconds(Math.abs(Double.parseDouble(temp.substring(temp.indexOf('\'')+1, temp.indexOf('"')).trim())));
                            }
                        }
                        tempDataObjectWithGPS.setGPSAccuracy(GPSAccuracy.GOOD);
                    }
                    catch (NumberFormatException ex) {
                        System.err.println("Could not parse GPS info from image EXIF data: " + tag.getTagName() + " = " + tag.getDescription());
                        ex.printStackTrace(System.err);
                    }
                }
            }
        }
        return tempDataObjectWithGPS;
     }

    public static DataObjectWithGPS getExifGpsFromJpeg(Path inPath) {
        try {
            return getExifGpsFromJpeg(JpegMetadataReader.readMetadata(inPath.toFile()));
        }
        catch (JpegProcessingException ex) {
            System.err.println("Error reading EXIF data for non-JPG file: " + inPath);
            ex.printStackTrace(System.err);
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        catch (Exception ex) {
            System.err.println("Error reading GPS EXIF data for: " + inPath);
            ex.printStackTrace(System.err);
        }
        return null;
    }

    /**
     * Creates a thumbnail for the provided original absolute path at the give absolute path.
     * @param inThumbnailAbsolutePath
     * @param inOriginalAbsolutePath
     * @param inSize
     */
    public static void createThumbnailOnDisk(Path inThumbnailAbsolutePath, Path inOriginalAbsolutePath, WildLogThumbnailSizes inSize) {
        // Resize the file and then save the thumbnail to into WildLog's folders
// TODO: Soek 'n beter manier om die image te save wat nie dependant is op die ImageIcon nie...
        ImageIcon thumbnail = UtilsImageProcessing.getScaledIcon(inOriginalAbsolutePath, inSize.getSize());
        try {
            // Make the folder
            if (!Files.exists(inThumbnailAbsolutePath, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectories(inThumbnailAbsolutePath);
            }
            // Create the image to save
            BufferedImage bufferedImage = new BufferedImage(thumbnail.getIconWidth(), thumbnail.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.drawImage(thumbnail.getImage(), 0, 0, null);
            // Hardcoding all thumbnails to be JPG (even originally PNG images)
            ImageIO.write(bufferedImage, "jpg", inThumbnailAbsolutePath.toFile());
            graphics2D.dispose();
        }
        catch (IOException ex) {
            // FIXME: This can generate "Access is denied" IO exceptions when multiple threads try to create the icons for the first time. 
            //        I'm OK with that and don't want to add sync blocks just to hadle that initial posible scenario. 
            //        If it continues or gives problems with "real" files, then fix it properly...
            ex.printStackTrace(System.err);
        }
    }

    /**
     * Calculates the absolute path to the thumbnail for this size.
     * @param inWildLogFile
     * @param inSize
     * @return
     */
    public static Path calculateAbsoluteThumbnailPath(WildLogFile inWildLogFile, WildLogThumbnailSizes inSize) {
        Path finalPath = WildLogPaths.WILDLOG_THUMBNAILS.getAbsoluteFullPath()
                .resolve(inWildLogFile.getRelativePath());
        // Hardcoding all thumbnails to be JPG (even originally PNG images)
        String newFilename = finalPath.getFileName().toString()
                .substring(0, finalPath.getFileName().toString().lastIndexOf('.'))
                + "_" + inSize.getSize() + "px.jpg";
        return finalPath.resolveSibling(newFilename)/*.normalize()*/;
    }


    /**
     * This method will create the thumbnail if it doesn't already exist and return
     * the absolute path that points to thumbnail.
     * @param inWildLogFile
     * @param inSize
     * @return
     */
    public static Path getAbsoluteThumbnailPathAndCreate(WildLogFile inWildLogFile, WildLogThumbnailSizes inSize) {
        Path thumbnail = calculateAbsoluteThumbnailPath(inWildLogFile, inSize);
        if (!Files.exists(thumbnail)) {
            createThumbnailOnDisk(thumbnail, inWildLogFile.getAbsolutePath(), inSize);
        }
        return thumbnail;
    }

}
