package wildlog.data.dataobjects;

import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.WildLogPaths;


public class Element extends ElementCore implements DataObjectWithHTML {

    public Element() {
        super();
    }

    public Element(String inPrimaryName) {
        super(inPrimaryName);
    }

    public Element(ElementType inElementType) {
        super(inElementType);
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
        StringBuilder htmlElement = new StringBuilder("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><title>Creature: " + primaryName + "</title></head>");
        htmlElement.append("<body bgcolor='#E3F0E3'>");
        htmlElement.append("<table bgcolor='#E3F0E3' width='100%'>");
        htmlElement.append("<tr><td style='font-size:9px;font-family:verdana;'>");
        htmlElement.append("<b><u>").append(primaryName).append("</u></b>");
        htmlElement.append("<br/>");
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Other Name:</b><br/> ", otherName, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Scientific Name:</b><br/>", scientificName, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Reference ID:</b><br/> ", referenceID, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Creature Type:</b><br/> ", type, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Feeding Class:</b><br/> ", feedingClass, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Add Frequency:</b><br/> ", addFrequency, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Wish List Rating:</b><br/> ", wishListRating, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Active Time:</b><br/> ", activeTime, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Endangered Status:</b><br/> ", endangeredStatus, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Water Need:</b><br/> ", waterDependance, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Food/Nutrition:</b><br/> ", nutrition, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Identification:</b><br/> ", diagnosticDescription, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Habitat:</b><br/> ", description, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Distribution:</b><br/> ", distribution, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Behaviour:</b><br/> ", behaviourDescription, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Lifespan:</b><br/> ", lifespan, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Breeding:</b><br/> ", breedingDuration, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Number of Young:</b><br/> ", breedingNumber, true);
        UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Size Type:</b> ", sizeType, true);
        if (sizeMaleMin > 0 && !UnitsSize.NONE.equals(sizeUnit)) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Min Male Size:</b><br/> ", sizeMaleMin + " " + UtilsHTML.formatObjectAsString(sizeUnit));
        }
        if (sizeMaleMax > 0 && !UnitsSize.NONE.equals(sizeUnit)) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Max Male Size:</b><br/> ", sizeMaleMax + " " + UtilsHTML.formatObjectAsString(sizeUnit));
        }
        if (sizeFemaleMin > 0 && !UnitsSize.NONE.equals(sizeUnit)) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Min Female Size:</b><br/> ", sizeFemaleMin + " " + UtilsHTML.formatObjectAsString(sizeUnit));
        }
        if (sizeFemaleMax > 0 && !UnitsSize.NONE.equals(sizeUnit)) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Max Female Size:</b><br/> ", sizeFemaleMax + " " + UtilsHTML.formatObjectAsString(sizeUnit));
        }
        if (weightMaleMin > 0 && !UnitsWeight.NONE.equals(weightUnit)) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Min Male Weight:</b><br/> ", weightMaleMin + " " + UtilsHTML.formatObjectAsString(weightUnit));
        }
        if (weightMaleMax > 0 && !UnitsWeight.NONE.equals(weightUnit)) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Max Male Weight:</b><br/> ", weightMaleMax + " " + UtilsHTML.formatObjectAsString(weightUnit));
        }
        if (weightFemaleMin > 0 && !UnitsWeight.NONE.equals(weightUnit)) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Min Female Weight:</b><br/> ", weightFemaleMin + " " + UtilsHTML.formatObjectAsString(weightUnit));
        }
        if (weightFemaleMax > 0 && !UnitsWeight.NONE.equals(weightUnit)) {
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Max Female Weight:</b><br/> ", weightFemaleMax + " " + UtilsHTML.formatObjectAsString(weightUnit));
        }
        if (inIncludeImages) {
            StringBuilder filesString = new StringBuilder(300);
            List<WildLogFile> files = inApp.getDBI().list(new WildLogFile(getWildLogFileID()));
            for (int t = 0; t < files.size(); t++) {
                filesString.append(files.get(t).toHTML(inExportType));
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress((int)(((double)t/files.size())*progressMarker));
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
            Sighting tempSighting = new Sighting();
            tempSighting.setElementName(primaryName);
            List<Sighting> sightings = inApp.getDBI().list(tempSighting);
            int counter = 0;
            for (Sighting temp : sightings) {
                htmlElement.append("<br/>").append(temp.toHTML(false, inIncludeImages, inApp, inExportType, null));
                if (inProgressbarTask != null) {
                    inProgressbarTask.setTaskProgress(progressMarker + (int)(((double)counter/sightings.size())*(95-progressMarker)));
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
    public String getExportPrefix() {
        return WildLogPaths.WildLogPathPrefixes.PREFIX_ELEMENT.toString();
    }

    @Override
    public String getDisplayName() {
        return primaryName;
    }

}