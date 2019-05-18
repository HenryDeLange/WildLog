package wildlog.ui.charts.implementations;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.ui.charts.ChartsBaseDialog;
import wildlog.ui.charts.implementations.helpers.AbstractChart;
import wildlog.ui.charts.implementations.helpers.BarChartChangeListener;
import wildlog.ui.charts.implementations.helpers.ChartCountWrapper;
import wildlog.ui.charts.implementations.helpers.ChartDataWrapper;
import wildlog.ui.charts.utils.UtilsCharts;
import wildlog.utils.UtilsTime;


public class AbundanceRichnessChart extends AbstractChart<Sighting> {
    private enum ChartType {BAR_CHART_ABUNDANCE, BAR_CHART_ELEMENT_ABUNDANCE, BAR_CHART_RICHNESS};
    private ChartType chartType;
    
    
    public AbundanceRichnessChart(List<Sighting> inLstData,JLabel inChartDescLabel, ChartsBaseDialog inReportsBaseDialog) {
        super("Abundance-Richness Charts", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(9);
        ToggleButton btnBarChartAbundance = new ToggleButton("Abundance of Observations");
        btnBarChartAbundance.setToggleGroup(BUTTON_GROUP);
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
        ToggleButton btnBarChartElementAbundance = new ToggleButton("Abundance of Creatures");
        btnBarChartElementAbundance.setToggleGroup(BUTTON_GROUP);
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
        ToggleButton btnBarChartRichness = new ToggleButton("Richness of Creatures");
        btnBarChartRichness.setToggleGroup(BUTTON_GROUP);
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
        displayedChart = null;
        if (chartType.equals(ChartType.BAR_CHART_ABUNDANCE)) {
            setActiveSubCategoryTitle("Abundance of Observations");
            displayedChart = createBarChartAbundance(lstData);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART_ELEMENT_ABUNDANCE)) {
            setActiveSubCategoryTitle("Abundance of Creatures");
            displayedChart = createBarChartElementAbundance(lstData);
        }
        else
        if (chartType.equals(ChartType.BAR_CHART_RICHNESS)) {
            setActiveSubCategoryTitle("Richness of Creatures");
            displayedChart = createBarChartRichness(lstData);
        }
        displayedChart.setBackground(Background.EMPTY);
        inScene.setRoot(displayedChart);
    }
    
    private Chart createBarChartAbundance(List<Sighting> inSightings) {
        Map<String, ChartDataWrapper> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ChartDataWrapper dataWrapper = mapData.get(sighting.getCachedVisitName());
            if (dataWrapper == null) {
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitID(), null, false, Visit.class);
                if (visit != null && visit.getStartDate() != null) {
                    LocalDate endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                    if (endDate == null) {
                        endDate = LocalDate.now();
                    }
                    long days = ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), endDate);
                    dataWrapper = new ChartDataWrapper(sighting.getCachedVisitName(), days, 0);
                    mapData.put(sighting.getCachedVisitName(), dataWrapper);
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
            ChartDataWrapper dataWrapper = mapData.get(key);
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
        UtilsCharts.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsCharts.setupCategoryAxis(catAxis, mapData.size(), true);
        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Ratio of Observations over the Duration of each Period");
        UtilsCharts.setupChartTooltips(chart, true, false);
        return chart;
    }
    
    private Chart createBarChartElementAbundance(List<Sighting> inSightings) {
        Map<String, Map<String, ChartDataWrapper>> mapChartDataGroupedForSeries = new HashMap<>();
        Map<String, ChartCountWrapper> mapKnownVisitInfo = new HashMap<>();
        for (Sighting sighting : inSightings) {
            Map<String, ChartDataWrapper> mapPeriodsForElement = mapChartDataGroupedForSeries.get(sighting.getCachedElementName(reportsBaseDialog.getOptionName()));
            if (mapPeriodsForElement == null) {
                mapPeriodsForElement = new HashMap<>();
                // Don't add to the amp yet, because the continue below might want to skip this visit...
            }
            ChartDataWrapper dataWrapper = mapPeriodsForElement.get(sighting.getCachedVisitName());
            ChartCountWrapper visitLevelInfo = mapKnownVisitInfo.get(sighting.getCachedVisitName());
            if (dataWrapper == null) {
                if (visitLevelInfo == null) {
                    Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitID(), null, false, Visit.class);
                    if (visit != null && visit.getStartDate() != null) {
                        LocalDate endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                        if (endDate == null) {
                            endDate = LocalDate.now();
                        }
                        long days = ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), endDate);
                        visitLevelInfo = new ChartCountWrapper(0, 0, 0, (int) days);
                        mapKnownVisitInfo.put(sighting.getCachedVisitName(), visitLevelInfo);
                    }
                    else {
                        // If this visit does not have a valid date range, then don't process it, continue to the next record instead
                        continue;
                    }
                }
                dataWrapper = new ChartDataWrapper(sighting.getCachedVisitName(), (int) visitLevelInfo.count, 0);
                mapPeriodsForElement.put(sighting.getCachedVisitName(), dataWrapper);
            }
            mapChartDataGroupedForSeries.putIfAbsent(sighting.getCachedElementName(reportsBaseDialog.getOptionName()), mapPeriodsForElement);
            dataWrapper.increaseCount();
            visitLevelInfo.total = visitLevelInfo.total + 1;
        }
        // Setup the final data series to be displayed
        ObservableList<StackedBarChart.Series<String, Number>> lstChartSeries = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapChartDataGroupedForSeries.keySet());
        Collections.sort(keys);
        for (String keyElement : keys) {
            StackedBarChart.Series<String, Number> series = new StackedBarChart.Series<String,Number>();
            Map<String, ChartDataWrapper> mapPeriodsForElement = mapChartDataGroupedForSeries.get(keyElement);
            for (String keyVisit : mapPeriodsForElement.keySet()) {
                ChartDataWrapper dataWrapper = mapPeriodsForElement.get(keyVisit);
                series.getData().add(new StackedBarChart.Data<String, Number>(keyVisit, Math.round((double) dataWrapper.count / (double) ((int) dataWrapper.value) * 1000.0) / 1000.0, keyElement));
            }
            series.setName(keyElement);
            lstChartSeries.add(series);
        }
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsCharts.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsCharts.setupCategoryAxis(catAxis, mapChartDataGroupedForSeries.size(), true);
        StackedBarChart<String, Number> chart = UtilsCharts.createStackedBarChartWithStyleIndexBiggerThanEight(catAxis, numAxis, lstChartSeries);
        chart.getStyleClass().add("wl-stacked-bar-30-color");
        chart.setTitle("Ratio of Observations over the Duration for each Period, per Creature");
        UtilsCharts.setupChartTooltips(chart, true, false);
        return chart;
    }

    private Chart createBarChartRichness(List<Sighting> inSightings) {
        Map<String, ChartDataWrapper> mapData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            ChartDataWrapper dataWrapper = mapData.get(sighting.getCachedVisitName());
            if (dataWrapper == null) {
                Visit visit = WildLogApp.getApplication().getDBI().findVisit(sighting.getVisitID(), null, false, Visit.class);
                if (visit != null && visit.getStartDate() != null) {
                    LocalDate endDate = UtilsTime.getLocalDateFromDate(visit.getEndDate());
                    if (endDate == null) {
                        endDate = LocalDate.now();
                    }
                    long days = ChronoUnit.DAYS.between(UtilsTime.getLocalDateFromDate(visit.getStartDate()), endDate);
                    dataWrapper = new ChartDataWrapper(sighting.getCachedVisitName(), new HashSet<String>(), (int) days);
                    mapData.put(sighting.getCachedVisitName(), dataWrapper);
                }
            }
            if (dataWrapper != null) {
                ((HashSet<String>) dataWrapper.value).add(sighting.getCachedElementName());
            }
        }
        ObservableList<BarChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        ObservableList<BarChart.Data<String, Number>> allSightings = FXCollections.observableArrayList();
        List<String> keys = new ArrayList<>(mapData.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            ChartDataWrapper dataWrapper = mapData.get(key);
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
        UtilsCharts.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsCharts.setupCategoryAxis(catAxis, mapData.size(), true);
        BarChart<String, Number> chart = new BarChart<String, Number>(catAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Ratio of Creatures observed over the Duration of each Period");
        UtilsCharts.setupChartTooltips(chart, true, false);
        return chart;
    }
    
}
