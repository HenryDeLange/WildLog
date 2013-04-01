package wildlog.data.dataobjects;

import java.util.Date;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.VisitType;
import wildlog.html.utils.UtilsHTML;

public class Visit implements Comparable<Visit>, DataObjectWithHTML {
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
        if (inVisit != null)
            if (name != null && inVisit.getName() != null) {
                return(name.compareToIgnoreCase(inVisit.getName()));
            }
        return 0;
    }

    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp, UtilsHTML.ImageExportTypes inExportType) {
        StringBuilder fotoString = new StringBuilder();
        if (inIncludeImages) {
            List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile("VISIT-" + name));
            for (int t = 0; t < fotos.size(); t++) {
                fotoString.append(fotos.get(t).toHTML(inExportType));
            }
        }
        StringBuilder sightingString = new StringBuilder();
        if (inIsRecursive) {
            Sighting tempSighting = new Sighting();
            tempSighting.setVisitName(name);
            List<Sighting> sightings = inApp.getDBI().list(tempSighting);
            for (int t = 0; t < sightings.size(); t++) {
                sightingString.append(sightings.get(t).toHTML(inIsRecursive, inIncludeImages, inApp, inExportType)).append("<br/>");
            }
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
        if (inIncludeImages && fotoString.length() > 0) {
            htmlVisit.append("<br/>");
            htmlVisit.append("<br/><b>Photos:</b><br/>").append(fotoString);
        }
        if (inIsRecursive) {
            htmlVisit.append("<br/>");
            htmlVisit.append("</td></tr>");
            htmlVisit.append("<tr><td>");
            htmlVisit.append("<br/>").append(sightingString);
        }
        htmlVisit.append("</td></tr>");
        htmlVisit.append("</table>");
        htmlVisit.append("<br/>");
        htmlVisit.append("</body>");
        return htmlVisit.toString();
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