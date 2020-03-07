package wildlog.maps.kml.generator;

import wildlog.maps.kml.UtilsKML;


public class KmlEntry {
    private int id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private String style;

    public String getDescription() {
        return description;
    }

    public void setDescription(String inDescription) {
        description = UtilsKML.getXmlFriendlyString(inDescription);
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String inStyle) {
        style = inStyle;
    }

    public int getId() {
        return id;
    }

    public void setId(int inId) {
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

    public String getName() {
        return name;
    }

    public void setName(String inName) {
        name = UtilsKML.getXmlFriendlyString(inName);
    }

}
