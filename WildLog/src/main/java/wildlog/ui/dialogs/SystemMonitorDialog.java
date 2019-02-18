package wildlog.ui.dialogs;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javax.swing.JFrame;
import org.apache.logging.log4j.Level;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import wildlog.WildLogApp;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.monitor.SystemMonitorController;
import wildlog.utils.NamedThreadFactory;


public class SystemMonitorDialog extends JFrame {
    private final int TICK_RATE = 3; // sekondes
    private final int HISTORY_LENGTH = 40; // 3 * 40 = 2 minute
    private final JFXPanel jfxPanel;
    private SystemMonitorController controller;
    private final SystemInfo systemInfo = new SystemInfo();
    private final HardwareAbstractionLayer hardware = systemInfo.getHardware();
    private final OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("WL_SystemMonitor"));
    private ObservableList<XYChart.Series<Long, Integer>> seriesCPUs;
    private ObservableList<XYChart.Series<Long, Integer>> seriesMemory;
    private ObservableList<XYChart.Series<Long, Integer>> seriesNetwork;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDisk;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDatabase;
    private int activeNetwork = 0;
    private int activeDisk = 0;
        
    public SystemMonitorDialog() {
        WildLogApp.LOGGER.log(Level.INFO, "[SystemMonitorDialog]");
        initComponents();
        // Initialise the rest of the screen 
        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), this);
        UtilsDialog.setupGlassPaneOnMainFrame(this);
        UtilsDialog.addEscapeKeyListener(this);
        // Setup JavaFx panel
        jfxPanel = new JFXPanel();
        jfxPanel.setBackground(getBackground());
        add(jfxPanel, BorderLayout.CENTER);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    FXMLLoader FXML_LOADER = new FXMLLoader(WildLogApp.class.getResource("/wildlog/ui/monitor/SystemMonitor.fxml"));
                    Parent root = FXML_LOADER.load();
                    Scene scene = new Scene(root);
                    jfxPanel.setScene(scene);
                    controller = (SystemMonitorController) FXML_LOADER.getController();
                    controller.setDialog(SystemMonitorDialog.this);
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
                pack();
                UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), SystemMonitorDialog.this);
                lblLoading.setVisible(false);
                // Setup
// TODO: Maak dat die x-axis mooi tyd waardes wys - nie die longs nie
                // CPU
                seriesCPUs = FXCollections.observableList(new ArrayList<>(hardware.getProcessor().getLogicalProcessorCount() + 1));
                for (int t = 0; t < hardware.getProcessor().getLogicalProcessorCount() + 1; t++) {
                    seriesCPUs.add(new XYChart.Series<>());
                    if (t > 0) {
                        seriesCPUs.get(t).setName("Total CPUs");
                    }
                    else {
                        seriesCPUs.get(t).setName("CPU " + t);
                    }
                }
                controller.getCrtProcessor().setData(seriesCPUs);
                controller.getCrtProcessor().getXAxis().setAutoRanging(true);
                controller.getCrtProcessor().setLegendVisible(false);
                // Memory
                seriesMemory = FXCollections.observableList(new ArrayList<>(4));
                seriesMemory.add(new XYChart.Series<>());
                seriesMemory.get(0).setName("Total Memory");
                seriesMemory.add(new XYChart.Series<>());
                seriesMemory.get(1).setName("Total Allocated Memory");
                seriesMemory.add(new XYChart.Series<>());
                seriesMemory.get(2).setName("WildLog Allocated Memory");
                seriesMemory.add(new XYChart.Series<>());
                seriesMemory.get(3).setName("WildLog Used Memory");
                controller.getCrtMemory().setData(seriesMemory);
                controller.getCrtMemory().getXAxis().setAutoRanging(true);
                controller.getCrtMemory().setLegendVisible(false);
                // Network
// TODO: Add a droplist to select the network connection to use (can be many present)
                seriesNetwork = FXCollections.observableList(new ArrayList<>(2));
                seriesNetwork.add(new XYChart.Series<>());
                seriesNetwork.get(0).setName("Sent");
                seriesNetwork.add(new XYChart.Series<>());
                seriesNetwork.get(1).setName("Received");
                controller.getCrtNetwork().setData(seriesNetwork);
                controller.getCrtNetwork().getXAxis().setAutoRanging(true);
                controller.getCrtNetwork().setLegendVisible(false);
                // Disk
// TODO: Add a droplist to select the disk to use (can be many present)
                seriesDisk = FXCollections.observableList(new ArrayList<>(2));
                seriesDisk.add(new XYChart.Series<>());
                seriesDisk.get(0).setName("Read");
                seriesDisk.add(new XYChart.Series<>());
                seriesDisk.get(1).setName("Write");
                controller.getCrtDisk().setData(seriesDisk);
                controller.getCrtDisk().getXAxis().setAutoRanging(true);
                controller.getCrtDisk().setLegendVisible(false);
                // Database
                // NOTE: See also http://www.h2database.com/html/functions.html#memory_free
                seriesDatabase = FXCollections.observableList(new ArrayList<>(5));
                seriesDatabase.add(new XYChart.Series<>());
                seriesDatabase.get(0).setName("Places");
                seriesDatabase.add(new XYChart.Series<>());
                seriesDatabase.get(1).setName("Creatures");
                seriesDatabase.add(new XYChart.Series<>());
                seriesDatabase.get(2).setName("Periods");
                seriesDatabase.add(new XYChart.Series<>());
// TODO: Hoe om die groot verskil in grote tussen die places en observations mooi op een grafiek te sien?
                seriesDatabase.get(3).setName("Observations");
                seriesDatabase.add(new XYChart.Series<>());
                seriesDatabase.get(4).setName("Files");
                controller.getCrtDatabase().setData(seriesDatabase);
                controller.getCrtDatabase().getXAxis().setAutoRanging(true);
                controller.getCrtDatabase().setLegendVisible(false);
                // Start the service to load the data
                service.scheduleAtFixedRate(() -> {
                    Platform.runLater(() -> {
                        load();
                    });
                }, 1, TICK_RATE, TimeUnit.SECONDS);
            }
        });
    }
    
    private void load() {
        // Remove old data points
        for (XYChart.Series series : seriesCPUs) {
            if (series.getData().size() > HISTORY_LENGTH) {
                series.getData().remove(0);
            }
        }
        for (XYChart.Series series : seriesMemory) {
            if (series.getData().size() > HISTORY_LENGTH) {
                series.getData().remove(0);
            }
        }
        for (XYChart.Series series : seriesNetwork) {
            if (series.getData().size() > HISTORY_LENGTH) {
                series.getData().remove(0);
            }
        }
        for (XYChart.Series series : seriesDisk) {
            if (series.getData().size() > HISTORY_LENGTH) {
                series.getData().remove(0);
            }
        }
        for (XYChart.Series series : seriesDatabase) {
            if (series.getData().size() > HISTORY_LENGTH) {
                series.getData().remove(0);
            }
        }
        // Add new data points
        
// TODO: Maak dat mens die charts kan kliek
        
        double MB = 1024.0*1024.0;
        // CPU
        seriesCPUs.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (hardware.getProcessor().getSystemCpuLoadBetweenTicks() * 100.0)));
        for (int t = 0; t < hardware.getProcessor().getLogicalProcessorCount(); t++) {
            seriesCPUs.get(t + 1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                    (int) (hardware.getProcessor().getProcessorCpuLoadBetweenTicks()[t] * 25.0)));
        }
        // Memory
        seriesMemory.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (hardware.getMemory().getTotal() / MB)));
        seriesMemory.get(1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) ((hardware.getMemory().getTotal() - hardware.getMemory().getAvailable()) / MB)));
        seriesMemory.get(2).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (Runtime.getRuntime().totalMemory() / MB)));
        seriesMemory.get(3).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB)));
        // Network
        int prevMBs;
        if (!seriesNetwork.get(0).getData().isEmpty()) {
            prevMBs = (Integer) seriesNetwork.get(0).getData().get(seriesNetwork.get(0).getData().size() - 1).extraValueProperty().getValue();
        }
        else {
            prevMBs = 0;
        }
        int nowMBs = (int) ((hardware.getNetworkIFs()[activeNetwork].getBytesSent()) / MB);
        seriesNetwork.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (((double) nowMBs - (double) prevMBs) / (double) TICK_RATE), nowMBs));
        if (!seriesNetwork.get(1).getData().isEmpty()) {
            prevMBs = (Integer) seriesNetwork.get(1).getData().get(seriesNetwork.get(1).getData().size() - 1).extraValueProperty().getValue();
        }
        else {
            prevMBs = 0;
        }
        nowMBs = (int) ((hardware.getNetworkIFs()[activeNetwork].getBytesRecv()) / MB);
        seriesNetwork.get(1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (((double) nowMBs - (double) prevMBs) / (double) TICK_RATE), nowMBs));
        // Disk
        seriesDisk.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) ((hardware.getDiskStores()[activeDisk].getReadBytes()) / MB)));
        seriesDisk.get(1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) ((hardware.getDiskStores()[activeDisk].getWriteBytes()) / MB)));
        // Database
        seriesDatabase.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countLocations(null)));
        seriesDatabase.get(1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countElements(null, null)));
        seriesDatabase.get(2).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countVisits(null, null)));
        seriesDatabase.get(3).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countSightings(0, null, null, null)));
        seriesDatabase.get(4).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countWildLogFiles(null, null)));
        controller.getLblConnections().setText(WildLogApp.getApplication().getDBI().activeSessionsCount() + " connections");
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblLoading = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("WildLog System Monitor");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lblLoading.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblLoading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLoading.setText("Loading...");
        getContentPane().add(lblLoading, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void btnResetAction(ActionEvent event) {
        for (XYChart.Series series : seriesCPUs) {
            series.getData().clear();
        }
        for (XYChart.Series series : seriesMemory) {
            series.getData().clear();
        }
        for (XYChart.Series series : seriesNetwork) {
            series.getData().clear();
        }
        for (XYChart.Series series : seriesDisk) {
            series.getData().clear();
        }
        for (XYChart.Series series : seriesDatabase) {
            series.getData().clear();
        }
    }
    
    public void btnSnapshotAction(ActionEvent event) {
// TODO: Print ALL info into the logs and a new file
    }
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        service.shutdown();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblLoading;
    // End of variables declaration//GEN-END:variables
}
