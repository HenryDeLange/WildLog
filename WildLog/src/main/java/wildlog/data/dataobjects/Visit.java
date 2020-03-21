package wildlog.data.dataobjects;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithTXT;
import wildlog.data.dataobjects.interfaces.DataObjectWithXML;
import wildlog.data.enums.system.WildLogExtraDataFieldTypes;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsTime;
import wildlog.utils.WildLogApplicationTypes;
import wildlog.xml.utils.UtilsXML;


public class Visit extends VisitCore implements DataObjectWithHTML, DataObjectWithXML, DataObjectWithTXT {

    public Visit() {
        super();
    }

    public Visit(long inID, String inName) {
        super(inID, inName);
    }

    public Visit(long inID, String inName, long inLocationID) {
        super(inID, inName, inLocationID);
    }


    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, boolean inIsSummary, 
            WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        StringBuilder html = new StringBuilder("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
        html.append("<title>Periods: ").append(name).append("</title></head>");
        html.append("<body bgcolor='#E6E4F0'>");
        html.append("<table bgcolor='#E6E4F0' width='100%'>");
        html.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        html.append("<b><u>").append(name).append("</u></b>");
        html.append("<br/>");
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Start Date:</b><br/>", UtilsHTML.formatDateAsString(startDate, false), true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>End Date:</b><br/>", UtilsHTML.formatDateAsString(endDate, false), true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Type of Visit:</b><br/>", type, true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Notes:</b><br/>", description, true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Game Watching:</b><br/>", gameWatchingIntensity, true);
        }
        if (!inIsSummary) {
            List<ExtraData> lstExtraData = inApp.getDBI().listExtraDatas(WildLogExtraDataFieldTypes.USER, id, ExtraData.class);
            if (!lstExtraData.isEmpty()) {
                html.append("<br/><hr/>");
                for (ExtraData extraData : lstExtraData) {
                    if (extraData.getDataKey() != null && !extraData.getDataKey().isEmpty()) {
                        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>" + extraData.getDataKey() + ":</b><br/>", extraData.getDataValue(), true);
                    }
                }
            }
        }
        if (!inIsSummary && (WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER
                && WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_REMOTE)) {
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
            List<Sighting> sightings = inApp.getDBI().listSightings(0, 0, id, true, Sighting.class);
            int counter = 0;
            for (int t = 0; t < sightings.size(); t++) {
                html.append("<br/>").append(sightings.get(t).toHTML(inIsRecursive, inIncludeImages, inIsSummary, inApp, inExportType, null)).append("<br/>");
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress(5 + (int)(((double)counter/sightings.size())*(94)));
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
    public String toXML(WildLogApp inApp, ProgressbarTask inProgressbarTask, boolean inIncludeSightings) {
        StringBuilder builder = new StringBuilder(300);
        builder.append("<Period>");
        builder.append("<name><![CDATA[").append(name).append("]]></name>");
        builder.append("<startDate>").append(UtilsHTML.formatDateAsString(startDate, false)).append("</startDate>");
        builder.append("<endDate>").append(UtilsHTML.formatDateAsString(endDate, false)).append("</endDate>");
        builder.append("<notes><![CDATA[").append(description).append("]]></notes>");
        builder.append("<gameWatchingIntensity>").append(gameWatchingIntensity).append("</gameWatchingIntensity>");
        builder.append("<type>").append(type).append("</type>");
        builder.append("<locationName>").append(cachedLocationName).append("</locationName>");
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
            List<Sighting> sightings = inApp.getDBI().listSightings(0, 0, id, true, Sighting.class);
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
        List<Sighting> lstSightingsToUse = inApp.getDBI().listSightings(0, 0, id, true, Sighting.class);
        builder.append("The following Creatures were observed during ").append(name).append(":").append(System.lineSeparator());
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