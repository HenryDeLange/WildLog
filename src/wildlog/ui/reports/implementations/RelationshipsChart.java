package wildlog.ui.reports.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Background;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.enums.ActiveTime;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.reports.ReportsBaseDialog;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.HorizontalBarChartChangeListener;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.ui.utils.UtilsTime;


public class RelationshipsChart extends AbstractReport<Sighting> {
    private enum ChartType {CHART_ELEMENTS};
    private ChartType chartType;
    private Chart displayedChart;
    private final CheckBox chkCompareVisits;
    private final ComboBox<String> cmbGPS;
    private final ComboBox<String> cmbCompareDates;
    private final ComboBox<String> cmbType;
    
    
    public RelationshipsChart(List<Sighting> inLstData, JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        super("Relationship Reports", inLstData, inChartDescLabel, inReportsBaseDialog);
        lstCustomButtons = new ArrayList<>(6);
        ToggleButton btnPieChartElementTypes = new ToggleButton("Creature Associations");
        btnPieChartElementTypes.setToggleGroup(BUTTON_GROUP);
        btnPieChartElementTypes.setCursor(Cursor.HAND);
        btnPieChartElementTypes.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.CHART_ELEMENTS;
                setupChartDescriptionLabel("<html>This chart shows the number of times two Creatures (species) where observed together.</html>");
            }
        });
        lstCustomButtons.add(btnPieChartElementTypes);
        // Chart options
// TODO: Sit 'n beskrywing onder aan die chart (en dalk in die titel) wat verduidelik hoe die opsies dinge beinvloed
        lstCustomButtons.add(new Label("Chart Options:"));
        chkCompareVisits = new CheckBox("Must be in the same Period");
        chkCompareVisits.setCursor(Cursor.HAND);
        chkCompareVisits.setSelected(true);
        lstCustomButtons.add(chkCompareVisits);
        cmbGPS = new ComboBox<>(FXCollections.observableArrayList(new String[] {
            "Ignore GPS coordinates", 
            "Compare exact GPS coordinates", 
            "Compare nearby GPS coordinates", 
            "Compare far away GPS coordinates"}));
        cmbGPS.setCursor(Cursor.HAND);
        cmbGPS.setVisibleRowCount(10);
        cmbGPS.getSelectionModel().clearSelection();
        cmbGPS.getSelectionModel().select(1);
        lstCustomButtons.add(cmbGPS);
        cmbCompareDates = new ComboBox<>(FXCollections.observableArrayList(new String[] {
            "Ignore Dates", 
            "Within 1 Day-Night Cycle", 
            "Within 2 Day-Night Cycles", 
            "Within 3 Day-Night Cycles", 
            "Within 7 Day-Night Cycles", 
            "Within 14 Day-Night Cycles"}));
        cmbCompareDates.setCursor(Cursor.HAND);
        cmbCompareDates.setVisibleRowCount(10);
        cmbCompareDates.getSelectionModel().clearSelection();
        cmbCompareDates.getSelectionModel().select(3);
        lstCustomButtons.add(cmbCompareDates);
        cmbType = new ComboBox<>(FXCollections.observableArrayList(new String[] {
// FIXME: Besluit wat om hier alles te doen...
            "Total Count", 
            "Relative Count", 
            "Ratio"}));
        cmbType.setCursor(Cursor.HAND);
        cmbType.setVisibleRowCount(10);
        cmbType.getSelectionModel().clearSelection();
        cmbType.getSelectionModel().select(0);
        lstCustomButtons.add(cmbType);
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.CHART_ELEMENTS)) {
                    setActiveSubCategoryTitle("Creature Associations");
                    displayedChart = createElementRelationshipChart(lstData);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }
    
    private Chart createElementRelationshipChart(List<Sighting> inSightings) {
        // Calculate the values
        Set<String> elements = new HashSet<>();
        Map<String, ReportDataWrapper> mapChartData = new HashMap<>();
        Map<String, Integer> elementTotalCounts = new HashMap<>(2);
        int maxCount = 0;
        for (Sighting sighting1 : inSightings) {
            String elementName1 = sighting1.getElementName(reportsBaseDialog.getOptionName());
            elements.add(elementName1);
            for (Sighting sighting2 : inSightings) {
                if (isRelated(sighting1, sighting2)) {
                    String elementName2 = sighting2.getElementName(reportsBaseDialog.getOptionName());
                    String groupName;
                    if (elementName1.compareTo(elementName2) < 0) {
                        groupName = elementName1 + " - " + elementName2;
                    }
                    else {
                        groupName = elementName2 + " - " + elementName1;
                    }
                    ReportDataWrapper dataWrapper = mapChartData.get(groupName);
                    if (dataWrapper == null) {
                        BubbleInfo bubbleInfo = new BubbleInfo();
                        dataWrapper = new ReportDataWrapper(groupName, bubbleInfo, 0);
                        mapChartData.put(groupName, dataWrapper);
                    }
                    BubbleInfo bubbleInfo = (BubbleInfo) dataWrapper.value;
                    if (!bubbleInfo.processedSightings.contains(sighting1.getSightingCounter())) {
                        // Keep track of the total number of pairs
                        dataWrapper.increaseCount();
                        bubbleInfo.processedSightings.add(sighting1.getSightingCounter());
                        if (maxCount < dataWrapper.count) {
                            maxCount = dataWrapper.count;
                        }
                        // Keep track of the number of each Element in the pair
                        int counter = bubbleInfo.elementRatio.getOrDefault(elementName1, 0);
                        bubbleInfo.elementRatio.put(elementName1, counter + 1);
                    }
                    if (!bubbleInfo.processedSightings.contains(sighting2.getSightingCounter())) {
                        // Keep track of the total number of pairs
                        dataWrapper.increaseCount();
                        bubbleInfo.processedSightings.add(sighting2.getSightingCounter());
                        if (maxCount < dataWrapper.count) {
                            maxCount = dataWrapper.count;
                        }
                        // Keep track of the number of each Element in the pair
                        int counter = bubbleInfo.elementRatio.getOrDefault(elementName2, 0);
                        bubbleInfo.elementRatio.put(elementName2, counter + 1);
                    }
                }
            }
            // Update the total counts
            int total = elementTotalCounts.getOrDefault(elementName1, 0);
            elementTotalCounts.put(elementName1, total + 1);
        }
        // Get the data in the correct structure for the chart to use
        List<String> keys = new ArrayList<>(mapChartData.keySet());
        Collections.sort(keys);
        BarChart.Series<Number, String> series = new BarChart.Series<>();
        for (String key : keys) {
            ReportDataWrapper dataWrapper = mapChartData.get(key);
            if (dataWrapper != null && dataWrapper.count > 0) {
                int value;
                if (cmbType.getSelectionModel().isSelected(0)) {
                    value = dataWrapper.count;
                }
                else
                if (cmbType.getSelectionModel().isSelected(1)) {
                    BubbleInfo bubbleInfo = (BubbleInfo) dataWrapper.value;
                    List<String> elementPair = new ArrayList<>(bubbleInfo.elementRatio.keySet());
                    double count1 = bubbleInfo.elementRatio.get(elementPair.get(0));
                    double count2 = bubbleInfo.elementRatio.get(elementPair.get(1));
                    double total1 = elementTotalCounts.get(elementPair.get(0));
                    double total2 = elementTotalCounts.get(elementPair.get(1));
                    double averageRatio = ((count1 / total1) + (count2 / total2)) / 2.0;
                    int adjustedCount = (int) (dataWrapper.count * averageRatio);
System.out.println("PAIR = " + key);
System.out.println(elementPair.get(0) + " = " + (int) count1 + " of " + (int) total1);
System.out.println(elementPair.get(1) + " = " + (int) count2 + " of " + (int) total2);
System.out.println("Totaal vir die twee saam = " + dataWrapper.count);
System.out.println("Average Ratio = " + averageRatio);
System.out.println("Adjusted Count = " + adjustedCount);
System.out.println("---------------------------------------");
                    value = adjustedCount;
                }
                else {
                    BubbleInfo bubbleInfo = (BubbleInfo) dataWrapper.value;
                    List<String> elementPair = new ArrayList<>(bubbleInfo.elementRatio.keySet());
                    double count1 = bubbleInfo.elementRatio.get(elementPair.get(0));
                    double count2 = bubbleInfo.elementRatio.get(elementPair.get(1));
                    double total1 = elementTotalCounts.get(elementPair.get(0));
                    double total2 = elementTotalCounts.get(elementPair.get(1));
                    double averageRatio = ((count1 / total1) + (count2 / total2)) / 2.0;
                    int adjustedCount = (int) (dataWrapper.count * averageRatio);
System.out.println("PAIR = " + key);
System.out.println(elementPair.get(0) + " = " + (int) count1 + " of " + (int) total1);
System.out.println(elementPair.get(1) + " = " + (int) count2 + " of " + (int) total2);
System.out.println("Totaal vir die twee saam = " + dataWrapper.count);
System.out.println("Average Ratio = " + averageRatio);
System.out.println("Adjusted Count = " + adjustedCount);
System.out.println("---------------------------------------");
                    value = (int) (averageRatio * 100.0);
                }
                BarChart.Data<Number, String> data = new BarChart.Data<>(value, key);
                data.nodeProperty().addListener(new HorizontalBarChartChangeListener<>(mapChartData.size(), data));
                series.getData().add(data);
            }
        }
        // Sort the results
        Collections.sort(series.getData(), new Comparator<BarChart.Data<Number, String>>() {
            @Override
            public int compare(BarChart.Data<Number, String> inData1, BarChart.Data<Number, String> inData2) {
                int compare = Double.compare(inData1.getXValue().doubleValue(), inData2.getXValue().doubleValue());
                if (compare == 0) {
                    compare = inData2.getYValue().compareTo(inData1.getYValue());
                }
                return compare;
            }
        });
        ObservableList<BarChart.Series<Number, String>> chartSeries = FXCollections.observableArrayList();
        chartSeries.add(series);
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, false);
        CategoryAxis catAxis = new CategoryAxis();
        UtilsReports.setupCategoryAxis(catAxis, mapChartData.size(), false);
        BarChart<Number, String> chart = new BarChart<Number, String>(numAxis, catAxis, chartSeries);
        chart.getStyleClass().add("wl-bar-single-color");
        chart.setLegendVisible(false);
        chart.setTitle("Creatures Observed together");
        UtilsReports.setupChartTooltips(chart, false, false);
        return chart;
    }
    
    private boolean isRelated(Sighting inSighting1, Sighting inSighting2) {
        // Maak seker dis ander Element
        if (inSighting1.getElementName(reportsBaseDialog.getOptionName()).equals(inSighting2.getElementName(reportsBaseDialog.getOptionName()))) {
            return false;
        }
        // Check Visit
        if (chkCompareVisits.isSelected()) {
            if (!inSighting1.getVisitName().equals(inSighting2.getVisitName())) {
                return false;
            }
        }
        // Check GPS
        if (!cmbGPS.getSelectionModel().isSelected(0)) {
            if (!UtilsGPS.hasGPSData(inSighting1) || !UtilsGPS.hasGPSData(inSighting1)) {
                return false;
            }
            double padding;
            if (cmbGPS.getSelectionModel().isSelected(2)) {
                padding = 0.0025;
            }
            else
            if (cmbGPS.getSelectionModel().isSelected(3)) {
                padding = 0.025;
            }
            else {
                padding = 0.0;
            }
            double inNorthEast_Lat = UtilsGPS.getLatDecimalDegree(inSighting1) + padding;
            double inNorthEast_Lon = UtilsGPS.getLonDecimalDegree(inSighting1) + padding;
            double inSouthWest_Lat = UtilsGPS.getLatDecimalDegree(inSighting1) - padding;
            double inSouthWest_Lon = UtilsGPS.getLonDecimalDegree(inSighting1) - padding;
            if (!UtilsGPS.isSightingInBox(inSighting2, inNorthEast_Lat, inNorthEast_Lon, inSouthWest_Lat, inSouthWest_Lon)) {
                return false;
            }
        }
        // Check Date
        if (!cmbCompareDates.getSelectionModel().isSelected(0)) {
            int requieredDays;
            if (cmbCompareDates.getSelectionModel().isSelected(1)) {
                requieredDays = 1;
            }
            else
            if (cmbCompareDates.getSelectionModel().isSelected(2)) {
                requieredDays = 2;
            }
            else
            if (cmbCompareDates.getSelectionModel().isSelected(3)) {
                requieredDays = 3;
            }
            else
            if (cmbCompareDates.getSelectionModel().isSelected(4)) {
                requieredDays = 7;
            }
            else
            if (cmbCompareDates.getSelectionModel().isSelected(5)) {
                requieredDays = 14;
            }
            else {
                requieredDays = Integer.MIN_VALUE;
            }
            long dayDifference = Math.abs(ChronoUnit.DAYS.between(getAdjustedDate(inSighting1), getAdjustedDate(inSighting2)));
            if (dayDifference > requieredDays){
                return false;
            }
        }
        // As alles reg was, dan is die Sightings verwant aan mekaar
        return true;
    }
    
    private LocalDate getAdjustedDate(Sighting inSighting) {
        LocalDateTime adjustedDate = UtilsTime.getLocalDateTimeFromDate(inSighting.getDate());
        // Since nights can be over two days, check from 12pm to 12pm of the next day (also handle the first day's half night)
        if (ActiveTime.NIGHT.equals(ActiveTime.getFromActiveTimeSpecific(inSighting.getTimeOfDay()))) {
            if (adjustedDate.toLocalTime().isAfter(LocalTime.MIDNIGHT) && adjustedDate.toLocalTime().isBefore(LocalTime.NOON)) {
                adjustedDate = adjustedDate.minusDays(1);
            }
        }
        return adjustedDate.toLocalDate();
    }
    
    private class BubbleInfo {
        public Set<Long> processedSightings = new HashSet<>();
        public Map<String, Integer> elementRatio = new HashMap<>(2);
    }
    
}
