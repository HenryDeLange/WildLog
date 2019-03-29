package wildlog.data.dataobjects;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithKML;
import wildlog.data.dataobjects.interfaces.DataObjectWithTXT;
import wildlog.data.dataobjects.interfaces.DataObjectWithXML;
import wildlog.data.enums.WildLogUserTypes;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.maps.kml.generator.KmlEntry;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsTime;
import wildlog.xml.utils.UtilsXML;


public class Location extends LocationCore implements DataObjectWithHTML, DataObjectWithKML, DataObjectWithXML, DataObjectWithTXT {

    public Location() {
        super();
    }

    public Location(long inID, String inName) {
        super(inID, inName);
    }


    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, boolean inIsSummary, 
            WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        StringBuilder html = new StringBuilder("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
        html.append("<title>Place: ").append(name).append("</title></head>");
        html.append("<body bgcolor='E9EFF4'>");
        html.append("<table bgcolor='#E9EFF4' width='100%'>");
        html.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        html.append("<b><u>").append(name).append("</u></b>");
        html.append("<br/>");
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Description:</b><br/>", description, true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Latitude:</b><br/>", UtilsGPS.getLatitudeString(this), true);
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Longitude:</b><br/>", UtilsGPS.getLongitudeString(this), true);
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>GPS Accuracy:</b><br/>", gpsAccuracy, true);
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>GPS Accuracy Value:</b><br/>", gpsAccuracyValue, true);
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>General Rating:</b><br/>", rating, true);
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Wildlife Rating:</b><br/>", gameViewingRating, true);
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Habitat:</b><br/>", habitatType, true);
        }
        if (!inIsSummary && (WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.WILDLOG_MASTER || WildLogApp.WILDLOG_USER_TYPE == WildLogUserTypes.OWNER)) {
            html.append("<br/><hr/>");
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>ID:</b><br/>", id, true);
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Audit Time:</b><br/>", UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(
                    UtilsTime.getLocalDateTimeFromMilliseconds(auditTime)), true);
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Audit User:</b><br/>", auditUser, true);
        }
        if (inIncludeImages) {
            StringBuilder filesString = new StringBuilder(300);
            List<WildLogFile> files = inApp.getDBI().listWildLogFiles(getWildLogFileID(), null, WildLogFile.class);
            for (int t = 0; t < files.size(); t++) {
                filesString.append(files.get(t).toHTML(inExportType));
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress((int)(((double)t/files.size())*5));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                            + " " + inProgressbarTask.getProgress() + "%");
                }
            }
            if (filesString.length() > 0) {
                html.append("<br/>");
                html.append("<br/><b>Photos:</b><br/>").append(filesString);
            }
        }
        if (inIsRecursive) {
            html.append("<br/>");
            html.append("</td></tr>");
            html.append("<tr><td>");
            List<Visit> visits = inApp.getDBI().listVisits(null, id, null, true, Visit.class);
            int counter = 0;
            for (int t = 0; t < visits.size(); t++) {
                html.append("<br/>").append(visits.get(t).toHTML(inIsRecursive, inIncludeImages, inIsSummary, inApp, inExportType, null)).append("<br/>");
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress(5 + (int)(((double)counter/visits.size())*(94)));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                            + " " + inProgressbarTask.getProgress() + "%");
                    counter++;
                }
            }
        }
        html.append("</td></tr>");
        html.append("</table>");
        html.append("<br/>");
        html.append("</body>");
        return html.toString();
    }

    @Override
    public KmlEntry toKML(int inID, WildLogApp inApp) {
        KmlEntry entry = new KmlEntry();
        entry.setId(inID);
        entry.setName(name);
        entry.setDescription(toHTML(false, true, true, inApp, UtilsHTMLExportTypes.ForKML, null));
        entry.setStyle("locationStyle");
        entry.setLatitude(UtilsGPS.getDecimalDegree(latitude, latDegrees, latMinutes, latSeconds));
        entry.setLongitude(UtilsGPS.getDecimalDegree(longitude, lonDegrees, lonMinutes, lonSeconds));
        return entry;
    }

    @Override
    public String toXML(WildLogApp inApp, ProgressbarTask inProgressbarTask, boolean inIncludeSightings) {
        StringBuilder builder = new StringBuilder(500);
        builder.append("<Place>");
        builder.append("<name><![CDATA[").append(name).append("]]></name>");
        builder.append("<description><![CDATA[").append(description).append("]]></description>");
        builder.append("<rating>").append(rating).append("</rating>");
        builder.append("<gameViewingRating>").append(gameViewingRating).append("</gameViewingRating>");
        builder.append("<habitatType><![CDATA[").append(habitatType).append("]]></habitatType>");
        builder.append(UtilsXML.getGPSInfoAsXML(this));
        StringBuilder filesString = new StringBuilder(200);
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
        if (inIncludeSightings) {
            StringBuilder sightingString = new StringBuilder(1024);
            List<Sighting> sightings = inApp.getDBI().listSightings(0, id, 0, true, Sighting.class);
            counter = 0;
            for (Sighting temp : sightings) {
                sightingString.append(temp.toXML(inApp, null, false));
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress(5 + (int)(((double)counter/sightings.size())*(84)));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                            + " " + inProgressbarTask.getProgress() + "%");
                    counter++;
                }
            }
            builder.append("<Observations>").append(sightingString).append("</Observations>");
        }
        builder.append("</Place>");
        return builder.toString();
    }
    
    @Override
    public String toTXT(WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        StringBuilder builder = new StringBuilder(50);
        List<Sighting> lstSightingsToUse = inApp.getDBI().listSightings(0, id, 0, true, Sighting.class);
        builder.append("The following Creatures were observed at ").append(name).append(":").append(System.lineSeparator());
        Set<String> uniqueNames = new HashSet<>();
        for (Sighting tempsighting : lstSightingsToUse) {
            if (!uniqueNames.contains(tempsighting.getCachedElementName())) {
                builder.append(tempsighting.getCachedElementName()).append(System.lineSeparator());
                uniqueNames.add(tempsighting.getCachedElementName());
            }
        }
        return builder.toString();
    }

}