package wildlog.html.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public final class UtilsHTML {
    public static enum ImageExportTypes {
        ForKML,
        ForHTML,
        ForMap;
    }

    public static String generateHTMLImages(String inFileLocation, ImageExportTypes inExportType) {
        File fromFile = new File(inFileLocation);
        File toDir = new File(
                WildLogPaths.concatPaths(true,
                    WildLogPaths.concatPaths(true, WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath(), "Images"),
                    WildLogPaths.stripRootFromPath(inFileLocation.substring(0, inFileLocation.lastIndexOf(File.separatorChar)),
                                            WildLogPaths.getFullWorkspacePrefix())));
        toDir.mkdirs();
        File toFile = new File(toDir.getAbsolutePath() + File.separatorChar + fromFile.getName());
        UtilsFileProcessing.copyFile(fromFile, toFile);
        String fullpath = toFile.getAbsolutePath();
        if (inExportType.equals(UtilsHTML.ImageExportTypes.ForHTML)) {
            return "<img src=\".." + File.separatorChar
                    + WildLogPaths.stripRootFromPath(fullpath,
                        WildLogPaths.concatPaths(true, WildLogPaths.getFullWorkspacePrefix(), WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath()))
                    .replaceAll(Pattern.quote(File.separator), "/")
                    + "\"/>  ";
        }
        else
        if (inExportType.equals(UtilsHTML.ImageExportTypes.ForKML)) {
            // Gebruik toLowerCase() want Google Earth herken nie die filenaam as 'n image as dit met hoofletter JPG eindig nie
            fullpath = fullpath.toLowerCase();
            return "<img src=\".." + File.separatorChar + "html" + File.separatorChar
                    + WildLogPaths.stripRootFromPath(fullpath,
                        WildLogPaths.concatPaths(true, WildLogPaths.getFullWorkspacePrefix(), WildLogPaths.WILDLOG_EXPORT_HTML.getRelativePath()))
                    .replaceAll(Pattern.quote(File.separator), "/")
                    + "\"/>  ";
        }
        else
        if (inExportType.equals(UtilsHTML.ImageExportTypes.ForMap)) {
            // Gebruik URI hier om in Windows en Linux reg te werk
            return "<img src=\"" + toFile.toURI().toString() + "\"/>  ";
        }
        return "[image error]";
    }

    public static String exportHTML(Element inElement, WildLogApp inApp) {
        copyDefaultFilePlaceholderImages(inApp);
        File toFile = new File(WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath() + "Creatures"  + File.separatorChar + inElement.getPrimaryName() + ".html");
        toFile.mkdirs();
        FileOutputStream fileOutput = null;
        try {
            if (toFile.exists())
                toFile.delete();
            fileOutput = new FileOutputStream(toFile);
            fileOutput.write(inElement.toHTML(true, true, inApp, UtilsHTML.ImageExportTypes.ForHTML).getBytes());
            fileOutput.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            try {
                if (fileOutput != null) {
                    fileOutput.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return toFile.getPath();
    }

    public static String exportHTML(Location inLocation, WildLogApp inApp) {
        copyDefaultFilePlaceholderImages(inApp);
        File toFile = new File(WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath() + "Places"  + File.separatorChar + inLocation.getName() + ".html");
        toFile.mkdirs();
        FileOutputStream fileOutput = null;
        try {
            if (toFile.exists())
                toFile.delete();
            fileOutput = new FileOutputStream(toFile);
            fileOutput.write(inLocation.toHTML(true, true, inApp, UtilsHTML.ImageExportTypes.ForHTML).getBytes());
            fileOutput.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            try {
                if (fileOutput != null) {
                    fileOutput.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return toFile.getPath();
    }

    public static String exportHTML(Visit inVisit, WildLogApp inApp) {
        copyDefaultFilePlaceholderImages(inApp);
        File toFile = new File(WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath() + "Periods" + File.separatorChar + inVisit.getName() + ".html");
        toFile.mkdirs();
        FileOutputStream fileOutput = null;
        try {
            if (toFile.exists())
                toFile.delete();
            fileOutput = new FileOutputStream(toFile);
            fileOutput.write(inVisit.toHTML(true, true, inApp, UtilsHTML.ImageExportTypes.ForHTML).getBytes());
            fileOutput.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            try {
                if (fileOutput != null) {
                    fileOutput.close();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return toFile.getPath();
    }

    public static String formatObjectAsString(Object inObject) {
        if (inObject == null)
            return "";
        else
            return inObject.toString();
    }

    public static String formatDateAsString(Date inDate, boolean inShowTime) {
        if (inDate != null) {
            if (inShowTime)
                return new SimpleDateFormat("E, dd MMM yyyy (hh:mm a)").format((Date)inDate);
            else
                return new SimpleDateFormat("E, dd MMM yyyy").format((Date)inDate);
        }
        else
            return "";
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

    public static void copyDefaultFilePlaceholderImages(WildLogApp inApp) {
        // Make the directory if it doesn't exist yet.
        String path = WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath();
        File tempFile = new File(path);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        // Copy the files.
        File noFile = new File(WildLogPaths.concatPaths(true, WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath(), "NoFile.png"));
        if (!noFile.exists()) {
            UtilsFileProcessing.copyFile(inApp.getClass().getResourceAsStream("resources/icons/NoFile.png"), noFile);
        }
        File movie = new File(WildLogPaths.concatPaths(true, WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath(), "Movie.png"));
        if (!movie.exists()) {
            UtilsFileProcessing.copyFile(inApp.getClass().getResourceAsStream("resources/icons/Movie.png"), movie);
        }
        File other = new File(WildLogPaths.concatPaths(true, WildLogPaths.WILDLOG_EXPORT_HTML.getFullPath(), "OtherFile.png"));
        if (!other.exists()) {
            UtilsFileProcessing.copyFile(inApp.getClass().getResourceAsStream("resources/icons/OtherFile.png"), other);
        }
    }

}