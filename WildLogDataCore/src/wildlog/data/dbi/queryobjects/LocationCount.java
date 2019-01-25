package wildlog.data.dbi.queryobjects;


public class LocationCount {
    private String locationName;
    private int count;

    public LocationCount() {
    }

    public LocationCount(String inLocationName, int inCount) {
        locationName = inLocationName;
        count = inCount;
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
