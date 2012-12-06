package wildlog.data.dataobjects;

import KmlGenerator.objects.KmlEntry;
import java.util.Date;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithKML;
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
import wildlog.mapping.utils.UtilsGps;
import wildlog.html.utils.UtilsHTML;

public class Sighting extends DataObjectWithGPS implements Comparable<Sighting>, DataObjectWithHTML, DataObjectWithKML {
    private Date date; // must include time
    private boolean timeUnknown;
    private ActiveTimeSpesific timeOfDay;
    private Weather weather;
    private ViewRating viewRating;
    private Certainty certainty;
    private int numberOfElements;
    private String details;
    private SightingEvidence sightingEvidence;
    private long sightingCounter;
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
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp, UtilsHTML.ImageExportTypes inExportType) {
        StringBuilder fotoString = new StringBuilder();
        if (inIncludeImages) {
            List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile("SIGHTING-" + sightingCounter));
            for (int t = 0; t < fotos.size(); t++) {
                fotoString.append(fotos.get(t).toHTML(inExportType));
            }
        }
        StringBuilder htmlSighting = new StringBuilder("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><title>Sightings ID: " + sightingCounter + "</title></head>");
        htmlSighting.append("<body bgcolor='rgb(238,234,211)'>");
        htmlSighting.append("<table bgcolor='rgb(238,234,211)' width='100%'>");
        htmlSighting.append("<tr><td>");
        htmlSighting.append("<b><u>Sighting ID: ").append(UtilsHTML.formatString(sightingCounter)).append("</u></b>");
        htmlSighting.append("<br/>");
        htmlSighting.append("<br/><b>Date:</b> ").append(UtilsHTML.formatDate(date, true));
        htmlSighting.append("<br/><b>Creature:</b> ").append(elementName);
        htmlSighting.append("<br/><b>Location:</b> ").append(UtilsHTML.formatString(locationName));
        htmlSighting.append("<br/>");
        htmlSighting.append("<br/><b>Latitude:</b> ").append(latitude).append(" ").append(latDegrees).append(" ").append(latMinutes).append(" ").append(latSeconds);
        htmlSighting.append("<br/><b>Longitude:</b> ").append(longitude).append(" ").append(lonDegrees).append(" ").append(lonMinutes).append(" ").append(lonSeconds);
        htmlSighting.append("<br/><b>Time of Day:</b> ").append(UtilsHTML.formatString(timeOfDay));
        htmlSighting.append("<br/><b>Weather:</b> ").append(UtilsHTML.formatString(weather));
        htmlSighting.append("<br/><b>View Rating:</b> ").append(UtilsHTML.formatString(viewRating));
        htmlSighting.append("<br/><b>Certainty:</b> ").append(UtilsHTML.formatString(certainty));
        htmlSighting.append("<br/><b>Number of Creatures:</b> ").append(UtilsHTML.formatString(numberOfElements));
        htmlSighting.append("<br/><b>Moon Phase:</b> ").append(UtilsHTML.formatString(moonPhase)).append(" % Full");
        htmlSighting.append("<br/><b>Moonlight:</b> ").append(UtilsHTML.formatString(moonlight));
        htmlSighting.append("<br/><b>Details:</b> ").append(UtilsHTML.formatString(details));
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

}