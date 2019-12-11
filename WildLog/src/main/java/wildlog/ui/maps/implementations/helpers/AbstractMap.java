package wildlog.ui.maps.implementations.helpers;

import java.awt.Cursor;
import java.util.ArrayList;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.utils.WildLogApplicationTypes;


public abstract class AbstractMap<T extends DataObjectWithGPS> {
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
    private String activeSubCategoryTitle = "Default Map";
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
        lblLoading.setBackground(Background.EMPTY);
        mapsBaseDialog.getJFXMapPanel().getScene().setRoot(lblLoading);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                createMap(mapsBaseDialog.getJFXMapPanel().getScene());
                // Add the watermark overlay
                applyWatermark();
                // Hide waiting cursor
                mapsBaseDialog.getGlassPane().setCursor(Cursor.getDefaultCursor());
                mapsBaseDialog.getGlassPane().setVisible(false);
            }
        });
    }
    
    protected void applyWatermark() {
        StackPane stackPane = new StackPane();
        stackPane.setStyle("-fx-padding: 0;");
        stackPane.setBackground(Background.EMPTY);
        stackPane.getChildren().add(mapsBaseDialog.getJFXMapPanel().getScene().getRoot());
        ImageView watermark;
        if (WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_ADMIN
                || WildLogApp.WILDLOG_APPLICATION_TYPE == WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
            watermark = new ImageView(new Image(WildLogApp.class.getResourceAsStream("resources/wei/WEI-square-50px.png")));
        }
        else {
            watermark = new ImageView(new Image(WildLogApp.class.getResourceAsStream("resources/icons/WildLog Map Icon.gif")));
        }
        watermark.setOpacity(0.8);
        StackPane watermarkPositionWrapper = new StackPane();
        watermarkPositionWrapper.setStyle("-fx-padding: 10;");
        watermarkPositionWrapper.setMaxSize(watermark.getFitWidth(), watermark.getFitHeight());
        watermarkPositionWrapper.getChildren().add(watermark);
        stackPane.getChildren().add(watermarkPositionWrapper);
        StackPane.setAlignment(watermarkPositionWrapper, Pos.TOP_RIGHT);
        mapsBaseDialog.getJFXMapPanel().getScene().setRoot(stackPane);
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

    public List<T> getDataList() {
        return lstData;
    }

    public List<Node> getLstCustomButtons() {
        return lstCustomButtons;
    }
    
    public void dispose() {
        // Be default do nothing...
    }
    
    /**
     * The default implementation returns the coordinates of the source DataList. 
     * Maps can overwrite this method to provide custom implementations.
     */
    public List<Object[]> getFinalMapData() {
        List<Object[]> lstFinalData = new ArrayList();
        lstFinalData.add(new Object[] {"ID", "LATITUDE", "LONGITUDE", "ACCURACY-CATEGORY", "ACCURACY"});
        for (DataObjectWithGPS gps : lstData) {
            if (UtilsGPS.hasGPSData(gps)) {
                lstFinalData.add(new Object[] {gps.getID(), UtilsGPS.getLatDecimalDegree(gps), UtilsGPS.getLonDecimalDegree(gps), gps.getGPSAccuracy(), gps.getGPSAccuracyValue()});
            }
        }
        return lstFinalData;
    }
    
}
