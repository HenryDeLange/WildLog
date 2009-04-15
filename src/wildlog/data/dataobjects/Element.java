/*
 * Element.java is part of WildLog
 *
 * Copyright (C) 2009 Henry James de Lange
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wildlog.data.dataobjects;


import java.util.ArrayList;
import java.util.List;
import wildlog.data.dataobjects.interfaces.HasFotos;
import wildlog.data.dataobjects.util.UtilsHTML;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.AddFrequency;
import wildlog.data.enums.ElementType;
import wildlog.data.enums.EndangeredStatus;
import wildlog.data.enums.FeedingClass;
import wildlog.data.enums.Habitat;
import wildlog.data.enums.UnitsSize;
import wildlog.data.enums.UnitsWeight;
import wildlog.data.enums.WaterDependancy;
import wildlog.data.enums.WishRating;

// Foundation for Elements classes
// Use inheritance for animal, bird, plant, fish, insects, etc
public class Element implements HasFotos {
    private String primaryName; // Used for indexing (ID)
    private String otherName;
    private String scientificName;
    private String description;
    private String nutrition; // What food or soil the element preferes
    private WaterDependancy waterDependance; // How dependant the element is on water
    private double sizeMaleAverage; // Might later split all "Averages" into min and max
    private double sizeFemaleAverage; // Measured in centimeters
    private double weightMaleAverage; // Measusred in kilograms
    private double weightFemaleAverage;
    private String breedingDuration; // How long the young is developed and how long it takes to be independant
    private double breedingNumber; // The number of young produced
    private String breedingAge; // CURRENTLY NOT USSED!!!
    //private int numberOfSightings; // The number of times a sighting for this element has been recorded (don't need its own variable, can get in other ways)
    private WishRating wishListRating; // How much I wish to see the element
    //private Habitat habitat; // This needs to be improved to be more specific (maybe select from list of habitats, can create new ones)
    private String diagnosticDescription; // A description of the element that will help to identify it in the field
    private ActiveTime activeTime; // What time of day the element is most active (morning, night, day, evening, ens.)
    private EndangeredStatus endangeredStatus; // The official endangered status of the element
    private String behaviourDescription; // Used to describe some unique behaviour
    private AddFrequency addFrequency; // How often the element is added when seen (bv always at any location, usualy only at new locations, sometimes at some locations, once, ... These values should be predefined)
    private List<Foto> fotos; // An ArrayList of Foto objects
    //private Foto primaryFoto; // Not needed, the first image in the list will always be the main image...
    private ElementType type; // Animal, Bird, etc
    private FeedingClass feedingClass; // Carnivore, etc (this might need to be implemented on child class when converting to enums)
    //private String wildPopulations; // Only seen in wild(protected) also in wild(unprotected) - Enums?
    //private Date breedingSeasonStart;
    //private Date breedingSeasonEnd;
    private UnitsSize sizeUnit;
    private UnitsWeight weightUnit;
    private String lifespan;

   
    // CONSTRUCTORS:
    public Element() {
    }

    public Element(String inEnglishName) {
        primaryName = inEnglishName;
    }
    
    public Element(ElementType inElementType) {
        type = inElementType;
    }
    
    
    // METHODS:
    @Override
    public String toString() {
        return primaryName;
    }

    public String toHTML() {
        String fotoString = "";
        if (fotos != null)
            for (int t = 0; t < fotos.size(); t++) {
                fotoString = fotoString + fotos.get(t).toHTML();
            }

        String htmlElement = "<head><title>" + primaryName + "</title></head>";
        htmlElement = htmlElement + "<body>";
        htmlElement = htmlElement + "<h2>" + primaryName + "</h2><h3/>(" + otherName + ")</h3><br/><h3/><i>[" + scientificName + "]</i></h3><br/>";
        htmlElement = htmlElement + "<table border='1' width='850px'>";
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Type", type, "Feeding Class", feedingClass);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Add Frequency", addFrequency, "Wish Rating", wishListRating);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Active Time", activeTime, "Endangered Status", endangeredStatus);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Water Dependance", waterDependance, "",""/*"Habitat", habitat*/);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Food/Nutrition", nutrition);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Description", description);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Behaviour", behaviourDescription);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Diagnostic", diagnosticDescription);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Average Male Size", sizeMaleAverage, "Size Units", sizeUnit);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Average Female Size", sizeFemaleAverage, "Size Units", sizeUnit);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Average Male Weight", weightMaleAverage, "Weight Units", weightUnit);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Average Female Weight", weightFemaleAverage, "Weight Units", weightUnit);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Age", lifespan);
        htmlElement = htmlElement + UtilsHTML.generateHTMLRow("Breeding Duration", breedingDuration, "Breeding Number", breedingNumber);
        htmlElement = htmlElement + "</table>";
        htmlElement = htmlElement + "</br><h3>Fotos:</h3>" + fotoString;
        htmlElement = htmlElement + "</body>";
        return htmlElement;
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

    public String getDescription() {
        return description;
    }

    public String getNutrition() {
        return nutrition;
    }

    public WaterDependancy getWaterDependance() {
        return waterDependance;
    }

    public double getSizeMaleAverage() {
        return sizeMaleAverage;
    }

    public double getSizeFemaleAverage() {
        return sizeFemaleAverage;
    }

    public double getWeightMaleAverage() {
        return weightMaleAverage;
    }

    public double getWeightFemaleAverage() {
        return weightFemaleAverage;
    }

    public String getBreedingDuration() {
        return breedingDuration;
    }

    public double getBreedingNumber() {
        return breedingNumber;
    }

    public String getBreedingAge() {
        return breedingAge;
    }

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

    @Override
    public List<Foto> getFotos() {
        if (fotos == null) fotos = new ArrayList<Foto>(1);
        return fotos;
    }
    
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

    public void setDescription(String inDescription) {
        description = inDescription;
    }

    public void setNutrition(String inNutrition) {
        nutrition = inNutrition;
    }

    public void setWaterDependance(WaterDependancy inWaterDependance) {
        waterDependance = inWaterDependance;
    }

    public void setSizeMaleAverage(double inSizeMaleAverage) {
        sizeMaleAverage = inSizeMaleAverage;
    }

    public void setSizeFemaleAverage(double inSizeFemaleAverage) {
        sizeFemaleAverage = inSizeFemaleAverage;
    }

    public void setWeightMaleAverage(double inWeightMaleAverage) {
        weightMaleAverage = inWeightMaleAverage;
    }

    public void setWeightFemaleAverage(double inWeightFemaleAverage) {
        weightFemaleAverage = inWeightFemaleAverage;
    }

    public void setBreedingDuration(String inBreedingDuration) {
        breedingDuration = inBreedingDuration;
    }

    public void setBreedingNumber(double inBreedingNumber) {
        breedingNumber = inBreedingNumber;
    }

    public void setBreedingAge(String inBreedingAge) {
        breedingAge = inBreedingAge;
    }

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

    @Override
    public void setFotos(List<Foto> inFotos) {
        fotos = inFotos;
    }
    
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

}