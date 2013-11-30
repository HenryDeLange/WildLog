package wildlog.data.dataobjects;

import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.WildLogPaths;


public class Visit extends VisitCore implements DataObjectWithHTML {

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
                    inProgressbarTask.setTaskProgress((int)(((double)counter/sightings.size())*(94)));
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
    public String getExportPrefix() {
        return WildLogPaths.WildLogPathPrefixes.PREFIX_VISIT.toString();
    }

    @Override
    public String getDisplayName() {
        return name;
    }

}