package wildlog.ui.reports.implementations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.ui.utils.UtilsTime;


public class EventTimelineChart extends AbstractReport<Sighting> {
    private enum ChartType {TIMELINE_FOR_ALL, TIMELINE_PER_ELEMENT};
    private ChartType chartType = ChartType.TIMELINE_FOR_ALL;
    private Chart displayedChart;
    private ComboBox<String> cmbIntervalSize;
    private final String[] options = new String[] {"1 hour", "3 hours", "6 hours", "12 hours", "24 hours"};

    
    public EventTimelineChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Timeline Reports (Date Range)", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(5);
        // Timeline for all
        ToggleButton btnLineChart = new ToggleButton("Timeline for All Observations (Line)");
        btnLineChart.setToggleGroup(BUTTON_GROUP);
        btnLineChart.setCursor(Cursor.HAND);
        btnLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.TIMELINE_FOR_ALL;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations over time during a spesified time interval.</html>");
            }
        });
        lstCustomButtons.add(btnLineChart);
        // Timeline per element
        ToggleButton btnStackedLineChart = new ToggleButton("Timeline per Creature (Line)");
        btnStackedLineChart.setToggleGroup(BUTTON_GROUP);
        btnStackedLineChart.setCursor(Cursor.HAND);
        btnStackedLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.TIMELINE_PER_ELEMENT;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations per Creature over time during a spesified time interval.</html>");
            }
        });
        lstCustomButtons.add(btnStackedLineChart);
        // Time interval size
        lstCustomButtons.add(new Label("Timeline interval size:"));
        cmbIntervalSize = new ComboBox<>(FXCollections.observableArrayList(options));
        cmbIntervalSize.setCursor(Cursor.HAND);
        cmbIntervalSize.setVisibleRowCount(10);
        cmbIntervalSize.getSelectionModel().clearSelection();
        cmbIntervalSize.getSelectionModel().select(4);
        cmbIntervalSize.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                if (!cmbIntervalSize.getSelectionModel().isEmpty()) {
                    if (chartType == ChartType.TIMELINE_FOR_ALL) {
                        setupChartDescriptionLabel("<html>This chart shows the number of Observations over time during a spesified time interval.</html>");
                    }
                    else
                    if (chartType == ChartType.TIMELINE_PER_ELEMENT) {
                        setupChartDescriptionLabel("<html>This chart shows the number of Observations per Creature over time during a spesified time interval.</html>");
                    }
                }
            }
        });
        lstCustomButtons.add(cmbIntervalSize);
    }
    
    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.TIMELINE_FOR_ALL)) {
                    setActiveSubCategoryTitle("Timeline for All Observations (Line)");
                    displayedChart = createTimelineForAllChart(lstData, true);
                }
                else
                if (chartType.equals(ChartType.TIMELINE_PER_ELEMENT)) {
                    setActiveSubCategoryTitle("Timeline per Creature (Line)");
                    displayedChart = createTimelineForAllChart(lstData, false);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }

// FIXME: Baie stadig vir groot datasets. (Dalk ook tedoen met as daar 'n paar series is wat oor 'n groot x-axis gaan, basies te veel datums om te render?)
    private Chart createTimelineForAllChart(List<Sighting> inSightings, boolean inIsForAllObservations) {
        // Get sorted (by date) Sightings list
        Collections.sort(inSightings);
        AreaChart<String, Number> chart;
        if (!inSightings.isEmpty()) {
            LocalDateTime firstDate = UtilsTime.getLocalDateTimeFromDate(inSightings.get(0).getDate());
            LocalDateTime lastDate = UtilsTime.getLocalDateTimeFromDate(inSightings.get(inSightings.size() - 1).getDate());
            // Get the data in the correct structure
            Map<String, Map<String, ReportDataWrapper>> mapDataPerElement = new HashMap<>();
            for (Sighting sighting : inSightings) {
                String key;
                if (inIsForAllObservations) {
                    key = "All Observations";
                }
                else {
                    key = sighting.getElementName();
                }
                Map<String, ReportDataWrapper> mapSpeciesData = mapDataPerElement.get(key);
                if (mapSpeciesData == null) {
                    mapSpeciesData = new HashMap<>();
                    mapDataPerElement.put(key, mapSpeciesData);
                }
                String timeString = getTimeAsString(UtilsTime.getLocalDateTimeFromDate(sighting.getDate()));
                ReportDataWrapper dataWrapper = mapSpeciesData.get(timeString);
                if (dataWrapper == null) {
                    dataWrapper = new ReportDataWrapper(null, null, 0);
                    mapSpeciesData.put(timeString, dataWrapper);
                }
                dataWrapper.increaseCount();
            }
            // Populate the structured data on the chart
            ObservableList<AreaChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
            List<String> keys = new ArrayList<>(mapDataPerElement.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                int entryCount = 0;
                ObservableList<AreaChart.Data<String, Number>> lstData = FXCollections.observableArrayList();
                setupEmtyIntervals(lstData, firstDate, lastDate, key);
                Map<String, ReportDataWrapper> mapSpeciesData = mapDataPerElement.get(key);
                for (AreaChart.Data<String, Number> data : lstData) {
                    ReportDataWrapper dataWrapper = mapSpeciesData.get(data.getXValue());
                    if (dataWrapper != null) {
                        data.setYValue(dataWrapper.count);
                        entryCount++;
                    }
                }
                chartData.add(new AreaChart.Series<String, Number>(key + " (" + entryCount + ")", lstData));
            }
            // Setup chart
            NumberAxis numAxis = new NumberAxis();
            UtilsReports.setupNumberAxis(numAxis, false);
            CategoryAxis catAxis = new CategoryAxis();
            catAxis.setCategories(getAllTimesAsList(firstDate, lastDate));
            catAxis.setTickLabelRotation(90);
            catAxis.setTickLabelFont(Font.font(12));
            chart = new AreaChart<String, Number>(catAxis, numAxis, chartData);
        }
        else {
            chart = new AreaChart<String, Number>(new CategoryAxis(), new NumberAxis(), FXCollections.observableArrayList());
        }
        chart.getStyleClass().add("wl-line-30-color");
        if (inIsForAllObservations) {
            chart.setLegendVisible(false);
        }
        else {
            chart.setLegendVisible(true);
        }
        chart.setTitle("Number of Observations during Time Interval");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }
    
    private ObservableList<String> getAllTimesAsList(LocalDateTime inStartDate, LocalDateTime inEndDate) {
        Set<String> timeCategories = new LinkedHashSet<>();
        LocalDateTime loopDate = inStartDate;
        while (!loopDate.isAfter(inEndDate)) {
            timeCategories.add(getTimeAsString(loopDate));
            loopDate = loopDate.plusHours(1);
        }
        return FXCollections.observableList(new ArrayList<String>(timeCategories));
    }

    private void setupEmtyIntervals(ObservableList<XYChart.Data<String, Number>> lstElementData, LocalDateTime inStartDate, LocalDateTime inEndDate, String inSeriesName) {
        List<String> lstTimes = getAllTimesAsList(inStartDate, inEndDate);
        for (String time : lstTimes) {
            lstElementData.add(new AreaChart.Data<String, Number>(time, 0, inSeriesName));
        }
    }
    
    private String getTimeAsString(LocalDateTime inTime) {
        int hoursDevider = 24;
        if (cmbIntervalSize.getSelectionModel().isSelected(0)) {
            // 1 hour
            hoursDevider = 24;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(1)) {
            // 3 hours
            hoursDevider = 8;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(2)) {
            // 6 hours
            hoursDevider = 4;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(3)) {
            // 12 hours
            hoursDevider = 2;
        }
        else
        if (cmbIntervalSize.getSelectionModel().isSelected(4)) {
            // 24 hours
            hoursDevider = 1;
        }
        // Get start time
        StringBuilder timeString = new StringBuilder(30);
        int temp = (inTime.getHour() / (24 / hoursDevider)) * (24 / hoursDevider);
        if (temp < 10) {
            timeString.append("0");
        }
        timeString.append(temp);
        // Get end time
        timeString.append(":00-");
        temp = ((inTime.getHour() + (24 / hoursDevider)) / (24 / hoursDevider)) * (24 / hoursDevider) - 1;
        if (temp < 10) {
            timeString.append("0");
        }
        timeString.append(temp);
        timeString.append(":59 (");
        timeString.append(UtilsTime.WL_DATE_FORMATTER_FOR_FILES.format(inTime));
        timeString.append(")");
        // Return result
        return timeString.toString();
    }
    
}
