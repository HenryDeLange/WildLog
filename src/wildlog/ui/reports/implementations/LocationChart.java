package wildlog.ui.reports.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.utils.UtilsData;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;


public class LocationChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART, BAR_CHART_SIGHITNGS, BAR_CHART_ELEMENTS};
    private ChartType chartType = ChartType.PIE_CHART;
    private Chart displayedChart;
    private final ComboBox<String> cmbCategories;
    private final String[] options = new String[] {"Place Name", "Period Name", "Period Type"};

    
    public LocationChart(List<Sighting> inLstData,JLabel inChartDescLabel) {
        super("Places and Periods", inLstData, inChartDescLabel);
//        "<html>This collection of charts focus on ratio of observations at Places.</html>"
        lstCustomButtons = new ArrayList<>(6);
        // Pie charts
        lstCustomButtons.add(new Label("Pie Chart Per Category:"));
        cmbCategories = new ComboBox<>(FXCollections.observableArrayList(options));
        cmbCategories.setCursor(Cursor.HAND);
        cmbCategories.getSelectionModel().clearSelection();
        cmbCategories.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!cmbCategories.getSelectionModel().isEmpty()) {
                    chartType = ChartType.PIE_CHART;
                    setupChartDescriptionLabel("<html>This collection of charts focuses on the ratio of Observations for the selected category.</html>");
                }
            }
        });
        lstCustomButtons.add(cmbCategories);
//        // New line for spacing
//        lstCustomButtons.add(new Label(""));
        // Bar charts
        lstCustomButtons.add(new Label("Bar Chart Per Place:"));
        Button btnBarChartSightings = new Button("Observations per Place");
        btnBarChartSightings.setCursor(Cursor.HAND);
        btnBarChartSightings.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_SIGHITNGS;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations per Place.</html>");
                cmbCategories.getSelectionModel().clearSelection();
            }
        });
        lstCustomButtons.add(btnBarChartSightings);
        Button btnBarChartElements = new Button("Creatures per Place");
        btnBarChartElements.setCursor(Cursor.HAND);
        btnBarChartElements.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_ELEMENTS;
                setupChartDescriptionLabel("<html>This chart shows the number of Creatures per Place.</html>");
                cmbCategories.getSelectionModel().clearSelection();
            }
        });
        lstCustomButtons.add(btnBarChartElements);
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.PIE_CHART)) {
                    displayedChart = createPieChart(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_SIGHITNGS)) {
                    displayedChart = createBarChartSightings(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_ELEMENTS)) {
                    displayedChart = createBarChartElements(lstData);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }
    
    private Chart createPieChart(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            String category = null;
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[0])) {
                category = sighting.getLocationName();
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[1])) {
                category = sighting.getVisitName();
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[2])) {
                Visit visit = WildLogApp.getApplication().getDBI().find(new Visit(sighting.getVisitName()));
                category = UtilsData.stringFromObject(visit.getType());
            }
            if (category == null || category.isEmpty()) {
                category = "Unknown";
            }
            ReportDataWrapper dataWrapper = mapGroupedData.get(category);
            if (dataWrapper == null) {
                mapGroupedData.put(category, new ReportDataWrapper("", "", 1));
            }
            else {
                dataWrapper.increaseCount();
            }
        }
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapGroupedData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            chartData.add(new PieChart.Data(key + " (" + mapGroupedData.get(key).getCount() + ")", mapGroupedData.get(key).getCount()));
        }
        PieChart chart = new PieChart(chartData);
        return chart;
    }
    
    private Chart createBarChartSightings(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapData.get(sighting.getLocationName());
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(sighting.getLocationName(), null, 0);
                mapData.put(sighting.getLocationName(), dataWrapper);
            }
            dataWrapper.increaseCount();
        }
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
//        axisX.setTickLabelRotation(-90);
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            allSightings.add(new BarChart.Data<String, Number>(key, mapData.get(key).count));
        }
        chartData.add(new BarChart.Series<String, Number>("Places (" + mapData.keySet().size() + ")", allSightings));
        BarChart<String, Number> chart = new BarChart<String, Number>(axisX, axisY, chartData);
        return chart;
    }
    
    private Chart createBarChartElements(List<Sighting> inSightings) {
        Map<String, Set<String>> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Set<String> set = mapData.get(sighting.getLocationName());
            if (set == null) {
                set = new HashSet<>();
                mapData.put(sighting.getLocationName(), set);
            }
            set.add(sighting.getElementName());
        }
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Creatures");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
//        axisX.setTickLabelRotation(-90);
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            allSightings.add(new BarChart.Data<String, Number>(key, mapData.get(key).size()));
        }
        chartData.add(new BarChart.Series<String, Number>("Places (" + mapData.keySet().size() + ")", allSightings));
        BarChart<String, Number> chart = new BarChart<String, Number>(axisX, axisY, chartData);
        return chart;
    }

}
