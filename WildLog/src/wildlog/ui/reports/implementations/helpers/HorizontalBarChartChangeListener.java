package wildlog.ui.reports.implementations.helpers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import wildlog.ui.reports.utils.UtilsReports;


public class HorizontalBarChartChangeListener<T extends Node> implements ChangeListener<Node> {
    private final int totalNumberOfBars;
    private final XYChart.Data<Number, String> data;

    public HorizontalBarChartChangeListener(int inTotalNumberOfBars, XYChart.Data<Number, String> inData) {
        totalNumberOfBars = inTotalNumberOfBars;
        data = inData;
    }
    
    @Override
    public void changed(ObservableValue<? extends Node> ov, Node oldNode, Node newNode) {
        if (newNode != null) {
            // Add the total at the top
            if (totalNumberOfBars < 30) {
                UtilsReports.displayLabelForDataToRight(data);
            }
        }
    }
}
