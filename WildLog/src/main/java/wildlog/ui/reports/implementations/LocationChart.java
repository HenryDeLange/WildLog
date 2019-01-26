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
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.BarChartChangeListener;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;


public class LocationChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART_SIGHITNGS, BAR_CHART_SIGHITNGS, BAR_CHART_ELEMENTS};
    private ChartType chartType;
    private Chart displayedChart;

    
    public LocationChart(List<Sighting> inLstData,JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        super("Place Reports", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(3);
        // Pie charts
        ToggleButton btnPieChartSightings = new ToggleButton("Observations per Place (Pie)");
        btnPieChartSightings.setToggleGroup(BUTTON_GROUP);
        btnPieChartSightings.setCursor(Cursor.HAND);
        btnPieChartSightings.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART_SIGHITNGS;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations that have been recorded at each Place.</html>");
            }
        });
        lstCustomButtons.add(btnPieChartSightings);
        // Bar charts
        ToggleButton btnBarChartSightings = new ToggleButton("Observations per Place (Bar)");
        btnBarChartSightings.setToggleGroup(BUTTON_GROUP);
        btnBarChartSightings.setCursor(Cursor.HAND);
        btnBarChartSightings.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_SIGHITNGS;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations that have been recorded at each Place.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartSightings);
        ToggleButton btnBarChartElements = new ToggleButton("Creatures per Place");
        btnBarChartElements.setToggleGroup(BUTTON_GROUP);
        btnBarChartElements.setCursor(Cursor.HAND);
        btnBarChartElements.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_ELEMENTS;
                setupChartDescriptionLabel("<html>This chart shows the number of Creatures (species) that have been observed at each Place.</html>");
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
                if (chartType.equals(ChartType.PIE_CHART_SIGHITNGS)) {
                    setActiveSubCategoryTitle("Observations per Place (Pie)");
                    displayedChart = createPieChartSightings(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_SIGHITNGS)) {
                    setActiveSubCategoryTitle("Observations per Place (Bar)");
                    displayedChart = createBarChartSightings(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_ELEMENTS)) {
                    setActiveSubCategoryTitle("Creatures per Place");
                    displayedChart = createBarChartElements(lstData);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
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
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            BarChart.Data<String, Number> data = new BarChart.Data<String, Number>(key, mapData.get(key).count);
            data.nodeProperty().addListener(new BarChartChangeListener<>(mapData.size(), data));
            allSightings.add(data);
        }
        chartData.add(new BarChart.Series<String, Number>("Places (" + mapData.keySet().size() + ")", allSightings));
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapData.size(), true);
        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Observations for each Place");
        UtilsReports.setupChartTooltips(chart, true, false);
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
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            BarChart.Data<String, Number> data = new BarChart.Data<String, Number>(key, mapData.get(key).size());
            data.nodeProperty().addListener(new BarChartChangeListener<>(mapData.size(), data));
            allSightings.add(data);
        }
        chartData.add(new BarChart.Series<String, Number>("Places (" + mapData.keySet().size() + ")", allSightings));
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapData.size(), true);
        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Creatures observed at each Place");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }
    
    private Chart createPieChartSightings(List<Sighting> inSightings) {
         Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
             ReportDataWrapper dataWrapper = mapGroupedData.get(sighting.getLocationName());
            if (dataWrapper == null) {
                mapGroupedData.put(sighting.getLocationName(), new ReportDataWrapper("", "", 1));
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
        chart.getStyleClass().add("wl-pie-30-color");
        chart.setTitle("Number of Observations for each Place");
        UtilsReports.setupChartTooltips(chart);
        return chart;
    }

}
