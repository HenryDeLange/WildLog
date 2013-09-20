package wildlog.ui.panels.bulkupload.helpers;

import java.nio.file.Path;
import java.util.Date;
import javax.swing.Icon;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.ui.panels.bulkupload.ImageBox;


public class BulkUploadImageFileWrapper implements Comparable<BulkUploadImageFileWrapper> {
    private Icon icon;
    private ImageBox imageBox;
    private Path file;
    private Date date;
    private DataObjectWithGPS dataObjectWithGPS;


    public BulkUploadImageFileWrapper(Path inFile, Icon inIcon, Date inDate) {
        this(inFile, inIcon, inDate, null);
    }

    public BulkUploadImageFileWrapper(Path inFile, Icon inIcon, Date inDate, ImageBox inImageBox) {
        file = inFile;
        icon = inIcon;
        date = inDate;
        imageBox = inImageBox;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon inIcon) {
        icon = inIcon;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date inDate) {
        date = inDate;
    }

    @Override
    public int compareTo(BulkUploadImageFileWrapper inObject) {
        if (date != null && inObject != null && inObject.getDate() != null) {
            return date.compareTo(inObject.getDate());
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

    public Path getFile() {
        return file;
    }

    public void setFile(Path inFile) {
        file = inFile;
    }

    public BulkUploadImageFileWrapper getClone() {
        BulkUploadImageFileWrapper imageFileWrapper = new BulkUploadImageFileWrapper(file, icon, date);
        imageFileWrapper.setImageBox(new ImageBox(imageFileWrapper, imageBox.getTable()));
        return imageFileWrapper;
    }

    public ImageBox getImageBox() {
        return imageBox;
    }

    public void setImageBox(ImageBox inImageBox) {
        imageBox = inImageBox;
    }

    public DataObjectWithGPS getDataObjectWithGPS() {
        return dataObjectWithGPS;
    }

    public void setDataObjectWithGPS(DataObjectWithGPS inDataObjectWithGPS) {
        dataObjectWithGPS = inDataObjectWithGPS;
    }

}
