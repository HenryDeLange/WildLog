package wildlog.ui.reports.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.utils.UtilsData;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;


public class ElementsChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART_SIGHTINGS, PIE_CHART_ELEMENT_TYPES, BAR_CHART_SIGHTINGS, BAR_CHART_ABUNDANCE, BAR_CHART_LOCATIONS};
    private ChartType chartType;
    private Chart displayedChart;
    
    
    public ElementsChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Creature Reports", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(5);
        // Pie charts
        Button btnPieChartElementTypes = new Button("Creatures per Type (Pie)");
        btnPieChartElementTypes.setCursor(Cursor.HAND);
        btnPieChartElementTypes.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART_ELEMENT_TYPES;
                setupChartDescriptionLabel("<html>This chart shows the number of Creatures (species) that have been observed for each Creature Type.</html>");
            }
        });
        lstCustomButtons.add(btnPieChartElementTypes);
        Button btnPieChartSightings = new Button("Observations per Creature (Pie)");
        btnPieChartSightings.setCursor(Cursor.HAND);
        btnPieChartSightings.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART_SIGHTINGS;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations of each Creature.</html>");
            }
        });
        lstCustomButtons.add(btnPieChartSightings);
        // Bar charts
        Button btnBarChartSightings = new Button("Observations per Creature (Bar)");
        btnBarChartSightings.setCursor(Cursor.HAND);
        btnBarChartSightings.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_SIGHTINGS;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations of each Creature. It can be used as a simple indication of Relative Abundance.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartSightings);
        Button btnBarChartElements = new Button("Places per Creature (Bar)");
        btnBarChartElements.setCursor(Cursor.HAND);
        btnBarChartElements.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_LOCATIONS;
                setupChartDescriptionLabel("<html>This chart shows the number of Places where a Creature has been observed.</html>");
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
                if (chartType.equals(ChartType.PIE_CHART_SIGHTINGS)) {
                    setActiveSubCategoryTitle("Observations per Creature (Pie)");
                    displayedChart = createPieChartSightings(lstData);
                }
                else
                if (chartType.equals(ChartType.PIE_CHART_ELEMENT_TYPES)) {
                    setActiveSubCategoryTitle("Creatures per Type (Pie)");
                    displayedChart = createPieChartElementType(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_SIGHTINGS)) {
                    setActiveSubCategoryTitle("Observations per Creature (Bar)");
                    displayedChart = createBarChartSightings(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_LOCATIONS)) {
                    setActiveSubCategoryTitle("Places per Creature (Bar)");
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
            ReportDataWrapper dataWrapper = mapData.get(sighting.getElementName());
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(sighting.getElementName(), null, 0);
                mapData.put(sighting.getElementName(), dataWrapper);
            }
            dataWrapper.increaseCount();
        }
        ObservableList<BarChart.Series<Number, String>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<Number, String>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            BarChart.Data<Number, String> data = new BarChart.Data<>(mapData.get(key).count, key);
// TODO: Implement die nommer lang die bar chart vir horisontale charts...
//            data.nodeProperty().addListener(new BarChartChangeListener<Number, String>(mapData.size(), data));
            allSightings.add(data);
        }
        // Sort the results
        Collections.sort(allSightings, new Comparator<BarChart.Data<Number, String>>() {
            @Override
            public int compare(BarChart.Data<Number, String> inData1, BarChart.Data<Number, String> inData2) {
                int compare = Double.compare(inData1.getXValue().doubleValue(), inData2.getXValue().doubleValue());
                if (compare == 0) {
                    compare = inData2.getYValue().compareTo(inData1.getYValue());
                }
                return compare;
            }
        });
        // Add the results to the final series
        chartData.add(new BarChart.Series<Number, String>("Creatures (" + mapData.keySet().size() + ")", allSightings));
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapData.size(), false);
        BarChart<Number, String> chart = new BarChart<Number, String>(numAxis, catAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Observations for each Creature");
        UtilsReports.setupChartTooltips(chart, false, false);
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
        ObservableList<BarChart.Series<Number, String>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<Number, String>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            BarChart.Data<Number, String> data = new BarChart.Data<>(mapData.get(key).size(), key);
// TODO: Implement die nommer lang die bar chart vir horisontale charts...
//            data.nodeProperty().addListener(new BarChartChangeListener<>(mapData.size(), data));
            allSightings.add(data);
        }
        // Sort the results
        Collections.sort(allSightings, new Comparator<BarChart.Data<Number, String>>() {
            @Override
            public int compare(BarChart.Data<Number, String> inData1, BarChart.Data<Number, String> inData2) {
                int compare = Double.compare(inData1.getXValue().doubleValue(), inData2.getXValue().doubleValue());
                if (compare == 0) {
                    compare = inData2.getYValue().compareTo(inData1.getYValue());
                }
                return compare;
            }
        });
        // Add the results to the final series
        chartData.add(new BarChart.Series<Number, String>("Creatures (" + mapData.keySet().size() + ")", allSightings));
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapData.size(), false);
        BarChart<Number, String> chart = new BarChart<>(numAxis, catAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Places where each Creature has been observed.");
        UtilsReports.setupChartTooltips(chart, false, false);
        return chart;
    }
    
    private Chart createPieChartSightings(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapGroupedData.get(sighting.getElementName());
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper("", "", 0);
                mapGroupedData.put(sighting.getElementName(), dataWrapper);
            }
            dataWrapper.increaseCount();
        }
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapGroupedData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            chartData.add(new PieChart.Data(key + " (" + mapGroupedData.get(key).getCount() + ")", mapGroupedData.get(key).getCount()));
        }
        PieChart chart = new PieChart(chartData);
        chart.getStyleClass().add("wl-pie-30-color");
        chart.setTitle("Number of Observations for each Creature");
        UtilsReports.setupChartTooltips(chart);
        return chart;
    }

    private Chart createPieChartElementType(List<Sighting> inSightings) {
        Map<String, Set<String>> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Element element = WildLogApp.getApplication().getDBI().find(new Element(sighting.getElementName()));
            Set<String> setElements = mapGroupedData.get(UtilsData.stringFromObject(element.getType()));
            if (setElements == null) {
                setElements = new HashSet<>();
                mapGroupedData.put(UtilsData.stringFromObject(element.getType()), setElements);
            }
            setElements.add(sighting.getElementName());
        }
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapGroupedData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            Set<String> setElements = mapGroupedData.get(key);
            chartData.add(new PieChart.Data(key + " (" + setElements.size() + ")", setElements.size()));
        }
        PieChart chart = new PieChart(chartData);
        chart.getStyleClass().add("wl-pie-30-color");
        chart.setTitle("Number of Creatures for each Creature Type");
        UtilsReports.setupChartTooltips(chart);
        return chart;
    }
    
}
