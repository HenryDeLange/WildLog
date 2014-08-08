package wildlog.ui.reports.implementations.helpers;

import java.util.List;
import javafx.scene.chart.Chart;


public abstract class AbstractReport<T> {
    private String reportButtonName;
    private List<T> lstData;

    public AbstractReport(String inReportButtonName, List<T> inLstData) {
        reportButtonName = inReportButtonName;
        lstData = inLstData;
    }
    
    public abstract Chart createChart(List<T> inLstData);
    
}
