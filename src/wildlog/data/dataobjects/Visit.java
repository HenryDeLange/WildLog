package wildlog.data.dataobjects;

import java.util.Date;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.VisitType;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.WildLogPaths;

public class Visit implements Comparable<Visit>, DataObjectWithHTML, DataObjectWithWildLogFile {
    public static final String WILDLOGFILE_ID_PREFIX = "VISIT-";
    private String name; // Used as index (ID)
    private Date startDate;
    private Date endDate;
    private String description;
    private GameWatchIntensity gameWatchingIntensity;
    private VisitType type;
    private String locationName;

    // CONSTRUCTORS:
    public Visit() {
    }

    public Visit(String inName) {
        name = inName;
    }

    // METHODS:
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Visit inVisit) {
        if (inVisit != null) {
            if (name != null && inVisit.getName() != null) {
                return(name.compareToIgnoreCase(inVisit.getName()));
            }
        }
        return 0;
    }

    @Override
    public String getWildLogFileID() {
        return WILDLOGFILE_ID_PREFIX + name;
    }

    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp, UtilsHTMLExportTypes inExportType, ProgressbarTask inProgressbarTask) {
        int progressMarker;
        if (inIsRecursive) {
            progressMarker = 30;
        }
        else {
            progressMarker = 95;
        }
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
                    inProgressbarTask.setTaskProgress((int)(((double)t/files.size())*progressMarker));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(" "))
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
                htmlVisit.append("<br/>").append(sightings.get(t).toHTML(inIsRecursive, inIncludeImages, inApp, inExportType, inProgressbarTask)).append("<br/>");
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress(progressMarker + (int)(((double)counter/sightings.size())*(95-progressMarker)));
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(" "))
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

    public boolean hasTheSameContent(Visit inVisit) {
        if (inVisit == null) {
            return false;
        }
        if (UtilsUI.isTheSame(this, inVisit)
                && UtilsUI.isTheSame(getDescription(), inVisit.getDescription())
                && UtilsUI.isTheSame(getEndDate(), inVisit.getEndDate())
                && UtilsUI.isTheSame(getGameWatchingIntensity(), inVisit.getGameWatchingIntensity())
                && UtilsUI.isTheSame(getLocationName(), inVisit.getLocationName())
                && UtilsUI.isTheSame(getName(), inVisit.getName())
                && UtilsUI.isTheSame(getStartDate(), inVisit.getStartDate())
                && UtilsUI.isTheSame(getType(), inVisit.getType())) {
            return true;
        }
        return false;
    }

    public Visit cloneShallow() {
        Visit visit = new Visit();
        visit.setDescription(description);
        visit.setEndDate(endDate);
        visit.setGameWatchingIntensity(gameWatchingIntensity);
        visit.setLocationName(locationName);
        visit.setName(name);
        visit.setStartDate(startDate);
        visit.setStartDate(startDate);
        visit.setType(type);
        return visit;
    }


    // GETTERS:
    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public GameWatchIntensity getGameWatchingIntensity() {
        return gameWatchingIntensity;
    }

    public VisitType getType() {
        return type;
    }

    // SETTERS:
    public void setName(String inName) {
        name = inName;
    }

    public void setStartDate(Date inStartDate) {
        startDate = inStartDate;
    }

    public void setEndDate(Date inEndDate) {
        endDate = inEndDate;
    }

    public void setDescription(String inDescription) {
        description = inDescription;
    }

    public void setGameWatchingIntensity(GameWatchIntensity inGameWatchingIntensity) {
        gameWatchingIntensity = inGameWatchingIntensity;
    }

    public void setType(VisitType inType) {
        type = inType;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

}