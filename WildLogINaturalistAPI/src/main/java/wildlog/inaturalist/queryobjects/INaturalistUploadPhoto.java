package wildlog.inaturalist.queryobjects;

import java.nio.file.Path;


/**
 * Used to upload photos using [POST/observation_photos]
 */
public class INaturalistUploadPhoto {
    private long observation_id;
    private Path file;

    
    public INaturalistUploadPhoto() {
    }

    public INaturalistUploadPhoto(long observation_id, Path file) {
        this.observation_id = observation_id;
        this.file = file;
    }
    

    public long getObservation_id() {
        return observation_id;
    }

    public void setObservation_id(long inObservation_id) {
        observation_id = inObservation_id;
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path inFile) {
        file = inFile;
    }
    
}
