package wildlog.ui.panels.bulkupload.helpers;

import java.nio.file.Path;
import java.util.Date;
import javax.swing.Icon;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;


public class BulkUploadImageFileWrapper implements Comparable<BulkUploadImageFileWrapper> {
    private final Icon icon;
    private final Path file;
    private final Date date;
    private final DataObjectWithGPS dataObjectWithGPS;
    

    public BulkUploadImageFileWrapper(Path inFile, Icon inIcon, Date inDate, DataObjectWithGPS inDataObjectWithGPS) {
        file = inFile;
        icon = inIcon;
        date = inDate;
        dataObjectWithGPS = inDataObjectWithGPS;
    }

    public Icon getIcon() {
        return icon;
    }

    public Date getDate() {
        return date;
    }
    
    public DataObjectWithGPS getDataObjectWithGPS() {
        return dataObjectWithGPS;
    }
    
    public Path getFile() {
        return file;
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
        BulkUploadImageFileWrapper imageFileWrapper = new BulkUploadImageFileWrapper(file, icon, date, dataObjectWithGPS);
        return imageFileWrapper;
    }

}
