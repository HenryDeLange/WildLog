package wildlog.ui.reports.implementations;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.Chart;
import javafx.scene.chart.PieChart;
import javax.swing.JButton;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;


public class LocationChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART};
    private ChartType chartType = ChartType.PIE_CHART;
    private Chart displayedChart;

    
    public LocationChart() {
        super("Place Composition", "<html>This collection of charts focus on ratio of observations at Places.</html>");
        lstCustomButtons = new ArrayList<>(3);
        // Area/Line Chart
        JButton btnPieChart = new JButton("Pie Chart All");
        btnPieChart.setFocusPainted(false);
        btnPieChart.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPieChart.setMargin(new Insets(2, 4, 2, 4));
        btnPieChart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartType = ChartType.PIE_CHART;
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
        lstCustomButtons.add(btnPieChart);
    }

    @Override
    public Chart createChart() {
        displayedChart = null;
        if (chartType.equals(ChartType.PIE_CHART)) {
            displayedChart = createPieChart(lstData);
        }
        return displayedChart;
    }
    
    private Chart createPieChart(List<Sighting> inSightings) {
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>(4);
        for (Sighting sighting : inSightings) {
            ReportDataWrapper dataWrapper = mapGroupedData.get(sighting.getLocationName());
            if (dataWrapper == null) {
                mapGroupedData.put(sighting.getLocationName(), new ReportDataWrapper("", "", 1));
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

}
