package wildlog.ui.reports.implementations.helpers;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.Scene;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;


public abstract class AbstractReport<T> {
    private final String reportButtonName;
    protected List<T> lstData;
    protected List<Node> lstCustomButtons;
    protected JLabel lblReportDescription;

    
    // TODO: Maak 'n opsie om te kies omdie totale bo op die bars te sien of nie
    // TODO: Maak opsie om te kies watse taal om te gebruik vir spesies
    // TODO: Maak opsie om te kies of die charts moet sorteer volgens alfabet of totale
    // TODO: Wanneer die chart te veel categories (creatures) is daar eintlik net twee opsies: 
    //          of laat die shart scroll, of wys niks (mens kan die font kleiner maak tot op 'n punt),
    //          want dit raak net stupid as die labels begin uitgelaat word.
    
    
    public AbstractReport(String inReportButtonName, List<T> inList, JLabel inChartDescLabel) {
        reportButtonName = inReportButtonName;
        lstData = inList;
        lblReportDescription = inChartDescLabel;
    }
    
    public abstract void createReport(Scene inScene);

    public void setupChartDescriptionLabel(String inText) {
        if (lblReportDescription != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    lblReportDescription.setText(inText);
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

    public List<Node> getLstCustomButtons() {
        return lstCustomButtons;
    }
    
}
