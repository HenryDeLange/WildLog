package wildlog.ui.reports.implementations;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.ui.utils.UtilsTime;


public class SpeciesAccumulationChart extends AbstractReport<Sighting> {
    private enum ChartType {LINE_CHART};
    private ChartType chartType = ChartType.LINE_CHART;
    private Chart displayedChart;

    
    public SpeciesAccumulationChart(List<Sighting> inLstData, JLabel inChartDescLabel) {
        super("Accumulation Reports", inLstData, inChartDescLabel);
        lstCustomButtons = new ArrayList<>(1);
        // Species accumulation chart
        Button btnLineChart = new Button("Creature Accumulation");
        btnLineChart.setCursor(Cursor.HAND);
        btnLineChart.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                chartType = ChartType.LINE_CHART;
                setupChartDescriptionLabel("<html>This chart illustrates the rate at which new Creatures were recorded over time.</html>");
            }
        });
        lstCustomButtons.add(btnLineChart);
    }

    @Override
    public void createReport(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedChart = null;
                if (chartType.equals(ChartType.LINE_CHART)) {
                    displayedChart = createReport(lstData);
                }
                displayedChart.setBackground(Background.EMPTY);
                inScene.setRoot(displayedChart);
            }
        });
    }
    
    private Chart createReport(List<Sighting> inSightings) {
        // Get the data in the correct structure
        // Get sorted (by date) Sightings list
        Collections.sort(inSightings);
        Map<String, LocalDateTime> mapAccumulation = new LinkedHashMap<>();
        for (Sighting sighting : inSightings) {
            mapAccumulation.putIfAbsent(sighting.getElementName(), UtilsTime.getLocalDateTimeFromDate(sighting.getDate()));
        }
        // Construct the final list of series that needs to be displayed
        int counter = 1;
        ObservableList<AreaChart.Data<Number, Number>> lstChartData = FXCollections.observableArrayList();
        long startTime = 0;
        long endTime = 0;
        for (String elementName : mapAccumulation.keySet()) {
            endTime = UtilsTime.getDateFromLocalDateTime(mapAccumulation.get(elementName)).getTime();
            if (startTime == 0) {
                startTime = endTime;
                // Add an entry to the front (and back) to make the first (and last) entry more visible
                lstChartData.add(new AreaChart.Data<>(startTime, 0, ""));
            }
            lstChartData.add(new AreaChart.Data<>(endTime, counter++, elementName));
        }
        ObservableList<AreaChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
        chartData.add(new AreaChart.Series<>("All Creatures", lstChartData));
        // Add an entry to the front and back to make the first and last entry more visible
//        double tick = (endTime - startTime)/7;
//        lstChartData.get(0).setXValue((startTime - tick/3));
//        lstChartData.add(new AreaChart.Data<>(endTime + tick/3, counter - 1, ""));
        // Setup axis and chart
        NumberAxis numAxis = new NumberAxis();
        UtilsReports.setupNumberAxis(numAxis, "Number of Creatures");
//        NumberAxis dateAxis = new NumberAxis(startTime - tick/10, endTime + tick/10, tick);
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
        dateAxis.setTickLabelFont(Font.font(12));
        AreaChart<Number, Number> chart = new AreaChart<Number, Number>(dateAxis, numAxis, chartData);
        chart.getStyleClass().add("wl-line-30-color");
        chart.setLegendVisible(false);
        chart.setTitle("Number of new Creatures observed over Time");
        return chart;
    }
   
}
