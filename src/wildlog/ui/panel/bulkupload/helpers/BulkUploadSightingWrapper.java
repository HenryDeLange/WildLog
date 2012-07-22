package wildlog.ui.panel.bulkupload.helpers;

import java.util.Date;


public class BulkUploadSightingWrapper {
    private long tempID;
    private String imagePath;
    private String elemantName;
    private Date dateAndTime;
    private double latitude;
    private double longitude;

    public Date getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(Date inDateAndTime) {
        dateAndTime = inDateAndTime;
    }

    public String getElemantName() {
        return elemantName;
    }

    public void setElemantName(String inElemantName) {
        elemantName = inElemantName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String inImagePath) {
        imagePath = inImagePath;
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

    public long getTempID() {
        return tempID;
    }

    public void setTempID(long inTempID) {
        tempID = inTempID;
    }

}
