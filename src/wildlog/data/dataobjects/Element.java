package wildlog.data.dataobjects;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithXML;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;
import wildlog.utils.WildLogSystemImages;
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
        StringBuilder htmlElement = new StringBuilder("<head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>");
        htmlElement.append("<title>Creature: ").append(primaryName).append("</title></head>");
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
            List<Sighting> sightings = inApp.getDBI().list(tempSighting, false);
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
    public String toFancyHTML(String inTemplate, WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        String html = inTemplate;
        List<Sighting> listSightings = inApp.getDBI().list(new Sighting(primaryName, null, null), false);
        int counter = 0;
        int mapBeginIndex = html.indexOf("//___MAP_CLICKABLE_DATA_POINTS_START___") + "//___MAP_CLICKABLE_DATA_POINTS_START___".length();
        int mapEndIndex = html.indexOf("//___MAP_CLICKABLE_DATA_POINTS_END___");
        String mapTemplate = html.substring(mapBeginIndex, mapEndIndex).trim();
        StringBuilder mapBuilder = new StringBuilder(1000 * listSightings.size());
        int relatedBeginIndex = html.indexOf("<!--___REPEAT_RELATED_RECORDS_START___-->") + "<!--___REPEAT_RELATED_RECORDS_START___-->".length();
        int relatedEndIndex = html.indexOf("<!--___REPEAT_RELATED_RECORDS_END___-->");
        String relatedTemplate = html.substring(relatedBeginIndex, relatedEndIndex).trim();
        StringBuilder relatedBuilder = new StringBuilder(1500 * listSightings.size());
        int sliderBeginIndex = html.indexOf("___SUB_SLIDER_START___") + "___SUB_SLIDER_START___".length();
        int sliderEndIndex = html.indexOf("___SUB_SLIDER_END___");
        String sliderTemplate = html.substring(sliderBeginIndex, sliderEndIndex).trim();
        int lightboxBeginIndex = html.indexOf("___SUB_LIGHTBOX_START___") + "___SUB_LIGHTBOX_START___".length();
        int lightboxEndIndex = html.indexOf("___SUB_LIGHTBOX_END___");
        String lightboxTemplate = html.substring(lightboxBeginIndex, lightboxEndIndex).trim();
        int subFieldBeginIndex = html.indexOf("//___REGISTER_RELATED_RECORDS_FIELD_BOXES_START___") + "//___REGISTER_RELATED_RECORDS_FIELD_BOXES_START___".length();
        int subFieldEndIndex = html.indexOf("//___REGISTER_RELATED_RECORDS_FIELD_BOXES_END___");
        String subFieldTemplate = html.substring(subFieldBeginIndex, subFieldEndIndex).trim();
        StringBuilder subFieldBuilder = new StringBuilder(200 * listSightings.size());
        int sliderScriptBeginIndex = html.indexOf("//___REGISTER_RELATED_RECORDS_IMAGE_SLIDER_START___") + "//___REGISTER_RELATED_RECORDS_IMAGE_SLIDER_START___".length();
        int sliderScriptEndIndex = html.indexOf("//___REGISTER_RELATED_RECORDS_IMAGE_SLIDER_END___");
        String sliderScriptTemplate = html.substring(sliderScriptBeginIndex, sliderScriptEndIndex).trim();
        StringBuilder sliderScriptBuilder = new StringBuilder(250 * listSightings.size());
        for (Sighting tempSighting : listSightings) {
            // JavaScript for related record fields
            subFieldBuilder.append(subFieldTemplate.replace("relatedRecordsFieldBoxZZZ", "relatedRecordsFieldBox" + tempSighting.getSightingCounter()));
            subFieldBuilder.append(System.lineSeparator());
            // JavaScript for related record sliders
            sliderScriptBuilder.append(sliderScriptTemplate.replace("subSliderZZZ", "subSlider" + tempSighting.getSightingCounter()));
            sliderScriptBuilder.append(System.lineSeparator());
            // Mapping Points
            if (UtilsGps.getLatDecimalDegree(tempSighting) != 0 && UtilsGps.getLonDecimalDegree(tempSighting) != 0) {
                mapBuilder.append(mapTemplate.replace("var markerZZZ", "var marker" + tempSighting.getSightingCounter())
                                             .replace("LatLng(44.5403, -78.5463)", "LatLng(" + UtilsGps.getLatDecimalDegree(tempSighting) + "," + UtilsGps.getLonDecimalDegree(tempSighting) + ")")
                                             .replace("ZZZ-title", tempSighting.getDisplayName().replaceAll("\"", "&quot;"))
                                             .replace("var infowindowZZZ", "var infowindow" + tempSighting.getSightingCounter())
                                             .replace("ZZZ-content", tempSighting.toHTML(false, false, inApp, UtilsHTMLExportTypes.ForHTML, null).replaceAll("\"", "&quot;"))
                                             .replace("addListener(markerZZZ", "addListener(marker" + tempSighting.getSightingCounter())
                                             .replace("infowindowZZZ.open(map, markerZZZ", "infowindow" + tempSighting.getSightingCounter() + ".open(map, marker" + tempSighting.getSightingCounter())
                                             .replace("extend(markerZZZ", "extend(marker" + tempSighting.getSightingCounter()));
            }
            mapBuilder.append(System.lineSeparator());
            // Observation Info
            List<WildLogFile> lstFiles = inApp.getDBI().list(new WildLogFile(tempSighting.getWildLogFileID()));
            StringBuilder sliderBuilder = new StringBuilder(200 * lstFiles.size());
            StringBuilder lightboxBuilder = new StringBuilder(200 * lstFiles.size());
            if (lstFiles.isEmpty()) {
                lstFiles.add(WildLogSystemImages.NO_FILES.getWildLogFile());
            }
            for (WildLogFile wildLogFile : lstFiles) {
                Path thumbnailFolder = WildLogPaths.WILDLOG_EXPORT_HTML_FANCY_THUMBNAILS.getAbsoluteFullPath().resolve(wildLogFile.getRelativePath().getParent());
                try {
                    Files.createDirectories(thumbnailFolder);
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
                Path fromFile = wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.MEDIUM);
                Path thumbnailPath = thumbnailFolder.resolve(fromFile.getFileName());
                UtilsFileProcessing.copyFile(fromFile, thumbnailPath, false, true);
                Path toFileAsRelativePath = WildLogPaths.WILDLOG_EXPORT_HTML_FANCY.getAbsoluteFullPath().relativize(thumbnailPath);
                sliderBuilder.append(sliderTemplate.replace("href=\"#bigImgZZZ2\"", "href=\"#bigImg" + wildLogFile.getDBFilePath() + "\"")
                                                   .replace("src=\"./ZZZ2.jpg\"", "src=\"../" + toFileAsRelativePath.toString() + "\"")
                                                   .replace("ZZZ2-alt", wildLogFile.getId())
                                                   .replace("ZZZ2-title", wildLogFile.getFilename()));
                sliderBuilder.append(System.lineSeparator());
                lightboxBuilder.append(lightboxTemplate.replace("id=\"bigImgZZZ2\"", "id=\"bigImg" + wildLogFile.getDBFilePath() + "\"")
                                                       .replace("src=\"./ZZZ2.jpg\"", "src=\"" + wildLogFile.getAbsolutePath() + "\"")
                                                       .replace("ZZZ2-alt", wildLogFile.getId())
                                                       .replace("ZZZ2-title", wildLogFile.getFilename()));
                lightboxBuilder.append(System.lineSeparator());
            }
            // Set the HTML for the Slider and LisghtBox
            relatedBuilder.append(relatedTemplate.replace("subSliderZZZ", "subSlider" + tempSighting.getSightingCounter())
                                                 .replace("relatedRecordsFieldBoxZZZ", "relatedRecordsFieldBox" + tempSighting.getSightingCounter())
                                                 .replace("___RELATED_RECORD_NAME___", tempSighting.getDisplayName())
                                                 .replace("___SUB_SLIDER_START___", "")
                                                 .replace("___SUB_SLIDER_END___", "")
                                                 .replace("___SUB_LIGHTBOX_START___", "")
                                                 .replace("___SUB_LIGHTBOX_END___", "")
                                                 .replace("___RELATED_INFORMATION_CONTENT___", tempSighting.toHTML(false, false, inApp, UtilsHTMLExportTypes.ForHTML, null).replaceAll("\"", "&quot;"))
                                                 .replace(sliderTemplate, sliderBuilder.toString())
                                                 .replace(lightboxTemplate, lightboxBuilder.toString()));
            relatedBuilder.append(System.lineSeparator());
            // Update progress
            if (inProgressbarTask != null) {
                inProgressbarTask.setTaskProgress(25 + (int)(((double)counter/listSightings.size())*(74)));
                inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(' '))
                        + " " + inProgressbarTask.getProgress() + "%");
            }
            counter++;
        }
        // Set the HTML of the Javascript
        html = html.replace("//___REGISTER_RELATED_RECORDS_FIELD_BOXES_START___", "")
                   .replace("//___REGISTER_RELATED_RECORDS_FIELD_BOXES_END___", "")
                   .replace(subFieldTemplate, subFieldBuilder.toString())
                   .replace("//___REGISTER_RELATED_RECORDS_IMAGE_SLIDER_START___", "")
                   .replace("//___REGISTER_RELATED_RECORDS_IMAGE_SLIDER_END___", "")
                   .replace(sliderScriptTemplate, sliderScriptBuilder.toString());
        // Set the HTML of the Map
        html = html.replace("//___MAP_CLICKABLE_DATA_POINTS_START___", "")
                   .replace("//___MAP_CLICKABLE_DATA_POINTS_END___", "")
                   .replace(mapTemplate, mapBuilder.toString());
        // Set the HTML of the Observations
        html = html.replace("<!--___REPEAT_RELATED_RECORDS_START___-->", "")
                   .replace("<!--___REPEAT_RELATED_RECORDS_END___-->", "")
                   .replace(relatedTemplate, relatedBuilder.toString());
        return html;
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
        builder.append("<addFrequency>").append(addFrequency).append("</addFrequency>");
        builder.append("<wishListRating>").append(wishListRating).append("</wishListRating>");
        builder.append("<activeTime>").append(activeTime).append("</activeTime>");
        builder.append("<endangeredStatus>").append(endangeredStatus).append("</endangeredStatus>");
        builder.append("<waterDependance>").append(waterDependance).append("</waterDependance>");
        builder.append("<nutrition><![CDATA[").append(nutrition).append("]]></nutrition>");
        builder.append("<diagnosticDescription><![CDATA[").append(diagnosticDescription).append("]]></diagnosticDescription>");
        builder.append("<description><![CDATA[").append(description).append("]]></description>");
        builder.append("<distribution><![CDATA[").append(distribution).append("]]></distribution>");
        builder.append("<behaviourDescription><![CDATA[").append(behaviourDescription).append("]]></behaviourDescription>");
        builder.append("<lifespan><![CDATA[").append(lifespan).append("]]></lifespan>");
        builder.append("<breedingDuration><![CDATA[").append(breedingDuration).append("]]></breedingDuration>");
        builder.append("<breedingNumber><![CDATA[").append(breedingNumber).append("]]></breedingNumber>");
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
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(5);
        }
        StringBuilder filesString = new StringBuilder(300);
        List<WildLogFile> files = inApp.getDBI().list(new WildLogFile(getWildLogFileID()));
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
            List<Sighting> sightings = inApp.getDBI().list(new Sighting(primaryName, null, null), false);
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
    
}