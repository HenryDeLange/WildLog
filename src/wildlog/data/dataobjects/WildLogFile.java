package wildlog.data.dataobjects;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import wildlog.data.enums.WildLogFileType;
import wildlog.html.utils.UtilsHTML;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;

public class WildLogFile implements Comparable<WildLogFile> {
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

    @Override
    public int compareTo(WildLogFile inWildLogFile) {
        if (WildLogFileType.IMAGE.equals(fileType) && inWildLogFile != null && WildLogFileType.IMAGE.equals(inWildLogFile.getFileType())) {
            File file1 = new File(this.getFilePath(true));
            File file2 = new File(inWildLogFile.getFilePath(true));
            Date date1 = UtilsImageProcessing.getDateFromImage(file1);
            Date date2 = UtilsImageProcessing.getDateFromImage(file2);
            if (date1 != null && date2 != null) {
                return date1.compareTo(date2);
            }
        }
        return 0;
    }

    public String toHTML(UtilsHTML.ImageExportTypes inExportType) {
        if (fileType.equals(WildLogFileType.IMAGE))
            // Moet die getter hier gebruik want ek wil die File().exists() doen...
            return "<a href='" + getFilePath(true) + "' target='_blank'>"
                    + UtilsHTML.generateHTMLImages(getThumbnailPath(UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM), inExportType) + "</a>";
        else
        if (fileType.equals(WildLogFileType.MOVIE))
            return "<a href='" + getFilePath(true) + "' target='_blank'>"
                    + UtilsHTML.generateHTMLImages(UtilsImageProcessing.getThumbnail(
                        new File(WildLogPaths.concatPaths(true, WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath(), "Movie.png")),
                        UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM), inExportType) + "</a> ";
        else
        if (fileType.equals(WildLogFileType.OTHER))
            return "<a href='" + getFilePath(true) + "' target='_blank'>"
                    + UtilsHTML.generateHTMLImages(UtilsImageProcessing.getThumbnail(
                        new File(WildLogPaths.concatPaths(true, WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath(), "OtherFile.png")),
                        UtilsImageProcessing.THUMBNAIL_SIZE_MEDIUM), inExportType) + "</a> ";
        else
            return "";
    }

    public String getThumbnailPath(int inSize) {
        return UtilsImageProcessing.getThumbnail(
                WildLogPaths.concatPaths(true, WildLogPaths.getFullWorkspacePrefix(),originalFileLocation),
                inSize);
    }

    // GETTERS and SETTERS
    public Date getUploadDate() {
        return uploadDate;
    }

    public String getFilePath(boolean inGetFullpath) {
        // Dis bietjie van 'n hack, maar dit help met geskuifde folders...
        if (inGetFullpath)
            return WildLogPaths.concatPaths(true, WildLogPaths.getFullWorkspacePrefix(), originalFileLocation);
        else
            return originalFileLocation;
    }

    public WildLogFileType getFileType() {
        return fileType;
    }

    public void setUploadDate(Date inDate) {
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