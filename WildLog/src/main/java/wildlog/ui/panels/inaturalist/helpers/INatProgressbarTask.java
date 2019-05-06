package wildlog.ui.panels.inaturalist.helpers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.logging.log4j.Level;
import org.jdesktop.application.Application;
import wildlog.WildLogApp;
import wildlog.inaturalist.INatAPI;
import wildlog.inaturalist.queryobjects.INaturalistUploadPhoto;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.WLOptionPane;


public class INatProgressbarTask extends ProgressbarTask {
    private static final Deque<INatTaskEntry> INATQUEUE = new ArrayDeque<>();

    public INatProgressbarTask(Application inApplication) {
        super(inApplication);
    }
    
    public void submitTask(long inINatID, Path inFile, String inINatToken, boolean inDeleteFile) {
        INATQUEUE.add(new INatTaskEntry(inINatID, inFile, inINatToken, inDeleteFile));
        setProgress(0);
        setMessage("Busy with the queued iNaturalist file uploads: 1 of " + INATQUEUE.size());
    }

    @Override
    protected Object doInBackground() throws Exception {
        setProgress(0);
        setMessage("Starting the queued iNaturalist file uploads: " + INATQUEUE.size() + " queued");
        while (!INATQUEUE.isEmpty()) {
            setProgress(0);
            setMessage("Busy with the queued iNaturalist file uploads: 1 of " + INATQUEUE.size());
            // Get the next file to upload, but don't remove from the queue until after it has been processed
            INatTaskEntry taskEntry = INATQUEUE.peek();
            if (Files.exists(taskEntry.getPath())) {
                try {
                    INaturalistUploadPhoto iNatPhoto = new INaturalistUploadPhoto();
                    iNatPhoto.setObservation_id(taskEntry.getINatID());
                    iNatPhoto.setFile(taskEntry.getPath());
                    INatAPI.uploadPhoto(iNatPhoto, taskEntry.getINatToken());
                    if (taskEntry.isDeleteAfterwards()) {
                        Files.delete(taskEntry.getPath());
                    }
                }
                catch (Exception ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    WLOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                            "<html>There was an unexpected problem uploading the WildLog Image to iNaturalist."
                            + "<br>iNaturalist Observation ID: " + taskEntry.getINatID() + "</html>",
                            "Upload Error", WLOptionPane.ERROR_MESSAGE);
                }
            }
            // Remove the file form the queue after it has been processed
            INATQUEUE.poll();
        }
        setProgress(100);
        setMessage("Done with all queued iNaturalist file uploads");
        cancel(false);
        return null;
    }
    
    private static class INatTaskEntry {
        private long iNatID;
        private Path path;
        private String iNatToken;
        private boolean deleteAfterwards;

        public INatTaskEntry() {
        }

        public INatTaskEntry(long inINatID, Path inPath, String inINatToken, boolean inDeleteAfterwards) {
            path = inPath;
            iNatID = inINatID;
            iNatToken = inINatToken;
            deleteAfterwards = inDeleteAfterwards;
        }
        
        public long getINatID() {
            return iNatID;
        }

        public void setINatID(long inINatID) {
            iNatID = inINatID;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path inPath) {
            path = inPath;
        }

        public String getINatToken() {
            return iNatToken;
        }

        public void setINatToken(String inINatToken) {
            iNatToken = inINatToken;
        }

        public boolean isDeleteAfterwards() {
            return deleteAfterwards;
        }

        public void setDeleteAfterwards(boolean inDeleteAfterwards) {
            deleteAfterwards = inDeleteAfterwards;
        }
        
    }
    
    public static int getINatQueueSize() {
        return INATQUEUE.size();
    }
    
}
