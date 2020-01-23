package wildlog.sync.azure.dataobjects;

/**
 * The metadata (EXIF, etc.) associated with the blob.
 */
public class SyncBlobMetadata {
    private boolean success = false;
    private String datetime;
    private String latitude;
    private String longitude;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean inSuccess) {
        success = inSuccess;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String inDatetime) {
        this.datetime = inDatetime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String inLatitude) {
        latitude = inLatitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String inLongitude) {
        longitude = inLongitude;
    }
    
}
