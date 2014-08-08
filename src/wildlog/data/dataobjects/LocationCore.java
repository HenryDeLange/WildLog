package wildlog.data.dataobjects;

import java.util.List;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.LocationRating;
import wildlog.data.utils.UtilsData;


public class LocationCore extends DataObjectWithGPS implements DataObjectWithWildLogFile {
    public static final String WILDLOGFILE_ID_PREFIX = "LOCATION-";
    public static final String WILDLOG_FOLDER_PREFIX = "Places";
    protected String name; // Used as index (ID)
    protected String description;
    protected LocationRating rating;
    protected GameViewRating gameViewingRating;
    protected String habitatType;
    protected List<AccommodationType> accommodationType;
    protected CateringType catering;
    protected String contactNumbers;
    protected String website;
    protected String email;
    protected String directions;


    public LocationCore() {
    }

    public LocationCore(String inName) {
        name = inName;
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Object inLocation) {
        if (inLocation != null) {
            // Ek los die instanceof check uit vir eers want ek glo nie ek sal ooit lyste sort met verskillende data objects in nie...
            LocationCore compareLocation = (LocationCore) inLocation;
            if (name != null && compareLocation.getName() != null) {
                return(name.compareToIgnoreCase(compareLocation.getName()));
            }
        }
        return 0;
    }

    @Override
    public String getWildLogFileID() {
        return WILDLOGFILE_ID_PREFIX + name;
    }
    
    @Override
    public String getExportPrefix() {
        return WILDLOG_FOLDER_PREFIX;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getIDField() {
        return name;
    }
    
    public boolean hasTheSameContent(LocationCore inLocation) {
        if (inLocation == null) {
            return false;
        }
        return UtilsData.isTheSame(this, inLocation)
                && UtilsData.isTheSame(getAccommodationType(), inLocation.getAccommodationType())
                && UtilsData.isTheSame(getCatering(), inLocation.getCatering())
                && UtilsData.isTheSame(getContactNumbers(), inLocation.getContactNumbers())
                && UtilsData.isTheSame(getDescription(), inLocation.getDescription())
                && UtilsData.isTheSame(getDirections(), inLocation.getDirections())
                && UtilsData.isTheSame(getEmail(), inLocation.getEmail())
                && UtilsData.isTheSame(getGameViewingRating(), inLocation.getGameViewingRating())
                && UtilsData.isTheSame(getHabitatType(), inLocation.getHabitatType())
                && UtilsData.isTheSame(getName(), inLocation.getName())
                && UtilsData.isTheSame(getRating(), inLocation.getRating())
                && UtilsData.isTheSame(getWebsite(), inLocation.getWebsite())
                && UtilsData.isTheSame(getLatDegrees(), inLocation.getLatDegrees())
                && UtilsData.isTheSame(getLatMinutes(), inLocation.getLatMinutes())
                && UtilsData.isTheSame(getLatSeconds(), inLocation.getLatSeconds())
                && UtilsData.isTheSame(getLonDegrees(), inLocation.getLonDegrees())
                && UtilsData.isTheSame(getLonMinutes(), inLocation.getLonMinutes())
                && UtilsData.isTheSame(getLonSeconds(), inLocation.getLonSeconds())
                && UtilsData.isTheSame(getLongitude(), inLocation.getLongitude())
                && UtilsData.isTheSame(getGPSAccuracy(), inLocation.getGPSAccuracy());
    }

    public <T extends LocationCore> T cloneShallow() {
        try {
            T location = (T) this.getClass().newInstance();
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
            location.setGPSAccuracy(gpsAccuracy);
            return location;
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

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