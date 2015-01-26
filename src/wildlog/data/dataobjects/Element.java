package wildlog.data.dataobjects;

import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithXML;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.xml.utils.UtilsXML;


public class Element extends ElementCore implements DataObjectWithHTML, DataObjectWithXML {

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
            Sighting tempSighting = new Sighting();
            tempSighting.setElementName(primaryName);
            List<Sighting> sightings = inApp.getDBI().list(tempSighting);
            int counter = 0;
            for (Sighting temp : sightings) {
                htmlElement.append("<br/>").append(temp.toHTML(false, inIncludeImages, inApp, inExportType, null));
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
    public String toFancyHTML(WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        String html = UtilsHTML.FANCY_HTML_TEMPLATE;
        // Title
        html = html.replace("___INSERT_TITLE___", primaryName);
        // Background
        html = html.replace("___INSERT_GRADIENT_CSS___", "gradient_elements");
        inProgressbarTask.setTaskProgress(1);
        inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                + " " + inProgressbarTask.getProgress() + "%");
        // Map
//        ___INSERT_SCRIPT_TO_LOAD_MAP_POINTS___
        inProgressbarTask.setTaskProgress(10);
        inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                + " " + inProgressbarTask.getProgress() + "%");
        // Header
        html = html.replace("___INSERT_TABLE_HEADER___", primaryName);
        // Images
        String images = "";
        List<WildLogFile> listWildLogFiles = inApp.getDBI().list(new WildLogFile(getWildLogFileID()));
        for (WildLogFile wildLogFile : listWildLogFiles) {
            images = images + wildLogFile.toHTML(UtilsHTMLExportTypes.ForFancyHTML);
        }
        html = html.replace("___INSERT_IMAGES___", images);
        inProgressbarTask.setTaskProgress(15);
        inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                + " " + inProgressbarTask.getProgress() + "%");
        // Details
        html = html.replace("___INSERT_DETAILS___", toHTML(false, false, inApp, UtilsHTMLExportTypes.ForHTML, inProgressbarTask));
        // Sightings
        String sightings = "";
        List<Sighting> listSightings = inApp.getDBI().list(new Sighting(primaryName, null, null));
        int counter = 0;
        for (Sighting temp : listSightings) {
            sightings = sightings + "\n" + temp.toFancyHTML(inApp, null);
            inProgressbarTask.setTaskProgress(15 + (int)(((double)counter/listSightings.size())*(84)));
            inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                    + " " + inProgressbarTask.getProgress() + "%");
            counter++;
        }
        html = html.replace("___INSERT_SIGHTINGS___", sightings);
        return html;
    }

    @Override
    public String toXML(WildLogApp inApp, ProgressbarTask inProgressbarTask, boolean inIsRecursive) {
        StringBuilder builder = new StringBuilder(500);
        builder.append("<Creature>");
        builder.append("<primaryName>").append(primaryName).append("</primaryName>");
        builder.append("<otherName>").append(otherName).append("</otherName>");
        builder.append("<scientificName>").append(scientificName).append("</scientificName>");
        builder.append("<referenceID>").append(referenceID).append("</referenceID>");
        builder.append("<type>").append(type).append("</type>");
        builder.append("<feedingClass>").append(feedingClass).append("</feedingClass>");
        builder.append("<addFrequency>").append(addFrequency).append("</addFrequency>");
        builder.append("<wishListRating>").append(wishListRating).append("</wishListRating>");
        builder.append("<activeTime>").append(activeTime).append("</activeTime>");
        builder.append("<endangeredStatus>").append(endangeredStatus).append("</endangeredStatus>");
        builder.append("<waterDependance>").append(waterDependance).append("</waterDependance>");
        builder.append("<nutrition>").append(nutrition).append("</nutrition>");
        builder.append("<diagnosticDescription>").append(diagnosticDescription).append("</diagnosticDescription>");
        builder.append("<description>").append(description).append("</description>");
        builder.append("<distribution>").append(distribution).append("</distribution>");
        builder.append("<behaviourDescription>").append(behaviourDescription).append("</behaviourDescription>");
        builder.append("<lifespan>").append(lifespan).append("</lifespan>");
        builder.append("<breedingDuration>").append(breedingDuration).append("</breedingDuration>");
        builder.append("<breedingNumber>").append(breedingNumber).append("</breedingNumber>");
        builder.append("<sizeType>").append(sizeType).append("</sizeType>");
        builder.append("<sizeMaleMin>").append(sizeMaleMin).append("</sizeMaleMin>");
        builder.append("<sizeMaleMax>").append(sizeMaleMax).append("</sizeMaleMax>");
        builder.append("<sizeFemaleMin>").append(sizeFemaleMin).append("</sizeFemaleMin>");
        builder.append("<sizeFemaleMax>").append(sizeFemaleMax).append("</sizeFemaleMax>");
        builder.append("<sizeUnit>").append(sizeUnit).append("</sizeUnit>");
        builder.append("<weightMaleMin>").append(weightMaleMin).append("</weightMaleMin>");
        builder.append("<weightMaleMax>").append(weightMaleMax).append("</weightMaleMax>");
        builder.append("<weightFemaleMin>").append(weightFemaleMin).append("</weightFemaleMin>");
        builder.append("<weightFemaleMax>").append(weightFemaleMax).append("</weightFemaleMax>");
        builder.append("<weightUnit>").append(weightUnit).append("</weightUnit>");
        StringBuilder filesString = new StringBuilder(300);
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
        builder.append("</Creature>");
        return builder.toString();
    }
    
}