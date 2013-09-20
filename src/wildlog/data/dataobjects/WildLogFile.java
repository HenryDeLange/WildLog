package wildlog.data.dataobjects;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import wildlog.data.enums.WildLogFileType;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;
import wildlog.utils.WildLogSystemImages;
import wildlog.utils.WildLogThumbnailSizes;

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
        this(inID, inFilename, inFilePath, inFileType, Calendar.getInstance().getTime());
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
        return "[WildLogFile=" + originalFileLocation + "]";
    }

    @Override
    public int compareTo(WildLogFile inWildLogFile) {
        if (inWildLogFile != null) {
            if (WildLogFileType.IMAGE.equals(fileType) && WildLogFileType.IMAGE.equals(inWildLogFile.getFileType())) {
                Date date1 = UtilsImageProcessing.getDateFromImage(getAbsolutePath());
                Date date2 = UtilsImageProcessing.getDateFromImage(inWildLogFile.getAbsolutePath());
                if (date1 != null && date2 != null) {
                    return date1.compareTo(date2);
                }
            }
            else {
                Date date1 = UtilsImageProcessing.getDateFromFileDate(getAbsolutePath());
                Date date2 = UtilsImageProcessing.getDateFromFileDate(inWildLogFile.getAbsolutePath());
                if (date1 != null && date2 != null) {
                    return date1.compareTo(date2);
                }
            }
        }
        return 0;
    }

    public String toHTML(UtilsHTMLExportTypes inExportType) {
        String startTag = "<a href='../../../" + getRelativePath().toString().replace(File.separator, "/") + "' target='_blank'>";
        // Moet die getAbsoluteThumbnailPath(..) hier gebruik want ek wil die File().exists() doen en dit create as dit nie bestaan nie...
        if (fileType.equals(WildLogFileType.IMAGE)) {
            return startTag + UtilsHTML.generateHTMLImages(this, inExportType) + "</a>";
        }
        else
        if (fileType.equals(WildLogFileType.MOVIE)) {
            return startTag + UtilsHTML.generateHTMLImages(WildLogSystemImages.MOVIES.getWildLogFile(), inExportType) + "</a> ";
        }
        else
        if (fileType.equals(WildLogFileType.OTHER)) {
            return startTag + UtilsHTML.generateHTMLImages(WildLogSystemImages.OTHER_FILES.getWildLogFile(), inExportType) + "</a> ";
        }
        else {
            return "";
        }
    }

    /**
     * Returns the relative path including the WildLog prefix for the workspace.
     * @return
     */
    public Path getRelativePath() {
        return Paths.get(originalFileLocation).normalize();
    }

    /**
     * Returns the absolute path of the file, including root, workspace location and relative path.
     * @return
     */
    public Path getAbsolutePath() {
        return WildLogPaths.getFullWorkspacePrefix().resolve(originalFileLocation).normalize();
    }

    /**
     * Convenience method for UtilsImageProcessing.getAbsoluteThumbnailPathAndCreate().
     * @param inSize
     * @return
     */
    public Path getAbsoluteThumbnailPath(WildLogThumbnailSizes inSize) {
        return UtilsImageProcessing.getAbsoluteThumbnailPathAndCreate(this, inSize);
    }

    // GETTERS and SETTERS
    public Date getUploadDate() {
        return uploadDate;
    }

    /**
     * This will be the relative path, as stored in the database.<br/>
     * This path should always start without a file separator.<br/>
     * The path should also always use '/' characters as path separators.<br/>
     * (Relative path for workspace: ex. WildLog/Files/Images/Observation/Kruger/IMGP3365.JPG)
     * @return
     */
    public String getDBFilePath() {
        if (originalFileLocation == null) {
            return null;
        }
        return Paths.get(originalFileLocation).normalize().toString().replace("\\", "/");
    }

    public WildLogFileType getFileType() {
        return fileType;
    }

    public void setUploadDate(Date inDate) {
        uploadDate = inDate;
    }

    /**
     * This needs to be the relative path that will be stored in the database.<br/>
     * This path should always start without a file separator.<br/>
     * The path should always use '/' characters as path separators.<br/>
     * (Relative path for workspace: ex. WildLog/Files/Images/Observation/Kruger/IMGP3365.JPG)
     * @param inFilePathFromDB
     */
    public void setDBFilePath(String inFilePathFromDB) {
        originalFileLocation = Paths.get(inFilePathFromDB).normalize().toString().replace("\\", "/");
    }

    public void setFileType(WildLogFileType inFileType) {
        fileType = inFileType;
    }

    public String getId() {
        return id;
    }

    public void setId(String inId) {
        id = inId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String inFilename) {
        filename = inFilename;
    }

    public boolean isDefaultFile() {
        return defaultFile;
    }

    public void setDefaultFile(boolean inDefaultFile) {
        defaultFile = inDefaultFile;
    }

}