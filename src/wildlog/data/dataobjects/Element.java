package wildlog.data.dataobjects;

import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
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

public class Element implements Comparable<Element>, DataObjectWithHTML {
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
        if (inElement != null)
            if (primaryName != null && inElement.getPrimaryName() != null) {
                return(primaryName.compareToIgnoreCase(inElement.getPrimaryName()));
            }
        return 0;
    }

    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp, UtilsHTML.ImageExportTypes inExportType) {
        StringBuilder fotoString = new StringBuilder();
        if (inIncludeImages) {
            List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile("ELEMENT-" + primaryName));
            for (int t = 0; t < fotos.size(); t++) {
                fotoString.append(fotos.get(t).toHTML(inExportType));
            }
        }
        StringBuilder htmlElement = new StringBuilder("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><title>Creature: " + primaryName + "</title></head>");
        htmlElement.append("<body bgcolor='rgb(227,240,227)'>");
        htmlElement.append("<table bgcolor='rgb(227,240,227)' width='100%'>");
        htmlElement.append("<tr><td>");
        htmlElement.append("<b><u>").append(primaryName).append("</u></b>");
        htmlElement.append("<br/>");
        htmlElement.append("<br/><b>Other Name:</b> ").append(UtilsHTML.formatString(otherName));
        htmlElement.append("<br/><b>Scientific Name:</b> <i>").append(UtilsHTML.formatString(scientificName)).append("</i>");
        htmlElement.append("<br/><b>Reference ID:</b> ").append(UtilsHTML.formatString(referenceID));
        htmlElement.append("<br/>");
        htmlElement.append("<br/><b>Creature Type:</b> ").append(UtilsHTML.formatString(type));
        htmlElement.append("<br/><b>Feeding Class:</b> ").append(UtilsHTML.formatString(feedingClass));
        htmlElement.append("<br/><b>Add Frequency:</b> ").append(UtilsHTML.formatString(addFrequency));
        htmlElement.append("<br/><b>Wish List Rating:</b> ").append(UtilsHTML.formatString(wishListRating));
        htmlElement.append("<br/><b>Active Time:</b> ").append(UtilsHTML.formatString(activeTime));
        htmlElement.append("<br/><b>Endangered Status:</b> ").append(UtilsHTML.formatString(endangeredStatus));
        htmlElement.append("<br/><b>Water Need:</b> ").append(UtilsHTML.formatString(waterDependance));
        htmlElement.append("<br/><b>Food/Nutrition:</b> ").append(UtilsHTML.formatString(nutrition));
        htmlElement.append("<br/><b>Identification:</b> ").append(UtilsHTML.formatString(diagnosticDescription));
        htmlElement.append("<br/><b>Habitat:</b> ").append(UtilsHTML.formatString(description));
        htmlElement.append("<br/><b>Distribution:</b> ").append(UtilsHTML.formatString(distribution));
        htmlElement.append("<br/><b>Behaviour:</b> ").append(UtilsHTML.formatString(behaviourDescription));
        htmlElement.append("<br/><b>Lifespan:</b> ").append(UtilsHTML.formatString(lifespan));
        htmlElement.append("<br/><b>Breeding:</b> ").append(UtilsHTML.formatString(breedingDuration));
        htmlElement.append("<br/><b>Number of Young:</b> ").append(UtilsHTML.formatString(breedingNumber));
        htmlElement.append("<br/><b>Size Type:</b> ").append(UtilsHTML.formatString(sizeType));
        htmlElement.append("<br/><b>Min Male Size:</b> ").append(UtilsHTML.formatString(sizeMaleMin)).append(" ").append(UtilsHTML.formatString(sizeUnit));
        htmlElement.append("<br/><b>Max Male Size:</b> ").append(UtilsHTML.formatString(sizeMaleMin)).append(" ").append(UtilsHTML.formatString(sizeUnit));
        htmlElement.append("<br/><b>Min Female Size:</b> ").append(UtilsHTML.formatString(sizeFemaleMin)).append(" ").append(UtilsHTML.formatString(sizeUnit));
        htmlElement.append("<br/><b>Max Female Size:</b> ").append(UtilsHTML.formatString(sizeFemaleMin)).append(" ").append(UtilsHTML.formatString(sizeUnit));
        htmlElement.append("<br/><b>Min Male Weight:</b> ").append(UtilsHTML.formatString(weightMaleMin)).append(" ").append(UtilsHTML.formatString(weightUnit));
        htmlElement.append("<br/><b>Max Male Weight:</b> ").append(UtilsHTML.formatString(weightMaleMin)).append(" ").append(UtilsHTML.formatString(weightUnit));
        htmlElement.append("<br/><b>Min Female Weight:</b> ").append(UtilsHTML.formatString(weightFemaleMin)).append(" ").append(UtilsHTML.formatString(weightUnit));
        htmlElement.append("<br/><b>Max Female Weight:</b> ").append(UtilsHTML.formatString(weightFemaleMin)).append(" ").append(UtilsHTML.formatString(weightUnit));
        if (inIncludeImages && fotoString.length() > 0) {
            htmlElement.append("<br/>");
            htmlElement.append("<br/><b>Photos:</b><br/>").append(fotoString);
        }
        if (inIsRecursive) {
            htmlElement.append("<br/>");
            htmlElement.append("</td></tr>");
            htmlElement.append("<tr><td>");
            Sighting tempSighting = new Sighting();
            tempSighting.setElementName(primaryName);
            List<Sighting> sightings = inApp.getDBI().list(tempSighting);
            for (Sighting temp : sightings) {
                htmlElement.append("<br/>").append(temp.toHTML(false, inIncludeImages, inApp, inExportType));
            }
        }
        htmlElement.append("</td></tr>");
        htmlElement.append("</table>");
        htmlElement.append("<br/>");
        htmlElement.append("</body>");
        return htmlElement.toString();
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