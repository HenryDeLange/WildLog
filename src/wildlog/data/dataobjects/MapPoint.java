package wildlog.data.dataobjects;

import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;

public class MapPoint {
    // Variables:
    private Latitudes latitude;
    private int latDegrees;
    private int latMinutes;
    private int latSeconds;
    private Longitudes longitude;
    private int lonDegrees;
    private int lonMinutes;
    private int lonSeconds;
    private String label;

    // Constructor:
    public MapPoint() {

    }

    public MapPoint(Latitudes inLatitude, int inLatDegrees, int inLatMinutes, int inLatSeconds, Longitudes inLongitude, int inLonDegrees, int inLonMinutes, int inLonSeconds, String inLabel) {
        latitude = inLatitude;
        latDegrees = inLatDegrees;
        latMinutes = inLatMinutes;
        latSeconds = inLatSeconds;
        longitude = inLongitude;
        lonDegrees = inLonDegrees;
        lonMinutes = inLonMinutes;
        lonSeconds = inLonSeconds;
        label = inLabel;
    }

    // Getters:
    public int getLatDegrees() {
        return latDegrees;
    }

    public int getLatMinutes() {
        return latMinutes;
    }

    public int getLatSeconds() {
        return latSeconds;
    }

    public Latitudes getLatitude() {
        return latitude;
    }

    public int getLonDegrees() {
        return lonDegrees;
    }

    public int getLonMinutes() {
        return lonMinutes;
    }

    public int getLonSeconds() {
        return lonSeconds;
    }

    public Longitudes getLongitude() {
        return longitude;
    }

    public String getLabel() {
        return label;
    }

    // Setters:
    public void setLatDegrees(int inLatDegrees) {
        latDegrees = inLatDegrees;
    }

    public void setLatMinutes(int inLatMinutes) {
        latMinutes = inLatMinutes;
    }

    public void setLatSeconds(int inLatSeconds) {
        latSeconds = inLatSeconds;
    }

    public void setLatitude(Latitudes inLatitude) {
        latitude = inLatitude;
    }

    public void setLonDegrees(int inLonDegrees) {
        lonDegrees = inLonDegrees;
    }

    public void setLonMinutes(int inLonMinutes) {
        lonMinutes = inLonMinutes;
    }

    public void setLonSeconds(int inLonSeconds) {
        lonSeconds = inLonSeconds;
    }

    public void setLongitude(Longitudes inLongitude) {
        longitude = inLongitude;
    }

    public void setLabel(String inLabel) {
        label = inLabel;
    }

}
