package wildlog.data.dataobjects;

import java.util.Calendar;
import java.util.Date;
import wildlog.html.utils.UtilsHTML;
import wildlog.data.enums.WildLogFileType;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;

public class WildLogFile {
    private String id; // The id should be in the format: location-kruger or creature-rooibok
    private String filename;
    private String originalFileLocation; // This is used as the DB table ID
    private Date uploadDate;
    private WildLogFileType fileType;
    private boolean defaultFile = false;

    // CONSTRUCTORS:
    public WildLogFile() {
    }

    public WildLogFile(String inID) {
        id = inID;
    }

    public WildLogFile(String inID, String inFilename, String inFilePath, WildLogFileType inFileType) {
        id = inID;
        filename = inFilename;
        originalFileLocation = inFilePath;
        uploadDate = Calendar.getInstance().getTime();
        fileType = inFileType;
    }

    public WildLogFile(String inID, String inFilename, String inFilePath, WildLogFileType inFileType, Date inUploadDate) {
        id = inID;
        filename = inFilename;
        originalFileLocation = inFilePath;
        uploadDate = inUploadDate;
        fileType = inFileType;
    }

    // METHODS:
    @Override
    public String toString() {
        return filename + " - " + originalFileLocation;
    }

    public String toHTML(UtilsHTML.ImageExportTypes inExportType) {
        if (fileType.equals(WildLogFileType.IMAGE))
            // Moet die getter hier gebruik want ek wil die File().exists() doen...
            return "<a href='" + getFilePath(true) + "' target='_blank'>"
                    + UtilsHTML.generateHTMLImages(getThumbnailPath(UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM), inExportType) + "</a>";
        else
        if (fileType.equals(WildLogFileType.MOVIE))
            return "[<a href='" + getFilePath(true) + "' target='_blank'>"
                    // FIXME: Kan nie dit nou al doen nie want dis tricky om die file binne die JAR te access...
//                    + UtilsHTML.generateHTMLImages(UtilsImageProcessing.getThumbnail(
//                        WildLogApp.class.getResource("resources/icons/Movie.png").toString(),
//                        UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM), inExportType)
                    + "Movie</a>] ";
        else
        if (fileType.equals(WildLogFileType.OTHER))
            return "[<a href='" + getFilePath(true) + "' target='_blank'>"
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

    public String getFilePath(boolean inGetFullpath) {
        // Dis bietjie van 'n hack, maar dit help met geskuifde folders...
        if (inGetFullpath)
            return WildLogPaths.concatPaths(WildLogPaths.getFullWorkspacePrefix(), originalFileLocation);
        else
            return originalFileLocation;
    }

    public WildLogFileType getFileType() {
        return fileType;
    }

    public void setDate(Date inDate) {
        uploadDate = inDate;
    }

    public void setFilePath(String inOriginalFileLocation) {
        originalFileLocation = inOriginalFileLocation;
    }

    public void setFileType(WildLogFileType inFileType) {
        fileType = inFileType;
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