/*
 * Visit.java is part of WildLog
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

import java.util.Date;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.VisitType;
import wildlog.utils.UtilsHTML;

// Foundation for the Visit class
public class Visit implements Comparable<Visit>, DataObjectWithHTML {
    private String name; // Used as index (ID)
    private Date startDate;
    private Date endDate;
    private String description;
    private GameWatchIntensity gameWatchingIntensity; // How intensely did we watch game (predefined values)
    //private List<Sighting> sightings; // An ArrayList of sighting objects
    private VisitType type; // Vacation, Remote Camera, Bird Atlasing, Other
//    private List<Foto> fotos;
    private String locationName;


    // CONSTRUCTORS:
    public Visit() {
    }
    
    public Visit(String inName) {
        name = inName;
    }

    // METHODS:
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Visit inVisit) {
        if (inVisit != null)
            if (name != null && inVisit.getName() != null) {
                return(name.compareToIgnoreCase(inVisit.getName()));
            }
        return 0;
    }

    @Override
    public String toHTML(boolean inIsRecursive, boolean inIncludeImages, WildLogApp inApp) {
        String fotoString = "";
        List<Foto> fotos = inApp.getDBI().list(new Foto("VISIT-" + name));
        for (int t = 0; t < fotos.size(); t++) {
            fotoString = fotoString + fotos.get(t).toHTML();
        }
        String sightingString = "";
        if (inIsRecursive) {
            Sighting tempSighting = new Sighting();
            tempSighting.setVisitName(name);
            List<Sighting> sightings = inApp.getDBI().list(tempSighting);
            for (int t = 0; t < sightings.size(); t++) {
                sightingString = sightingString + sightings.get(t).toHTML(inIsRecursive, inIncludeImages, inApp) + "<br/>";
            }
        }

        String htmlVisit = "";
        htmlVisit = htmlVisit + "<H2>Visit</H2>";
        htmlVisit = htmlVisit + "<b>Visit:</b> " + name;
        htmlVisit = htmlVisit + "<br/>";
        htmlVisit = htmlVisit + "<br/><b>Start Date:</b> " + UtilsHTML.formatDate(startDate, false);
        htmlVisit = htmlVisit + "<br/><b>End Date:</b> " + UtilsHTML.formatDate(endDate, false);
        htmlVisit = htmlVisit + "<br/><b>Game Watching:</b> " + UtilsHTML.formatString(gameWatchingIntensity);
        htmlVisit = htmlVisit + "<br/><b>Type:</b> " + UtilsHTML.formatString(type);
        htmlVisit = htmlVisit + "<br/><b>Description:</b> " + UtilsHTML.formatString(description);
        if (inIncludeImages)
            htmlVisit = htmlVisit + "<br/><b>Photos:</b><br/>" + fotoString;
        if (inIsRecursive)
            htmlVisit = htmlVisit + "<br/><H3>Sightings:</H3>" + sightingString;
        return htmlVisit;
    }

//    public void toCSV(CsvGenerator inCSVGenerator) {
//        inCSVGenerator.addData(name);
//        inCSVGenerator.addData(startDate);
//        inCSVGenerator.addData(endDate);
//        inCSVGenerator.addData(description);
//        inCSVGenerator.addData(gameWatchingIntensity);
//        //inCSVGenerator.addData("Sightings");
//        inCSVGenerator.addData(type);
////        inCSVGenerator.addData(fotos);
//    }

    // GETTERS:
    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }
    
    public GameWatchIntensity getGameWatchingIntensity() {
        return gameWatchingIntensity;
    }

//    public List<Sighting> getSightings() {
//        if (sightings == null) sightings = new ArrayList<Sighting>();
//        return sightings;
//    }
    
    public VisitType getType() {
        return type;
    }

//    @Override
//    public List<Foto> getFotos() {
//        if (fotos == null) fotos = new ArrayList<Foto>(1);
//        return fotos;
//    }

    // SETTERS:
    public void setName(String inName) {
        name = inName;
    }

    public void setStartDate(Date inStartDate) {
        startDate = inStartDate;
    }

    public void setEndDate(Date inEndDate) {
        endDate = inEndDate;
    }

    public void setDescription(String inDescription) {
        description = inDescription;
    }
    
    public void setGameWatchingIntensity(GameWatchIntensity inGameWatchingIntensity) {
        gameWatchingIntensity = inGameWatchingIntensity;
    }

//    public void setSightings(List<Sighting> inSightings) {
//        sightings = inSightings;
//    }
    
    public void setType(VisitType inType) {
        type = inType;
    }

//    @Override
//    public void setFotos(List<Foto> inFotos) {
//        fotos = inFotos;
//    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    
}