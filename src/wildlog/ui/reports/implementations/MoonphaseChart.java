package wildlog.ui.reports.implementations;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.Moonlight;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.utils.UtilsTime;


public class MoonphaseChart extends AbstractReport<Sighting> {
    private enum ChartType {BAR_CHART_ALL, BAR_CHART_ELEMENTS, LINE_CHART_ALL, LINE_CHART_ELEMENTS};
    private ChartType chartType = ChartType.BAR_CHART_ALL;
    private Chart displayedChart;
    private boolean showDayOrNight = false;
    private boolean showMoonShiningOrNot = false;
    private int PERCENTAGES_PER_INTERVAL = 10;
    
    public MoonphaseChart() {
        super("Moon Phase", "<html>This collection of charts focus on the moon phase that was present at the time of the Observation. "
                + "The phase and visibilaty of the moon isn't tied to the phase of sun and can be visible during the day or night.</html>");
        lstCustomButtons = new ArrayList<>(5);
        // Area/Line Chart
        JButton btnLineChart = new JButton("Bar Chart All");
        btnLineChart.setFocusPainted(false);
        btnLineChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLineChart.setMargin(new Insets(2, 4, 2, 4));
        btnLineChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.BAR_CHART_ALL;
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
        JButton btnStackedBarChart = new JButton("Bar Chart Creatures");
        btnStackedBarChart.setFocusPainted(false);
        btnStackedBarChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnStackedBarChart.setMargin(new Insets(2, 4, 2, 4));
        btnStackedBarChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.BAR_CHART_ELEMENTS;
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
        JButton btnBarChart = new JButton("Line Chart All");
        btnBarChart.setFocusPainted(false);
        btnBarChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBarChart.setMargin(new Insets(2, 4, 2, 4));
        btnBarChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.LINE_CHART_ALL;
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
        // Bar Chart
        JButton btnBarChart2 = new JButton("Line Chart Creatures");
        btnBarChart2.setFocusPainted(false);
        btnBarChart2.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBarChart2.setMargin(new Insets(2, 4, 2, 4));
        btnBarChart2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.LINE_CHART_ELEMENTS;
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
        lstCustomButtons.add(btnBarChart2);
        // Show day, night, twilight
        JCheckBox chkShowDetails = new JCheckBox("Show day/night");
        chkShowDetails.setFocusPainted(false);
        chkShowDetails.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        chkShowDetails.setMargin(new Insets(2, 4, 2, 4));
        chkShowDetails.setSelected(false);
        chkShowDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDayOrNight = chkShowDetails.isSelected();
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
        lstCustomButtons.add(chkShowDetails);
        // Show moon shining or not
        JCheckBox chkShowMoonlight = new JCheckBox("Show Moonlight");
        chkShowMoonlight.setFocusPainted(false);
        chkShowMoonlight.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        chkShowMoonlight.setMargin(new Insets(2, 4, 2, 4));
        chkShowMoonlight.setSelected(false);
        chkShowMoonlight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMoonShiningOrNot = chkShowMoonlight.isSelected();
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
        lstCustomButtons.add(chkShowMoonlight);
        // TODO: Sit 'n pie chart by
    }

    @Override
    public Chart createChart() {
        displayedChart = null;
        if (chartType.equals(ChartType.BAR_CHART_ALL)) {
            displayedChart = createBarChartForAll(lstData);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART_ELEMENTS)) {
            displayedChart = createBarChartForElements(lstData);
        }
        else
        if (chartType.equals(ChartType.LINE_CHART_ALL)) {
            displayedChart = createLineChartForAll(lstData);
        }
        else
        if (chartType.equals(ChartType.LINE_CHART_ELEMENTS)) {
            displayedChart = createLineChartForElements(lstData);
        }
        return displayedChart;
    }
    
    private Chart createBarChartForAll(List<Sighting> inSightings) {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
        axisX.setCategories(FXCollections.<String>observableArrayList(new String[]{"Moon 0-50% Full", "Moon 50-100% Full", "Unknown"}));
        // Build the data map that will be displayed
        Map<String, ObservableList<StackedBarChart.Data<String, Number>>> mapChartDataGroupedForSeries = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ObservableList<StackedBarChart.Data<String, Number>> lstChartData = mapChartDataGroupedForSeries.get("All Observations" + getDetailsString(sighting));
            if (lstChartData == null) {
                lstChartData = FXCollections.observableArrayList();
                mapChartDataGroupedForSeries.put("All Observations" + getDetailsString(sighting), lstChartData);
            }
            if (sighting.getMoonPhase() >= 0) {
                if (sighting.getMoonPhase() >= 0 && sighting.getMoonPhase() < 50) {
                    lstChartData.add(new StackedBarChart.Data<String, Number>("Moon 0-50% Full", 1));
                } 
                else
                if (sighting.getMoonPhase() > 50 && sighting.getMoonPhase() <= 100) {
                    lstChartData.add(new StackedBarChart.Data<String, Number>("Moon 50-100% Full", 1));
                } 
                else
                // If the moon is 50% then base it on whether the moon is growing or shrinking.
                if (sighting.getMoonPhase() == 50) {
                    LocalDateTime futureTime = UtilsTime.getLocalDateTimeFromDate(sighting.getDate()).plusDays(2);
                    int testMoonphase = AstroCalculator.getMoonPhase(Date.from(futureTime.atZone(ZoneId.systemDefault()).toInstant()));
                    if (testMoonphase >= 0 && testMoonphase < 50) {
                        lstChartData.add(new StackedBarChart.Data<String, Number>("Moon 0-50% Full", 1));
                    } 
                    else
                    if (testMoonphase > 50 && testMoonphase <= 100) {
                        lstChartData.add(new StackedBarChart.Data<String, Number>("Moon 50-100% Full", 1));
                    } 
                    else {
                        lstChartData.add(new StackedBarChart.Data<String, Number>("Unknown", 1));
                    }
                }
                else {
                    lstChartData.add(new StackedBarChart.Data<String, Number>("Unknown", 1));
                }
            }
            else {
                lstChartData.add(new StackedBarChart.Data<String, Number>("Unknown", 1));
            }
        }
        // Setup the final data series to be displayed
        ObservableList<StackedBarChart.Series<String, Number>> lstChartSeries = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapChartDataGroupedForSeries.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            StackedBarChart.Series<String, Number> series = new StackedBarChart.Series<String, Number>(
                    key + " (" + mapChartDataGroupedForSeries.get(key).size() + ")", 
                    mapChartDataGroupedForSeries.get(key));
            lstChartSeries.add(series);
        }
        // Create the chart
        StackedBarChart<String, Number> chart = new StackedBarChart<String, Number>(axisX, axisY, lstChartSeries);
        return chart;
    }
    
    private Chart createBarChartForElements(List<Sighting> inSightings) {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
        axisX.setCategories(FXCollections.<String>observableArrayList(new String[]{"Moon 0-50% Full", "Moon 50-100% Full", "Unknown"}));
        // Build the data map that will be displayed
        Map<String, ObservableList<StackedBarChart.Data<String, Number>>> mapChartDataGroupedForSeries = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ObservableList<StackedBarChart.Data<String, Number>> lstChartData = mapChartDataGroupedForSeries.get(sighting.getElementName() + getDetailsString(sighting));
            if (lstChartData == null) {
                lstChartData = FXCollections.observableArrayList();
                mapChartDataGroupedForSeries.put(sighting.getElementName() + getDetailsString(sighting), lstChartData);
            }
            if (sighting.getMoonPhase() >= 0) {
                if (sighting.getMoonPhase() >= 0 && sighting.getMoonPhase() < 50) {
                    lstChartData.add(new StackedBarChart.Data<String, Number>("Moon 0-50% Full", 1));
                } 
                else
                if (sighting.getMoonPhase() > 50 && sighting.getMoonPhase() <= 100) {
                    lstChartData.add(new StackedBarChart.Data<String, Number>("Moon 50-100% Full", 1));
                } 
                else
                // If the moon is 50% then base it on whether the moon is growing or shrinking.
                if (sighting.getMoonPhase() == 50) {
                    LocalDateTime futureTime = UtilsTime.getLocalDateTimeFromDate(sighting.getDate()).plusDays(2);
                    int testMoonphase = AstroCalculator.getMoonPhase(Date.from(futureTime.atZone(ZoneId.systemDefault()).toInstant()));
                    if (testMoonphase >= 0 && testMoonphase < 50) {
                        lstChartData.add(new StackedBarChart.Data<String, Number>("Moon 0-50% Full", 1));
                    } 
                    else
                    if (testMoonphase > 50 && testMoonphase <= 100) {
                        lstChartData.add(new StackedBarChart.Data<String, Number>("Moon 50-100% Full", 1));
                    } 
                    else {
                        lstChartData.add(new StackedBarChart.Data<String, Number>("Unknown", 1));
                    }
                }
                else {
                    lstChartData.add(new StackedBarChart.Data<String, Number>("Unknown", 1));
                }
            }
            else {
                lstChartData.add(new StackedBarChart.Data<String, Number>("Unknown", 1));
            }
        }
        // Setup the final data series to be displayed
        ObservableList<StackedBarChart.Series<String, Number>> lstChartSeries = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapChartDataGroupedForSeries.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            StackedBarChart.Series<String, Number> series = new StackedBarChart.Series<String, Number>(
                    key + " (" + mapChartDataGroupedForSeries.get(key).size() + ")", 
                    mapChartDataGroupedForSeries.get(key));
            lstChartSeries.add(series);
        }
        // Create the chart
        StackedBarChart<String, Number> chart = new StackedBarChart<String, Number>(axisX, axisY, lstChartSeries);
        return chart;
    }
    
    private Chart createLineChartForAll(List<Sighting> inSightings) {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
        ObservableList<String> lstXCategories = FXCollections.<String>observableArrayList();
        for (int percentage = 0; percentage < 100/PERCENTAGES_PER_INTERVAL; percentage++) {
            lstXCategories.add(getMoonIntervalPercentage(percentage*PERCENTAGES_PER_INTERVAL));
        }
        lstXCategories.add(Moonlight.UNKNOWN.toString());
        axisX.setCategories(lstXCategories);
        // Get the data in the correct structure
        Map<String, ReportDataWrapper> mapInitialCountedData = new HashMap<>();
        Map<String, Integer> mapTotalsForSeries = new HashMap<>();
        for (Sighting sighting : inSightings) {
            
// TODO: Merge met die ander chart want dis baises identies ek hardcode net die "spesie naam" hier na "All Observations"
            
            ReportDataWrapper dataWrapper = mapInitialCountedData.get("All Observations" + getDetailsString(sighting) 
                    + "-" + getMoonIntervalPercentage(sighting.getMoonPhase()));
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper();
                dataWrapper.key = "All Observations" + getDetailsString(sighting);
                if (sighting.getMoonPhase() >= 0) {
                    dataWrapper.value = getMoonIntervalPercentage(sighting.getMoonPhase());
                }
                else {
                    dataWrapper.value = Moonlight.UNKNOWN.toString();
                }
                dataWrapper.count = 0;
                mapTotalsForSeries.put("All Observations" + getDetailsString(sighting), 0);
            }
            dataWrapper.count++;
            mapInitialCountedData.put("All Observations" + getDetailsString(sighting) 
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
        AreaChart<String, Number> chart = new AreaChart<String, Number>(axisX, axisY, chartData);
        return chart;
    }
    
    private Chart createLineChartForElements(List<Sighting> inSightings) {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
        CategoryAxis axisX = new CategoryAxis();
        ObservableList<String> lstXCategories = FXCollections.<String>observableArrayList();
        for (int percentage = 0; percentage < 100/PERCENTAGES_PER_INTERVAL; percentage++) {
            lstXCategories.add(getMoonIntervalPercentage(percentage*PERCENTAGES_PER_INTERVAL));
        }
        lstXCategories.add(Moonlight.UNKNOWN.toString());
        axisX.setCategories(lstXCategories);
        // Get the data in the correct structure
        Map<String, ReportDataWrapper> mapInitialCountedData = new HashMap<>();
        Map<String, Integer> mapTotalsForSeries = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapInitialCountedData.get(sighting.getElementName() + getDetailsString(sighting) 
                    + "-" + getMoonIntervalPercentage(sighting.getMoonPhase()));
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper();
                dataWrapper.key = sighting.getElementName() + getDetailsString(sighting);
                if (sighting.getMoonPhase() >= 0) {
                    dataWrapper.value = getMoonIntervalPercentage(sighting.getMoonPhase());
                }
                else {
                    dataWrapper.value = Moonlight.UNKNOWN.toString();
                }
                dataWrapper.count = 0;
                mapTotalsForSeries.put(sighting.getElementName() + getDetailsString(sighting), 0);
            }
            dataWrapper.count++;
            mapInitialCountedData.put(sighting.getElementName() + getDetailsString(sighting) 
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
        AreaChart<String, Number> chart = new AreaChart<String, Number>(axisX, axisY, chartData);
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
