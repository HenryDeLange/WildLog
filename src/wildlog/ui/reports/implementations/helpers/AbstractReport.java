package wildlog.ui.reports.implementations.helpers;

import java.util.List;
import javafx.scene.Parent;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public abstract class AbstractReport<T> {
    private final String reportButtonName;
    private final String reportDescription;
    protected List<T> lstData;
    protected List<JComponent> lstCustomButtons;
    protected JPanel pnlReportOptions;
    protected JLabel lblReportDescription;

    public AbstractReport(String inReportButtonName, String inReportDescription) {
        reportButtonName = inReportButtonName;
        reportDescription = inReportDescription;
    }
    
    public abstract Parent createReport();

    public void setChartOptionsPanel(JPanel inChartOptionsPanel) {
        pnlReportOptions = inChartOptionsPanel;
    }
    
    public void setupReportOptionsPanel() {
        if (pnlReportOptions != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // Remove the old buttons
                    for (int t = (pnlReportOptions.getComponentCount() - 1 - 1); t >= 0; t--) {
                        pnlReportOptions.remove(t);
                    }
                    // Add the new buttons
                    if (lstCustomButtons != null) {
                        for (JComponent button : lstCustomButtons) {
                            pnlReportOptions.add(button, pnlReportOptions.getComponentCount() - 1);
                        }
                    }
                    pnlReportOptions.validate();
                    pnlReportOptions.repaint();
                }
            });
        }
    }
    
    public void setChartDescriptionLabel(JLabel inChartDescLabel) {
        lblReportDescription = inChartDescLabel;
    }
    
    public void setupChartDescriptionLabel() {
        if (lblReportDescription != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    lblReportDescription.setText(reportDescription);
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
