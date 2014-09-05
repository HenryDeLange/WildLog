package wildlog.ui.reports.implementations.helpers;

import java.util.List;
import javafx.scene.chart.Chart;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public abstract class AbstractReport<T> {
    private final String reportButtonName;
    private final String reportDescription;
    protected List<T> lstData;
    protected List<JComponent> lstCustomButtons;
    protected JPanel pnlChartOptions;
    protected JLabel lblChartDescription;

    public AbstractReport(String inReportButtonName, String inReportDescription) {
        reportButtonName = inReportButtonName;
        reportDescription = inReportDescription;
    }
    
    public abstract Chart createChart();

    public void setChartOptionsPanel(JPanel inChartOptionsPanel) {
        pnlChartOptions = inChartOptionsPanel;
    }
    
    public void setupChartOptionsPanel() {
        if (pnlChartOptions != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // Remove the old buttons
                    for (int t = (pnlChartOptions.getComponentCount() - 1 - 1); t >= 0; t--) {
                        pnlChartOptions.remove(t);
                    }
                    // Add the new buttons
                    if (lstCustomButtons != null) {
                        for (JComponent button : lstCustomButtons) {
                            pnlChartOptions.add(button, pnlChartOptions.getComponentCount() - 1);
                        }
                    }
                    pnlChartOptions.validate();
                    pnlChartOptions.repaint();
                }
            });
        }
    }
    
    public void setChartDescriptionLabel(JLabel inChartDescLabel) {
        lblChartDescription = inChartDescLabel;
    }
    
    public void setupChartDescriptionLabel() {
        if (lblChartDescription != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    lblChartDescription.setText(reportDescription);
                }
            });
        }
    }

    public String getReportButtonName() {
        return reportButtonName;
    }
    
    public void setDataList(List<T> inList) {
        lstData = inList;
    }
    
}
