package wildlog.ui.panel.bulkupload.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadSightingWrapper;


public class BulkUploadDataLoader {

    public static Object[][] genenrateTableData(File inFolderPath, boolean inIsRecuresive) {
        List<File> files = getListOfFilesToImport(inFolderPath, inIsRecuresive);

        // TODO read all of the file info at this stage: EXIF data and make the thumbnail in memory

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

    private static List<File> getListOfFilesToImport(File inRoot, boolean inIncludeFolders) {
        List<File> list = new ArrayList<File>();
        File[] tempFileList = inRoot.listFiles();
        for (File tempFile : tempFileList) {
        if (inIncludeFolders && tempFile.isDirectory()) {
            list.addAll(getListOfFilesToImport(tempFile, inIncludeFolders));
        }
        else {
            String tempName = tempFile.getName().toLowerCase();
            if (tempName.endsWith("jpg") || tempName.endsWith("jpeg"))
                list.add(tempFile);
            }
        }
        return list;
    }

}
