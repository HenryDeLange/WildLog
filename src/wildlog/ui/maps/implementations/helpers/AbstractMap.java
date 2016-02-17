package wildlog.ui.maps.implementations.helpers;

import java.awt.Cursor;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import wildlog.ui.maps.MapsBaseDialog;


public abstract class AbstractMap<T> {
    private final String mapButtonName;
    protected final MapsBaseDialog mapsBaseDialog;
    protected List<T> lstData;
    protected List<Node> lstCustomButtons;
    protected JLabel lblMapDescription;

    
    public AbstractMap(String inMapButtonName, List<T> inList, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        mapButtonName = inMapButtonName;
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

    public String getMapButtonName() {
        return mapButtonName;
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
