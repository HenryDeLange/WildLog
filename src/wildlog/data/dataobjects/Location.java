package wildlog.data.dataobjects;

import KmlGenerator.objects.KmlEntry;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithKML;
import wildlog.data.dataobjects.interfaces.DataObjectWithXML;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.xml.utils.UtilsXML;


public class Location extends LocationCore implements DataObjectWithHTML, DataObjectWithKML, DataObjectWithXML {

    public Location() {
        super();
    }

    public Location(String inName) {
        super(inName);
    }


    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, boolean inIsSummary, 
            WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        StringBuilder htmlLocation = new StringBuilder("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
        htmlLocation.append("<title>Place: ").append(name).append("</title></head>");
        htmlLocation.append("<body bgcolor='E9EFF4'>");
        htmlLocation.append("<table bgcolor='#E9EFF4' width='100%'>");
        htmlLocation.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        htmlLocation.append("<b><u>").append(name).append("</u></b>");
        htmlLocation.append("<br/>");
        UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Description:</b><br/> ", description, true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Latitude:</b><br/> ", UtilsGps.getLatitudeString(this), true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Longitude:</b><br/> ", UtilsGps.getLongitudeString(this), true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>GPS Accuracy:</b><br/> ", gpsAccuracy, true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>General Rating:</b><br/> ", rating, true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Wildlife Rating:</b><br/> ", gameViewingRating, true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Habitat:</b><br/> ", habitatType, true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Directions:</b><br/> ", directions, true);
            if (website != null) {
                if (website.length() > 0) {
                    UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Website:</b><br/> ", "<a href=\"" + website + "\">" + website + "</a>", true);
                }
                else {
                    UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Website:</b><br/> ", website, true);
                }
            }
            else {
                UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Website:</b><br/> ", website, true);
            }
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Email:</b><br/> ", email, true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Phone Number:</b><br/> ", contactNumbers, true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Catering:</b><br/> ", catering, true);
            UtilsHTML.appendIfNotNullNorEmpty(htmlLocation, "<br/><b>Accomodation:</b><br/> ", accommodationType, true);
        }
        if (inIncludeImages) {
            StringBuilder filesString = new StringBuilder(300);
            List<WildLogFile> files = inApp.getDBI().list(new WildLogFile(getWildLogFileID()));
            for (int t = 0; t < files.size(); t++) {
                filesString.append(files.get(t).toHTML(inExportType));
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress((int)(((double)t/files.size())*5));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                            + " " + inProgressbarTask.getProgress() + "%");
                }
            }
            if (filesString.length() > 0) {
                htmlLocation.append("<br/>");
                htmlLocation.append("<br/><b>Photos:</b><br/>").append(filesString);
            }
        }
        if (inIsRecursive) {
            htmlLocation.append("<br/>");
            htmlLocation.append("</td></tr>");
            htmlLocation.append("<tr><td>");
            Visit tempVisit = new Visit();
            tempVisit.setLocationName(name);
            List<Visit> visits = inApp.getDBI().list(tempVisit);
            int counter = 0;
            for (int t = 0; t < visits.size(); t++) {
                htmlLocation.append("<br/>").append(visits.get(t).toHTML(inIsRecursive, inIncludeImages, inIsSummary, inApp, inExportType, null)).append("<br/>");
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress(5 + (int)(((double)counter/visits.size())*(94)));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                            + " " + inProgressbarTask.getProgress() + "%");
                    counter++;
                }
            }
        }
        htmlLocation.append("</td></tr>");
        htmlLocation.append("</table>");
        htmlLocation.append("<br/>");
        htmlLocation.append("</body>");
        return htmlLocation.toString();
    }

    @Override
    public KmlEntry toKML(int inID, WildLogApp inApp) {
        KmlEntry entry = new KmlEntry();
        entry.setId(inID);
        entry.setName(name);
        entry.setDescription(toHTML(false, true, true, inApp, UtilsHTMLExportTypes.ForKML, null));
        entry.setStyle("locationStyle");
        entry.setLatitude(UtilsGps.getDecimalDegree(latitude, latDegrees, latMinutes, latSeconds));
        entry.setLongitude(UtilsGps.getDecimalDegree(longitude, lonDegrees, lonMinutes, lonSeconds));
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
        builder.append("<accommodationType>").append(accommodationType).append("</accommodationType>");
        builder.append("<catering>").append(catering).append("</catering>");
        builder.append("<contactNumbers><![CDATA[").append(contactNumbers).append("]]></contactNumbers>");
        builder.append("<website><![CDATA[").append(website).append("]]></website>");
        builder.append("<email><![CDATA[").append(email).append("]]></email>");
        builder.append("<directions><![CDATA[").append(directions).append("]]></directions>");
        builder.append(UtilsXML.getGPSInfoAsXML(this));
        StringBuilder filesString = new StringBuilder(200);
        List<WildLogFile> files = inApp.getDBI().list(new WildLogFile(getWildLogFileID()));
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
            List<Sighting> sightings = inApp.getDBI().list(new Sighting(null, name, null), false);
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

}