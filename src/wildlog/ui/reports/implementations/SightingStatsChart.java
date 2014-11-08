package wildlog.ui.reports.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.util.StringConverter;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTime;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportCountWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.ui.utils.UtilsTime;


public class SightingStatsChart extends AbstractReport<Sighting> {
    private enum ChartType {NUMBER_PER_SIGHTING_CHART, SUBSEQUENT_CHART, SIGHTINGS_PER_DAY_CHART};
    private ChartType chartType = ChartType.NUMBER_PER_SIGHTING_CHART;
    private Chart displayedChart;
    private RadioButton chkAve;
    private RadioButton chkMax;
      
    
    public SightingStatsChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Observation Statistics Reports", inLstData, inChartDescLabel);
//        "<html>This collection of charts focus on statistics of the Observations.</html>"
        lstCustomButtons = new ArrayList<>(6);
        // Bar chart
        Button btnElementPerSightingBarChart = new Button("Number of Individuals");
        btnElementPerSightingBarChart.setCursor(Cursor.HAND);
        btnElementPerSightingBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.NUMBER_PER_SIGHTING_CHART;
                
            }
        });
        lstCustomButtons.add(btnElementPerSightingBarChart);
        // Bar chart
        Button btnSightingsPerDayBarChart = new Button("Observations per Day");
        btnSightingsPerDayBarChart.setCursor(Cursor.HAND);
        btnSightingsPerDayBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.SIGHTINGS_PER_DAY_CHART;
                
            }
        });
        lstCustomButtons.add(btnSightingsPerDayBarChart);
        // Bar chart
        Button btnSightingChanceBarChart = new Button("Subsequent Observations");
        btnSightingChanceBarChart.setCursor(Cursor.HAND);
        btnSightingChanceBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.SUBSEQUENT_CHART;
                
            }
        });
        lstCustomButtons.add(btnSightingChanceBarChart);
        // Chart options
        lstCustomButtons.add(new Label("Chart Options:"));
        ToggleGroup toggleGroup = new ToggleGroup();
        chkAve = new RadioButton("Average");
        chkAve.setToggleGroup(toggleGroup);
        chkAve.setCursor(Cursor.HAND);
        chkAve.setSelected(true);
        lstCustomButtons.add(chkAve);
        // Maximum
        chkMax = new RadioButton("Maximum");
        chkMax.setToggleGroup(toggleGroup);
        chkMax.setCursor(Cursor.HAND);
        chkMax.setSelected(false);
        lstCustomButtons.add(chkMax);
// TODO: Sit dalk nog 'n "mode" (nee dis nie regtig mode waarvoor ek soek nie) ook by, dit sal dalk resultate gee wat meer sin maak vir goed soos buffels wat normaalweg net 1-5 is, maar soms 200+
// TODO: SO verander die radiobuttons dalk in 'n combobox met opsies Max, Average (Mean), Most Frequent Value (Mode) en Significt Frequency Distribution (The more time a value is present the more relevant it becomes)

    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.NUMBER_PER_SIGHTING_CHART)) {
                    displayedChart = createNumOfElementsPerSightingBarChart(lstData);
                }
                else
                if (chartType.equals(ChartType.SUBSEQUENT_CHART)) {
                    displayedChart = createSubsequentSightingBarChart(lstData);
                }
                else
                if (chartType.equals(ChartType.SIGHTINGS_PER_DAY_CHART)) {
                    displayedChart = createSightingPerDayBarChart(lstData);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }
    
    private Chart createNumOfElementsPerSightingBarChart(List<Sighting> inSightings) {
        // Get the data structured
        Map<String, ReportCountWrapper> mapElemNum = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportCountWrapper countWrapper = mapElemNum.get(sighting.getElementName());
            if (countWrapper == null) {
                countWrapper = new ReportCountWrapper(0, 0, 0, 0);
                mapElemNum.put(sighting.getElementName(), countWrapper);
            }
            if (sighting.getNumberOfElements() > 0) {
                countWrapper.total++;
            }
            countWrapper.count = countWrapper.count + sighting.getNumberOfElements();
            if (sighting.getNumberOfElements() > countWrapper.max) {
                countWrapper.max = sighting.getNumberOfElements();
            }
        }
        // Create the series
        ObservableList<BarChart.Series<Number, String>> lstFinalChartDataSeries = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<Number, String>> lstCountForElements = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapElemNum.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            if (chkMax.isSelected()) {
                lstCountForElements.add(new BarChart.Data<Number, String>(mapElemNum.get(key).max, key));
            }
            else
            if (chkAve.isSelected()) {
                lstCountForElements.add(new BarChart.Data<Number, String>(
                        Math.round((mapElemNum.get(key).count/mapElemNum.get(key).total)*100.0)/100.0, key));
            }
        }
        lstFinalChartDataSeries.add(new BarChart.Series<Number, String>(
                "Creatures (" + keys.size() + ") Observations (" + inSightings.size() + ")", 
                lstCountForElements));
        // Setup the axis and chart
        NumberAxis numAxis = new NumberAxis();
        String indicator = "";
        if (chkMax.isSelected()) {
            indicator = "Maximum ";
        }
        else
        if (chkAve.isSelected()) {
            indicator = "Average ";
        }
        UtilsReports.setupNumberAxis(numAxis, indicator + "number of individual Creatures seen per Observation");
        CategoryAxis yAxis = new CategoryAxis();
        BarChart<Number, String> chart = new BarChart<Number, String>(numAxis, yAxis, lstFinalChartDataSeries);
        return chart;
    }
    
    private Chart createSightingPerDayBarChart(List<Sighting> inSightings) {
        // Count how many sightings are on the same day/night (remember that the night streches over two calendar days)
        // Get the data structured
        Map<String, Map<LocalDate, ReportCountWrapper>> mapElemCount = new HashMap<>();
        for (Sighting sighting : inSightings) {
            LocalDateTime currentDate = UtilsTime.getLocalDateTimeFromDate(sighting.getDate());
            Map<LocalDate, ReportCountWrapper> mapCount = mapElemCount.get(sighting.getElementName());
            if (mapCount == null) {
                mapCount = new HashMap<>();
                mapElemCount.put(sighting.getElementName(), mapCount);
            }
            if (ActiveTime.DAY.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
                // Days are easier because the dates simply need to be on the same day
                ReportCountWrapper countWrapper = mapCount.get(currentDate.toLocalDate());
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(currentDate.toLocalDate(), countWrapper);
                }
                countWrapper.total++;
            }
            else
            if (ActiveTime.NIGHT.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
                // Since nights can be over two days, check from 12pm to 12pm of the next day (also handle the first day's half night)
                LocalDate adjustedDate = currentDate.toLocalDate();
                if (currentDate.toLocalTime().isAfter(LocalTime.MIDNIGHT) && currentDate.toLocalTime().isBefore(LocalTime.NOON)) {
                    adjustedDate = currentDate.toLocalDate().minusDays(1);
                }
                ReportCountWrapper countWrapper = mapCount.get(adjustedDate);
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(adjustedDate, countWrapper);
                }
                countWrapper.total++;
            }
            else {
                // For twilight or anything else it is assumed that it will still be on the same calendar day
                ReportCountWrapper countWrapper = mapCount.get(currentDate.toLocalDate());
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(currentDate.toLocalDate(), countWrapper);
                }
                countWrapper.total++;
            }
        }
        // Create the series
        ObservableList<BarChart.Series<Number, String>> lstFinalChartDataSeries = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<Number, String>> lstCountForElements = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapElemCount.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            double maxSightingsPerDay = 0;
            double totalNumberOfSightings = 0;
            double totalNumberOfDaysSighted = 0;
            Map<LocalDate, ReportCountWrapper> mapCount = mapElemCount.get(key);
            for (LocalDate localDate : mapCount.keySet()) {
                double total = mapCount.get(localDate).total;
                if (maxSightingsPerDay < total) {
                    maxSightingsPerDay = total;
                }
                totalNumberOfSightings = totalNumberOfSightings + total;
                totalNumberOfDaysSighted++;
            }
            
            if (chkMax.isSelected()) {
                lstCountForElements.add(new BarChart.Data<Number, String>(maxSightingsPerDay, key));
            }
            else
            if (chkAve.isSelected()) {
                lstCountForElements.add(new BarChart.Data<Number, String>(
                        Math.round((totalNumberOfSightings/totalNumberOfDaysSighted)*100.0)/100.0, key));
            }
        }
        lstFinalChartDataSeries.add(new BarChart.Series<Number, String>(
                "Creatures (" + keys.size() + ") Observations (" + inSightings.size() + ")", 
                lstCountForElements));
        // Setup the axis and chart
        NumberAxis numAxis = new NumberAxis();
        String indicator = "";
        if (chkMax.isSelected()) {
            indicator = "Maximum ";
        }
        else
        if (chkAve.isSelected()) {
            indicator = "Average ";
        }
        UtilsReports.setupNumberAxis(numAxis, indicator + "number of Observations per day-night cycle");
        CategoryAxis yAxis = new CategoryAxis();
        BarChart<Number, String> chart = new BarChart<Number, String>(numAxis, yAxis, lstFinalChartDataSeries);
        return chart;
    }
    
    private Chart createSubsequentSightingBarChart(List<Sighting> inSightings) {
        // Count how many sightings are on the same day/night (remember that the night streches over two calendar days)
        // Get the data structured
        Map<String, Map<LocalDate, ReportCountWrapper>> mapElemCount = new HashMap<>();
        for (Sighting sighting : inSightings) {
            LocalDateTime currentDate = UtilsTime.getLocalDateTimeFromDate(sighting.getDate());
            Map<LocalDate, ReportCountWrapper> mapCount = mapElemCount.get(sighting.getElementName());
            if (mapCount == null) {
                mapCount = new HashMap<>();
                mapElemCount.put(sighting.getElementName(), mapCount);
            }
            if (ActiveTime.DAY.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
                // Days are easier because the dates simply need to be on the same day
                ReportCountWrapper countWrapper = mapCount.get(currentDate.toLocalDate());
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(currentDate.toLocalDate(), countWrapper);
                }
                countWrapper.total++;
            }
            else
            if (ActiveTime.NIGHT.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
                // Since nights can be over two days, check from 12pm to 12pm of the next day (also handle the first day's half night)
                LocalDate adjustedDate = currentDate.toLocalDate();
                if (currentDate.toLocalTime().isAfter(LocalTime.MIDNIGHT) && currentDate.toLocalTime().isBefore(LocalTime.NOON)) {
                    adjustedDate = currentDate.toLocalDate().minusDays(1);
                }
                ReportCountWrapper countWrapper = mapCount.get(adjustedDate);
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(adjustedDate, countWrapper);
                }
                countWrapper.total++;
            }
            else {
                // For twilight or anything else it is assumed that it will still be on the same calendar day
                ReportCountWrapper countWrapper = mapCount.get(currentDate.toLocalDate());
                if (countWrapper == null) {
                    countWrapper = new ReportCountWrapper();
                    mapCount.put(currentDate.toLocalDate(), countWrapper);
                }
                countWrapper.total++;
            }
        }
        // Create the series
        ObservableList<BarChart.Data<Number, String>> lstCountForElements = FXCollections.observableArrayList();
        ObservableList<BarChart.Series<Number, String>> lstFinalChartDataSeries = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapElemCount.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            double maxSightingsPerDay = 0;
            double totalNumberOfDaysSighted = 0;
            double daysWithMultipleSightings = 0;
            Map<LocalDate, ReportCountWrapper> mapCount = mapElemCount.get(key);
            for (LocalDate localDate : mapCount.keySet()) {
                double total = mapCount.get(localDate).total;
                if (maxSightingsPerDay < total) {
                    maxSightingsPerDay = total;
                }
                if (total >= 2) {
                    daysWithMultipleSightings++;
                }
                totalNumberOfDaysSighted++;
            }
            lstCountForElements.add(new BarChart.Data<Number, String>(
                    Math.round((daysWithMultipleSightings/totalNumberOfDaysSighted)*100.0), key));
        }
        lstFinalChartDataSeries.add(new BarChart.Series<Number, String>(
                "Creatures (" + keys.size() + ") Observations (" + inSightings.size() + ")", 
                lstCountForElements));
        // Setup the axis and chart
        NumberAxis numAxis = new NumberAxis(0, 100, 10);
        String indicator = "";
        numAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return Integer.toString(((Double)object).intValue()) + "%";
            }
            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string.substring(0, string.length() - 1));
            }
        });
        UtilsReports.setupNumberAxis(numAxis, indicator + "Chance of subsequent Observations per day-night cycle");
        CategoryAxis yAxis = new CategoryAxis();
        BarChart<Number, String> chart = new BarChart<Number, String>(numAxis, yAxis, lstFinalChartDataSeries);
        return chart;
    }

}
