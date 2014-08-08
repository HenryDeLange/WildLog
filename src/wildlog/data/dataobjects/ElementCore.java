package wildlog.data.dataobjects;

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
import wildlog.data.utils.UtilsData;


public class ElementCore implements DataObjectWithWildLogFile {
    public static final String WILDLOGFILE_ID_PREFIX = "ELEMENT-";
    public static final String WILDLOG_FOLDER_PREFIX = "Creatures";
    protected String primaryName; // Used for indexing (ID)
    protected String otherName;
    protected String scientificName;
    protected String description;
    protected String nutrition;
    protected WaterDependancy waterDependance;
    protected double sizeMaleMin;
    protected double sizeMaleMax;
    protected double sizeFemaleMin;
    protected double sizeFemaleMax;
    protected double weightMaleMin;
    protected double weightMaleMax;
    protected double weightFemaleMin;
    protected double weightFemaleMax;
    protected String breedingDuration;
    protected String breedingNumber;
    protected WishRating wishListRating;
    protected String diagnosticDescription;
    protected ActiveTime activeTime;
    protected EndangeredStatus endangeredStatus;
    protected String behaviourDescription;
    protected AddFrequency addFrequency;
    protected ElementType type;
    protected FeedingClass feedingClass;
    protected UnitsSize sizeUnit;
    protected UnitsWeight weightUnit;
    protected String lifespan;
    protected String referenceID;
    protected String distribution;
    protected SizeType sizeType;


    public ElementCore() {
    }

    public ElementCore(String inPrimaryName) {
        primaryName = inPrimaryName;
    }

    public ElementCore(ElementType inElementType) {
        type = inElementType;
    }


    @Override
    public String toString() {
        return primaryName;
    }

    @Override
    public int compareTo(Object inElement) {
        if (inElement != null) {
            // Ek los die instanceof check uit vir eers want ek glo nie ek sal ooit lyste sort met verskillende data objects in nie...
            ElementCore compareElement = (ElementCore) inElement;
            if (primaryName != null && compareElement.getPrimaryName() != null) {
                return(primaryName.compareToIgnoreCase(compareElement.getPrimaryName()));
            }
        }
        return 0;
    }

    @Override
    public String getWildLogFileID() {
        return WILDLOGFILE_ID_PREFIX + primaryName;
    }
    
    @Override
    public String getDisplayName() {
        return primaryName;
    }
    
    @Override
    public String getExportPrefix() {
        return WILDLOG_FOLDER_PREFIX;
    }

    @Override
    public String getIDField() {
        return primaryName;
    }

    public boolean hasTheSameContent(ElementCore inElement) {
        if (inElement == null) {
            return false;
        }
        return UtilsData.isTheSame(this, inElement)
                && UtilsData.isTheSame(getActiveTime(), inElement.getActiveTime())
                && UtilsData.isTheSame(getAddFrequency(), inElement.getAddFrequency())
                && UtilsData.isTheSame(getBehaviourDescription(), getBehaviourDescription())
                && UtilsData.isTheSame(getBreedingDuration(), inElement.getBreedingDuration())
                && UtilsData.isTheSame(getBreedingNumber(), inElement.getBreedingNumber())
                && UtilsData.isTheSame(getDescription(), inElement.getDescription())
                && UtilsData.isTheSame(getDiagnosticDescription(), inElement.getDiagnosticDescription())
                && UtilsData.isTheSame(getDistribution(), inElement.getDistribution())
                && UtilsData.isTheSame(getEndangeredStatus(), inElement.getEndangeredStatus())
                && UtilsData.isTheSame(getFeedingClass(), inElement.getFeedingClass())
                && UtilsData.isTheSame(getLifespan(), inElement.getLifespan())
                && UtilsData.isTheSame(getNutrition(), inElement.getNutrition())
                && UtilsData.isTheSame(getOtherName(), inElement.getOtherName())
                && UtilsData.isTheSame(getPrimaryName(), inElement.getPrimaryName())
                && UtilsData.isTheSame(getReferenceID(), inElement.getReferenceID())
                && UtilsData.isTheSame(getScientificName(), inElement.getScientificName())
                && UtilsData.isTheSame(getSizeFemaleMax(), inElement.getSizeFemaleMax())
                && UtilsData.isTheSame(getSizeFemaleMin(), inElement.getSizeFemaleMin())
                && UtilsData.isTheSame(getSizeMaleMax(), inElement.getSizeMaleMax())
                && UtilsData.isTheSame(getSizeMaleMin(), inElement.getSizeMaleMin())
                && UtilsData.isTheSame(getSizeType(), inElement.getSizeType())
                && UtilsData.isTheSame(getSizeUnit(), inElement.getSizeUnit())
                && UtilsData.isTheSame(getType(), inElement.getType())
                && UtilsData.isTheSame(getWaterDependance(), inElement.getWaterDependance())
                && UtilsData.isTheSame(getWeightFemaleMax(), inElement.getWeightFemaleMax())
                && UtilsData.isTheSame(getWeightFemaleMin(), inElement.getWeightFemaleMin())
                && UtilsData.isTheSame(getWeightMaleMax(), inElement.getWeightMaleMax())
                && UtilsData.isTheSame(getWeightMaleMin(), inElement.getWeightMaleMin())
                && UtilsData.isTheSame(getWeightUnit(), inElement.getWeightUnit())
                && UtilsData.isTheSame(getWishListRating(), inElement.getWishListRating());
    }

    public <T extends ElementCore> T cloneShallow() {
        try {
            T element = (T) this.getClass().newInstance();
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
            element.setSizeMaleMax(sizeMaleMax);
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
        catch (InstantiationException ex) {
            ex.printStackTrace(System.err);
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

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