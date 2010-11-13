package wildlog.data.dataobjects;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import wildlog.utils.UtilsHTML;
import wildlog.data.enums.WildLogFileType;


// Foundation for the Foto class
public class WildLogFile {
    private String id; // The id should be in the format: location-kruger or creature-rooibok
    private String filename; // Automatically made from file name
    //private String description;
    private String fileLocation; // This is sort of used as a primary key
    private String originalFotoLocation;
    //private FotoRating rating;
    private Date date;
    private WildLogFileType fotoType;
    private boolean defaultFile = false;


    // CONSTRUCTORS:
    public WildLogFile() {
    }

    public WildLogFile(String inID) {
        id = inID;
    }
    
    public WildLogFile(String inID, String inName, String inFileLocation, String inOriginalFotoLocation, WildLogFileType inFotoType) {
        id = inID;
        filename = inName;
        fileLocation = inFileLocation;
        originalFotoLocation = inOriginalFotoLocation;
        date = Calendar.getInstance().getTime();
        fotoType = inFotoType;
    }

    public WildLogFile(String inID, String inName, String inFileLocation, String inOriginalFotoLocation, WildLogFileType inFotoType, Date inDate) {
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
        if (fotoType.equals(WildLogFileType.IMAGE))
            // Moet die getter hier gebruik want ek wil die File().exists() doen...
            return UtilsHTML.generateHTMLImages(getFileLocation());
        else
        if (fotoType.equals(WildLogFileType.MOVIE))
            return "[Movie] ";
        else
        if (fotoType.equals(WildLogFileType.OTHER))
            return "[Other File] ";
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

    public WildLogFileType getFotoType() {
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

    public void setFotoType(WildLogFileType inFotoType) {
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