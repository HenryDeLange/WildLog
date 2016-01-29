package wildlog.data.dataobjects;


public class WildLogOptions {
    private int databaseVersion;
    private double defaultLatitude;
    private double defaultLongitude;
    private float defaultSlideshowSpeed;
    private int defaultSlideshowSize;
    private boolean useThumbnailTables;
    private boolean useThumnailBrowsing;
    private boolean enableSounds;
    private boolean useScientificNames;
    private String workspaceName;
    private long workspaceID;


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

    public int getDefaultSlideshowSize() {
        return defaultSlideshowSize;
    }

    public void setDefaultSlideshowSize(int inDefaultSlideshowSize) {
        defaultSlideshowSize = inDefaultSlideshowSize;
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

    public boolean isUseScientificNames() {
        return useScientificNames;
    }

    public void setUseScientificNames(boolean inUseScientificNames) {
        useScientificNames = inUseScientificNames;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String inWorkspaceName) {
        workspaceName = inWorkspaceName;
    }

    public long getWorkspaceID() {
        return workspaceID;
    }

    public void setWorkspaceID(long inWorkspaceID) {
        workspaceID = inWorkspaceID;
    }

}
