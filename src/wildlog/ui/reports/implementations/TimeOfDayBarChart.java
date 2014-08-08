package wildlog.ui.reports.implementations;

import java.util.List;
import javafx.scene.chart.Chart;
import wildlog.data.dataobjects.Sighting;
import wildlog.ui.reports.implementations.helpers.AbstractReport;


public class TimeOfDayBarChart extends AbstractReport<Sighting>{
    
    public TimeOfDayBarChart(String inReportButtonName, List<Sighting> inLstData) {
        super(inReportButtonName, inLstData);
    }

    @Override
    public Chart createChart(List<Sighting> inLstData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
