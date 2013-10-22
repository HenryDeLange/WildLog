package wildlog.html.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.data.enums.utils.WildLogThumbnailSizes;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public final class UtilsHTML {
    private static SimpleDateFormat simpleDateFormatWithTime = new SimpleDateFormat("E, dd MMM yyyy (hh:mm a)");
    private static SimpleDateFormat simpleDateFormatWithoutTime = new SimpleDateFormat("E, dd MMM yyyy");

    private UtilsHTML() {
    }

    public static String generateHTMLImages(WildLogFile inWildLogFile, UtilsHTMLExportTypes inExportType) {
        Path fromFile = inWildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.NORMAL);
        Path toFileAsRelativePath;
        if (UtilsHTMLExportTypes.ForHTML.equals(inExportType)) {
            // Create the directories
            Path thumbnailFolder = WildLogPaths.WILDLOG_EXPORT_HTML_THUMBNAILS.getAbsoluteFullPath().resolve(inWildLogFile.getRelativePath().getParent());
            try {
                Files.createDirectories(thumbnailFolder);
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            // Copy the file. (Don't replace files, but if it already exists use that copy.)
            Path thumbnailPath = thumbnailFolder.resolve(fromFile.getFileName());
            UtilsFileProcessing.copyFile(fromFile, thumbnailPath, false, true);
            // Get relative path
            toFileAsRelativePath = WildLogPaths.WILDLOG_EXPORT_HTML.getAbsoluteFullPath().relativize(thumbnailPath);
        }
        else
        if (UtilsHTMLExportTypes.ForKML.equals(inExportType)) {
            // Create the directories
            Path thumbnailFolder = WildLogPaths.WILDLOG_EXPORT_KML_THUMBNAILS.getAbsoluteFullPath().resolve(inWildLogFile.getRelativePath().getParent());
            try {
                Files.createDirectories(thumbnailFolder);
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            // Copy the file. (Don't replace files, but if it already exists use that copy.)
            Path thumbnailPath = thumbnailFolder.resolve(fromFile.getFileName());
            UtilsFileProcessing.copyFile(fromFile, thumbnailPath, false, true);
            // Get relative path
            toFileAsRelativePath = WildLogPaths.WILDLOG_EXPORT_KML.getAbsoluteFullPath().relativize(thumbnailPath);
        }
        else {
            toFileAsRelativePath = inWildLogFile.getRelativePath();
        }
        // Generate HTML segment
        if (inExportType.equals(UtilsHTMLExportTypes.ForHTML)) {
            return "<img src=\"../" + toFileAsRelativePath.toString().replace(File.separator, "/") + "\"/>  ";
        }
        else
        if (inExportType.equals(UtilsHTMLExportTypes.ForKML)) {
            // Gebruik toLowerCase() want Google Earth herken nie die filenaam as 'n image as dit met hoofletter JPG eindig nie
            return "<img src=\"../" + toFileAsRelativePath.toString().replace(File.separator, "/") + "\"/>  ";
        }
        else
        if (inExportType.equals(UtilsHTMLExportTypes.ForMap)) {
            // Gebruik URI hier om in Windows en Linux reg te werk
            return "<img src=\"" + toFileAsRelativePath.toFile().toURI().toString() + "\"/>  ";
        }
        return "[image error]";
    }

    public static Path exportHTML(DataObjectWithHTML inDataObject, WildLogApp inApp, ProgressbarTask inProgressbarTask) {
        if (inProgressbarTask != null) {
            inProgressbarTask.setMessage("Starting the HTML Export for '" + inDataObject.getDisplayName() + "' ");
            inProgressbarTask.setTaskProgress(0);
        }
        Path toFile = WildLogPaths.WILDLOG_EXPORT_HTML.getAbsoluteFullPath().resolve(inDataObject.getExportPrefix()).resolve(inDataObject.getDisplayName() + ".html");
        UtilsFileProcessing.createFileFromBytes(inDataObject.toHTML(true, true, inApp, UtilsHTMLExportTypes.ForHTML, inProgressbarTask).getBytes(), toFile);
        if (inProgressbarTask != null) {
            inProgressbarTask.setTaskProgress(100);
            inProgressbarTask.setMessage("Done with the HTML Export for '" + inDataObject.getDisplayName() + "' ");
        }
        return toFile;
    }

    public static String formatObjectAsString(Object inObject) {
        if (inObject == null) {
            return "";
        }
        else {
            return inObject.toString();
        }
    }

    public static String formatDateAsString(Date inDate, boolean inShowTime) {
        if (inDate != null) {
            if (inShowTime) {
                return simpleDateFormatWithTime.format(inDate);
            }
            else {
                return simpleDateFormatWithoutTime.format(inDate);
            }
        }
        else {
            return "";
        }
    }

    public static void appendIfNotNullNorEmpty(StringBuilder inStringBuilder, String inKey, Object inValue, boolean... inAddBreakLine) {
        if (inValue != null) {
            String temp = UtilsHTML.formatObjectAsString(inValue);
            if (!temp.isEmpty()) {
                if (((inValue instanceof Enum) && "NONE".equalsIgnoreCase(temp)) || ((inValue instanceof List) && "[]".equalsIgnoreCase(temp))) {
                    // Don't print anything
                }
                else {
                    inStringBuilder.append(inKey).append(temp);
                    if (inAddBreakLine != null && inAddBreakLine.length == 1 && inAddBreakLine[0] == true) {
                        inStringBuilder.append("<br/>");
                    }
                }
            }
        }
    }

}