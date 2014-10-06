package wildlog.ui.reports.implementations;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
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
import javafx.scene.chart.StackedBarChart;
import javax.swing.JButton;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;


public class TimeOfDayChart extends AbstractReport<Sighting> {
    private enum ChartType {LINE_CHART, STACKED_BAR_CHART, BAR_CHART};
    private ChartType chartType = ChartType.LINE_CHART;
    private Chart displayedChart;
    
    public TimeOfDayChart() {
        super("Sun Phase", "<html>This collection of charts use the time of the Observations. "
                + "The Time of Day category charts are better at seeing trends in data that spreads over longer times (seasons) or large geographic areas.</html>");
        lstCustomButtons = new ArrayList<>(3);
        // Area/Line Chart
        JButton btnLineChart = new JButton("Line Chart");
        btnLineChart.setFocusPainted(false);
        btnLineChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLineChart.setMargin(new Insets(2, 4, 2, 4));
        btnLineChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.LINE_CHART;
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
        // Stacked Bar Chart
        JButton btnStackedBarChart = new JButton("Stacked Bar Chart");
        btnStackedBarChart.setFocusPainted(false);
        btnStackedBarChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnStackedBarChart.setMargin(new Insets(2, 4, 2, 4));
        btnStackedBarChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.STACKED_BAR_CHART;
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
        // Bar Chart
        JButton btnBarChart = new JButton("Bar Chart");
        btnBarChart.setFocusPainted(false);
        btnBarChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBarChart.setMargin(new Insets(2, 4, 2, 4));
        btnBarChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.BAR_CHART;
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
        lstCustomButtons.add(btnBarChart);
        // TODO: Sit 'n Pie chart by
    }

    @Override
    public Chart createChart() {
        displayedChart = null;
        if (chartType.equals(ChartType.LINE_CHART)) {
            displayedChart = createLineChart(lstData);
        }
        else
        if (chartType.equals(ChartType.STACKED_BAR_CHART)) {
            displayedChart = createStackedBarChart(lstData);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART)) {
            displayedChart = createBarChart(lstData);
        }
        return displayedChart;
    }
    
    private Chart createStackedBarChart(List<Sighting> inSightings) {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
        axisX.setCategories(FXCollections.<String>observableArrayList(ActiveTimeSpesific.getEnumListAsString()));
        axisX.setTickLabelRotation(-90);
        ObservableList<StackedBarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        Map<String, ObservableList<StackedBarChart.Data<String, Number>>> groupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ObservableList<StackedBarChart.Data<String, Number>> allElementSightings = groupedData.get(sighting.getElementName());
            if (allElementSightings == null) {
                allElementSightings = FXCollections.observableArrayList();
                groupedData.put(sighting.getElementName(), allElementSightings);
            }
            allElementSightings.add(new StackedBarChart.Data<String, Number>(sighting.getTimeOfDay().toString(), 1));
        }
        for (String key : groupedData.keySet()) {
            StackedBarChart.Series<String, Number> series = new StackedBarChart.Series<String, Number>(
                    key + " (" + groupedData.get(key).size() + ")", 
                    groupedData.get(key));
            chartData.add(series);
        }
        StackedBarChart<String, Number> chart = new StackedBarChart<String, Number>(axisX, axisY, chartData);
        return chart;
    }
    
    private Chart createLineChart(List<Sighting> inSightings) {
        List<String> lstActiveTimeSpesific = ActiveTimeSpesific.getEnumListAsString();
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
        axisX.setCategories(FXCollections.<String>observableArrayList(lstActiveTimeSpesific));
        axisX.setTickLabelRotation(-90);
        // Get the data in the correct structure
        Map<String, ReportDataWrapper> mapInitialCountedData = new HashMap<>();
        Map<String, Integer> mapTotalElements = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapInitialCountedData.get(sighting.getElementName() + "-" + sighting.getTimeOfDay());
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper();
                dataWrapper.key = sighting.getElementName();
                if (sighting.getTimeOfDay() != null) {
                    dataWrapper.value = sighting.getTimeOfDay().toString();
                }
                else {
                    dataWrapper.value = ActiveTimeSpesific.UNKNOWN.toString();
                }
                dataWrapper.count = 0;
                mapTotalElements.put(sighting.getElementName(), 0);
            }
            dataWrapper.count++;
            mapInitialCountedData.put(sighting.getElementName() + "-" + sighting.getTimeOfDay(), dataWrapper);
        }
        // Add all the points on the chart in the correct order. This also adds the 0 values for the data gaps.
        Map<String, ObservableList<AreaChart.Data<String, Number>>> mapDataPerElement = new HashMap<>(mapInitialCountedData.size());
        for (String elementName : mapTotalElements.keySet()) {
            ObservableList<AreaChart.Data<String, Number>> lstElementData = FXCollections.observableArrayList();
            mapDataPerElement.put(elementName, lstElementData);
            for (String activeTimeSpesific : lstActiveTimeSpesific) {
                lstElementData.add(new AreaChart.Data<String, Number>(activeTimeSpesific, 0));
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

    private Chart createBarChart(List<Sighting> inSightings) {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
        axisX.setCategories(FXCollections.<String>observableArrayList(ActiveTimeSpesific.getEnumListAsString()));
        axisX.setTickLabelRotation(-90);
        ObservableList<StackedBarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<StackedBarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        for (Sighting sighting : inSightings) {
            allSightings.add(new StackedBarChart.Data<String, Number>(sighting.getTimeOfDay().toString(), 1));
        }
        chartData.add(new StackedBarChart.Series<String, Number>("Observations (" + allSightings.size() + ")", allSightings));
        StackedBarChart<String, Number> chart = new StackedBarChart<String, Number>(axisX, axisY, chartData);
        return chart;
    }
    
}
