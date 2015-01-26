package wildlog.data.dataobjects;

import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithXML;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.xml.utils.UtilsXML;


public class Visit extends VisitCore implements DataObjectWithHTML, DataObjectWithXML {

    public Visit() {
        super();
    }

    public Visit(String inName) {
        super(inName);
    }

    public Visit(String inName, String inLocationName) {
        super(inName, inLocationName);
    }


    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        StringBuilder htmlVisit = new StringBuilder("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><title>Periods: " + name + "</title></head>");
        htmlVisit.append("<body bgcolor='#E6E4F0'>");
        htmlVisit.append("<table bgcolor='#E6E4F0' width='100%'>");
        htmlVisit.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        htmlVisit.append("<b><u>").append(name).append("</u></b>");
        htmlVisit.append("<br/>");
        UtilsHTML.appendIfNotNullNorEmpty(htmlVisit, "<br/><b>Start Date:</b><br/> ", UtilsHTML.formatDateAsString(startDate, false), true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlVisit, "<br/><b>End Date:</b><br/> ", UtilsHTML.formatDateAsString(endDate, false), true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlVisit, "<br/><b>Game Watching:</b><br/> ", gameWatchingIntensity, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlVisit, "<br/><b>Type of Visit:</b><br/> ", type, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlVisit, "<br/><b>Description:</b><br/> ", description, true);
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
                htmlVisit.append("<br/>");
                htmlVisit.append("<br/><b>Photos:</b><br/>").append(filesString);
            }
        }
        if (inIsRecursive) {
            htmlVisit.append("<br/>");
            htmlVisit.append("</td></tr>");
            htmlVisit.append("<tr><td>");
            Sighting tempSighting = new Sighting();
            tempSighting.setVisitName(name);
            List<Sighting> sightings = inApp.getDBI().list(tempSighting);
            int counter = 0;
            for (int t = 0; t < sightings.size(); t++) {
                htmlVisit.append("<br/>").append(sightings.get(t).toHTML(inIsRecursive, inIncludeImages, inApp, inExportType, null)).append("<br/>");
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress(5 + (int)(((double)counter/sightings.size())*(94)));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                            + " " + inProgressbarTask.getProgress() + "%");
                    counter++;
                }
            }
        }
        htmlVisit.append("</td></tr>");
        htmlVisit.append("</table>");
        htmlVisit.append("<br/>");
        htmlVisit.append("</body>");
        return htmlVisit.toString();
    }

    @Override
    public String toFancyHTML(WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toXML(WildLogApp inApp, ProgressbarTask inProgressbarTask, boolean inIsRecursive) {
        StringBuilder builder = new StringBuilder(300);
        builder.append("<Period>");
        builder.append("<name>").append(name).append("</name>");
        builder.append("<startDate>").append(UtilsHTML.formatDateAsString(startDate, false)).append("</startDate>");
        builder.append("<endDate>").append(UtilsHTML.formatDateAsString(endDate, false)).append("</endDate>");
        builder.append("<description>").append(description).append("</description>");
        builder.append("<gameWatchingIntensity>").append(gameWatchingIntensity).append("</gameWatchingIntensity>");
        builder.append("<type>").append(type).append("</type>");
        builder.append("<locationName>").append(locationName).append("</locationName>");
        StringBuilder filesString = new StringBuilder(200);
        List<WildLogFile> files = inApp.getDBI().list(new WildLogFile(getWildLogFileID()));
        for (int t = 0; t < files.size(); t++) {
            filesString.append(UtilsXML.getWildLogFileInfoAsXML(files.get(t)));
        }
        builder.append("<Files>").append(filesString).append("</Files>");
        if (inIsRecursive) {
//            builder.append("<br/>");
//            builder.append("</td></tr>");
//            builder.append("<tr><td>");
//            Sighting tempSighting = new Sighting();
//            tempSighting.setElementName(primaryName);
//            List<Sighting> sightings = inApp.getDBI().list(tempSighting);
//            int counter = 0;
//            for (Sighting temp : sightings) {
//                builder.append("<br/>").append(temp.toHTML(false, inIncludeImages, inApp, inExportType, null));
//                if (inProgressbarTask != null) {
//                    inProgressbarTask.setTaskProgress(5 + (int)(((double)counter/sightings.size())*(94)));
//                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
//                            + " " + inProgressbarTask.getProgress() + "%");
//                    counter++;
//                }
//            }
        }
        builder.append("</Period>");
        return builder.toString();
    }

}