package wildlog.ui.reports.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;


public class DurationChart extends AbstractReport<Sighting> {
    private enum ChartType {TIMELINE_FOR_ALL, TIMELINE_PER_ELEMENT};
    private ChartType chartType = ChartType.TIMELINE_FOR_ALL;
    private Chart displayedChart;
    private ComboBox<String> cmbIntervalSize;
    private final String[] options = new String[] {"2 seconds", "5 seconds", "10 seconds", "30 seconds", "1 minute", "5 minutes", "15 minutes"};
    private int maxMinutesCap = 30;

    
    public DurationChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Duration Reports", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(5);
        // Timeline for all
        Button btnLineChart = new Button("Duration for All");
        btnLineChart.setCursor(Cursor.HAND);
        btnLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.TIMELINE_FOR_ALL;
                setupChartDescriptionLabel("<html>This chart shows the number of all Observations that lasted a specific time period.</html>");
            }
        });
        lstCustomButtons.add(btnLineChart);
        // Timeline per element
        Button btnStackedBarChart = new Button("Duration per Creature");
        btnStackedBarChart.setCursor(Cursor.HAND);
        btnStackedBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.TIMELINE_PER_ELEMENT;
                setupChartDescriptionLabel("<html>This chart shows the number of all Observations that lasted a specific time period.</html>");
            }
        });
        lstCustomButtons.add(btnStackedBarChart);
        // Time interval size
        lstCustomButtons.add(new Label("Duration interval size:"));
        cmbIntervalSize = new ComboBox<>(FXCollections.observableArrayList(options));
        cmbIntervalSize.setCursor(Cursor.HAND);
        cmbIntervalSize.getSelectionModel().clearSelection();
        cmbIntervalSize.getSelectionModel().select(2);
        cmbIntervalSize.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!cmbIntervalSize.getSelectionModel().isEmpty()) {
                    if (chartType == ChartType.TIMELINE_FOR_ALL) {
                        setupChartDescriptionLabel("<html>This chart shows the number of all Observations that lasted a specific time period.</html>");
                    }
                    else
                    if (chartType == ChartType.TIMELINE_PER_ELEMENT) {
                        setupChartDescriptionLabel("<html>This chart shows the number of all Observations that lasted a specific time period.</html>");
                    }
                }
            }
        });
        lstCustomButtons.add(cmbIntervalSize);
    }
    
    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.TIMELINE_FOR_ALL)) {
                    displayedChart = createTimelineForAllChart(lstData);
                }
                else
                if (chartType.equals(ChartType.TIMELINE_PER_ELEMENT)) {
                    displayedChart = createTimelinePerElementChart(lstData);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }

    private Chart createTimelineForAllChart(List<Sighting> inSightings) {
        // Get the data in the correct structure
        int maxMinutes = 0;
        ObservableList<AreaChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        ObservableList<AreaChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        Map<String, ReportDataWrapper> mapCounter = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapCounter.get(getTimeAsString(sighting.getDurationMinutes(), sighting.getDurationSeconds()));
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(null, null, 0);
                mapCounter.put(getTimeAsString(sighting.getDurationMinutes(), sighting.getDurationSeconds()), dataWrapper);
            }
            dataWrapper.increaseCount();
            if (sighting.getDurationMinutes() > maxMinutes) {
                maxMinutes = sighting.getDurationMinutes();
            }
        }
        setupEmtyIntervals(allSightings, maxMinutes);
        for (AreaChart.Data<String, Number> data : allSightings) {
            ReportDataWrapper dataWrapper = mapCounter.get(data.getXValue());
            if (dataWrapper != null) {
                data.setYValue(dataWrapper.count);
            }
        }
        chartData.add(new AreaChart.Series<String, Number>("Observations (" + lstData.size() + ")", allSightings));
        // Setup chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        catAxis.setCategories(getAllTimesAsList(maxMinutes));
        catAxis.setTickLabelFont(Font.font(12));
        AreaChart<String, Number> chart = new AreaChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-line-30-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Observations for each Duration Interval");
        return chart;
    }
    
    private Chart createTimelinePerElementChart(List<Sighting> inSightings) {
        // Get the data in the correct structure
        int maxMinutes = 0;
        Map<String, ReportDataWrapper> mapInitialCountedData = new HashMap<>();
        Map<String, Integer> mapTotalElements = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapInitialCountedData.get(sighting.getElementName() + "-" + getTimeAsString(sighting.getDurationMinutes(), sighting.getDurationSeconds()));
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper();
                dataWrapper.key = sighting.getElementName();
                dataWrapper.value = getTimeAsString(sighting.getDurationMinutes(), sighting.getDurationSeconds());
                dataWrapper.count = 0;
                mapTotalElements.put(sighting.getElementName(), 0);
            }
            dataWrapper.count++;
            mapInitialCountedData.put(sighting.getElementName() + "-" + getTimeAsString(sighting.getDurationMinutes(), sighting.getDurationSeconds()), dataWrapper);
            if (sighting.getDurationMinutes() > maxMinutes) {
                maxMinutes = sighting.getDurationMinutes();
            }
        }
        // Add all the points on the chart in the correct order. This also adds the 0 values for the data gaps.
        Map<String, ObservableList<AreaChart.Data<String, Number>>> mapDataPerElement = new HashMap<>(mapInitialCountedData.size());
        for (String elementName : mapTotalElements.keySet()) {
            ObservableList<AreaChart.Data<String, Number>> lstElementData = FXCollections.observableArrayList();
            mapDataPerElement.put(elementName, lstElementData);
            setupEmtyIntervals(lstElementData, maxMinutes);
        }
        // Set the DataWrapper values in an ObservableList
        for (ReportDataWrapper dataWrapper : mapInitialCountedData.values()) {
            ObservableList<AreaChart.Data<String, Number>> lstElementData = mapDataPerElement.get(dataWrapper.key);
            for (AreaChart.Data<String, Number> data : lstElementData) {
                if (data.getXValue().equals(dataWrapper.value)) {
                    data.setYValue(dataWrapper.count);
                    mapTotalElements.put(dataWrapper.key, mapTotalElements.get(dataWrapper.key) + dataWrapper.count);
                    break;
                }
            }
        }
        // Construct the final list of series that needs to be displayed
        ObservableList<AreaChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapTotalElements.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            AreaChart.Series<String, Number> series = new AreaChart.Series<String, Number>(
                    key + " (" + mapTotalElements.get(key) + ")", 
                    mapDataPerElement.get(key));
            chartData.add(series);
        }
        // Setup chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        catAxis.setCategories(getAllTimesAsList(maxMinutes));
        catAxis.setTickLabelFont(Font.font(12));
        AreaChart<String, Number> chart = new AreaChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-line-30-color");
        chart.setLegendVisible(true);
        chart.setTitle("Number of Observations for each Duration Interval, per Creature");
        return chart;
    }
    
    private ObservableList<String> getAllTimesAsList(int inMaxMinutes) {
        Set<String> timeCategories = new LinkedHashSet<>();
        for (int m = 0; (m <= inMaxMinutes && m <= maxMinutesCap + 1); m++) {
            for (int s = 0; s < 60; s++) {
                timeCategories.add(getTimeAsString(m, s));
            }
        }
        return FXCollections.observableList(new ArrayList<String>(timeCategories));
    }

    private void setupEmtyIntervals(ObservableList<XYChart.Data<String, Number>> lstElementData, int inMaxMinutes) {
        List<String> lstTimes;
        if (inMaxMinutes <= maxMinutesCap) {
            lstTimes = getAllTimesAsList(inMaxMinutes);
        }
        else {
            lstTimes = getAllTimesAsList(maxMinutesCap + 1);
        }
        for (String time : lstTimes) {
            lstElementData.add(new AreaChart.Data<String, Number>(time, 0));
        }
    }
    
    private String getTimeAsString(int inMinutes, double inSeconds) {
        int secInterval = 60;
        int minInterval = 60;
        if (cmbIntervalSize.getSelectionModel().isSelected(0)) {
            // 2 secs
            secInterval = 2;
            minInterval = 0;
            maxMinutesCap = 2;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(1)) {
            // 5 secs
            secInterval = 5;
            minInterval = 0;
            maxMinutesCap = 3;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(2)) {
            // 10 secs
            secInterval = 10;
            minInterval = 0;
            maxMinutesCap = 6;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(3)) {
            // 30 secs
            secInterval = 30;
            minInterval = 0;
            maxMinutesCap = 15;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(4)) {
            // 1 min
            secInterval = 0;
            minInterval = 1;
            maxMinutesCap = 30;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(5)) {
            // 5 mins
            secInterval = 0;
            minInterval = 5;
            maxMinutesCap = 60;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(6)) {
            // 15 mins
            secInterval = 0;
            minInterval = 15;
            maxMinutesCap = 120;
        }
        // Get start time
        String mins;
        if (inMinutes < minInterval) {
            mins = "00";
        }
        else
        if (inMinutes > maxMinutesCap) {
            if (minInterval > 0) {
                mins = " " + (maxMinutesCap + (minInterval)) + "+";
            }
            else {
                mins = " " + (maxMinutesCap + 1) + "+";
            }
        }
        else {
            if (minInterval > 0) {
                mins = "" + (inMinutes / minInterval) * minInterval;
            }
            else {
                mins = "" + inMinutes;
            }
        }
        if (mins.length() < 2) {
            mins = "0" + mins;
        }
        String secs;
        if (inMinutes > maxMinutesCap) {
            secs = " min";
        }
        else
        if (((int)inSeconds) < secInterval) {
            secs = "00";
        }
        else {
            if (secInterval > 0) {
                secs = "" + ((int)inSeconds / secInterval) * secInterval;
            }
            else {
                secs = "00";
            }
        }
        if (secs.length() < 2) {
            secs = "0" + secs;
        }
        // Get end time
        String result;
        if (inMinutes <= maxMinutesCap) {
            String endMins;
            if (minInterval > 0) {
                endMins = "" + (((inMinutes / minInterval) * minInterval) + minInterval - 1);
            }
            else {
                endMins = "" + inMinutes;
            }
            if (endMins.length() < 2) {
                endMins = "0" + endMins;
            }
            String endSecs;
            if (secInterval > 0) {
                endSecs = "" + ((((int)inSeconds / secInterval) * secInterval) + secInterval - 1);
            }
            else {
                endSecs = "59";
            }
            if (endSecs.length() < 2) {
                endSecs = "0" + endSecs;
            }
            result = mins + ":" + secs + /*"\n  to  \n"*/"\n" + endMins + ":" + endSecs;
        }
        else {
            result = mins + "\n"+ secs;
        }
        // Return result
        return result;
    }
    
}
