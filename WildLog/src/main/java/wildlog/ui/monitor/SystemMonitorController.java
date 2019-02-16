package wildlog.ui.monitor;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;

public class SystemMonitorController implements Initializable {
    
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

    @Override
    public void initialize(URL inLocation, ResourceBundle inResources) {
        
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
    
}
