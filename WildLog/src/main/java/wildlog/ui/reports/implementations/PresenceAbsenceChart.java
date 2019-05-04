package wildlog.ui.reports.implementations;

import java.time.LocalDate;
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.utils.UtilsTime;


public class PresenceAbsenceChart extends AbstractReport<Sighting> {
    private enum ChartType {PRESENCE_ABSENCE_BUBBLE_CHART};
    private ChartType chartType;
    private final ComboBox<String> cmbInterval;
    private final String[] options = new String[] {"1 Day", "3 Days", "7 Days", "14 Days", "28 Days", "91 Days", "1 Year"};
    private CheckBox chkUseTotals;
    private RadioButton rdbPerLocation;
    private RadioButton rdbPerVisit;
    private RadioButton rdbPerElement;


    public PresenceAbsenceChart(List<Sighting> inLstData,JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        super("Presence-Absence Charts", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(9);
        // Pie charts
        ToggleButton btnPieChartSightingCount = new ToggleButton("Presence / Absence");
        btnPieChartSightingCount.setToggleGroup(BUTTON_GROUP);
        btnPieChartSightingCount.setCursor(Cursor.HAND);
        btnPieChartSightingCount.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PRESENCE_ABSENCE_BUBBLE_CHART;
                setupChartDescriptionLabel("<html>This chart shows the presence or absence of a Creature over a time period. "
// TODO: Ek weet nie of dit 'n probleem is of nie, maar hierdie werk op kalender dae, nie day-night-cycles nie...
                        + "<i>(Calendar days are used, not day-night cycles.)</i></html>");
            }
        });
        lstCustomButtons.add(btnPieChartSightingCount);
        // Chart options
        lstCustomButtons.add(new Label("Chart Options:"));
        chkUseTotals = new CheckBox("Use totals");
        chkUseTotals.setCursor(Cursor.HAND);
        chkUseTotals.setSelected(false);
        lstCustomButtons.add(chkUseTotals);
        lstCustomButtons.add(new Label("Interval size:"));
        cmbInterval = new ComboBox<>(FXCollections.observableArrayList(options));
        cmbInterval.setCursor(Cursor.HAND);
        cmbInterval.setVisibleRowCount(7);
        cmbInterval.getSelectionModel().clearSelection();
        cmbInterval.getSelectionModel().select(1);
        lstCustomButtons.add(cmbInterval);
        ToggleGroup toggleGroup = new ToggleGroup();
        rdbPerLocation = new RadioButton("Per Place");
        rdbPerLocation.setToggleGroup(toggleGroup);
        rdbPerLocation.setCursor(Cursor.HAND);
        lstCustomButtons.add(rdbPerLocation);
        rdbPerVisit = new RadioButton("Per Period");
        rdbPerVisit.setToggleGroup(toggleGroup);
        rdbPerVisit.setCursor(Cursor.HAND);
        lstCustomButtons.add(rdbPerVisit);
        rdbPerElement = new RadioButton("Per Creature");
        rdbPerElement.setToggleGroup(toggleGroup);
        rdbPerElement.setCursor(Cursor.HAND);
        rdbPerElement.setSelected(true);
        lstCustomButtons.add(rdbPerElement);
    }

    @Override
    public void createReport(Scene inScene) {
        displayedChart = null;
        if (chartType.equals(ChartType.PRESENCE_ABSENCE_BUBBLE_CHART)) {
            setActiveSubCategoryTitle("Presence-Absence");
            displayedChart = createPresenceAbsenceChart(lstData);
        }
        displayedChart.setBackground(Background.EMPTY);
        inScene.setRoot(displayedChart);
    }
    
    private Chart createPresenceAbsenceChart(List<Sighting> inSightings) {
        // Get the data in the correct structure
        // Get sorted (by date) Sightings list
        Collections.sort(inSightings);
        // Construct the final list of series that needs to be displayed
        Map<String, Map<Integer, ReportDataWrapper>> mapGroupedData = new HashMap<>();
        LocalDate intervalDate = UtilsTime.getLocalDateFromDate(inSightings.get(0).getDate());
        int intervalCount = 0;
        for (Sighting sighting : inSightings) {
            String key = generateGroupingKey(sighting);
            Map<Integer, ReportDataWrapper> mapIntervalData = mapGroupedData.get(key);
            if (mapIntervalData == null) {
                mapIntervalData = new HashMap<>();
                mapGroupedData.put(key, mapIntervalData);
                // Add the current interval
                mapIntervalData.put(intervalCount, new ReportDataWrapper(key, Integer.toString(intervalCount), 0));
            }
            LocalDate sightingDate = UtilsTime.getLocalDateFromDate(sighting.getDate());
            // Move to the next interval
            while (!isInInterval(intervalDate, sightingDate)) {
                // Add empty intervals (if any)
                intervalDate = nextInterval(intervalDate);
                intervalCount++;
                mapIntervalData.put(intervalCount, new ReportDataWrapper(key, Integer.toString(intervalCount), 0));
            }
            ReportDataWrapper dataWrapper = mapIntervalData.get(intervalCount);
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(key, Integer.toString(intervalCount), 0);
                mapIntervalData.put(intervalCount, dataWrapper);
            }
            dataWrapper.increaseCount();
        }
        // Get the final list of series
        ObservableList<StackedBarChart.Series<String, Number>> lstChartSeries = FXCollections.observableArrayList();
        List<String> lstKeys = new ArrayList<>(mapGroupedData.keySet());
        Collections.sort(lstKeys);
        // Fill the empty interval gaps in the data series for each grouping
        for (String key : lstKeys) {
            Map<Integer, ReportDataWrapper> mapIntervalData = mapGroupedData.get(key);
            for (int i = 0; i <= intervalCount; i++) {
                ReportDataWrapper dataWrapper = mapIntervalData.get(i);
                if (dataWrapper == null) {
                    dataWrapper = new ReportDataWrapper(key, Integer.toString(i), 0);
                    mapIntervalData.put(i, dataWrapper);
                }
            }
        }
        // Build the final list of series
        for (String key : lstKeys) {
            Map<Integer, ReportDataWrapper> mapIntervalData = mapGroupedData.get(key);
            // Turn the map into a list data points for this series
            ObservableList<StackedBarChart.Data<String, Number>> lstChartData = FXCollections.observableArrayList();
            List<Integer> lstIntervalKeys = new ArrayList<>(mapIntervalData.keySet());
            Collections.sort(lstIntervalKeys);
            for (Integer intervalKey : lstIntervalKeys) {
                ReportDataWrapper dataWrapper = mapIntervalData.get(intervalKey);
                int size;
                if (chkUseTotals.isSelected()) {
                    size = dataWrapper.count;
                }
                else {
                    if (dataWrapper.count > 0) {
                        size = 1;
                    }
                    else {
                        size = 0;
                    }
                }
                lstChartData.add(new StackedBarChart.Data<>(Integer.toString(intervalKey), size, dataWrapper.key));
            }
            // Add the series
            lstChartSeries.add(new StackedBarChart.Series<>(key, lstChartData));
        }
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, intervalCount, true);
        StackedBarChart<String, Number> chart = UtilsReports.createStackedBarChartWithStyleIndexBiggerThanEight(catAxis, numAxis, lstChartSeries);
        chart.getStyleClass().add("wl-stacked-bar-30-color");
        chart.setTitle("Presence-Absence per " + cmbInterval.getSelectionModel().getSelectedItem());
        UtilsReports.setupChartTooltips(chart, true, false, true);
        return chart;
    }
    
    private String generateGroupingKey(Sighting inSighting) {
        if (rdbPerLocation.isSelected()) {
            return inSighting.getCachedLocationName();
        }
        else
        if (rdbPerVisit.isSelected()) {
            return inSighting.getCachedVisitName();
        }
        else
        if (rdbPerElement.isSelected()) {
            return inSighting.getCachedElementName(reportsBaseDialog.getOptionName());
        }
        return null;
    }
    
    private boolean isInInterval(LocalDate inIntervalStartDate, LocalDate inSightingDate) {
        return !inSightingDate.isBefore(inIntervalStartDate) && inSightingDate.isBefore(nextInterval(inIntervalStartDate));
    }
    
    private LocalDate nextInterval(LocalDate inIntervalDate) {
        if (cmbInterval.getSelectionModel().isSelected(0)) {
            return inIntervalDate.plusDays(1);
        }
        else
        if (cmbInterval.getSelectionModel().isSelected(1)) {
            return inIntervalDate.plusDays(3);
        }
        else
        if (cmbInterval.getSelectionModel().isSelected(2)) {
            return inIntervalDate.plusDays(7);
        }
        else
        if (cmbInterval.getSelectionModel().isSelected(3)) {
            return inIntervalDate.plusDays(14);
        }
        else
        if (cmbInterval.getSelectionModel().isSelected(4)) {
            return inIntervalDate.plusDays(28);
        }
        else
        if (cmbInterval.getSelectionModel().isSelected(5)) {
            return inIntervalDate.plusDays(91);
        }
        else
        if (cmbInterval.getSelectionModel().isSelected(6)) {
            return inIntervalDate.plusYears(1);
        }
        return null;
    }
    
}
