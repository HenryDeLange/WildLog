package wildlog.ui.reports.implementations.helpers;

import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import wildlog.ui.reports.ReportsBaseDialog;


public abstract class AbstractReport<T> {
    protected static final ToggleGroup BUTTON_GROUP = new ToggleGroup();
    static {
        // Make sure the button stays selected when pressing it again if already selected
        BUTTON_GROUP.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> value, Toggle oldToggle, Toggle newToggle) {
                if ((newToggle == null)) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            BUTTON_GROUP.selectToggle(oldToggle);
                        }
                    });
                }
            }
        });
    }
    private final String reportCategoryTitle;
    private String activeSubCategoryTitle = "Default Report";
    protected final ReportsBaseDialog reportsBaseDialog;
    protected List<T> lstData;
    protected List<Node> lstCustomButtons;
    protected JLabel lblReportDescription;

    
    public AbstractReport(String inReportCategoryTitle, List<T> inList, JLabel inChartDescLabel, ReportsBaseDialog inReportsBaseDialog) {
        reportCategoryTitle = inReportCategoryTitle;
        lstData = inList;
        lblReportDescription = inChartDescLabel;
        reportsBaseDialog = inReportsBaseDialog;
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

    public String getReportCategoryTitle() {
        return reportCategoryTitle;
    }

    public String getActiveSubCategoryTitle() {
        return activeSubCategoryTitle;
    }

    public void setActiveSubCategoryTitle(String inActiveSubCategoryTitle) {
        activeSubCategoryTitle = inActiveSubCategoryTitle;
    }
    
    public void setDataList(List<T> inList) {
        lstData = inList;
    }

    public List<Node> getLstCustomButtons() {
        return lstCustomButtons;
    }
    
}
