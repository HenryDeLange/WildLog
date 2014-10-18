package wildlog.ui.reports.implementations;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Background;
import javafx.util.StringConverter;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTime;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportCountWrapper;
import wildlog.ui.utils.UtilsTime;


public class SightingStatsChart extends AbstractReport<Sighting> {
    private enum ChartType {NUMBER_PER_SIGHTING_CHART, SUBSEQUENT_CHART, SIGHTINGS_PER_DAY_CHART};
    private ChartType chartType = ChartType.NUMBER_PER_SIGHTING_CHART;
    private Chart displayedChart;
    private boolean maximum;
    private boolean average;
    
      
    
    public SightingStatsChart() {
        super("Observation Statistics", "<html>This collection of charts focus on statistics of the Observations.</html>");
        lstCustomButtons = new ArrayList<>(6);
        // Bar chart
        JButton btnElementPerSightingBarChart = new JButton("Number of Individuals");
        btnElementPerSightingBarChart.setFocusPainted(false);
        btnElementPerSightingBarChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnElementPerSightingBarChart.setMargin(new Insets(2, 4, 2, 4));
        btnElementPerSightingBarChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.NUMBER_PER_SIGHTING_CHART;
                if (displayedChart != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            displayedChart.getScene().setRoot(createReport());
                        }
                    });
                }
            }
        });
        lstCustomButtons.add(btnElementPerSightingBarChart);
        // Bar chart
        JButton btnSightingsPerDayBarChart = new JButton("Observations per Day");
        btnSightingsPerDayBarChart.setFocusPainted(false);
        btnSightingsPerDayBarChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSightingsPerDayBarChart.setMargin(new Insets(2, 4, 2, 4));
        btnSightingsPerDayBarChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.SIGHTINGS_PER_DAY_CHART;
                if (displayedChart != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            displayedChart.getScene().setRoot(createReport());
                        }
                    });
                }
            }
        });
        lstCustomButtons.add(btnSightingsPerDayBarChart);
        // Bar chart
        JButton btnSightingChanceBarChart = new JButton("Subsequent Observations");
        btnSightingChanceBarChart.setFocusPainted(false);
        btnSightingChanceBarChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSightingChanceBarChart.setMargin(new Insets(2, 4, 2, 4));
        btnSightingChanceBarChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.SUBSEQUENT_CHART;
                if (displayedChart != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            displayedChart.getScene().setRoot(createReport());
                        }
                    });
                }
            }
        });
        lstCustomButtons.add(btnSightingChanceBarChart);
        // Average
        JRadioButton chkAve = new JRadioButton("Average");
        chkAve.setFocusPainted(false);
        chkAve.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkAve.setMargin(new Insets(2, 4, 2, 4));
        chkAve.setSelected(false);
        chkAve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maximum = false;
                average = true;
                if (displayedChart != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            displayedChart.getScene().setRoot(createReport());
                        }
                    });
                }
            }
        });
        lstCustomButtons.add(chkAve);
        // Maximum
        JRadioButton chkMax = new JRadioButton("Maximum");
        chkMax.setFocusPainted(false);
        chkMax.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkMax.setMargin(new Insets(2, 4, 2, 4));
        chkMax.setSelected(false);
        chkMax.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maximum = true;
                average = false;
                if (displayedChart != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            displayedChart.getScene().setRoot(createReport());
                        }
                    });
                }
            }
        });
        lstCustomButtons.add(chkMax);
// TODO: Sit dalk nog 'n "mode" (nee dis nie regtig mode waarvoor ek soek nie) ook by, dit sal dalk resultate gee wat meer sin maak vir goed soos buffels wat normaalweg net 1-5 is, maar soms 200+
// TODO: SO verander die radiobuttons dalk in 'n combobox met opsies Max, Average (Mean), Most Frequent Value (Mode) en Significt Frequency Distribution (The more time a value is present the more relevant it becomes)
        // Max and ave radio button group
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(chkMax);
        buttonGroup.add(chkAve);
        chkAve.setSelected(true);
        average = true;
    }

    @Override
    public Chart createReport() {
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
        return displayedChart;
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
            if (maximum) {
                lstCountForElements.add(new BarChart.Data<Number, String>(mapElemNum.get(key).max, key));
            }
            else
            if (average) {
                lstCountForElements.add(new BarChart.Data<Number, String>(
                        Math.round((mapElemNum.get(key).count/mapElemNum.get(key).total)*100.0)/100.0, key));
            }
        }
        lstFinalChartDataSeries.add(new BarChart.Series<Number, String>(
                "Creatures (" + keys.size() + ") Observations (" + inSightings.size() + ")", 
                lstCountForElements));
        // Setup the axis and chart
        NumberAxis xAxis = new NumberAxis();
        String indicator = "";
        if (maximum) {
            indicator = "Maximum ";
        }
        else
        if (average) {
            indicator = "Average ";
        }
        xAxis.setLabel(indicator + "number of individual Creatures seen per Observation");
        xAxis.setAutoRanging(true);
        CategoryAxis yAxis = new CategoryAxis();
        BarChart<Number, String> chart = new BarChart<Number, String>(xAxis, yAxis, lstFinalChartDataSeries);
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
            
            if (maximum) {
                lstCountForElements.add(new BarChart.Data<Number, String>(maxSightingsPerDay, key));
            }
            else
            if (average) {
                lstCountForElements.add(new BarChart.Data<Number, String>(
                        Math.round((totalNumberOfSightings/totalNumberOfDaysSighted)*100.0)/100.0, key));
            }
        }
        lstFinalChartDataSeries.add(new BarChart.Series<Number, String>(
                "Creatures (" + keys.size() + ") Observations (" + inSightings.size() + ")", 
                lstCountForElements));
        // Setup the axis and chart
        NumberAxis xAxis = new NumberAxis();
        String indicator = "";
        if (maximum) {
            indicator = "Maximum ";
        }
        else
        if (average) {
            indicator = "Average ";
        }
        xAxis.setLabel(indicator + "number of Observations per day-night cycle");
        xAxis.setAutoRanging(true);
        CategoryAxis yAxis = new CategoryAxis();
        BarChart<Number, String> chart = new BarChart<Number, String>(xAxis, yAxis, lstFinalChartDataSeries);
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
        NumberAxis xAxis = new NumberAxis(0, 100, 10);
        String indicator = "";
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return Integer.toString(((Double)object).intValue()) + "%";
            }
            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string.substring(0, string.length() - 1));
            }
        });
        xAxis.setLabel(indicator + "Chance of subsequent Observations per day-night cycle");
        CategoryAxis yAxis = new CategoryAxis();
        BarChart<Number, String> chart = new BarChart<Number, String>(xAxis, yAxis, lstFinalChartDataSeries);
        return chart;
    }

}
