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
import wildlog.utils.UtilsTime;
import wildlog.utils.WildLogApplicationTypes;
import wildlog.xml.utils.UtilsXML;


public class Element extends ElementCore implements DataObjectWithHTML, DataObjectWithXML, DataObjectWithTXT {

    public Element() {
        super();
    }

    public Element(long inID, String inPrimaryName) {
        super(inID, inPrimaryName);
    }

    
// TODO: Ek is nog nie seker hoe veilig dit is om equals te overwrite nie, so vir nou doen ek dit eers nie op die ElementCore nie, en nie vir ander tipes nie... Kan dalk later vir almal doen.
    
    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof Element) {
            return id == ((Element) inObject).getID();
        }
        return (this == inObject);
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    
    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, boolean inIsSummary, 
            WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        StringBuilder html = new StringBuilder("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
        html.append("<title>Creature: ").append(primaryName).append("</title></head>");
        html.append("<body bgcolor='#E3F0E3'>");
        html.append("<table bgcolor='#E3F0E3' width='100%'>");
        html.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        html.append("<b><u>").append(primaryName).append("</u></b>");
        html.append("<br/>");
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Other Name:</b><br/>", otherName, true);
        }
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Scientific Name:</b><br/>", scientificName, true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Reference ID:</b><br/>", referenceID, true);
        }
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Creature Type:</b><br/>", type, true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Feeding Class:</b><br/>", feedingClass, true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Threat Status:</b><br/>", endangeredStatus, true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Identification:</b><br/>", diagnosticDescription, true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Habitat:</b><br/>", description, true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Distribution:</b><br/>", distribution, true);
        UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Behaviour:</b><br/>", behaviourDescription, true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Food/Nutrition:</b><br/>", nutrition, true);
        }
        if (!inIsSummary && (WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER)) {
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
                UtilsHTML.appendIfNotNullNorEmpty(html, "<br/><b>Photos:</b><br/>", filesString);
            }
        }
        if (inIsRecursive) {
            html.append("<br/>");
            html.append("</td></tr>");
            html.append("<tr><td>");
            List<Sighting> sightings = inApp.getDBI().listSightings(id, 0, 0, true, Sighting.class);
            int counter = 0;
            for (Sighting temp : sightings) {
                html.append("<br/>").append(temp.toHTML(false, inIncludeImages, inIsSummary, inApp, inExportType, null));
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
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(1);
            inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                    + " " + inProgressbarTask.getProgress() + "%");
        }
        StringBuilder builder = new StringBuilder(500);
        builder.append("<Creature>");
        builder.append("<primaryName><![CDATA[").append(primaryName).append("]]></primaryName>");
        builder.append("<otherName><![CDATA[").append(otherName).append("]]></otherName>");
        builder.append("<scientificName><![CDATA[").append(scientificName).append("]]></scientificName>");
        builder.append("<referenceID><![CDATA[").append(referenceID).append("]]></referenceID>");
        builder.append("<type>").append(type).append("</type>");
        builder.append("<feedingClass>").append(feedingClass).append("</feedingClass>");
        builder.append("<endangeredStatus>").append(endangeredStatus).append("</endangeredStatus>");
        builder.append("<nutrition><![CDATA[").append(nutrition).append("]]></nutrition>");
        builder.append("<diagnosticDescription><![CDATA[").append(diagnosticDescription).append("]]></diagnosticDescription>");
        builder.append("<description><![CDATA[").append(description).append("]]></description>");
        builder.append("<distribution><![CDATA[").append(distribution).append("]]></distribution>");
        builder.append("<behaviourDescription><![CDATA[").append(behaviourDescription).append("]]></behaviourDescription>");
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(5);
        }
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
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(15);
            inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                            + " " + inProgressbarTask.getProgress() + "%");
        }
        if (inIncludeSightings) {
            StringBuilder sightingString = new StringBuilder(1024);
            List<Sighting> sightings = inApp.getDBI().listSightings(id, 0, 0, true, Sighting.class);
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
        builder.append("</Creature>");
        return builder.toString();
    }

    @Override
    public String toTXT(WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        StringBuilder builder = new StringBuilder(50);
        List<Sighting> lstSightingsToUse = inApp.getDBI().listSightings(id, 0, 0, true, Sighting.class);
        builder.append(primaryName).append(" was observed at the following Places:").append(System.lineSeparator());
        Set<String> uniqueNames = new HashSet<>();
        for (Sighting tempsighting : lstSightingsToUse) {
            if (!uniqueNames.contains(tempsighting.getCachedLocationName())) {
                builder.append(tempsighting.getCachedLocationName()).append(System.lineSeparator());
                uniqueNames.add(tempsighting.getCachedLocationName());
            }
        }
        return builder.toString();
    }
    
}