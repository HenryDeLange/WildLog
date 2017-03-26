package wildlog.ui.maps.implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javax.swing.JLabel;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractMap;
import wildlog.ui.maps.implementations.helpers.UtilsMaps;
import wildlog.ui.utils.UtilsTime;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public class HeatMap extends AbstractMap<Sighting> {
    private enum MapType {TOTAL_OBSERVATIONS_MAP, TOTAL_ELEMENTS_MAP, ABUNDANCE_MAP, RICHNESS_MAP, SAMPLE_EFFORT_MAP};
    private enum HeatMapSize {SMALL, MEDIUM, LARGE, VERY_LARGE};
    private MapType activeMapType = MapType.TOTAL_OBSERVATIONS_MAP;
    private HeatMapSize activeHeatMapSize = HeatMapSize.MEDIUM;
    private boolean isTransparent = false;
    private Parent displayedMap;
    private String displayedTemplate;
    private enum SplitIndicator {
        SHORT(7), 
        NORMAL(14), 
        LONG(28),
        NEVER(Integer.MAX_VALUE);
        
        private final int days;

        private SplitIndicator(int inDays) {
            days = inDays;
        }

        public int getDays() {
            return days;
        }

        @Override
        public String toString() {
            switch (this) {
                case SHORT:
                case NORMAL:
                case LONG:
                    return "Split Periods after " + days + " days";
                case NEVER:
                    return "Don't Split Periods";
                default:
                    return super.toString();
            }
        }
    };
    private final ComboBox<HeatMap.SplitIndicator> cmbSplitIndicator;

    
    public HeatMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("Distribution Heat Maps", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(13);
        // Maps
        ToggleButton btnHeatMapClient = new ToggleButton("Observation Count Map");
        btnHeatMapClient.setToggleGroup(BUTTON_GROUP);
        btnHeatMapClient.setCursor(Cursor.HAND);
        btnHeatMapClient.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.TOTAL_OBSERVATIONS_MAP;
            }
        });
        lstCustomButtons.add(btnHeatMapClient);
        ToggleButton btnTotalElementsMapClient = new ToggleButton("Creature Count Map");
        btnTotalElementsMapClient.setToggleGroup(BUTTON_GROUP);
        btnTotalElementsMapClient.setCursor(Cursor.HAND);
        btnTotalElementsMapClient.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.TOTAL_ELEMENTS_MAP;
            }
        });
        lstCustomButtons.add(btnTotalElementsMapClient);
        ToggleButton btnSampleEffortMapClient = new ToggleButton("Sampling Effort Map");
        btnSampleEffortMapClient.setToggleGroup(BUTTON_GROUP);
        btnSampleEffortMapClient.setCursor(Cursor.HAND);
        btnSampleEffortMapClient.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.SAMPLE_EFFORT_MAP;
            }
        });
        lstCustomButtons.add(btnSampleEffortMapClient);
        ToggleButton btnAbundanceMapClient = new ToggleButton("Abundance of Observations Map");
        btnAbundanceMapClient.setToggleGroup(BUTTON_GROUP);
        btnAbundanceMapClient.setCursor(Cursor.HAND);
        btnAbundanceMapClient.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.ABUNDANCE_MAP;
            }
        });
        lstCustomButtons.add(btnAbundanceMapClient);
        ToggleButton btnRichnessMapClient = new ToggleButton("Richness of Creatures Map");
        btnRichnessMapClient.setToggleGroup(BUTTON_GROUP);
        btnRichnessMapClient.setCursor(Cursor.HAND);
        btnRichnessMapClient.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.RICHNESS_MAP;
            }
        });
        lstCustomButtons.add(btnRichnessMapClient);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        cmbSplitIndicator = new ComboBox<>(FXCollections.observableArrayList(HeatMap.SplitIndicator.values()));
        cmbSplitIndicator.setVisibleRowCount(4);
        cmbSplitIndicator.setCursor(Cursor.HAND);
        lstCustomButtons.add(cmbSplitIndicator);
        cmbSplitIndicator.getSelectionModel().select(HeatMap.SplitIndicator.NEVER);
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

// TODO: Maak offline heatmaps ook (sien http://stackoverflow.com/questions/13299045/geotools-render-a-gridcoverage2d-to-a-heat-map)
        
    }

    @Override
    public void createMap(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedMap = null;
                if (activeMapType.equals(MapType.TOTAL_OBSERVATIONS_MAP)) {
                    setActiveSubCategoryTitle("Observation Count Map");
                    setupChartDescriptionLabel("<html>This Heat Map can be used to show areas with higher or lower density of data points.</html>");
                    displayedMap = createSightingsHeatMapClient(lstData);
                }
                else
                if (activeMapType.equals(MapType.TOTAL_ELEMENTS_MAP)) {
                    setActiveSubCategoryTitle("Creature Count Map");
                    setupChartDescriptionLabel("<html>This Heat Map can be used to show areas with higher or lower density of recorded Creatures.</html>");
                    displayedMap = createElementsHeatMapClient(lstData);
                }
                else
                if (activeMapType.equals(MapType.ABUNDANCE_MAP)) {
                    setActiveSubCategoryTitle("Abundance of Observations Map");
                    setupChartDescriptionLabel("<html>This map can be used as a simplified Observation Abundance Map."
                            + "<br/>It shows the number of Observations, at each GPS location, devided by the number of active days for each Period (based on the start and end dates)."
                            + "<br/><b>Note:</b> This map works best when comparing data where each Observation for a Period have the same GPS location "
                            + "and all Periods have similar durations. "
                            + "Such as camera trapp data.</html>");
                    displayedMap = createAbundanceMapClient(lstData);
                }
                else
                if (activeMapType.equals(MapType.RICHNESS_MAP)) {
                    setActiveSubCategoryTitle("Richness of Creatures Map");
                    setupChartDescriptionLabel("<html>This map can be used as a simplified Creature Richness Map."
                            + "<br/>It shows the number of Creatures, at each GPS location, devided by the number of active days for each Period (based on the start and end dates)."
                            + "<br/><b>Note:</b> This map works best when comparing data where each Observation for a Period have the same GPS location "
                            + "and all Periods have similar durations. "
                            + "Such as camera trap data.</html>");
                    displayedMap = createRichnessMapClient(lstData);
                }
                else
                if (activeMapType.equals(MapType.SAMPLE_EFFORT_MAP)) {
                    setActiveSubCategoryTitle("Sampling Effort Map");
                    setupChartDescriptionLabel("<html>This map can be used as a simplified Sampling Effort Map."
                            + "<br/>It shows for each GPS point how many days the associated Period was active. (The list of unique GPS points is based on the active Obseravations. The duration is based on the Period's start and end dates.)"
                            + "<br/><b>Note:</b> This map works best when comparing data where each Observation for a Period have the same GPS location. "
                            + "Such as camera trap data.</html>");
                    displayedMap = createSampleEffortMapClient(lstData);
                }
                inScene.setRoot(displayedMap);
            }
        });
    }
    
    private Parent createSightingsHeatMapClient(List<Sighting> inLstSightings) {
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
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        String template = builder.toString();
        // Edit the template
        int beginIndex = template.indexOf("//___POINTS_START___") + "//___POINTS_START___".length();
        int endIndex = template.indexOf("//___POINTS_END___");
        String gpsPointTemplate = template.substring(beginIndex, endIndex).trim();
        StringBuilder gpsBuilder = new StringBuilder(50 * inLstSightings.size());
        Map<String, HeatPoint> mapHeatPoints = new HashMap<>();
        for (Sighting sighting : inLstSightings) {
            if (UtilsGPS.hasGPSData(sighting)) {
                double lat = UtilsGPS.getLatDecimalDegree(sighting);
                double lon = UtilsGPS.getLonDecimalDegree(sighting);
                String key = lat + ":" + lon;
                HeatPoint heatPoint = mapHeatPoints.get(key);
                if (heatPoint == null) {
                    heatPoint = new HeatPoint();
                    heatPoint.lat = lat;
                    heatPoint.lon = lon;
                    mapHeatPoints.put(key, heatPoint);
                }
                heatPoint.weight++;
            }
        }
        for (HeatPoint  heatPoint : mapHeatPoints.values()) {
            String point = UtilsMaps.replace(gpsPointTemplate, "LatLng(-32,", "LatLng(" + Double.toString(heatPoint.lat) + ",");
            point = UtilsMaps.replace(point, ", 22), w", ", " + Double.toString(heatPoint.lon) + "), w");
            point = UtilsMaps.replace(point, "weight: 1.0}", "weight: " + heatPoint.weight + "}");
            gpsBuilder.append(point);
            gpsBuilder.append(System.lineSeparator());
        }
        template = UtilsMaps.replace(template, "//___POINTS_START___", "");
        template = UtilsMaps.replace(template, "//___POINTS_END___", "");
        template = UtilsMaps.replace(template, gpsPointTemplate, gpsBuilder.toString());
        // Setup options
        StringBuilder options = new StringBuilder(60);
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
        template = UtilsMaps.replace(template, "//___OPTIONS___", options.toString());
        // Set the template
        webEngine.loadContent(template);
        displayedTemplate = template;
        return webView;
    }
    
    private Parent createElementsHeatMapClient(List<Sighting> inLstSightings) {
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
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        String template = builder.toString();
        // Edit the template
        int beginIndex = template.indexOf("//___POINTS_START___") + "//___POINTS_START___".length();
        int endIndex = template.indexOf("//___POINTS_END___");
        String gpsPointTemplate = template.substring(beginIndex, endIndex).trim();
        StringBuilder gpsBuilder = new StringBuilder(50 * inLstSightings.size());
        Map<String, HeatPoint> mapHeatPoints = new HashMap<>();
        for (Sighting sighting : inLstSightings) {
            if (UtilsGPS.hasGPSData(sighting)) {
                double lat = UtilsGPS.getLatDecimalDegree(sighting);
                double lon = UtilsGPS.getLonDecimalDegree(sighting);
                String key = sighting.getElementName() + ":" + sighting.getVisitName() + ":" + lat + ":" + lon;
                HeatPoint heatPoint = mapHeatPoints.get(key);
                if (heatPoint == null) {
                    heatPoint = new HeatPoint();
                    heatPoint.lat = lat;
                    heatPoint.lon = lon;
                    heatPoint.value = sighting.getElementName() + ":" + sighting.getVisitName();
                    heatPoint.weight = 1;
                    mapHeatPoints.put(key, heatPoint);
                }
            }
        }
        for (HeatPoint  heatPoint : mapHeatPoints.values()) {
            String point = UtilsMaps.replace(gpsPointTemplate, "LatLng(-32,", "LatLng(" + Double.toString(heatPoint.lat) + ",");
            point = UtilsMaps.replace(point, ", 22), w", ", " + Double.toString(heatPoint.lon) + "), w");
            point = UtilsMaps.replace(point, "weight: 1.0}", "weight: " + heatPoint.weight + "}");
            gpsBuilder.append(point);
            gpsBuilder.append(System.lineSeparator());
        }
        template = UtilsMaps.replace(template, "//___POINTS_START___", "");
        template = UtilsMaps.replace(template, "//___POINTS_END___", "");
        template = UtilsMaps.replace(template, gpsPointTemplate, gpsBuilder.toString());
        // Setup options
        StringBuilder options = new StringBuilder(60);
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
        template = UtilsMaps.replace(template, "//___OPTIONS___", options.toString());
        // Set the template
        webEngine.loadContent(template);
        displayedTemplate = template;
        return webView;
    }
    
    private Parent createAbundanceMapClient(List<Sighting> inLstSightings) {
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
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        String template = builder.toString();
        // Edit the template
        int beginIndex = template.indexOf("//___POINTS_START___") + "//___POINTS_START___".length();
        int endIndex = template.indexOf("//___POINTS_END___");
        String gpsPointTemplate = template.substring(beginIndex, endIndex).trim();
        StringBuilder gpsBuilder = new StringBuilder(50 * inLstSightings.size());
        Map<String, HeatPoint> mapHeatPoints = new HashMap<>();
        Map<String, Long> mapVisitDuration = new HashMap<>();
        for (Sighting sighting : inLstSightings) {
            if (UtilsGPS.hasGPSData(sighting)) {
                // Get the visit data
                String visitDurationKey = sighting.getVisitName();
                // Lyk my nie dis nodig om die visits self hier te cache nie... Maar dalk eendag...
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitName(), Visit.class);
                if (visit != null && visit.getStartDate() != null) {
                    if (!cmbSplitIndicator.getSelectionModel().getSelectedItem().equals(HeatMap.SplitIndicator.NEVER)) {
                        visitDurationKey = visitDurationKey + ":" + getSplitNumber(visit, sighting);
                    }
                    if (!mapVisitDuration.containsKey(visitDurationKey)) {
                        LocalDate endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                        if (endDate == null) {
                            endDate = LocalDate.now();
                        }
                        long days = ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), endDate);
                        if (days > cmbSplitIndicator.getSelectionModel().getSelectedItem().getDays()) {
                            days = cmbSplitIndicator.getSelectionModel().getSelectedItem().getDays();
                        }
                        mapVisitDuration.put(visitDurationKey, days);
                    }
                }
                else {
                    // If this visit does not have a valid date range, then don't process it, continue to the next record instead
                    continue;
                }
                // Add the heat map data
                double lat = UtilsGPS.getLatDecimalDegree(sighting);
                double lon = UtilsGPS.getLonDecimalDegree(sighting);
                String heatMapKey = visitDurationKey + ":" + lat + ":" + lon;
                HeatPoint heatPoint = mapHeatPoints.get(heatMapKey);
                if (heatPoint == null) {
                    heatPoint = new HeatPoint();
                    heatPoint.lat = lat;
                    heatPoint.lon = lon;
                    heatPoint.value = visitDurationKey;
                    mapHeatPoints.put(heatMapKey, heatPoint);
                }
                heatPoint.weight++;
            }
        }
        for (HeatPoint  heatPoint : mapHeatPoints.values()) {
            String point = UtilsMaps.replace(gpsPointTemplate, "LatLng(-32,", "LatLng(" + Double.toString(heatPoint.lat) + ",");
            point = UtilsMaps.replace(point, ", 22), w", ", " + Double.toString(heatPoint.lon) + "), w");
            point = UtilsMaps.replace(point, "weight: 1.0}", "weight: " + (Math.round((double) heatPoint.weight / (double) mapVisitDuration.get(heatPoint.value) * 1000.0) / 1000.0) + "}");
            gpsBuilder.append(point);
            gpsBuilder.append(System.lineSeparator());
        }
        template = UtilsMaps.replace(template, "//___POINTS_START___", "");
        template = UtilsMaps.replace(template, "//___POINTS_END___", "");
        template = UtilsMaps.replace(template, gpsPointTemplate, gpsBuilder.toString());
        // Setup options
        StringBuilder options = new StringBuilder(60);
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
        template = UtilsMaps.replace(template, "//___OPTIONS___", options.toString());
        // Set the template
        webEngine.loadContent(template);
        displayedTemplate = template;
        return webView;
    }
    
    private Parent createRichnessMapClient(List<Sighting> inLstSightings) {
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
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        String template = builder.toString();
        // Edit the template
        int beginIndex = template.indexOf("//___POINTS_START___") + "//___POINTS_START___".length();
        int endIndex = template.indexOf("//___POINTS_END___");
        String gpsPointTemplate = template.substring(beginIndex, endIndex).trim();
        StringBuilder gpsBuilder = new StringBuilder(50 * inLstSightings.size());
        Map<String, HeatPoint> mapHeatPoints = new HashMap<>();
        Map<String, Long> mapVisitDuration = new HashMap<>();
        for (Sighting sighting : inLstSightings) {
            if (UtilsGPS.hasGPSData(sighting)) {
                // Get the visit data
                String visitDurationKey = sighting.getVisitName();
                // Lyk my nie dis nodig om die visits self hier te cache nie... Maar dalk eendag...
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitName(), Visit.class);
                if (visit != null && visit.getStartDate() != null) {
                    if (!cmbSplitIndicator.getSelectionModel().getSelectedItem().equals(HeatMap.SplitIndicator.NEVER)) {
                        visitDurationKey = visitDurationKey + ":" + getSplitNumber(visit, sighting);
                    }
                    if (!mapVisitDuration.containsKey(visitDurationKey)) {
                        LocalDate endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                        if (endDate == null) {
                            endDate = LocalDate.now();
                        }
                        long days = ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), endDate);
                        if (days > cmbSplitIndicator.getSelectionModel().getSelectedItem().getDays()) {
                            days = cmbSplitIndicator.getSelectionModel().getSelectedItem().getDays();
                        }
                        mapVisitDuration.put(visitDurationKey, days);
                    }
                }
                else {
                    // If this visit does not have a valid date range, then don't process it, continue to the next record instead
                    continue;
                }
                // Add the heat map data
                double lat = UtilsGPS.getLatDecimalDegree(sighting);
                double lon = UtilsGPS.getLonDecimalDegree(sighting);
                String heatMapKey = visitDurationKey + ":" + sighting.getElementName() + ":" + lat + ":" + lon;
                HeatPoint heatPoint = mapHeatPoints.get(heatMapKey);
                if (heatPoint == null) {
                    heatPoint = new HeatPoint();
                    heatPoint.lat = lat;
                    heatPoint.lon = lon;
                    heatPoint.value = visitDurationKey;
                    heatPoint.weight = 1;
                    mapHeatPoints.put(heatMapKey, heatPoint);
                }
            }
        }
        for (HeatPoint  heatPoint : mapHeatPoints.values()) {
            String point = UtilsMaps.replace(gpsPointTemplate, "LatLng(-32,", "LatLng(" + Double.toString(heatPoint.lat) + ",");
            point = UtilsMaps.replace(point, ", 22), w", ", " + Double.toString(heatPoint.lon) + "), w");
            point = UtilsMaps.replace(point, "weight: 1.0}", "weight: " + (Math.round((double) heatPoint.weight / (double) mapVisitDuration.get(heatPoint.value) * 1000.0) / 1000.0) + "}");
            gpsBuilder.append(point);
            gpsBuilder.append(System.lineSeparator());
        }
        template = UtilsMaps.replace(template, "//___POINTS_START___", "");
        template = UtilsMaps.replace(template, "//___POINTS_END___", "");
        template = UtilsMaps.replace(template, gpsPointTemplate, gpsBuilder.toString());
        // Setup options
        StringBuilder options = new StringBuilder(60);
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
        template = UtilsMaps.replace(template, "//___OPTIONS___", options.toString());
        // Set the template
        webEngine.loadContent(template);
        displayedTemplate = template;
        return webView;
    }
    
    private Parent createSampleEffortMapClient(List<Sighting> inLstSightings) {
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
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        String template = builder.toString();
        // Edit the template
        int beginIndex = template.indexOf("//___POINTS_START___") + "//___POINTS_START___".length();
        int endIndex = template.indexOf("//___POINTS_END___");
        String gpsPointTemplate = template.substring(beginIndex, endIndex).trim();
        StringBuilder gpsBuilder = new StringBuilder(50 * inLstSightings.size());
        Map<String, HeatPoint> mapHeatPoints = new HashMap<>();
        Map<String, Long> mapVisitDuration = new HashMap<>();
        for (Sighting sighting : inLstSightings) {
            if (!mapVisitDuration.containsKey(sighting.getVisitName())) {
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitName(), Visit.class);
                if (visit != null && visit.getStartDate() != null) {
                    LocalDate endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                    if (endDate == null) {
                        endDate = LocalDate.now();
                    }
                    long days = ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), endDate);
                    mapVisitDuration.put(sighting.getVisitName(), days);
                }
                else {
                    // If this visit does not have a valid date range, then don't process it, continue to the next record instead
                    continue;
                }
            }
            if (UtilsGPS.hasGPSData(sighting)) {
                double lat = UtilsGPS.getLatDecimalDegree(sighting);
                double lon = UtilsGPS.getLonDecimalDegree(sighting);
                String key = sighting.getVisitName() + ":" + lat + ":" + lon;
                HeatPoint heatPoint = mapHeatPoints.get(key);
                if (heatPoint == null) {
                    heatPoint = new HeatPoint();
                    heatPoint.lat = lat;
                    heatPoint.lon = lon;
                    heatPoint.value = sighting.getVisitName();
                    mapHeatPoints.put(key, heatPoint);
                }
            }
        }
        for (HeatPoint  heatPoint : mapHeatPoints.values()) {
            String point = UtilsMaps.replace(gpsPointTemplate, "LatLng(-32,", "LatLng(" + Double.toString(heatPoint.lat) + ",");
            point = UtilsMaps.replace(point, ", 22), w", ", " + Double.toString(heatPoint.lon) + "), w");
            point = UtilsMaps.replace(point, "weight: 1.0}", "weight: " + mapVisitDuration.get(heatPoint.value) + "}");
            gpsBuilder.append(point);
            gpsBuilder.append(System.lineSeparator());
        }
        template = UtilsMaps.replace(template, "//___POINTS_START___", "");
        template = UtilsMaps.replace(template, "//___POINTS_END___", "");
        template = UtilsMaps.replace(template, gpsPointTemplate, gpsBuilder.toString());
        // Setup options
        StringBuilder options = new StringBuilder(60);
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
        template = UtilsMaps.replace(template, "//___OPTIONS___", options.toString());
        // Set the template
        webEngine.loadContent(template);
        displayedTemplate = template;
        return webView;
    }
    
    private class HeatPoint {
        public double lat;
        public double lon;
        public int weight = 0;
        public String value;
    }
    
    private long getSplitNumber(Visit inVisit, Sighting inSighting) {
        LocalDate startDate = UtilsTime.getLocalDateFromDate(inVisit.getStartDate());
        LocalDate sightingDate = UtilsTime.getLocalDateFromDate(inSighting.getDate());
        long gap = ChronoUnit.DAYS.between(startDate, sightingDate);
        return gap / cmbSplitIndicator.getSelectionModel().getSelectedItem().getDays();
    }
    
}
