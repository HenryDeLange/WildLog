/*
 * Foto.java is part of WildLog
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

import java.util.Calendar;
import java.util.Date;
import wildlog.data.dataobjects.util.UtilsHTML;
import wildlog.data.enums.FotoRating;


// Foundation for the Foto class
// NOTE: that the actual files are not deleted currently...
public class Foto {
    private String name; // Automatically made from file name
    private String description;
    private String fileLocation;
    private String originalFotoLocation;
    private FotoRating rating;
    private Date date;


    // CONSTRUCTORS:
    public Foto() {

    }
    
    public Foto(String inName, String inFileLocation, String inOriginalFotoLocation) {
        name = inName;
        fileLocation = inFileLocation;
        originalFotoLocation = inOriginalFotoLocation;
        date = Calendar.getInstance().getTime();
    }

    // METHODS:
    @Override
    public String toString() {
        return name + " - " + fileLocation;
    }

    public String toHTML() {
        return UtilsHTML.generateHTMLImages(fileLocation);
    }

    // GETTERS:
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public FotoRating getRating() {
        return rating;
    }
    
    public Date getDate() {
        return date;
    }

    public String getOriginalFotoLocation() {
        return originalFotoLocation;
    }

    // SETTERS:
    public void setName(String inName) {
            name = inName;
    }

    public void setDescription(String inDescription) {
            description = inDescription;
    }

    public void setFileLocation(String inFileLocation) {
            fileLocation = inFileLocation;
    }

    public void setRating(FotoRating inRating) {
            rating = inRating;
    }
    
    public void setDate(Date inDate) {
        date = inDate;
    }

    public void setOriginalFotoLocation(String inOriginalFotoLocation) {
        originalFotoLocation = inOriginalFotoLocation;
    }

}