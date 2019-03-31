package wildlog.ui.panels.bulkupload.data;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageListWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadSightingWrapper;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogApplicationTypes;
import wildlog.utils.WildLogFileExtentions;
import wildlog.utils.WildLogSystemImages;


public class BulkUploadDataLoader {
    
    public static BulkUploadDataWrapper genenrateTableData(List<Path> inLstFolderPaths, boolean inIsRecuresive, 
            int inSightingDurationInSeconds, final ProgressbarTask inProgressbarTask, final JLabel inLblFilesRead, WildLogApp inApp) {
        long time = System.currentTimeMillis();
        WildLogApp.LOGGER.log(Level.INFO, "Starting BulkUploadDataWrapper.genenrateTableData() - The files will be read and prepared for the table to display.");
        inProgressbarTask.setMessage("Bulk Import Preparation: Configuring...");
        if (inLstFolderPaths != null && inLstFolderPaths.size() == 1) {
            if (Files.isRegularFile(inLstFolderPaths.get(0))) {
                int result = WLOptionPane.showConfirmDialog(WildLogApp.getApplication().getMainFrame(), 
                        "Only one file was selected. Would you like to search for other files in the folders as well?", 
                        "Include all files?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    inLstFolderPaths.set(0, inLstFolderPaths.get(0).getParent());
                }
            }
        }
        final List<Path> lstAllFiles = getListOfFilesToImport(inLstFolderPaths, inIsRecuresive);
        if (lstAllFiles.isEmpty() && !inIsRecuresive) {
            int result = WLOptionPane.showConfirmDialog(WildLogApp.getApplication().getMainFrame(), 
                    "No supported files were found in the specified folder. Would you like to search in the subfolders as well?", 
                    "Include subfolders?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                lstAllFiles.addAll(getListOfFilesToImport(inLstFolderPaths, true));
            }
            else {
                return null;
            }
        }
        // Update the UI to show the numebr of files found
        inLblFilesRead.setText(inLblFilesRead.getText().substring(0, inLblFilesRead.getText().lastIndexOf(':') + 1) + " " + lstAllFiles.size());
        // Read all of the files at this stage: EXIF data and make the thumbnail in memory
        final List<BulkUploadImageFileWrapper> imageList = Collections.synchronizedList(new ArrayList<BulkUploadImageFileWrapper>(lstAllFiles.size()));
        // First load all the images and sort them according to date
        final ExecutorService executorService = Executors.newFixedThreadPool(inApp.getThreadCount(), new NamedThreadFactory("WL_BulkImport(Load)"));
        final AtomicInteger counter = new AtomicInteger();
        for (int t = 0; t < lstAllFiles.size(); t++) {
            final Path tempFile = lstAllFiles.get(t);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    loadFileData(tempFile, imageList);
                    try {
                        inProgressbarTask.setTaskProgress(counter.getAndIncrement(), 0, lstAllFiles.size() + 1); // Prevent the progress bar from reaching 100%
                        inProgressbarTask.setMessage("Bulk Import Preparation: Loading files... " + inProgressbarTask.getProgress() + "%");
                    }
                    catch (Exception ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                }
            });
        }
        if (!UtilsConcurency.waitForExecutorToShutdown(executorService)) {
            return null;
        }
        // The images must be sorted according to date to make sure they are grouped correctly into sightings
        Collections.sort(imageList);
        inProgressbarTask.setTaskProgress(99);
        inProgressbarTask.setMessage("Bulk Import Preparation: Loading files... " + inProgressbarTask.getProgress() + "%");
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
                    UtilsGPS.copyGpsBetweenDOs(sightingKey, temp.getDataObjectWithGPS());
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
        WildLogApp.LOGGER.log(Level.INFO, "Finished BulkUploadDataWrapper.genenrateTableData() - The process took {} seconds.", (System.currentTimeMillis() - time)/1000);
        return wrapper;
    }

    public static void setDefaultsForNewBulkUploadSightings(BulkUploadSightingWrapper inBulkUploadSightingWrapper) {
        // Setup the certainty
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
            inBulkUploadSightingWrapper.setCertainty(Certainty.NONE);
        }
        else {
            inBulkUploadSightingWrapper.setCertainty(Certainty.SURE);
        }
        inBulkUploadSightingWrapper.setSightingEvidence(SightingEvidence.SEEN);
        inBulkUploadSightingWrapper.setLifeStatus(LifeStatus.ALIVE);
        inBulkUploadSightingWrapper.setTimeAccuracy(TimeAccuracy.GOOD);
    }

    private static void loadFileData(Path inFile, List<BulkUploadImageFileWrapper> inImageList) {
        // Note: Ek load die file meer as een keer (HDD+OS cache), maar dis steeds redelik vinnig, 
        //       en ek kry "out of memory" issues as ek dit alles in 'n inputstream in lees en traai hergebruik...
        Metadata metadata = null;
        try {
            if (WildLogFileExtentions.Images.isJPG(inFile)) {
                metadata = JpegMetadataReader.readMetadata(inFile.toFile());
            }
        }
        catch (JpegProcessingException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "Error reading EXIF data for: {}", inFile);
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        Date date = UtilsImageProcessing.getDateFromImage(metadata, inFile);
        if (date != null) {
            ImageIcon imageIcon;
            if (WildLogFileExtentions.Images.isKnownExtention(inFile)) {
                imageIcon = UtilsImageProcessing.getScaledIcon(inFile, WildLogThumbnailSizes.MEDIUM.getSize(), true, metadata);
            }
            else 
            if (WildLogFileExtentions.Movies.isKnownExtention(inFile)) {
                imageIcon = UtilsImageProcessing.getScaledIcon(
                        WildLogSystemImages.MOVIES.getWildLogFile().getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM),
                        WildLogThumbnailSizes.MEDIUM.getSize(), false);
            }
            else {
                imageIcon = UtilsImageProcessing.getScaledIcon(
                        WildLogSystemImages.OTHER_FILES.getWildLogFile().getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM),
                        WildLogThumbnailSizes.MEDIUM.getSize(), false);
            }
            BulkUploadImageFileWrapper wrapper = new BulkUploadImageFileWrapper(inFile, imageIcon, date, UtilsImageProcessing.getExifGpsFromJpeg(metadata));
            inImageList.add(wrapper);
        }
        else {
            WildLogApp.LOGGER.log(Level.INFO, "Could not determine date for image file: {}", inFile.toAbsolutePath());
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

    private static List<Path> getListOfFilesToImport(List<Path> inListPaths, boolean inIncludeFolders) {
        List<Path> lstAllFiles = new ArrayList<>();
        if (inListPaths != null) {
            for (Path path : inListPaths) {
                if (path != null) {
                    lstAllFiles.addAll(getListOfFilesToImport(path.toFile(), inIncludeFolders));
                }
            }
        }
        return lstAllFiles;
    }
    
    private static List<Path> getListOfFilesToImport(File inRoot, boolean inIncludeFolders) {
        List<Path> lstFilesToImport = new ArrayList<>();
        if (inRoot != null) {
            if (inRoot.isFile()) {
                if (WildLogFileExtentions.Images.isKnownExtention(inRoot.toPath()) 
                        || WildLogFileExtentions.Movies.isKnownExtention(inRoot.toPath())) {
                    lstFilesToImport.add(inRoot.toPath());
                }
            }
            else {
                File[] tempFileList = inRoot.listFiles();
                if (tempFileList != null) {
                    for (File tempFile : tempFileList) {
                        if (inIncludeFolders && tempFile.isDirectory()) {
                            lstFilesToImport.addAll(getListOfFilesToImport(tempFile, inIncludeFolders));
                        }
                        else {
                            if (WildLogFileExtentions.Images.isKnownExtention(tempFile.toPath()) 
                                    || WildLogFileExtentions.Movies.isKnownExtention(tempFile.toPath())) {
                                lstFilesToImport.add(tempFile.toPath());
                            }
                        }
                    }
                }
            }
        }
        return lstFilesToImport;
    }

}
