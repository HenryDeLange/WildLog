package wildlog.ui.reports.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.utils.UtilsData;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;


public class VisitChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART_NAME, PIE_CHART_TYPE};
    private ChartType chartType;
    private Chart displayedChart;

    
    public VisitChart(List<Sighting> inLstData,JLabel inChartDescLabel) {
        super("Period Reports", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(2);
        // Pie charts
        Button btnPieChartType = new Button("Observations per Period Type");
        btnPieChartType.setCursor(Cursor.HAND);
        btnPieChartType.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART_TYPE;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations per Period Type.</html>");
            }
        });
        lstCustomButtons.add(btnPieChartType);
        Button btnPieChartName = new Button("Observations per Period");
        btnPieChartName.setCursor(Cursor.HAND);
        btnPieChartName.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART_NAME;
                setupChartDescriptionLabel("<html>This chart shows the number of Places per Period.</html>");
            }
        });
        lstCustomButtons.add(btnPieChartName);
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.PIE_CHART_TYPE)) {
                    displayedChart = createPieChartVisitTypes(lstData);
                }
                else
                if (chartType.equals(ChartType.PIE_CHART_NAME)) {
                    displayedChart = createPieChartVisitNames(lstData);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }
    
    private Chart createPieChartVisitNames(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapGroupedData.get(sighting.getVisitName());
            if (dataWrapper == null) {
                mapGroupedData.put(sighting.getVisitName(), new ReportDataWrapper("", "", 1));
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
//        chart.setLegendVisible(false);
        chart.setTitle("Number of Observations for each Period");
        return chart;
    }
    
    private Chart createPieChartVisitTypes(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Visit visit = WildLogApp.getApplication().getDBI().find(new Visit(sighting.getVisitName()));
            String category = UtilsData.stringFromObject(visit.getType());
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
//        chart.setLegendVisible(false);
        chart.setTitle("Number of Observations for each Period Type");
        return chart;
    }
    
}
