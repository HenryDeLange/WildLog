package wildlog.data.dbi.legacy;

import java.util.Date;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Age;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.TimeAccuracy;
import wildlog.data.enums.UnitsTemperature;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.VisitType;
import wildlog.data.enums.Weather;


public class Legacy_SightingCore extends Legacy_DataObjectWithGPS {
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
    // Adding some extra fields that can optionally be cached for performance reasons
    protected ElementType cachedElementType;
    protected VisitType cachedVisitType;
    protected boolean cachedLinkedToINaturalist;


    public Legacy_SightingCore() {
    }

    public Legacy_SightingCore(long inSightingCounter) {
        sightingCounter = inSightingCounter;
    }

    public Legacy_SightingCore(String inElementName, String inLocationName, String inVisitName) {
        elementName = inElementName;
        locationName = inLocationName;
        visitName = inVisitName;
    }

    @Override
    public String toString() {
        return locationName + " (" + elementName + ") [" + sightingCounter + "]";
    }

    public String getWildLogFileID() {
        return WILDLOGFILE_ID_PREFIX + sightingCounter;
    }
    
    public String getExportPrefix() {
        return WILDLOG_FOLDER_PREFIX;
    }

    public String getDisplayName() {
        return toString();
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

    public ElementType getCachedElementType() {
        return cachedElementType;
    }

    public void setCachedElementType(ElementType inCachedElementType) {
        cachedElementType = inCachedElementType;
    }

    public VisitType getCachedVisitType() {
        return cachedVisitType;
    }

    public void setCachedVisitType(VisitType inCachedVisitType) {
        cachedVisitType = inCachedVisitType;
    }

    public boolean isCachedLinkedToINaturalist() {
        return cachedLinkedToINaturalist;
    }

    public void setCachedLinkedToINaturalist(boolean inCachedLinkedToINaturalist) {
        cachedLinkedToINaturalist = inCachedLinkedToINaturalist;
    }

}