package wildlog.ui.charts.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.ui.charts.ChartsBaseDialog;
import wildlog.ui.charts.implementations.helpers.AbstractChart;
import wildlog.ui.charts.implementations.helpers.CustomColourBarChartChangeListener;
import wildlog.ui.charts.implementations.helpers.ChartDataWrapper;
import wildlog.ui.charts.utils.UtilsCharts;


public class TimeOfDayChart extends AbstractChart<Sighting> {
    private enum ChartType {LINE_CHART, STACKED_BAR_CHART, BAR_CHART, PIE_CHART};
    private ChartType chartType = ChartType.PIE_CHART;
    
    public TimeOfDayChart(List<Sighting> inLstData, JLabel inChartDescLabel, ChartsBaseDialog inReportsBaseDialog) {
        super("Time of Day Charts", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(3);
        // Bar charts
        ToggleButton btnBarChart = new ToggleButton("Observation Time of Day (Bar)");
        btnBarChart.setToggleGroup(BUTTON_GROUP);
        btnBarChart.setCursor(Cursor.HAND);
        btnBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations for each Time Of Day category.</html>");
            }
        });
        lstCustomButtons.add(btnBarChart);
        // Pie charts
        ToggleButton btnPieChart = new ToggleButton("Observation Time of Day (Pie)");
        btnPieChart.setToggleGroup(BUTTON_GROUP);
        btnPieChart.setCursor(Cursor.HAND);
        btnPieChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations for each Time Of Day category.</html>");
            }
        });
        lstCustomButtons.add(btnPieChart);
        // Line charts
        ToggleButton btnLineChart = new ToggleButton("Creature Time of Day");
        btnLineChart.setToggleGroup(BUTTON_GROUP);
        btnLineChart.setCursor(Cursor.HAND);
        btnLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.LINE_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations of each Creature for every Time Of Day category.</html>");
            }
        });
        lstCustomButtons.add(btnLineChart);
    }

    @Override
    public void createReport(Scene inScene) {
        displayedChart = null;
        if (chartType.equals(ChartType.LINE_CHART)) {
            setActiveSubCategoryTitle("Creature Time of Day");
            displayedChart = createLineChart(lstData);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART)) {
            setActiveSubCategoryTitle("Observation Time of Day (Bar)");
            displayedChart = createBarChart(lstData);
        }
        else
        if (chartType.equals(ChartType.PIE_CHART)) {
            setActiveSubCategoryTitle("Observation Time of Day (Pie)");
            displayedChart = createPieChart(lstData);
        }
        displayedChart.setBackground(Background.EMPTY);
        inScene.setRoot(displayedChart);
    }
    
    private Chart createBarChart(List<Sighting> inSightings) {
        Map<String, ChartDataWrapper> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ChartDataWrapper dataWrapper = mapData.get(sighting.getTimeOfDay().toString());
            if (dataWrapper == null) {
                dataWrapper = new ChartDataWrapper(null, null, 0);
                mapData.put(sighting.getTimeOfDay().toString(), dataWrapper);
            }
            dataWrapper.increaseCount();
        }
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        int maxCount = 0;
        for (String key : keys) {
            BarChart.Data<String, Number> data = new BarChart.Data<String, Number>(key, mapData.get(key).count);
            // Setup custom colour based on the Enum value
            data.nodeProperty().addListener(new CustomColourBarChartChangeListener<>(mapData.size(), data, UtilsCharts.COLOURS_TIME_OF_DAY, key));
            allSightings.add(data);
            if (mapData.get(key).count > maxCount) {
                maxCount = mapData.get(key).count;
            }
        }
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis(0, (int)(maxCount*1.2), maxCount/10);
// FIXME: Autoranging kap soms die boonste getalletjie af...??
        UtilsCharts.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        List<String> lstActiveTimeSpesific = ActiveTimeSpesific.getEnumListAsString();
        catAxis.setCategories(FXCollections.<String>observableArrayList(lstActiveTimeSpesific));
        UtilsCharts.setupCategoryAxis(catAxis, lstActiveTimeSpesific.size(), true);
        catAxis.setTickLabelRotation(-90);
        chartData.add(new BarChart.Series<String, Number>("Observations (" + allSightings.size() + ")", allSightings));
        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-bar-custom-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Observations per Time Of Day category");
        UtilsCharts.setupChartTooltips(chart, true, false);
        return chart;
    }
    
    private Chart createPieChart(List<Sighting> inSightings) {
        Map<String, ChartDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ChartDataWrapper dataWrapper = mapGroupedData.get(sighting.getTimeOfDay().toString());
            if (dataWrapper == null) {
                mapGroupedData.put(sighting.getTimeOfDay().toString(), new ChartDataWrapper("", "", 1));
            }
            else {
                dataWrapper.increaseCount();
            }
        }
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        List<String> keys = ActiveTimeSpesific.getEnumListAsString();
        for (String key : keys) {
            String text = key;
            if (text.isEmpty()) {
                text = ActiveTimeSpesific.NONE.getDescription();
            }
            if (mapGroupedData.containsKey(key)) {
                PieChart.Data data = new PieChart.Data(text + " (" + mapGroupedData.get(key).getCount() + ")", mapGroupedData.get(key).getCount());
                chartData.add(data);
            }
            else {
// FIXME: Kry 'n manier om die lee data se labels te hide want dit lyk simpel
                if (!ActiveTimeSpesific.UNKNOWN.equals(ActiveTimeSpesific.getEnumFromText(key))
                        && !ActiveTimeSpesific.NONE.equals(ActiveTimeSpesific.getEnumFromText(key))) {
                    PieChart.Data data = new PieChart.Data(text + " (0)", 0);
                    chartData.add(data);
                }
            }
        }
        // Setup chart
        PieChart chart = new PieChart(chartData);
        chart.getStylesheets().add("wildlog/ui/reports/chart/styling/ChartsTimeOfDay.css");
        chart.getStyleClass().add("wl-pie-time-of-day-color");
        chart.setTitle("Number of Observations per Time Of Day category");
        UtilsCharts.setupChartTooltips(chart);
        return chart;
    }
    
    private Chart createLineChart(List<Sighting> inSightings) {
        List<String> lstActiveTimeSpesific = ActiveTimeSpesific.getEnumListAsString();
        // Get the data in the correct structure
        Map<String, ChartDataWrapper> mapInitialCountedData = new HashMap<>();
        Map<String, Integer> mapTotalElements = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ChartDataWrapper dataWrapper = mapInitialCountedData.get(sighting.getCachedElementName(reportsBaseDialog.getOptionName()) + "-" + sighting.getTimeOfDay());
            if (dataWrapper == null) {
                dataWrapper = new ChartDataWrapper();
                dataWrapper.key = sighting.getCachedElementName(reportsBaseDialog.getOptionName());
                if (sighting.getTimeOfDay() != null) {
                    dataWrapper.value = sighting.getTimeOfDay().toString();
                }
                else {
                    dataWrapper.value = ActiveTimeSpesific.UNKNOWN.toString();
                }
                dataWrapper.count = 0;
                mapTotalElements.put(sighting.getCachedElementName(reportsBaseDialog.getOptionName()), 0);
            }
            dataWrapper.count++;
            mapInitialCountedData.put(sighting.getCachedElementName(reportsBaseDialog.getOptionName()) + "-" + sighting.getTimeOfDay(), dataWrapper);
        }
        // Add all the points on the chart in the correct order. This also adds the 0 values for the data gaps.
        Map<String, ObservableList<AreaChart.Data<String, Number>>> mapDataPerElement = new HashMap<>(mapInitialCountedData.size());
        for (String elementName : mapTotalElements.keySet()) {
            ObservableList<AreaChart.Data<String, Number>> lstElementData = FXCollections.observableArrayList();
            mapDataPerElement.put(elementName, lstElementData);
            for (String activeTimeSpesific : lstActiveTimeSpesific) {
                lstElementData.add(new AreaChart.Data<String, Number>(activeTimeSpesific, 0, elementName));
            }
        }
        // Set the DataWrapper values in an ObservableList
        for (ChartDataWrapper dataWrapper : mapInitialCountedData.values()) {
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
                    key + " (" + mapTotalElements.get(key) + ")", mapDataPerElement.get(key));
            chartData.add(series);
        }
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsCharts.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        catAxis.setCategories(FXCollections.<String>observableArrayList(lstActiveTimeSpesific));
        UtilsCharts.setupCategoryAxis(catAxis, lstActiveTimeSpesific.size(), true);
        catAxis.setTickLabelRotation(-90);
        AreaChart<String, Number> chart = new AreaChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-line-30-color");
        chart.setTitle("Number of Observations per Time Of Day category for each Creature");
        UtilsCharts.setupChartTooltips(chart, true, false);
        return chart;
    }
    
}