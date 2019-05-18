package wildlog.ui.charts.implementations.helpers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import wildlog.ui.charts.utils.UtilsCharts;


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
                UtilsCharts.displayLabelForDataToRight(data);
            }
        }
    }
}
