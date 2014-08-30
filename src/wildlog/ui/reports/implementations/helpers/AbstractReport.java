package wildlog.ui.reports.implementations.helpers;

import java.util.List;
import javafx.scene.chart.Chart;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public abstract class AbstractReport<T> {
    private final String reportButtonName;
    protected List<T> lstData;
    protected List<JButton> lstCustomButtons;
    protected JPanel pnlChartOptions;

    public AbstractReport(String inReportButtonName) {
        reportButtonName = inReportButtonName;
    }
    
    public abstract Chart createChart();
    
    public void setupChartOptionsPanel() {
        if (pnlChartOptions != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // Remove the old buttons
                    for (int t = (pnlChartOptions.getComponentCount() - 1); t > 0; t--) {
                        pnlChartOptions.remove(t);
                    }
                    // Add the new buttons
                    if (lstCustomButtons != null) {
                        for (JButton button : lstCustomButtons) {
                            pnlChartOptions.add(button);
                        }
                    }
                    pnlChartOptions.validate();
                    pnlChartOptions.repaint();
                }
            });
        }
    }

    public String getReportButtonName() {
        return reportButtonName;
    }
    
    public void setChartOptionsPanel(JPanel inChartOptionsPanel) {
        pnlChartOptions = inChartOptionsPanel;
    }
    
    public void setDataList(List<T> inList) {
        lstData = inList;
    }
    
}
