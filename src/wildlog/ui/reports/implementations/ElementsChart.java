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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.utils.UtilsData;
import wildlog.ui.reports.implementations.helpers.AbstractReport;
import wildlog.ui.reports.implementations.helpers.ReportDataWrapper;


public class ElementsChart extends AbstractReport<Sighting> {
    private enum ChartType {PIE_CHART};
    private ChartType chartType = ChartType.PIE_CHART;
    private Chart displayedChart;
    private JComboBox<String> cmbCategories;
    private String[] options = new String[] {"Creature Name", "Number Observed", "Sex", "Age", "Life Status", "Evidence", "Certainty", "Rating", "Info Tag"};

    
    public ElementsChart() {
        super("Creatures and Observations", "<html>This collection of charts focus on ratio of observed Creatures.</html>");
        lstCustomButtons = new ArrayList<>(2);
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
        // Piechart by category
        cmbCategories = new JComboBox<>(options);
        cmbCategories.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
        cmbCategories.setMaximumRowCount(options.length);
        cmbCategories.setFocusable(false);
        cmbCategories.setSelectedIndex(0); // Set the index before the action is added to brevent triggering it
        cmbCategories.addActionListener(new ActionListener() {
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
        lstCustomButtons.add(new JLabel("  Per Category:"));
        lstCustomButtons.add(cmbCategories);
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
        Map<String, ReportDataWrapper> mapGroupedData = new HashMap<>();
        for (Sighting sighting : inSightings) {
            String category = null;
            if (cmbCategories.getSelectedItem().equals(options[0])) {
                category = sighting.getElementName();
            }
            else
            if (cmbCategories.getSelectedItem().equals(options[1])) {
                category = Integer.toString(sighting.getNumberOfElements());
            }
            else
            if (cmbCategories.getSelectedItem().equals(options[2])) {
                category = UtilsData.stringFromObject(sighting.getSex());
            }
            else
            if (cmbCategories.getSelectedItem().equals(options[3])) {
                category = UtilsData.stringFromObject(sighting.getAge());
            }
            else
            if (cmbCategories.getSelectedItem().equals(options[4])) {
                category = UtilsData.stringFromObject(sighting.getLifeStatus());
            }
            else
            if (cmbCategories.getSelectedItem().equals(options[5])) {
                category = UtilsData.stringFromObject(sighting.getSightingEvidence());
            }
            else
            if (cmbCategories.getSelectedItem().equals(options[6])) {
                category = UtilsData.stringFromObject(sighting.getCertainty());
            }
            else
            if (cmbCategories.getSelectedItem().equals(options[7])) {
                category = UtilsData.stringFromObject(sighting.getViewRating());
            }
            else
            if (cmbCategories.getSelectedItem().equals(options[8])) {
                category = UtilsData.stringFromObject(sighting.getTag());
            }
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
        return chart;
    }

}
