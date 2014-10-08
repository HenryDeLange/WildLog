package wildlog.ui.reports.implementations;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.chart.Chart;
import javafx.scene.layout.Background;
import javax.swing.JButton;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.implementations.helpers.AbstractReport;


public class SightingStatsChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART};
    private ChartType chartType = ChartType.PIE_CHART;
    private Chart displayedChart;

    
    
    
    // TODO: Daai lys van charts wat ek vir tygerberg gedoen het wat die aantal spesies per dag/kans om 'n spesie weer te sien per dag/ens. wys
    
    
    
    
    public SightingStatsChart() {
        super("Creature Composition", "<html>This collection of charts focus on ratio of observed Creatures.</html>");
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
        displayedChart.setBackground(Background.EMPTY);
        return displayedChart;
    }
    
    private Chart createPieChart(List<Sighting> inSightings) {
        
        
        return null;
    }

}
