package wildlog.ui.reports.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.astro.AstroCalculator;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.enums.ActiveTime;
import wildlog.data.enums.ActiveTimeSpesific;
import wildlog.data.enums.Moonlight;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.utils.UtilsTime;


public class MoonphaseChart extends AbstractReport<Sighting> {
    private enum ChartType {BAR_CHART_ALL, BAR_CHART_ELEMENTS, LINE_CHART_ALL, LINE_CHART_ELEMENTS, PIE_CHART};
    private ChartType chartType = ChartType.BAR_CHART_ALL;
    private Chart displayedChart;
    private boolean showDayOrNight = false;
    private boolean showMoonShiningOrNot = false;
    private boolean showBaseline = false;
    private final int PERCENTAGES_PER_INTERVAL = 10;
    private final String BASELINE = "<<Baseline>>";
    private final String MOON_BELOW_50 = "Half Moon 0-50%";
    private final String MOON_ABOVE_50 = "Full Moon 50-100%";
    private final String MOON_UNKNOWN = "Unknown";
    private final String ALL_SIGHTINGS = "All Observations";
    
    
    public MoonphaseChart(List<Sighting> inLstData, JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        super("Moon Phase Reports", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(9);
        // Charts
        ToggleButton btnPieChart = new ToggleButton("All Observations Together (Pie)");
        btnPieChart.setToggleGroup(BUTTON_GROUP);
        btnPieChart.setCursor(Cursor.HAND);
        btnPieChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded during a certain Moon Phase."
                        + "<br/>(The phase and visibilaty of the moon isn't tied to the sun and can be visible during the day or night.)</html>");
            }
        });
        lstCustomButtons.add(btnPieChart);
        ToggleButton btnLineChart = new ToggleButton("All Observations Together (Bar)");
        btnLineChart.setToggleGroup(BUTTON_GROUP);
        btnLineChart.setCursor(Cursor.HAND);
        btnLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_ALL;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded during a certain Moon Phase."
                        + "<br/>(The phase and visibilaty of the moon isn't tied to the sun and can be visible during the day or night.)</html>");
            }
        });
        lstCustomButtons.add(btnLineChart);
        ToggleButton btnBarChart = new ToggleButton("All Observations Together (Line)");
        btnBarChart.setToggleGroup(BUTTON_GROUP);
        btnBarChart.setCursor(Cursor.HAND);
        btnBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.LINE_CHART_ALL;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded during a certain Moon Phase."
                        + "<br/>(The phase and visibilaty of the moon isn't tied to the sun and can be visible during the day or night.)</html>");
            }
        });
        lstCustomButtons.add(btnBarChart);
        ToggleButton btnStackedBarChart = new ToggleButton("Grouped by Creatures (Bar)");
        btnStackedBarChart.setToggleGroup(BUTTON_GROUP);
        btnStackedBarChart.setCursor(Cursor.HAND);
        btnStackedBarChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_ELEMENTS;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded for each Creature during a certain Moon Phase."
                        + "<br/>(The phase and visibilaty of the moon isn't tied to the sun and can be visible during the day or night.)</html>");
            }
        });
        lstCustomButtons.add(btnStackedBarChart);
        ToggleButton btnLineChartAll = new ToggleButton("Grouped by Creatures (Line)");
        btnLineChartAll.setToggleGroup(BUTTON_GROUP);
        btnLineChartAll.setCursor(Cursor.HAND);
        btnLineChartAll.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.LINE_CHART_ELEMENTS;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded for each Creature during a certain Moon Phase."
                        + "<br/>(The phase and visibilaty of the moon isn't tied to the sun and can be visible during the day or night.)</html>");
            }
        });
        lstCustomButtons.add(btnLineChartAll);
        // Chart options
        lstCustomButtons.add(new Label("Chart Options:"));
        CheckBox chkShowDetails = new CheckBox("Show Day, Night and Twilight");
        chkShowDetails.setCursor(Cursor.HAND);
        chkShowDetails.setSelected(false);
        chkShowDetails.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showDayOrNight = chkShowDetails.isSelected();
            }
        });
        lstCustomButtons.add(chkShowDetails);
        CheckBox chkShowMoonlight = new CheckBox("Show Moon Shining or No Moon");
        chkShowMoonlight.setCursor(Cursor.HAND);
        chkShowMoonlight.setSelected(false);
        chkShowMoonlight.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showMoonShiningOrNot = chkShowMoonlight.isSelected();
            }
        });
        lstCustomButtons.add(chkShowMoonlight);
// TODO: Maak dalk twee opsies: een vir "Baseline at Night" en een vir "Baseline at Day"
        CheckBox chkShowBaseline = new CheckBox("Show Baseline");
        chkShowBaseline.setCursor(Cursor.HAND);
        chkShowBaseline.setSelected(false);
        chkShowBaseline.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showBaseline = chkShowBaseline.isSelected();
            }
        });
        lstCustomButtons.add(chkShowBaseline);
    }

    @Override
    public void createReport(Scene inScene) {
        displayedChart = null;
        if (chartType.equals(ChartType.BAR_CHART_ALL)) {
            setActiveSubCategoryTitle("All Observations Together");
            displayedChart = createBarChart(lstData, true);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART_ELEMENTS)) {
            setActiveSubCategoryTitle("Grouped by Creatures");
            displayedChart = createBarChart(lstData, false);
        }
        else
        if (chartType.equals(ChartType.LINE_CHART_ALL)) {
            setActiveSubCategoryTitle("All Observations Together");
            displayedChart = createLineChart(lstData, true);
        }
        else
        if (chartType.equals(ChartType.LINE_CHART_ELEMENTS)) {
            setActiveSubCategoryTitle("Grouped by Creatures");
            displayedChart = createLineChart(lstData, false);
        }
        else
        if (chartType.equals(ChartType.PIE_CHART)) {
            setActiveSubCategoryTitle("All Observations Together");
            displayedChart = createPieChart(lstData);
        }
        displayedChart.setBackground(Background.EMPTY);
        inScene.setRoot(displayedChart);
    }
    
    private Chart createBarChart(List<Sighting> inSightings, boolean inIsForAllObservations) {
        // Build the data map that will be displayed
        Map<String, Map<String, ReportDataWrapper>> mapChartDataGroupedForSeries = new HashMap<>();
        String temp = null;
        if (inIsForAllObservations) {
            temp = ALL_SIGHTINGS;
        }
        for (Sighting sighting : inSightings) {
            if (!inIsForAllObservations) {
                temp = sighting.getElementName(reportsBaseDialog.getOptionName());
            }
            Map<String, ReportDataWrapper> mapChartData = mapChartDataGroupedForSeries.get(temp + getDetailsString(sighting));
            if (mapChartData == null) {
                mapChartData = new HashMap<>();
                mapChartDataGroupedForSeries.put(temp + getDetailsString(sighting), mapChartData);
            }
            String key = getMoonphaseKey(sighting.getMoonPhase(), UtilsTime.getLocalDateTimeFromDate(sighting.getDate()), "");
            increaseCountInMap(mapChartData, key);
        }
        // Add the baseline
        if (showBaseline) {
            Map<String, ReportDataWrapper> mapChartData = new HashMap<>();
            Map<String, VisitDates> mapVisitDates = getBaselineDates(inSightings);
            for (VisitDates visitDates : mapVisitDates.values()) {
                if (visitDates.startDate != null && visitDates.endDate != null) {
                    for (LocalDate day = visitDates.startDate; day.isBefore(visitDates.endDate); day = day.plusDays(1)) {
                        int moonphase = AstroCalculator.getMoonPhase(Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        String key = getMoonphaseKey(moonphase, day.atStartOfDay(), "");
                        increaseCountInMap(mapChartData, key);
                    }
                }
            }
            mapChartDataGroupedForSeries.put(BASELINE, mapChartData);
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
                series.getData().add(new StackedBarChart.Data<String, Number>(mapKey, count, key));
            }
            series.setName(key + " (" + total + ")");
            lstChartSeries.add(series);
        }
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, 3, true);
        catAxis.setCategories(FXCollections.<String>observableArrayList(new String[]{"Half Moon 0-50%", "Full Moon 50-100%", "Unknown"}));
        StackedBarChart<String, Number> chart = UtilsReports.createStackedBarChartWithStyleIndexBiggerThanEight(catAxis, numAxis, lstChartSeries);
        chart.getStyleClass().add("wl-stacked-bar-30-color");
        chart.setTitle("Number of Observations for each Moon Phase");
        UtilsReports.setupChartTooltips(chart, true, false, true);
        return chart;
    }
    
    private Chart createPieChart(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapChartData = new HashMap<>(5);
        for (Sighting sighting : inSightings) {
            String key = getMoonphaseKey(sighting.getMoonPhase(), UtilsTime.getLocalDateTimeFromDate(sighting.getDate()), getDetailsString(sighting));
            increaseCountInMap(mapChartData, key);
        }
        // Add the baseline
        if (showBaseline) {
            Map<String, VisitDates> mapVisitDates = getBaselineDates(inSightings);
            for (VisitDates visitDates : mapVisitDates.values()) {
                if (visitDates.startDate != null && visitDates.endDate != null) {
                    for (LocalDate day = visitDates.startDate; day.isBefore(visitDates.endDate); day = day.plusDays(1)) {
                        int moonphase = AstroCalculator.getMoonPhase(Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        String key = getMoonphaseKey(moonphase, day.atStartOfDay(), " - " + BASELINE);
                        increaseCountInMap(mapChartData, key);
                    }
                }
            }
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
        PieChart chart = new PieChart(chartData);
        chart.getStyleClass().add("wl-pie-30-color");
        chart.setTitle("Number of Observations for each Moon Phase");
        UtilsReports.setupChartTooltips(chart);
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
            temp = ALL_SIGHTINGS;
        }
        for (Sighting sighting : inSightings) {
            if (!inIsForAllObservations) {
                temp = sighting.getElementName(reportsBaseDialog.getOptionName());
            }
            ReportDataWrapper dataWrapper = mapInitialCountedData.get(temp + getDetailsString(sighting) + "-" + getMoonIntervalPercentage(sighting.getMoonPhase()));
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
                mapInitialCountedData.put(temp + getDetailsString(sighting) + "-" + getMoonIntervalPercentage(sighting.getMoonPhase()), dataWrapper);
                mapTotalsForSeries.put(temp + getDetailsString(sighting), 0);
            }
            dataWrapper.increaseCount();
        }
        // Add the baseline
        if (showBaseline) {
            Map<String, VisitDates> mapVisitDates = getBaselineDates(inSightings);
            for (VisitDates visitDates : mapVisitDates.values()) {
                if (visitDates.startDate != null && visitDates.endDate != null) {
                    for (LocalDate day = visitDates.startDate; day.isBefore(visitDates.endDate); day = day.plusDays(1)) {
                        int moonphase = AstroCalculator.getMoonPhase(Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        ReportDataWrapper dataWrapper = mapInitialCountedData.get(BASELINE + "-" + getMoonIntervalPercentage(moonphase));
                        if (dataWrapper == null) {
                            dataWrapper = new ReportDataWrapper();
                            dataWrapper.key = BASELINE;
                            if (moonphase >= 0) {
                                dataWrapper.value = getMoonIntervalPercentage(moonphase);
                            }
                            else {
                                dataWrapper.value = Moonlight.UNKNOWN.toString();
                            }
                            dataWrapper.count = 0;
                            mapInitialCountedData.put(BASELINE + "-" + getMoonIntervalPercentage(moonphase), dataWrapper);
                            mapTotalsForSeries.put(BASELINE, 0);
                        }
                        dataWrapper.increaseCount();
                    }
                }
            }
        }
        // Add all the points on the chart in the correct order. This also adds the 0 values for the data gaps.
        Map<String, ObservableList<AreaChart.Data<String, Number>>> mapDataPerElement = new HashMap<>(mapInitialCountedData.size());
        for (String seriesName : mapTotalsForSeries.keySet()) {
            ObservableList<AreaChart.Data<String, Number>> lstChartDataForElement = FXCollections.observableArrayList();
            mapDataPerElement.put(seriesName, lstChartDataForElement);
            for (int percentage = 0; percentage <= 100/PERCENTAGES_PER_INTERVAL; percentage++) {
                lstChartDataForElement.add(new AreaChart.Data<String, Number>(getMoonIntervalPercentage(percentage*PERCENTAGES_PER_INTERVAL), 0, seriesName));
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
        UtilsReports.setupChartTooltips(chart, true, false);
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
    
    private String getMoonphaseKey(int inMoonphase, LocalDateTime inDate, String inDetailsString) {
        String key;
        if (inMoonphase >= 0) {
            if (inMoonphase >= 0 && inMoonphase < 50) {
                key = MOON_BELOW_50 + inDetailsString;
            } 
            else
            if (inMoonphase > 50 && inMoonphase <= 100) {
                key = MOON_ABOVE_50 + inDetailsString;
            } 
            else
            // If the moon is 50% then base it on whether the moon is growing or shrinking.
            if (inMoonphase == 50) {
                LocalDateTime futureTime = inDate.plusDays(2);
                int testMoonphase = AstroCalculator.getMoonPhase(Date.from(futureTime.atZone(ZoneId.systemDefault()).toInstant()));
                if (testMoonphase >= 0 && testMoonphase < 50) {
                    key = MOON_BELOW_50 + inDetailsString;
                } 
                else
                if (testMoonphase > 50 && testMoonphase <= 100) {
                    key = MOON_ABOVE_50 + inDetailsString;
                } 
                else {
                    key = MOON_UNKNOWN;
                }
            }
            else {
                key = MOON_UNKNOWN;
            }
        }
        else {
            key = MOON_UNKNOWN;
        }
        return key;
    }
    
    private Map<String, VisitDates> getBaselineDates(List<Sighting> inLstSightings) {
        Map<String, VisitDates> mapDates = new HashMap<>();
        for (Sighting sighting : inLstSightings) {
            VisitDates visitDates = mapDates.get(sighting.getVisitName());
            if (visitDates == null) {
                visitDates = new VisitDates();
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitName(), Visit.class);
                if (visit.getStartDate() != null) {
                    visitDates.startDate = UtilsTime.getLocalDateFromDate(visit.getStartDate());
                    visitDates.startDateFromVisit = true;
                }
                if (visit.getEndDate()!= null) {
                    visitDates.endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                    visitDates.endDateFromVisit = true;
                }
                mapDates.put(sighting.getVisitName(), visitDates);
            }
            if (!visitDates.startDateFromVisit || !visitDates.endDateFromVisit) {
                LocalDate sightingDate = UtilsTime.getLocalDateFromDate(sighting.getDate());
                if (!visitDates.startDateFromVisit) {
                    if (visitDates.startDate == null || sightingDate.isBefore(visitDates.startDate)) {
                        visitDates.startDate = sightingDate;
                    }
                }
                if (!visitDates.endDateFromVisit) {
                    if (visitDates.endDate == null || sightingDate.isAfter(visitDates.endDate)) {
                        visitDates.endDate = sightingDate;
                    }
                }
            }
        }
        for (Map.Entry<String, VisitDates> entry : mapDates.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue().startDate.toString() + " - "  + entry.getValue().endDate.toString() + " ("  + entry.getValue().endDateFromVisit + ")");
        }
        return mapDates;
    }
    
    private class VisitDates {
        LocalDate startDate;
        boolean startDateFromVisit = false;
        LocalDate endDate;
        boolean endDateFromVisit = false;
    }
    
}
