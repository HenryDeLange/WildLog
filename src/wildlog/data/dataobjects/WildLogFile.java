package wildlog.data.dataobjects;

import java.util.Calendar;
import java.util.Date;
import wildlog.html.utils.UtilsHTML;
import wildlog.data.enums.WildLogFileType;
import wildlog.utils.WildLogPaths;

public class WildLogFile {
    private String id; // The id should be in the format: location-kruger or creature-rooibok
    private String filename;
    private String fileLocation; // This is sort of used as a primary key
    private String originalFotoLocation;
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

    public String toHTML(UtilsHTML.ImageExportTypes inExportType) {
        if (fotoType.equals(WildLogFileType.IMAGE))
            // Moet die getter hier gebruik want ek wil die File().exists() doen...
            return UtilsHTML.generateHTMLImages(getFileLocation(true), inExportType);
        else
        if (fotoType.equals(WildLogFileType.MOVIE))
            return "[Movie] ";
        else
        if (fotoType.equals(WildLogFileType.OTHER))
            return "[Other File] ";
        else
            return "";
    }

    // GETTERS and SETTERS
    public String getFileLocation(boolean inGetFullpath) {
        // Dis bietjie van 'n hack, maar dit help met geskuifde folders...
        if (inGetFullpath)
            return WildLogPaths.getFullWorkspacePrefix() + fileLocation;
        else
            return fileLocation;
    }

    public Date getDate() {
        return date;
    }

    public String getOriginalFotoLocation(boolean inGetFullpath) {
        // Dis bietjie van 'n hack, maar dit help met geskuifde folders...
        if (inGetFullpath)
            return WildLogPaths.getFullWorkspacePrefix() + originalFotoLocation;
        else
            return originalFotoLocation;
    }

    public WildLogFileType getFotoType() {
        return fotoType;
    }

    public void setFileLocation(String inFileLocation) {
            fileLocation = inFileLocation;
    }

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