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
    private LineChart<Long, Integer> crtDBLocations;
    @FXML
    private LineChart<Long, Integer> crtDBVisits;
    @FXML
    private LineChart<Long, Integer> crtDBElements;
    @FXML
    private LineChart<Long, Integer> crtDBSightings;
    @FXML
    private LineChart<Long, Integer> crtDBFiles;
    @FXML
    private Label lblConnections;
    @FXML
    private ChoiceBox chbNetwork;
    @FXML
    private ChoiceBox chbDisk;

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
        dialog = inDialog;
    }

    public LineChart<Long, Integer> getCrtProcessor() {
        return crtProcessor;
    }

    public void setCrtProcessor(LineChart<Long, Integer> inCrtProcessor) {
        crtProcessor = inCrtProcessor;
    }

    public LineChart<Long, Integer> getCrtMemory() {
        return crtMemory;
    }

    public void setCrtMemory(LineChart<Long, Integer> inCrtMemory) {
        crtMemory = inCrtMemory;
    }

    public LineChart<Long, Integer> getCrtNetwork() {
        return crtNetwork;
    }

    public void setCrtNetwork(LineChart<Long, Integer> inCrtNetwork) {
        crtNetwork = inCrtNetwork;
    }

    public LineChart<Long, Integer> getCrtDisk() {
        return crtDisk;
    }

    public void setCrtDisk(LineChart<Long, Integer> inCrtDisk) {
        crtDisk = inCrtDisk;
    }

    public LineChart<Long, Integer> getCrtDBLocations() {
        return crtDBLocations;
    }

    public void setCrtDBLocations(LineChart<Long, Integer> inCrtDBLocations) {
        crtDBLocations = inCrtDBLocations;
    }

    public LineChart<Long, Integer> getCrtDBVisits() {
        return crtDBVisits;
    }

    public void setCrtDBVisits(LineChart<Long, Integer> inCrtDBVisits) {
        crtDBVisits = inCrtDBVisits;
    }

    public LineChart<Long, Integer> getCrtDBElements() {
        return crtDBElements;
    }

    public void setCrtDBElements(LineChart<Long, Integer> inCrtDBElements) {
        crtDBElements = inCrtDBElements;
    }

    public LineChart<Long, Integer> getCrtDBSightings() {
        return crtDBSightings;
    }

    public void setCrtDBSightings(LineChart<Long, Integer> inCrtDBSightings) {
        crtDBSightings = inCrtDBSightings;
    }

    public LineChart<Long, Integer> getCrtDBFiles() {
        return crtDBFiles;
    }

    public void setCrtDBFiles(LineChart<Long, Integer> inCrtDBFiles) {
        crtDBFiles = inCrtDBFiles;
    }

    public Label getLblConnections() {
        return lblConnections;
    }

    public void setLblConnections(Label inLblConnections) {
        lblConnections = inLblConnections;
    }

    public ChoiceBox getChbNetwork() {
        return chbNetwork;
    }

    public void setChbNetwork(ChoiceBox inChbNetwork) {
        chbNetwork = inChbNetwork;
    }

    public ChoiceBox getChbDisk() {
        return chbDisk;
    }

    public void setChbDisk(ChoiceBox inChbDisk) {
        chbDisk = inChbDisk;
    }
    
}
