package wildlog.data.dataobjects;

import java.util.Calendar;
import java.util.Date;
import wildlog.WildLogApp;
import wildlog.html.utils.UtilsHTML;
import wildlog.data.enums.WildLogFileType;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;

public class WildLogFile {
    private String id; // The id should be in the format: location-kruger or creature-rooibok
    private String filename;
    private String originalFileLocation;
    private Date uploadDate;
    private WildLogFileType fileType;
    private boolean defaultFile = false;

    // CONSTRUCTORS:
    public WildLogFile() {
    }

    public WildLogFile(String inID) {
        id = inID;
    }

    public WildLogFile(String inID, String inName, String inOriginalFileLocation, WildLogFileType inFotoType) {
        id = inID;
        filename = inName;
        originalFileLocation = inOriginalFileLocation;
        uploadDate = Calendar.getInstance().getTime();
        fileType = inFotoType;
    }

    public WildLogFile(String inID, String inName, String inOriginalFileLocation, WildLogFileType inFotoType, Date inUploadDate) {
        id = inID;
        filename = inName;
        originalFileLocation = inOriginalFileLocation;
        uploadDate = inUploadDate;
        fileType = inFotoType;
    }

    // METHODS:
    @Override
    public String toString() {
        return filename + " - " + originalFileLocation;
    }

    public String toHTML(UtilsHTML.ImageExportTypes inExportType) {
        if (fileType.equals(WildLogFileType.IMAGE))
            // Moet die getter hier gebruik want ek wil die File().exists() doen...
            return "<a href='" + getOriginalFotoLocation(true) + "' target='_blank'>"
                    + UtilsHTML.generateHTMLImages(getThumbnailPath(UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM), inExportType) + "</a>";
        else
        if (fileType.equals(WildLogFileType.MOVIE))
            return "[<a href='" + getOriginalFotoLocation(true) + "' target='_blank'>"
                    // FIXME: Kan nie dit nou al doen nie want dis tricky om die file binne die JAR te access...
//                    + UtilsHTML.generateHTMLImages(UtilsImageProcessing.getThumbnail(
//                        WildLogApp.class.getResource("resources/icons/Movie.png").toString(),
//                        UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM), inExportType)
                    + "Movie</a>] ";
        else
        if (fileType.equals(WildLogFileType.OTHER))
            return "[<a href='" + getOriginalFotoLocation(true) + "' target='_blank'>"
                    // FIXME: Kan nie dit nou al doen nie want dis tricky om die file binne die JAR te access...
//                    + UtilsHTML.generateHTMLImages(UtilsImageProcessing.getThumbnail(
//                        WildLogApp.class.getResource("resources/icons/OtherFile.png").toString(),
//                        UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM), inExportType)
                    + "Other File</a>] ";
        else
            return "";
    }

    public String getThumbnailPath(int inSize) {
        return UtilsImageProcessing.getThumbnail(
                WildLogPaths.concatPaths(WildLogPaths.getFullWorkspacePrefix(),originalFileLocation),
                inSize);
    }

    // GETTERS and SETTERS
    public Date getUploadDate() {
        return uploadDate;
    }

    public String getOriginalFotoLocation(boolean inGetFullpath) {
        // Dis bietjie van 'n hack, maar dit help met geskuifde folders...
        if (inGetFullpath)
            return WildLogPaths.concatPaths(WildLogPaths.getFullWorkspacePrefix(), originalFileLocation);
        else
            return originalFileLocation;
    }

    public WildLogFileType getFotoType() {
        return fileType;
    }

    public void setDate(Date inDate) {
        uploadDate = inDate;
    }

    public void setOriginalFotoLocation(String inOriginalFotoLocation) {
        originalFileLocation = inOriginalFotoLocation;
    }

    public void setFotoType(WildLogFileType inFotoType) {
        fileType = inFotoType;
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