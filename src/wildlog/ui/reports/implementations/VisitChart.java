package wildlog.ui.reports.implementations;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.utils.UtilsData;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.BarChartChangeListener;
import wildlog.ui.reports.implementations.helpers.ReportCountWrapper;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.ui.utils.UtilsTime;


public class VisitChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART_SIGHTING_COUNT, BAR_CHART_SIGHTING_COUNT, BAR_CHART_ABUNDANCE, BAR_CHART_ELEMENT_ABUNDANCE, BAR_CHART_ELEMENT_COUNT, BAR_CHART_RICHNESS, PIE_CHART_TYPE, BAR_CHART_COMBINED_DURATION, BAR_CHART_DURATION};
    private ChartType chartType;
    private Chart displayedChart;

    
    public VisitChart(List<Sighting> inLstData,JLabel inChartDescLabel) {
        super("Period Reports", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(9);
        // Pie charts
        Button btnPieChartSightingCount = new Button("Observations per Period (Pie)");
        btnPieChartSightingCount.setCursor(Cursor.HAND);
        btnPieChartSightingCount.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART_SIGHTING_COUNT;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations per Period.</html>");
            }
        });
        lstCustomButtons.add(btnPieChartSightingCount);
        Button btnBarChartName = new Button("Observations per Period (Bar)");
        btnBarChartName.setCursor(Cursor.HAND);
        btnBarChartName.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_SIGHTING_COUNT;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations per Period.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartName);
        Button btnBarChartElementCount = new Button("Creatures per Period (Bar)");
        btnBarChartElementCount.setCursor(Cursor.HAND);
        btnBarChartElementCount.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_ELEMENT_COUNT;
                setupChartDescriptionLabel("<html>This chart shows the number of Creatures observed for each Period.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartElementCount);
        Button btnPieChartType = new Button("Observations per Period Type (Pie)");
        btnPieChartType.setCursor(Cursor.HAND);
        btnPieChartType.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.PIE_CHART_TYPE;
                setupChartDescriptionLabel("<html>This chart shows the number of Observations recorded for each Period Type.</html>");
            }
        });
        lstCustomButtons.add(btnPieChartType);
        Button btnBarChartDuration = new Button("Period Duration (Bar)");
        btnBarChartDuration.setCursor(Cursor.HAND);
        btnBarChartDuration.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_DURATION;
                setupChartDescriptionLabel("<html>This chart shows the duration of each Period (based on the start and end dates).</html>");
            }
        });
        lstCustomButtons.add(btnBarChartDuration);
         Button btnBarChartDurationCombined = new Button("Period Combined Duration (Bar)");
        btnBarChartDurationCombined.setCursor(Cursor.HAND);
        btnBarChartDurationCombined.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_COMBINED_DURATION;
                setupChartDescriptionLabel("<html>This chart shows the number of Periods with a specific duration (based on the start and end dates).</html>");
            }
        });
        lstCustomButtons.add(btnBarChartDurationCombined);
        Button btnBarChartAbundance = new Button("Abundance of Observations (Bar)");
        btnBarChartAbundance.setCursor(Cursor.HAND);
        btnBarChartAbundance.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_ABUNDANCE;
                setupChartDescriptionLabel("<html>This chart can be used as a simplified Observation Abundance report. "
                                        + "<br/>It shows the number of Observations devided by the number of active days for each Period (based on the start and end dates)."
                                        + "<br/><b>Note:</b> This chart works best when comparing Periods with similar durations.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartAbundance);
        Button btnBarChartElementAbundance = new Button("Abundance of Creatures (Bar)");
        btnBarChartElementAbundance.setCursor(Cursor.HAND);
        btnBarChartElementAbundance.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_ELEMENT_ABUNDANCE;
                setupChartDescriptionLabel("<html>This chart can be used as a simplified Creature Abundance report. "
                                        + "<br/>It shows the number of Observations devided by the number of active days for each Period (based on the start and end dates), per Creature."
                                        + "<br/><b>Note:</b> This chart works best when comparing Periods with similar durations.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartElementAbundance);
        Button btnBarChartRichness = new Button("Richness of Creatures (Bar)");
        btnBarChartRichness.setCursor(Cursor.HAND);
        btnBarChartRichness.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.BAR_CHART_RICHNESS;
                setupChartDescriptionLabel("<html>This chart can be used as a simplified Creature Richness report. "
                                        + "<br/>It shows the number of Creatures devided by the number of active days for each Period (based on the start and end dates)."
                                        + "<br/><b>Note:</b> This chart works best when comparing Periods with similar durations.</html>");
            }
        });
        lstCustomButtons.add(btnBarChartRichness);
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.PIE_CHART_TYPE)) {
                    displayedChart = createPieChartVisitTypes(lstData);
                }
                else
                if (chartType.equals(ChartType.PIE_CHART_SIGHTING_COUNT)) {
                    displayedChart = createPieChartSightingCount(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_SIGHTING_COUNT)) {
                    displayedChart = createBarChartSightingCount(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_COMBINED_DURATION)) {
                    displayedChart = createBarChartDurationCombined(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_DURATION)) {
                    displayedChart = createBarChartDuration(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_ABUNDANCE)) {
                    displayedChart = createBarChartAbundance(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_ELEMENT_ABUNDANCE)) {
                    displayedChart = createBarChartElementAbundance(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_ELEMENT_COUNT)) {
                    displayedChart = createBarChartElementCount(lstData);
                }
                else
                if (chartType.equals(ChartType.BAR_CHART_RICHNESS)) {
                    displayedChart = createBarChartRichness(lstData);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }
    
    private Chart createPieChartSightingCount(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapGroupedData.get(sighting.getVisitName());
            if (dataWrapper == null) {
                mapGroupedData.put(sighting.getVisitName(), new ReportDataWrapper("", "", 1));
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
            Visit visit = WildLogApp.getApplication().getDBI().find(new Visit(sighting.getVisitName()));
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
            ReportDataWrapper dataWrapper = mapData.get(sighting.getVisitName());
            if (dataWrapper == null) {
                Visit visit = WildLogApp.getApplication().getDBI().find(new Visit(sighting.getVisitName()));
                dataWrapper = new ReportDataWrapper();
                if (visit != null && visit.getStartDate() != null && visit.getEndDate() != null) {
                    dataWrapper.count = (int) ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), UtilsTime.getLocalDateFromDate(visit.getEndDate()));
                }
                else {
                     dataWrapper.count = 0;
                }
                mapData.put(sighting.getVisitName(), dataWrapper);
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
            if (!processedVisits.contains(sighting.getVisitName())) {
                processedVisits.add(sighting.getVisitName());
                String duration = "Unknown";
                Visit visit = WildLogApp.getApplication().getDBI().find(new Visit(sighting.getVisitName()));
                if (visit != null && visit.getStartDate() != null && visit.getEndDate() != null) {
                    long days = ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), UtilsTime.getLocalDateFromDate(visit.getEndDate()));
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
                dataWrapper.value = dataWrapper.value + sighting.getVisitName();
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
            ReportDataWrapper dataWrapper = mapData.get(sighting.getVisitName());
            if (dataWrapper == null) {
                dataWrapper = new ReportDataWrapper(sighting.getVisitName(), null, 0);
                mapData.put(sighting.getVisitName(), dataWrapper);
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
        Map<String, Set<String>> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Set<String> elementSet = mapData.get(sighting.getVisitName());
            if (elementSet == null) {
                elementSet = new HashSet<>();
                mapData.put(sighting.getVisitName(), elementSet);
            }
            elementSet.add(sighting.getElementName());
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
    
    private Chart createBarChartAbundance(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapData.get(sighting.getVisitName());
            if (dataWrapper == null) {
                Visit visit = WildLogApp.getApplication().getDBI().find(new Visit(sighting.getVisitName()));
                if (visit != null && visit.getStartDate() != null && visit.getEndDate() != null) {
                    long days = ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), UtilsTime.getLocalDateFromDate(visit.getEndDate()));
                    dataWrapper = new ReportDataWrapper(sighting.getVisitName(), days, 0);
                    mapData.put(sighting.getVisitName(), dataWrapper);
                }
            }
            if (dataWrapper != null) {
                dataWrapper.increaseCount();
            }
        }
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            ReportDataWrapper dataWrapper = mapData.get(key);
            BarChart.Data<String, Number> data = new BarChart.Data<String, Number>(key, Math.round((double) dataWrapper.count / (double) ((long) dataWrapper.value) * 1000.0) / 1000.0);
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
        chart.setTitle("Ratio of Observations over the Duration of each Period");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }
    
    private Chart createBarChartElementAbundance(List<Sighting> inSightings) {
        Map<String, Map<String, ReportDataWrapper>> mapChartDataGroupedForSeries = new HashMap<>();
        Map<String, ReportCountWrapper> mapKnownVisitInfo = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Map<String, ReportDataWrapper> mapPeriodsForElement = mapChartDataGroupedForSeries.get(sighting.getElementName());
            if (mapPeriodsForElement == null) {
                mapPeriodsForElement = new HashMap<>();
                // Don't add to the amp yet, because the continue below might want to skip this visit...
            }
            ReportDataWrapper dataWrapper = mapPeriodsForElement.get(sighting.getVisitName());
            ReportCountWrapper visitLevelInfo = mapKnownVisitInfo.get(sighting.getVisitName());
            if (dataWrapper == null) {
                if (visitLevelInfo == null) {
                    Visit visit = WildLogApp.getApplication().getDBI().find(new Visit(sighting.getVisitName()));
                    if (visit != null && visit.getStartDate() != null && visit.getEndDate() != null) {
                        long days = ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), UtilsTime.getLocalDateFromDate(visit.getEndDate()));
                        visitLevelInfo = new ReportCountWrapper(0, 0, 0, (int) days);
                        mapKnownVisitInfo.put(sighting.getVisitName(), visitLevelInfo);
                    }
                    else {
                        // If this visit does not have a valid date range, then don't process it, continue to the next record instead
                        continue;
                    }
                }
                dataWrapper = new ReportDataWrapper(sighting.getVisitName(), (int) visitLevelInfo.count, 0);
                mapPeriodsForElement.put(sighting.getVisitName(), dataWrapper);
            }
            mapChartDataGroupedForSeries.putIfAbsent(sighting.getElementName(), mapPeriodsForElement);
            dataWrapper.increaseCount();
            visitLevelInfo.total = visitLevelInfo.total + 1;
        }
        // Setup the final data series to be displayed
        ObservableList<StackedBarChart.Series<String, Number>> lstChartSeries = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapChartDataGroupedForSeries.keySet());
        Collections.sort(keys);
        for (String keyElement : keys) {
            StackedBarChart.Series<String, Number> series = new StackedBarChart.Series<String,Number>();
            Map<String, ReportDataWrapper> mapPeriodsForElement = mapChartDataGroupedForSeries.get(keyElement);
            for (String keyVisit : mapPeriodsForElement.keySet()) {
                ReportDataWrapper dataWrapper = mapPeriodsForElement.get(keyVisit);
                series.getData().add(new StackedBarChart.Data<String, Number>(keyVisit, Math.round((double) dataWrapper.count / (double) ((int) dataWrapper.value) * 1000.0) / 1000.0, keyElement));
            }
            series.setName(keyElement);
            lstChartSeries.add(series);
        }
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapChartDataGroupedForSeries.size(), true);
        StackedBarChart<String, Number> chart = UtilsReports.createStackedBarChartWithStyleIndexBiggerThanEight(catAxis, numAxis, lstChartSeries);
        chart.getStyleClass().add("wl-stacked-bar-30-color");
        chart.setTitle("Ratio of Observations over the Duration of each Period and Creature");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }

    private Chart createBarChartRichness(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapData.get(sighting.getVisitName());
            if (dataWrapper == null) {
                Visit visit = WildLogApp.getApplication().getDBI().find(new Visit(sighting.getVisitName()));
                if (visit != null && visit.getStartDate() != null && visit.getEndDate() != null) {
                    long days = ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), UtilsTime.getLocalDateFromDate(visit.getEndDate()));
                    dataWrapper = new ReportDataWrapper(sighting.getVisitName(), new HashSet<String>(), (int) days);
                    mapData.put(sighting.getVisitName(), dataWrapper);
                }
            }
            if (dataWrapper != null) {
                ((HashSet<String>) dataWrapper.value).add(sighting.getElementName());
            }
        }
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            ReportDataWrapper dataWrapper = mapData.get(key);
            BarChart.Data<String, Number> data = new BarChart.Data<String, Number>(key, Math.round((double) ((HashSet<String>) dataWrapper.value).size() / (double) ((long) dataWrapper.count) * 1000.0) / 1000.0);
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
        chart.setTitle("Ratio of Creatures observed over the Duration of each Period");
        UtilsReports.setupChartTooltips(chart, true, false);
        return chart;
    }
    
}
