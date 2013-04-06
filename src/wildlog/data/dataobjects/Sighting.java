package wildlog.data.dataobjects;

import KmlGenerator.objects.KmlEntry;
import java.util.Date;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithKML;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LifeStatus;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.Sex;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.UnitsTemperature;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.Weather;
import wildlog.html.utils.UtilsHTML;
import wildlog.mapping.utils.UtilsGps;

public class Sighting extends DataObjectWithGPS implements Comparable<Sighting>, DataObjectWithHTML, DataObjectWithKML, DataObjectWithWildLogFile {
    public static final String WILDLOGFILE_ID_PREFIX = "SIGHTING-";
    private long sightingCounter; // Used as index (ID)
    private Date date; // must include time
    private boolean timeUnknown;
    private ActiveTimeSpesific timeOfDay;
    private Weather weather;
    private ViewRating viewRating;
    private Certainty certainty;
    private int numberOfElements;
    private String details;
    private SightingEvidence sightingEvidence;
    private String elementName;
    private String locationName;
    private String visitName;
    private int moonPhase = -1;
    private Moonlight moonlight;
    private Sex sex;
    private LifeStatus lifeStatus;
    private String tag; // For individual's names, etc.
    private double temperature;
    private UnitsTemperature unitsTemperature;
    private int durationMinutes;
    private double durationSeconds;

    // CONSTRUCTORS:
    public Sighting() {
    }

    public Sighting(long inSightingCounter) {
        sightingCounter = inSightingCounter;
    }

    // METHIDS:
    @Override
    public String toString() {
        return locationName + " (" + elementName + ") [" + sightingCounter + "]";
    }

    @Override
    public int compareTo(Sighting inSighting) {
        if (inSighting != null)
            if (date != null && inSighting.getDate() != null) {
                return(date.compareTo(inSighting.getDate()));
            }
        return 0;
    }

    @Override
    public String getWildLogFileID() {
        return WILDLOGFILE_ID_PREFIX + sightingCounter;
    }

    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp, UtilsHTML.ImageExportTypes inExportType) {
        StringBuilder fotoString = new StringBuilder();
        if (inIncludeImages) {
            List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(getWildLogFileID()));
            for (int t = 0; t < fotos.size(); t++) {
                fotoString.append(fotos.get(t).toHTML(inExportType));
            }
        }
        StringBuilder htmlSighting = new StringBuilder("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><title>Sightings ID: " + sightingCounter + "</title></head>");
        htmlSighting.append("<body bgcolor='#EEEAD3'>");
        htmlSighting.append("<table bgcolor='#EEEAD3' width='100%'>");
        htmlSighting.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        htmlSighting.append("<b><u>Observation ID: ").append(UtilsHTML.formatObjectAsString(sightingCounter)).append("</u></b>");
        htmlSighting.append("<br/>");
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Creature:</b><br/> ", elementName, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Place:</b><br/> ", locationName, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Period:</b><br/> ", visitName, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Latitude:</b><br/> ", UtilsGps.getLatitudeString(this), true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Longitude:</b><br/> ", UtilsGps.getLongitudeString(this), true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Date:</b><br/> ", UtilsHTML.formatDateAsString(date, true), true);
        if (durationMinutes >= 0 && durationSeconds > 0)
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Duration:</b><br/> ", durationMinutes + " minutes, " + durationSeconds + " seconds", true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Time of Day:</b><br/> ", timeOfDay, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Moon Phase:</b><br/> ", moonPhase + " % Full", true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Moonlight:</b><br/> ", moonlight, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Number of Creatures:</b><br/> ", numberOfElements, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Weather:</b><br/> ", weather, true);
        if (unitsTemperature != null && !UnitsTemperature.NONE.equals(unitsTemperature))
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Temperature:</b><br/> ", temperature + " " + unitsTemperature, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Sex:</b><br/> ", sex, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Life Status:</b><br/> ", lifeStatus, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Evidence:</b><br/> ", sightingEvidence, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>View Rating:</b><br/> ", viewRating, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Certainty:</b><br/> ", certainty, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Tag:</b><br/> ", tag, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Details:</b><br/> ", details, true);
        if (inIncludeImages && fotoString.length() > 0) {
            htmlSighting.append("<br/>");
            htmlSighting.append("<br/><b>Photos:</b><br/>").append(fotoString);
        }
        htmlSighting.append("</td></tr>");
        htmlSighting.append("</table>");
        htmlSighting.append("<br/>");
        htmlSighting.append("</body>");
        return htmlSighting.toString();
    }

    @Override
    public KmlEntry toKML(int inID, WildLogApp inApp) {
        KmlEntry entry = new KmlEntry();
        entry.setId(inID);
        entry.setName(elementName);
        entry.setDescription(this.toHTML(false, true, inApp, UtilsHTML.ImageExportTypes.ForKML));
        Element element = inApp.getDBI().find(new Element(elementName));
        if (element.getType() != null) {
            if (element.getType().equals(ElementType.ANIMAL)) {
                if (element.getFeedingClass() == null)
                    entry.setStyle("animalOtherStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.CARNIVORE))
                    entry.setStyle("animalCarnivoreStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.HERBIVORE))
                    entry.setStyle("animalHerbivoreStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.OMNIVORE))
                    entry.setStyle("animalOmnivoreStyle");
                else
                    entry.setStyle("animalOtherStyle");
            }
            else
            if (element.getType().equals(ElementType.BIRD)) {
                if (element.getFeedingClass() == null)
                    entry.setStyle("birdOtherStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.CARNIVORE))
                    entry.setStyle("birdCarnivoreStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.HERBIVORE))
                    entry.setStyle("birdHerbivoreStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.OMNIVORE))
                    entry.setStyle("birdOmnivoreStyle");
                else
                    entry.setStyle("birdOtherStyle");
            }
            else
            if (element.getType().equals(ElementType.PLANT)) {
                if (element.getFeedingClass() == null)
                    entry.setStyle("plantOtherStyle");
                else
                if (!element.getFeedingClass().equals(FeedingClass.NONE))
                    entry.setStyle("plantStyle");
                else
                    entry.setStyle("plantOtherStyle");
            }
            else
            if (element.getType().equals(ElementType.AMPHIBIAN)) {
                if (element.getFeedingClass() == null)
                    entry.setStyle("amphibianOtherStyle");
                else
                if (!element.getFeedingClass().equals(FeedingClass.NONE))
                    entry.setStyle("amphibianStyle");
                else
                    entry.setStyle("amphibianOtherStyle");
            }
            else
            if (element.getType().equals(ElementType.FISH)) {
                if (element.getFeedingClass() == null)
                    entry.setStyle("fishOtherStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.CARNIVORE))
                    entry.setStyle("fishCarnivoreStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.HERBIVORE))
                    entry.setStyle("fishHerbivoreStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.OMNIVORE))
                    entry.setStyle("fishOmnivoreStyle");
                else
                    entry.setStyle("fishOtherStyle");
            }
            else
            if (element.getType().equals(ElementType.INSECT)) {
                if (element.getFeedingClass() == null)
                    entry.setStyle("insectOtherStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.CARNIVORE))
                    entry.setStyle("insectCarnivoreStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.HERBIVORE))
                    entry.setStyle("insectHerbivoreStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.OMNIVORE))
                    entry.setStyle("insectOmnivoreStyle");
                else
                    entry.setStyle("insectOtherStyle");
            }
            else
            if (element.getType().equals(ElementType.REPTILE)) {
                if (element.getFeedingClass() == null)
                    entry.setStyle("reptileOtherStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.CARNIVORE))
                    entry.setStyle("reptileCarnivoreStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.HERBIVORE))
                    entry.setStyle("reptileHerbivoreStyle");
                else
                if (element.getFeedingClass().equals(FeedingClass.OMNIVORE))
                    entry.setStyle("reptileOmnivoreStyle");
                else
                    entry.setStyle("reptileOtherStyle");
            }
            else {
                entry.setStyle("otherStyle");
            }
        }
        else {
            entry.setStyle("otherStyle");
        }
        if (latitude == null || longitude == null) {
            Location location = inApp.getDBI().find(new Location(locationName));
            if (location.getLatitude() != null && location.getLongitude() != null) {
                if (!location.getLatitude().equals(Latitudes.NONE) && !location.getLongitude().equals(Longitudes.NONE)) {
                    entry.setLatitude(UtilsGps.getDecimalDegree(location.getLatitude(), location.getLatDegrees(), location.getLatMinutes(), location.getLatSeconds()));
                    entry.setLongitude(UtilsGps.getDecimalDegree(location.getLongitude(), location.getLonDegrees(), location.getLonMinutes(), location.getLonSeconds()));
                }
            }
            else {
                entry.setLatitude(0);
                entry.setLongitude(0);
            }
        }
        else
        if (latitude.equals(Latitudes.NONE) || longitude.equals(Longitudes.NONE)) {
            Location location = inApp.getDBI().find(new Location(locationName));
            if (location.getLatitude() != null && location.getLongitude() != null) {
                if (!location.getLatitude().equals(Latitudes.NONE) && !location.getLongitude().equals(Longitudes.NONE)) {
                    entry.setLatitude(UtilsGps.getDecimalDegree(location.getLatitude(), location.getLatDegrees(), location.getLatMinutes(), location.getLatSeconds()));
                    entry.setLongitude(UtilsGps.getDecimalDegree(location.getLongitude(), location.getLonDegrees(), location.getLonMinutes(), location.getLonSeconds()));
                }
            }
            else {
                entry.setLatitude(0);
                entry.setLongitude(0);
            }
        }
        else {
            entry.setLatitude(UtilsGps.getDecimalDegree(latitude, latDegrees, latMinutes, latSeconds));
            entry.setLongitude(UtilsGps.getDecimalDegree(longitude, lonDegrees, lonMinutes, lonSeconds));
        }
        return entry;
    }


    // GETTERS and SETTERS:
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
        if (details == null) details = "";
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

    public boolean isTimeUnknown() {
        return timeUnknown;
    }

    public void setTimeUnknown(boolean inTimeUnknown) {
        timeUnknown = inTimeUnknown;
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

}