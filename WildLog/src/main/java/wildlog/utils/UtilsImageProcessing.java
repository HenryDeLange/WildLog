package wildlog.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
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
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.wrappers.WildLogSystemFile;
import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;


public class UtilsImageProcessing {
    private static final DateTimeFormatter EXIF_DATE_FORMAT_1 = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
    private static final DateTimeFormatter EXIF_DATE_FORMAT_2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private UtilsImageProcessing() {
    }

    public static ImageIcon getScaledIcon(Path inAbsolutePathToScale, int inSize, boolean inDoAutoRotate) {
        return getScaledIcon(inAbsolutePathToScale, inSize, inDoAutoRotate, null);
    }

    public static ImageIcon getScaledIcon(Path inAbsolutePathToScale, int inSize, boolean inDoAutoRotate, Metadata inMetadata) {
        // Get the size to scale the image to
        int finalHeight = inSize;
        int finalWidth = inSize;
        ImageReader imageReader = null;
        FileImageInputStream inputStream = null;
        try {
            inputStream = new FileImageInputStream(inAbsolutePathToScale.toFile());
            Iterator<ImageReader> imageReaderList = ImageIO.getImageReaders(inputStream);
            imageReader = imageReaderList.next();
            imageReader.setInput(inputStream);
            int imageWidth = imageReader.getWidth(imageReader.getMinIndex());
            int imageHeight = imageReader.getHeight(imageReader.getMinIndex());
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
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
            }
        }
        try {
            // Mens kan een van die values negatief hou dan sal hy self die image kleiner maak en die aspect ratio hou,
            // maar ek sal in elk geval moet uitwerk of dit landscape of portriate is, so vir eers hou ek maar die kode soos hierbo.
            Image image = Toolkit.getDefaultToolkit().createImage(inAbsolutePathToScale.toAbsolutePath().normalize().toString());
            Image scaledImage = getScaledImage(image, finalWidth, finalHeight);
            // Rotate the image
            if (inDoAutoRotate) {
                try {
                    Metadata metadata;
                    if (inMetadata != null) {
                        metadata = inMetadata;
                    }
                    else {
                        metadata = ImageMetadataReader.readMetadata(inAbsolutePathToScale.toFile());
                    }
                    Collection<ExifIFD0Directory> directories = metadata.getDirectoriesOfType(ExifIFD0Directory.class);
                    int orientation = 0;
                    if (directories != null) {
                        for (ExifIFD0Directory directory : directories) {
                            if (directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
                            }
                        }
                    }
                    if (orientation > 1) {
                        AffineTransform transform = new AffineTransform();
                        switch (orientation) {
                            case 1:
                                // No rotation
                                break;
                            case 2: 
                                // Flip H
                                transform.scale(-1.0, 1.0);
                                transform.translate(-1 * finalWidth, 0);
                                break;
                            case 3: 
                                // Rotate 180
                                transform.translate(finalWidth, finalHeight);
                                transform.rotate(Math.PI);
                                break;
                            case 4: 
                                // flip V
                                transform.scale(1.0, -1.0);
                                transform.translate(0, -1 * finalHeight);
                                break;
                            case 5: 
                                // Transpose
                                transform.rotate(-Math.PI / 2);
                                transform.scale(-1.0, 1.0);
                                break;
                            case 6: 
                                // Rotate 90
                                transform.rotate(Math.PI / 2, finalWidth / 2, finalHeight / 2);
                                double offset = (finalWidth - finalHeight) / 2;
                                transform.translate(offset, offset);
                                break;
                            case 7: 
                                // Transverse
                                transform.scale(-1.0, 1.0);
                                transform.translate(-1 * finalHeight, 0);
                                transform.translate(0, finalWidth);
                                transform.rotate(3 * Math.PI / 2);
                                break;
                            case 8: 
                                // Rotate 270
                                transform.translate(0, finalWidth);
                                transform.rotate(3 * Math.PI / 2);
                                break;
                        }
                        // Get BufferedImage of scaled image
                        BufferedImage bufferedImage = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_RGB);
                        Graphics2D bufferedImageG2D = bufferedImage.createGraphics();
                        // Maak 'n ImageIcon sodat die scaledImage actually klaar gelees word (nie net blank bly nie)
                        new ImageIcon(scaledImage).paintIcon(null, bufferedImageG2D, 0, 0);
                        // Rotate the thumbnail
                        AffineTransformOp transformOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
                        BufferedImage rotatedImage = transformOp.createCompatibleDestImage(bufferedImage, bufferedImage.getColorModel());
//                        rotatedImage.getWidth() rotatedImage.getHeight()
                        rotatedImage = transformOp.filter(bufferedImage, rotatedImage);
                        return new ImageIcon(rotatedImage);
                    }
                }
                catch (MetadataException | ImageProcessingException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
            }
            // Return final ImageIcon
            return new ImageIcon(scaledImage);
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            WildLogThumbnailSizes thumbnailSize = WildLogThumbnailSizes.NORMAL;
            for (WildLogThumbnailSizes size : WildLogThumbnailSizes.values()) {
                if (inSize == size.getSize()) {
                    thumbnailSize = size;
                    break;
                }
            }
            return getScaledIconForBrokenFiles(thumbnailSize);
        }
    }

    private static Image getScaledImage(Image inImage, int inWidth, int inHeight) {
        return inImage.getScaledInstance(inWidth, inHeight, Image.SCALE_SMOOTH);
    }

    public static ImageIcon getScaledIconForBrokenFiles(WildLogThumbnailSizes inSize) {
        return getScaledIconForPlaceholder(WildLogSystemImages.BROKEN_FILES.getWildLogFile(), inSize);
    }
    
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

    public static int previousImage(long inLinkID, int inImageIndex, JLabel inImageLabel, WildLogThumbnailSizes inSize, WildLogApp inApp) {
        int newImageIndex = inImageIndex;
        int fotoCount = inApp.getDBI().countWildLogFiles(0, inLinkID);
        if (fotoCount > 0) {
            if (newImageIndex > 0) {
                newImageIndex = newImageIndex - 1;
            }
            else {
                if (inApp.getWildLogOptions().isEnableSounds()) {
                    Toolkit.getDefaultToolkit().beep();
                }
                newImageIndex = fotoCount - 1;
            }
            setupFoto(inLinkID, newImageIndex, inImageLabel, inSize, inApp);
        }
        return newImageIndex;
    }

    public static int nextImage(long inLinkID, int inImageIndex, JLabel inImageLabel, WildLogThumbnailSizes inSize, WildLogApp inApp) {
        int newImageIndex = inImageIndex;
        int fotoCount = inApp.getDBI().countWildLogFiles(0, inLinkID);
        if (fotoCount > 0) {
            if (newImageIndex < fotoCount - 1) {
                newImageIndex = newImageIndex + 1;
            }
            else {
                if (inApp.getWildLogOptions().isEnableSounds()) {
                    Toolkit.getDefaultToolkit().beep();
                }
                newImageIndex = 0;
            }
            setupFoto(inLinkID, newImageIndex, inImageLabel, inSize, inApp);
        }
        return newImageIndex;
    }

    public static int setMainImage(long inLinkID, int inImageIndex, WildLogApp inApp) {
        int newImageIndex = inImageIndex;
        List<WildLogFile> lstFiles = inApp.getDBI().listWildLogFiles(inLinkID, null, WildLogFile.class);
        for (int t = 0; t < lstFiles.size(); t++) {
            if (t != newImageIndex) {
                lstFiles.get(t).setDefaultFile(false);
            }
            else {
                lstFiles.get(t).setDefaultFile(true);
            }
            inApp.getDBI().updateWildLogFile(lstFiles.get(t), false);
            if (inApp.getWildLogOptions().isEnableSounds()) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
        newImageIndex = 0;
        return newImageIndex;
    }

    public static int removeImage(long inLinkID, int inImageIndex, JLabel inImageLabel, WildLogThumbnailSizes inSize, WildLogApp inApp) {
        int newImageIndex = inImageIndex;
        if (inImageLabel != null) {
            // Flush the old image data - this should help to render newly uploaded images with the same name as the deleted image correctly
            if (inImageLabel.getIcon() instanceof ImageIcon) {
                ((ImageIcon) inImageLabel.getIcon()).getImage().flush();
            }
            List<WildLogFile> fotos = inApp.getDBI().listWildLogFiles(inLinkID, null, WildLogFile.class);
            if (fotos.size() > 0) {
                WildLogFile tempFoto = fotos.get(newImageIndex);
                inApp.getDBI().deleteWildLogFile(tempFoto.getID());
                if (fotos.size() > 1) {
                    newImageIndex = newImageIndex - 1;
                    newImageIndex = nextImage(inLinkID, newImageIndex, inImageLabel, inSize, inApp);
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

    public static void setupFoto(long inLinkID, int inImageIndex, JLabel inImageLabel, WildLogThumbnailSizes inSize, WildLogApp inApp) {
        if (inImageLabel != null) {
            List<WildLogFile> files = inApp.getDBI().listWildLogFiles(inLinkID, null, WildLogFile.class);
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
            WildLogApp.LOGGER.log(Level.ERROR, "Error showing EXIF data for: {}", inPath);
            WildLogApp.LOGGER.log(Level.ERROR, "The file extention might be wrong...");
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                WildLogApp.LOGGER.log(Level.ERROR, "Error reading EXIF data for: {}", inPath);
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
        // NOTE: Die ou Moultrie images het 'n issue waar hulle altyd 'n EXIF datum het van "01 Jan 2000 00:00:01" (die Last Modified datum werk nie as 'n alternatief nie)
        if (inMeta != null) {
            Iterator<Directory> directories = inMeta.getDirectories().iterator();
            while (directories.hasNext()) {
                Directory directory = directories.next();
                Collection<Tag> tags = directory.getTags();
                for (Tag tag : tags) {
                    try {
                        if (tag.getTagName().equalsIgnoreCase("Date/Time") || tag.getTagName().equalsIgnoreCase("Date/Time Original")) {
                            // Not all files store the date in the same format, so I have to try a few known formats...
                            // Try 1:
                            try {
                                // This seems to be by far the most used format
                                LocalDateTime localDateTime = LocalDateTime.parse(tag.getDescription().trim(), EXIF_DATE_FORMAT_1);
                                return UtilsTime.getDateFromLocalDateTime(localDateTime);
                            }
                            catch (DateTimeParseException ex) {
                                WildLogApp.LOGGER.log(Level.INFO, "Try1: [THIS DATE ({}) COULD NOT BE PARSED USING ''yyyy:MM:dd HH:mm:ss'']", tag.getDescription());
                                WildLogApp.LOGGER.log(Level.INFO, ex.toString(), ex);
                            }
                            // Try 2:
                            try {
                                // Wierd format used by Samsung Galaxy Gio (Android)
                                LocalDateTime localDateTime = LocalDateTime.parse(tag.getDescription().trim(), EXIF_DATE_FORMAT_2);
                                return UtilsTime.getDateFromLocalDateTime(localDateTime);
                            }
                            catch (DateTimeParseException ex) {
                                WildLogApp.LOGGER.log(Level.INFO, "Try2: [THIS DATE ({}) COULD NOT BE PARSED USING ''yyyy-MM-dd HH:mm:ss '']", tag.getDescription());
                                WildLogApp.LOGGER.log(Level.INFO, ex.toString(), ex);
                            }
                        }
                    }
                    catch (NumberFormatException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, "Could not parse Date info from image EXIF data: {} = {}", new Object[]{tag.getTagName(), tag.getDescription()});
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
                        // Die accuracy word ongelukkig nie gestoor in meeste EXIF tags nie...
                        // Sien bv.: https://gis.stackexchange.com/questions/22223/does-a-geotagged-image-contain-information-about-its-accuracy
                        tempDataObjectWithGPS.setGPSAccuracy(GPSAccuracy.GOOD);
                        tempDataObjectWithGPS.setGPSAccuracyValue(GPSAccuracy.GOOD.getMaxMeters());
                    }
                    catch (NumberFormatException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, "Could not parse GPS info from image EXIF data: {} = {}", new Object[]{tag.getTagName(), tag.getDescription()});
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
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
            WildLogApp.LOGGER.log(Level.ERROR, "Error reading EXIF data for non-JPG file: {}", inPath);
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "Error reading GPS EXIF data for: {}", inPath);
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return null;
    }

    /**
     * Creates a thumbnail for the provided original absolute path at the give absolute path.
     * @param inThumbnailAbsolutePath
     * @param inOriginalAbsolutePath
     * @param inSize
     */
    private static void createThumbnailOnDisk(Path inThumbnailAbsolutePath, Path inOriginalAbsolutePath, WildLogThumbnailSizes inSize) {
        // Resize the file and then save the thumbnail to into WildLog's folders
        ImageIcon thumbnail = UtilsImageProcessing.getScaledIcon(inOriginalAbsolutePath, inSize.getSize(), true);
        try {
            // Make the folder
            Files.createDirectories(inThumbnailAbsolutePath.getParent());
            // Create the image to save
            BufferedImage bufferedImage = new BufferedImage(thumbnail.getIconWidth(), thumbnail.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.drawImage(thumbnail.getImage(), 0, 0, null);
            // Hardcoding all thumbnails to be JPG (even originally PNG images)
            ImageIO.write(bufferedImage, "jpg", inThumbnailAbsolutePath.toFile());
            graphics2D.dispose();
        }
        catch (IOException ex) {
            // This can generate "Access is denied" IO exceptions when multiple threads try to create the icons for the first time. 
            // I'm OK with that and don't want to add sync blocks just to handle that initial posible scenario. 
            // If it continues or gives problems with "real" files, then fix it properly...
            WildLogApp.LOGGER.log(Level.ERROR, "Current thread name was -> {}", Thread.currentThread().getName());
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
    }
    
    /**
     * Resizes the provided image to the specified size. <br>
     * <b>WARNING: This overwrites the original file.</b>
     * @param inWildLogFile
     * @param inSize
     */
    public static void resizeImage(WildLogFile inWildLogFile, int inSize) {
        // Resize the file and then save the thumbnail over the original
        ImageIcon thumbnail = UtilsImageProcessing.getScaledIcon(inWildLogFile.getAbsolutePath(), inSize, true);
        try {
            // Create the image to save
            BufferedImage bufferedImage = new BufferedImage(thumbnail.getIconWidth(), thumbnail.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.drawImage(thumbnail.getImage(), 0, 0, null);
            // Hardcoding all thumbnails to be JPG (even originally PNG images)
            ImageIO.write(bufferedImage, "jpg", inWildLogFile.getAbsolutePath().toFile());
            graphics2D.dispose();
        }
        catch (IOException ex) {
            // This can generate "Access is denied" IO exceptions when multiple threads try to create the icons for the first time. 
            // I'm OK with that and don't want to add sync blocks just to handle that initial posible scenario. 
            // If it continues or gives problems with "real" files, then fix it properly...
            WildLogApp.LOGGER.log(Level.ERROR, "Current thread name was -> {}", Thread.currentThread().getName());
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
    }

    /**
     * Calculates the absolute path to the thumbnail for this size.
     * @param inRelativePath
     * @param inSize
     * @return
     */
    public static Path calculateAbsoluteThumbnailPath(String inRelativePath, WildLogThumbnailSizes inSize) {
        Path finalPath = WildLogPaths.WILDLOG_THUMBNAILS.getAbsoluteFullPath().resolve(inRelativePath);
        // Hardcoding all thumbnails to be JPG (even originally PNG images)
        String newFilename = finalPath.getFileName().toString().substring(0, finalPath.getFileName().toString().lastIndexOf('.'))
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
        Path thumbnail = calculateAbsoluteThumbnailPath(inWildLogFile.getDBFilePath(), inSize);
        if (!Files.exists(thumbnail)) {
            createThumbnailOnDisk(thumbnail, inWildLogFile.getAbsolutePath(), inSize);
        }
        return thumbnail;
    }

}
