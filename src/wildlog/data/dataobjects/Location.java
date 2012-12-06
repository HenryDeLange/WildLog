package wildlog.data.dataobjects;

import KmlGenerator.objects.KmlEntry;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithKML;
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.LocationRating;
import wildlog.mapping.utils.UtilsGps;
import wildlog.html.utils.UtilsHTML;

public class Location extends DataObjectWithGPS implements Comparable<Location>, DataObjectWithHTML, DataObjectWithKML {
    private String name; // Used as index (ID)
    private String description;
    private LocationRating rating;
    private GameViewRating gameViewingRating;
    private String habitatType;
    private List<AccommodationType> accommodationType;
    private CateringType catering;
    private String contactNumbers;
    private String website;
    private String email;
    private String directions;

    // CONSTRUCTORS:
    public Location() {
    }

    public Location(String inName) {
        name = inName;
    }

    // METHODS:
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Location inLocation) {
        if (inLocation != null)
            if (name != null && inLocation.getName() != null) {
                return(name.compareToIgnoreCase(inLocation.getName()));
            }
        return 0;
    }

    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp, UtilsHTML.ImageExportTypes inExportType) {
        StringBuilder fotoString = new StringBuilder();
        if (inIncludeImages) {
            List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile("LOCATION-" + name));
            for (int t = 0; t < fotos.size(); t++) {
                fotoString.append(fotos.get(t).toHTML(inExportType));
            }
        }
        StringBuilder visitsString = new StringBuilder();
        if (inIsRecursive) {
            Visit tempVisit = new Visit();
            tempVisit.setLocationName(name);
            List<Visit> visits = inApp.getDBI().list(tempVisit);
            for (int t = 0; t < visits.size(); t++) {
                visitsString.append(visits.get(t).toHTML(inIsRecursive, inIncludeImages, inApp, inExportType)).append("<br/>");
            }
        }

        StringBuilder htmlLocation = new StringBuilder("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><title>Location: " + name + "</title></head>");
        htmlLocation.append("<body bgcolor='rgb(233,239,244)'>");
        htmlLocation.append("<table bgcolor='rgb(233,239,244)' width='100%'>");
        htmlLocation.append("<tr><td>");
        htmlLocation.append("<b><u>").append(name).append("</u></b>");
        htmlLocation.append("<br/>");
        htmlLocation.append("<br/><b>Latitude:</b> ").append(latitude).append(" ").append(latDegrees).append(" ").append(latMinutes).append(" ").append(latSeconds);
        htmlLocation.append("<br/><b>Longitude:</b> ").append(longitude).append(" ").append(lonDegrees).append(" ").append(lonMinutes).append(" ").append(lonSeconds);
        htmlLocation.append("<br/><b>General Rating:</b> ").append(UtilsHTML.formatString(rating));
        htmlLocation.append("<br/><b>Wildlife Rating:</b> ").append(UtilsHTML.formatString(gameViewingRating));
        htmlLocation.append("<br/><b>Habitat:</b> ").append(UtilsHTML.formatString(habitatType));
        htmlLocation.append("<br/><b>Description:</b> ").append(UtilsHTML.formatString(description));
        htmlLocation.append("<br/><b>Directions:</b> ").append(UtilsHTML.formatString(directions));
        if (website != null)
            if (website.length() > 0)
                htmlLocation.append("<br/><b>Website:</b> <a href=\"").append(UtilsHTML.formatString(website)).append("\">Link</a>");
            else
                htmlLocation.append("<br/><b>Website:</b> ").append(UtilsHTML.formatString(website));
        else
            htmlLocation.append("<br/><b>Website:</b> ").append(UtilsHTML.formatString(website));
        htmlLocation.append("<br/><b>Email:</b> ").append(UtilsHTML.formatString(email));
        htmlLocation.append("<br/><b>Phone Number:</b> ").append(UtilsHTML.formatString(contactNumbers));
        htmlLocation.append("<br/><b>Catering:</b> ").append(UtilsHTML.formatString(catering));
        htmlLocation.append("<br/><b>Accomodation:</b> ").append(UtilsHTML.formatString(accommodationType));
        if (inIncludeImages && fotoString.length() > 0) {
            htmlLocation.append("<br/>");
            htmlLocation.append("<br/><b>Photos:</b><br/>").append(fotoString);
        }
        if (inIsRecursive) {
            htmlLocation.append("<br/>");
            htmlLocation.append("</td></tr>");
            htmlLocation.append("<tr><td>");
            htmlLocation.append("<br/>").append(visitsString);
        }
        htmlLocation.append("</td></tr>");
        htmlLocation.append("</table>");
        htmlLocation.append("<br/>");
        htmlLocation.append("</body>");
        return htmlLocation.toString();
    }

    @Override
    public KmlEntry toKML(int inID, WildLogApp inApp) {
        KmlEntry entry = new KmlEntry();
        entry.setId(inID);
        entry.setName(name);
        entry.setDescription(this.toHTML(false, true, inApp, UtilsHTML.ImageExportTypes.ForKML));
        entry.setStyle("locationStyle");
        entry.setLatitude(UtilsGps.getDecimalDegree(latitude, latDegrees, latMinutes, latSeconds));
        entry.setLongitude(UtilsGps.getDecimalDegree(longitude, lonDegrees, lonMinutes, lonSeconds));
        return entry;
    }


    // GETTERS:
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocationRating getRating() {
        return rating;
    }

    public GameViewRating getGameViewingRating() {
        return gameViewingRating;
    }

    public String getHabitatType() {
        return habitatType;
    }

    public List<AccommodationType> getAccommodationType() {
        return accommodationType;
    }

    public String getContactNumbers() {
        return contactNumbers;
    }

    public CateringType getCatering() {
        return catering;
    }

    public String getDirections() {
        return directions;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    // SETTERS:
    public void setName(String inName) {
        name = inName;
    }

    public void setDescription(String inDescription) {
        description = inDescription;
    }

    public void setRating(LocationRating inRating) {
        rating = inRating;
    }

    public void setGameViewingRating(GameViewRating inGameViewingRating) {
        gameViewingRating = inGameViewingRating;
    }

    public void setHabitatType(String inHabitatType) {
        habitatType = inHabitatType;
    }

    public void setAccommodationType(List<AccommodationType> inAccommodationType) {
        accommodationType = inAccommodationType;
    }

    public void setContactNumbers(String inContactNumbers) {
        contactNumbers = inContactNumbers;
    }

    public void setCatering(CateringType inCatering) {
        catering = inCatering;
    }

    public void setDirections(String inDirections) {
        directions = inDirections;
    }

    public void setEmail(String inEmail) {
        email = inEmail;
    }

    public void setWebsite(String inWebsite) {
        website = inWebsite;
    }

}