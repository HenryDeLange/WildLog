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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.ReportsBaseDialog;
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

    
    public DurationChart(List<Sighting> inLstData, JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        super("Duration Reports", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(5);
        // Timeline for all
        ToggleButton btnLineChart = new ToggleButton("Duration for All Observations (Line)");
        btnLineChart.setToggleGroup(BUTTON_GROUP);
        btnLineChart.setCursor(Cursor.HAND);
        btnLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.TIMELINE_FOR_ALL;
                setupChartDescriptionLabel("<html>This chart shows the number of all Observations that lasted a specific duration.</html>");
            }
        });
        lstCustomButtons.add(btnLineChart);
        // Timeline per element
        ToggleButton btnStackedLineChart = new ToggleButton("Duration per Creature (Line)");
        btnStackedLineChart.setToggleGroup(BUTTON_GROUP);
        btnStackedLineChart.setCursor(Cursor.HAND);
        btnStackedLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.TIMELINE_PER_ELEMENT;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations that lasted a specific duration for each Creature.</html>");
            }
        });
        lstCustomButtons.add(btnStackedLineChart);
        // Time interval size
        lstCustomButtons.add(new Label("Duration interval size:"));
        cmbIntervalSize = new ComboBox<>(FXCollections.observableArrayList(options));
        cmbIntervalSize.setCursor(Cursor.HAND);
        cmbIntervalSize.setVisibleRowCount(10);
        cmbIntervalSize.getSelectionModel().clearSelection();
        cmbIntervalSize.getSelectionModel().select(2);
        cmbIntervalSize.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!cmbIntervalSize.getSelectionModel().isEmpty()) {
                    if (chartType == ChartType.TIMELINE_FOR_ALL) {
                        setupChartDescriptionLabel("<html>This chart shows the number of all Observations that lasted a specific duration.</html>");
                    }
                    else
                    if (chartType == ChartType.TIMELINE_PER_ELEMENT) {
                        setupChartDescriptionLabel("<html>This chart shows the number of Observations that lasted a specific duration for each Creature.</html>");
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
                    setActiveSubCategoryTitle("Duration for All Observations (Line)");
                    displayedChart = createTimelineForAllChart(lstData);
                }
                else
                if (chartType.equals(ChartType.TIMELINE_PER_ELEMENT)) {
                    setActiveSubCategoryTitle("Duration per Creature (Line)");
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
        setupEmtyIntervals(allSightings, maxMinutes, null);
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
        catAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14));
        AreaChart<String, Number> chart = new AreaChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-line-30-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Observations for each Duration Interval");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }
    
    private Chart createTimelinePerElementChart(List<Sighting> inSightings) {
        // Get the data in the correct structure
        int maxMinutes = 0;
        Map<String, ReportDataWrapper> mapInitialCountedData = new HashMap<>();
        Map<String, Integer> mapTotalElements = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapInitialCountedData.get(sighting.getElementName(reportsBaseDialog.getOptionName()) + "-" + getTimeAsString(sighting.getDurationMinutes(), sighting.getDurationSeconds()));
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper();
                dataWrapper.key = sighting.getElementName(reportsBaseDialog.getOptionName());
                dataWrapper.value = getTimeAsString(sighting.getDurationMinutes(), sighting.getDurationSeconds());
                dataWrapper.count = 0;
                mapTotalElements.put(sighting.getElementName(reportsBaseDialog.getOptionName()), 0);
            }
            dataWrapper.count++;
            mapInitialCountedData.put(sighting.getElementName(reportsBaseDialog.getOptionName()) + "-" + getTimeAsString(sighting.getDurationMinutes(), sighting.getDurationSeconds()), dataWrapper);
            if (sighting.getDurationMinutes() > maxMinutes) {
                maxMinutes = sighting.getDurationMinutes();
            }
        }
        // Add all the points on the chart in the correct order. This also adds the 0 values for the data gaps.
        Map<String, ObservableList<AreaChart.Data<String, Number>>> mapDataPerElement = new HashMap<>(mapInitialCountedData.size());
        for (String elementName : mapTotalElements.keySet()) {
            ObservableList<AreaChart.Data<String, Number>> lstElementData = FXCollections.observableArrayList();
            mapDataPerElement.put(elementName, lstElementData);
            setupEmtyIntervals(lstElementData, maxMinutes, elementName);
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
        catAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14));
        AreaChart<String, Number> chart = new AreaChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-line-30-color");
        chart.setLegendVisible(true);
        chart.setTitle("Number of Observations for each Duration Interval, per Creature");
        UtilsReports.setupChartTooltips(chart, true, false);
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

    private void setupEmtyIntervals(ObservableList<XYChart.Data<String, Number>> lstElementData, int inMaxMinutes, String inSeriesName) {
        List<String> lstTimes;
        if (inMaxMinutes <= maxMinutesCap) {
            lstTimes = getAllTimesAsList(inMaxMinutes);
        }
        else {
            lstTimes = getAllTimesAsList(maxMinutesCap + 1);
        }
        for (String time : lstTimes) {
            lstElementData.add(new AreaChart.Data<String, Number>(time, 0, inSeriesName));
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
        // Get times as String
        StringBuilder timeString = new StringBuilder(15);
        if (inMinutes < minInterval) {
            timeString.append("00");
        }
        else
        if (inMinutes > maxMinutesCap) {
            if (minInterval > 0) {
                timeString.append(" ").append(maxMinutesCap + (minInterval)).append("+");
            }
            else {
                timeString.append(" ").append(maxMinutesCap + 1).append("+");
            }
        }
        else {
            if (minInterval > 0) {
                timeString.append((inMinutes / minInterval) * minInterval);
            }
            else {
                timeString.append(inMinutes);
            }
        }
        if (timeString.length() < 2) {
            timeString.insert(0, "0");
        }
        if (inMinutes > maxMinutesCap) {
            timeString.append(" min");
        }
        else {
            timeString.append(":");
            if (((int)inSeconds) < secInterval) {
                timeString.append("00");
            }
            else {
                if (secInterval > 0) {
                    int temp = ((int)inSeconds / secInterval) * secInterval;
                    if (temp < 10) {
                        timeString.append("0");
                    }
                    timeString.append(temp);
                }
                else {
                    timeString.append("00");
                }
            }
        }
        timeString.append("\n");
        if (inMinutes <= maxMinutesCap) {
            if (minInterval > 0) {
                int temp = (((inMinutes / minInterval) * minInterval) + minInterval - 1);
                if (temp < 10) {
                    timeString.append("0");
                }
                timeString.append(temp);
            }
            else {
                if (inMinutes < 10) {
                    timeString.append("0");
                }
                timeString.append(inMinutes);
            }
            timeString.append(":");
            if (secInterval > 0) {
                int temp = (((int)inSeconds / secInterval) * secInterval) + secInterval - 1;
                if (temp < 10) {
                    timeString.append("0");
                }
                timeString.append(temp);
            }
            else {
                timeString.append("59");
            }
        }
        // Return result
        return timeString.toString();
    }
    
}
