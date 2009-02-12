/*
 * Sighting.java is part of WildLog
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

import java.io.File;
import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.util.UtilsHTML;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.AreaType;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.Weather;

// Foundation for the Sighting class
public class Sighting {
    private Date date; // must include time
    private Element element;
    private Location location;
    private ActiveTimeSpesific timeOfDay; // General description of the time (bv early morning, night, ens) Predefined
    private Weather weather; // Predefined set of possible values
    private AreaType areaType; // In what 'mini-habitat' was the element seen (bv river, open patch, ens) Predefined
    private ViewRating viewRating; // How close, long, ens the element was seen
    private Certainty certainty; // How sure you are that it was identified correctly
    private int numberOfElements; // How many where present at sighting
    private String details;
    private List<Foto> fotos; // The difference between this and the Element foto is that Element fotos are the "good" ones, these are more foto records...
    private Latitudes latitude;
    private int latDegrees;
    private int latMinutes;
    private int latSeconds;
    private Longitudes longitude;
    private int lonDegrees;
    private int lonMinutes;
    private int lonSeconds;
    private String subArea;


    // CONSTRUCTORS:
    public Sighting() {
    }

    public Sighting(Date inDate, Element inElement, Location inLocation) {
        date = inDate;
        element = inElement;
        location = inLocation;
    }

    // METHIDS:
    @Override
    public String toString() {
        return location.getName() + File.separatorChar + element.getPrimaryName() + File.separatorChar + date.getDate() + "-" + (date.getMonth()+1) + "-" + (date.getYear()+1900);
    }

    public String toHTML() {
        String fotoString = "";
        if (fotos != null)
            for (int t = 0; t < fotos.size(); t++) {
                fotoString = fotoString + fotos.get(t).toHTML();
            }
        String htmlSighting = "";
        htmlSighting = htmlSighting + "<h2>" + element.getPrimaryName() + " - " + date + " - " + location.getName() + "</h2><br/>";
        htmlSighting = htmlSighting + "<table border='1' width='850px'>";
        htmlSighting = htmlSighting + UtilsHTML.generateHTMLRow("Date", date);
        htmlSighting = htmlSighting + UtilsHTML.generateHTMLRow("Element", element.getPrimaryName(), "Location", location.getName());
        htmlSighting = htmlSighting + UtilsHTML.generateHTMLRow("Time of Day", timeOfDay, "Weather", weather);
        htmlSighting = htmlSighting + UtilsHTML.generateHTMLRow("Area Type", areaType, "View Rating", viewRating);
        htmlSighting = htmlSighting + UtilsHTML.generateHTMLRow("Certainty", certainty, "Number of Creatures", numberOfElements);
        htmlSighting = htmlSighting + UtilsHTML.generateHTMLRow("Details", details);
        htmlSighting = htmlSighting + UtilsHTML.generateHTMLRow("Latitude", latitude, "Latitude Degrees", latDegrees);
        htmlSighting = htmlSighting + UtilsHTML.generateHTMLRow("Latitude Minutes", latMinutes, "Latitude Seconds", latSeconds);
        htmlSighting = htmlSighting + UtilsHTML.generateHTMLRow("Longitude", longitude, "Longitude Degrees", lonDegrees);
        htmlSighting = htmlSighting + UtilsHTML.generateHTMLRow("Longitude Minutes", lonMinutes, "Longitude Seconds", lonSeconds);
        htmlSighting = htmlSighting + UtilsHTML.generateHTMLRow("Sub Area", subArea);
        htmlSighting = htmlSighting + "</table>";
        htmlSighting = htmlSighting + "</br><h3>Fotos:</h3>" + fotoString;
        return htmlSighting;
    }

    // GETTERS:
    public Date getDate() {
        return date;
    }

    public Element getElement() {
        return element;
    }

    public Location getLocation() {
        return location;
    }

    public ActiveTimeSpesific getTimeOfDay() {
        return timeOfDay;
    }

    public Weather getWeather() {
        return weather;
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public ViewRating getViewRating() {
        return viewRating;
    }

    public Certainty getCertainty() {
        return certainty;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public String getDetails() {
        return details;
    }

    public List<Foto> getFotos() {
        return fotos;
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

    public String getSubArea() {
        if (subArea == null) subArea = "";
        return subArea;
    }

    // SETTERS:
    public void setDate(Date inDate) {
        date = inDate;
    }

    public void setElement(Element inElement) {
        element = inElement;
    }

    public void setLocation(Location inLocation) {
        location = inLocation;
    }

    public void setTimeOfDay(ActiveTimeSpesific inTimeOfDay) {
        timeOfDay = inTimeOfDay;
    }

    public void setWeather(Weather inWeather) {
        weather = inWeather;
    }

    public void setAreaType(AreaType inAreaType) {
        areaType = inAreaType;
    }

    public void setViewRating(ViewRating inViewRating) {
        viewRating = inViewRating;
    }

    public void setCertainty(Certainty inCertainty) {
        certainty = inCertainty;
    }

    public void setNumberOfElements(int inNumberOfElements) {
        numberOfElements = inNumberOfElements;
    }

    public void setDetails(String inDetails) {
        details = inDetails;
    }

    public void setFotos(List<Foto> inFotos) {
        fotos = inFotos;
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

    public void setSubArea(String inSubArea) {
        subArea = inSubArea;
    }
    
}