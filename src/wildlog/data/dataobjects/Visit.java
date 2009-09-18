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

import CsvGenerator.CsvGenerator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.interfaces.HasFotos;
import wildlog.utils.UtilsHTML;
import wildlog.data.enums.GameWatchIntensity;
import wildlog.data.enums.VisitType;

// Foundation for the Visit class
public class Visit implements HasFotos {
    private String name; // Used as index (ID)
    private Date startDate;
    private Date endDate;
    private String description;
    private GameWatchIntensity gameWatchingIntensity; // How intensely did we watch game (predefined values)
    private List<Sighting> sightings; // An ArrayList of sighting objects
    private VisitType type; // Vacation, Remote Camera, Bird Atlasing, Other
    private List<Foto> fotos;


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

    public String toHTML(boolean inIsRecursive, boolean inIncludeImages) {
        String fotoString = "";
        if (fotos != null)
            for (int t = 0; t < fotos.size(); t++) {
                fotoString = fotoString + fotos.get(t).toHTML();
            }
        String sightingString = "";
        if (inIsRecursive)
            if (sightings != null)
                for (int t = 0; t < sightings.size(); t++) {
                    sightingString = sightingString + sightings.get(t).toHTML(inIsRecursive, inIncludeImages) + "<br/>";
                }

        String htmlVisit = "";
        htmlVisit = htmlVisit + "<H2>" + name + "</H2>";
        htmlVisit = htmlVisit + "<b>Start Date:</b> " + startDate;
        htmlVisit = htmlVisit + "<br/><b>End Date:</b> " + endDate;
        htmlVisit = htmlVisit + "<br/><b>Game Watching:</b> " + gameWatchingIntensity;
        htmlVisit = htmlVisit + "<br/><b>Type:</b> " + type;
        htmlVisit = htmlVisit + "<br/><b>Description:</b> " + description;
        if (inIncludeImages)
            htmlVisit = htmlVisit + "<br/><b>Photos:</b></br/>" + fotoString;
        if (inIsRecursive)
            htmlVisit = htmlVisit + "</br><H3>Sightings:</H3>" + sightingString;
        return htmlVisit;
    }

    public void toCSV(CsvGenerator inCSVGenerator) {
        inCSVGenerator.addData(name);
        inCSVGenerator.addData(startDate);
        inCSVGenerator.addData(endDate);
        inCSVGenerator.addData(description);
        inCSVGenerator.addData(gameWatchingIntensity);
        //inCSVGenerator.addData("Sightings");
        inCSVGenerator.addData(type);
        inCSVGenerator.addData(fotos);
    }

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

    public List<Sighting> getSightings() {
        if (sightings == null) sightings = new ArrayList<Sighting>();
        return sightings;
    }
    
    public VisitType getType() {
        return type;
    }

    @Override
    public List<Foto> getFotos() {
        if (fotos == null) fotos = new ArrayList<Foto>(1);
        return fotos;
    }

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

    public void setSightings(List<Sighting> inSightings) {
        sightings = inSightings;
    }
    
    public void setType(VisitType inType) {
        type = inType;
    }

    @Override
    public void setFotos(List<Foto> inFotos) {
        fotos = inFotos;
    }

}