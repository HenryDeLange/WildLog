package wildlog.data.dataobjects;

import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.AddFrequency;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.data.enums.WaterDependancy;
import wildlog.data.enums.WishRating;
import wildlog.utils.UtilsHTML;

// Foundation for Elements classes
// Use inheritance for animal, bird, plant, fish, insects, etc
public class Element implements Comparable<Element>, DataObjectWithHTML {
    private String primaryName; // Used for indexing (ID)
    private String otherName;
    private String scientificName;
    private String description; // Should rename to HABITAT Discription
    private String nutrition; // What food or soil the element preferes
    private WaterDependancy waterDependance; // How dependant the element is on water
    private double sizeMaleMin;
    private double sizeMaleMax;
    private double sizeFemaleMin;
    private double sizeFemaleMax;
    private double weightMaleMin;
    private double weightMaleMax;
    private double weightFemaleMin;
    private double weightFemaleMax;
    private String breedingDuration; // How long the young is developed and how long it takes to be independant
    private String breedingNumber; // The number of young produced
    //private String breedingAge; // CURRENTLY NOT USSED!!!
    //private int numberOfSightings; // The number of times a sighting for this element has been recorded (don't need its own variable, can get in other ways)
    private WishRating wishListRating; // How much I wish to see the element
    //private Habitat habitat; // This needs to be improved to be more specific (maybe select from list of habitats, can create new ones)
    private String diagnosticDescription; // A description of the element that will help to identify it in the field
    private ActiveTime activeTime; // What time of day the element is most active (morning, night, day, evening, ens.)
    private EndangeredStatus endangeredStatus; // The official endangered status of the element
    private String behaviourDescription; // Used to describe some unique behaviour
    private AddFrequency addFrequency; // How often the element is added when seen (bv always at any location, usualy only at new locations, sometimes at some locations, once, ... These values should be predefined)
//    private List<Foto> fotos; // An ArrayList of Foto objects
    //private Foto primaryFoto; // Not needed, the first image in the list will always be the main image...
    private ElementType type; // Animal, Bird, etc
    private FeedingClass feedingClass; // Carnivore, etc (this might need to be implemented on child class when converting to enums)
    //private String wildPopulations; // Only seen in wild(protected) also in wild(unprotected) - Enums?
    //private Date breedingSeasonStart;
    //private Date breedingSeasonEnd;
    private UnitsSize sizeUnit;
    private UnitsWeight weightUnit;
    private String lifespan;
    private String referenceID;
    private String distribution;

   
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
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp) {
        StringBuilder fotoString = new StringBuilder();
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile("ELEMENT-" + primaryName));
        for (int t = 0; t < fotos.size(); t++) {
            fotoString.append(fotos.get(t).toHTML());
        }
        StringBuilder htmlElement = new StringBuilder("<head><title>" + primaryName + "</title></head>");
        htmlElement.append("<body>");
        htmlElement.append("<H2>Creature</H2>");
        htmlElement.append("<b>Primary Name:</b> ").append(primaryName);
        htmlElement.append("<br/><b>Other Name:</b> ").append(UtilsHTML.formatString(otherName));
        htmlElement.append("<br/><b>Scientific Name:</b> <i>").append(UtilsHTML.formatString(scientificName)).append("</i>");
        htmlElement.append("<br/><b>Reference ID:</b> ").append(UtilsHTML.formatString(referenceID));
        htmlElement.append("<br/>");
        htmlElement.append("<br/><b>Type:</b> ").append(UtilsHTML.formatString(type));
        htmlElement.append("<br/><b>Feeding Class:</b> ").append(UtilsHTML.formatString(feedingClass));
        htmlElement.append("<br/><b>Add Frequency:</b> ").append(UtilsHTML.formatString(addFrequency));
        htmlElement.append("<br/><b>Wish Rating:</b> ").append(UtilsHTML.formatString(wishListRating));
        htmlElement.append("<br/><b>Active Time:</b> ").append(UtilsHTML.formatString(activeTime));
        htmlElement.append("<br/><b>Endangered Status:</b> ").append(UtilsHTML.formatString(endangeredStatus));
        htmlElement.append("<br/><b>Water Dependance:</b> ").append(UtilsHTML.formatString(waterDependance));
        htmlElement.append("<br/><b>Food/Nutrition:</b> ").append(UtilsHTML.formatString(nutrition));
        htmlElement.append("<br/><b>Identification:</b> ").append(UtilsHTML.formatString(diagnosticDescription));
        htmlElement.append("<br/><b>Habitat:</b> ").append(UtilsHTML.formatString(description));
        htmlElement.append("<br/><b>Distribution:</b> ").append(UtilsHTML.formatString(distribution));
        htmlElement.append("<br/><b>Behaviour:</b> ").append(UtilsHTML.formatString(behaviourDescription));
        htmlElement.append("<br/><b>Minimum Male Size:</b> ").append(UtilsHTML.formatString(sizeMaleMin)).append(" ").append(UtilsHTML.formatString(sizeUnit));
        htmlElement.append("<br/><b>Maximum Male Size:</b> ").append(UtilsHTML.formatString(sizeMaleMin)).append(" ").append(UtilsHTML.formatString(sizeUnit));
        htmlElement.append("<br/><b>Minimum Female Size:</b> ").append(UtilsHTML.formatString(sizeFemaleMin)).append(" ").append(UtilsHTML.formatString(sizeUnit));
        htmlElement.append("<br/><b>Maximum Female Size:</b> ").append(UtilsHTML.formatString(sizeFemaleMin)).append(" ").append(UtilsHTML.formatString(sizeUnit));
        htmlElement.append("<br/><b>Minimum Male Weight:</b> ").append(UtilsHTML.formatString(weightMaleMin)).append(" ").append(UtilsHTML.formatString(weightUnit));
        htmlElement.append("<br/><b>Maximum Male Weight:</b> ").append(UtilsHTML.formatString(weightMaleMin)).append(" ").append(UtilsHTML.formatString(weightUnit));
        htmlElement.append("<br/><b>Minimum Female Weight:</b> ").append(UtilsHTML.formatString(weightFemaleMin)).append(" ").append(UtilsHTML.formatString(weightUnit));
        htmlElement.append("<br/><b>Maximum Female Weight:</b> ").append(UtilsHTML.formatString(weightFemaleMin)).append(" ").append(UtilsHTML.formatString(weightUnit));
        htmlElement.append("<br/><b>Lifespan:</b> ").append(UtilsHTML.formatString(lifespan));
        htmlElement.append("<br/><b>Breeding:</b> ").append(UtilsHTML.formatString(breedingDuration));
        htmlElement.append("<br/><b>Breeding Number:</b> ").append(UtilsHTML.formatString(breedingNumber));
        if (inIncludeImages)
            htmlElement.append("<br/><b>Photos:</b><br/>").append(fotoString);
        if (inIsRecursive) {
            htmlElement.append("<br/><H2>Sightings:</H2>");
            Sighting tempSighting = new Sighting();
            tempSighting.setElementName(primaryName);
            List<Sighting> sightings = inApp.getDBI().list(tempSighting);
            for (Sighting temp : sightings) {
                htmlElement.append("<br/>").append(temp.toHTML(false, inIncludeImages, inApp));
            }
        }

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

//    public String getBreedingAge() {
//        return breedingAge;
//    }

    public WishRating getWishListRating() {
        return wishListRating;
    }
/*
    public Habitat getHabitat() {
        return habitat;
    }
*/
    public String getDiagnosticDescription() {
        return diagnosticDescription;
    }

    public ActiveTime getActiveTime() {
        return activeTime;
    }

    public EndangeredStatus getEndangeredStatus() {
        return endangeredStatus;
    }

//    @Override
//    public List<Foto> getFotos() {
//        if (fotos == null) fotos = new ArrayList<Foto>(1);
//        return fotos;
//    }
    
    public String getBehaviourDescription() {
        return behaviourDescription;
    }
    
    public AddFrequency getAddFrequency() {
        return addFrequency;
    }
    
//    public Foto getPrimaryFoto() {
//        return primaryFoto;
//    }
    
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

//    public void setBreedingAge(String inBreedingAge) {
//        breedingAge = inBreedingAge;
//    }

    public void setWishListRating(WishRating inWishListRating) {
        wishListRating = inWishListRating;
    }
/*
    public void setHabitat(Habitat inHabitat) {
        habitat = inHabitat;
    }
*/
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

//    @Override
//    public void setFotos(List<Foto> inFotos) {
//        fotos = inFotos;
//    }
    
//    public void setPrimaryfoto(Foto inFoto) {
//        primaryFoto = inFoto;
//    }
    
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

    public void setSizeFemaleMax(double sizeFemaleMax) {
        this.sizeFemaleMax = sizeFemaleMax;
    }

    public double getSizeMaleMax() {
        return sizeMaleMax;
    }

    public void setSizeMaleMax(double sizeMaleMax) {
        this.sizeMaleMax = sizeMaleMax;
    }

    public double getWeightFemaleMax() {
        return weightFemaleMax;
    }

    public void setWeightFemaleMax(double weightFemaleMax) {
        this.weightFemaleMax = weightFemaleMax;
    }

    public double getWeightMaleMax() {
        return weightMaleMax;
    }

    public void setWeightMaleMax(double weightMaleMax) {
        this.weightMaleMax = weightMaleMax;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

}