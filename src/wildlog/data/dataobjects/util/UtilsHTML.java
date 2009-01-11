package wildlog.data.dataobjects.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


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
                File toDir = new File(File.separatorChar + "WildLog" + File.separatorChar + "HTML" + File.separatorChar + "Images");
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
                return "<img src='../Images/" + fromFile.getName() + "'>";
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

}