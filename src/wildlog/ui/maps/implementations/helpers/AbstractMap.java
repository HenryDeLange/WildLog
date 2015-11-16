package wildlog.ui.maps.implementations.helpers;

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


public abstract class AbstractMap<T> {
    private final String mapButtonName;
    protected List<T> lstData;
    protected List<Node> lstCustomButtons;
    protected JLabel lblMapDescription;

    
    public AbstractMap(String inMapButtonName, List<T> inList, JLabel inChartDescLabel) {
        mapButtonName = inMapButtonName;
        lstData = inList;
        lblMapDescription = inChartDescLabel;
    }
    
    public void loadMap(Scene inScene) {
        // Setup loading label
        final Label lblLoading = new Label("... LOADING ...");
        lblLoading.setPadding(new Insets(20));
        lblLoading.setFont(new Font(24));
        lblLoading.setTextAlignment(TextAlignment.CENTER);
        lblLoading.setAlignment(Pos.CENTER);
        inScene.setRoot(lblLoading);
        createMap(inScene);
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
    
}
