package wildlog.ui.maps.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.maps.utils.UtilsGps;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractMap;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public class PointMap extends AbstractMap<Sighting> {
    private enum MapType {POINT_MAP_GOOGLE, POINT_MAP_BING};
    private MapType activeMapType = MapType.POINT_MAP_GOOGLE;
    private Parent displayedMap;
    private String displayedTemplate;

    
    public PointMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("World Maps (Online)", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(4);
        // Maps
        Button btnPointMapGoogle = new Button("Show Points on Google Maps");
        btnPointMapGoogle.setCursor(Cursor.HAND);
        btnPointMapGoogle.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.POINT_MAP_GOOGLE;
            }
        });
        lstCustomButtons.add(btnPointMapGoogle);
        Button btnPointMapBing = new Button("Show Points on Bing Maps");
        btnPointMapBing.setCursor(Cursor.HAND);
        btnPointMapBing.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.POINT_MAP_BING;
            }
        });
        lstCustomButtons.add(btnPointMapBing);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        Hyperlink btnOpenInBrowser = new Hyperlink("View in External Web Browser");
        btnOpenInBrowser.setCursor(Cursor.HAND);
        btnOpenInBrowser.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (displayedTemplate != null && !displayedTemplate.isEmpty()) {
                    Path toFile = WildLogPaths.WILDLOG_EXPORT_HTML_TEMPORARY.getAbsoluteFullPath().resolve("TempMap_" + System.currentTimeMillis() + ".html");
                    UtilsFileProcessing.createFileFromBytes(displayedTemplate.getBytes(), toFile);
                    UtilsFileProcessing.openFile(toFile);
                }
            }
        });
        lstCustomButtons.add(btnOpenInBrowser);
    }

    @Override
    public void createMap(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedMap = null;
                if (activeMapType.equals(MapType.POINT_MAP_GOOGLE)) {
                    setupChartDescriptionLabel("<html>The data points are displayed on the imagery provided by Google Earth and Google Maps. Click on a marker to see more details.</html>");
                    displayedMap = createPointMapGoogle(lstData);
                }
                else
                if (activeMapType.equals(MapType.POINT_MAP_BING)) {
                    setupChartDescriptionLabel("<html>The data points are displayed on the imagery provided by Microsoft Bing Maps. Click on a marker to see more details.</html>");
                    displayedMap = createPointMapBing(lstData);
                }
                inScene.setRoot(displayedMap);
            }
        });
    }
    
    private Parent createPointMapGoogle(List<Sighting> inLstSightings) {
// FIXME: Die Spiderfier werk mooi in die web, en half decent in JavaFX WebView, 
//        maar in die WebView clear die pointers nie altyd mooi nadat mens 'n spiderfy event trigger nie...
//        Dalk kan ek later die JavaScript source bekyk en edit om beter te werk????
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
                                                  .replace("LatLng(44.5403, -78.5463)", "LatLng(" + UtilsGps.getLatDecimalDegree((DataObjectWithGPS) sighting) 
                                                          + "," + UtilsGps.getLonDecimalDegree((DataObjectWithGPS) sighting) + ")")
                                                  .replace("ZZZ-title", sighting.getDisplayName().replaceAll("\"", "&quot;"))
                                                  .replace("markerZZZ.desc", "marker" + sighting.getIDField() + ".desc")
                                                  .replace("ZZZ-content", sighting.toHTML(false, true, true, WildLogApp.getApplication(), 
                                                          UtilsHTMLExportTypes.ForMap, null).replaceAll("\"", "&quot;"))
                                                  .replace("oms.addMarker(markerZZZ", "oms.addMarker(marker" + sighting.getIDField())
                                                  .replace("bounds.extend(markerZZZ", "bounds.extend(marker" + sighting.getIDField()));
                gpsBuilder.append(System.lineSeparator());
            }
        }
        template = template.replace("//___MAP_CLICKABLE_DATA_POINTS_START___", "")
                           .replace("//___MAP_CLICKABLE_DATA_POINTS_END___", "")
                           .replace(gpsPointTemplate, gpsBuilder.toString());
        // Set the template
        webEngine.loadContent(template);
        displayedTemplate = template;
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
                                                  .replace("Location(11.111, 22.222)", "Location(" + UtilsGps.getLatDecimalDegree((DataObjectWithGPS) sighting) 
                                                          + "," + UtilsGps.getLonDecimalDegree((DataObjectWithGPS) sighting) + ")")
                                                  .replace("var pinZZZ", "var pin" + sighting.getIDField())
                                                  .replace("Pushpin(locationZZZ", "Pushpin(location" + sighting.getIDField())
                                                  .replace("push(pinZZZ", "push(pin" + sighting.getIDField())
                                                  .replace("Infobox(locationZZZ", "Infobox(location" + sighting.getIDField())
                                                  .replace("ZZZ-title", sighting.getDisplayName().replaceAll("\"", "&quot;"))
                                                  .replace("ZZZ-content", sighting.toHTML(false, true, true, WildLogApp.getApplication(), 
                                                          UtilsHTMLExportTypes.ForMap, null).replaceAll("\"", "&quot;"))
                                                  .replace("pushpin: pinZZZ", "pushpin: pin" + sighting.getIDField()));
                gpsBuilder.append(System.lineSeparator());
            }
        }
        template = template.replace("//___PINS_START___", "")
                           .replace("//___PINS_END___", "")
                           .replace(gpsPointTemplate, gpsBuilder.toString());
        // Set the template
        webEngine.loadContent(template);
        displayedTemplate = template;
        return webView;
    }

}
