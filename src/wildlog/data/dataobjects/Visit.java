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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import wildlog.data.dataobjects.interfaces.HasFotos;
import wildlog.data.dataobjects.util.UtilsHTML;
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

    public String toHTML() {
        String sightingString = "";
        if (sightings != null)
            for (int t = 0; t < sightings.size(); t++) {
                sightingString = sightingString + sightings.get(t).toHTML() + "<br/>";
            }

        String htmlVisit = "";
        htmlVisit = htmlVisit + "<h2>" + name + "</h2><br/>";
        htmlVisit = htmlVisit + "<table border='1' width='850px'>";
        htmlVisit = htmlVisit + UtilsHTML.generateHTMLRow("Start Date", startDate, "End Date", endDate);
        htmlVisit = htmlVisit + UtilsHTML.generateHTMLRow("Game Watching", gameWatchingIntensity, "Type", type);
        htmlVisit = htmlVisit + UtilsHTML.generateHTMLRow("Description", description);
        htmlVisit = htmlVisit + "</table>";
        htmlVisit = htmlVisit + "</br><h3>Sightings:</h3>" + sightingString;
        return htmlVisit;
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