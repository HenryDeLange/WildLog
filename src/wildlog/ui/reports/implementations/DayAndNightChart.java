package wildlog.ui.reports.implementations;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.util.StringConverter;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTime;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.utils.UtilsTime;


public class DayAndNightChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART, LINE_CHART, STACKED_LINE_CHART/*, STACKED_LINE_100_PERCENT_CHART*/};
    private ChartType chartType = ChartType.PIE_CHART;
    private Chart displayedChart;

    
    public DayAndNightChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Day and Night Cycles", inLstData, inChartDescLabel);
//        "<html>This collection of charts focus on day and night cycle of Observations.</html>"
        lstCustomButtons = new ArrayList<>(4);
        // Area/Line Chart
        Button btnPieChart = new Button("Pie Chart");
        btnPieChart.setCursor(Cursor.HAND);
        btnPieChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART;
                
            }
        });
        lstCustomButtons.add(btnPieChart);
        // Area/Line Chart
        Button btnLineAllChart = new Button("Line Chart");
        btnLineAllChart.setCursor(Cursor.HAND);
        btnLineAllChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.LINE_CHART;
                
            }
        });
        lstCustomButtons.add(btnLineAllChart);
        // Area/Line Chart
        Button btnLineCreatureChart = new Button("Stacked Line Chart");
        btnLineCreatureChart.setCursor(Cursor.HAND);
        btnLineCreatureChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.STACKED_LINE_CHART;
                
            }
        });
        lstCustomButtons.add(btnLineCreatureChart);
// FIXME: Vir eers gebruik ek nie die chart nie, want hy is nog nie "lekker" nie...
//        // Area/Line Chart
//        JButton btnLine100CreatureChart = new JButton("100% Stacked Line Chart");
//        btnLine100CreatureChart.setFocusPainted(false);
//        btnLine100CreatureChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
//        btnLine100CreatureChart.setMargin(new Insets(2, 4, 2, 4));
//        btnLine100CreatureChart.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                chartType = ChartType.STACKED_LINE_100_PERCENT_CHART;
//                if (displayedChart != null) {
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            scene.setRoot(createChart());
//                        }
//                    });
//                }
//            }
//        });
//        lstCustomButtons.add(btnLine100CreatureChart);
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.PIE_CHART)) {
                    displayedChart = createPieChart(lstData);
                }
                else
                if (chartType.equals(ChartType.LINE_CHART)) {
                    displayedChart = createLineChartForAll(lstData);
                }
                else
                if (chartType.equals(ChartType.STACKED_LINE_CHART)) {
                    displayedChart = createStackedChartForAll(lstData);
                }
//                else
//                if (chartType.equals(ChartType.STACKED_LINE_100_PERCENT_CHART)) {
//                    displayedChart = create100PercentStackedChartForAll(lstData);
//                }
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
        List<String> keys = new ArrayList<>(mapGroupedData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            chartData.add(new PieChart.Data(key + " (" + mapGroupedData.get(key).getCount() + ")", mapGroupedData.get(key).getCount()));
        }
        PieChart chart = new PieChart(chartData);
        return chart;
    }
    
    private Chart createLineChartForAll(List<Sighting> inSightings) {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
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
        long startTime = inSightings.get(0).getDate().getTime();
        long endTime = inSightings.get(inSightings.size() - 1).getDate().getTime();
        ObservableList<AreaChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapGroupedData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            Map<Long, ReportDataWrapper> mapChartData = mapGroupedData.get(key);
            ObservableList<AreaChart.Data<Number, Number>> lstChartData = FXCollections.observableArrayList();
            lstChartData.add(new AreaChart.Data<>(startTime, 0, null));
            for (long time : mapChartData.keySet()) {
                ReportDataWrapper dataWrapper = mapChartData.get(time);
                lstChartData.add(new AreaChart.Data<>(time, dataWrapper.count, null));
            }
            lstChartData.add(new AreaChart.Data<>(endTime, mapCategoryCounter.get(key).count, null));
            chartData.add(new AreaChart.Series<>(key + " (" + lstChartData.size() + ")", lstChartData));
        }
        // Setup the axis
        double tick = (endTime - startTime)/7;
        NumberAxis axisX = new NumberAxis(startTime - tick/3, endTime + tick/3, tick);
        axisX.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return UtilsTime.WL_DATE_FORMATTER.format(object);
            }
            @Override
            public Number fromString(String string) {
                try {
                    return UtilsTime.WL_DATE_FORMATTER.parse(string).getTime();
                }
                catch (ParseException ex) {
                    ex.printStackTrace(System.err);
                }
                return 0;
            }
        });
        // Add an entry to the front and back to make the first and last entry more visible
        AreaChart<Number, Number> chart = new AreaChart<Number, Number>(axisX, axisY, chartData);
        return chart;
    }
    
    private Chart createStackedChartForAll(List<Sighting> inSightings) {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Observations");
        axisY.setAutoRanging(true);
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
        long startTime = inSightings.get(0).getDate().getTime();
        long endTime = inSightings.get(inSightings.size() - 1).getDate().getTime();
        ObservableList<StackedAreaChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapGroupedData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            Map<Long, ReportDataWrapper> mapChartData = mapGroupedData.get(key);
            ObservableList<StackedAreaChart.Data<Number, Number>> lstChartData = FXCollections.observableArrayList();
            lstChartData.add(new StackedAreaChart.Data<>(startTime, 0, null));
            for (long time : mapChartData.keySet()) {
                ReportDataWrapper dataWrapper = mapChartData.get(time);
                lstChartData.add(new StackedAreaChart.Data<>(time, dataWrapper.count, null));
            }
            lstChartData.add(new StackedAreaChart.Data<>(endTime, mapCategoryCounter.get(key).count, null));
            chartData.add(new StackedAreaChart.Series<>(key + " (" + lstChartData.size() + ")", lstChartData));
        }
        // Setup the axis
        double tick = (endTime - startTime)/7;
        NumberAxis axisX = new NumberAxis(startTime - tick/3, endTime + tick/3, tick);
        axisX.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return UtilsTime.WL_DATE_FORMATTER.format(object);
            }
            @Override
            public Number fromString(String string) {
                try {
                    return UtilsTime.WL_DATE_FORMATTER.parse(string).getTime();
                }
                catch (ParseException ex) {
                    ex.printStackTrace(System.err);
                }
                return 0;
            }
        });
        // Add an entry to the front and back to make the first and last entry more visible
        StackedAreaChart<Number, Number> chart = new StackedAreaChart<Number, Number>(axisX, axisY, chartData);
        return chart;
    }
    
    // FIXME: Ek comment die chart vir eers uit... 
    //    Dit is nog nie regtig handig nie, en dis misleidend omdat die punte aand die einde 'n kleiner impak het op die 
    //    grafiek as die eerste punte. Basies begin die storie net uit-average en 'n mens verloor die impak van klein veranderings oor
    //    tyd. Dit is maklikker om die pie chart en line chart te gebruik en te filter op datums en dan die grafieke te vergelyk...
    
//    private Chart create100PercentStackedChartForAll(List<Sighting> inSightings) {
//        NumberAxis axisY = new NumberAxis();
//        axisY.setLabel("Number of Observations");
//        axisY.setAutoRanging(true);
//        // Sort sightings (by date)
//        Collections.sort(inSightings);
//        // Get the data in the correct structure
//        Map<String, LinkedHashMap<Long, ReportDataWrapper>> mapGroupedData = new HashMap<>(4);
//        Map<String, ReportDataWrapper> mapCategoryCounter = new HashMap<>(4);
//        Map<Long, ReportDataWrapper> mapTotalCounter = new HashMap<>();
//        int totalCount = 0;
//        for (Sighting sighting : inSightings) {
//            String categoryKey = ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()).toString();
//            LinkedHashMap<Long, ReportDataWrapper> mapChartData = mapGroupedData.get(categoryKey);
//            if (mapChartData == null) {
//                mapChartData = new LinkedHashMap<>();
//                mapGroupedData.put(categoryKey, mapChartData);
//            }
//            // Keep track of the amount of entries for each category
//            ReportDataWrapper categoryDataWrapper = mapCategoryCounter.get(categoryKey);
//            if (categoryDataWrapper == null) {
//                categoryDataWrapper = new ReportDataWrapper(null, null, 0);
//                mapCategoryCounter.put(categoryKey, categoryDataWrapper);
//            }
//            categoryDataWrapper.increaseCount();
//            // Set the value of the category for this particular time
//            ReportDataWrapper dataWrapper = mapChartData.get(sighting.getDate().getTime());
//            if (dataWrapper == null) {
//                dataWrapper = new ReportDataWrapper(null, null, categoryDataWrapper.count);
//                mapChartData.put(sighting.getDate().getTime(), dataWrapper);
//            }
//            else {
//                dataWrapper.count = categoryDataWrapper.count;
//            }
//            // Keep track of the total number of records for all categories by this time
//            ReportDataWrapper dataTotalWrapper = mapTotalCounter.get(sighting.getDate().getTime());
//            if (dataTotalWrapper == null) {
//                dataTotalWrapper = new ReportDataWrapper(null, null, 0);
//                mapTotalCounter.put(sighting.getDate().getTime(), dataTotalWrapper);
//            }
//            dataTotalWrapper.count = ++totalCount;
//            // Now make sure that the other categories have a value as well for this Sighting's time
//            if (!ActiveTime.DAY.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
//                ReportDataWrapper otherCategoryDataWrapper = mapCategoryCounter.get(ActiveTime.DAY.toString());
//                if (otherCategoryDataWrapper == null) {
//                    otherCategoryDataWrapper = new ReportDataWrapper(null, null, 0);
//                    mapCategoryCounter.put(ActiveTime.DAY.toString(), otherCategoryDataWrapper);
//                }
//                LinkedHashMap<Long, ReportDataWrapper> mapOtherCategoryData = mapGroupedData.get(ActiveTime.DAY.toString());
//                if (mapOtherCategoryData == null) {
//                    mapOtherCategoryData = new LinkedHashMap<>();
//                }
//                mapOtherCategoryData.putIfAbsent(sighting.getDate().getTime(), new ReportDataWrapper(null, null, otherCategoryDataWrapper.count));
//                mapGroupedData.put(ActiveTime.DAY.toString(), mapOtherCategoryData);
//            }
//            if (!ActiveTime.NIGHT.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
//                ReportDataWrapper otherCategoryDataWrapper = mapCategoryCounter.get(ActiveTime.NIGHT.toString());
//                if (otherCategoryDataWrapper == null) {
//                    otherCategoryDataWrapper = new ReportDataWrapper(null, null, 0);
//                    mapCategoryCounter.put(ActiveTime.NIGHT.toString(), otherCategoryDataWrapper);
//                }
//                LinkedHashMap<Long, ReportDataWrapper> mapOtherCategoryData = mapGroupedData.get(ActiveTime.NIGHT.toString());
//                if (mapOtherCategoryData == null) {
//                    mapOtherCategoryData = new LinkedHashMap<>();
//                }
//                mapOtherCategoryData.putIfAbsent(sighting.getDate().getTime(), new ReportDataWrapper(null, null, otherCategoryDataWrapper.count));
//                mapGroupedData.put(ActiveTime.NIGHT.toString(), mapOtherCategoryData);
//            }
//            if (!ActiveTime.DAWN_OR_DUST.equals(ActiveTime.getFromActiveTimeSpecific(sighting.getTimeOfDay()))) {
//                ReportDataWrapper otherCategoryDataWrapper = mapCategoryCounter.get(ActiveTime.DAWN_OR_DUST.toString());
//                if (otherCategoryDataWrapper == null) {
//                    otherCategoryDataWrapper = new ReportDataWrapper(null, null, 0);
//                    mapCategoryCounter.put(ActiveTime.DAWN_OR_DUST.toString(), otherCategoryDataWrapper);
//                }
//                LinkedHashMap<Long, ReportDataWrapper> mapOtherCategoryData = mapGroupedData.get(ActiveTime.DAWN_OR_DUST.toString());
//                if (mapOtherCategoryData == null) {
//                    mapOtherCategoryData = new LinkedHashMap<>();
//                }
//                mapOtherCategoryData.putIfAbsent(sighting.getDate().getTime(), new ReportDataWrapper(null, null, otherCategoryDataWrapper.count));
//                mapGroupedData.put(ActiveTime.DAWN_OR_DUST.toString(), mapOtherCategoryData);
//            }
//        }
//        // Setup the final chart data
//        long startTime = inSightings.get(0).getDate().getTime();
//        long endTime = inSightings.get(inSightings.size() - 1).getDate().getTime();
//        ObservableList<StackedAreaChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
//        List<String> keys = new ArrayList<>(mapGroupedData.keySet());
//        Collections.sort(keys);
//        for (String key : keys) {
//            Map<Long, ReportDataWrapper> mapTimeData = mapGroupedData.get(key);
//            ObservableList<StackedAreaChart.Data<Number, Number>> lstChartData = FXCollections.observableArrayList();
//            lstChartData.add(new StackedAreaChart.Data<>(startTime, 0, null));
//            for (long time : mapTimeData.keySet()) {
//                ReportDataWrapper dataWrapper = mapTimeData.get(time);
//                lstChartData.add(new StackedAreaChart.Data<>(time, 
//                        (int)((((double)dataWrapper.count)/((double)mapTotalCounter.get(time).count))*100.0), null));
//            }
//            lstChartData.add(new StackedAreaChart.Data<>(endTime, lstChartData.get(lstChartData.size() - 1).getYValue(), null));
//            chartData.add(new StackedAreaChart.Series<>(key + " (" + lstChartData.size() + ")", lstChartData));
//        }
//        // Setup the axis
//        double tick = (endTime - startTime)/7;
//        NumberAxis axisX = new NumberAxis(startTime - tick/3, endTime + tick/3, tick);
//        axisX.setTickLabelFormatter(new StringConverter<Number>() {
//            @Override
//            public String toString(Number object) {
//                return UtilsReports.dateFormat.format(object);
//            }
//            @Override
//            public Number fromString(String string) {
//                try {
//                    return UtilsReports.dateFormat.parse(string).getTime();
//                }
//                catch (ParseException ex) {
//                    ex.printStackTrace(System.err);
//                }
//                return 0;
//            }
//        });
//        // Add an entry to the front and back to make the first and last entry more visible
//        StackedAreaChart<Number, Number> chart = new StackedAreaChart<Number, Number>(axisX, axisY, chartData);
//        return chart;
//    }
   
}
