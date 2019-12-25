package wildlog.data.dbi.queryobjects;


public class LocationCount {
    private long locationID;
    private String locationName;
    private int count;

    public LocationCount() {
    }

    public LocationCount(long inLocationID, String inLocationName, int inCount) {
        locationID = inLocationID;
        locationName = inLocationName;
        count = inCount;
    }

    public long getLocationID() {
        return locationID;
    }

    public void setLocationID(long inLocationID) {
        locationID = inLocationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String inLocationName) {
        locationName = inLocationName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int inCount) {
        count = inCount;
    }

}
