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

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import wildlog.utils.UtilsHTML;
import wildlog.data.enums.FotoRating;
import wildlog.data.enums.FotoType;


// Foundation for the Foto class
// NOTE: that the actual files are not deleted currently...
public class Foto {
    private String name; // Automatically made from file name
    private String description;
    private String fileLocation;
    private String originalFotoLocation;
    private FotoRating rating;
    private Date date;
    private FotoType fotoType;


    // CONSTRUCTORS:
    public Foto() {

    }
    
    public Foto(String inName, String inFileLocation, String inOriginalFotoLocation, FotoType inFotoType) {
        name = inName;
        fileLocation = inFileLocation;
        originalFotoLocation = inOriginalFotoLocation;
        date = Calendar.getInstance().getTime();
        fotoType = inFotoType;
    }

    // METHODS:
    @Override
    public String toString() {
        return name + " - " + fileLocation;
    }

    public String toHTML() {
        if (fotoType.equals(FotoType.IMAGE))
            // Moet die getter hier gebruik want ek wil die File().exists() doen...
            return UtilsHTML.generateHTMLImages(getFileLocation());
        else
        if (fotoType.equals(FotoType.MOVIE))
            return "[Movie]";
        else
        if (fotoType.equals(FotoType.OTHER))
            return "[Other File]";
        else
            return "";
    }

    // GETTERS:
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFileLocation() {
        // Dis bietjie van 'n hack, maar dit help vir die kere wat 'n rekenaar 'n ander C drive het...
        if (new File(fileLocation.substring(2)).exists())
            return fileLocation.substring(2);
        else
            return fileLocation;
    }

    public FotoRating getRating() {
        return rating;
    }
    
    public Date getDate() {
        return date;
    }

    public String getOriginalFotoLocation() {
        // Dis bietjie van 'n hack, maar dit help vir die kere wat 'n rekenaar 'n ander C drive het...
        if (new File(originalFotoLocation.substring(2)).exists())
            return originalFotoLocation.substring(2);
        else
            return originalFotoLocation;
    }

    public FotoType getFotoType() {
        return fotoType;
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

    public void setFotoType(FotoType inFotoType) {
        fotoType = inFotoType;
    }

}