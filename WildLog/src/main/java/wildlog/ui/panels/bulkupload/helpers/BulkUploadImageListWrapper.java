package wildlog.ui.panels.bulkupload.helpers;

import java.util.ArrayList;
import java.util.List;

public class BulkUploadImageListWrapper {
    private List<BulkUploadImageFileWrapper> imageList;
    private int imageBoxSize;

    public BulkUploadImageListWrapper(int inImageBoxSize) {
        imageList = new ArrayList<>();
        imageBoxSize = inImageBoxSize;
    }

    public List<BulkUploadImageFileWrapper> getImageList() {
        return imageList;
    }

    public void setImageList(List<BulkUploadImageFileWrapper> inImageList) {
        imageList = inImageList;
    }

    public int getImageBoxSize() {
        return imageBoxSize;
    }

    public void setImageBoxSize(int inImageBoxSize) {
        imageBoxSize = inImageBoxSize;
    }

}
