package wildlog.data.dataobjects;

import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.LocationRating;
import wildlog.data.utils.UtilsData;


public class LocationCore extends DataObjectWithGPS implements DataObjectWithWildLogFile {
    public static final String WILDLOG_FOLDER_PREFIX = "Places";
    protected String name; // Must be unique
    protected String description;
    protected LocationRating rating;
    protected GameViewRating gameViewingRating;
    protected String habitatType;


    public LocationCore() {
    }
    
    public LocationCore(long inID, String inName) {
        id = inID;
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
    public long getWildLogFileID() {
        return id;
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
    public long getIDField() {
        return id;
    }
    
    public boolean hasTheSameContent(LocationCore inLocation) {
        if (inLocation == null) {
            return false;
        }
        return UtilsData.isTheSame(this, inLocation)
                && UtilsData.isTheSame(getID(), inLocation.getID())
                && UtilsData.isTheSame(getDescription(), inLocation.getDescription())
                && UtilsData.isTheSame(getGameViewingRating(), inLocation.getGameViewingRating())
                && UtilsData.isTheSame(getHabitatType(), inLocation.getHabitatType())
                && UtilsData.isTheSame(getName(), inLocation.getName())
                && UtilsData.isTheSame(getRating(), inLocation.getRating())
                && UtilsData.isTheSame(getLatDegrees(), inLocation.getLatDegrees())
                && UtilsData.isTheSame(getLatMinutes(), inLocation.getLatMinutes())
                && UtilsData.isTheSame(getLatSeconds(), inLocation.getLatSeconds())
                && UtilsData.isTheSame(getLonDegrees(), inLocation.getLonDegrees())
                && UtilsData.isTheSame(getLonMinutes(), inLocation.getLonMinutes())
                && UtilsData.isTheSame(getLonSeconds(), inLocation.getLonSeconds())
                && UtilsData.isTheSame(getLongitude(), inLocation.getLongitude())
                && UtilsData.isTheSame(getGPSAccuracy(), inLocation.getGPSAccuracy())
                && UtilsData.isTheSame(getGPSAccuracyValue(), inLocation.getGPSAccuracyValue())
                && UtilsData.isTheSame(getAuditTime(), inLocation.getAuditTime())
                && UtilsData.isTheSame(getAuditUser(), inLocation.getAuditUser());
    }

    public <T extends LocationCore> T cloneShallow() {
        try {
            T location = (T) this.getClass().newInstance();
            location.setID(id);
            location.setDescription(description);
            location.setGameViewingRating(gameViewingRating);
            location.setHabitatType(habitatType);
            location.setName(name);
            location.setRating(rating);
            location.setLatDegrees(latDegrees);
            location.setLatMinutes(latMinutes);
            location.setLatSeconds(latSeconds);
            location.setLonDegrees(lonDegrees);
            location.setLonMinutes(lonMinutes);
            location.setLonSeconds(lonSeconds);
            location.setLongitude(longitude);
            location.setGPSAccuracy(gpsAccuracy);
            location.setGPSAccuracyValue(gpsAccuracyValue);
            location.setAuditTime(auditTime);
            location.setAuditUser(auditUser);
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

}