package wildlog.ui.reports.implementations;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Background;
import javax.swing.JButton;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.utils.UtilsTime;


public class TimelineChart extends AbstractReport<Sighting> {
    private enum ChartType {TIMELINE_FOR_ALL, TIMELINE_PER_ELEMENT};
    private ChartType chartType = ChartType.TIMELINE_FOR_ALL;
    private Chart displayedChart;
    private final int INTERVALS_PER_HOUR = 1;

    public TimelineChart() {
        super("Timeline", "<html>This collection of charts use the time of the Observations. "
                + "The real time intervals are good for comparing related data from the same location during a shorter time period (a few weeks).</html>");
        lstCustomButtons = new ArrayList<>(3);
        // Timeline for all
        JButton btnLineChart = new JButton("Timeline for All");
        btnLineChart.setFocusPainted(false);
        btnLineChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLineChart.setMargin(new Insets(2, 4, 2, 4));
        btnLineChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.TIMELINE_FOR_ALL;
                if (displayedChart != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            displayedChart.getScene().setRoot(createChart());
                        }
                    });
                }
            }
        });
        lstCustomButtons.add(btnLineChart);
        // Timeline per element
        JButton btnStackedBarChart = new JButton("Timeline per Creature");
        btnStackedBarChart.setFocusPainted(false);
        btnStackedBarChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnStackedBarChart.setMargin(new Insets(2, 4, 2, 4));
        btnStackedBarChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.TIMELINE_PER_ELEMENT;
                if (displayedChart != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            displayedChart.getScene().setRoot(createChart());
                        }
                    });
                }
            }
        });
        lstCustomButtons.add(btnStackedBarChart);
    }
    
    @Override
    public Chart createChart() {
        displayedChart = null;
        if (chartType.equals(ChartType.TIMELINE_FOR_ALL)) {
            displayedChart = createTimelineForAllChart(lstData);
        }
        else
        if (chartType.equals(ChartType.TIMELINE_PER_ELEMENT)) {
            displayedChart = createTimelinePerElementChart(lstData);
        }
        displayedChart.setBackground(Background.EMPTY);
        return displayedChart;
    }

    private Chart createTimelineForAllChart(List<Sighting> inSightings) {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
        ObservableList<String> timeCategories = FXCollections.observableArrayList();
        ObservableList<AreaChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < INTERVALS_PER_HOUR; m++) {
                String hours;
                if (h < 10) {
                    hours = "0" + h;
                }
                else {
                    hours = ""+h;
                }
                String mins;
                if (m == 0) {
                    mins = "00";
                }
                else {
                    mins = ""+(m*(60/INTERVALS_PER_HOUR));
                }
                timeCategories.add(hours + ":" + mins);
                allSightings.add(new AreaChart.Data<String, Number>(hours + ":" + mins, 0));
            }
        }
        axisX.setCategories(timeCategories);
        axisX.setTickLabelRotation(-90);
        ObservableList<AreaChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        Map<String, Integer> mapCounter = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Integer currentValue = mapCounter.get(getTimeAsString(sighting.getDate()));
            if (currentValue == null) {
                currentValue = 0;
            }
            currentValue = currentValue + 1;
            mapCounter.put(getTimeAsString(sighting.getDate()), currentValue);
        }
        for (AreaChart.Data<String, Number> data : allSightings) {
            Integer value = mapCounter.get(data.getXValue());
            if (value != null) {
                data.setYValue(value);
            }
        }
        chartData.add(new AreaChart.Series<String, Number>("Observations (" + lstData.size() + ")", allSightings));
        AreaChart<String, Number> chart = new AreaChart<String, Number>(axisX, axisY, chartData);
        return chart;
    }
    
    private Chart createTimelinePerElementChart(List<Sighting> inSightings) {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
        ObservableList<String> timeCategories = FXCollections.observableArrayList();
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < INTERVALS_PER_HOUR; m++) {
                String hours;
                if (h < 10) {
                    hours = "0" + h;
                }
                else {
                    hours = ""+h;
                }
                String mins;
                if (m == 0) {
                    mins = "00";
                }
                else {
                    mins = ""+(m*(60/INTERVALS_PER_HOUR));
                }
                timeCategories.add(hours + ":" + mins);
            }
        }
        axisX.setCategories(timeCategories);
        axisX.setTickLabelRotation(-90);
        // Get the data in the correct structure
        Map<String, ReportDataWrapper> mapInitialCountedData = new HashMap<>();
        Map<String, Integer> mapTotalElements = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapInitialCountedData.get(sighting.getElementName() + "-" + getTimeAsString(sighting.getDate()));
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper();
                dataWrapper.key = sighting.getElementName();
                dataWrapper.value = getTimeAsString(sighting.getDate());
                dataWrapper.count = 0;
                mapTotalElements.put(sighting.getElementName(), 0);
            }
            dataWrapper.count++;
            mapInitialCountedData.put(sighting.getElementName() + "-" + getTimeAsString(sighting.getDate()), dataWrapper);
        }
        // Add all the points on the chart in the correct order. This also adds the 0 values for the data gaps.
        Map<String, ObservableList<AreaChart.Data<String, Number>>> mapDataPerElement = new HashMap<>(mapInitialCountedData.size());
        for (String elementName : mapTotalElements.keySet()) {
            ObservableList<AreaChart.Data<String, Number>> lstElementData = FXCollections.observableArrayList();
            mapDataPerElement.put(elementName, lstElementData);
            for (int h = 0; h < 24; h++) {
                for (int m = 0; m < INTERVALS_PER_HOUR; m++) {
                    String hours;
                    if (h < 10) {
                        hours = "0" + h;
                    }
                    else {
                        hours = ""+h;
                    }
                    String mins;
                    if (m == 0) {
                        mins = "00";
                    }
                    else {
                        mins = ""+(m*(60/INTERVALS_PER_HOUR));
                    }
                    lstElementData.add(new AreaChart.Data<String, Number>(hours + ":" + mins, 0));
                }
            }
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
        AreaChart<String, Number> chart = new AreaChart<String, Number>(axisX, axisY, chartData);
        return chart;
    }
    
    private String getTimeAsString(Date inDate) {
        // TODO: Maak die interfal verstelbaar (5min,10min,15min,30min,1uur,2ure)
        LocalDateTime currentSightingTime = UtilsTime.getLocalDateTimeFromDate(inDate);
        LocalTime time = currentSightingTime.toLocalTime();
        String hours;
        if (time.getHour() < 10) {
            hours = "0" + time.getHour();
        }
        else {
            hours = ""+time.getHour();
        }
        String mins;
        if (time.getMinute() < (60/INTERVALS_PER_HOUR)) {
            mins = "00";
        }
        else {
            mins = ""+(time.getMinute()/(60/INTERVALS_PER_HOUR))*(60/INTERVALS_PER_HOUR);
        }
        return hours + ":" + mins;
    }
    
}
