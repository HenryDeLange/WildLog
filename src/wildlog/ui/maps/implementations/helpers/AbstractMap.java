package wildlog.ui.maps.implementations.helpers;

import java.awt.Cursor;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import wildlog.ui.maps.MapsBaseDialog;


public abstract class AbstractMap<T> {
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
    private final String mapCategoryTitle;
    private String activeSubCategoryTitle = "Default Report";
    protected final MapsBaseDialog mapsBaseDialog;
    protected List<T> lstData;
    protected List<Node> lstCustomButtons;
    protected JLabel lblMapDescription;

    
    public AbstractMap(String inMapButtonName, List<T> inList, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        mapCategoryTitle = inMapButtonName;
        lstData = inList;
        lblMapDescription = inChartDescLabel;
        mapsBaseDialog = inMapsBaseDialog;
    }
    
    public void loadMap() {
        // Setup waiting cursor
        mapsBaseDialog.getGlassPane().setVisible(true);
        mapsBaseDialog.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // Setup loading label
        final Label lblLoading = new Label("... LOADING ...");
        lblLoading.setPadding(new Insets(20));
        lblLoading.setFont(new Font(24));
        lblLoading.setTextAlignment(TextAlignment.CENTER);
        lblLoading.setAlignment(Pos.CENTER);
        mapsBaseDialog.getJFXMapPanel().getScene().setRoot(lblLoading);
        createMap(mapsBaseDialog.getJFXMapPanel().getScene());
        // Hide waiting cursor
        mapsBaseDialog.getGlassPane().setVisible(false);
        mapsBaseDialog.getGlassPane().setCursor(Cursor.getDefaultCursor());
    }
    
    public abstract void createMap(Scene inScene);

    public void setupChartDescriptionLabel(String inText) {
        if (lblMapDescription != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    lblMapDescription.setText(inText);
                }
            });
        }
    }

    public String getMapCategoryTitle() {
        return mapCategoryTitle;
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
    
    public void dispose() {
        // Be default do nothing...
    }
    
}
