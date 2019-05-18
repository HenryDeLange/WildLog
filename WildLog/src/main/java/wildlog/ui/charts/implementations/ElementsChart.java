package wildlog.ui.charts.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.utils.UtilsData;
import wildlog.ui.charts.ChartsBaseDialog;
import wildlog.ui.charts.implementations.helpers.AbstractChart;
import wildlog.ui.charts.implementations.helpers.HorizontalBarChartChangeListener;
import wildlog.ui.charts.implementations.helpers.ChartDataWrapper;
import wildlog.ui.charts.utils.UtilsCharts;


public class ElementsChart extends AbstractChart<Sighting> {
    private enum ChartType {PIE_CHART_SIGHTINGS, PIE_CHART_ELEMENT_TYPES, BAR_CHART_SIGHTINGS, BAR_CHART_ABUNDANCE, BAR_CHART_LOCATIONS};
    private ChartType chartType;
    
    
    public ElementsChart(List<Sighting> inLstData, JLabel inChartDescLabel, ChartsBaseDialog inReportsBaseDialog) {
        super("Creature Charts", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(4);
        // Pie charts
        ToggleButton btnPieChartElementTypes = new ToggleButton("Creatures per Type");
        btnPieChartElementTypes.setToggleGroup(BUTTON_GROUP);
        btnPieChartElementTypes.setCursor(Cursor.HAND);
        btnPieChartElementTypes.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART_ELEMENT_TYPES;
                setupChartDescriptionLabel("<html>This chart shows the number of Creatures (species) that have been observed for each Creature Type.</html>");
            }
        });
        lstCustomButtons.add(btnPieChartElementTypes);
        ToggleButton btnPieChartSightings = new ToggleButton("Observations per Creature (Pie)");
        btnPieChartSightings.setToggleGroup(BUTTON_GROUP);
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
        ToggleButton btnBarChartSightings = new ToggleButton("Observations per Creature (Bar)");
        btnBarChartSightings.setToggleGroup(BUTTON_GROUP);
        btnBarChartSightings.setCursor(Cursor.HAND);
        btnBarChartSightings.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_SIGHTINGS;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations of each Creature. It can be used as a simple indication of Relative Abundance.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartSightings);
        ToggleButton btnBarChartElements = new ToggleButton("Places per Creature");
        btnBarChartElements.setToggleGroup(BUTTON_GROUP);
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
        displayedChart = null;
        if (chartType.equals(ChartType.PIE_CHART_SIGHTINGS)) {
            setActiveSubCategoryTitle("Observations per Creature (Pie)");
            displayedChart = createPieChartSightings(lstData);
        }
        else
        if (chartType.equals(ChartType.PIE_CHART_ELEMENT_TYPES)) {
            setActiveSubCategoryTitle("Creatures per Type");
            displayedChart = createPieChartElementType(lstData);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART_SIGHTINGS)) {
            setActiveSubCategoryTitle("Observations per Creature (Bar)");
            displayedChart = createBarChartSightings(lstData);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART_LOCATIONS)) {
            setActiveSubCategoryTitle("Places per Creature");
            displayedChart = createBarChartElements(lstData);
        }
        displayedChart.setBackground(Background.EMPTY);
        inScene.setRoot(displayedChart);
    }
    
    private Chart createBarChartSightings(List<Sighting> inSightings) {
        Map<String, ChartDataWrapper> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ChartDataWrapper dataWrapper = mapData.get(sighting.getCachedElementName(reportsBaseDialog.getOptionName()));
            if (dataWrapper == null) {
                dataWrapper = new ChartDataWrapper(sighting.getCachedElementName(reportsBaseDialog.getOptionName()), null, 0);
                mapData.put(sighting.getCachedElementName(reportsBaseDialog.getOptionName()), dataWrapper);
            }
            dataWrapper.increaseCount();
        }
        ObservableList<BarChart.Series<Number, String>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<Number, String>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            BarChart.Data<Number, String> data = new BarChart.Data<>(mapData.get(key).count, key);
            data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(mapData.size(), data));
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
        UtilsCharts.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsCharts.setupCategoryAxis(catAxis, mapData.size(), false);
        BarChart<Number, String> chart = new BarChart<Number, String>(numAxis, catAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Observations for each Creature");
        UtilsCharts.setupChartTooltips(chart, false, false);
        return chart;
    }

    private Chart createBarChartElements(List<Sighting> inSightings) {
        Map<String, Set<Long>> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Set<Long> set = mapData.get(sighting.getCachedElementName(reportsBaseDialog.getOptionName()));
            if (set == null) {
                set = new HashSet<>();
                mapData.put(sighting.getCachedElementName(reportsBaseDialog.getOptionName()), set);
            }
            set.add(sighting.getLocationID());
        }
        ObservableList<BarChart.Series<Number, String>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<Number, String>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            BarChart.Data<Number, String> data = new BarChart.Data<>(mapData.get(key).size(), key);
            data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(mapData.size(), data));
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
        UtilsCharts.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsCharts.setupCategoryAxis(catAxis, mapData.size(), false);
        BarChart<Number, String> chart = new BarChart<>(numAxis, catAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Places where each Creature has been observed.");
        UtilsCharts.setupChartTooltips(chart, false, false);
        return chart;
    }
    
    private Chart createPieChartSightings(List<Sighting> inSightings) {
        Map<String, ChartDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ChartDataWrapper dataWrapper = mapGroupedData.get(sighting.getCachedElementName(reportsBaseDialog.getOptionName()));
            if (dataWrapper == null) {
                dataWrapper = new ChartDataWrapper("", "", 0);
                mapGroupedData.put(sighting.getCachedElementName(reportsBaseDialog.getOptionName()), dataWrapper);
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
        UtilsCharts.setupChartTooltips(chart);
        return chart;
    }

    private Chart createPieChartElementType(List<Sighting> inSightings) {
        Map<String, Set<String>> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Element element = WildLogApp.getApplication().getDBI().findElement(sighting.getElementID(), null, Element.class);
            Set<String> setElements = mapGroupedData.get(UtilsData.stringFromObject(element.getType()));
            if (setElements == null) {
                setElements = new HashSet<>();
                mapGroupedData.put(UtilsData.stringFromObject(element.getType()), setElements);
            }
            setElements.add(sighting.getCachedElementName());
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
        UtilsCharts.setupChartTooltips(chart);
        return chart;
    }
    
}
