package wildlog.ui.panels.bulkupload.helpers;

import java.nio.file.Path;
import java.util.Date;
import javax.swing.Icon;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.ui.panels.bulkupload.ImageBox;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogFileExtentions;
import wildlog.utils.WildLogSystemImages;


public class BulkUploadImageFileWrapper implements Comparable<BulkUploadImageFileWrapper> {
    private Path file;
    private Icon icon;
    private int size;
    private final Date date;
    private final DataObjectWithGPS dataObjectWithGPS;
    

    public BulkUploadImageFileWrapper(Path inFile, Icon inIcon, int inSize, Date inDate, DataObjectWithGPS inDataObjectWithGPS) {
        file = inFile;
        icon = inIcon;
        size = inSize;
        date = inDate;
        dataObjectWithGPS = inDataObjectWithGPS;
    }

    public Icon getIcon() {
        // When loading from a saved bulk import, then the icon will initially be null and needs to be reloaded
        if (icon == null) {
            if (WildLogFileExtentions.Images.isKnownExtention(file)) {
                icon = UtilsImageProcessing.getScaledIcon(file, size - ImageBox.BUTTON_AND_PADDING_BUFFER, true);
            }
            else 
            if (WildLogFileExtentions.Movies.isKnownExtention(file)) {
                icon = UtilsImageProcessing.getScaledIcon(
                        WildLogSystemImages.MOVIES.getWildLogFile().getAbsolutePath(), size - ImageBox.BUTTON_AND_PADDING_BUFFER, false);
            }
            else {
                icon = UtilsImageProcessing.getScaledIcon(
                        WildLogSystemImages.OTHER_FILES.getWildLogFile().getAbsolutePath(), size - ImageBox.BUTTON_AND_PADDING_BUFFER, false);
            }
        }
        return icon;
    }

    public Date getDate() {
        return date;
    }
    
    public DataObjectWithGPS getDataObjectWithGPS() {
        return dataObjectWithGPS;
    }

    public void setFile(Path inFile) {
        file = inFile;
    }
    
    public Path getFile() {
        return file;
    }

    public void setIcon(Icon inIcon) {
        icon = inIcon;
    }

    public void setSize(int inSize) {
        size = inSize;
    }

    public int getSize() {
        return size;
    }

    @Override
    public int compareTo(BulkUploadImageFileWrapper inObject) {
        if (date != null && inObject != null && inObject.getDate() != null) {
            int result = date.compareTo(inObject.getDate());
            if (result == 0 ) {
                try {
                    result = file.getFileName().toString().compareTo(inObject.getFile().getFileName().toString());
                }
                catch (Exception ex) {
                    WildLogApp.LOGGER.log(Level.INFO, ex.toString(), ex);
                }
            }
            return result;
        }
        else {
            return 0;
        }
    }

    public boolean isInSameSighting(Date inDate, long inWindowPeriodInMiliseconds) {
        if (inDate == null) {
            return false;
        }
        else {
            return date.getTime() <= (inDate.getTime() + inWindowPeriodInMiliseconds);
        }
    }

    public BulkUploadImageFileWrapper getClone() {
        BulkUploadImageFileWrapper imageFileWrapper = new BulkUploadImageFileWrapper(file, icon, size, date, dataObjectWithGPS);
        return imageFileWrapper;
    }

}
