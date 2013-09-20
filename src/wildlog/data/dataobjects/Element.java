package wildlog.data.dataobjects;

import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.dataobjects.interfaces.DataObjectWithWildLogFile;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.AddFrequency;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.SizeType;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.data.enums.WaterDependancy;
import wildlog.data.enums.WishRating;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.utils.UtilsUI;
import wildlog.utils.WildLogPaths;

public class Element implements Comparable<Element>, DataObjectWithHTML, DataObjectWithWildLogFile {
    public static final String WILDLOGFILE_ID_PREFIX = "ELEMENT-";
    private String primaryName; // Used for indexing (ID)
    private String otherName;
    private String scientificName;
    private String description;
    private String nutrition;
    private WaterDependancy waterDependance;
    private double sizeMaleMin;
    private double sizeMaleMax;
    private double sizeFemaleMin;
    private double sizeFemaleMax;
    private double weightMaleMin;
    private double weightMaleMax;
    private double weightFemaleMin;
    private double weightFemaleMax;
    private String breedingDuration;
    private String breedingNumber;
    private WishRating wishListRating;
    private String diagnosticDescription;
    private ActiveTime activeTime;
    private EndangeredStatus endangeredStatus;
    private String behaviourDescription;
    private AddFrequency addFrequency;
    private ElementType type;
    private FeedingClass feedingClass;
    private UnitsSize sizeUnit;
    private UnitsWeight weightUnit;
    private String lifespan;
    private String referenceID;
    private String distribution;
    private SizeType sizeType;

    // CONSTRUCTORS:
    public Element() {
    }

    public Element(String inPrimaryName) {
        primaryName = inPrimaryName;
    }

    public Element(ElementType inElementType) {
        type = inElementType;
    }

    // METHODS:
    @Override
    public String toString() {
        return primaryName;
    }

    @Override
    public int compareTo(Element inElement) {
        if (inElement != null) {
            if (primaryName != null && inElement.getPrimaryName() != null) {
                return(primaryName.compareToIgnoreCase(inElement.getPrimaryName()));
            }
        }
        return 0;
    }

    @Override
    public String getWildLogFileID() {
        return WILDLOGFILE_ID_PREFIX + primaryName;
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
        if (sizeMaleMin > 0 && !UnitsSize.NONE.equals(sizeUnit))
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Min Male Size:</b><br/> ", sizeMaleMin + " " + UtilsHTML.formatObjectAsString(sizeUnit));
        if (sizeMaleMax > 0 && !UnitsSize.NONE.equals(sizeUnit))
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Max Male Size:</b><br/> ", sizeMaleMax + " " + UtilsHTML.formatObjectAsString(sizeUnit));
        if (sizeFemaleMin > 0 && !UnitsSize.NONE.equals(sizeUnit))
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Min Female Size:</b><br/> ", sizeFemaleMin + " " + UtilsHTML.formatObjectAsString(sizeUnit));
        if (sizeFemaleMax > 0 && !UnitsSize.NONE.equals(sizeUnit))
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Max Female Size:</b><br/> ", sizeFemaleMax + " " + UtilsHTML.formatObjectAsString(sizeUnit));
        if (weightMaleMin > 0 && !UnitsWeight.NONE.equals(weightUnit))
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Min Male Weight:</b><br/> ", weightMaleMin + " " + UtilsHTML.formatObjectAsString(weightUnit));
        if (weightMaleMax > 0 && !UnitsWeight.NONE.equals(weightUnit))
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Max Male Weight:</b><br/> ", weightMaleMax + " " + UtilsHTML.formatObjectAsString(weightUnit));
        if (weightFemaleMin > 0 && !UnitsWeight.NONE.equals(weightUnit))
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Min Female Weight:</b><br/> ", weightFemaleMin + " " + UtilsHTML.formatObjectAsString(weightUnit));
        if (weightFemaleMax > 0 && !UnitsWeight.NONE.equals(weightUnit))
            UtilsHTML.appendIfNotNullNorEmpty(htmlElement, "<br/><b>Max Female Weight:</b><br/> ", weightFemaleMax + " " + UtilsHTML.formatObjectAsString(weightUnit));
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
                    inProgressbarTask.setMessage(inProgressbarTask.getMessage().substring(0, inProgressbarTask.getMessage().lastIndexOf(" "))
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

    public boolean hasTheSameContent(Element inElement) {
        if (inElement == null) {
            return false;
        }
        if (UtilsUI.isTheSame(this, inElement)
                && UtilsUI.isTheSame(getActiveTime(), inElement.getActiveTime())
                && UtilsUI.isTheSame(getAddFrequency(), inElement.getAddFrequency())
                && UtilsUI.isTheSame(getBehaviourDescription(), getBehaviourDescription())
                && UtilsUI.isTheSame(getBreedingDuration(), inElement.getBreedingDuration())
                && UtilsUI.isTheSame(getBreedingNumber(), inElement.getBreedingNumber())
                && UtilsUI.isTheSame(getDescription(), inElement.getDescription())
                && UtilsUI.isTheSame(getDiagnosticDescription(), inElement.getDiagnosticDescription())
                && UtilsUI.isTheSame(getDistribution(), inElement.getDistribution())
                && UtilsUI.isTheSame(getEndangeredStatus(), inElement.getEndangeredStatus())
                && UtilsUI.isTheSame(getFeedingClass(), inElement.getFeedingClass())
                && UtilsUI.isTheSame(getLifespan(), inElement.getLifespan())
                && UtilsUI.isTheSame(getNutrition(), inElement.getNutrition())
                && UtilsUI.isTheSame(getOtherName(), inElement.getOtherName())
                && UtilsUI.isTheSame(getPrimaryName(), inElement.getPrimaryName())
                && UtilsUI.isTheSame(getReferenceID(), inElement.getReferenceID())
                && UtilsUI.isTheSame(getScientificName(), inElement.getScientificName())
                && UtilsUI.isTheSame(getSizeFemaleMax(), inElement.getSizeFemaleMax())
                && UtilsUI.isTheSame(getSizeFemaleMin(), inElement.getSizeFemaleMin())
                && UtilsUI.isTheSame(getSizeMaleMax(), inElement.getSizeMaleMax())
                && UtilsUI.isTheSame(getSizeMaleMin(), inElement.getSizeMaleMin())
                && UtilsUI.isTheSame(getSizeType(), inElement.getSizeType())
                && UtilsUI.isTheSame(getSizeUnit(), inElement.getSizeUnit())
                && UtilsUI.isTheSame(getType(), inElement.getType())
                && UtilsUI.isTheSame(getWaterDependance(), inElement.getWaterDependance())
                && UtilsUI.isTheSame(getWeightFemaleMax(), inElement.getWeightFemaleMax())
                && UtilsUI.isTheSame(getWeightFemaleMin(), inElement.getWeightFemaleMin())
                && UtilsUI.isTheSame(getWeightMaleMax(), inElement.getWeightMaleMax())
                && UtilsUI.isTheSame(getWeightMaleMin(), inElement.getWeightMaleMin())
                && UtilsUI.isTheSame(getWeightUnit(), inElement.getWeightUnit())
                && UtilsUI.isTheSame(getWishListRating(), inElement.getWishListRating())) {
            return true;
        }
        return false;
    }

    public Element cloneShallow() {
        Element element = new Element();
        element.setActiveTime(activeTime);
        element.setAddFrequency(addFrequency);
        element.setBehaviourDescription(behaviourDescription);
        element.setBreedingDuration(breedingDuration);
        element.setBreedingNumber(breedingNumber);
        element.setDescription(description);
        element.setDiagnosticDescription(diagnosticDescription);
        element.setDistribution(distribution);
        element.setEndangeredStatus(endangeredStatus);
        element.setFeedingClass(feedingClass);
        element.setLifespan(lifespan);
        element.setNutrition(nutrition);
        element.setOtherName(otherName);
        element.setPrimaryName(primaryName);
        element.setReferenceID(referenceID);
        element.setScientificName(scientificName);
        element.setSizeFemaleMax(sizeFemaleMax);
        element.setSizeFemaleMin(sizeFemaleMin);
        element.setSizeMaleMax(sizeMaleMin);
        element.setSizeMaleMin(sizeMaleMin);
        element.setSizeType(sizeType);
        element.setSizeUnit(sizeUnit);
        element.setType(type);
        element.setWaterDependance(waterDependance);
        element.setWeightFemaleMax(weightFemaleMax);
        element.setWeightFemaleMin(weightFemaleMin);
        element.setWeightMaleMax(weightMaleMax);
        element.setWeightMaleMin(weightMaleMin);
        element.setWeightUnit(weightUnit);
        element.setWishListRating(wishListRating);
        return element;
    }


    // GETTERS:
    public String getPrimaryName() {
        return primaryName;
    }

    public String getOtherName() {
        return otherName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public String getDescription() {
        return description;
    }

    public String getNutrition() {
        return nutrition;
    }

    public WaterDependancy getWaterDependance() {
        return waterDependance;
    }

    public double getSizeMaleMin() {
        return sizeMaleMin;
    }

    public double getSizeFemaleMin() {
        return sizeFemaleMin;
    }

    public double getWeightMaleMin() {
        return weightMaleMin;
    }

    public double getWeightFemaleMin() {
        return weightFemaleMin;
    }

    public String getBreedingDuration() {
        return breedingDuration;
    }

    public String getBreedingNumber() {
        return breedingNumber;
    }

    public WishRating getWishListRating() {
        return wishListRating;
    }

    public String getDiagnosticDescription() {
        return diagnosticDescription;
    }

    public ActiveTime getActiveTime() {
        return activeTime;
    }

    public EndangeredStatus getEndangeredStatus() {
        return endangeredStatus;
    }

    public String getBehaviourDescription() {
        return behaviourDescription;
    }

    public AddFrequency getAddFrequency() {
        return addFrequency;
    }

    public ElementType getType() {
        return type;
    }

    public FeedingClass getFeedingClass() {
        return feedingClass;
    }

    public UnitsSize getSizeUnit() {
        return sizeUnit;
    }

    public UnitsWeight getWeightUnit() {
        return weightUnit;
    }

    public String getLifespan() {
        return lifespan;
    }

    // SETTERS:
    public void setPrimaryName(String inPrimaryName) {
        primaryName = inPrimaryName;
    }

    public void setOtherName(String inOtherName) {
        otherName = inOtherName;
    }

    public void setScientificName(String inScientificName) {
        scientificName = inScientificName;
    }

    public void setReferenceID(String inReferenceID) {
        referenceID = inReferenceID;
    }

    public void setDescription(String inDescription) {
        description = inDescription;
    }

    public void setNutrition(String inNutrition) {
        nutrition = inNutrition;
    }

    public void setWaterDependance(WaterDependancy inWaterDependance) {
        waterDependance = inWaterDependance;
    }

    public void setSizeMaleMin(double inSizeMaleMin) {
        sizeMaleMin = inSizeMaleMin;
    }

    public void setSizeFemaleMin(double inSizeFemaleMin) {
        sizeFemaleMin = inSizeFemaleMin;
    }

    public void setWeightMaleMin(double inWeightMaleMin) {
        weightMaleMin = inWeightMaleMin;
    }

    public void setWeightFemaleMin(double inWeightFemaleMin) {
        weightFemaleMin = inWeightFemaleMin;
    }

    public void setBreedingDuration(String inBreedingDuration) {
        breedingDuration = inBreedingDuration;
    }

    public void setBreedingNumber(String inBreedingNumber) {
        breedingNumber = inBreedingNumber;
    }

    public void setWishListRating(WishRating inWishListRating) {
        wishListRating = inWishListRating;
    }

    public void setDiagnosticDescription(String inDiagnosticDescription) {
        diagnosticDescription = inDiagnosticDescription;
    }

    public void setActiveTime(ActiveTime inActiveTime) {
        activeTime = inActiveTime;
    }

    public void setEndangeredStatus(EndangeredStatus inEndangeredStatus) {
        endangeredStatus = inEndangeredStatus;
    }

    public void setBehaviourDescription(String inBehaviourDescription) {
        behaviourDescription = inBehaviourDescription;
    }

    public void setAddFrequency(AddFrequency inAddFrequency) {
        addFrequency = inAddFrequency;
    }

    public void setType(ElementType inType) {
        type = inType;
    }

    public void setFeedingClass(FeedingClass inFeedingClass) {
        feedingClass = inFeedingClass;
    }

    public void setSizeUnit(UnitsSize inSizeUnit) {
        sizeUnit = inSizeUnit;
    }

    public void setWeightUnit(UnitsWeight inWeightUnit) {
        weightUnit = inWeightUnit;
    }

    public void setLifespan(String inLifespan) {
        lifespan = inLifespan;
    }

    public double getSizeFemaleMax() {
        return sizeFemaleMax;
    }

    public void setSizeFemaleMax(double inSizeFemaleMax) {
        sizeFemaleMax = inSizeFemaleMax;
    }

    public double getSizeMaleMax() {
        return sizeMaleMax;
    }

    public void setSizeMaleMax(double inSizeMaleMax) {
        sizeMaleMax = inSizeMaleMax;
    }

    public double getWeightFemaleMax() {
        return weightFemaleMax;
    }

    public void setWeightFemaleMax(double inWeightFemaleMax) {
        weightFemaleMax = inWeightFemaleMax;
    }

    public double getWeightMaleMax() {
        return weightMaleMax;
    }

    public void setWeightMaleMax(double inWeightMaleMax) {
        weightMaleMax = inWeightMaleMax;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String inDistribution) {
        distribution = inDistribution;
    }

    public SizeType getSizeType() {
        return sizeType;
    }

    public void setSizeType(SizeType inSizeType) {
        sizeType = inSizeType;
    }

}