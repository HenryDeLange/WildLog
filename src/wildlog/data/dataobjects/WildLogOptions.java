package wildlog.data.dataobjects;


public class WildLogOptions {
    private int databaseVersion;
    private double defaultLatitude;
    private double defaultLongitude;
    private float defaultSlideshowSpeed;

    
    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(int inDatabaseVersion) {
        databaseVersion = inDatabaseVersion;
    }

    public double getDefaultLatitude() {
        return defaultLatitude;
    }

    public void setDefaultLatitude(double inDefaultLatitude) {
        defaultLatitude = inDefaultLatitude;
    }

    public double getDefaultLongitude() {
        return defaultLongitude;
    }

    public void setDefaultLongitude(double inDefaultLongitude) {
        defaultLongitude = inDefaultLongitude;
    }

    public float getDefaultSlideshowSpeed() {
        return defaultSlideshowSpeed;
    }

    public void setDefaultSlideshowSpeed(float inDefaultSlideshowSpeed) {
        defaultSlideshowSpeed = inDefaultSlideshowSpeed;
    }

}
