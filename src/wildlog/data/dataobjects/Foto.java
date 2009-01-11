package wildlog.data.dataobjects;

import java.io.File;
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
    private FotoRating rating;
    private Date date;


    // CONSTRUCTORS:
    public Foto() {

    }
    
    public Foto(String inName) {
        name = inName;
        fileLocation = File.separatorChar + "WildLog" + File.separatorChar + "Images" + File.separatorChar + name;
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

}