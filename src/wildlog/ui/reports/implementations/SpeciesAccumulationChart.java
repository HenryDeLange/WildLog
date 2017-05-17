package wildlog.ui.reports.implementations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTime;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.ui.utils.UtilsTime;


public class SpeciesAccumulationChart extends AbstractReport<Sighting> {
    private enum ChartType {ACCUMULATION_LINE_CHART, DAILY_CREATURE_LINE_CHART, DAILY_OBSERVATION_LINE_CHART};
    private ChartType chartType = ChartType.ACCUMULATION_LINE_CHART;
    private Chart displayedChart;
    private boolean showDayOrNight = false;

    
// Nuwe chart wat die species accumulation curvevan verskillende periods gelyk op die mak kan wys om te vergelyk
    
    public SpeciesAccumulationChart(List<Sighting> inLstData, JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        super("Recording Rate Reports", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(5);
        // Line charts
        ToggleButton btnLineChart = new ToggleButton("Creature Accumulation (Line)");
        btnLineChart.setToggleGroup(BUTTON_GROUP);
        btnLineChart.setCursor(Cursor.HAND);
        btnLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.ACCUMULATION_LINE_CHART;
                setupChartDescriptionLabel("<html>This chart illustrates the rate at which new Creatures were recorded over time.</html>");
            }
        });
        lstCustomButtons.add(btnLineChart);
        ToggleButton btnDailyObsLineChart = new ToggleButton("Daily Observation Count (Line)");
        btnDailyObsLineChart.setToggleGroup(BUTTON_GROUP);
        btnDailyObsLineChart.setCursor(Cursor.HAND);
        btnDailyObsLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.DAILY_OBSERVATION_LINE_CHART;
                setupChartDescriptionLabel("<html>This chart illustrates the number of Observations recorded per day (calendar day, not the real day-night cycle).</html>");
            }
        });
        lstCustomButtons.add(btnDailyObsLineChart);
        ToggleButton btnDailyLineChart = new ToggleButton("Daily Creature Count (Line)");
        btnDailyLineChart.setToggleGroup(BUTTON_GROUP);
        btnDailyLineChart.setCursor(Cursor.HAND);
        btnDailyLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.DAILY_CREATURE_LINE_CHART;
                setupChartDescriptionLabel("<html>This chart illustrates the number of Creatures recorded per day (calendar day, not the real day-night cycle).</html>");
            }
        });
        lstCustomButtons.add(btnDailyLineChart);
        // Options
        lstCustomButtons.add(new Label("Chart Options:"));
        CheckBox chkShowDetails = new CheckBox("Show Day, Night or Twilight");
        chkShowDetails.setCursor(Cursor.HAND);
        chkShowDetails.setSelected(false);
        chkShowDetails.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showDayOrNight = chkShowDetails.isSelected();
            }
        });
        lstCustomButtons.add(chkShowDetails);
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.ACCUMULATION_LINE_CHART)) {
                    setActiveSubCategoryTitle("Creature Accumulation (Line)");
                    displayedChart = createAccumulationReport(lstData);
                }
                else
                if (chartType.equals(ChartType.DAILY_CREATURE_LINE_CHART)) {
                    setActiveSubCategoryTitle("Daily Creature Count (Line)");
                    displayedChart = createDailyReport(lstData, false);
                }
                else
                if (chartType.equals(ChartType.DAILY_OBSERVATION_LINE_CHART)) {
                    setActiveSubCategoryTitle("Daily Observation Count (Line)");
                    displayedChart = createDailyReport(lstData, true);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }
    
    private Chart createAccumulationReport(List<Sighting> inSightings) {
// TODO: Include an option to include the start and end date of the first and last Visits, repectively, in the cart, to get an idea of how it slowed down toward the end...
        // Get the data in the correct structure
        // Get sorted (by date) Sightings list
        Collections.sort(inSightings);
        Map<String, LocalDateTime> mapAccumulation = new LinkedHashMap<>();
        for (Sighting sighting : inSightings) {
            mapAccumulation.putIfAbsent(sighting.getElementName(), UtilsTime.getLocalDateTimeFromDate(sighting.getDate()));
        }
        // Construct the final list of series that needs to be displayed
        int counter = 1;
        ObservableList<AreaChart.Data<Number, Number>> lstChartData = FXCollections.observableArrayList();
        long startTime = 0;
        long endTime = 0;
        for (String elementName : mapAccumulation.keySet()) {
            endTime = UtilsTime.getDateFromLocalDateTime(mapAccumulation.get(elementName)).getTime();
            if (startTime == 0) {
                startTime = endTime;
                // Add an entry to the front (and back) to make the first (and last) entry more visible
                lstChartData.add(new AreaChart.Data<>(startTime, 0, ""));
            }
            lstChartData.add(new AreaChart.Data<>(endTime, counter++, elementName));
        }
        ObservableList<AreaChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
        chartData.add(new AreaChart.Series<>("All Creatures", lstChartData));
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        NumberAxis dateAxis = new NumberAxis();
        dateAxis.setAutoRanging(true);
        dateAxis.setForceZeroInRange(false);
        dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return UtilsTime.WL_DATE_FORMATTER.format(UtilsTime.getLocalDateTimeFromDate(new Date(object.longValue())));
            }
            @Override
            public Number fromString(String string) {
                return UtilsTime.WL_DATE_FORMATTER.parse(string).get(ChronoField.MILLI_OF_SECOND);
            }
        });
        dateAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14));
        AreaChart<Number, Number> chart = new AreaChart<Number, Number>(dateAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-line-30-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of new Creatures observed over Time");
        UtilsReports.setupChartTooltips(chart, true, true);
        return chart;
    }
    
// FIXME: Baie stadig vir groot datasets. (Dalk ook tedoen met as daar 'n paar series is wat oor 'n groot x-axis gaan, basies te veel datums om te render?)
    private Chart createDailyReport(List<Sighting> inSightings, boolean inIsForAllObservations) {
        Map<String, Map<String, ReportDataWrapper>> mapSeries = new HashMap<>();
        // Get sorted (by date) Sightings list
        Collections.sort(inSightings);
        // Setup the initial maps that will be used for the chart data
        for (Sighting sighting : inSightings) {
            String key;
            if (inIsForAllObservations) {
                if (showDayOrNight) {
                    key = ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()).toString();
                }
                else {
                    key = "All Observations";
                }
            }
            else {
                key = sighting.getElementName(reportsBaseDialog.getOptionName());
                if (showDayOrNight) {
                    key = key + " - " + ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()).toString();
                }
            }
            Map<String, ReportDataWrapper> mapColumnData = mapSeries.get(key);
            if (mapColumnData == null) {
                mapColumnData = new HashMap<>();
                mapSeries.put(key, mapColumnData);
            }
            String dayString = UtilsTime.WL_DATE_FORMATTER.format(UtilsTime.getLocalDateTimeFromDate(sighting.getDate()));
            ReportDataWrapper dataWrapper = mapColumnData.get(dayString);
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(dayString, sighting.getElementName(reportsBaseDialog.getOptionName()), 0);
                mapColumnData.put(dayString, dataWrapper);
            }
            dataWrapper.increaseCount();
        }
        // Setup the chart data
        AreaChart<String, Number> chart;
        if (!inSightings.isEmpty()) {
            LocalDate firstDate = UtilsTime.getLocalDateFromDate(inSightings.get(0).getDate());
            LocalDate lastDate = UtilsTime.getLocalDateFromDate(inSightings.get(inSightings.size() - 1).getDate());
            int numberOfDays = (int) Duration.between(firstDate.atTime(0, 0), lastDate.atTime(0, 0)).toDays();
            ObservableList<AreaChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
            List<String> keys;
            if (inIsForAllObservations && showDayOrNight) {
                keys = ActiveTime.getEnumListAsStringForReports();
            }
            else {
                keys = new ArrayList<>(mapSeries.keySet());
            }
            Collections.sort(keys);
            for (String key : keys) {
                Map<String, ReportDataWrapper> mapColumnData = mapSeries.get(key);
                if (mapColumnData != null) {
                    int seriesDataCount = 0;
                    ObservableList<AreaChart.Data<String, Number>> lstChartData = FXCollections.observableArrayList();
                    LocalDate loopDate = firstDate;
                    while (!loopDate.isAfter(lastDate)) {
                        String dateString = UtilsTime.WL_DATE_FORMATTER.format(loopDate);
                        ReportDataWrapper reportDataWrapper = mapColumnData.get(dateString);
                        if (reportDataWrapper != null) {
                            lstChartData.add(new AreaChart.Data<String, Number>(dateString, reportDataWrapper.count, key));
                            seriesDataCount = seriesDataCount + reportDataWrapper.count;
                        }
                        else {
                            lstChartData.add(new AreaChart.Data<String, Number>(dateString, 0, key));
                        }
                        loopDate = loopDate.plusDays(1);
                    }
                    AreaChart.Series<String, Number> series = new AreaChart.Series<String, Number>(
                            key + " (" + seriesDataCount + ")", lstChartData);
                    chartData.add(series);
                }
                else {
                    if (!ActiveTime.UNKNOWN.equals(ActiveTime.getEnumFromText(key))
                            && !ActiveTime.NONE.equals(ActiveTime.getEnumFromText(key))) {
                        chartData.add(new AreaChart.Series<>(key + " (0)", FXCollections.observableArrayList()));
                    }
                }
            }
            // Setup the axis
            NumberAxis numAxis = new NumberAxis();
            UtilsReports.setupNumberAxis(numAxis, false);
            CategoryAxis catAxis = new CategoryAxis();
            UtilsReports.setupCategoryAxis(catAxis, numberOfDays, true);
            chart = new AreaChart<String, Number>(catAxis, numAxis, chartData);
        }
        else {
            chart = new AreaChart<String, Number>(new CategoryAxis(), new NumberAxis(), FXCollections.observableArrayList());
        }
        if (inIsForAllObservations && showDayOrNight) {
            chart.getStylesheets().add("wildlog/ui/reports/chart/styling/ChartsDayNightTwilight.css");
            chart.getStyleClass().add("wl-line-day-night-color");
        }
        else {
            chart.getStyleClass().add("wl-line-30-color");
        }
        if (inIsForAllObservations) {
            if (showDayOrNight) {
                chart.setTitle("Number of Observations per Day, Night and Twilight");
                chart.setLegendVisible(true);
            }
            else {
                chart.setTitle("Number of Observations per calendar day");
                chart.setLegendVisible(false);
            }
        }
        else {
            if (showDayOrNight) {
                chart.setTitle("Number of Creatures per Day, Night and Twilight");
            }
            else {
                chart.setTitle("Number of Creatures per calendar day");
            }
            chart.setLegendVisible(true);
        }
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }
    
}
