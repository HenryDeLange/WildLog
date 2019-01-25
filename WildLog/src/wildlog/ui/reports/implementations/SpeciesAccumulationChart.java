package wildlog.ui.reports.implementations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTime;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.ui.utils.UtilsTime;


public class SpeciesAccumulationChart extends AbstractReport<Sighting> {
    private enum ChartType {ACCUMULATION_LINE_CHART, ACCUMULATION_SIGHTING_LINE_CHART, ACCUMULATION_SIGHTING_ELEMENT_LINE_CHART, 
                            DAILY_CREATURE_LINE_CHART, DAILY_OBSERVATION_LINE_CHART, ACC_VISIT_ELEMENT_CHART, ACC_VISIT_SIGHTING_CHART};
    private ChartType chartType = ChartType.ACCUMULATION_LINE_CHART;
    private Chart displayedChart;
    private final CheckBox chkShowDayOrNight;

    
    public SpeciesAccumulationChart(List<Sighting> inLstData, JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        super("Accumulation Rate Reports", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(9);
        // Line charts
        ToggleButton btnCreatureAccumulationLineChart = new ToggleButton("Creature Accumulation");
        btnCreatureAccumulationLineChart.setToggleGroup(BUTTON_GROUP);
        btnCreatureAccumulationLineChart.setCursor(Cursor.HAND);
        btnCreatureAccumulationLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.ACCUMULATION_LINE_CHART;
                setupChartDescriptionLabel("<html>This chart illustrates the rate at which new Creatures were recorded over time.</html>");
            }
        });
        lstCustomButtons.add(btnCreatureAccumulationLineChart);
        ToggleButton btnSightingAccumulationLineChart = new ToggleButton("Observation Accumulation");
        btnSightingAccumulationLineChart.setToggleGroup(BUTTON_GROUP);
        btnSightingAccumulationLineChart.setCursor(Cursor.HAND);
        btnSightingAccumulationLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.ACCUMULATION_SIGHTING_LINE_CHART;
                setupChartDescriptionLabel("<html>This chart illustrates the rate at which new Observations were recorded over time.</html>");
            }
        });
        lstCustomButtons.add(btnSightingAccumulationLineChart);
        ToggleButton btnSightingElementAccumulationLineChart = new ToggleButton("Observations per Creature");
        btnSightingElementAccumulationLineChart.setToggleGroup(BUTTON_GROUP);
        btnSightingElementAccumulationLineChart.setCursor(Cursor.HAND);
        btnSightingElementAccumulationLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.ACCUMULATION_SIGHTING_ELEMENT_LINE_CHART;
                setupChartDescriptionLabel("<html>This chart illustrates the rate at which new Observations were recorded over time for each Creature.</html>");
            }
        });
        lstCustomButtons.add(btnSightingElementAccumulationLineChart);
        ToggleButton btnAccElementPerVisitLineChart = new ToggleButton("Creatures per Period");
        btnAccElementPerVisitLineChart.setToggleGroup(BUTTON_GROUP);
        btnAccElementPerVisitLineChart.setCursor(Cursor.HAND);
        btnAccElementPerVisitLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.ACC_VISIT_ELEMENT_CHART;
                setupChartDescriptionLabel("<html>This chart illustrates the number of Creatures recorded over time per Period.</html>");
            }
        });
        lstCustomButtons.add(btnAccElementPerVisitLineChart);
        ToggleButton btnAccSightingPerVisitLineChart = new ToggleButton("Observations per Period");
        btnAccSightingPerVisitLineChart.setToggleGroup(BUTTON_GROUP);
        btnAccSightingPerVisitLineChart.setCursor(Cursor.HAND);
        btnAccSightingPerVisitLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.ACC_VISIT_SIGHTING_CHART;
                setupChartDescriptionLabel("<html>This chart illustrates the number of Observations recorded over time per Period.</html>");
            }
        });
        lstCustomButtons.add(btnAccSightingPerVisitLineChart);
        ToggleButton btnDailyObsLineChart = new ToggleButton("Daily Observation Count");
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
        ToggleButton btnDailyLineChart = new ToggleButton("Daily Creature Count");
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
        chkShowDayOrNight = new CheckBox("Show Day, Night or Twilight");
        chkShowDayOrNight.setCursor(Cursor.HAND);
        chkShowDayOrNight.setSelected(false);
        lstCustomButtons.add(chkShowDayOrNight);
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.ACCUMULATION_LINE_CHART)) {
                    chkShowDayOrNight.setDisable(true);
                    setActiveSubCategoryTitle("Creature Accumulation");
                    displayedChart = createAccumulationReport(lstData);
                }
                else
                if (chartType.equals(ChartType.ACCUMULATION_SIGHTING_LINE_CHART)) {
                    chkShowDayOrNight.setDisable(true);
                    setActiveSubCategoryTitle("Observation Accumulation");
                    displayedChart = createAccumulationSightingReport(lstData, true);
                }
                else
                if (chartType.equals(ChartType.ACCUMULATION_SIGHTING_ELEMENT_LINE_CHART)) {
                    chkShowDayOrNight.setDisable(true);
                    setActiveSubCategoryTitle("Observations per Creature");
                    displayedChart = createAccumulationSightingReport(lstData, false);
                }
                else
                if (chartType.equals(ChartType.ACC_VISIT_ELEMENT_CHART)) {
                    chkShowDayOrNight.setDisable(true);
                    setActiveSubCategoryTitle("Creatures per Period");
                    displayedChart = createAccumulationVisitReport(lstData, false);
                }
                else
                if (chartType.equals(ChartType.ACC_VISIT_SIGHTING_CHART)) {
                    chkShowDayOrNight.setDisable(true);
                    setActiveSubCategoryTitle("Observations per Period");
                    displayedChart = createAccumulationVisitReport(lstData, true);
                }
                else
                if (chartType.equals(ChartType.DAILY_CREATURE_LINE_CHART)) {
                    chkShowDayOrNight.setDisable(false);
                    setActiveSubCategoryTitle("Daily Creature Count");
                    displayedChart = createDailyReport(lstData, false);
                }
                else
                if (chartType.equals(ChartType.DAILY_OBSERVATION_LINE_CHART)) {
                    chkShowDayOrNight.setDisable(false);
                    setActiveSubCategoryTitle("Daily Observation Count");
                    displayedChart = createDailyReport(lstData, true);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }
    
    private Chart createAccumulationReport(List<Sighting> inSightings) {
        // Get the fisrt and last Visit date
        Map<String, DateWrapper> mapVisitDates = new HashMap<>();
        LocalDate startDate = null; 
        LocalDate endDate = null;
        for (Sighting sighting : inSightings) {
            DateWrapper visitDates = mapVisitDates.get(sighting.getVisitName());
            if (visitDates == null) {
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitName(), Visit.class);
                visitDates = new DateWrapper();
                visitDates.startDate = UtilsTime.getLocalDateFromDate(visit.getStartDate());
                visitDates.endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                mapVisitDates.put(sighting.getVisitName(), visitDates);
            }
            // Get Start Date
            // If no date was found then use the Sighting's date
            LocalDate possibleDate = visitDates.startDate;
            if (possibleDate == null) {
                possibleDate = UtilsTime.getLocalDateFromDate(sighting.getDate());
            }
            // Kies die vroegste datum
            if (startDate == null || possibleDate.isBefore(startDate)) {
                startDate = possibleDate;
            }
            // Get End Date
            // If no date was found then use the Sighting's date
            possibleDate = visitDates.endDate;
            if (possibleDate == null) {
                possibleDate = UtilsTime.getLocalDateFromDate(sighting.getDate());
            }
            // Kies die laaste datum
            if (endDate == null || possibleDate.isAfter(endDate)) {
                endDate = possibleDate;
            }
        }
        // Get the data in the correct structure
        // Get sorted (by date) Sightings list
        Collections.sort(inSightings);
        Map<String, LocalDateTime> mapAccumulation = new LinkedHashMap<>();
        for (Sighting sighting : inSightings) {
            mapAccumulation.putIfAbsent(sighting.getElementName(reportsBaseDialog.getOptionName()), UtilsTime.getLocalDateTimeFromDate(sighting.getDate()));
        }
        // Construct the final list of series that needs to be displayed
        int counter = 0;
        ObservableList<AreaChart.Data<Number, Number>> lstChartData = FXCollections.observableArrayList();
        for (String elementName : mapAccumulation.keySet()) {
            long sightingTime = UtilsTime.getDateFromLocalDateTime(mapAccumulation.get(elementName)).getTime();
            lstChartData.add(new AreaChart.Data<>(sightingTime, ++counter, elementName));
        }
        // Add an entry to the front (and back) to make the first (and last) entry more visible
        if (endDate != null) {
            lstChartData.add(new AreaChart.Data<>(UtilsTime.getDateFromLocalDateTime(endDate.atTime(LocalTime.MAX)).getTime(), counter, "END"));
        }
        if (startDate != null) {
            lstChartData.add(0, new AreaChart.Data<>(UtilsTime.getDateFromLocalDateTime(startDate.atTime(LocalTime.MIN)).getTime(), 0, "START"));
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
    
    private Chart createAccumulationSightingReport(List<Sighting> inSightings, boolean inIsForAllCreatures) {
        // Get the fisrt and last Visit date
        Map<String, DateWrapper> mapVisitDates = new HashMap<>();
        LocalDate startDate = null; 
        LocalDate endDate = null;
        for (Sighting sighting : inSightings) {
            DateWrapper visitDates = mapVisitDates.get(sighting.getVisitName());
            if (visitDates == null) {
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitName(), Visit.class);
                visitDates = new DateWrapper();
                visitDates.startDate = UtilsTime.getLocalDateFromDate(visit.getStartDate());
                visitDates.endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                mapVisitDates.put(sighting.getVisitName(), visitDates);
            }
            // Get Start Date
            // If no date was found then use the Sighting's date
            LocalDate possibleDate = visitDates.startDate;
            if (possibleDate == null) {
                possibleDate = UtilsTime.getLocalDateFromDate(sighting.getDate());
            }
            // Kies die vroegste datum
            if (startDate == null || possibleDate.isBefore(startDate)) {
                startDate = possibleDate;
            }
            // Get End Date
            // If no date was found then use the Sighting's date
            possibleDate = visitDates.endDate;
            if (possibleDate == null) {
                possibleDate = UtilsTime.getLocalDateFromDate(sighting.getDate());
            }
            // Kies die laaste datum
            if (endDate == null || possibleDate.isAfter(endDate)) {
                endDate = possibleDate;
            }
        }
        // Get the data in the correct structure
        // Get sorted (by date) Sightings list
        Collections.sort(inSightings);
        // Construct the final list of series that needs to be displayed
        Map<String, ObservableList<AreaChart.Data<Number, Number>>> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            String key;
            if (inIsForAllCreatures) {
                key = "All Creatures";
            }
            else {
                key = sighting.getElementName(reportsBaseDialog.getOptionName());
            }
            ObservableList<AreaChart.Data<Number, Number>> lstChartData = mapGroupedData.get(key);
            if (lstChartData == null) {
                lstChartData = FXCollections.observableArrayList();
                mapGroupedData.put(key, lstChartData);
            }
            lstChartData.add(new AreaChart.Data<>(sighting.getDate().getTime(), lstChartData.size() + 1, 
                    sighting.getElementName(reportsBaseDialog.getOptionName())));
        }
        // Get the final list of series
        ObservableList<AreaChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
        List<String> lstKeys = new ArrayList<>(mapGroupedData.keySet());
        Collections.sort(lstKeys);
        for (String key : lstKeys) {
            ObservableList<AreaChart.Data<Number, Number>> lstChartData = mapGroupedData.get(key);
            // Add an entry to the front (and back) to make the first (and last) entry more visible
            if (endDate != null) {
                lstChartData.add(new AreaChart.Data<>(UtilsTime.getDateFromLocalDateTime(endDate.atTime(LocalTime.MAX)).getTime(), lstChartData.size(), "END"));
            }
            if (startDate != null) {
                lstChartData.add(0, new AreaChart.Data<>(UtilsTime.getDateFromLocalDateTime(startDate.atTime(LocalTime.MIN)).getTime(), 0, "START"));
            }
            // Add the series
            chartData.add(new AreaChart.Series<>(key + " (" + lstChartData.size() + ")", lstChartData));
        }
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
        chart.setLegendVisible(!inIsForAllCreatures);
        if (inIsForAllCreatures) {
            chart.setTitle("Number of Observations over Time");
        }
        else {
            chart.setTitle("Number of Observations per Creature over Time");
        }
        UtilsReports.setupChartTooltips(chart, true, true);
        return chart;
    }
    
// FIXME: Baie stadig vir groot datasets.
//        Die probleem is dat daar 'n data punt vir elke dag moet wees. Dan raak die chart stadiger hou verder die begin en eind datum uit mekaar is...
//        Dit raak dan ook erger hoe meer series mens het want dan raak dit net meer ne meer data punte
//        OPLOSSING? Iets soos 'n (stacked vir multi series) bar chart sal seker beter werk en selfs maklikker lees?
    private Chart createDailyReport(List<Sighting> inSightings, boolean inIsForAllObservations) {
        Map<String, Map<String, ReportDataWrapper>> mapSeries = new HashMap<>();
        // Get sorted (by date) Sightings list
        Collections.sort(inSightings);
        // Setup the initial maps that will be used for the chart data
        for (Sighting sighting : inSightings) {
            String key;
            if (inIsForAllObservations) {
                if (chkShowDayOrNight.isSelected()) {
                    key = ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()).toString();
                }
                else {
                    key = "All Observations";
                }
            }
            else {
                key = sighting.getElementName(reportsBaseDialog.getOptionName());
                if (chkShowDayOrNight.isSelected()) {
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
            int numberOfDays = (int) Duration.between(firstDate.atTime(LocalTime.MIN), lastDate.atTime(LocalTime.MIN)).toDays();
            ObservableList<AreaChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
            List<String> keys;
            if (inIsForAllObservations && chkShowDayOrNight.isSelected()) {
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
        if (inIsForAllObservations && chkShowDayOrNight.isSelected()) {
            chart.getStylesheets().add("wildlog/ui/reports/chart/styling/ChartsDayNightTwilight.css");
            chart.getStyleClass().add("wl-line-day-night-color");
        }
        else {
            chart.getStyleClass().add("wl-line-30-color");
        }
        if (inIsForAllObservations) {
            if (chkShowDayOrNight.isSelected()) {
                chart.setTitle("Number of Observations per Day, Night and Twilight");
                chart.setLegendVisible(true);
            }
            else {
                chart.setTitle("Number of Observations per calendar day");
                chart.setLegendVisible(false);
            }
        }
        else {
            if (chkShowDayOrNight.isSelected()) {
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

    private Chart createAccumulationVisitReport(List<Sighting> inSightings, boolean inIsForObservations) {
        // Get the fisrt and last Visit date
        Map<String, DateWrapper> mapVisitDates = new HashMap<>();
        TemporalAmount dateDiffRange = null;
        for (Sighting sighting : inSightings) {
            DateWrapper visitDates = mapVisitDates.get(sighting.getVisitName());
            if (visitDates == null) {
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitName(), Visit.class);
                visitDates = new DateWrapper();
                visitDates.startDate = UtilsTime.getLocalDateFromDate(visit.getStartDate());
                visitDates.endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                mapVisitDates.put(sighting.getVisitName(), visitDates);
            }
            LocalDate sightingDate = UtilsTime.getLocalDateFromDate(sighting.getDate());
            // Get Start Date
            if (visitDates.startDate == null) {
                visitDates.startDate = sightingDate;
            }
            if (visitDates.startDate.isAfter(sightingDate)) {
                visitDates.startDate = sightingDate;
            }
            // Get End Date
            if (visitDates.endDate == null) {
                visitDates.endDate = sightingDate;
            }
            if (visitDates.endDate.isBefore(sightingDate)) {
                visitDates.endDate = sightingDate;
            }
            // check longest duration
            TemporalAmount tempDifference = Duration.between(visitDates.startDate.atTime(LocalTime.MIN), visitDates.endDate.atTime(LocalTime.MAX));
            if (dateDiffRange == null || dateDiffRange.get(ChronoUnit.SECONDS) < tempDifference.get(ChronoUnit.SECONDS)) {
                dateDiffRange = tempDifference;
            }
        }
        // Get the data in the correct structure
        // Get sorted (by date) Sightings list
        Collections.sort(inSightings);
        // Construct the final list of series that needs to be displayed
        Map<String, ObservableList<AreaChart.Data<Number, Number>>> mapGroupedData = new HashMap<>();
        Map<String, Set<String>> mapVisitElementCount = new HashMap<>(mapVisitDates.size());
        for (Sighting sighting : inSightings) {
            ObservableList<AreaChart.Data<Number, Number>> lstChartData = mapGroupedData.get(sighting.getVisitName());
            if (lstChartData == null) {
                lstChartData = FXCollections.observableArrayList();
                mapGroupedData.put(sighting.getVisitName(), lstChartData);
            }
            TemporalAmount tempDifference = Duration.between(mapVisitDates.get(sighting.getVisitName()).startDate.atTime(LocalTime.MIN), 
                    UtilsTime.getLocalDateTimeFromDate(sighting.getDate()));
            if (inIsForObservations) {
                lstChartData.add(new AreaChart.Data<>(tempDifference.get(ChronoUnit.SECONDS), lstChartData.size() + 1, 
                    sighting.getElementName(reportsBaseDialog.getOptionName()) + " [" + sighting.getVisitName() + "]"));
            }
            else {
                Set<String> elementCount = mapVisitElementCount.get(sighting.getVisitName());
                if (elementCount == null) {
                    elementCount = new HashSet<>();
                    mapVisitElementCount.put(sighting.getVisitName(), elementCount);
                }
                if (!elementCount.contains(sighting.getElementName(reportsBaseDialog.getOptionName()))) {
                    elementCount.add(sighting.getElementName(reportsBaseDialog.getOptionName()));
                    lstChartData.add(new AreaChart.Data<>(tempDifference.get(ChronoUnit.SECONDS), elementCount.size(), 
                        sighting.getElementName(reportsBaseDialog.getOptionName()) + " [" + sighting.getVisitName() + "]"));
                }
            }
        }
        // Get the final list of series
        ObservableList<AreaChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
        List<String> lstKeys = new ArrayList<>(mapGroupedData.keySet());
        Collections.sort(lstKeys);
        for (String key : lstKeys) {
            ObservableList<AreaChart.Data<Number, Number>> lstChartData = mapGroupedData.get(key);
            // Add an entry to the front (and back) to make the first (and last) entry more visible
            int maxCount = lstChartData.size();
            DateWrapper visitDates = mapVisitDates.get(key);
            TemporalAmount tempDifference = Duration.between(visitDates.startDate.atTime(LocalTime.MIN), visitDates.endDate.atTime(LocalTime.MAX));
            lstChartData.add(new AreaChart.Data<>(tempDifference.get(ChronoUnit.SECONDS), maxCount, "PERIOD ENDED"));
            lstChartData.add(new AreaChart.Data<>(dateDiffRange.get(ChronoUnit.SECONDS), maxCount, "END"));
            lstChartData.add(0, new AreaChart.Data<>(0, 0, "START"));
            // Add the series
            chartData.add(new AreaChart.Series<>(key + " (" + lstChartData.size() + ")", lstChartData));
        }
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        NumberAxis dayAxis = new NumberAxis();
        dayAxis.setAutoRanging(true);
        dayAxis.setForceZeroInRange(true);
        dayAxis.setTickLabelFormatter(new StringConverter<Number>() {
            private final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("(HH:mm:ss)");
            @Override
            public String toString(Number object) {
                long allSeconds = object.longValue();
                int days = (int) (allSeconds / /*24 * 60 * 60 = */86400);
                LocalTime time = LocalTime.ofSecondOfDay(allSeconds - (days * 86400));
                return "Day " + UtilsReports.getNumberWithZero(days) + " " + TIMEFORMAT.format(time);
            }
            @Override
            public Number fromString(String string) {
                int days = Integer.parseInt(string.substring(string.indexOf(' '), string.lastIndexOf(' ')));
                String time = string.substring(string.lastIndexOf(' '));
                return TIMEFORMAT.parse(time).get(ChronoField.MILLI_OF_SECOND) + (days * 86400);
            }
        });
        dayAxis.setTickLabelRotation(-90);
        dayAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14));
        AreaChart<Number, Number> chart = new AreaChart<Number, Number>(dayAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-line-30-color");
        chart.setLegendVisible(true);
        if (inIsForObservations) {
            chart.setTitle("Observations per Period over Time");
        }
        else {
            chart.setTitle("Creatures per Period over Time");
        }
        UtilsReports.setupChartTooltips(chart, true, true);
        return chart;
    }
    
    private class DateWrapper {
        LocalDate startDate;
        LocalDate endDate;
    }

}
