package wildlog.ui.panels.bulkupload.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import wildlog.WildLogApp;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.SightingEvidence;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageListWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadSightingWrapper;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsImageProcessing;


public class BulkUploadDataLoader {

    public static BulkUploadDataWrapper genenrateTableData(File inFolderPath, boolean inIsRecuresive, int inSightingDurationInSeconds, final ProgressbarTask inProgressbarTask, WildLogApp inApp) {
        inProgressbarTask.setMessage("Bulk Import Preparation: Loading files...");
        final List<File> files = getListOfFilesToImport(inFolderPath, inIsRecuresive);
        // Read all of the files at this stage: EXIF data and make the thumbnail in memory
        final List<BulkUploadImageFileWrapper> imageList = new ArrayList<BulkUploadImageFileWrapper>(files.size());
        // First load all the images and sort them according to date
        ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount());
        for (int t = 0; t < files.size(); t++) {
            final File tempFile = files.get(t);
            final int counter = t;
            executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            loadFileData(tempFile, imageList);
                            try {
                                inProgressbarTask.setTaskProgress(counter, 0, files.size());
                            }
                            catch (Exception e) {
                                e.printStackTrace(System.out);
                            }
                        }
                    });
        }
        if (!UtilsConcurency.tryAndWaitToShutdownExecutorService(executorService)) {
            return null;
        }
        Collections.sort(imageList);
        inProgressbarTask.setMessage("Bulk Import Preparation: Process files...");
        inProgressbarTask.setTaskProgress(95);
        long timeDiffInMiliseconds = inSightingDurationInSeconds*1000;
        // Next calculate the sightings and build the Object[][]
        Map<BulkUploadSightingWrapper, BulkUploadImageListWrapper> finalMap =
                new LinkedHashMap<BulkUploadSightingWrapper, BulkUploadImageListWrapper>(imageList.size());
        if (imageList.size() > 0) {
            Date currentSightingDate = new Date(imageList.get(0).getDate().getTime() - timeDiffInMiliseconds*2);
            BulkUploadSightingWrapper sightingKey = null;
            for (BulkUploadImageFileWrapper temp : imageList) {
                if (!temp.isInSameSighting(currentSightingDate, timeDiffInMiliseconds)) {
                    // Start a new sighting and image list for the linked images
                    sightingKey = new BulkUploadSightingWrapper(UtilsImageProcessing.getScaledIconForNoImage(150));
                    // Set the date for this sighting
                    sightingKey.setDate(currentSightingDate);
                    // Set other defaults for the sighting
                    sightingKey.setCertainty(Certainty.SURE);
                    sightingKey.setSightingEvidence(SightingEvidence.SEEN);
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
        BulkUploadDataWrapper wrapper = new BulkUploadDataWrapper();
        if (!imageList.isEmpty()) {
            wrapper.setStartDate(imageList.get(0).getDate());
            wrapper.setEndDate(imageList.get(imageList.size()-1).getDate());
        }
        wrapper.setData(getArrayFromHash(finalMap));
        return wrapper;
    }

    private static void loadFileData(File inFile, List<BulkUploadImageFileWrapper> inImageList) {
        // Note: Ek load die file twee keer, maar dis steeds redelik vinnig, en ek kry "out of memory" issues as ek dit alles in 'n inputstream in lees en traai hergebruik...
        Date date = UtilsImageProcessing.getExifDateFromJpeg(inFile);
        inImageList.add(new BulkUploadImageFileWrapper(inFile, UtilsImageProcessing.getScaledIcon(inFile, 200), date));
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
        if (inRoot != null) {
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
        }
        return list;
    }

}