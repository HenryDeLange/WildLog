package wildlog.data.dataobjects;

import KmlGenerator.objects.KmlEntry;
import java.util.Date;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithKML;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.AreaType;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Moonlight;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.Weather;
import wildlog.utils.LatLonConverter;
import wildlog.utils.UtilsHTML;

// Foundation for the Sighting class
public class Sighting implements Comparable<Sighting>, DataObjectWithHTML, DataObjectWithKML {
    private Date date; // must include time
//    private Element element;
//    private Location location;
    private ActiveTimeSpesific timeOfDay; // General description of the time (bv early morning, night, ens) Predefined
    private Weather weather; // Predefined set of possible values
    private AreaType areaType; // In what 'mini-habitat' was the element seen (bv river, open patch, ens) Predefined
    private ViewRating viewRating; // How close, long, ens the element was seen
    private Certainty certainty; // How sure you are that it was identified correctly
    private int numberOfElements; // How many where present at sighting
    private String details;
//    private List<Foto> fotos; // The difference between this and the Element foto is that Element fotos are the "good" ones, these are more foto records...
    private Latitudes latitude;
    private int latDegrees;
    private int latMinutes;
    //private int latSeconds;  // Old field not used anymore
    private float latSecondsFloat;
    private Longitudes longitude;
    private int lonDegrees;
    private int lonMinutes;
    //private int lonSeconds;  // Old field not used anymore
    private float lonSecondsFloat;
    //private String subArea;
    private SightingEvidence sightingEvidence;
    private long sightingCounter;
    private String elementName;
    private String locationName;
    private String visitName;
    private int moonPhase;
    private Moonlight moonlight;


    // CONSTRUCTORS:
    public Sighting() {
    }

    public Sighting(long inSightingCounter) {
        sightingCounter = inSightingCounter;
    }

//    public Sighting(Date inDate, Element inElement, Location inLocation, long inSightingCounter) {
//        date = inDate;
//        element = inElement;
//        location = inLocation;
//        sightingCounter = inSightingCounter;
//    }

    // METHIDS:
    @Override
    public String toString() {
        return locationName + " (" + date.getDate() + "-" + (date.getMonth()+1) + "-" + (date.getYear()+1900) + ") " + elementName + " [" + sightingCounter + "]";
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
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp) {
        StringBuilder fotoString = new StringBuilder();
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile("SIGHTING-" + sightingCounter));
        for (int t = 0; t < fotos.size(); t++) {
            fotoString.append(fotos.get(t).toHTML());
        }
        StringBuilder htmlSighting = new StringBuilder("<H2>Sighting</H2>");
        htmlSighting.append("<b>Date:</b> ").append(UtilsHTML.formatDate(date, true));
        htmlSighting.append("<br/><b>Element:</b> ").append(elementName);
        htmlSighting.append("<br/><b>Location:</b> ").append(UtilsHTML.formatString(locationName));
        htmlSighting.append("<br/>");
        htmlSighting.append("<br/><b>Time of Day:</b> ").append(UtilsHTML.formatString(timeOfDay));
        htmlSighting.append("<br/><b>Weather:</b> ").append(UtilsHTML.formatString(weather));
        htmlSighting.append("<br/><b>Area Type:</b> ").append(UtilsHTML.formatString(areaType));
        htmlSighting.append("<br/><b>View Rating:</b> ").append(UtilsHTML.formatString(viewRating));
        htmlSighting.append("<br/><b>Certainty:</b> ").append(UtilsHTML.formatString(certainty));
        htmlSighting.append("<br/><b>Number of Creatures:</b> ").append(UtilsHTML.formatString(numberOfElements));
        htmlSighting.append("<br/><b>Moon Phase:</b> ").append(UtilsHTML.formatString(moonPhase)).append(" % Full");
        htmlSighting.append("<br/><b>Moonlight:</b> ").append(UtilsHTML.formatString(moonlight));
        htmlSighting.append("<br/><b>Details:</b> ").append(UtilsHTML.formatString(details));
        htmlSighting.append("<br/><b>Latitude:</b> ").append(latitude).append(" ").append(latDegrees).append(" ").append(latMinutes).append(" ").append(latSecondsFloat);
        htmlSighting.append("<br/><b>Longitude:</b> ").append(longitude).append(" ").append(lonDegrees).append(" ").append(lonMinutes).append(" ").append(lonSecondsFloat);
        htmlSighting.append("<br/><b>Sighting ID:</b> ").append(UtilsHTML.formatString(sightingCounter));
        if (inIncludeImages)
            htmlSighting.append("<br/><b>Photos:</b><br/>").append(fotoString);
        return htmlSighting.toString();
    }

    @Override
    public KmlEntry toKML(int inID, WildLogApp inApp) {
        KmlEntry entry = new KmlEntry();
        entry.setId(inID);
        entry.setName(elementName);
        entry.setDescription(this.toHTML(false, true, inApp));
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
                    entry.setLatitude(LatLonConverter.getDecimalDegree(location.getLatitude(), location.getLatDegrees(), location.getLatMinutes(), location.getLatSecondsFloat()));
                    entry.setLongitude(LatLonConverter.getDecimalDegree(location.getLongitude(), location.getLonDegrees(), location.getLonMinutes(), location.getLonSecondsFloat()));
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
                    entry.setLatitude(LatLonConverter.getDecimalDegree(location.getLatitude(), location.getLatDegrees(), location.getLatMinutes(), location.getLatSecondsFloat()));
                    entry.setLongitude(LatLonConverter.getDecimalDegree(location.getLongitude(), location.getLonDegrees(), location.getLonMinutes(), location.getLonSecondsFloat()));
                }
            }
            else {
                entry.setLatitude(0);
                entry.setLongitude(0);
            }
        }
        else {
            entry.setLatitude(LatLonConverter.getDecimalDegree(latitude, latDegrees, latMinutes, latSecondsFloat));
            entry.setLongitude(LatLonConverter.getDecimalDegree(longitude, lonDegrees, lonMinutes, lonSecondsFloat));
        }
        return entry;
    }


    // GETTERS:
    public Date getDate() {
        return date;
    }

//    public Element getElement() {
//        return element;
//    }

//    public Location getLocation() {
//        return location;
//    }

    public ActiveTimeSpesific getTimeOfDay() {
        return timeOfDay;
    }

    public Weather getWeather() {
        return weather;
    }

    public AreaType getAreaType() {
        return areaType;
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

//    @Override
//    public List<Foto> getFotos() {
//        if (fotos == null) fotos = new ArrayList<Foto>(1);
//        return fotos;
//    }

    public int getLatDegrees() {
        return latDegrees;
    }

    public int getLatMinutes() {
        return latMinutes;
    }

    public float getLatSecondsFloat() {
        return latSecondsFloat;
    }

    public Latitudes getLatitude() {
        return latitude;
    }

    public int getLonDegrees() {
        return lonDegrees;
    }

    public int getLonMinutes() {
        return lonMinutes;
    }

    public float getLonSecondsFloat() {
        return lonSecondsFloat;
    }

    public Longitudes getLongitude() {
        return longitude;
    }

//    public String getSubArea() {
//        if (subArea == null) subArea = "";
//        return subArea;
//    }

    public SightingEvidence getSightingEvidence() {
        return sightingEvidence;
    }

    public long getSightingCounter() {
        return sightingCounter;
    }

    // SETTERS:
    public void setDate(Date inDate) {
        date = inDate;
    }

//    public void setElement(Element inElement) {
//        element = inElement;
//    }
//
//    public void setLocation(Location inLocation) {
//        location = inLocation;
//    }

    public void setTimeOfDay(ActiveTimeSpesific inTimeOfDay) {
        timeOfDay = inTimeOfDay;
    }

    public void setWeather(Weather inWeather) {
        weather = inWeather;
    }

    public void setAreaType(AreaType inAreaType) {
        areaType = inAreaType;
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

//    @Override
//    public void setFotos(List<Foto> inFotos) {
//        fotos = inFotos;
//    }

    public void setLatDegrees(int inLatDegrees) {
        latDegrees = inLatDegrees;
    }

    public void setLatMinutes(int inLatMinutes) {
        latMinutes = inLatMinutes;
    }

    public void setLatSecondsFloat(float inLatSeconds) {
        latSecondsFloat = inLatSeconds;
    }

    public void setLatitude(Latitudes inLatitude) {
        latitude = inLatitude;
    }

    public void setLonDegrees(int inLonDegrees) {
        lonDegrees = inLonDegrees;
    }

    public void setLonMinutes(int inLonMinutes) {
        lonMinutes = inLonMinutes;
    }

    public void setLonSecondsFloat(float inLonSeconds) {
        lonSecondsFloat = inLonSeconds;
    }

    public void setLongitude(Longitudes inLongitude) {
        longitude = inLongitude;
    }

//    public void setSubArea(String inSubArea) {
//        subArea = inSubArea;
//    }

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
    
}