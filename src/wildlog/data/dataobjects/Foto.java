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
import wildlog.data.enums.FotoType;


// Foundation for the Foto class
// NOTE: that the actual files are not deleted currently...
public class Foto {
    private String id; // The id should be in the format: location-kruger or creature-rooibok
    private String filename; // Automatically made from file name
    //private String description;
    private String fileLocation; // This is sort of used as a primary key
    private String originalFotoLocation;
    //private FotoRating rating;
    private Date date;
    private FotoType fotoType;
    private boolean defaultFile = false;


    // CONSTRUCTORS:
    public Foto() {
    }

    public Foto(String inID) {
        id = inID;
    }
    
    public Foto(String inID, String inName, String inFileLocation, String inOriginalFotoLocation, FotoType inFotoType) {
        id = inID;
        filename = inName;
        fileLocation = inFileLocation;
        originalFotoLocation = inOriginalFotoLocation;
        date = Calendar.getInstance().getTime();
        fotoType = inFotoType;
    }

    public Foto(String inID, String inName, String inFileLocation, String inOriginalFotoLocation, FotoType inFotoType, Date inDate) {
        id = inID;
        filename = inName;
        fileLocation = inFileLocation;
        originalFotoLocation = inOriginalFotoLocation;
        date = inDate;
        fotoType = inFotoType;
    }

    // METHODS:
    @Override
    public String toString() {
        return filename + " - " + fileLocation;
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
//    public String getDescription() {
//        return description;
//    }

    public String getFileLocation() {
        // Dis bietjie van 'n hack, maar dit help vir die kere wat 'n rekenaar 'n ander C drive het...
        if (new File(fileLocation.substring(2)).exists())
            return fileLocation.substring(2);
        else
            return fileLocation;
    }

//    public FotoRating getRating() {
//        return rating;
//    }
    
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
//    public void setDescription(String inDescription) {
//            description = inDescription;
//    }

    public void setFileLocation(String inFileLocation) {
            fileLocation = inFileLocation;
    }

//    public void setRating(FotoRating inRating) {
//            rating = inRating;
//    }
    
    public void setDate(Date inDate) {
        date = inDate;
    }

    public void setOriginalFotoLocation(String inOriginalFotoLocation) {
        originalFotoLocation = inOriginalFotoLocation;
    }

    public void setFotoType(FotoType inFotoType) {
        fotoType = inFotoType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isDefaultFile() {
        return defaultFile;
    }

    public void setDefaultFile(boolean defaultFile) {
        this.defaultFile = defaultFile;
    }

}