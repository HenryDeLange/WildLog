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

import CsvGenerator.CsvGenerator;
import KmlGenerator.objects.KmlEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.interfaces.HasFotos;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.AreaType;
import wildlog.data.enums.Certainty;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.data.enums.SightingEvidence;
import wildlog.data.enums.ViewRating;
import wildlog.data.enums.Weather;
import wildlog.utils.LatLonConverter;

// Foundation for the Sighting class
public class Sighting implements HasFotos, Comparable<Sighting> {
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
    private SightingEvidence sightingEvidence;
    private long sightingCounter;


    // CONSTRUCTORS:
    public Sighting() {
    }

    public Sighting(long inSightingCounter) {
        sightingCounter = inSightingCounter;
    }

    public Sighting(Date inDate, Element inElement, Location inLocation, long inSightingCounter) {
        date = inDate;
        element = inElement;
        location = inLocation;
        sightingCounter = inSightingCounter;
    }

    // METHIDS:
    @Override
    public String toString() {
        return location.getName() + " (" + date.getDate() + "-" + (date.getMonth()+1) + "-" + (date.getYear()+1900) + ") " + element.getPrimaryName();
    }

    @Override
    public int compareTo(Sighting inSighting) {
        if (inSighting != null)
            if (date != null && inSighting.getDate() != null) {
                return(date.compareTo(inSighting.getDate()));
            }
        return 0;
    }

    public String toHTML(boolean inIsRecursive, boolean inIncludeImages) {
        String fotoString = "";
        if (fotos != null)
            for (int t = 0; t < fotos.size(); t++) {
                fotoString = fotoString + fotos.get(t).toHTML();
            }
        String htmlSighting = "";
        htmlSighting = htmlSighting + "<H2>" + element.getPrimaryName() + " - " + date + " - " + location.getName() + "</H2>";
        htmlSighting = htmlSighting + "<b>Date:</b> " + date;
        htmlSighting = htmlSighting + "<br/><b>Element:</b> " + element.getPrimaryName();
        htmlSighting = htmlSighting + "<br/><b>Location:</b> " + location.getName();
        htmlSighting = htmlSighting + "<br/><b>Time of Day:</b> " + timeOfDay;
        htmlSighting = htmlSighting + "<br/><b>Weather:</b> " + weather;
        htmlSighting = htmlSighting + "<br/><b>Area Type:</b> " + areaType;
        htmlSighting = htmlSighting + "<br/><b>View Rating:</b> " + viewRating;
        htmlSighting = htmlSighting + "<br/><b>Certainty:</b> " + certainty;
        htmlSighting = htmlSighting + "<br/><b>Number of Creatures:</b> " + numberOfElements;
        htmlSighting = htmlSighting + "<br/><b>Details:</b> " + details;
        htmlSighting = htmlSighting + "<br/><b>Latitude:</b> " + latitude + " " + latDegrees + " " + latMinutes + " " + latSeconds;
        htmlSighting = htmlSighting + "<br/><b>Longitude:</b> " + longitude + " " + lonDegrees + " " + lonMinutes + " " + lonSeconds;
        htmlSighting = htmlSighting + "<br/><b>Sub Area:</b> " + subArea;
        if (inIncludeImages)
            htmlSighting = htmlSighting + "</br><b>Photos:</b></br/>" + fotoString;
        return htmlSighting;
    }

    public KmlEntry toKML(int inID) {
        KmlEntry entry = new KmlEntry();
        entry.setId(inID);
        entry.setName(element.getPrimaryName());
        entry.setDescription(this.toHTML(false, true));
        entry.setStyle("elementStyle");
        if (latitude == null || longitude == null) {
            if (location.getLatitude() != null && location.getLongitude() != null) {
                if (!location.getLatitude().equals(Latitudes.NONE) && !location.getLongitude().equals(Longitudes.NONE)) {
                    entry.setLatitude(LatLonConverter.getDecimalDegree(location.getLatitude(), location.getLatDegrees(), location.getLatMinutes(), location.getLatSeconds()));
                    entry.setLongitude(LatLonConverter.getDecimalDegree(location.getLongitude(), location.getLonDegrees(), location.getLonMinutes(), location.getLonSeconds()));
                }
            }
            else {
                entry.setLatitude(0);
                entry.setLongitude(0);
            }
        }
        else
        if (latitude.equals(Latitudes.NONE) || longitude.equals(Longitudes.NONE)) {
            if (location.getLatitude() != null && location.getLongitude() != null) {
                if (!location.getLatitude().equals(Latitudes.NONE) && !location.getLongitude().equals(Longitudes.NONE)) {
                    entry.setLatitude(LatLonConverter.getDecimalDegree(location.getLatitude(), location.getLatDegrees(), location.getLatMinutes(), location.getLatSeconds()));
                    entry.setLongitude(LatLonConverter.getDecimalDegree(location.getLongitude(), location.getLonDegrees(), location.getLonMinutes(), location.getLonSeconds()));
                }
            }
            else {
                entry.setLatitude(0);
                entry.setLongitude(0);
            }
        }
        else {
            entry.setLatitude(LatLonConverter.getDecimalDegree(latitude, latDegrees, latMinutes, latSeconds));
            entry.setLongitude(LatLonConverter.getDecimalDegree(longitude, lonDegrees, lonMinutes, lonSeconds));
        }
        return entry;
    }

    public void toCSV(CsvGenerator inCSVGenerator) {
        inCSVGenerator.addData(date);
        inCSVGenerator.addData(element.getPrimaryName());
        inCSVGenerator.addData(location.getName());
        inCSVGenerator.addData(timeOfDay);
        inCSVGenerator.addData(weather);
        inCSVGenerator.addData(areaType);
        inCSVGenerator.addData(viewRating);
        inCSVGenerator.addData(certainty);
        inCSVGenerator.addData(numberOfElements);
        inCSVGenerator.addData(details);
        inCSVGenerator.addData(fotos);
        inCSVGenerator.addData(latitude);
        inCSVGenerator.addData(latDegrees);
        inCSVGenerator.addData(latMinutes);
        inCSVGenerator.addData(latSeconds);
        inCSVGenerator.addData(longitude);
        inCSVGenerator.addData(lonDegrees);
        inCSVGenerator.addData(lonMinutes);
        inCSVGenerator.addData(lonSeconds);
        inCSVGenerator.addData(subArea);
        inCSVGenerator.addData(sightingEvidence);
        //inCSVGenerator.addData(sightingCounter);
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

    @Override
    public List<Foto> getFotos() {
        if (fotos == null) fotos = new ArrayList<Foto>(1);
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

    public SightingEvidence getSightingEvidence() {
        return sightingEvidence;
    }

    public long getSightingCounter() {
        return sightingCounter;
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

    @Override
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

    public void setSightingEvidence(SightingEvidence inSightingEvidence) {
        sightingEvidence = inSightingEvidence;
    }

    public void setSightingCounter(long inSightingCounter) {
        sightingCounter = inSightingCounter;
    }
    
}