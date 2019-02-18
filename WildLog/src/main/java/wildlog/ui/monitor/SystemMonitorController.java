package wildlog.ui.monitor;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import wildlog.ui.dialogs.SystemMonitorDialog;

public class SystemMonitorController implements Initializable {
    private SystemMonitorDialog dialog;
    
    @FXML
    private LineChart<Long, Integer> crtProcessor;
    @FXML
    private LineChart<Long, Integer> crtMemory;
    @FXML
    private LineChart<Long, Integer> crtNetwork;
    @FXML
    private LineChart<Long, Integer> crtDisk;
    @FXML
    private LineChart<Long, Integer> crtDatabase;
    @FXML
    private Label lblConnections;
    @FXML
    private ChoiceBox cmbNetwork;
    @FXML
    private ChoiceBox cmbDisk;

    @Override
    public void initialize(URL inLocation, ResourceBundle inResources) {
    }
    
    @FXML
    private void btnResetAction(ActionEvent event) {
        dialog.btnResetAction(event);
    }
    
    @FXML
    private void btnSnapshotAction(ActionEvent event) {
        dialog.btnSnapshotAction(event);
    }
    
    public void setDialog(SystemMonitorDialog inDialog) {
        this.dialog = inDialog;
    }

    public LineChart<Long, Integer> getCrtProcessor() {
        return crtProcessor;
    }

    public void setCrtProcessor(LineChart<Long, Integer> inCrtProcessor) {
        this.crtProcessor = inCrtProcessor;
    }

    public LineChart<Long, Integer> getCrtMemory() {
        return crtMemory;
    }

    public void setCrtMemory(LineChart<Long, Integer> inCrtMemory) {
        this.crtMemory = inCrtMemory;
    }

    public LineChart<Long, Integer> getCrtNetwork() {
        return crtNetwork;
    }

    public void setCrtNetwork(LineChart<Long, Integer> inCrtNetwork) {
        this.crtNetwork = inCrtNetwork;
    }

    public LineChart<Long, Integer> getCrtDisk() {
        return crtDisk;
    }

    public void setCrtDisk(LineChart<Long, Integer> inCrtDisk) {
        this.crtDisk = inCrtDisk;
    }

    public LineChart<Long, Integer> getCrtDatabase() {
        return crtDatabase;
    }

    public void setCrtDatabase(LineChart<Long, Integer> inCrtDatabase) {
        this.crtDatabase = inCrtDatabase;
    }

    public Label getLblConnections() {
        return lblConnections;
    }

    public void setLblConnections(Label inLblConnections) {
        this.lblConnections = inLblConnections;
    }

    public ChoiceBox getCmbNetwork() {
        return cmbNetwork;
    }

    public void setCmbNetwork(ChoiceBox inCmbNetwork) {
        this.cmbNetwork = inCmbNetwork;
    }

    public ChoiceBox getCmbDisk() {
        return cmbDisk;
    }

    public void setCmbDisk(ChoiceBox inCmbDisk) {
        this.cmbDisk = inCmbDisk;
    }
    
}
