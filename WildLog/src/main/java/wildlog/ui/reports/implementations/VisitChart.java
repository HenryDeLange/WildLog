package wildlog.ui.reports.implementations;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.utils.UtilsData;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.BarChartChangeListener;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.utils.UtilsTime;


public class VisitChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART_SIGHTING_COUNT, BAR_CHART_SIGHTING_COUNT, BAR_CHART_ELEMENT_COUNT, PIE_CHART_TYPE, BAR_CHART_COMBINED_DURATION, BAR_CHART_DURATION};
    private ChartType chartType;

// TODO: Klompie van hierdie charts sal beter werk as line (area) charts
    
    public VisitChart(List<Sighting> inLstData,JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        super("Period Charts", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(9);
        // Pie charts
        ToggleButton btnPieChartSightingCount = new ToggleButton("Observations per Period (Pie)");
        btnPieChartSightingCount.setToggleGroup(BUTTON_GROUP);
        btnPieChartSightingCount.setCursor(Cursor.HAND);
        btnPieChartSightingCount.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART_SIGHTING_COUNT;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations per Period.</html>");
            }
        });
        lstCustomButtons.add(btnPieChartSightingCount);
        ToggleButton btnBarChartName = new ToggleButton("Observations per Period (Bar)");
        btnBarChartName.setToggleGroup(BUTTON_GROUP);
        btnBarChartName.setCursor(Cursor.HAND);
        btnBarChartName.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_SIGHTING_COUNT;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations per Period.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartName);
        ToggleButton btnBarChartElementCount = new ToggleButton("Creatures per Period");
        btnBarChartElementCount.setToggleGroup(BUTTON_GROUP);
        btnBarChartElementCount.setCursor(Cursor.HAND);
        btnBarChartElementCount.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_ELEMENT_COUNT;
                setupChartDescriptionLabel("<html>This chart shows the number of Creatures observed for each Period.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartElementCount);
        ToggleButton btnPieChartType = new ToggleButton("Observations per Period Type");
        btnPieChartType.setToggleGroup(BUTTON_GROUP);
        btnPieChartType.setCursor(Cursor.HAND);
        btnPieChartType.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART_TYPE;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded for each Period Type.</html>");
            }
        });
        lstCustomButtons.add(btnPieChartType);
        ToggleButton btnBarChartDuration = new ToggleButton("Period Duration");
        btnBarChartDuration.setToggleGroup(BUTTON_GROUP);
        btnBarChartDuration.setCursor(Cursor.HAND);
        btnBarChartDuration.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_DURATION;
                setupChartDescriptionLabel("<html>This chart shows the duration, in days, of each Period (based on the start and end dates).</html>");
            }
        });
        lstCustomButtons.add(btnBarChartDuration);
        ToggleButton btnBarChartDurationCombined = new ToggleButton("Grouped Period Duration");
        btnBarChartDurationCombined.setToggleGroup(BUTTON_GROUP);
        btnBarChartDurationCombined.setCursor(Cursor.HAND);
        btnBarChartDurationCombined.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_COMBINED_DURATION;
                setupChartDescriptionLabel("<html>This chart shows the number of Periods with a specific duration (based on the start and end dates).</html>");
            }
        });
        lstCustomButtons.add(btnBarChartDurationCombined);
    }

    @Override
    public void createReport(Scene inScene) {
        displayedChart = null;
        if (chartType.equals(ChartType.PIE_CHART_TYPE)) {
            setActiveSubCategoryTitle("Observations per Period Type");
            displayedChart = createPieChartVisitTypes(lstData);
        }
        else
        if (chartType.equals(ChartType.PIE_CHART_SIGHTING_COUNT)) {
            setActiveSubCategoryTitle("Observations per Period (Pie)");
            displayedChart = createPieChartSightingCount(lstData);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART_SIGHTING_COUNT)) {
            setActiveSubCategoryTitle("Observations per Period (Bar)");
            displayedChart = createBarChartSightingCount(lstData);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART_COMBINED_DURATION)) {
            setActiveSubCategoryTitle("Grouped Period Duration");
            displayedChart = createBarChartDurationCombined(lstData);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART_DURATION)) {
            setActiveSubCategoryTitle("Period Duration");
            displayedChart = createBarChartDuration(lstData);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART_ELEMENT_COUNT)) {
            setActiveSubCategoryTitle("Creatures per Period");
            displayedChart = createBarChartElementCount(lstData);
        }
        displayedChart.setBackground(Background.EMPTY);
        inScene.setRoot(displayedChart);
    }
    
    private Chart createPieChartSightingCount(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapGroupedData.get(sighting.getCachedVisitName());
            if (dataWrapper == null) {
                mapGroupedData.put(sighting.getCachedVisitName(), new ReportDataWrapper("", "", 1));
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
        chart.getStyleClass().add("wl-pie-30-color");
        chart.setTitle("Number of Observations for each Period");
        UtilsReports.setupChartTooltips(chart);
        return chart;
    }
    
    private Chart createPieChartVisitTypes(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitID(), null, false, Visit.class);
            String category = UtilsData.stringFromObject(visit.getType());
            if (category == null || category.isEmpty()) {
                category = "Unknown";
            }
            ReportDataWrapper dataWrapper = mapGroupedData.get(category);
            if (dataWrapper == null) {
                mapGroupedData.put(category, new ReportDataWrapper("", "", 1));
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
        chart.getStyleClass().add("wl-pie-30-color");
        chart.setTitle("Number of Observations for each Period Type");
        UtilsReports.setupChartTooltips(chart);
        return chart;
    }
    
    private Chart createBarChartDuration(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapData.get(sighting.getCachedVisitName());
            if (dataWrapper == null) {
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitID(), null, false, Visit.class);
                dataWrapper = new ReportDataWrapper();
                if (visit != null && visit.getStartDate() != null) {
                    LocalDate endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                    if (endDate == null) {
                        endDate = LocalDate.now();
                    }
                    dataWrapper.count = (int) ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), endDate);
                }
                else {
                     dataWrapper.count = 0;
                }
                mapData.put(sighting.getCachedVisitName(), dataWrapper);
            }
        }
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        for (String key : keys) {
            BarChart.Data<String, Number> data = new BarChart.Data<String, Number>(key, mapData.get(key).count);
            data.nodeProperty().addListener(new BarChartChangeListener<>(mapData.size(), data));
            allSightings.add(data);
        }
        // Sort the results
        Collections.sort(allSightings, new Comparator<XYChart.Data<String, Number>>() {
            @Override
            public int compare(XYChart.Data<String, Number> inData1, XYChart.Data<String, Number> inData2) {
                return Integer.compare(inData2.getYValue().intValue(), inData1.getYValue().intValue());
            }
        });
        // Add the resutls to the final series
        chartData.add(new BarChart.Series<String, Number>("Periods (" + mapData.keySet().size() + ")", allSightings));
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapData.size(), true);
        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of days between start and end dates per Period");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }
    
    private Chart createBarChartDurationCombined(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapData = new HashMap<>();
        Set<String> processedVisits = new HashSet<>();
        for (Sighting sighting : inSightings) {
            if (!processedVisits.contains(sighting.getCachedVisitName())) {
                processedVisits.add(sighting.getCachedVisitName());
                String duration = "Unknown";
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitID(), null, false, Visit.class);
                if (visit != null && visit.getStartDate() != null) {
                    LocalDate endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                    if (endDate == null) {
                        endDate = LocalDate.now();
                    }
                    long days = ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), endDate);
                    duration = days + " days";
                }
                ReportDataWrapper dataWrapper = mapData.get(duration);
                if (dataWrapper == null) {
                    dataWrapper = new ReportDataWrapper(duration, "", 0);
                    mapData.put(duration, dataWrapper);
                }
                if (!dataWrapper.value.toString().isEmpty()) {
                    dataWrapper.value = dataWrapper.value + ", ";
                }
                dataWrapper.value = dataWrapper.value + sighting.getCachedVisitName();
                dataWrapper.increaseCount();
            }
        }
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        for (String key : keys) {
            ReportDataWrapper dataWrapper = mapData.get(key);
            BarChart.Data<String, Number> data = new BarChart.Data<String, Number>(key, dataWrapper.count, dataWrapper.value);
            data.nodeProperty().addListener(new BarChartChangeListener<>(mapData.size(), data));
            allSightings.add(data);
        }
        // Sort the results
        Collections.sort(allSightings, new Comparator<XYChart.Data<String, Number>>() {
            @Override
            public int compare(XYChart.Data<String, Number> inData1, XYChart.Data<String, Number> inData2) {
                if (inData1.getXValue().equals("Unknown")) {
                    return Integer.compare(Integer.MAX_VALUE, Integer.parseInt(inData2.getXValue().substring(0, inData2.getXValue().indexOf(' '))));
                }
                if (inData2.getXValue().equals("Unknown")) {
                    return Integer.compare(Integer.parseInt(inData1.getXValue().substring(0, inData1.getXValue().indexOf(' '))), Integer.MAX_VALUE);
                }
                return Integer.compare(Integer.parseInt(inData1.getXValue().substring(0, inData1.getXValue().indexOf(' '))), 
                                       Integer.parseInt(inData2.getXValue().substring(0, inData2.getXValue().indexOf(' '))));
            }
        });
        // Add the resutls to the final series
        chartData.add(new BarChart.Series<String, Number>("Periods (" + mapData.keySet().size() + ")", allSightings));
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapData.size(), true);
        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Periods with the specified duration");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }
    
    private Chart createBarChartSightingCount(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapData.get(sighting.getCachedVisitName());
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(sighting.getCachedVisitName(), null, 0);
                mapData.put(sighting.getCachedVisitName(), dataWrapper);
            }
            dataWrapper.increaseCount();
        }
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            BarChart.Data<String, Number> data = new BarChart.Data<String, Number>(key, mapData.get(key).count);
            data.nodeProperty().addListener(new BarChartChangeListener<>(mapData.size(), data));
            allSightings.add(data);
        }
        // Sort the results
        Collections.sort(allSightings, new Comparator<BarChart.Data<String, Number>>() {
            @Override
            public int compare(BarChart.Data<String, Number> inData1, BarChart.Data<String, Number> inData2) {
                int compare = Double.compare(inData2.getYValue().doubleValue(), inData1.getYValue().doubleValue());
                if (compare == 0) {
                    compare = inData1.getXValue().compareTo(inData2.getXValue());
                }
                return compare;
            }
        });
        // Add the results to the final series
        chartData.add(new BarChart.Series<String, Number>("Periods (" + mapData.keySet().size() + ")", allSightings));
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapData.size(), true);
        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Observations for each Period");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }

    private Chart createBarChartElementCount(List<Sighting> inSightings) {
        Map<String, Set<Long>> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Set<Long> elementSet = mapData.get(sighting.getCachedVisitName());
            if (elementSet == null) {
                elementSet = new HashSet<>();
                mapData.put(sighting.getCachedVisitName(), elementSet);
            }
            elementSet.add(sighting.getElementID());
        }
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            BarChart.Data<String, Number> data = new BarChart.Data<String, Number>(key, mapData.get(key).size());
            data.nodeProperty().addListener(new BarChartChangeListener<>(mapData.size(), data));
            allSightings.add(data);
        }
        // Sort the results
        Collections.sort(allSightings, new Comparator<BarChart.Data<String, Number>>() {
            @Override
            public int compare(BarChart.Data<String, Number> inData1, BarChart.Data<String, Number> inData2) {
                int compare = Double.compare(inData2.getYValue().doubleValue(), inData1.getYValue().doubleValue());
                if (compare == 0) {
                    compare = inData1.getXValue().compareTo(inData2.getXValue());
                }
                return compare;
            }
        });
        // Add the results to the final series
        chartData.add(new BarChart.Series<String, Number>("Periods (" + mapData.keySet().size() + ")", allSightings));
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapData.size(), true);
        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of Creatures for each Period");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }
    
}
