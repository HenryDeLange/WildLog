package wildlog.ui.panels.bulkupload.data;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import wildlog.WildLogApp;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageListWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadSightingWrapper;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogFileExtentions;
import wildlog.utils.WildLogSystemImages;


public class BulkUploadDataLoader {
    
    public static BulkUploadDataWrapper genenrateTableData(File inFolderPath, boolean inIsRecuresive, int inSightingDurationInSeconds, final ProgressbarTask inProgressbarTask, WildLogApp inApp) {
        inProgressbarTask.setMessage("Bulk Import Preparation: Loading files...");
        final List<File> files = getListOfFilesToImport(inFolderPath, inIsRecuresive);
        if (files.isEmpty() && !inIsRecuresive) {
            int result = UtilsDialog.showDialogBackgroundWrapper(WildLogApp.getApplication().getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    return JOptionPane.showConfirmDialog(WildLogApp.getApplication().getMainFrame(), 
                            "No supported files were found in the specified folder. Would you like to search in the subfolders as well?", 
                            "Include subfolders?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                }
            });
            if (result == JOptionPane.YES_OPTION) {
                inIsRecuresive = true;
                files.addAll(getListOfFilesToImport(inFolderPath, inIsRecuresive));
            }
            else {
                return null;
            }
        }
        // Read all of the files at this stage: EXIF data and make the thumbnail in memory
        final List<BulkUploadImageFileWrapper> imageList = Collections.synchronizedList(new ArrayList<BulkUploadImageFileWrapper>(files.size()));
        // First load all the images and sort them according to date
        final ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount(), new NamedThreadFactory("WL_BulkImport(Load)"));
        final AtomicInteger counter = new AtomicInteger();
        for (int t = 0; t < files.size(); t++) {
            final File tempFile = files.get(t);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    loadFileData(tempFile.toPath(), imageList);
                    try {
                        inProgressbarTask.setTaskProgress(counter.getAndIncrement(), 0, files.size());
                    }
                    catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            });
        }
        if (!UtilsConcurency.waitForExecutorToShutdown(executorService)) {
            return null;
        }
        // The images must be sorted according to date to make sure they are grouped correctly into sightings
        Collections.sort(imageList);
        inProgressbarTask.setMessage("Bulk Import Preparation: Process files...");
        inProgressbarTask.setTaskProgress(95);
        long timeDiffInMiliseconds = inSightingDurationInSeconds*1000;
        // Next calculate the sightings and build the Object[][]
        Map<BulkUploadSightingWrapper, BulkUploadImageListWrapper> finalMap =
                new LinkedHashMap<BulkUploadSightingWrapper, BulkUploadImageListWrapper>(imageList.size());
        if (imageList.size() > 0) {
            // Start off with a value that is garuanteed to be updated for the first record, to get things started...
            Date currentSightingDate = new Date(imageList.get(0).getDate().getTime() - timeDiffInMiliseconds*2);
            // If no image has any date, then all will be in this initial SightingWrapper.
            BulkUploadSightingWrapper sightingKey = new BulkUploadSightingWrapper(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
            for (BulkUploadImageFileWrapper temp : imageList) {
                if (!temp.isInSameSighting(currentSightingDate, timeDiffInMiliseconds)) {
                    // Start a new sighting and image list for the linked images
                    sightingKey = new BulkUploadSightingWrapper(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.MEDIUM_SMALL));
                    // Set other defaults for the sighting
                    setDefaultsForNewBulkUploadSightings(sightingKey);
                    // Update the map
                    finalMap.put(sightingKey, new BulkUploadImageListWrapper());
                    // Set the date for this sighting
                    currentSightingDate = temp.getDate();
                    sightingKey.setDate(currentSightingDate);
                }
                else {
                    // Update the curent date to compare against
                    currentSightingDate = temp.getDate();
                }
                // Set the GPS details (if not already set)
                if ((sightingKey.getLatitude() == null || Latitudes.NONE.equals(sightingKey.getLatitude()))
                        && (sightingKey.getLongitude() == null || Longitudes.NONE.equals(sightingKey.getLongitude()))) {
                    UtilsGps.copyGpsBetweenDOs(sightingKey, temp.getDataObjectWithGPS());
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
        wrapper.setRecursive(inIsRecuresive);
        return wrapper;
    }

    public static void setDefaultsForNewBulkUploadSightings(BulkUploadSightingWrapper inBulkUploadSightingWrapper) {
        inBulkUploadSightingWrapper.setCertainty(Certainty.SURE);
        inBulkUploadSightingWrapper.setSightingEvidence(SightingEvidence.SEEN);
        inBulkUploadSightingWrapper.setLifeStatus(LifeStatus.ALIVE);
        inBulkUploadSightingWrapper.setTimeAccuracy(TimeAccuracy.GOOD);
    }

    private static void loadFileData(Path inFile, List<BulkUploadImageFileWrapper> inImageList) {
        // Note: Ek load die file meer as een keer (HDD+OS cache), maar dis steeds redelik vinnig, en ek kry "out of memory" issues as ek dit alles in 'n inputstream in lees en traai hergebruik...
        Metadata metadata = null;
        try {
            if (WildLogFileExtentions.Images.isJPG(inFile)) {
                metadata = JpegMetadataReader.readMetadata(inFile.toFile());
            }
        }
        catch (JpegProcessingException ex) {
            System.err.println("Error reading EXIF data for: " + inFile);
            ex.printStackTrace(System.err);
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        Date date = UtilsImageProcessing.getDateFromImage(metadata, inFile);
        if (date != null) {
            ImageIcon imageIcon;
            if (WildLogFileExtentions.Images.isKnownExtention(inFile)) {
                imageIcon = UtilsImageProcessing.getScaledIcon(inFile, WildLogThumbnailSizes.MEDIUM.getSize());
            }
            else 
            if (WildLogFileExtentions.Movies.isKnownExtention(inFile)) {
                imageIcon = UtilsImageProcessing.getScaledIcon(
                        WildLogSystemImages.MOVIES.getWildLogFile().getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM),
                        WildLogThumbnailSizes.MEDIUM.getSize());
            }
            else {
                imageIcon = UtilsImageProcessing.getScaledIcon(
                        WildLogSystemImages.OTHER_FILES.getWildLogFile().getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM),
                        WildLogThumbnailSizes.MEDIUM.getSize());
            }
            BulkUploadImageFileWrapper wrapper = new BulkUploadImageFileWrapper(
                    inFile, imageIcon, date);
            wrapper.setDataObjectWithGPS(UtilsImageProcessing.getExifGpsFromJpeg(metadata));
            inImageList.add(wrapper);
        }
        else {
            System.out.println("Could not determine date for image file: " + inFile.toAbsolutePath());
        }
    }

    private static Object[][] getArrayFromHash(Map<BulkUploadSightingWrapper, BulkUploadImageListWrapper> inData){
        Object[] keys = inData.keySet().toArray();
        Object[] values = inData.values().toArray();
        Object[][] temp = new Object[keys.length][2];
        for(int i=0; i < keys.length; i++) {
            temp[i][0] = keys[i];
            temp[i][1] = values[i];
        }
        return temp;
    }

    public static String[] getColumnNames() {
        return new String[] {
            "Observations", "Images"
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
                    if (WildLogFileExtentions.Images.isKnownExtention(tempFile.toPath()) 
                            || WildLogFileExtentions.Movies.isKnownExtention(tempFile.toPath())) {
                        list.add(tempFile);
                    }
                }
            }
        }
        return list;
    }

}
