package wildlog.ui.reports.implementations;

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
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTime;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.ui.utils.UtilsTime;


public class DayAndNightChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART, LINE_CHART, STACKED_LINE_CHART};
    private ChartType chartType = ChartType.PIE_CHART;
    private Chart displayedChart;

    
    public DayAndNightChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Day and Night Cycle Reports", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(4);
        // Area/Line Chart
        ToggleButton btnPieChart = new ToggleButton("Day/Night Observations (Pie)");
        btnPieChart.setToggleGroup(BUTTON_GROUP);
        btnPieChart.setCursor(Cursor.HAND);
        btnPieChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded during the day, night or twilight.</html>");
            }
        });
        lstCustomButtons.add(btnPieChart);
        ToggleButton btnLineAllChart = new ToggleButton("Day/Night Observations (Line)");
        btnLineAllChart.setToggleGroup(BUTTON_GROUP);
        btnLineAllChart.setCursor(Cursor.HAND);
        btnLineAllChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.LINE_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded during the day, night or twilight.</html>");
            }
        });
        lstCustomButtons.add(btnLineAllChart);
        ToggleButton btnLineCreatureChart = new ToggleButton("Day/Night Observations (Stacked)");
        btnLineCreatureChart.setToggleGroup(BUTTON_GROUP);
        btnLineCreatureChart.setCursor(Cursor.HAND);
        btnLineCreatureChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.STACKED_LINE_CHART;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded during the day, night or twilight.</html>");
            }
        });
        lstCustomButtons.add(btnLineCreatureChart);
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.PIE_CHART)) {
                    setActiveSubCategoryTitle("Day and Night Observations (Pie)");
                    displayedChart = createPieChart(lstData);
                }
                else
                if (chartType.equals(ChartType.LINE_CHART)) {
                    setActiveSubCategoryTitle("Day and Night Observations (Line)");
                    displayedChart = createLineChartForAll(lstData);
                }
                else
                if (chartType.equals(ChartType.STACKED_LINE_CHART)) {
                    setActiveSubCategoryTitle("Day and Night Observations (Stacked)");
                    displayedChart = createStackedChartForAll(lstData);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }
    
    private Chart createPieChart(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>(4);
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapGroupedData.get(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()).toString());
            if (dataWrapper == null) {
                mapGroupedData.put(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()).toString(), new ReportDataWrapper("", "", 1));
            }
            else {
                dataWrapper.increaseCount();
            }
        }
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        List<String> keys = ActiveTime.getEnumListAsStringForReports();
        Collections.sort(keys);
        for (String key : keys) {
            ReportDataWrapper dataWrapper = mapGroupedData.get(key);
            if (dataWrapper != null) {
                chartData.add(new PieChart.Data(key + " (" + mapGroupedData.get(key).getCount() + ")", mapGroupedData.get(key).getCount()));
            }
            else {
// FIXME: Kry 'n manier om die lee data se labels te hide want dit lyk simpel (vir nou is die if hier onder OK)
                if (!ActiveTime.UNKNOWN.equals(ActiveTime.getEnumFromText(key))
                        && !ActiveTime.NONE.equals(ActiveTime.getEnumFromText(key))) {
                    chartData.add(new PieChart.Data(key + " (0)", 0));
                }
            }
        }
        // Setup chart
        PieChart chart = new PieChart(chartData);
        chart.getStylesheets().add("wildlog/ui/reports/chart/styling/ChartsDayNightTwilight.css");
        chart.getStyleClass().add("wl-pie-day-night-color");
        chart.setTitle("Number of Observations per Day, Night and Twilight period");
        UtilsReports.setupChartTooltips(chart);
        return chart;
    }
    
    private Chart createLineChartForAll(List<Sighting> inSightings) {
        // Sort sightings (by date)
        Collections.sort(inSightings);
        // Get the data in the correct structure
        Map<String, Map<Long, ReportDataWrapper>> mapGroupedData = new HashMap<>(4);
        Map<String, ReportDataWrapper> mapCategoryCounter = new HashMap<>(4);
        for (Sighting sighting : inSightings) {
            String categoryKey = ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()).toString();
            Map<Long, ReportDataWrapper> mapChartData = mapGroupedData.get(categoryKey);
            if (mapChartData == null) {
                mapChartData = new LinkedHashMap<>();
                mapGroupedData.put(categoryKey, mapChartData);
            }
            ReportDataWrapper categoryDataWrapper = mapCategoryCounter.get(categoryKey);
            if (categoryDataWrapper == null) {
                categoryDataWrapper = new ReportDataWrapper(null, null, 0);
                mapCategoryCounter.put(categoryKey, categoryDataWrapper);
            }
            categoryDataWrapper.increaseCount();
            ReportDataWrapper dataWrapper = mapChartData.get(sighting.getDate().getTime());
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(null, null, categoryDataWrapper.count);
                mapChartData.put(sighting.getDate().getTime(), dataWrapper);
            }
            else {
                dataWrapper.count = categoryDataWrapper.count;
            }
        }
        // Setup the final chart data
        AreaChart<Number, Number> chart;
        if (!inSightings.isEmpty()) {
            long startTime = inSightings.get(0).getDate().getTime();
            long endTime = inSightings.get(inSightings.size() - 1).getDate().getTime();
            ObservableList<AreaChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
            List<String> keys = ActiveTime.getEnumListAsStringForReports();
            Collections.sort(keys);
            for (String key : keys) {
                Map<Long, ReportDataWrapper> mapChartData = mapGroupedData.get(key);
                if (mapChartData != null) {
                    ObservableList<AreaChart.Data<Number, Number>> lstChartData = FXCollections.observableArrayList();
                    lstChartData.add(new AreaChart.Data<>(startTime, 0, null));
                    for (long time : mapChartData.keySet()) {
                        ReportDataWrapper dataWrapper = mapChartData.get(time);
                        lstChartData.add(new AreaChart.Data<>(time, dataWrapper.count, null));
                    }
                    lstChartData.add(new AreaChart.Data<>(endTime, mapCategoryCounter.get(key).count, null));
                    chartData.add(new AreaChart.Series<>(key + " (" + lstChartData.size() + ")", lstChartData));
                }
                else {
                    if (!ActiveTime.UNKNOWN.equals(ActiveTime.getEnumFromText(key))
                            && !ActiveTime.NONE.equals(ActiveTime.getEnumFromText(key))) {
                        chartData.add(new AreaChart.Series<>(key + " (0)", FXCollections.observableArrayList()));
                    }
                }
            }
            // Setup the axis and chart
            NumberAxis numAxis = new NumberAxis();
            UtilsReports.setupNumberAxis(numAxis, false);
            double tick = (endTime - startTime)/7;
            NumberAxis dateAxis = new NumberAxis(startTime - tick/3, endTime + tick/3, tick);
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
            chart = new AreaChart<Number, Number>(dateAxis, numAxis, chartData);
        }
        else {
            chart = new AreaChart<Number, Number>(new NumberAxis(), new NumberAxis(), FXCollections.observableArrayList());
        }
        chart.getStylesheets().add("wildlog/ui/reports/chart/styling/ChartsDayNightTwilight.css");
        chart.getStyleClass().add("wl-line-day-night-color");
        chart.setLegendVisible(true);
        chart.setTitle("Number of Observations per Day, Night and Twilight period");
        UtilsReports.setupChartTooltips(chart, true, true);
        return chart;
    }
    
    private Chart createStackedChartForAll(List<Sighting> inSightings) {
        // Sort sightings (by date)
        Collections.sort(inSightings);
        // Get the data in the correct structure
        Map<String, Map<Long, ReportDataWrapper>> mapGroupedData = new HashMap<>(4);
        Map<String, ReportDataWrapper> mapCategoryCounter = new HashMap<>(4);
        for (Sighting sighting : inSightings) {
            String categoryKey = ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()).toString();
            Map<Long, ReportDataWrapper> mapChartData = mapGroupedData.get(categoryKey);
            if (mapChartData == null) {
                mapChartData = new LinkedHashMap<>();
                mapGroupedData.put(categoryKey, mapChartData);
            }
            ReportDataWrapper categoryDataWrapper = mapCategoryCounter.get(categoryKey);
            if (categoryDataWrapper == null) {
                categoryDataWrapper = new ReportDataWrapper(null, null, 0);
                mapCategoryCounter.put(categoryKey, categoryDataWrapper);
            }
            categoryDataWrapper.increaseCount();
            ReportDataWrapper dataWrapper = mapChartData.get(sighting.getDate().getTime());
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(null, null, categoryDataWrapper.count);
                mapChartData.put(sighting.getDate().getTime(), dataWrapper);
            }
            else {
                dataWrapper.count = categoryDataWrapper.count;
            }
        }
        // Setup the final chart data
        StackedAreaChart<Number, Number> chart;
        if (!inSightings.isEmpty()) {
            long startTime = inSightings.get(0).getDate().getTime();
            long endTime = inSightings.get(inSightings.size() - 1).getDate().getTime();
            ObservableList<StackedAreaChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
            List<String> keys = ActiveTime.getEnumListAsStringForReports();
            Collections.sort(keys);
            for (String key : keys) {
                Map<Long, ReportDataWrapper> mapChartData = mapGroupedData.get(key);
                if (mapChartData != null) {
                    ObservableList<StackedAreaChart.Data<Number, Number>> lstChartData = FXCollections.observableArrayList();
                    lstChartData.add(new StackedAreaChart.Data<>(startTime, 0, null));
                    for (long time : mapChartData.keySet()) {
                        ReportDataWrapper dataWrapper = mapChartData.get(time);
                        lstChartData.add(new StackedAreaChart.Data<>(time, dataWrapper.count, null));
                    }
                    lstChartData.add(new StackedAreaChart.Data<>(endTime, mapCategoryCounter.get(key).count, null));
                    chartData.add(new StackedAreaChart.Series<>(key + " (" + lstChartData.size() + ")", lstChartData));
                }
                else {
                    if (!ActiveTime.UNKNOWN.equals(ActiveTime.getEnumFromText(key))
                            && !ActiveTime.NONE.equals(ActiveTime.getEnumFromText(key))) {
                        ObservableList<StackedAreaChart.Data<Number, Number>> lstChartData = FXCollections.observableArrayList();
                        lstChartData.add(new StackedAreaChart.Data<>(0, 0, null));
                        chartData.add(new StackedAreaChart.Series<>(key + " (0)", lstChartData));
                    }
                }
            }
            // Setup the axis
            NumberAxis numAxis = new NumberAxis();
            UtilsReports.setupNumberAxis(numAxis, false);
            double tick = (endTime - startTime)/7;
            NumberAxis dateAxis = new NumberAxis(startTime - tick/3, endTime + tick/3, tick);
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
            chart = new StackedAreaChart<Number, Number>(dateAxis, numAxis, chartData);
        }
        else {
            chart = new StackedAreaChart<Number, Number>(new NumberAxis(), new NumberAxis(), FXCollections.observableArrayList());
        }
        chart.getStylesheets().add("wildlog/ui/reports/chart/styling/ChartsDayNightTwilight.css");
        chart.getStyleClass().add("wl-line-day-night-color");
        chart.setLegendVisible(true);
        chart.setTitle("Number of Observations per Day, Night and Twilight period");
        UtilsReports.setupChartTooltips(chart, true, true);
        return chart;
    }
    
}
