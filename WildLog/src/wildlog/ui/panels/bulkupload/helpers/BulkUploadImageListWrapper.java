package wildlog.ui.panels.bulkupload.helpers;

import java.util.ArrayList;
import java.util.List;

public class BulkUploadImageListWrapper {
    private List<BulkUploadImageFileWrapper> imageList;

    public BulkUploadImageListWrapper() {
        imageList = new ArrayList<BulkUploadImageFileWrapper>();
    }

    public List<BulkUploadImageFileWrapper> getImageList() {
        return imageList;
    }

    public void setImageList(List<BulkUploadImageFileWrapper> inImageList) {
        imageList = inImageList;
    }

}
