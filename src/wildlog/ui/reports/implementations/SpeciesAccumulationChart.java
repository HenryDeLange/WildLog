package wildlog.ui.reports.implementations;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;
import javax.swing.JButton;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.utils.UtilsTime;


public class SpeciesAccumulationChart extends AbstractReport<Sighting> {
    private enum ChartType {LINE_CHART};
    private ChartType chartType = ChartType.LINE_CHART;
    private Chart displayedChart;

    
    public SpeciesAccumulationChart() {
        super("Creature Accumulation", "<html>This collection of charts focus on the rate at wich new Creatures were recorded.</html>");
        lstCustomButtons = new ArrayList<>(1);
        // Area/Line Chart
        JButton btnLineChart = new JButton("Line Chart All");
        btnLineChart.setFocusPainted(false);
        btnLineChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLineChart.setMargin(new Insets(2, 4, 2, 4));
        btnLineChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.LINE_CHART;
                if (displayedChart != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            displayedChart.getScene().setRoot(createChart());
                        }
                    });
                }
            }
        });
        lstCustomButtons.add(btnLineChart);
    }

    @Override
    public Chart createChart() {
        displayedChart = null;
        if (chartType.equals(ChartType.LINE_CHART)) {
            displayedChart = createChart(lstData);
        }
        return displayedChart;
    }
    
    private Chart createChart(List<Sighting> inSightings) {
        NumberAxis axisY = new NumberAxis();
        axisY.setLabel("Number of Creatures");
        axisY.setAutoRanging(true);
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
            lstChartData.add(new AreaChart.Data<>(endTime, counter++, elementName));
            if (startTime == 0) {
                startTime = endTime;
            }
        }
        ObservableList<AreaChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
        chartData.add(new AreaChart.Series<>("All Creatures", lstChartData));
        NumberAxis axisX = new NumberAxis(startTime, endTime, (endTime - startTime)/7);
        axisX.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return new SimpleDateFormat("dd MMM yyyy").format(object);
            }
            @Override
            public Number fromString(String string) {
                try {
                    return new SimpleDateFormat("dd MMM yyyy").parse(string).getTime();
                }
                catch (ParseException ex) {
                    ex.printStackTrace(System.err);
                }
                return 0;
            }
        });
        AreaChart<Number, Number> chart = new AreaChart<Number, Number>(axisX, axisY, chartData);
        return chart;
    }
   
}
