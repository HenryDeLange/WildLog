package wildlog.ui.maps.implementations.helpers;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.Scene;
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
