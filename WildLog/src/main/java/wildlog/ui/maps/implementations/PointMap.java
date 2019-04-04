package wildlog.ui.maps.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JLabel;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractMap;
import wildlog.ui.maps.implementations.helpers.UtilsMaps;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogApplicationTypes;
import wildlog.utils.WildLogPaths;


public class PointMap extends AbstractMap<Sighting> {
    private enum MapType {POINT_MAP_GOOGLE, POINT_MAP_BING};
    private MapType activeMapType = MapType.POINT_MAP_GOOGLE;
    private Parent displayedMap;
    private String displayedTemplate;
    private boolean showDetails;
    private boolean showThumbnails;

    
    public PointMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("Satellite Imagery Maps", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(6);
        // Maps
        ToggleButton btnPointMapGoogle = new ToggleButton("Markers on Google Maps");
        btnPointMapGoogle.setToggleGroup(BUTTON_GROUP);
        btnPointMapGoogle.setCursor(Cursor.HAND);
        btnPointMapGoogle.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.POINT_MAP_GOOGLE;
            }
        });
        lstCustomButtons.add(btnPointMapGoogle);
        ToggleButton btnPointMapBing = new ToggleButton("Markers on Bing Maps");
        btnPointMapBing.setToggleGroup(BUTTON_GROUP);
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
        if (WildLogApp.WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
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
        // Include sighting details (performance)
        CheckBox chkShowDetails = new CheckBox("Include Observation Details");
        chkShowDetails.setCursor(Cursor.HAND);
        chkShowDetails.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showDetails = chkShowDetails.isSelected();
            }
        });
        lstCustomButtons.add(chkShowDetails);
        // Include Thumbnails (performance)
        CheckBox chkShowFileThumbnails = new CheckBox("Include Observation Thumbnails");
        chkShowFileThumbnails.setCursor(Cursor.HAND);
        chkShowFileThumbnails.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showThumbnails = chkShowFileThumbnails.isSelected();
            }
        });
        lstCustomButtons.add(chkShowFileThumbnails);
    }

    @Override
    public void createMap(Scene inScene) {
        displayedMap = null;
        if (activeMapType.equals(MapType.POINT_MAP_GOOGLE)) {
            setActiveSubCategoryTitle("Markers on Google Maps");
            setupChartDescriptionLabel("<html>The data points are displayed on the imagery provided by Google Earth. Click on a marker to see more details.</html>");
            displayedMap = createPointMapGoogle(lstData);
        }
        else
        if (activeMapType.equals(MapType.POINT_MAP_BING)) {
            setActiveSubCategoryTitle("Markers on Bing Maps");
            setupChartDescriptionLabel("<html>The data points are displayed on the imagery provided by Microsoft Bing Maps. Click on a marker to see more details.</html>");
            displayedMap = createPointMapBing(lstData);
        }
        inScene.setRoot(displayedMap);
    }
    
    private Parent createPointMapGoogle(List<Sighting> inLstSightings) {
// FIXME: Die Spiderfier werk mooi in die web, maar net half decent in JavaFX WebView, 
//        In die WebView clear die pointers nie altyd mooi nadat mens 'n spiderfy event trigger nie...
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
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        String template = builder.toString();
        // Edit the template
        int beginIndex = template.indexOf("//___MAP_CLICKABLE_DATA_POINTS_START___") + "//___MAP_CLICKABLE_DATA_POINTS_START___".length();
        int endIndex = template.indexOf("//___MAP_CLICKABLE_DATA_POINTS_END___");
        String gpsPointTemplate = template.substring(beginIndex, endIndex).trim();
        StringBuilder gpsBuilder = new StringBuilder(50 * inLstSightings.size());
        for (DataObjectWithHTML sighting : inLstSightings) {
            if (UtilsGPS.hasGPSData((DataObjectWithGPS) sighting)) {
                String point = UtilsMaps.replace(gpsPointTemplate, "var markerZZZ", "var marker" + sighting.getIDField());
                point = UtilsMaps.replace(point, "LatLng(-31, 29)", "LatLng(" + UtilsGPS.getLatDecimalDegree((DataObjectWithGPS) sighting) + "," + UtilsGPS.getLonDecimalDegree((DataObjectWithGPS) sighting) + ")");
                point = UtilsMaps.replace(point, "markerZZZ.desc", "marker" + sighting.getIDField() + ".desc");
                point = UtilsMaps.replace(point, "oms.addMarker(markerZZZ", "oms.addMarker(marker" + sighting.getIDField());
                point = UtilsMaps.replace(point, "bounds.extend(markerZZZ", "bounds.extend(marker" + sighting.getIDField());
                point = UtilsMaps.replace(point, "ZZZ-title", UtilsMaps.replace(sighting.getDisplayName(), "\"", "&quot;"));
                String html = sighting.toHTML(false, showThumbnails, !showDetails, WildLogApp.getApplication(), UtilsHTMLExportTypes.ForMap, null);
                point = UtilsMaps.replace(point, "ZZZ-content", UtilsMaps.replace(UtilsMaps.replace(UtilsMaps.replace(html, "\"", "&quot;"), "\n", "<br/>"), "\r", ""));
                gpsBuilder.append(point);
                gpsBuilder.append(System.lineSeparator());
            }
        }
        template = UtilsMaps.replace(template, "//___MAP_CLICKABLE_DATA_POINTS_START___", "");
        template = UtilsMaps.replace(template, "//___MAP_CLICKABLE_DATA_POINTS_END___", "");
        template = UtilsMaps.replace(template, gpsPointTemplate, gpsBuilder.toString());
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
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        String template = builder.toString();
        // Edit the template
        int beginIndex = template.indexOf("//___PINS_START___") + "//___PINS_START___".length();
        int endIndex = template.indexOf("//___PINS_END___");
        String gpsPointTemplate = template.substring(beginIndex, endIndex).trim();
        StringBuilder gpsBuilder = new StringBuilder(50 * inLstSightings.size());
        for (DataObjectWithHTML sighting : inLstSightings) {
            if (UtilsGPS.hasGPSData((DataObjectWithGPS) sighting)) {
                String point = UtilsMaps.replace(gpsPointTemplate, "locationZZZ", "location" + sighting.getIDField());
                point = UtilsMaps.replace(point, "pinZZZ", "pin" + sighting.getIDField());
                point = UtilsMaps.replace(point, "boxZZZ", "box" + sighting.getIDField());
                point = UtilsMaps.replace(point, "Location(-33, 23)", "Location(" + UtilsGPS.getLatDecimalDegree((DataObjectWithGPS) sighting) + "," + UtilsGPS.getLonDecimalDegree((DataObjectWithGPS) sighting) + ")");
                point = UtilsMaps.replace(point, "ZZZ-title", UtilsMaps.replace(sighting.getDisplayName(), "\"", "&quot;"));
                String html = sighting.toHTML(false, showThumbnails, !showDetails, WildLogApp.getApplication(), UtilsHTMLExportTypes.ForMap, null);
                point = UtilsMaps.replace(point, "ZZZ-content", UtilsMaps.replace(UtilsMaps.replace(UtilsMaps.replace(html, "\"", "&quot;"), "\n", "<br/>"), "\r", ""));
                gpsBuilder.append(point);
                gpsBuilder.append(System.lineSeparator());
            }
        }
        template = UtilsMaps.replace(template, "//___PINS_START___", "");
        template = UtilsMaps.replace(template, "//___PINS_END___", "");
        template = UtilsMaps.replace(template, gpsPointTemplate, gpsBuilder.toString());
        // Set the template
        webEngine.loadContent(template);
        displayedTemplate = template;
        return webView;
    }

}
