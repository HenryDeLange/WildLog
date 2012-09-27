package wildlog.ui.panel.bulkupload.data;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.SightingEvidence;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageListWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadSightingWrapper;
import wildlog.utils.ui.Utils;


public class BulkUploadDataLoader {

    public static BulkUploadDataWrapper genenrateTableData(File inFolderPath, boolean inIsRecuresive, int inSightingDurationInSeconds) {
        final List<File> files = getListOfFilesToImport(inFolderPath, inIsRecuresive);
        // Read all of the files at this stage: EXIF data and make the thumbnail in memory
        final List<BulkUploadImageFileWrapper> imageList = new ArrayList<BulkUploadImageFileWrapper>(files.size());
        // First load all the images and sort them according to date
        Runnable run1 = new Runnable() {
                @Override
                public void run() {
                    for (int t = 0; t < files.size()/4; t++) {
                        loadFileData(files.get(t), imageList);
                    }
                }
            };
        Runnable run2 = new Runnable() {
                @Override
                public void run() {
                    for (int t = files.size()/4; t < files.size()/4*2; t++) {
                        loadFileData(files.get(t), imageList);
                    }
                }
            };
        Runnable run3 = new Runnable() {
                @Override
                public void run() {
                    for (int t = files.size()/4*2; t < files.size()/4*3; t++) {
                        loadFileData(files.get(t), imageList);
                    }
                }
            };
        Runnable run4 = new Runnable() {
                @Override
                public void run() {
                    for (int t = files.size()/4*3; t < files.size(); t++) {
                        loadFileData(files.get(t), imageList);
                    }
                }
            };
         // This will starts the runnables and calls "Thread.join()" to make sure that
         // processing waits for the thread to finish before continuing. This is done
         // to preserve method execution order, but still benefit from multiple threads
         // doing the work, without having to manage their states, async nature, etc.
        Thread thread1 = new Thread(run1);
        thread1.start();
        Thread thread2 = new Thread(run2);
        thread2.start();
        Thread thread3 = new Thread(run3);
        thread3.start();
        Thread thread4 = new Thread(run4);
        thread4.start();
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        }
        catch (InterruptedException ex) {
            System.out.println("Thread unexpectedly interrupted...");
            ex.printStackTrace(System.err);
        }
        Collections.sort(imageList);
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
                    sightingKey = new BulkUploadSightingWrapper(Utils.getScaledIconForNoImage(150));
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
        // Hope that making the buffer big enough and mark/reset the stream will improve reading performance
        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(inFile), 3000000);
            inputStream.mark(5000000);
            Date date = Utils.getExifDateFromJpeg(inFile);
            inputStream.reset();
            inImageList.add(new BulkUploadImageFileWrapper(
                inFile,
                Utils.getScaledIcon(new ImageIcon(ImageIO.read(inputStream)), 200),
                date));
        }
        catch (IOException ex) {
            Logger.getLogger(BulkUploadDataLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
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
