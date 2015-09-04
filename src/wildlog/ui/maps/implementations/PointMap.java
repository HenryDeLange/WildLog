package wildlog.ui.maps.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.mapping.utils.UtilsGps;
import wildlog.ui.maps.implementations.helpers.AbstractMap;


public class PointMap extends AbstractMap<Sighting> {
    private enum MapType {POINT_MAP_GOOGLE, POINT_MAP_BING};
    private MapType activeMapType = MapType.POINT_MAP_GOOGLE;
    private Parent displayedMap;

    
    public PointMap(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Point Map", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(7);
        // Maps
        Button btnPointMapGoogle = new Button("Point Map (Google)");
        btnPointMapGoogle.setCursor(Cursor.HAND);
        btnPointMapGoogle.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.POINT_MAP_GOOGLE;
                setupChartDescriptionLabel("<html>...Map info...</html>");
            }
        });
        lstCustomButtons.add(btnPointMapGoogle);
        Button btnPointMapBing = new Button("Point Map (Bing)");
        btnPointMapBing.setCursor(Cursor.HAND);
        btnPointMapBing.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.POINT_MAP_BING;
                setupChartDescriptionLabel("<html>...Map info...</html>");
            }
        });
        lstCustomButtons.add(btnPointMapBing);
    }

    @Override
    public void createMap(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedMap = null;
                if (activeMapType.equals(MapType.POINT_MAP_GOOGLE)) {
                    displayedMap = createPointMapGoogle(lstData);
                }
                else
                if (activeMapType.equals(MapType.POINT_MAP_BING)) {
                    displayedMap = createPointMapBing(lstData);
                }
                inScene.setRoot(displayedMap);
            }
        });
    }
    
    private Parent createPointMapGoogle(List<Sighting> inLstSightings) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        // Get the template file
        final char[] buffer = new char[4096];
        final StringBuilder builder = new StringBuilder(7500);
        try (Reader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("resources/pointmap_google.html"), "UTF-8"))) {
            int length = 0;
            while (length >= 0) {
                length = in.read(buffer, 0, buffer.length);
                if (length > 0) {
                    builder.append(buffer, 0, length);
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        String template = builder.toString();
        // Edit the template
        int beginIndex = template.indexOf("//___MAP_CLICKABLE_DATA_POINTS_START___") + "//___MAP_CLICKABLE_DATA_POINTS_START___".length();
        int endIndex = template.indexOf("//___MAP_CLICKABLE_DATA_POINTS_END___");
        String gpsPointTemplate = template.substring(beginIndex, endIndex).trim();
        StringBuilder gpsBuilder = new StringBuilder(50 * inLstSightings.size());
        for (DataObjectWithHTML sighting : inLstSightings) {
            if (UtilsGps.getLatDecimalDegree((DataObjectWithGPS) sighting) != 0 && UtilsGps.getLonDecimalDegree((DataObjectWithGPS) sighting) != 0) {
                gpsBuilder.append(gpsPointTemplate.replace("var markerZZZ", "var marker" + sighting.getIDField())
                                                  .replace("LatLng(11.111, 22.222)", "LatLng(" + UtilsGps.getLatDecimalDegree((DataObjectWithGPS) sighting) + "," + UtilsGps.getLonDecimalDegree((DataObjectWithGPS) sighting) + ")")
                                                  .replace("ZZZ-title", sighting.getDisplayName().replaceAll("\"", "&quot;"))
                                                  .replace("var infowindowZZZ", "var infowindow" + sighting.getIDField())
                                                  .replace("ZZZ-content", sighting.toHTML(false, false, WildLogApp.getApplication(), UtilsHTMLExportTypes.ForHTML, null).replaceAll("\"", "&quot;"))
                                                  .replace("addListener(markerZZZ", "addListener(marker" + sighting.getIDField())
                                                  .replace("infowindowZZZ.open(map, markerZZZ", "infowindow" + sighting.getIDField() + ".open(map, marker" + sighting.getIDField())
                                                  .replace("extend(markerZZZ", "extend(marker" + sighting.getIDField()));
                gpsBuilder.append(System.lineSeparator());
            }
        }
        template = template.replace("//___MAP_CLICKABLE_DATA_POINTS_START___", "")
                           .replace("//___MAP_CLICKABLE_DATA_POINTS_END___", "")
                           .replace(gpsPointTemplate, gpsBuilder.toString());
        // Set the template
        webEngine.loadContent(template);
        return webView;
    }
    
    private Parent createPointMapBing(List<Sighting> inLstSightings) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        // Get the template file
        final char[] buffer = new char[4096];
        final StringBuilder builder = new StringBuilder(7500);
        try (Reader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("resources/pointmap_bing.html"), "UTF-8"))) {
            int length = 0;
            while (length >= 0) {
                length = in.read(buffer, 0, buffer.length);
                if (length > 0) {
                    builder.append(buffer, 0, length);
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        String template = builder.toString();
        // Edit the template
        int beginIndex = template.indexOf("//___PINS_START___") + "//___PINS_START___".length();
        int endIndex = template.indexOf("//___PINS_END___");
        String gpsPointTemplate = template.substring(beginIndex, endIndex).trim();
        StringBuilder gpsBuilder = new StringBuilder(50 * inLstSightings.size());
        for (DataObjectWithHTML sighting : inLstSightings) {
            if (UtilsGps.getLatDecimalDegree((DataObjectWithGPS) sighting) != 0 && UtilsGps.getLonDecimalDegree((DataObjectWithGPS) sighting) != 0) {
                gpsBuilder.append(gpsPointTemplate.replace("var locationZZZ", "var location" + sighting.getIDField())
                                                  .replace("Location(11.111, 22.222)", "Location(" + UtilsGps.getLatDecimalDegree((DataObjectWithGPS) sighting) + "," + UtilsGps.getLonDecimalDegree((DataObjectWithGPS) sighting) + ")")
                                                  .replace("var pinZZZ", "var pin" + sighting.getIDField())
                                                  .replace("Pushpin(locationZZZ", "Pushpin(location" + sighting.getIDField())
                                                  .replace("push(pinZZZ", "push(pin" + sighting.getIDField())
                                                  .replace("Infobox(locationZZZ", "Infobox(location" + sighting.getIDField())
                                                  .replace("ZZZ-title", sighting.getDisplayName().replaceAll("\"", "&quot;"))
                                                  .replace("ZZZ-content", sighting.toHTML(false, false, WildLogApp.getApplication(), UtilsHTMLExportTypes.ForHTML, null).replaceAll("\"", "&quot;"))
                                                  .replace("pushpin: pinZZZ", "pushpin: pin" + sighting.getIDField()));
                gpsBuilder.append(System.lineSeparator());
            }
        }
        template = template.replace("//___PINS_START___", "")
                           .replace("//___PINS_END___", "")
                           .replace(gpsPointTemplate, gpsBuilder.toString());
        // Set the template
        webEngine.loadContent(template);
        return webView;
    }
    
}
