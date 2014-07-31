package wildlog.data.dataobjects;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import wildlog.data.enums.WildLogFileType;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.html.utils.UtilsHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogPaths;
import wildlog.utils.WildLogSystemImages;


public class WildLogFile extends WildLogFileCore implements Comparable<WildLogFile> {

    public WildLogFile() {
        super();
    }

    public WildLogFile(String inID) {
        super(inID);
    }

    public WildLogFile(String inID, String inFilename, String inFilePath, WildLogFileType inFileType) {
        super(inID, inFilename, inFilePath, inFileType);
    }

    public WildLogFile(String inID, String inFilename, String inFilePath, WildLogFileType inFileType, Date inUploadDate) {
        super(inID, inFilename, inFilePath, inFileType, inUploadDate);
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
        String startTag = "<a href=\"../../../" + getRelativePath().toString().replace(File.separator, "/") + "\" target=\"_blank\">";
        if (fileType.equals(WildLogFileType.IMAGE)) {
            if (UtilsHTMLExportTypes.ForFancyHTML.equals(inExportType)) {
                return UtilsHTML.generateHTMLImages(this, inExportType)
                        + "<div class=\"SliderName_2Description\">"
                        + startTag + "View Original" + "</a>   "
                        + startTag.replace("target=\"_blank\"", "rel=\"lightbox\"") + "View Larger</a>"
                        + "</div>";
            }
            else {
                return startTag + UtilsHTML.generateHTMLImages(this, inExportType) + "</a>"/* + "<div class=\"SliderName_2Description\">" + getFilename() + "</div>"*/;
            }
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
     * Returns the relative path (similar to the value stored in the database) excluding the
     * WildLog Workspace prefix.
     * (Relative path for workspace: ex. Files/Images/Observation/Kruger/IMGP3365.JPG)
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
        if (WildLogFileType.IMAGE.equals(getFileType())) {
            return UtilsImageProcessing.getAbsoluteThumbnailPathAndCreate(this, inSize);
        }
        else
        if (WildLogFileType.MOVIE.equals(getFileType())) {
            return UtilsImageProcessing.getAbsoluteThumbnailPathAndCreate(WildLogSystemImages.MOVIES.getWildLogFile(), inSize);
        }
        else
        if (WildLogFileType.OTHER.equals(getFileType())) {
            return UtilsImageProcessing.getAbsoluteThumbnailPathAndCreate(WildLogSystemImages.OTHER_FILES.getWildLogFile(), inSize);
        }
        return UtilsImageProcessing.getAbsoluteThumbnailPathAndCreate(WildLogSystemImages.NO_FILES.getWildLogFile(), inSize);
    }

    /**
     * This will be the relative path, as stored in the database.<br/>
     * This path should never start without a file separator.<br/>
     * The path should also always use '/' characters as path separators.<br/>
     * (Relative path for workspace: ex. Files/Images/Observation/Kruger/IMGP3365.JPG)
     * @return
     */
    @Override
    public String getDBFilePath() {
        if (originalFileLocation == null) {
            return null;
        }
        return Paths.get(originalFileLocation).normalize().toString().replace("\\", "/");
    }

    /**
     * This needs to be the relative path that will be stored in the database.<br/>
     * This path should never start without a file separator.<br/>
     * The path should always use '/' characters as path separators.<br/>
     * (Relative path for workspace: ex. Files/Images/Observation/Kruger/IMGP3365.JPG)
     * @param inFilePathFromDB
     */
    @Override
    public void setDBFilePath(String inFilePathFromDB) {
        originalFileLocation = Paths.get(inFilePathFromDB).normalize().toString().replace("\\", "/");
    }

}