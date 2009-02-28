/*
 * Location.java is part of WildLog
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
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.Habitat;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LocationRating;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Province;

// Foundation for the Location class
public class Location implements HasFotos {
    private String name; // Used as index (ID)
    private String description;
    private Province province; // For locations outside south africa the country name must be used
    //private int numberOfVisits; // (This can be determined from the ArrayList of visits' size)
    private LocationRating rating;
    private GameViewRating gameViewingRating;
    private Habitat habitatType; // Select from predefined list what the locations habitat is like (bv semi-desert, fynbos, ens)
    private List<Foto> fotos; // An ArrayList of Foto objects of the location
    //private Foto primaryFoto; // Primary fotos and maps of the location (String of the path)
    private List<Visit> visits; // An ArrayList of visit objects
    //private ArrayList wantedElements; // An ArrayList of wanted Elements that are possible to see at the location (some are links, others are string). This will be a arraylist of type <Element> and not the derived types
    //private Date accommodationLastUpdated; // This date indicates how old the other information regarding the accommodation is
    private List<AccommodationType> accommodationType; // For example tent, timeshare, ens
    private CateringType catering; // Self catering or not
    //private ArrayList accommodationPrice; // The price for each of the types
    private String contactNumbers; // <main>(code) ### #### / <other>(code) ### ####
    //private String address;
    private String website;
    private String email;
    private String directions;
    private Latitudes latitude;
    private int latDegrees;
    private int latMinutes;
    private int latSeconds;
    private Longitudes longitude;
    private int lonDegrees;
    private int lonMinutes;
    private int lonSeconds;
    private List<String> subAreas;

    
    // CONSTRUCTORS:
    public Location() {
    }
    
    public Location(String inName) {
        name = inName;
    }

    // METHODS:
    @Override
    public String toString() {
        return name;
    }

    public String toHTML() {
        String fotoString = "";
        if (fotos != null)
            for (int t = 0; t < fotos.size(); t++) {
                fotoString = fotoString + fotos.get(t).toHTML();
            }
        String subAreasString = "";
        if (subAreas != null)
            for (int t = 0; t < subAreas.size(); t++) {
                subAreasString = subAreasString + subAreas.get(t);
            }
        String visitsString = "";
        if (visits != null)
            for (int t = 0; t < visits.size(); t++) {
                visitsString = visitsString + visits.get(t).toHTML() + "<br/>";
            }
        String htmlLocation = "<head><title>" + name + "</title></head>";
        htmlLocation = htmlLocation + "<body>";
        htmlLocation = htmlLocation + "<h2>" + name + "</h2><br/>";
        htmlLocation = htmlLocation + "<table border='1' width='850px'>";
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Province", province, "Rating", rating);
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Game View Rating", gameViewingRating, "Habitat", habitatType);
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Description", description);
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Directions", directions);
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Website", website);
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Email", email, "Contact Number", contactNumbers);
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Catering", catering, "Accomodation", accommodationType);
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Latitude", latitude, "Latitude Degrees", latDegrees);
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Latitude Minutes", latMinutes, "Latitude Seconds", latSeconds);
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Longitude", longitude, "Longitude Degrees", lonDegrees);
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Longitude Minutes", lonMinutes, "Longitude Seconds", lonSeconds);
        htmlLocation = htmlLocation + UtilsHTML.generateHTMLRow("Sub Areas", subAreasString);
        htmlLocation = htmlLocation + "</table>";
        htmlLocation = htmlLocation + "</br><h3>Fotos:</h3>" + fotoString;
        htmlLocation = htmlLocation + "</br><h3>Visits:</h3>" + visitsString;
        htmlLocation = htmlLocation + "</body>";
        return htmlLocation;
    }

    // GETTERS:
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Province getProvince() {
        return province;
    }
    
    public LocationRating getRating() {
        return rating;
    }

    public GameViewRating getGameViewingRating() {
        return gameViewingRating;
    }

    public Habitat getHabitatType() {
        return habitatType;
    }
    
    @Override
    public List<Foto> getFotos() {
        return fotos;
    }
    
//    public Foto getPrimaryFoto() {
//        return primaryFoto;
//    }
    
    public List<Visit> getVisits() {
        return visits;
    }
    
    public List<AccommodationType> getAccommodationType() {
        return accommodationType;
    }
    
    public String getContactNumbers() {
        return contactNumbers;
    }
    
    public CateringType getCatering() {
        return catering;
    }
    
    public String getDirections() {
        return directions;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getWebsite() {
        return website;
    }

    public int getLatDegrees() {
        return latDegrees;
    }

    public int getLatMinutes() {
        return latMinutes;
    }

    public int getLatSeconds() {
        return latSeconds;
    }

    public Latitudes getLatitude() {
        return latitude;
    }

    public int getLonDegrees() {
        return lonDegrees;
    }

    public int getLonMinutes() {
        return lonMinutes;
    }

    public int getLonSeconds() {
        return lonSeconds;
    }

    public Longitudes getLongitude() {
        return longitude;
    }

    public List<String> getSubAreas() {
        if (subAreas == null) subAreas = new ArrayList<String>();
        if (subAreas.size() == 0) subAreas.add("None");
        if (subAreas.size() > 0)
            if (!subAreas.contains("None"))
                subAreas.add("None");
        return subAreas;
    }


    // SETTERS:
    public void setName(String inName) {
        name = inName;
    }

    public void setDescription(String inDescription) {
        description = inDescription;
    }

    public void setProvince(Province inProvince) {
        province = inProvince;
    }

    public void setRating(LocationRating inRating) {
        rating = inRating;
    }

    public void setGameViewingRating(GameViewRating inGameViewingRating) {
        gameViewingRating = inGameViewingRating;
    }

    public void setHabitatType(Habitat inHabitatType) {
        habitatType = inHabitatType;
    }

    @Override
    public void setFotos(List<Foto> inFotos) {
        fotos = inFotos;
    }
    
//    public void setPrimaryFoto(Foto inFoto) {
//        primaryFoto = inFoto;
//    }
    
    public void setVisits(List<Visit> inVisits) {
        visits = inVisits;
    }
    
    public void setAccommodationType(List<AccommodationType> inAccommodationType) {
        accommodationType = inAccommodationType;
    }
    
    public void setContactNumbers(String inContactNumbers) {
        contactNumbers = inContactNumbers;
    }

    public void setCatering(CateringType inCatering) {
        catering = inCatering;
    }

    public void setDirections(String inDirections) {
        directions = inDirections;
    }

    public void setEmail(String inEmail) {
        email = inEmail;
    }

    public void setWebsite(String inWebsite) {
        website = inWebsite;
    }

    public void setLatDegrees(int inLatDegrees) {
        latDegrees = inLatDegrees;
    }

    public void setLatMinutes(int inLatMinutes) {
        latMinutes = inLatMinutes;
    }

    public void setLatSeconds(int inLatSeconds) {
        latSeconds = inLatSeconds;
    }

    public void setLatitude(Latitudes inLatitude) {
        latitude = inLatitude;
    }

    public void setLonDegrees(int inLonDegrees) {
        lonDegrees = inLonDegrees;
    }

    public void setLonMinutes(int inLonMinutes) {
        lonMinutes = inLonMinutes;
    }

    public void setLonSeconds(int inLonSeconds) {
        lonSeconds = inLonSeconds;
    }

    public void setLongitude(Longitudes inLongitude) {
        longitude = inLongitude;
    }

    public void setSubAreas(List<String> inSubAreas) {
        subAreas = inSubAreas;
    }

}