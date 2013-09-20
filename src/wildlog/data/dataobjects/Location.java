package wildlog.data.dataobjects;

import KmlGenerator.objects.KmlEntry;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithKML;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.LocationRating;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.WildLogPaths;

public class Location extends DataObjectWithGPS implements Comparable<Location>, DataObjectWithHTML, DataObjectWithKML, DataObjectWithWildLogFile {
    public static final String WILDLOGFILE_ID_PREFIX = "LOCATION-";
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
        if (inLocation != null) {
            if (name != null && inLocation.getName() != null) {
                return(name.compareToIgnoreCase(inLocation.getName()));
            }
        }
        return 0;
    }

    @Override
    public String getWildLogFileID() {
        return WILDLOGFILE_ID_PREFIX + name;
    }

    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        int progressMarker;
        if (inIsRecursive) {
            progressMarker = 30;
        }
        else {
            progressMarker = 95;
        }
        StringBuilder htmlLocation = new StringBuilder("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><title>Place: " + name + "</title></head>");
        htmlLocation.append("<body bgcolor='E9EFF4'>");
        htmlLocation.append("<table bgcolor='#E9EFF4' width='100%'>");
        htmlLocation.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        htmlLocation.append("<b><u>").append(name).append("</u></b>");
        htmlLocation.append("<br/>");
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Latitude:</b><br/> ", UtilsGps.getLatitudeString(this), true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Longitude:</b><br/> ", UtilsGps.getLongitudeString(this), true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>General Rating:</b><br/> ", rating, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Wildlife Rating:</b><br/> ", gameViewingRating, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Habitat:</b><br/> ", habitatType, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Description:</b><br/> ", description, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Directions:</b><br/> ", directions, true);
        if (website != null) {
            if (website.length() > 0) {
                UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Website:</b><br/> ", "<a href=\"" + website + "\">" + website + "</a>", true);
            }
            else {
                UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Website:</b><br/> ", website, true);
            }
        }
        else {
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Website:</b><br/> ", website, true);
        }
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Email:</b><br/> ", email, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Phone Number:</b><br/> ", contactNumbers, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Catering:</b><br/> ", catering, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Accomodation:</b><br/> ", accommodationType, true);
        if (inIncludeImages) {
            StringBuilder filesString = new StringBuilder(300);
            List<WildLogFile> files = inApp.getDBI().list(new WildLogFile(getWildLogFileID()));
            for (int t = 0; t < files.size(); t++) {
                filesString.append(files.get(t).toHTML(inExportType));
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress((int)(((double)t/files.size())*progressMarker));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(" "))
                            + " " + inProgressbarTask.getProgress() + "%");
                }
            }
            if (filesString.length() > 0) {
                htmlLocation.append("<br/>");
                htmlLocation.append("<br/><b>Photos:</b><br/>").append(filesString);
            }
        }
        if (inIsRecursive) {
            htmlLocation.append("<br/>");
            htmlLocation.append("</td></tr>");
            htmlLocation.append("<tr><td>");
            Visit tempVisit = new Visit();
            tempVisit.setLocationName(name);
            List<Visit> visits = inApp.getDBI().list(tempVisit);
            int counter = 0;
            for (int t = 0; t < visits.size(); t++) {
                htmlLocation.append("<br/>").append(visits.get(t).toHTML(inIsRecursive, inIncludeImages, inApp, inExportType, inProgressbarTask)).append("<br/>");
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress(progressMarker + (int)(((double)counter/visits.size())*(95-progressMarker)));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(" "))
                            + " " + inProgressbarTask.getProgress() + "%");
                    counter++;
                }
            }
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
        entry.setDescription(toHTML(false, true, inApp, UtilsHTMLExportTypes.ForKML, null));
        entry.setStyle("locationStyle");
        entry.setLatitude(UtilsGps.getDecimalDegree(latitude, latDegrees, latMinutes, latSeconds));
        entry.setLongitude(UtilsGps.getDecimalDegree(longitude, lonDegrees, lonMinutes, lonSeconds));
        return entry;
    }

    @Override
    public String getExportPrefix() {
        return WildLogPaths.WildLogPathPrefixes.PREFIX_LOCATION.toString();
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    public boolean hasTheSameContent(Location inLocation) {
        if (inLocation == null) {
            return false;
        }
        if (UtilsUI.isTheSame(this, inLocation)
                && UtilsUI.isTheSame(getAccommodationType(), inLocation.getAccommodationType())
                && UtilsUI.isTheSame(getCatering(), inLocation.getCatering())
                && UtilsUI.isTheSame(getContactNumbers(), inLocation.getContactNumbers())
                && UtilsUI.isTheSame(getDescription(), inLocation.getDescription())
                && UtilsUI.isTheSame(getDirections(), inLocation.getDirections())
                && UtilsUI.isTheSame(getEmail(), inLocation.getEmail())
                && UtilsUI.isTheSame(getGameViewingRating(), inLocation.getGameViewingRating())
                && UtilsUI.isTheSame(getHabitatType(), inLocation.getHabitatType())
                && UtilsUI.isTheSame(getName(), inLocation.getName())
                && UtilsUI.isTheSame(getRating(), inLocation.getRating())
                && UtilsUI.isTheSame(getWebsite(), inLocation.getWebsite())
                && UtilsUI.isTheSame(getLatDegrees(), inLocation.getLatDegrees())
                && UtilsUI.isTheSame(getLatMinutes(), inLocation.getLatMinutes())
                && UtilsUI.isTheSame(getLatSeconds(), inLocation.getLatSeconds())
                && UtilsUI.isTheSame(getLonDegrees(), inLocation.getLonDegrees())
                && UtilsUI.isTheSame(getLonMinutes(), inLocation.getLonMinutes())
                && UtilsUI.isTheSame(getLonSeconds(), inLocation.getLonSeconds())
                && UtilsUI.isTheSame(getLongitude(), inLocation.getLongitude())) {
            return true;
        }
        return false;
    }

    public Location cloneShallow() {
        Location location = new Location();
        location.setAccommodationType(accommodationType);
        location.setCatering(catering);
        location.setContactNumbers(contactNumbers);
        location.setDescription(description);
        location.setDirections(directions);
        location.setEmail(email);
        location.setGameViewingRating(gameViewingRating);
        location.setHabitatType(habitatType);
        location.setName(name);
        location.setRating(rating);
        location.setWebsite(website);
        location.setLatDegrees(latDegrees);
        location.setLatMinutes(latMinutes);
        location.setLatSeconds(latSeconds);
        location.setLonDegrees(lonDegrees);
        location.setLonMinutes(lonMinutes);
        location.setLonSeconds(lonSeconds);
        location.setLongitude(longitude);
        return location;
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