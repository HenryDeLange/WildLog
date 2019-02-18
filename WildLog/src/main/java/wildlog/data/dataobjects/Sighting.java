package wildlog.data.dataobjects;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithKML;
import wildlog.data.dataobjects.interfaces.DataObjectWithTXT;
import wildlog.data.dataobjects.interfaces.DataObjectWithXML;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.UnitsTemperature;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.maps.kml.generator.KmlEntry;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.utils.UtilsTime;
import wildlog.xml.utils.UtilsXML;


public class Sighting extends SightingCore implements DataObjectWithHTML, DataObjectWithKML, DataObjectWithXML, DataObjectWithTXT {

    public Sighting() {
        super();
    }

    public Sighting(long inSightingCounter) {
        super(inSightingCounter);
    }

    public Sighting(String inElementName, String inLocationName, String inVisitName) {
        super(inElementName, inLocationName, inVisitName);
    }
    
    
    /**
     * Get the indicated name of the Element (if present, otherwise returns the primary name). <br>
     * 0 = Primary Key Name <br>
     * 1 = Other Name <br>
     * 2 = Scientific Name
     */
    public String getElementName(int inNameChoice) {
// FIXME: Hierdie is handig, maar kan baie stadig word binne groot FOR loops...
        String primaryName = super.getElementName();
        if (inNameChoice == 0) {
            return primaryName;
        }
        else
        if (inNameChoice == 1) {
            // Other name
            Element element = WildLogApp.getApplication().getDBI().findElement(primaryName, Element.class);
            if (element.getOtherName() != null && !element.getOtherName().isEmpty()) {
                return element.getOtherName();
            }
        }
        else 
        if (inNameChoice == 2) {
            // Scientific name
            Element element = WildLogApp.getApplication().getDBI().findElement(primaryName, Element.class);
            if (element.getScientificName()!= null && !element.getScientificName().isEmpty()) {
                return element.getScientificName();
            }
        }
        return primaryName;
    }


    /**
     * Return the Path representing the folder names where the files for this sighting should be stored.
     * This path is relative to the normal prefix, such as 'Files/Images/Observation'
     * and does not include the filename.
     * @return
     */
    public Path toPath() {
        if (!WildLogApp.getApplication().getWildLogOptions().isUseIndividualsInSightingPath()) {
            return Paths.get(locationName, visitName, elementName);
        }
        else {
            return Paths.get(locationName, visitName, elementName, Integer.toString(numberOfElements));
        }
    }
    
    public String getCustomFileName(LocalDateTime inFirstFileDate, LocalDateTime inCurrentFileDate) {
        LocalDateTime finalCurrentFileDate;
        if (date != null) {
            LocalDateTime sightingDate = UtilsTime.getLocalDateTimeFromDate(date);
            if (inFirstFileDate != null && inCurrentFileDate != null) {
                TemporalAmount dateAdjustment = Duration.between(inFirstFileDate, inCurrentFileDate);
                finalCurrentFileDate = sightingDate.plus(dateAdjustment);
            }
            else {
                finalCurrentFileDate = sightingDate;
            }
        }
        else {
            finalCurrentFileDate = LocalDateTime.now();
        }
        return UtilsTime.WL_DATE_FORMATTER_FOR_FILES_CAMERATRAP_TIMESTAMP.format(finalCurrentFileDate);
    }

    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, boolean inIsSummary, 
            WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        StringBuilder htmlSighting = new StringBuilder("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
        htmlSighting.append("<title>Sightings ID: ").append(sightingCounter).append("</title></head>");
        htmlSighting.append("<body bgcolor='#EEEAD3'>");
        htmlSighting.append("<table bgcolor='#EEEAD3' width='100%'>");
        htmlSighting.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        htmlSighting.append(getHtmlContent(inIncludeImages, inIsSummary, inApp, inExportType, inProgressbarTask));
        htmlSighting.append("</td></tr>");
        htmlSighting.append("</table>");
        htmlSighting.append("<br/>");
        htmlSighting.append("</body>");
        return htmlSighting.toString();
    }

    private String getHtmlContent(boolean inIncludeImages, boolean inIsSummary, WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        StringBuilder htmlSighting = new StringBuilder(1000);
        htmlSighting.append("<b><u>Observation ID: ").append(UtilsHTML.formatObjectAsString(sightingCounter)).append("</u></b>");
        htmlSighting.append("<br/>");
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Creature:</b><br/>", elementName, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Place:</b><br/>", locationName, true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Period:</b><br/>", visitName, true);
        }
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Date:</b><br/>", UtilsHTML.formatDateAsString(date, true), true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Date and Time Accuracy:</b><br/>", timeAccuracy, true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Time of Day:</b><br/>", timeOfDay, true);
        }
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Latitude:</b><br/>", UtilsGPS.getLatitudeString(this), true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Longitude:</b><br/>", UtilsGPS.getLongitudeString(this), true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>GPS Accuracy:</b><br/>", gpsAccuracy, true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>GPS Accuracy Value:</b><br/>", gpsAccuracyValue, true);
        }
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Certainty:</b><br/>", certainty, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Life Status:</b><br/>", lifeStatus, true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Evidence:</b><br/>", sightingEvidence, true);
        }
        if (numberOfElements > 0) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Number of Creatures:</b><br/>", numberOfElements, true);
        }
        UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Details:</b><br/>", details, true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Gender:</b><br/>", sex, true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Age:</b><br/>", age, true);
            if (moonPhase >= 0) {
                UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Moon Phase:</b><br/>", moonPhase + " % Full", true);
            }
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Moonlight:</b><br/>", moonlight, true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Weather:</b><br/>", weather, true);
            if (unitsTemperature != null && !UnitsTemperature.NONE.equals(unitsTemperature)) {
                UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Temperature:</b><br/>", temperature + " " + unitsTemperature, true);
            }
            if (durationMinutes > 0 || durationSeconds > 0) {
                UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Duration:</b><br/>", durationMinutes + " minutes, " + durationSeconds + " seconds", true);
            }
        }
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>View Rating:</b><br/>", viewRating, true);
        }
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlSighting, "<br/><b>Tag:</b><br/>", tag, true);
        }
        if (inIncludeImages) {
            StringBuilder filesString = new StringBuilder(500);
            List<WildLogFile> files = inApp.getDBI().listWildLogFiles(getWildLogFileID(), null, WildLogFile.class);
            for (int t = 0; t < files.size(); t++) {
                filesString.append(files.get(t).toHTML(inExportType));
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress((int)(((double)t/files.size())*99));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                            + " " + inProgressbarTask.getProgress() + "%");
                }
            }
            if (filesString.length() > 0) {
                htmlSighting.append("<br/>");
                htmlSighting.append("<br/><b>Photos:</b><br/>").append(filesString);
            }
        }
        return htmlSighting.toString();
    }

    @Override
    public KmlEntry toKML(int inID, WildLogApp inApp) {
        KmlEntry entry = new KmlEntry();
        entry.setId(inID);
        entry.setName(elementName);
        entry.setDescription(toHTML(false, true, true, inApp, UtilsHTMLExportTypes.ForKML, null));
        Element element = inApp.getDBI().findElement(elementName, Element.class);
        if (element.getType() != null) {
            if (element.getType().equals(ElementType.MAMMAL)) {
                if (element.getFeedingClass() == null) {
                    entry.setStyle("animalOtherStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.CARNIVORE)) {
                    entry.setStyle("animalCarnivoreStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.HERBIVORE)) {
                    entry.setStyle("animalHerbivoreStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.OMNIVORE)) {
                    entry.setStyle("animalOmnivoreStyle");
                }
                else {
                    entry.setStyle("animalOtherStyle");
                }
            }
            else
            if (element.getType().equals(ElementType.BIRD)) {
                if (element.getFeedingClass() == null) {
                    entry.setStyle("birdOtherStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.CARNIVORE)) {
                    entry.setStyle("birdCarnivoreStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.HERBIVORE)) {
                    entry.setStyle("birdHerbivoreStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.OMNIVORE)) {
                    entry.setStyle("birdOmnivoreStyle");
                }
                else {
                    entry.setStyle("birdOtherStyle");
                }
            }
            else
            if (element.getType().equals(ElementType.PLANT)) {
                if (element.getFeedingClass() == null) {
                    entry.setStyle("plantOtherStyle");
                }
                else
                if (!element.getFeedingClass().equals(FeedingClass.NONE)) {
                    entry.setStyle("plantStyle");
                }
                else {
                    entry.setStyle("plantOtherStyle");
                }
            }
            else
            if (element.getType().equals(ElementType.AMPHIBIAN)) {
                if (element.getFeedingClass() == null) {
                    entry.setStyle("amphibianOtherStyle");
                }
                else
                if (!element.getFeedingClass().equals(FeedingClass.NONE)) {
                    entry.setStyle("amphibianStyle");
                }
                else {
                    entry.setStyle("amphibianOtherStyle");
                }
            }
            else
            if (element.getType().equals(ElementType.FISH)) {
                if (element.getFeedingClass() == null) {
                    entry.setStyle("fishOtherStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.CARNIVORE)) {
                    entry.setStyle("fishCarnivoreStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.HERBIVORE)) {
                    entry.setStyle("fishHerbivoreStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.OMNIVORE)) {
                    entry.setStyle("fishOmnivoreStyle");
                }
                else {
                    entry.setStyle("fishOtherStyle");
                }
            }
            else
            if (element.getType().equals(ElementType.INSECT)) {
                if (element.getFeedingClass() == null) {
                    entry.setStyle("insectOtherStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.CARNIVORE)) {
                    entry.setStyle("insectCarnivoreStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.HERBIVORE)) {
                    entry.setStyle("insectHerbivoreStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.OMNIVORE)) {
                    entry.setStyle("insectOmnivoreStyle");
                }
                else {
                    entry.setStyle("insectOtherStyle");
                }
            }
            else
            if (element.getType().equals(ElementType.REPTILE)) {
                if (element.getFeedingClass() == null) {
                    entry.setStyle("reptileOtherStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.CARNIVORE)) {
                    entry.setStyle("reptileCarnivoreStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.HERBIVORE)) {
                    entry.setStyle("reptileHerbivoreStyle");
                }
                else
                if (element.getFeedingClass().equals(FeedingClass.OMNIVORE)) {
                    entry.setStyle("reptileOmnivoreStyle");
                }
                else {
                    entry.setStyle("reptileOtherStyle");
                }
            }
            else {
                entry.setStyle("otherStyle");
            }
        }
        else {
            entry.setStyle("otherStyle");
        }
        if (latitude == null || longitude == null) {
            Location location = inApp.getDBI().findLocation(locationName, Location.class);
            if (location.getLatitude() != null && location.getLongitude() != null) {
                if (!location.getLatitude().equals(Latitudes.NONE) && !location.getLongitude().equals(Longitudes.NONE)) {
                    entry.setLatitude(UtilsGPS.getDecimalDegree(location.getLatitude(), location.getLatDegrees(), location.getLatMinutes(), location.getLatSeconds()));
                    entry.setLongitude(UtilsGPS.getDecimalDegree(location.getLongitude(), location.getLonDegrees(), location.getLonMinutes(), location.getLonSeconds()));
                }
            }
            else {
                entry.setLatitude(0);
                entry.setLongitude(0);
            }
        }
        else
        if (latitude.equals(Latitudes.NONE) || longitude.equals(Longitudes.NONE)) {
            Location location = inApp.getDBI().findLocation(locationName, Location.class);
            if (location.getLatitude() != null && location.getLongitude() != null) {
                if (!location.getLatitude().equals(Latitudes.NONE) && !location.getLongitude().equals(Longitudes.NONE)) {
                    entry.setLatitude(UtilsGPS.getDecimalDegree(location.getLatitude(), location.getLatDegrees(), location.getLatMinutes(), location.getLatSeconds()));
                    entry.setLongitude(UtilsGPS.getDecimalDegree(location.getLongitude(), location.getLonDegrees(), location.getLonMinutes(), location.getLonSeconds()));
                }
            }
            else {
                entry.setLatitude(0);
                entry.setLongitude(0);
            }
        }
        else {
            entry.setLatitude(UtilsGPS.getDecimalDegree(latitude, latDegrees, latMinutes, latSeconds));
            entry.setLongitude(UtilsGPS.getDecimalDegree(longitude, lonDegrees, lonMinutes, lonSeconds));
        }
        return entry;
    }

    @Override
    public String toXML(WildLogApp inApp, ProgressbarTask inProgressbarTask, boolean inIncludeSightings) {
        StringBuilder builder = new StringBuilder(700);
        builder.append("<Observation>");
        builder.append("<sightingCounter>").append(sightingCounter).append("</sightingCounter>");
        builder.append("<date>").append(UtilsHTML.formatDateAsString(date, true)).append("</date>");
        builder.append("<elementName><![CDATA[").append(elementName).append("]]></elementName>");
        builder.append("<locationName><![CDATA[").append(locationName).append("]]></locationName>");
        builder.append("<visitName><![CDATA[").append(visitName).append("]]></visitName>");
        builder.append("<timeOfDay>").append(timeOfDay).append("</timeOfDay>");
        builder.append("<timeAccuracy>").append(timeAccuracy).append("</timeAccuracy>");
        builder.append("<certainty>").append(certainty).append("</certainty>");
        builder.append("<numberOfElements>").append(numberOfElements).append("</numberOfElements>");
        builder.append("<details><![CDATA[").append(details).append("]]></details>");
        builder.append("<sightingEvidence>").append(sightingEvidence).append("</sightingEvidence>");
        builder.append("<moonPhase>").append(moonPhase).append("</moonPhase>");
        builder.append("<moonlight>").append(moonlight).append("</moonlight>");
        builder.append("<gender>").append(sex).append("</gender>");
        builder.append("<lifeStatus>").append(lifeStatus).append("</lifeStatus>");
        builder.append("<tag><![CDATA[").append(tag).append("]]></tag>");
        builder.append("<temperature>").append(temperature).append("</temperature>");
        builder.append("<unitsTemperature>").append(unitsTemperature).append("</unitsTemperature>");
        builder.append("<durationMinutes>").append(durationMinutes).append("</durationMinutes>");
        builder.append("<durationSeconds>").append(durationSeconds).append("</durationSeconds>");
        builder.append("<weather>").append(weather).append("</weather>");
        builder.append("<viewRating>").append(viewRating).append("</viewRating>");
        builder.append("<age>").append(age).append("</age>");
        builder.append(UtilsXML.getGPSInfoAsXML(this));
        StringBuilder filesString = new StringBuilder(300);
        List<WildLogFile> files = inApp.getDBI().listWildLogFiles(getWildLogFileID(), null, WildLogFile.class);
        int counter = 0;
        for (WildLogFile file : files) {
            filesString.append(UtilsXML.getWildLogFileInfoAsXML(file));
            if (inProgressbarTask != null) {
                inProgressbarTask.setTaskProgress(5 + (int)((counter/(double)files.size())*10));
                inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                        + " " + inProgressbarTask.getProgress() + "%");
                counter++;
            }
        }
        builder.append("<Files>").append(filesString).append("</Files>");
        builder.append("</Observation>");
        return builder.toString();
    }
    
    @Override
    public String toTXT(WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        StringBuilder builder = new StringBuilder(50);
        builder.append("Observation ").append(sightingCounter).append(" details:").append(System.lineSeparator());
        if (date != null) {
            builder.append("Date = ").append(UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(UtilsTime.getLocalDateTimeFromDate(date))).append(System.lineSeparator());
        }
        else {
            builder.append("Date = ").append(System.lineSeparator());
        }
        builder.append("Creature = ").append(elementName).append(System.lineSeparator());
        builder.append("Place = ").append(locationName).append(System.lineSeparator());
        builder.append("Period = ").append(visitName).append(System.lineSeparator());
        return builder.toString();
    }

}