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
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.BarChartChangeListener;
import wildlog.ui.reports.implementations.helpers.PieChartChangeListener;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;


public class TimeOfDayChart extends AbstractReport<Sighting> {
    private enum ChartType {LINE_CHART, STACKED_BAR_CHART, BAR_CHART, PIE_CHART};
    private ChartType chartType = ChartType.PIE_CHART;
    private Chart displayedChart;
    
    public TimeOfDayChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Time of Day Reports", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(7);
        // All Observations
        lstCustomButtons.add(new Label("Time of Day for All Observations:"));
        Button btnPieChart = new Button("Pie Chart (For All)");
        btnPieChart.setCursor(Cursor.HAND);
        btnPieChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations for each Time Of Day category.</html>");
            }
        });
        lstCustomButtons.add(btnPieChart);
        Button btnBarChart = new Button("Bar Chart (For All)");
        btnBarChart.setCursor(Cursor.HAND);
        btnBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations for each Time Of Day category.</html>");
            }
        });
        lstCustomButtons.add(btnBarChart);
        // Per Creature
        lstCustomButtons.add(new Label("Time of Day per Creature:"));
        Button btnLineChart = new Button("Line Chart (Per Creature)");
        btnLineChart.setCursor(Cursor.HAND);
        btnLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.LINE_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations of each Creature for every Time Of Day category.</html>");
            }
        });
        lstCustomButtons.add(btnLineChart);
        // Stacked Bar Chart
        Button btnStackedBarChart = new Button("Bar Chart (Per Creature)");
        btnStackedBarChart.setCursor(Cursor.HAND);
        btnStackedBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.STACKED_BAR_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations of each Creature for every Time Of Day category.</html>");
            }
        });
        lstCustomButtons.add(btnStackedBarChart);
        
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
                else
                if (chartType.equals(ChartType.PIE_CHART)) {
                    displayedChart = createPieChart(lstData);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }
    
    private Chart createStackedBarChart(List<Sighting> inSightings) {
        ObservableList<StackedBarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        Map<String, Map<ActiveTimeSpesific, ReportDataWrapper>> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Map<ActiveTimeSpesific, ReportDataWrapper> mapTimeOfDayForElements = mapData.get(sighting.getElementName());
            if (mapTimeOfDayForElements == null) {
                mapTimeOfDayForElements = new HashMap<>();
                mapData.put(sighting.getElementName(), mapTimeOfDayForElements);
            }
            ReportDataWrapper dataWrapper = mapTimeOfDayForElements.get(sighting.getTimeOfDay());
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(sighting.getTimeOfDay().toString(), sighting.getElementName(), 0);
                mapTimeOfDayForElements.put(sighting.getTimeOfDay(), dataWrapper);
            }
            dataWrapper.increaseCount();
        }
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            int total = 0;
            ObservableList<StackedBarChart.Data<String, Number>> lstSeriesData = FXCollections.observableArrayList();
            Map<ActiveTimeSpesific, ReportDataWrapper> mapTimeOfDayForElements = mapData.get(key);
            for (ActiveTimeSpesific activeTimeSpesific : mapTimeOfDayForElements.keySet()) {
                int count = mapTimeOfDayForElements.get(activeTimeSpesific).count;
                lstSeriesData.add(new StackedBarChart.Data<String, Number>(activeTimeSpesific.toString(), count));
                total = total + count;
            }
            StackedBarChart.Series<String, Number> series = new StackedBarChart.Series<String, Number>(
                    key + " (" + total + ")", 
                    lstSeriesData);
            chartData.add(series);
        }
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, "Number of Observations");
        CategoryAxis catAxis = new CategoryAxis();
        catAxis.setCategories(FXCollections.<String>observableArrayList(ActiveTimeSpesific.getEnumListAsString()));
        catAxis.setTickLabelRotation(-90);
        catAxis.setTickLabelFont(Font.font(15));
        StackedBarChart<String, Number> chart = new StackedBarChart<String, Number>(catAxis, numAxis, chartData);
        return chart;
    }
    
    private Chart createLineChart(List<Sighting> inSightings) {
        List<String> lstActiveTimeSpesific = ActiveTimeSpesific.getEnumListAsString();
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, "Number of Observations");
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
        AreaChart<String, Number> chart = new AreaChart<String, Number>(axisX, numAxis, chartData);
        return chart;
    }

    private Chart createBarChart(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapData.get(sighting.getTimeOfDay().toString());
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(null, null, 0);
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
            data.nodeProperty().addListener(new BarChartChangeListener<>(allSightings.size(), mapData.size(), data, UtilsReports.COLOURS_30));
            allSightings.add(data);
            if (mapData.get(key).count > maxCount) {
                maxCount = mapData.get(key).count;
            }
        }
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis(0, (int)(maxCount*1.2), maxCount/10);
// FIXME: Autoranging kap soms die boonste getalletjie af...??
        UtilsReports.setupNumberAxis(numAxis, "Number of Observations");
        CategoryAxis catAxis = new CategoryAxis();
        catAxis.setCategories(FXCollections.<String>observableArrayList(ActiveTimeSpesific.getEnumListAsString()));
        catAxis.setTickLabelFont(Font.font(14));
        catAxis.setTickLabelRotation(-90);
        chartData.add(new BarChart.Series<String, Number>("Observations (" + allSightings.size() + ")", allSightings));
        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, chartData);
        chart.setLegendVisible(false);
        return chart;
    }
    
    private Chart createPieChart(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapGroupedData.get(sighting.getTimeOfDay().toString());
            if (dataWrapper == null) {
                mapGroupedData.put(sighting.getTimeOfDay().toString(), new ReportDataWrapper("", "", 1));
            }
            else {
                dataWrapper.increaseCount();
            }
        }
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        List<String> keys = ActiveTimeSpesific.getEnumListAsString();
        for (String key : keys) {
            if (mapGroupedData.containsKey(key)) {
                String text = key;
                if (text.isEmpty()) {
                    text = ActiveTimeSpesific.NONE.getDescription();
                }
                PieChart.Data data = new PieChart.Data(text + " (" + mapGroupedData.get(key).getCount() + ")", mapGroupedData.get(key).getCount());
                data.nodeProperty().addListener(new PieChartChangeListener<>(key, UtilsReports.COLOURS_TIME_OF_DAY));
                chartData.add(data);
            }
        }
        PieChart chart = new PieChart(chartData);
//        chart.setLegendVisible(false);
        return chart;
    }
    
}
