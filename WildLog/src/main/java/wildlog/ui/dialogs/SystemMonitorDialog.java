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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import org.apache.logging.log4j.Level;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import wildlog.WildLogApp;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.monitor.SystemMonitorController;
import wildlog.utils.NamedThreadFactory;


public class SystemMonitorDialog extends javax.swing.JFrame {
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
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
                pack();
                UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), SystemMonitorDialog.this);
                lblLoading.setVisible(false);
                // Setup
// TODO: Add button that, when pressed, will dump all info I can find into both the logs and an output file.
// TODO: Maak dat die x-axis mooi tyd waardes wys - nie die longs nie
// TODO: Sit ook 'n "reset" button heel bo - en maak dat die lists nie meer as bv. 100 entries wys nie
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
                // Memory
                seriesMemory = FXCollections.observableList(new ArrayList<>(3));
                seriesMemory.add(new XYChart.Series<>());
                seriesMemory.get(0).setName("Total Memory");
                seriesMemory.add(new XYChart.Series<>());
                seriesMemory.get(1).setName("WildLog Available Memory");
                seriesMemory.add(new XYChart.Series<>());
                seriesMemory.get(2).setName("WildLog Used Memory");
                controller.getCrtMemory().setData(seriesMemory);
                controller.getCrtMemory().getXAxis().setAutoRanging(true);
                // Network
// TODO: Add a droplist to select the network connection to use (can be many present)
                seriesNetwork = FXCollections.observableList(new ArrayList<>(2));
                seriesNetwork.add(new XYChart.Series<>());
                seriesNetwork.get(0).setName("Sent");
                seriesNetwork.add(new XYChart.Series<>());
                seriesNetwork.get(1).setName("Received");
                controller.getCrtNetwork().setData(seriesNetwork);
                controller.getCrtNetwork().getXAxis().setAutoRanging(true);
                // Disk
                seriesDisk = FXCollections.observableList(new ArrayList<>(2));
                seriesDisk.add(new XYChart.Series<>());
                seriesDisk.get(0).setName("Read");
                seriesDisk.add(new XYChart.Series<>());
                seriesDisk.get(1).setName("Write");
                controller.getCrtDisk().setData(seriesDisk);
                controller.getCrtDisk().getXAxis().setAutoRanging(true);
                // Database
                seriesDatabase = FXCollections.observableList(new ArrayList<>(5));
// TODO: Maak sessions eerder 'n getal onder die label - nie nodig om dit in grafiek te sien nie
//                seriesDatabase.add(new XYChart.Series<>());
//                seriesDatabase.get(0).setName("Sessions");
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
                // Start the service to load the data
                service.scheduleAtFixedRate(() -> {
                    Platform.runLater(() -> {
                        load();
                    });
                }, 1, 3, TimeUnit.SECONDS);
            }
        });
    }
    
    private void load() {
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
                (int) ((hardware.getMemory().getTotal() - hardware.getMemory().getAvailable()) / MB)));
        seriesMemory.get(1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (Runtime.getRuntime().totalMemory() / MB)));
        seriesMemory.get(2).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB)));
        // Network
// FIXME: Ek sal moet track hou van die vorige waarde en dan deur die timer se sekndes moet deel om die MB/s te kry
        seriesNetwork.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) ((hardware.getNetworkIFs()[activeNetwork].getBytesSent()) / MB)));
        seriesNetwork.get(1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) ((hardware.getNetworkIFs()[activeNetwork].getBytesRecv()) / MB)));
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

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        service.shutdown();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblLoading;
    // End of variables declaration//GEN-END:variables
}
