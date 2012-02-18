package wildlog.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.utils.ui.Utils;


public final class UtilsHTML {
    public static enum ImageExportTypes {
        ForKML,
        ForHTML,
        ForMap;
    }

    public static String generateHTMLImages(String inFileLocation, ImageExportTypes inExportType) {
        File fromFile = new File(inFileLocation);
        File toDir = new File(
                FilePaths.concatPaths(
                    FilePaths.concatPaths(FilePaths.WILDLOG_EXPORT_HTML.getFullPath(), "Images"),
                    Utils.stripRootFromPath(inFileLocation.substring(0, inFileLocation.lastIndexOf(File.separatorChar)),
                                            FilePaths.getFullWorkspacePrefix())));
        toDir.mkdirs();
        File toFile = new File(toDir.getAbsolutePath() + File.separatorChar + fromFile.getName());
        FileInputStream fileInput = null;
        FileOutputStream fileOutput = null;
        try {
            fileInput = new FileInputStream(fromFile);
            fileOutput = new FileOutputStream(toFile);
            byte[] tempBytes = new byte[(int) fromFile.length()];
            fileInput.read(tempBytes);
            fileOutput.write(tempBytes);
            fileOutput.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileInput.close();
                fileOutput.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        // Gebruik toLowerCase() want Google Earth herken nie die filenaam as 'n image as dit met hoofletter JPG eindig nie
        String fullpath = toFile.getAbsolutePath().toLowerCase();
        if (inExportType.equals(UtilsHTML.ImageExportTypes.ForHTML))
            //return "<img src=\"" + toFile.getAbsolutePath().toLowerCase().replaceFirst(Matcher.quoteReplacement(toFile.getAbsolutePath().toLowerCase().substring(0, 1) + ":" + FilePaths.WILDLOG_EXPORT_HTML), "..") + "\"/>  ";
            return "<img src=\"..\\" 
                    + Utils.stripRootFromPath(fullpath, 
                        FilePaths.concatPaths(FilePaths.getFullWorkspacePrefix(), FilePaths.WILDLOG_EXPORT_HTML.getRelativePath())) 
                    + "\"/>  ";
        else
        if (inExportType.equals(UtilsHTML.ImageExportTypes.ForKML))
            return "<img src=\"..\\html\\"  
                    + Utils.stripRootFromPath(fullpath, 
                        FilePaths.concatPaths(FilePaths.getFullWorkspacePrefix(), FilePaths.WILDLOG_EXPORT_HTML.getRelativePath())) 
                    + "\"/>  ";
        else
        if (inExportType.equals(UtilsHTML.ImageExportTypes.ForMap))
            return "<img src=\"file:\\" + toFile.getAbsolutePath().toLowerCase() + "\"/>  ";
        else
            return "[image error]";
    }

    public static String exportHTML(Element inElement, WildLogApp inApp) {
        File toFile = new File(FilePaths.WILDLOG_EXPORT_HTML.getFullPath() + "Creatures"  + File.separatorChar + inElement.getPrimaryName() + ".html");
        toFile.mkdirs();
        FileOutputStream fileOutput = null;
        try {
            if (toFile.exists()) toFile.delete();
            fileOutput = new FileOutputStream(toFile);
            fileOutput.write(inElement.toHTML(true, true, inApp, UtilsHTML.ImageExportTypes.ForHTML).getBytes());
            fileOutput.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                fileOutput.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return toFile.getPath();
    }

    public static String exportHTML(Location inLocation, WildLogApp inApp) {
        File toFile = new File(FilePaths.WILDLOG_EXPORT_HTML.getFullPath() + "Locations"  + File.separatorChar + inLocation.getName() + ".html");
        toFile.mkdirs();
        FileOutputStream fileOutput = null;
        try {
            if (toFile.exists()) toFile.delete();
            fileOutput = new FileOutputStream(toFile);
            fileOutput.write(inLocation.toHTML(true, true, inApp, UtilsHTML.ImageExportTypes.ForHTML).getBytes());
            fileOutput.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                fileOutput.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return toFile.getPath();
    }

    public static String exportHTML(Visit inVisit, WildLogApp inApp) {
        File toFile = new File(FilePaths.WILDLOG_EXPORT_HTML.getFullPath() + "Visits" + File.separatorChar + inVisit.getName() + ".html");
        toFile.mkdirs();
        FileOutputStream fileOutput = null;
        try {
            if (toFile.exists()) toFile.delete();
            fileOutput = new FileOutputStream(toFile);
            fileOutput.write(inVisit.toHTML(true, true, inApp, UtilsHTML.ImageExportTypes.ForHTML).getBytes());
            fileOutput.flush();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                fileOutput.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return toFile.getPath();
    }

    public static String formatString(Object inObject) {
        if (inObject == null)
            return "";
        else
            return inObject.toString();
    }

    public static String formatDate(Date inDate, boolean inShowTime) {
        if (inDate != null) {
            if (inShowTime)
                return new SimpleDateFormat("E, dd MMM yyyy (hh:mm a)").format((Date)inDate);
            else
                return new SimpleDateFormat("E, dd MMM yyyy").format((Date)inDate);
        }
        else
            return "";
    }

}