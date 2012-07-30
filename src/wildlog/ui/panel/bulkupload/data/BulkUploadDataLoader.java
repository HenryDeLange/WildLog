package wildlog.ui.panel.bulkupload.data;

import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadSightingWrapper;


public class BulkUploadDataLoader {

    public static Object[][] genenrateTableData(String inFolderPath) {
        return new Object[][] {
            {new BulkUploadSightingWrapper("C:\\_Camera Trap\\utopia\\DSCN3106.JPG"), new BulkUploadImageFileWrapper("C:\\_Camera Trap\\utopia\\DSCN3229.JPG", true)},
            {new BulkUploadSightingWrapper("C:\\_Camera Trap\\utopia\\DSCN3107.JPG"), new BulkUploadImageFileWrapper("C:\\_Camera Trap\\utopia\\DSCN3345.JPG", true)},
            {new BulkUploadSightingWrapper("C:\\_Camera Trap\\utopia\\DSCN3108.JPG"), new BulkUploadImageFileWrapper("C:\\_Camera Trap\\utopia\\DSCN3346.JPG", true)},
            {new BulkUploadSightingWrapper("C:\\_Camera Trap\\utopia\\DSCN3109.JPG"), new BulkUploadImageFileWrapper("C:\\_Camera Trap\\utopia\\DSCN3348.JPG", true)}
        };
    }

    public static String[] getColumnNames() {
        return new String[] {
            "Sightings", "Images"
        };
    }

}
