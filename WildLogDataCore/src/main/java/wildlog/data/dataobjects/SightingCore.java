package wildlog.data.dataobjects;

import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
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
import wildlog.data.utils.UtilsData;


public class SightingCore extends DataObjectWithGPS implements DataObjectWithWildLogFile {
    public static final String WILDLOG_FOLDER_PREFIX = "Observations";
    protected Date date; // Must include the time
    protected ActiveTimeSpesific timeOfDay;
    protected Weather weather;
    protected ViewRating viewRating;
    protected Certainty certainty;
    protected int numberOfElements;
    protected String details;
    protected SightingEvidence sightingEvidence;
    protected long elementID;
    protected long locationID;
    protected long visitID;
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
    protected String cachedElementName;
    protected String cachedLocationName;
    protected String cachedVisitName;
    protected ElementType cachedElementType;
    protected VisitType cachedVisitType;
    protected boolean cachedLinkedToINaturalist;
    protected List<ExtraData> cachedLstExtraData;


    public SightingCore() {
    }

    public SightingCore(long inID) {
        id = inID;
    }

    public SightingCore(Long inElementID, Long inLocationID, Long inVisitID) {
        elementID = inElementID;
        locationID = inLocationID;
        visitID = inVisitID;
    }

    @Override
    public String toString() {
        return cachedLocationName + " (" + cachedElementName + ") [" + id + "]";
    }
    
// TODO: Dink weer oor of die ander data objects veilig die methods kan overwrite. Die op SightingCore breek Bulk Import...
//    @Override
//    public boolean equals(Object inObject) {
//        if (inObject == null || !(inObject instanceof SightingCore)) {
//            return false;
//        }
//        return id == ((SightingCore) inObject).getID();
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 5;
//        hash = 83 * hash + (int) (this.id ^ (this.id >>> 32));
//        hash = 83 * hash + (int) (this.elementID ^ (this.elementID >>> 32));
//        hash = 83 * hash + (int) (this.locationID ^ (this.locationID >>> 32));
//        hash = 83 * hash + (int) (this.visitID ^ (this.visitID >>> 32));
//        return hash;
//    }

    @Override
    public int compareTo(Object inSighting) {
        if (inSighting != null) {
            // Ek los die instanceof check uit vir eers want ek glo nie ek sal ooit lyste sort met verskillende data objects in nie...
            SightingCore compareSighting = (SightingCore) inSighting;
            if (date != null && compareSighting.getDate() != null) {
                int result = (date.compareTo(compareSighting.getDate()));
                if (result == 0) {
                    //From Long.compare(..) (since Java 7)
                    result = (id < compareSighting.getID()) ? -1 : ((id == compareSighting.getID()) ? 0 : 1);
                }
                return result;
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
        return toString();
    }
    
    @Override
    public long getIDField() {
        return id;
    }
    
    public boolean hasTheSameContent(SightingCore inSighting) {
        if (inSighting == null) {
            return false;
        }
        return UtilsData.isTheSame(getID(), inSighting.getID())
                && UtilsData.isTheSame(getAge(), inSighting.getAge())
                && UtilsData.isTheSame(getCertainty(), inSighting.getCertainty())
                && UtilsData.isTheSame(getDate(), inSighting.getDate())
                && UtilsData.isTheSame(getDetails(), inSighting.getDetails())
                && UtilsData.isTheSame(getDurationMinutes(), inSighting.getDurationMinutes())
                && UtilsData.isTheSame(getDurationSeconds(), inSighting.getDurationSeconds())
                && UtilsData.isTheSame(getElementID(), inSighting.getElementID())
                && UtilsData.isTheSame(getGPSAccuracy(), inSighting.getGPSAccuracy())
                && UtilsData.isTheSame(getGPSAccuracyValue(), inSighting.getGPSAccuracyValue())
                && UtilsData.isTheSame(getLatDegrees(), inSighting.getLatDegrees())
                && UtilsData.isTheSame(getLatMinutes(), inSighting.getLatMinutes())
                && UtilsData.isTheSame(getLatSeconds(), inSighting.getLatSeconds())
                && UtilsData.isTheSame(getLatitude(), inSighting.getLatitude())
                && UtilsData.isTheSame(getLifeStatus(), inSighting.getLifeStatus())
                && UtilsData.isTheSame(getLocationID(), inSighting.getLocationID())
                && UtilsData.isTheSame(getLonDegrees(), inSighting.getLonDegrees())
                && UtilsData.isTheSame(getLonMinutes(), inSighting.getLonMinutes())
                && UtilsData.isTheSame(getLonSeconds(), inSighting.getLonSeconds())
                && UtilsData.isTheSame(getLongitude(), inSighting.getLongitude())
                && UtilsData.isTheSame(getMoonPhase(), inSighting.getMoonPhase())
                && UtilsData.isTheSame(getMoonlight(), inSighting.getMoonlight())
                && UtilsData.isTheSame(getNumberOfElements(), inSighting.getNumberOfElements())
                && UtilsData.isTheSame(getSex(), inSighting.getSex())
                && UtilsData.isTheSame(getSightingEvidence(), inSighting.getSightingEvidence())
                && UtilsData.isTheSame(getTag(), inSighting.getTag())
                && UtilsData.isTheSame(getTemperature(), inSighting.getTemperature())
                && UtilsData.isTheSame(getTimeAccuracy(), inSighting.getTimeAccuracy())
                && UtilsData.isTheSame(getTimeOfDay(), inSighting.getTimeOfDay())
                && UtilsData.isTheSame(getUnitsTemperature(), inSighting.getUnitsTemperature())
                && UtilsData.isTheSame(getViewRating(), inSighting.getViewRating())
                && UtilsData.isTheSame(getVisitID(), inSighting.getVisitID())
                && UtilsData.isTheSame(getWeather(), inSighting.getWeather())
                && UtilsData.isTheSame(getAuditTime(), inSighting.getAuditTime())
                && UtilsData.isTheSame(getAuditUser(), inSighting.getAuditUser());
    }
    
    public <T extends SightingCore> T cloneShallow(Class<T> inClass) {
        try {
            T sighting = inClass.newInstance();
            sighting.setID(id);
            sighting.setAge(age);
            sighting.setCertainty(certainty);
            sighting.setDate(date);
            sighting.setDetails(details);
            sighting.setDurationMinutes(durationMinutes);
            sighting.setDurationSeconds(durationSeconds);
            sighting.setElementID(elementID);
            sighting.setCachedElementName(cachedElementName);
            sighting.setGPSAccuracy(gpsAccuracy);
            sighting.setGPSAccuracyValue(gpsAccuracyValue);
            sighting.setLatDegrees(latDegrees);
            sighting.setLatMinutes(latMinutes);
            sighting.setLatSeconds(latSeconds);
            sighting.setLatitude(latitude);
            sighting.setLifeStatus(lifeStatus);
            sighting.setLocationID(locationID);
            sighting.setCachedLocationName(cachedLocationName);
            sighting.setLonDegrees(lonDegrees);
            sighting.setLonMinutes(lonMinutes);
            sighting.setLonSeconds(lonSeconds);
            sighting.setLongitude(longitude);
            sighting.setMoonPhase(moonPhase);
            sighting.setMoonlight(moonlight);
            sighting.setNumberOfElements(numberOfElements);
            sighting.setSex(sex);
            sighting.setSightingEvidence(sightingEvidence);
            sighting.setTag(tag);
            sighting.setTemperature(temperature);
            sighting.setTimeAccuracy(timeAccuracy);
            sighting.setTimeOfDay(timeOfDay);
            sighting.setUnitsTemperature(unitsTemperature);
            sighting.setViewRating(viewRating);
            sighting.setVisitID(visitID);
            sighting.setCachedVisitName(cachedVisitName);
            sighting.setWeather(weather);
            sighting.setCachedElementType(cachedElementType);
            sighting.setCachedVisitType(cachedVisitType);
            sighting.setCachedLstExtraData(cachedLstExtraData);
            sighting.setAuditTime(auditTime);
            sighting.setAuditUser(auditUser);
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
    
    public long getElementID() {
        return elementID;
    }

    public void setElementID(long inElementID) {
        elementID = inElementID;
    }

    public long getLocationID() {
        return locationID;
    }

    public void setLocationID(long inLocationID) {
        locationID = inLocationID;
    }

    public long getVisitID() {
        return visitID;
    }

    public void setVisitID(long inVisitID) {
        visitID = inVisitID;
    }

    public String getCachedElementName() {
        return cachedElementName;
    }

    public void setCachedElementName(String inCachedElementName) {
        cachedElementName = inCachedElementName;
    }

    public String getCachedLocationName() {
        return cachedLocationName;
    }

    public void setCachedLocationName(String inCachedLocationName) {
        cachedLocationName = inCachedLocationName;
    }

    public String getCachedVisitName() {
        return cachedVisitName;
    }

    public void setCachedVisitName(String inCachedVisitName) {
        cachedVisitName = inCachedVisitName;
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

    public List<ExtraData> getCachedLstExtraData() {
        return cachedLstExtraData;
    }

    public void setCachedLstExtraData(List<ExtraData> inCachedLstExtraData) {
        cachedLstExtraData = inCachedLstExtraData;
    }

}