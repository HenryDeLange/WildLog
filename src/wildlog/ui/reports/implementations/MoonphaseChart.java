package wildlog.ui.reports.implementations;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Moonlight;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.ui.utils.UtilsTime;


public class MoonphaseChart extends AbstractReport<Sighting> {
    private enum ChartType {BAR_CHART_ALL, BAR_CHART_ELEMENTS, LINE_CHART_ALL, LINE_CHART_ELEMENTS, PIE_CHART};
    private ChartType chartType = ChartType.BAR_CHART_ALL;
    private Chart displayedChart;
    private boolean showDayOrNight = false;
    private boolean showMoonShiningOrNot = false;
    private final int PERCENTAGES_PER_INTERVAL = 10;
    
    public MoonphaseChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Moon Phase Reports", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(9);
        // Charts
        Button btnPieChart = new Button("All Observations Together (Pie)");
        btnPieChart.setCursor(Cursor.HAND);
        btnPieChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded during a certain Moon Phase as a pie chart."
                        + "<br/>(The phase and visibilaty of the moon isn't tied to the sun and can be visible during the day or night.)</html>");
            }
        });
        lstCustomButtons.add(btnPieChart);
        Button btnLineChart = new Button("All Observations Together (Bar)");
        btnLineChart.setCursor(Cursor.HAND);
        btnLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_ALL;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded during a certain Moon Phase as a bar chart."
                        + "<br/>(The phase and visibilaty of the moon isn't tied to the sun and can be visible during the day or night.)</html>");
            }
        });
        lstCustomButtons.add(btnLineChart);
        Button btnBarChart = new Button("All Observations Together (Line)");
        btnBarChart.setCursor(Cursor.HAND);
        btnBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.LINE_CHART_ALL;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded during a certain Moon Phase as a line chart."
                        + "<br/>(The phase and visibilaty of the moon isn't tied to the sun and can be visible during the day or night.)</html>");
            }
        });
        lstCustomButtons.add(btnBarChart);
        Button btnStackedBarChart = new Button("Grouped by Creatures (Bar)");
        btnStackedBarChart.setCursor(Cursor.HAND);
        btnStackedBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_ELEMENTS;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded for each Creature during a certain Moon Phase as a bar chart."
                        + "<br/>(The phase and visibilaty of the moon isn't tied to the sun and can be visible during the day or night.)</html>");
            }
        });
        lstCustomButtons.add(btnStackedBarChart);
        Button btnBarChart2 = new Button("Grouped by Creatures (Line)");
        btnBarChart2.setCursor(Cursor.HAND);
        btnBarChart2.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.LINE_CHART_ELEMENTS;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded for each Creature during a certain Moon Phase as a bar chart."
                        + "<br/>(The phase and visibilaty of the moon isn't tied to the sun and can be visible during the day or night.)</html>");
            }
        });
        lstCustomButtons.add(btnBarChart2);
        // Chart options
        lstCustomButtons.add(new Label("Chart Options:"));
        CheckBox chkShowDetails = new CheckBox("Show Day/Night/Twilight");
        chkShowDetails.setCursor(Cursor.HAND);
        chkShowDetails.setSelected(false);
        chkShowDetails.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showDayOrNight = chkShowDetails.isSelected();
            }
        });
        lstCustomButtons.add(chkShowDetails);
        CheckBox chkShowMoonlight = new CheckBox("Show Moon Shining/No Moon");
        chkShowMoonlight.setCursor(Cursor.HAND);
        chkShowMoonlight.setSelected(false);
        chkShowMoonlight.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showMoonShiningOrNot = chkShowMoonlight.isSelected();
            }
        });
        lstCustomButtons.add(chkShowMoonlight);
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.BAR_CHART_ALL)) {
                    displayedChart = createBarChart(lstData, true);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_ELEMENTS)) {
                    displayedChart = createBarChart(lstData, false);
                }
                else
                if (chartType.equals(ChartType.LINE_CHART_ALL)) {
                    displayedChart = createLineChart(lstData, true);
                }
                else
                if (chartType.equals(ChartType.LINE_CHART_ELEMENTS)) {
                    displayedChart = createLineChart(lstData, false);
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
    
    private Chart createBarChart(List<Sighting> inSightings, boolean inIsForAllObservations) {
        // Build the data map that will be displayed
        Map<String, Map<String, ReportDataWrapper>> mapChartDataGroupedForSeries = new HashMap<>();
        String temp = null;
        if (inIsForAllObservations) {
            temp = "All Observations";
        }
        for (Sighting sighting : inSightings) {
            if (!inIsForAllObservations) {
                temp = sighting.getElementName();
            }
            Map<String, ReportDataWrapper> mapChartData = mapChartDataGroupedForSeries.get(temp + getDetailsString(sighting));
            if (mapChartData == null) {
                mapChartData = new HashMap<>();
                mapChartDataGroupedForSeries.put(temp + getDetailsString(sighting), mapChartData);
            }
            if (sighting.getMoonPhase() >= 0) {
                if (sighting.getMoonPhase() >= 0 && sighting.getMoonPhase() < 50) {
                    increaseCountInMap(mapChartData, "Moon 0-50% Full");
                } 
                else
                if (sighting.getMoonPhase() > 50 && sighting.getMoonPhase() <= 100) {
                    increaseCountInMap(mapChartData, "Moon 50-100% Full");
                } 
                else
                // If the moon is 50% then base it on whether the moon is growing or shrinking.
                if (sighting.getMoonPhase() == 50) {
                    LocalDateTime futureTime = UtilsTime.getLocalDateTimeFromDate(sighting.getDate()).plusDays(2);
                    int testMoonphase = AstroCalculator.getMoonPhase(Date.from(futureTime.atZone(ZoneId.systemDefault()).toInstant()));
                    if (testMoonphase >= 0 && testMoonphase < 50) {
                        increaseCountInMap(mapChartData, "Moon 0-50% Full");
                    } 
                    else
                    if (testMoonphase > 50 && testMoonphase <= 100) {
                        increaseCountInMap(mapChartData, "Moon 50-100% Full");
                    } 
                    else {
                        increaseCountInMap(mapChartData, "Unknown");
                    }
                }
                else {
                    increaseCountInMap(mapChartData, "Unknown");
                }
            }
            else {
                increaseCountInMap(mapChartData, "Unknown");
            }
        }
        // Setup the final data series to be displayed
        ObservableList<StackedBarChart.Series<String, Number>> lstChartSeries = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapChartDataGroupedForSeries.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            StackedBarChart.Series<String, Number> series = new StackedBarChart.Series<String,Number>();
            Map<String, ReportDataWrapper> mapChartData = mapChartDataGroupedForSeries.get(key);
            int total = 0;
            for (String mapKey : mapChartData.keySet()) {
                int count = mapChartData.get(mapKey).count;
                total = total + count;
                series.getData().add(new StackedBarChart.Data<String, Number>(mapKey, count));
            }
            series.setName(key + " (" + total + ")");
            lstChartSeries.add(series);
        }
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, 3, true);
        catAxis.setCategories(FXCollections.<String>observableArrayList(new String[]{"Moon 0-50% Full", "Moon 50-100% Full", "Unknown"}));
        StackedBarChart<String, Number> chart = UtilsReports.createStackedBarChartWithStyleIndexBiggerThanEight(catAxis, numAxis, lstChartSeries);
        chart.getStyleClass().add("wl-stacked-bar-30-color");
        chart.setTitle("Number of Observations for each Moon Phase");
        return chart;
    }
    
    private void increaseCountInMap(Map<String, ReportDataWrapper> inMap, String inKey) {
        ReportDataWrapper dataWrapper = inMap.get(inKey);
        if (dataWrapper == null) {
            dataWrapper = new ReportDataWrapper(inKey, inKey, 0);
            inMap.put(inKey, dataWrapper);
        }
        dataWrapper.increaseCount();
    }
    
    private Chart createPieChart(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapChartData = new HashMap<>(4);
        for (Sighting sighting : inSightings) {
            String key = "";
            if (sighting.getMoonPhase() >= 0) {
                if (sighting.getMoonPhase() >= 0 && sighting.getMoonPhase() < 50) {
                    key = "Moon 0-50% Full" + getDetailsString(sighting);
                } 
                else
                if (sighting.getMoonPhase() > 50 && sighting.getMoonPhase() <= 100) {
                    key = "Moon 50-100% Full" + getDetailsString(sighting);
                } 
                else
                // If the moon is 50% then base it on whether the moon is growing or shrinking.
                if (sighting.getMoonPhase() == 50) {
                    LocalDateTime futureTime = UtilsTime.getLocalDateTimeFromDate(sighting.getDate()).plusDays(2);
                    int testMoonphase = AstroCalculator.getMoonPhase(Date.from(futureTime.atZone(ZoneId.systemDefault()).toInstant()));
                    if (testMoonphase >= 0 && testMoonphase < 50) {
                        key = "Moon 0-50% Full" + getDetailsString(sighting);
                    } 
                    else
                    if (testMoonphase > 50 && testMoonphase <= 100) {
                        key = "Moon 50-100% Full" + getDetailsString(sighting);
                    } 
                    else {
                        key = "Unknown";
                    }
                }
                else {
                    key = "Unknown";
                }
            }
            else {
                key = "Unknown";
            }
            ReportDataWrapper dataWrapper = mapChartData.get(key);
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(null, null, 0);
                mapChartData.put(key, dataWrapper);
            }
            dataWrapper.increaseCount();
        }
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapChartData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            String text = key;
            if (text.isEmpty()) {
                text = ActiveTimeSpesific.NONE.getDescription();
            }
            chartData.add(new PieChart.Data(text + " (" + mapChartData.get(key).getCount() + ")", mapChartData.get(key).getCount()));
        }
        PieChart chart = UtilsReports.createPieChartWithStyleIndexReset(chartData);
        chart.getStyleClass().add("wl-pie-30-color");
        chart.setTitle("Number of Observations for each Moon Phase");
        return chart;
    }
    
    private Chart createLineChart(List<Sighting> inSightings, boolean inIsForAllObservations) {
        ObservableList<String> lstXCategories = FXCollections.<String>observableArrayList();
        for (int percentage = 0; percentage < 100/PERCENTAGES_PER_INTERVAL; percentage++) {
            lstXCategories.add(getMoonIntervalPercentage(percentage*PERCENTAGES_PER_INTERVAL));
        }
        lstXCategories.add(Moonlight.UNKNOWN.toString());
        // Get the data in the correct structure
        Map<String, ReportDataWrapper> mapInitialCountedData = new HashMap<>();
        Map<String, Integer> mapTotalsForSeries = new HashMap<>();
        String temp = null;
        if (inIsForAllObservations) {
            temp = "All Observations";
        }
        for (Sighting sighting : inSightings) {
            if (!inIsForAllObservations) {
                temp = sighting.getElementName();
            }
            ReportDataWrapper dataWrapper = mapInitialCountedData.get(temp + getDetailsString(sighting) 
                    + "-" + getMoonIntervalPercentage(sighting.getMoonPhase()));
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper();
                dataWrapper.key = temp + getDetailsString(sighting);
                if (sighting.getMoonPhase() >= 0) {
                    dataWrapper.value = getMoonIntervalPercentage(sighting.getMoonPhase());
                }
                else {
                    dataWrapper.value = Moonlight.UNKNOWN.toString();
                }
                dataWrapper.count = 0;
                mapTotalsForSeries.put(temp + getDetailsString(sighting), 0);
            }
            dataWrapper.count++;
            mapInitialCountedData.put(temp + getDetailsString(sighting) 
                    + "-" + getMoonIntervalPercentage(sighting.getMoonPhase()), dataWrapper);
        }
        // Add all the points on the chart in the correct order. This also adds the 0 values for the data gaps.
        Map<String, ObservableList<AreaChart.Data<String, Number>>> mapDataPerElement = new HashMap<>(mapInitialCountedData.size());
        for (String seriesName : mapTotalsForSeries.keySet()) {
            ObservableList<AreaChart.Data<String, Number>> lstChartDataForElement = FXCollections.observableArrayList();
            mapDataPerElement.put(seriesName, lstChartDataForElement);
            for (int percentage = 0; percentage <= 100/PERCENTAGES_PER_INTERVAL; percentage++) {
                lstChartDataForElement.add(new AreaChart.Data<String, Number>(getMoonIntervalPercentage(percentage*PERCENTAGES_PER_INTERVAL), 0));
            }
        }
        // Set the DataWrapper values in an ObservableList
        for (ReportDataWrapper dataWrapper : mapInitialCountedData.values()) {
            ObservableList<AreaChart.Data<String, Number>> lstChartDataForElement = mapDataPerElement.get(dataWrapper.key);
            for (AreaChart.Data<String, Number> data : lstChartDataForElement) {
                if (data.getXValue().equals(dataWrapper.value)) {
                    data.setYValue(dataWrapper.count);
                    mapTotalsForSeries.put(dataWrapper.key, mapTotalsForSeries.get(dataWrapper.key) + dataWrapper.count);
                    break;
                }
            }
        }
        // Construct the final list of series that needs to be displayed
        ObservableList<AreaChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapTotalsForSeries.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            AreaChart.Series<String, Number> series = new AreaChart.Series<String, Number>(
                    key + " (" + mapTotalsForSeries.get(key) + ")", 
                    mapDataPerElement.get(key));
            chartData.add(series);
        }
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, 3, false);
        catAxis.setCategories(lstXCategories);
        AreaChart<String, Number> chart = new AreaChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-line-30-color");
        chart.setLegendVisible(true);
        chart.setTitle("Number of Observations for each Moon Phase");
        return chart;
    }
    
    private String getDetailsString(Sighting inSighting) {
        String temp = "";
        if (showDayOrNight) {
            temp = temp + " - " + ActiveTime.getFromActiveTimeSpecific(inSighting.getTimeOfDay()).toString();
        }
        if (showMoonShiningOrNot) {
            String moonShining;
            if (inSighting.getMoonlight() != null) {
                moonShining = inSighting.getMoonlight().toString();
            }
            else {
                moonShining = Moonlight.UNKNOWN.toString();
            }
            temp = temp + " - " + moonShining;
        }
        return temp;
    }
    
    private String getMoonIntervalPercentage(int inValue) {
        if (inValue >= 100) {
            inValue = 99;
        }
        return Integer.toString((inValue/PERCENTAGES_PER_INTERVAL)*PERCENTAGES_PER_INTERVAL) + "-" 
                + Integer.toString(((inValue + PERCENTAGES_PER_INTERVAL)/PERCENTAGES_PER_INTERVAL)*PERCENTAGES_PER_INTERVAL) + "%";
    }
    
}
