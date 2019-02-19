package wildlog.ui.dialogs;

import java.awt.BorderLayout;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
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
    private ObservableList<XYChart.Series<Long, Integer>> seriesDBLocations;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDBVisits;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDBElements;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDBSightings;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDBFiles;
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
                NumberAxis dateAxis = (NumberAxis) (Axis) controller.getCrtProcessor().getXAxis();
                dateAxis.setAutoRanging(true);
                dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
                    private final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
                    @Override
                    public String toString(Number object) {
                        long milliSeconds = object.longValue();
                        if (milliSeconds > 0) {
                            LocalDateTime time = Instant.ofEpochMilli(milliSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            return TIMEFORMAT.format(time);
                        }
                        return "";
                    }
                    @Override
                    public Number fromString(String string) {
                        return null;
                    }
                });
                dateAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 10));
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
                dateAxis = (NumberAxis) (Axis) controller.getCrtMemory().getXAxis();
                dateAxis.setAutoRanging(true);
                dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
                    private final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
                    @Override
                    public String toString(Number object) {
                        long milliSeconds = object.longValue();
                        if (milliSeconds > 0) {
                            LocalDateTime time = Instant.ofEpochMilli(milliSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            return TIMEFORMAT.format(time);
                        }
                        return "";
                    }
                    @Override
                    public Number fromString(String string) {
                        return null;
                    }
                });
                dateAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 10));
                controller.getCrtMemory().setLegendVisible(false);
                // Network
// TODO: Add a droplist to select the network connection to use (can be many present)
                seriesNetwork = FXCollections.observableList(new ArrayList<>(2));
                seriesNetwork.add(new XYChart.Series<>());
                seriesNetwork.get(0).setName("Sent");
                seriesNetwork.add(new XYChart.Series<>());
                seriesNetwork.get(1).setName("Received");
                controller.getCrtNetwork().setData(seriesNetwork);
                dateAxis = (NumberAxis) (Axis) controller.getCrtNetwork().getXAxis();
                dateAxis.setAutoRanging(true);
                dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
                    private final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
                    @Override
                    public String toString(Number object) {
                        long milliSeconds = object.longValue();
                        if (milliSeconds > 0) {
                            LocalDateTime time = Instant.ofEpochMilli(milliSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            return TIMEFORMAT.format(time);
                        }
                        return "";
                    }
                    @Override
                    public Number fromString(String string) {
                        return null;
                    }
                });
                dateAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 10));
                controller.getCrtNetwork().setLegendVisible(false);
                // Disk
// TODO: Add a droplist to select the disk to use (can be many present)
                seriesDisk = FXCollections.observableList(new ArrayList<>(2));
                seriesDisk.add(new XYChart.Series<>());
                seriesDisk.get(0).setName("Read");
                seriesDisk.add(new XYChart.Series<>());
                seriesDisk.get(1).setName("Write");
                controller.getCrtDisk().setData(seriesDisk);
                dateAxis = (NumberAxis) (Axis) controller.getCrtDisk().getXAxis();
                dateAxis.setAutoRanging(true);
                dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
                    private final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
                    @Override
                    public String toString(Number object) {
                        long milliSeconds = object.longValue();
                        if (milliSeconds > 0) {
                            LocalDateTime time = Instant.ofEpochMilli(milliSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            return TIMEFORMAT.format(time);
                        }
                        return "";
                    }
                    @Override
                    public Number fromString(String string) {
                        return null;
                    }
                });
                dateAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 10));
                controller.getCrtDisk().setLegendVisible(false);
                // Database
                // NOTE: See also http://www.h2database.com/html/functions.html#memory_free
                seriesDBLocations = FXCollections.observableList(new ArrayList<>(5));
                seriesDBLocations.add(new XYChart.Series<>());
                seriesDBLocations.get(0).setName("Places");
                seriesDBVisits = FXCollections.observableList(new ArrayList<>(5));
                seriesDBVisits.add(new XYChart.Series<>());
                seriesDBVisits.get(0).setName("Periods");
                seriesDBElements = FXCollections.observableList(new ArrayList<>(5));
                seriesDBElements.add(new XYChart.Series<>());
                seriesDBElements.get(0).setName("Creatures");
                seriesDBSightings = FXCollections.observableList(new ArrayList<>(5));
                seriesDBSightings.add(new XYChart.Series<>());
                seriesDBSightings.get(0).setName("Observations");
                seriesDBFiles = FXCollections.observableList(new ArrayList<>(5));
                seriesDBFiles.add(new XYChart.Series<>());
                seriesDBFiles.get(0).setName("Files");
                controller.getCrtDBLocations().setData(seriesDBLocations);
                dateAxis = (NumberAxis) (Axis) controller.getCrtDBLocations().getXAxis();
                dateAxis.setAutoRanging(true);
                dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
                    private final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
                    @Override
                    public String toString(Number object) {
                        long milliSeconds = object.longValue();
                        if (milliSeconds > 0) {
                            LocalDateTime time = Instant.ofEpochMilli(milliSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            return TIMEFORMAT.format(time);
                        }
                        return "";
                    }
                    @Override
                    public Number fromString(String string) {
                        return null;
                    }
                });
                dateAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 10));
                controller.getCrtDBLocations().setLegendVisible(false);
                controller.getCrtDBVisits().setData(seriesDBVisits);
                dateAxis = (NumberAxis) (Axis) controller.getCrtDBVisits().getXAxis();
                dateAxis.setAutoRanging(true);
                dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
                    private final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
                    @Override
                    public String toString(Number object) {
                        long milliSeconds = object.longValue();
                        if (milliSeconds > 0) {
                            LocalDateTime time = Instant.ofEpochMilli(milliSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            return TIMEFORMAT.format(time);
                        }
                        return "";
                    }
                    @Override
                    public Number fromString(String string) {
                        return null;
                    }
                });
                dateAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 10));
                controller.getCrtDBVisits().setLegendVisible(false);
                controller.getCrtDBElements().setData(seriesDBElements);
                dateAxis = (NumberAxis) (Axis) controller.getCrtDBElements().getXAxis();
                dateAxis.setAutoRanging(true);
                dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
                    private final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
                    @Override
                    public String toString(Number object) {
                        long milliSeconds = object.longValue();
                        if (milliSeconds > 0) {
                            LocalDateTime time = Instant.ofEpochMilli(milliSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            return TIMEFORMAT.format(time);
                        }
                        return "";
                    }
                    @Override
                    public Number fromString(String string) {
                        return null;
                    }
                });
                dateAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 10));
                controller.getCrtDBElements().setLegendVisible(false);
                controller.getCrtDBSightings().setData(seriesDBSightings);
                dateAxis = (NumberAxis) (Axis) controller.getCrtDBSightings().getXAxis();
                dateAxis.setAutoRanging(true);
                dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
                    private final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
                    @Override
                    public String toString(Number object) {
                        long milliSeconds = object.longValue();
                        if (milliSeconds > 0) {
                            LocalDateTime time = Instant.ofEpochMilli(milliSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            return TIMEFORMAT.format(time);
                        }
                        return "";
                    }
                    @Override
                    public Number fromString(String string) {
                        return null;
                    }
                });
                dateAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 10));
                controller.getCrtDBSightings().setLegendVisible(false);
                controller.getCrtDBFiles().setData(seriesDBFiles);
                dateAxis = (NumberAxis) (Axis) controller.getCrtDBFiles().getXAxis();
                dateAxis.setAutoRanging(true);
                dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
                    private final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
                    @Override
                    public String toString(Number object) {
                        long milliSeconds = object.longValue();
                        if (milliSeconds > 0) {
                            LocalDateTime time = Instant.ofEpochMilli(milliSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
                            return TIMEFORMAT.format(time);
                        }
                        return "";
                    }
                    @Override
                    public Number fromString(String string) {
                        return null;
                    }
                });
                dateAxis.setTickLabelFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 10));
                controller.getCrtDBFiles().setLegendVisible(false);
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
        for (XYChart.Series series : seriesDBLocations) {
            if (series.getData().size() > HISTORY_LENGTH) {
                series.getData().remove(0);
            }
        }
        for (XYChart.Series series : seriesDBVisits) {
            if (series.getData().size() > HISTORY_LENGTH) {
                series.getData().remove(0);
            }
        }
        for (XYChart.Series series : seriesDBElements) {
            if (series.getData().size() > HISTORY_LENGTH) {
                series.getData().remove(0);
            }
        }
        for (XYChart.Series series : seriesDBSightings) {
            if (series.getData().size() > HISTORY_LENGTH) {
                series.getData().remove(0);
            }
        }
        for (XYChart.Series series : seriesDBFiles) {
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
        if (!seriesDisk.get(0).getData().isEmpty()) {
            prevMBs = (Integer) seriesDisk.get(0).getData().get(seriesDisk.get(0).getData().size() - 1).extraValueProperty().getValue();
        }
        else {
            prevMBs = 0;
        }
        nowMBs = (int) ((hardware.getDiskStores()[activeDisk].getReadBytes()) / MB);
        seriesDisk.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (((double) nowMBs - (double) prevMBs) / (double) TICK_RATE), nowMBs));
        if (!seriesDisk.get(1).getData().isEmpty()) {
            prevMBs = (Integer) seriesDisk.get(1).getData().get(seriesDisk.get(1).getData().size() - 1).extraValueProperty().getValue();
        }
        else {
            prevMBs = 0;
        }
        nowMBs = (int) ((hardware.getDiskStores()[activeDisk].getWriteBytes()) / MB);
        seriesDisk.get(1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (((double) nowMBs - (double) prevMBs) / (double) TICK_RATE), nowMBs));
        // Database
        seriesDBLocations.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countLocations(null)));
        seriesDBVisits.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countVisits(null, null)));
        seriesDBElements.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countElements(null, null)));
        seriesDBSightings.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countSightings(0, null, null, null)));
        seriesDBFiles.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
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
        for (XYChart.Series series : seriesDBLocations) {
            series.getData().clear();
        }
        for (XYChart.Series series : seriesDBVisits) {
            series.getData().clear();
        }
        for (XYChart.Series series : seriesDBElements) {
            series.getData().clear();
        }
        for (XYChart.Series series : seriesDBSightings) {
            series.getData().clear();
        }
        for (XYChart.Series series : seriesDBFiles) {
            series.getData().clear();
        }
    }
    
    public void btnSnapshotAction(ActionEvent event) {
// TODO: Print ALL info into the logs and a new file + save a JavaFx screenshot
    }
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        service.shutdown();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblLoading;
    // End of variables declaration//GEN-END:variables
}
