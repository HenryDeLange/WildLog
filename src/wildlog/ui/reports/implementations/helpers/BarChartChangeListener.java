package wildlog.ui.reports.implementations.helpers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import wildlog.ui.reports.utils.UtilsReports;


public class BarChartChangeListener<T extends Node> implements ChangeListener<Node> {
    private final int totalNumberOfBars;
    private final XYChart.Data<String, Number> data;

    public BarChartChangeListener(int inTotalNumberOfBars, XYChart.Data<String, Number> inData) {
        totalNumberOfBars = inTotalNumberOfBars;
        data = inData;
    }
    
    @Override
    public void changed(ObservableValue<? extends Node> ov, Node oldNode, Node newNode) {
        if (newNode != null) {
            // Add the total at the top
            if (totalNumberOfBars < 30) {
                UtilsReports.displayLabelForDataOnTop(data);
            }
        }
    }
}
