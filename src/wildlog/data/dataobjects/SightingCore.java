package wildlog.data.dataobjects;

import java.util.Date;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Age;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.UnitsTemperature;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.Weather;


public class SightingCore extends DataObjectWithGPS implements DataObjectWithWildLogFile {
    public static final String WILDLOGFILE_ID_PREFIX = "SIGHTING-";
    public static final String WILDLOG_FOLDER_PREFIX = "Observations";
    protected long sightingCounter; // Used as index (ID)
    protected Date date; // must include time
    protected ActiveTimeSpesific timeOfDay;
    protected Weather weather;
    protected ViewRating viewRating;
    protected Certainty certainty;
    protected int numberOfElements;
    protected String details;
    protected SightingEvidence sightingEvidence;
    protected String elementName;
    protected String locationName;
    protected String visitName;
    protected int moonPhase = -1;
    protected Moonlight moonlight;
    protected Sex sex;
    protected LifeStatus lifeStatus;
    protected String tag; // For individual's names, etc.
    protected double temperature;
    protected UnitsTemperature unitsTemperature;
    protected int durationMinutes;
    protected double durationSeconds;
    protected TimeAccuracy timeAccuracy;
    protected Age age;


    public SightingCore() {
    }

    public SightingCore(long inSightingCounter) {
        sightingCounter = inSightingCounter;
    }

    public SightingCore(String inElementName, String inLocationName, String inVisitName) {
        elementName = inElementName;
        locationName = inLocationName;
        visitName = inVisitName;
    }

    @Override
    public String toString() {
        return locationName + " (" + elementName + ") [" + sightingCounter + "]";
    }

    @Override
    public int compareTo(Object inSighting) {
        if (inSighting != null) {
            // Ek los die instanceof check uit vir eers want ek glo nie ek sal ooit lyste sort met verskillende data objects in nie...
            SightingCore compareSighting = (SightingCore) inSighting;
            if (date != null && compareSighting.getDate() != null) {
                return(date.compareTo(compareSighting.getDate()));
            }
        }
        return 0;
    }

    @Override
    public String getWildLogFileID() {
        return WILDLOGFILE_ID_PREFIX + sightingCounter;
    }
    
    @Override
    public String getExportPrefix() {
        return WILDLOG_FOLDER_PREFIX;
    }

    @Override
    public String getDisplayName() {
        return toString();
    }
    
    @Override
    public String getIDField() {
        return Long.toString(sightingCounter);
    }
    
    public <T extends SightingCore> T cloneShallow() {
        try {
            T sighting = (T) this.getClass().newInstance();
            sighting.setAge(age);
            sighting.setCertainty(certainty);
            sighting.setDate(date);
            sighting.setDetails(details);
            sighting.setDurationMinutes(durationMinutes);
            sighting.setDurationSeconds(durationSeconds);
            sighting.setElementName(elementName);
            sighting.setGPSAccuracy(gpsAccuracy);
            sighting.setLatDegrees(latDegrees);
            sighting.setLatMinutes(latMinutes);
            sighting.setLatSeconds(latSeconds);
            sighting.setLatitude(latitude);
            sighting.setLifeStatus(lifeStatus);
            sighting.setLocationName(locationName);
            sighting.setLonDegrees(lonDegrees);
            sighting.setLonMinutes(lonMinutes);
            sighting.setLonSeconds(lonSeconds);
            sighting.setLongitude(longitude);
            sighting.setMoonPhase(moonPhase);
            sighting.setMoonlight(moonlight);
            sighting.setNumberOfElements(numberOfElements);
            sighting.setSex(sex);
            sighting.setSightingCounter(sightingCounter);
            sighting.setSightingEvidence(sightingEvidence);
            sighting.setTag(tag);
            sighting.setTemperature(temperature);
            sighting.setTimeAccuracy(timeAccuracy);
            sighting.setTimeOfDay(timeOfDay);
            sighting.setUnitsTemperature(unitsTemperature);
            sighting.setViewRating(viewRating);
            sighting.setVisitName(visitName);
            sighting.setWeather(weather);
            return sighting;
        }
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    public Date getDate() {
        return date;
    }

    public ActiveTimeSpesific getTimeOfDay() {
        return timeOfDay;
    }

    public Weather getWeather() {
        return weather;
    }

    public ViewRating getViewRating() {
        return viewRating;
    }

    public Certainty getCertainty() {
        return certainty;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public String getDetails() {
        if (details == null) {
            details = "";
        }
        return details;
    }

    public SightingEvidence getSightingEvidence() {
        return sightingEvidence;
    }

    public long getSightingCounter() {
        return sightingCounter;
    }

    public void setDate(Date inDate) {
        date = inDate;
    }

    public void setTimeOfDay(ActiveTimeSpesific inTimeOfDay) {
        timeOfDay = inTimeOfDay;
    }

    public void setWeather(Weather inWeather) {
        weather = inWeather;
    }

    public void setViewRating(ViewRating inViewRating) {
        viewRating = inViewRating;
    }

    public void setCertainty(Certainty inCertainty) {
        certainty = inCertainty;
    }

    public void setNumberOfElements(int inNumberOfElements) {
        numberOfElements = inNumberOfElements;
    }

    public void setDetails(String inDetails) {
        details = inDetails;
    }

    public void setSightingEvidence(SightingEvidence inSightingEvidence) {
        sightingEvidence = inSightingEvidence;
    }

    public void setSightingCounter(long inSightingCounter) {
        sightingCounter = inSightingCounter;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String inElementName) {
        elementName = inElementName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String inLocationName) {
        locationName = inLocationName;
    }

    public String getVisitName() {
        return visitName;
    }

    public void setVisitName(String inVisitName) {
        visitName = inVisitName;
    }

    public int getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(int inMoonPhase) {
        moonPhase = inMoonPhase;
    }

    public Moonlight getMoonlight() {
        return moonlight;
    }

    public void setMoonlight(Moonlight inMoonlight) {
        moonlight = inMoonlight;
    }

    public LifeStatus getLifeStatus() {
        return lifeStatus;
    }

    public void setLifeStatus(LifeStatus inLifeStatus) {
        lifeStatus = inLifeStatus;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex inSex) {
        sex = inSex;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String inTag) {
        tag = inTag;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double inTemperature) {
        temperature = inTemperature;
    }

    public UnitsTemperature getUnitsTemperature() {
        return unitsTemperature;
    }

    public void setUnitsTemperature(UnitsTemperature inUnitsTemperature) {
        unitsTemperature = inUnitsTemperature;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int inDurationMinutes) {
        durationMinutes = inDurationMinutes;
    }

    public double getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(double inDurationSeconds) {
        durationSeconds = inDurationSeconds;
    }

    public TimeAccuracy getTimeAccuracy() {
        return timeAccuracy;
    }

    public void setTimeAccuracy(TimeAccuracy inTimeAccuracy) {
        timeAccuracy = inTimeAccuracy;
    }

    public Age getAge() {
        return age;
    }

    public void setAge(Age inAge) {
        age = inAge;
    }

}