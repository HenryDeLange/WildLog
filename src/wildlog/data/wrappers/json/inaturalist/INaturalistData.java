package wildlog.data.wrappers.json.inaturalist;


public class INaturalistData {
    private long id;
    private double latitude;
    private double longitude;

    public long getId() {
        return id;
    }

    public void setId(long inId) {
        id = inId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double inLatitude) {
        latitude = inLatitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double inLongitude) {
        longitude = inLongitude;
    }

    @Override
    public String toString() {
        return super.toString() + "; id=" + id + "; latitude=" + latitude + "; longitude=" + longitude;
    }
    
}
