package wildlog.data.dataobjects.interfaces;

import wildlog.data.enums.GPSAccuracy;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;


public abstract class DataObjectWithGPS {
    protected Latitudes latitude;
    protected int latDegrees;
    protected int latMinutes;
    protected double latSeconds;
    protected Longitudes longitude;
    protected int lonDegrees;
    protected int lonMinutes;
    protected double lonSeconds;
    protected GPSAccuracy gpsAccuracy;
    protected double gpsAccuracyValue;


    public int getLatDegrees() {
        return latDegrees;
    }

    public int getLatMinutes() {
        return latMinutes;
    }

    public double getLatSeconds() {
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

    public double getLonSeconds() {
        return lonSeconds;
    }

    public Longitudes getLongitude() {
        return longitude;
    }

    public void setLatDegrees(int inLatDegrees) {
        latDegrees = inLatDegrees;
    }

    public void setLatMinutes(int inLatMinutes) {
        latMinutes = inLatMinutes;
    }

    public void setLatSeconds(double inLatSeconds) {
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

    public void setLonSeconds(double inLonSeconds) {
        lonSeconds = inLonSeconds;
    }

    public void setLongitude(Longitudes inLongitude) {
        longitude = inLongitude;
    }

    public GPSAccuracy getGPSAccuracy() {
        return gpsAccuracy;
    }

    public void setGPSAccuracy(GPSAccuracy inGPSAccuracy) {
        gpsAccuracy = inGPSAccuracy;
    }

    public double getGPSAccuracyValue() {
        return gpsAccuracyValue;
    }

    public void setGPSAccuracyValue(double gpsAccuracyValue) {
        this.gpsAccuracyValue = gpsAccuracyValue;
    }

}
