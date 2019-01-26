package wildlog.data.dataobjects;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithTXT;
import wildlog.data.dataobjects.interfaces.DataObjectWithXML;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.xml.utils.UtilsXML;


public class Visit extends VisitCore implements DataObjectWithHTML, DataObjectWithXML, DataObjectWithTXT {

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
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, boolean inIsSummary, 
            WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        StringBuilder htmlVisit = new StringBuilder("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
        htmlVisit.append("<title>Periods: ").append(name).append("</title></head>");
        htmlVisit.append("<body bgcolor='#E6E4F0'>");
        htmlVisit.append("<table bgcolor='#E6E4F0' width='100%'>");
        htmlVisit.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        htmlVisit.append("<b><u>").append(name).append("</u></b>");
        htmlVisit.append("<br/>");
        UtilsHTML.appendIfNotNullNorEmpty(htmlVisit, "<br/><b>Start Date:</b><br/>", UtilsHTML.formatDateAsString(startDate, false), true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlVisit, "<br/><b>End Date:</b><br/>", UtilsHTML.formatDateAsString(endDate, false), true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlVisit, "<br/><b>Type of Visit:</b><br/>", type, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlVisit, "<br/><b>Description:</b><br/>", description, true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlVisit, "<br/><b>Game Watching:</b><br/>", gameWatchingIntensity, true);
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
                htmlVisit.append("<br/>");
                htmlVisit.append("<br/><b>Photos:</b><br/>").append(filesString);
            }
        }
        if (inIsRecursive) {
            htmlVisit.append("<br/>");
            htmlVisit.append("</td></tr>");
            htmlVisit.append("<tr><td>");
            List<Sighting> sightings = inApp.getDBI().listSightings(0, null, null, name, false, Sighting.class);
            int counter = 0;
            for (int t = 0; t < sightings.size(); t++) {
                htmlVisit.append("<br/>").append(sightings.get(t).toHTML(inIsRecursive, inIncludeImages, inIsSummary, inApp, inExportType, null)).append("<br/>");
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
    public String toXML(WildLogApp inApp, ProgressbarTask inProgressbarTask, boolean inIncludeSightings) {
        StringBuilder builder = new StringBuilder(300);
        builder.append("<Period>");
        builder.append("<name><![CDATA[").append(name).append("]]></name>");
        builder.append("<startDate>").append(UtilsHTML.formatDateAsString(startDate, false)).append("</startDate>");
        builder.append("<endDate>").append(UtilsHTML.formatDateAsString(endDate, false)).append("</endDate>");
        builder.append("<description><![CDATA[").append(description).append("]]></description>");
        builder.append("<gameWatchingIntensity>").append(gameWatchingIntensity).append("</gameWatchingIntensity>");
        builder.append("<type>").append(type).append("</type>");
        builder.append("<locationName>").append(locationName).append("</locationName>");
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
            List<Sighting> sightings = inApp.getDBI().listSightings(0, null, null, name, false, Sighting.class);
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
        builder.append("</Period>");
        return builder.toString();
    }
    
    @Override
    public String toTXT(WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        StringBuilder builder = new StringBuilder(50);
        List<Sighting> lstSightingsToUse = inApp.getDBI().listSightings(0, null, null, name, false, Sighting.class);
        builder.append("The following Creatures were observed during ").append(name).append(":").append(System.lineSeparator());
        Set<String> uniqueNames = new HashSet<>();
        for (Sighting tempsighting : lstSightingsToUse) {
            if (!uniqueNames.contains(tempsighting.getElementName())) {
                builder.append(tempsighting.getElementName()).append(System.lineSeparator());
                uniqueNames.add(tempsighting.getElementName());
            }
        }
        return builder.toString();
    }

}