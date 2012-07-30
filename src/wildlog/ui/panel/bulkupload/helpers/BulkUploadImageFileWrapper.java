package wildlog.ui.panel.bulkupload.helpers;


public class BulkUploadImageFileWrapper {
    private String imagePath;
    private boolean includeInImport = true;

    public BulkUploadImageFileWrapper() {
    }

    public BulkUploadImageFileWrapper(String inImagePath, boolean inIncludeInImport) {
        imagePath = inImagePath;
        includeInImport = inIncludeInImport;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String inImagePath) {
        imagePath = inImagePath;
    }

    public boolean isIncludeInImport() {
        return includeInImport;
    }

    public void setIncludeInImport(boolean inIncludeInImport) {
        includeInImport = inIncludeInImport;
    }

}
