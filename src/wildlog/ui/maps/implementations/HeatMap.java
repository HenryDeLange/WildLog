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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractMap;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public class HeatMap extends AbstractMap<Sighting> {
    private enum MapType {HEAT_MAP_CLIENTSIDE};
    private enum HeatMapSize {SMALL, MEDIUM, LARGE, VERY_LARGE};
    private MapType activeMapType = MapType.HEAT_MAP_CLIENTSIDE;
    private HeatMapSize activeHeatMapSize = HeatMapSize.MEDIUM;
    private boolean isTransparent = false;
    private Parent displayedMap;
    private String displayedTemplate;

    
    public HeatMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("Distribution Maps (Heat)", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(8);
        // Maps
        Button btnHeatMapClient = new Button("Heat Map");
        btnHeatMapClient.setCursor(Cursor.HAND);
        btnHeatMapClient.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.HEAT_MAP_CLIENTSIDE;
            }
        });
        lstCustomButtons.add(btnHeatMapClient);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        CheckBox chkTransparent = new CheckBox("Semi-Transparent");
        chkTransparent.setCursor(Cursor.HAND);
        chkTransparent.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                isTransparent = chkTransparent.isSelected();
            }
        });
        lstCustomButtons.add(chkTransparent);
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton rdbSmall = new RadioButton("Small Radius");
        rdbSmall.setToggleGroup(toggleGroup);
        rdbSmall.setSelected(false);
        rdbSmall.setCursor(Cursor.HAND);
        rdbSmall.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeHeatMapSize = HeatMapSize.SMALL;
            }
        });
        lstCustomButtons.add(rdbSmall);
        RadioButton rdbMedium = new RadioButton("Medium Radius");
        rdbMedium.setToggleGroup(toggleGroup);
        rdbMedium.setSelected(true);
        rdbMedium.setCursor(Cursor.HAND);
        rdbMedium.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeHeatMapSize = HeatMapSize.MEDIUM;
            }
        });
        lstCustomButtons.add(rdbMedium);
        RadioButton rdbLarge = new RadioButton("Large Radius");
        rdbLarge.setToggleGroup(toggleGroup);
        rdbLarge.setSelected(false);
        rdbLarge.setCursor(Cursor.HAND);
        rdbLarge.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeHeatMapSize = HeatMapSize.LARGE;
            }
        });
        lstCustomButtons.add(rdbLarge);
        RadioButton rdbVeryLarge = new RadioButton("Very Large Radius");
        rdbVeryLarge.setToggleGroup(toggleGroup);
        rdbVeryLarge.setSelected(false);
        rdbVeryLarge.setCursor(Cursor.HAND);
        rdbVeryLarge.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeHeatMapSize = HeatMapSize.VERY_LARGE;
            }
        });
        lstCustomButtons.add(rdbVeryLarge);
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
                if (activeMapType.equals(MapType.HEAT_MAP_CLIENTSIDE)) {
                    setupChartDescriptionLabel("<html>This Heat Map can be used to show areas with higher or lower density of data points.</html>");
                    displayedMap = createHeatMapClient(lstData);
                }
                inScene.setRoot(displayedMap);
            }
        });
    }
    
    private Parent createHeatMapClient(List<Sighting> inLstSightings) {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        // Get the template file
        final char[] buffer = new char[4096];
        final StringBuilder builder = new StringBuilder(7500);
        try (Reader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("resources/heatmap.html"), "UTF-8"))) {
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
        int beginIndex = template.indexOf("//___POINTS_START___") + "//___POINTS_START___".length();
        int endIndex = template.indexOf("//___POINTS_END___");
        String gpsPointTemplate = template.substring(beginIndex, endIndex).trim();
        StringBuilder gpsBuilder = new StringBuilder(50 * inLstSightings.size());
        for (Sighting sighting : inLstSightings) {
            if (UtilsGPS.getLatDecimalDegree((DataObjectWithGPS) sighting) != 0 && UtilsGPS.getLonDecimalDegree((DataObjectWithGPS) sighting) != 0) {
                gpsBuilder.append(gpsPointTemplate.replace("(-32,", "(" + Double.toString(UtilsGPS.getLatDecimalDegree(sighting)) + ",")
                                                  .replace(", 22)", ", " + Double.toString(UtilsGPS.getLonDecimalDegree(sighting)) + ")"));
                gpsBuilder.append(System.lineSeparator());
            }
        }
        template = template.replace("//___POINTS_START___", "")
                           .replace("//___POINTS_END___", "")
                           .replace(gpsPointTemplate, gpsBuilder.toString());
        // Setup options
        StringBuilder options = new StringBuilder();
        if (isTransparent) {
            options.append("heatmap.set('opacity', 0.2);");
            options.append(System.lineSeparator());
        }
        if (activeHeatMapSize == HeatMapSize.SMALL) {
            options.append("heatmap.set('radius', 10);");
            options.append(System.lineSeparator());
        }
        else
        if (activeHeatMapSize == HeatMapSize.MEDIUM) {
            options.append("heatmap.set('radius', 25);");
            options.append(System.lineSeparator());
        }
        else
        if (activeHeatMapSize == HeatMapSize.LARGE) {
            options.append("heatmap.set('radius', 45);");
            options.append(System.lineSeparator());
        }
        else
        if (activeHeatMapSize == HeatMapSize.VERY_LARGE) {
            options.append("heatmap.set('radius', 90);");
            options.append(System.lineSeparator());
        }
        template = template.replace("//___OPTIONS___", options);
        // Set the template
        webEngine.loadContent(template);
        displayedTemplate = template;
        return webView;
    }
    
}
