package wildlog.ui.reports.implementations;

import java.time.LocalTime;
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
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.ui.utils.UtilsTime;


public class TimelineChart extends AbstractReport<Sighting> {
    private enum ChartType {TIMELINE_FOR_ALL, TIMELINE_PER_ELEMENT};
    private ChartType chartType = ChartType.TIMELINE_FOR_ALL;
    private Chart displayedChart;
    private ComboBox<String> cmbIntervalSize;
    private final String[] options = new String[] {"5 minutes", "15 minutes", "30 minutes", "1 hour", "2 hours", "4 hours", "6 hours"};

    
    public TimelineChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Timeline Reports (24 Hours)", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(5);
        // Timeline for all
        ToggleButton btnLineChart = new ToggleButton("Timeline for All Observations (Line)");
        btnLineChart.setToggleGroup(BUTTON_GROUP);
        btnLineChart.setCursor(Cursor.HAND);
        btnLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.TIMELINE_FOR_ALL;
                setupChartDescriptionLabel("<html>This chart shows the number of all Observations during a specific time period. "
                    + "The real time intervals are good for comparing related data from the same location during a short time period (a few weeks).</html>");
            }
        });
        lstCustomButtons.add(btnLineChart);
        // Timeline per element
        ToggleButton btnStackedBarChart = new ToggleButton("Timeline per Creature (Line)");
        btnStackedBarChart.setToggleGroup(BUTTON_GROUP);
        btnStackedBarChart.setCursor(Cursor.HAND);
        btnStackedBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.TIMELINE_PER_ELEMENT;
                setupChartDescriptionLabel("<html>This chart shows the number of all Observations per Creature during a specific time period. "
                    + "The real time intervals are good for comparing related data from the same location during a short time period (a few weeks).</html>");
            }
        });
        lstCustomButtons.add(btnStackedBarChart);
        // Time interval size
        lstCustomButtons.add(new Label("Timeline interval size:"));
        cmbIntervalSize = new ComboBox<>(FXCollections.observableArrayList(options));
        cmbIntervalSize.setCursor(Cursor.HAND);
        cmbIntervalSize.setVisibleRowCount(10);
        cmbIntervalSize.getSelectionModel().clearSelection();
        cmbIntervalSize.getSelectionModel().select(3);
        cmbIntervalSize.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!cmbIntervalSize.getSelectionModel().isEmpty()) {
                    if (chartType == ChartType.TIMELINE_FOR_ALL) {
                        setupChartDescriptionLabel("<html>This chart shows the number of all Observations during a specific time period. "
                            + "The real time intervals are good for comparing related data from the same location during a short time period (a few weeks).</html>");
                    }
                    else
                    if (chartType == ChartType.TIMELINE_PER_ELEMENT) {
                        setupChartDescriptionLabel("<html>This chart shows the number of all Observations during a specific time period. "
                            + "The real time intervals are good for comparing related data from the same location during a short time period (a few weeks).</html>");
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
                    setActiveSubCategoryTitle("Timeline for All Observations (Line)");
                    displayedChart = createTimelineForAllChart(lstData);
                }
                else
                if (chartType.equals(ChartType.TIMELINE_PER_ELEMENT)) {
                    setActiveSubCategoryTitle("Timeline per Creature (Line)");
                    displayedChart = createTimelinePerElementChart(lstData);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }

    private Chart createTimelineForAllChart(List<Sighting> inSightings) {
        // Get the data in the correct structure
        ObservableList<AreaChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        setupEmtyIntervals(allSightings, null);
        ObservableList<AreaChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        Map<String, ReportDataWrapper> mapCounter = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapCounter.get(getTimeAsString(UtilsTime.getLocalDateTimeFromDate(sighting.getDate()).toLocalTime()));
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(null, null, 0);
                mapCounter.put(getTimeAsString(UtilsTime.getLocalDateTimeFromDate(sighting.getDate()).toLocalTime()), dataWrapper);
            }
            dataWrapper.increaseCount();
        }
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
        catAxis.setCategories(getAllTimesAsList());
//        catAxis.setTickLabelRotation(-90);
        catAxis.setTickLabelFont(Font.font(12));
        AreaChart<String, Number> chart = new AreaChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-line-30-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Observations during Time Interval");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }
    
    private Chart createTimelinePerElementChart(List<Sighting> inSightings) {
        // Get the data in the correct structure
        Map<String, ReportDataWrapper> mapInitialCountedData = new HashMap<>();
        Map<String, Integer> mapTotalElements = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapInitialCountedData.get(sighting.getElementName() + "-" + getTimeAsString(UtilsTime.getLocalDateTimeFromDate(sighting.getDate()).toLocalTime()));
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper();
                dataWrapper.key = sighting.getElementName();
                dataWrapper.value = getTimeAsString(UtilsTime.getLocalDateTimeFromDate(sighting.getDate()).toLocalTime());
                dataWrapper.count = 0;
                mapTotalElements.put(sighting.getElementName(), 0);
            }
            dataWrapper.count++;
            mapInitialCountedData.put(sighting.getElementName() + "-" + getTimeAsString(UtilsTime.getLocalDateTimeFromDate(sighting.getDate()).toLocalTime()), dataWrapper);
        }
        // Add all the points on the chart in the correct order. This also adds the 0 values for the data gaps.
        Map<String, ObservableList<AreaChart.Data<String, Number>>> mapDataPerElement = new HashMap<>(mapInitialCountedData.size());
        for (String elementName : mapTotalElements.keySet()) {
            ObservableList<AreaChart.Data<String, Number>> lstElementData = FXCollections.observableArrayList();
            mapDataPerElement.put(elementName, lstElementData);
            setupEmtyIntervals(lstElementData, elementName);
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
        catAxis.setCategories(getAllTimesAsList());
        catAxis.setTickLabelFont(Font.font(12));
//        catAxis.setTickLabelRotation(-90);
        AreaChart<String, Number> chart = new AreaChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-line-30-color");
        chart.setLegendVisible(true);
        chart.setTitle("Number of Observations during Time Interval for each Creature");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }
    
    private ObservableList<String> getAllTimesAsList() {
        Set<String> timeCategories = new LinkedHashSet<>();
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m++) {
                timeCategories.add(getTimeAsString(LocalTime.of(h, m)));
            }
        }
        return FXCollections.observableList(new ArrayList<String>(timeCategories));
    }

    private void setupEmtyIntervals(ObservableList<XYChart.Data<String, Number>> lstElementData, String inSeriesName) {
        List<String> lstTimes = getAllTimesAsList();
        for (String time : lstTimes) {
            lstElementData.add(new AreaChart.Data<String, Number>(time, 0, inSeriesName));
        }
    }
    
    private String getTimeAsString(LocalTime inTime) {
        int minsDevider = 60;
        int hoursDevider = 24;
        if (cmbIntervalSize.getSelectionModel().isSelected(0)) {
            // 5 mins
            minsDevider = 12;
            hoursDevider = 24;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(1)) {
            // 15 mins
            minsDevider = 4;
            hoursDevider = 24;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(2)) {
            // 30 mins
            minsDevider = 2;
            hoursDevider = 24;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(3)) {
            // 1 hours
            minsDevider = 1;
            hoursDevider = 24;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(4)) {
            // 2 hours
            minsDevider = 1;
            hoursDevider = 12;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(5)) {
            // 4 hours
            minsDevider = 1;
            hoursDevider = 6;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(6)) {
            // 6 hours
            minsDevider = 1;
            hoursDevider = 4;
        }
        // Get start time
        StringBuilder timeString = new StringBuilder(15);
        int temp = (inTime.getHour() / (24 / hoursDevider)) * (24 / hoursDevider);
        if (temp < 10) {
            timeString.append("0");
        }
        timeString.append(temp);
        timeString.append(":");
        temp = (inTime.getMinute() / (60 / minsDevider)) * (60 / minsDevider);
        if (temp < 10) {
            timeString.append("0");
        }
        timeString.append(temp);
        timeString.append("\n");
        temp = (((inTime.getHour() + (24 / hoursDevider)) / (24 / hoursDevider)) * (24 / hoursDevider) - 1);
        if (temp < 10) {
            timeString.append("0");
        }
        timeString.append(temp);
        timeString.append(":");
        temp = (((inTime.getMinute() + (60 / minsDevider)) / (60 / minsDevider)) * (60 / minsDevider) - 1);
        if (temp < 10) {
            timeString.append("0");
        }
        timeString.append(temp);
        // Return result
        return timeString.toString();
    }
    
}
