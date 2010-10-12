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


public class UtilsHTML {

    public static String generateHTMLRow(String inLabel1, Object inObject1, String inLabel2, Object inObject2) {
        if (inObject1 instanceof String) if (((String)inObject1).length() == 0) inObject1 = "No Data";
        if (inObject1 == null) inObject1 = "No Data";
        if (inObject2 instanceof String) if (((String)inObject2).length() == 0) inObject2 = "No Data";
        if (inObject2 == null) inObject2 = "No Data";
        return "<tr><td width='16%'><h4>" + inLabel1 + ":</h4></td><td width='33%'>" + inObject1 + "</td><td width='16%'><h4>" + inLabel2 + ":</h4></td><td width='33%'>" + inObject2 + "</td></tr>";
    }

    public static String generateHTMLRow(String inLabel, Object inObject) {
        if (inObject instanceof String) if (((String)inObject).length() == 0) inObject = "No Data";
        if (inObject == null) inObject = "No Data";
        return "<tr><td colspan='1'><h4>" + inLabel + ":</h4></td><td colspan='3'>" + inObject + "</td></tr>";
    }

    public static String generateHTMLImages(String inFileLocation) {
        //try {
            //URL imgURL = UtilsHTML.class.getResource(inFileLocation);
            //if (imgURL != null) {
                File fromFile = new File(inFileLocation);
                File toDir = new File(File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "HTML" + File.separatorChar + "Images" + inFileLocation.substring(0, inFileLocation.lastIndexOf(File.separatorChar)));
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
                //return "<img src='file://" + inFileLocation.toLowerCase() + "'/>";
                File temp = new File(inFileLocation);
                return "<img src='" + temp.getAbsolutePath().toLowerCase() + "'/>";
            //}
            //else {
            //    System.out.println("path is wrong " + inFileLocation);
            //}
        //}
        //catch (URISyntaxException ex) {
        //    System.out.println("Problem creating image for HTML export");
        //    ex.printStackTrace();
        //}
        //return null;
    }

    public static String exportHTML(Element inElement, WildLogApp inApp) {
        File toFile = new File(File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "HTML" + File.separatorChar + "Creatures"  + File.separatorChar + inElement.getPrimaryName() + ".html");
        toFile.mkdirs();
        FileOutputStream fileOutput = null;
        try {
            if (toFile.exists()) toFile.delete();
            fileOutput = new FileOutputStream(toFile);
            fileOutput.write(inElement.toHTML(true, true, inApp).getBytes());
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
        File toFile = new File(File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "HTML" + File.separatorChar + "Locations"  + File.separatorChar + inLocation.getName() + ".html");
        toFile.mkdirs();
        FileOutputStream fileOutput = null;
        try {
            if (toFile.exists()) toFile.delete();
            fileOutput = new FileOutputStream(toFile);
            fileOutput.write(inLocation.toHTML(true, true, inApp).getBytes());
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
        File toFile = new File(File.separatorChar + "WildLog" + File.separatorChar + "Export" + File.separatorChar + "HTML" + File.separatorChar + "Visits" + File.separatorChar + inVisit.getName() + ".html");
        toFile.mkdirs();
        FileOutputStream fileOutput = null;
        try {
            if (toFile.exists()) toFile.delete();
            fileOutput = new FileOutputStream(toFile);
            fileOutput.write(inVisit.toHTML(true, true, inApp).getBytes());
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