package wildlog.ui.reports.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
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
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.utils.UtilsData;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;


public class ElementsChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART, BAR_CHART_SIGHITNGS, BAR_CHART_ELEMENTS};
    private ChartType chartType = ChartType.PIE_CHART;
    private Chart displayedChart;
    private final ComboBox<String> cmbCategories;
    private final String[] options = new String[] {"Creature Name", "Number Observed", "Sex", "Age", "Life Status", "Evidence", "Certainty", "Rating", "Info Tag"};
    
    
    public ElementsChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Creatures and Observations", inLstData, inChartDescLabel);
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
        lstCustomButtons.add(new Label("Bar Chart Per Creature:"));
        Button btnBarChartSightings = new Button("Observations per Creature");
        btnBarChartSightings.setCursor(Cursor.HAND);
        btnBarChartSightings.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_SIGHITNGS;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations per Creature.</html>");
                cmbCategories.getSelectionModel().clearSelection();
            }
        });
        lstCustomButtons.add(btnBarChartSightings);
        Button btnBarChartElements = new Button("Places per Creature");
        btnBarChartElements.setCursor(Cursor.HAND);
        btnBarChartElements.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_ELEMENTS;
                setupChartDescriptionLabel("<html>This chart shows the number of Places per Creature.</html>");
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
                category = sighting.getElementName();
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[1])) {
                category = Integer.toString(sighting.getNumberOfElements()) + " Observations";
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[2])) {
                category = UtilsData.stringFromObject(sighting.getSex());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[3])) {
                category = UtilsData.stringFromObject(sighting.getAge());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[4])) {
                category = UtilsData.stringFromObject(sighting.getLifeStatus());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[5])) {
                category = UtilsData.stringFromObject(sighting.getSightingEvidence());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[6])) {
                category = UtilsData.stringFromObject(sighting.getCertainty());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[7])) {
                category = UtilsData.stringFromObject(sighting.getViewRating());
            }
            else
            if (cmbCategories.getSelectionModel().getSelectedItem().equals(options[8])) {
                category = UtilsData.stringFromObject(sighting.getTag());
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
            ReportDataWrapper dataWrapper = mapData.get(sighting.getElementName());
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(sighting.getElementName(), null, 0);
                mapData.put(sighting.getElementName(), dataWrapper);
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
        chartData.add(new BarChart.Series<String, Number>("Creatures (" + mapData.keySet().size() + ")", allSightings));
        BarChart<String, Number> chart = new BarChart<String, Number>(axisX, axisY, chartData);
        return chart;
    }
    
    private Chart createBarChartElements(List<Sighting> inSightings) {
        Map<String, Set<String>> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Set<String> set = mapData.get(sighting.getElementName());
            if (set == null) {
                set = new HashSet<>();
                mapData.put(sighting.getElementName(), set);
            }
            set.add(sighting.getLocationName());
        }
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            final int count = allSightings.size();
            BarChart.Data<String, Number> data = new BarChart.Data<String, Number>(key, mapData.get(key).size());
            data.nodeProperty().addListener(new ChangeListener<Node>() {
                @Override
                public void changed(ObservableValue<? extends Node> ov, Node oldNode, Node newNode) {
                    if (newNode != null) {
                        // Set the bar colour
                        newNode.setStyle("-fx-bar-fill: " + UtilsReports.COLOURS_30.get(count % UtilsReports.COLOURS_30.size()));
                        // Add the total at the top
                        if (mapData.size() < 30) {
                            UtilsReports.displayLabelForDataOnTop(data);
                        }
                    }
                }
            });
            allSightings.add(data);
        }
        chartData.add(new BarChart.Series<String, Number>("Creatures (" + mapData.keySet().size() + ")", allSightings));
        NumberAxis numAxis = new NumberAxis();
        numAxis.setTickLabelFont(Font.font(20));
        numAxis.setLabel("Number of Places");
        numAxis.setAutoRanging(true);
        numAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if(object.intValue()!=object.doubleValue())
                    return "";
                return ""+(object.intValue());
            }

            @Override
            public Number fromString(String string) {
                Number val = Double.parseDouble(string);
                return val.intValue();
            }
        });
        CategoryAxis catAxis = new CategoryAxis();
        if (mapData.size() < 10) {
            catAxis.setTickLabelFont(Font.font(15));
        }
        else
        if (mapData.size() < 20) {
            catAxis.setTickLabelFont(Font.font(12));
        }
        else {
            catAxis.setTickLabelFont(Font.font(10));
        }
        if (mapData.size() > 30) {
            catAxis.setTickLabelRotation(90);
        }
        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, chartData);
        chart.setLegendVisible(false);
        return chart;
    }
    
}
