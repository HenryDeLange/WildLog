package wildlog.maps.kml.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.maps.kml.UtilsKML;


public class KmlGenerator {
    // Variables:
    private String kmlPath;


    // Methods:
    public void generateFile(Map<String, List<KmlEntry>> inEntries, List<KmlStyle> inStyles) {
        FileWriter file = null;
        try {
            file = new FileWriter(kmlPath);
            file.write(generateStream(inEntries, inStyles));
            file.flush();
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        finally {
            try {
                file.close();
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
        }
    }

    public String generateStream(Map<String, List<KmlEntry>> inEntries, List<KmlStyle> inStyles) {
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><kml xmlns=\"http://earth.google.com/kml/2.1\"><Document>");
        for (KmlStyle style : inStyles) {
            builder.append("<Style id=\"");
            builder.append(style.getName());
            builder.append("\"><IconStyle id=\"");
            builder.append(style.getIconName());
            builder.append("\"><Icon><href>");
            builder.append(style.getIconPath());
            builder.append("</href></Icon></IconStyle></Style>");
        }
        List<String> keyList = new ArrayList<String>(inEntries.keySet());
        Collections.sort(keyList);
        for (String key : keyList) {
            builder.append("<Folder><name>").append(UtilsKML.getXmlFriendlyString(key)).append("</name>");
            for (KmlEntry entry : inEntries.get(key)) {
                builder.append("<Placemark><name>");
                builder.append(entry.getName());
                builder.append("</name><description>");
                builder.append(entry.getDescription());
                builder.append("</description><styleUrl>#");
                builder.append(entry.getStyle());
                builder.append("</styleUrl><Point><coordinates>");
                builder.append(entry.getLongitude());
                builder.append(",");
                builder.append(entry.getLatitude());
                builder.append("</coordinates></Point></Placemark>");
            }
            builder.append("</Folder>");
        }
        builder.append("</Document></kml>");
        return builder.toString();
    }


    // Getters and Setters:
    public String getKmlPath() {
        return kmlPath;
    }

    public void setKmlPath(String inKmlPath) {
        kmlPath = inKmlPath;
    }

}
