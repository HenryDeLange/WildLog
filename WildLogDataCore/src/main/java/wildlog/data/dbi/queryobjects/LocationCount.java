package wildlog.data.dbi.queryobjects;


public class LocationCount {
    private long locationID;
    private int count;

    public LocationCount() {
    }

    public LocationCount(long inLocationID, int inCount) {
        locationID = inLocationID;
        count = inCount;
    }

    public long getLocationID() {
        return locationID;
    }

    public void setLocationID(long inLocationID) {
        locationID = inLocationID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int inCount) {
        count = inCount;
    }

}
