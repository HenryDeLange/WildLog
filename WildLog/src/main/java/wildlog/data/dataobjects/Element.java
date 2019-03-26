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


public class Element extends ElementCore implements DataObjectWithHTML, DataObjectWithXML, DataObjectWithTXT {

    public Element() {
        super();
    }

    public Element(long inID, String inPrimaryName) {
        super(inID, inPrimaryName);
    }


    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, boolean inIsSummary, 
            WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        StringBuilder htmlElement = new StringBuilder("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
        htmlElement.append("<title>Creature: ").append(primaryName).append("</title></head>");
        htmlElement.append("<body bgcolor='#E3F0E3'>");
        htmlElement.append("<table bgcolor='#E3F0E3' width='100%'>");
        htmlElement.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        htmlElement.append("<b><u>").append(primaryName).append("</u></b>");
        htmlElement.append("<br/>");
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Other Name:</b><br/>", otherName, true);
        }
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Scientific Name:</b><br/>", scientificName, true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Reference ID:</b><br/>", referenceID, true);
        }
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Creature Type:</b><br/>", type, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Feeding Class:</b><br/>", feedingClass, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Threat Status:</b><br/>", endangeredStatus, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Identification:</b><br/>", diagnosticDescription, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Habitat:</b><br/>", description, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Distribution:</b><br/>", distribution, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Behaviour:</b><br/>", behaviourDescription, true);
        if (!inIsSummary) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Food/Nutrition:</b><br/>", nutrition, true);
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
                htmlElement.append("<br/>");
                UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Photos:</b><br/>", filesString);
            }
        }
        if (inIsRecursive) {
            htmlElement.append("<br/>");
            htmlElement.append("</td></tr>");
            htmlElement.append("<tr><td>");
            List<Sighting> sightings = inApp.getDBI().listSightings(id, 0, 0, true, Sighting.class);
            int counter = 0;
            for (Sighting temp : sightings) {
                htmlElement.append("<br/>").append(temp.toHTML(false, inIncludeImages, inIsSummary, inApp, inExportType, null));
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress(5 + (int)(((double)counter/sightings.size())*(94)));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                            + " " + inProgressbarTask.getProgress() + "%");
                    counter++;
                }
            }
        }
        htmlElement.append("</td></tr>");
        htmlElement.append("</table>");
        htmlElement.append("<br/>");
        htmlElement.append("</body>");
        return htmlElement.toString();
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