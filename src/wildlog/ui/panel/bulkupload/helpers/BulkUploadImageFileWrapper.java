package wildlog.ui.panel.bulkupload.helpers;

import java.io.File;
import java.util.Date;
import javax.swing.Icon;


public class BulkUploadImageFileWrapper implements Comparable<BulkUploadImageFileWrapper> {
    private Icon icon;
    private File file;
    private Date date;
    // FIXME: Is this property really needed?
    private boolean includeInImport = true;


    public BulkUploadImageFileWrapper(File inFile, Icon inIcon, Date inDate, boolean inIncludeInImport) {
        file = inFile;
        icon = inIcon;
        date = inDate;
        includeInImport = inIncludeInImport;
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

    public boolean isIncludeInImport() {
        return includeInImport;
    }

    public void setIncludeInImport(boolean inIncludeInImport) {
        includeInImport = inIncludeInImport;
    }

    @Override
    public int compareTo(BulkUploadImageFileWrapper inObject) {
        if (date != null && inObject != null && inObject.getDate() != null) {
            return date.compareTo(inObject.getDate());
        }
        else
            return 0;
    }

    public boolean isInSameSighting(Date inDate, int inWindowPeriodInMiliseconds) {
        if (inDate == null)
            return false;
        else
            return date.getTime() < (inDate.getTime() + inWindowPeriodInMiliseconds);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File inFile) {
        file = inFile;
    }

    public BulkUploadImageFileWrapper getClone() {
        return new BulkUploadImageFileWrapper(file, icon, date, includeInImport);
    }

}
