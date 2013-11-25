package wildlog.data.dataobjects;

import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;


public class WildLogOptions {
    private int databaseVersion;
    private double defaultLatitude;
    private double defaultLongitude;
    private float defaultSlideshowSpeed;
    private int defaultSlideshowSize;
    private Latitudes defaultInputLatitude;
    private Longitudes defaultInputLongitude;
    private boolean isOnlinemapTheDefault;
    private boolean useThumbnailTables;
    private boolean useThumnailBrowsing;
    private boolean enableSounds;


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

    public Latitudes getDefaultInputLatitude() {
        return defaultInputLatitude;
    }

    public void setDefaultInputLatitude(Latitudes inDefaultInputLatitude) {
        defaultInputLatitude = inDefaultInputLatitude;
    }

    public Longitudes getDefaultInputLongitude() {
        return defaultInputLongitude;
    }

    public void setDefaultInputLongitude(Longitudes inDefaultInputLongitude) {
        defaultInputLongitude = inDefaultInputLongitude;
    }

    public int getDefaultSlideshowSize() {
        return defaultSlideshowSize;
    }

    public void setDefaultSlideshowSize(int inDefaultSlideshowSize) {
        defaultSlideshowSize = inDefaultSlideshowSize;
    }

    public boolean isIsOnlinemapTheDefault() {
        return isOnlinemapTheDefault;
    }

    public void setIsOnlinemapTheDefault(boolean inIsOnlinemapTheDefault) {
        isOnlinemapTheDefault = inIsOnlinemapTheDefault;
    }

    public boolean isUseThumbnailTables() {
        return useThumbnailTables;
    }

    public void setUseThumbnailTables(boolean inUseThumbnailTables) {
        useThumbnailTables = inUseThumbnailTables;
    }

    public boolean isUseThumnailBrowsing() {
        return useThumnailBrowsing;
    }

    public void setUseThumnailBrowsing(boolean inUseThumnailBrowsing) {
        useThumnailBrowsing = inUseThumnailBrowsing;
    }

    public boolean isEnableSounds() {
        return enableSounds;
    }

    public void setEnableSounds(boolean inEnableSounds) {
        enableSounds = inEnableSounds;
    }

}
