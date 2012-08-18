package wildlog.ui.panel.bulkupload.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;
import wildlog.WildLogApp;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageListWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadSightingWrapper;
import wildlog.utils.ui.Utils;


public class BulkUploadDataLoader {

    public static Object[][] genenrateTableData(File inFolderPath, boolean inIsRecuresive) {
        List<File> files = getListOfFilesToImport(inFolderPath, inIsRecuresive);

        // TODO read all of the file info at this stage: EXIF data and make the thumbnail in memory
        List<BulkUploadImageFileWrapper> imageList = new ArrayList<BulkUploadImageFileWrapper>();
        // First load all the images and sort them according to date
        for (File file : files) {
            Date date = Utils.getExifDateFromJpeg(file);
            imageList.add(
                    new BulkUploadImageFileWrapper(file, Utils.getScaledIcon(new ImageIcon(file.getAbsolutePath()), 200), date, true));
        }
        Collections.sort(files);

        // Next calculate the sightings and build the Object[][]
        Map<BulkUploadSightingWrapper, BulkUploadImageListWrapper> finalMap =
                new LinkedHashMap<BulkUploadSightingWrapper, BulkUploadImageListWrapper>(imageList.size());
        if (imageList.size() > 0) {
            Date currentSightingDate = new Date(imageList.get(0).getDate().getTime() - 120000*2);
            BulkUploadSightingWrapper sightingKey = null;
            for (BulkUploadImageFileWrapper temp : imageList) {
                if (!temp.isInSameSighting(currentSightingDate, 120000)) {
                    // Start a new sighting and image list for the linked images
                    sightingKey = new BulkUploadSightingWrapper(
                            Utils.getScaledIcon(new ImageIcon(WildLogApp.class.getResource("resources/images/NoImage.gif")), 150));
                    // Update the map
                    finalMap.put(sightingKey, new BulkUploadImageListWrapper());
                    // Update the curent date to compare against
                    currentSightingDate = temp.getDate();
                }
                // Add the image to the sighting
                finalMap.get(sightingKey).getImageList().add(temp);
            }
        }

        // Return the results
        return getArrayFromHash(finalMap);
    }

    private static Object[][] getArrayFromHash(Map<BulkUploadSightingWrapper, BulkUploadImageListWrapper> inData){
        Object[][] temp = null; {
            Object[] keys = inData.keySet().toArray();
            Object[] values = inData.values().toArray();
            temp = new Object[keys.length][values.length];
            for(int i=0; i < keys.length; i++) {
                temp[i][0] = keys[i];
                temp[i][1] = values[i];
            }
        }
        return temp;
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
