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

import CsvGenerator.CsvGenerator;
import KmlGenerator.objects.KmlEntry;
import java.util.ArrayList;
import java.util.List;
import wildlog.data.dataobjects.interfaces.HasFotos;
import wildlog.data.enums.AccommodationType;
import wildlog.data.enums.CateringType;
import wildlog.data.enums.GameViewRating;
import wildlog.data.enums.Habitat;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.LocationRating;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.Province;
import wildlog.utils.LatLonConverter;
import wildlog.utils.UtilsHTML;

// Foundation for the Location class
public class Location implements HasFotos, Comparable<Location> {
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
    private int latSeconds; // Old field not used anymore
    private float latSecondsFloat;
    private Longitudes longitude;
    private int lonDegrees;
    private int lonMinutes;
    private int lonSeconds;  // Old field not used anymore
    private float lonSecondsFloat;
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

    @Override
    public int compareTo(Location inLocation) {
        if (inLocation != null)
            if (name != null && inLocation.getName() != null) {
                return(name.compareToIgnoreCase(inLocation.getName()));
            }
        return 0;
    }

    public String toHTML(boolean inIsRecursive, boolean inIncludeImages) {
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
        if (inIsRecursive)
            if (visits != null)
                for (int t = 0; t < visits.size(); t++) {
                    visitsString = visitsString + visits.get(t).toHTML(inIsRecursive, inIncludeImages) + "<br/>";
                }
        String htmlLocation = "<head><title>" + name + "</title></head>";
        htmlLocation = htmlLocation + "<body>";
        htmlLocation = htmlLocation + "<H2>Location</H2>";
        htmlLocation = htmlLocation + "<b>Name:</b> " + name;
        htmlLocation = htmlLocation + "<br/>";
        htmlLocation = htmlLocation + "<br/><b>Province:</b> " + UtilsHTML.formatString(province);
        htmlLocation = htmlLocation + "<br/><b>Rating:</b> " + UtilsHTML.formatString(rating);
        htmlLocation = htmlLocation + "<br/><b>Game View Rating:</b> " + UtilsHTML.formatString(gameViewingRating);
        htmlLocation = htmlLocation + "<br/><b>Habitat:</b> " + UtilsHTML.formatString(habitatType);
        htmlLocation = htmlLocation + "<br/><b>Description:</b> " + UtilsHTML.formatString(description);
        htmlLocation = htmlLocation + "<br/><b>Directions:</b> " + UtilsHTML.formatString(directions);
        if (website != null)
            if (website.length() > 0)
                htmlLocation = htmlLocation + "<br/><b>Website:</b> <a href=\"" + UtilsHTML.formatString(website) + "\">Link</a>";
            else
                htmlLocation = htmlLocation + "<br/><b>Website:</b> " + UtilsHTML.formatString(website);
        else
            htmlLocation = htmlLocation + "<br/><b>Website:</b> " + UtilsHTML.formatString(website);
        htmlLocation = htmlLocation + "<br/><b>Email:</b> " + UtilsHTML.formatString(email);
        htmlLocation = htmlLocation + "<br/><b>Contact Number:</b> " + UtilsHTML.formatString(contactNumbers);
        htmlLocation = htmlLocation + "<br/><b>Catering:</b> " + UtilsHTML.formatString(catering);
        htmlLocation = htmlLocation + "<br/><b>Accomodation:</b> " + UtilsHTML.formatString(accommodationType);
        htmlLocation = htmlLocation + "<br/><b>Latitude:</b> " + latitude + " " + latDegrees + " " + latMinutes + " " + latSecondsFloat;
        htmlLocation = htmlLocation + "<br/><b>Longitude:</b> " + longitude + " " + lonDegrees + " " + lonMinutes + " " + lonSecondsFloat;
        htmlLocation = htmlLocation + "<br/><b>Sub Areas:</b> " + UtilsHTML.formatString(subAreasString);
        if (inIncludeImages)
            htmlLocation = htmlLocation + "</br><b>Photos:</b></br/>" + fotoString;
        if (inIsRecursive)
            htmlLocation = htmlLocation + "</br><H3>Visits:</H3><hr/>" + visitsString;
        htmlLocation = htmlLocation + "</body>";
        return htmlLocation;
    }

    public KmlEntry toKML(int inID) {
        KmlEntry entry = new KmlEntry();
        entry.setId(inID);
        entry.setName(name);
        entry.setDescription(this.toHTML(false, true));
        entry.setStyle("locationStyle");
        entry.setLatitude(LatLonConverter.getDecimalDegree(latitude, latDegrees, latMinutes, latSecondsFloat));
        entry.setLongitude(LatLonConverter.getDecimalDegree(longitude, lonDegrees, lonMinutes, lonSecondsFloat));
        return entry;
    }

    public void toCSV(CsvGenerator inCSVGenerator) {
        inCSVGenerator.addData(name);
        inCSVGenerator.addData(description);
        inCSVGenerator.addData(province);
        inCSVGenerator.addData(rating);
        inCSVGenerator.addData(gameViewingRating);
        inCSVGenerator.addData(habitatType);
        inCSVGenerator.addData(fotos);
        //inCSVGenerator.addData("Visits");
        inCSVGenerator.addData(accommodationType);
        inCSVGenerator.addData(catering);
        inCSVGenerator.addData(contactNumbers);
        inCSVGenerator.addData(website);
        inCSVGenerator.addData(email);
        inCSVGenerator.addData(directions);
        inCSVGenerator.addData(latitude);
        inCSVGenerator.addData(latDegrees);
        inCSVGenerator.addData(latMinutes);
        inCSVGenerator.addData(latSecondsFloat);
        inCSVGenerator.addData(longitude);
        inCSVGenerator.addData(lonDegrees);
        inCSVGenerator.addData(lonMinutes);
        inCSVGenerator.addData(lonSeconds);
        inCSVGenerator.addData(subAreas);
        inCSVGenerator.addData(visits);
    }

    public void doUpdate_v2() {
        latSecondsFloat = latSeconds;
        lonSecondsFloat = lonSeconds;
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
        if (fotos == null) fotos = new ArrayList<Foto>(1);
        return fotos;
    }
    
//    public Foto getPrimaryFoto() {
//        return primaryFoto;
//    }
    
    public List<Visit> getVisits() {
        if (visits == null) visits = new ArrayList<Visit>();
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

    public float getLatSecondsFloat() {
        return latSecondsFloat;
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

    public float getLonSecondsFloat() {
        return lonSecondsFloat;
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

    public void setLatSecondsFloat(float inLatSeconds) {
        latSecondsFloat = inLatSeconds;
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

    public void setLonSecondsFloat(float inLonSeconds) {
        lonSecondsFloat = inLonSeconds;
    }

    public void setLongitude(Longitudes inLongitude) {
        longitude = inLongitude;
    }

    public void setSubAreas(List<String> inSubAreas) {
        subAreas = inSubAreas;
    }

}