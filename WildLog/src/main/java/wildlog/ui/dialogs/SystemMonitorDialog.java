package wildlog.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.apache.logging.log4j.Level;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.Display;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.PowerSource;
import oshi.hardware.SoundCard;
import oshi.hardware.UsbDevice;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import wildlog.WildLogApp;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.monitor.SystemMonitorController;
import wildlog.ui.reports.utils.UtilsReports;
import wildlog.ui.utils.UtilsTime;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogPaths;


public class SystemMonitorDialog extends JFrame {
    private final double MB = 1024.0*1024.0;
    private final int TICK_RATE = 3; // sekondes
    private final int HISTORY_LENGTH = 40; // 3 * 40 = 2 minute
    private final JFXPanel jfxPanel;
    private SystemMonitorController controller;
    private final SystemInfo systemInfo = new SystemInfo();
    private final HardwareAbstractionLayer hardware = systemInfo.getHardware();
    private final OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
    private final ScheduledExecutorService tickService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("WL_SystemMonitor_Tick"));
    private final ExecutorService snapshotService = Executors.newSingleThreadExecutor(new NamedThreadFactory("WL_SystemMonitor_Snapshot"));
    private ObservableList<XYChart.Series<Long, Integer>> seriesCPUs;
    private ObservableList<XYChart.Series<Long, Integer>> seriesMemory;
    private ObservableList<XYChart.Series<Long, Integer>> seriesNetwork;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDisk;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDBLocations;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDBVisits;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDBElements;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDBSightings;
    private ObservableList<XYChart.Series<Long, Integer>> seriesDBFiles;
        
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
                tickService.scheduleAtFixedRate(() -> {
                    Platform.runLater(() -> {
                        load();
                    });
                }, 1, TICK_RATE, TimeUnit.SECONDS);
                // Doen die eerste load datelik (en dan 1 sekonde later sal die eerste tick gebeur)
                btnResetAction(null); // Om die droplists op te stel
                load();
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
        // CPU
        seriesCPUs.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (hardware.getProcessor().getSystemCpuLoadBetweenTicks() * 100.0)));
        for (int t = 0; t < hardware.getProcessor().getLogicalProcessorCount(); t++) {
            seriesCPUs.get(t + 1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                    (int) (hardware.getProcessor().getProcessorCpuLoadBetweenTicks()[t] * 25.0)));
        }
        UtilsReports.setupChartTooltips(controller.getCrtProcessor(), true, true, false, false, true, seriesCPUs.get(0).getData().size() - 1);
        // Memory
        seriesMemory.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (hardware.getMemory().getTotal() / MB)));
        seriesMemory.get(1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) ((hardware.getMemory().getTotal() - hardware.getMemory().getAvailable()) / MB)));
        seriesMemory.get(2).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (Runtime.getRuntime().totalMemory() / MB)));
        seriesMemory.get(3).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB)));
        UtilsReports.setupChartTooltips(controller.getCrtMemory(), true, true, false, false, true, seriesMemory.get(0).getData().size() - 1);
        // Network - Sent
        int activeNetwork = controller.getChbNetwork().getSelectionModel().getSelectedIndex();
        int nowMBs = 0;
        if (activeNetwork < hardware.getNetworkIFs().length) {
            nowMBs = (int) ((hardware.getNetworkIFs()[activeNetwork].getBytesSent()) / MB);
        }
        int prevMBs = 0;
        if (!seriesNetwork.get(0).getData().isEmpty()) {
            prevMBs = (Integer) seriesNetwork.get(0).getData().get(seriesNetwork.get(0).getData().size() - 1).extraValueProperty().getValue();
        }
        else {
            prevMBs = nowMBs;
        }
        seriesNetwork.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (((double) nowMBs - (double) prevMBs) / (double) TICK_RATE), nowMBs));
        // Network - Received
        nowMBs = 0;
        if (activeNetwork < hardware.getNetworkIFs().length) {
            nowMBs = (int) ((hardware.getNetworkIFs()[activeNetwork].getBytesRecv()) / MB);
        }
        if (!seriesNetwork.get(1).getData().isEmpty()) {
            prevMBs = (Integer) seriesNetwork.get(1).getData().get(seriesNetwork.get(1).getData().size() - 1).extraValueProperty().getValue();
        }
        else {
            prevMBs = nowMBs;
        }
        seriesNetwork.get(1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (((double) nowMBs - (double) prevMBs) / (double) TICK_RATE), nowMBs));
        UtilsReports.setupChartTooltips(controller.getCrtNetwork(), true, true, false, false, true, seriesNetwork.get(0).getData().size() - 1);
        // Disk - Write
        int activeDisk = controller.getChbDisk().getSelectionModel().getSelectedIndex();
        nowMBs = 0;
        if (activeDisk < hardware.getDiskStores().length) {
            nowMBs = (int) ((hardware.getDiskStores()[activeDisk].getWriteBytes()) / MB);
        }
        if (!seriesDisk.get(0).getData().isEmpty()) {
            prevMBs = (Integer) seriesDisk.get(0).getData().get(seriesDisk.get(0).getData().size() - 1).extraValueProperty().getValue();
        }
        else {
            prevMBs = nowMBs;
        }
        seriesDisk.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (((double) nowMBs - (double) prevMBs) / (double) TICK_RATE), nowMBs));
        // Disk - Read
        nowMBs = 0;
        if (activeDisk < hardware.getDiskStores().length) {
            nowMBs = (int) ((hardware.getDiskStores()[activeDisk].getReadBytes()) / MB);
        }
        if (!seriesDisk.get(1).getData().isEmpty()) {
            prevMBs = (Integer) seriesDisk.get(1).getData().get(seriesDisk.get(1).getData().size() - 1).extraValueProperty().getValue();
        }
        else {
            prevMBs = nowMBs;
        }
        seriesDisk.get(1).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                (int) (((double) nowMBs - (double) prevMBs) / (double) TICK_RATE), nowMBs));
        UtilsReports.setupChartTooltips(controller.getCrtDisk(), true, true, false, false, true, seriesDisk.get(0).getData().size() - 1);
        // Database
        seriesDBLocations.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countLocations(null)));
        UtilsReports.setupChartTooltips(controller.getCrtDBLocations(), true, true, false, false, true, seriesDBLocations.get(0).getData().size() - 1);
        seriesDBVisits.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countVisits(null, null)));
        UtilsReports.setupChartTooltips(controller.getCrtDBVisits(), true, true, false, false, true, seriesDBVisits.get(0).getData().size() - 1);
        seriesDBElements.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countElements(null, null)));
        UtilsReports.setupChartTooltips(controller.getCrtDBElements(), true, true, false, false, true, seriesDBElements.get(0).getData().size() - 1);
        seriesDBSightings.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countSightings(0, null, null, null)));
        UtilsReports.setupChartTooltips(controller.getCrtDBSightings(), true, true, false, false, true, seriesDBSightings.get(0).getData().size() - 1);
        seriesDBFiles.get(0).getData().add(new XYChart.Data<>(System.currentTimeMillis(), 
                WildLogApp.getApplication().getDBI().countWildLogFiles(null, null)));
        UtilsReports.setupChartTooltips(controller.getCrtDBFiles(), true, true, false, false, true, seriesDBFiles.get(0).getData().size() - 1);
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
        setIconImage(new ImageIcon(WildLogApp.getApplication().getClass().getResource("resources/icons/WildLog Icon Selected.gif")).getImage());
        setMinimumSize(new java.awt.Dimension(350, 200));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lblLoading.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblLoading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLoading.setText("Loading...");
        getContentPane().add(lblLoading, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void btnResetAction(ActionEvent event) {
        // Reset the data series
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
        // Reconfigure the droplists
        // Network
        controller.getChbNetwork().getItems().clear();
        int selectedNetworkIndex = 0;
        long selectedNetworkUsage = -1;
        for (int t = 0; t < hardware.getNetworkIFs().length; t++) {
            NetworkIF network = hardware.getNetworkIFs()[t];
            controller.getChbNetwork().getItems().add(network.getDisplayName() + " - " + network.getName());
            if (selectedNetworkUsage < (network.getBytesRecv() + network.getBytesSent())) {
                selectedNetworkIndex = t;
                selectedNetworkUsage = network.getBytesRecv() + network.getBytesSent();
            }
        }
        controller.getChbNetwork().getSelectionModel().select(selectedNetworkIndex);
        // Disk
        controller.getChbDisk().getItems().clear();
        int selectedDiskIndex = 0;
        String selectedDiskName = null;
        for (int t = 0; t < hardware.getDiskStores().length; t++) {
            HWDiskStore disk = hardware.getDiskStores()[t];
            controller.getChbDisk().getItems().add(disk.getModel() + " - " + disk.getName());
            if (selectedDiskName == null || selectedDiskName.compareTo(disk.getName()) > 0) {
                selectedDiskIndex = t;
                selectedDiskName = disk.getName();
            }
        }
        controller.getChbDisk().getSelectionModel().select(selectedDiskIndex);
    }
    
    public void btnSnapshotAction(ActionEvent event) {
        snapshotService.submit(() -> {
            String dateString = UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(LocalDateTime.now());
            Path path = WildLogPaths.WILDLOG_EXPORT_SYSTEM_MONITOR.getAbsoluteFullPath();
            try {
                Files.createDirectories(path);
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            // Save screenshot
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        WildLogApp.LOGGER.log(Level.INFO, "Taking screenshot...");
                        WritableImage writableImage = jfxPanel.getScene().snapshot(null);
                        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        Path filePath = path.resolve("Snapshot [" + dateString + "].png");
                        Files.createDirectories(filePath.getParent());
                        ImageIO.write(bufferedImage, "png", filePath.toFile());
                        WildLogApp.LOGGER.log(Level.INFO, "Screenshot saved");
                    }
                    catch (Exception ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                }
            });
            // Writer the text data to the logs and a seperate file
            Path filePath = path.resolve("Snapshot [" + dateString + "].txt");
            try (PrintWriter feedback = new PrintWriter(new FileWriter(filePath.toFile()), true)) {
                write(feedback, "**********************************", "");
                write(feedback, "***** WildLog System Monitor *****", "");
                write(feedback, "**********************************", "");
                write(feedback, "", "");
                write(feedback, "Date: ", dateString);
                // Save operating system data
                write(feedback, "", "");
                write(feedback, "***** Operating System *****", "");
                write(feedback, "Manufacturer: ", operatingSystem.getManufacturer());
                write(feedback, "Family      : ", operatingSystem.getFamily());
                write(feedback, "Version     : ", operatingSystem.getVersion().getVersion());
                write(feedback, "CodeName    : ", operatingSystem.getVersion().getCodeName());
                write(feedback, "BuildNumber : ", operatingSystem.getVersion().getBuildNumber());
                write(feedback, "Bitness     : ", operatingSystem.getBitness());
                // Save OSHI data
                write(feedback, "", "");
                write(feedback, "***** Computer System *****", "");
                write(feedback, "Manufacturer: ", hardware.getComputerSystem().getManufacturer());
                write(feedback, "Model       : ", hardware.getComputerSystem().getModel());
                write(feedback, "SerialNumber: ", hardware.getComputerSystem().getSerialNumber());
                write(feedback, ">>> Computer System >>> Baseboard", "");
                write(feedback, "Manufacturer: ", hardware.getComputerSystem().getBaseboard().getManufacturer());
                write(feedback, "Model       : ", hardware.getComputerSystem().getBaseboard().getModel());
                write(feedback, "SerialNumber: ", hardware.getComputerSystem().getBaseboard().getSerialNumber());
                write(feedback, "Version     : ", hardware.getComputerSystem().getBaseboard().getVersion());
                write(feedback, ">>> Computer System >>> Firmware", "");
                write(feedback, "Name        : ", hardware.getComputerSystem().getFirmware().getName());
                write(feedback, "Description : ", hardware.getComputerSystem().getFirmware().getDescription());
                write(feedback, "Manufacturer: ", hardware.getComputerSystem().getFirmware().getManufacturer());
                write(feedback, "Version     : ", hardware.getComputerSystem().getFirmware().getVersion());
                write(feedback, "ReleaseDate : ", hardware.getComputerSystem().getFirmware().getReleaseDate());
                write(feedback, "", "");
                write(feedback, "***** Disk Stores *****", "");
                for (int t = 0; t < hardware.getDiskStores().length; t++) {
                    write(feedback, ">>> Disk Stores " + t, "");
                    HWDiskStore diskStore = hardware.getDiskStores()[t];
                    write(feedback, "Name              : ", diskStore.getName());
                    write(feedback, "Model             : ", diskStore.getModel());
                    write(feedback, "Serial            : ", diskStore.getSerial());
                    write(feedback, "Size              : ", Math.round(diskStore.getSize() / MB) + " MB");
                    write(feedback, "ReadBytes         : ", Math.round(diskStore.getReadBytes() / MB) + " MB");
                    write(feedback, "Reads             : ", diskStore.getReads());
                    write(feedback, "WriteBytes        : ", Math.round(diskStore.getWriteBytes() / MB) + " MB");
                    write(feedback, "Writes            : ", diskStore.getWrites());
                    write(feedback, "CurrentQueueLength: ", diskStore.getCurrentQueueLength());
                    write(feedback, "TransferTime      : ", diskStore.getTransferTime() + " milliseconds (" 
                            + UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(UtilsTime.getLocalDateTimeFromDate(new Date(diskStore.getTransferTime()))) + ")");
                    write(feedback, "TimeStamp         : ", diskStore.getTimeStamp() + " milliseconds (" 
                            + UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(UtilsTime.getLocalDateTimeFromDate(new Date(diskStore.getTimeStamp()))) + ")");
                    for (int i = 0; i < diskStore.getPartitions().length; i++) {
                        HWPartition partition = diskStore.getPartitions()[i];
                        write(feedback, ">>> Disk Stores " + t + " >>> Partition " + i, "");
                        write(feedback, "Name          : ", partition.getName());
                        write(feedback, "Type          : ", partition.getType());
                        write(feedback, "Size          : ", Math.round(partition.getSize() / MB) + " MB");
                        write(feedback, "Identification: ", partition.getIdentification());
                        write(feedback, "Uuid          : ", partition.getUuid());
                        write(feedback, "MountPoint    : ", partition.getMountPoint());
                        write(feedback, "Major         : ", partition.getMajor());
                        write(feedback, "Minor         : ", partition.getMinor());
                    }
                }
                write(feedback, "", "");
                write(feedback, ">>> Operating System >>> File System", "");
                write(feedback, "MaxFileDescriptors : ", operatingSystem.getFileSystem().getMaxFileDescriptors());
                write(feedback, "OpenFileDescriptors: ", operatingSystem.getFileSystem().getOpenFileDescriptors());
                for (int t = 0; t < operatingSystem.getFileSystem().getFileStores().length; t++) {
                    write(feedback, ">>> Operating System >>> File System " + t, "");
                    OSFileStore fileStore = operatingSystem.getFileSystem().getFileStores()[t];
                    write(feedback, "Name         : ", fileStore.getName());
                    write(feedback, "Description  : ", fileStore.getDescription());
                    write(feedback, "Type         : ", fileStore.getType());
                    write(feedback, "Mount        : ", fileStore.getMount());
                    write(feedback, "UUID         : ", fileStore.getUUID());
                    write(feedback, "TotalSpace   : ", Math.round(fileStore.getTotalSpace() / MB) + " MB");
                    write(feedback, "UsableSpace  : ", Math.round(fileStore.getUsableSpace() / MB) + " MB");
                    write(feedback, "TotalInodes  : ", fileStore.getTotalInodes());
                    write(feedback, "FreeInodes   : ", fileStore.getFreeInodes());
                    write(feedback, "Volume       : ", fileStore.getVolume());
                    write(feedback, "LogicalVolume: ", fileStore.getLogicalVolume());
                }
                write(feedback, "", "");
                write(feedback, "***** Displays *****", "");
                for (int t = 0; t < hardware.getDisplays().length; t++) {
                    write(feedback, ">>> Display - " + t, "");
                    Display display = hardware.getDisplays()[t];
                    write(feedback, "Edid: ", Arrays.toString(display.getEdid()));
                }
                write(feedback, "", "");
                write(feedback, "***** Memory *****", "");
                write(feedback, "Total       : ", Math.round(hardware.getMemory().getTotal() / MB) + " MB");
                write(feedback, "Available   : ", Math.round(hardware.getMemory().getAvailable() / MB) + " MB");
                write(feedback, "PageSize    : ", Math.round(hardware.getMemory().getPageSize() / MB) + " MB");
                write(feedback, "SwapUsed    : ", Math.round(hardware.getMemory().getSwapUsed() / MB) + " MB");
                write(feedback, "SwapTotal   : ", Math.round(hardware.getMemory().getSwapTotal() / MB) + " MB");
                write(feedback, "SwapPagesIn : ", Math.round(hardware.getMemory().getSwapPagesIn() / MB) + " MB");
                write(feedback, "SwapPagesOut: ", Math.round(hardware.getMemory().getSwapPagesOut() / MB) + " MB");
                write(feedback, "", "");
                write(feedback, "***** Network *****", "");
                for (int t = 0; t < hardware.getNetworkIFs().length; t++) {
                    write(feedback, ">>> Network " + t, "");
                    NetworkIF network = hardware.getNetworkIFs()[t];
                    write(feedback, "Name       : ", network.getName());
                    write(feedback, "DisplayName: ", network.getDisplayName());
                    write(feedback, "Macaddr    : ", network.getMacaddr());
                    write(feedback, "TimeStamp  : ", network.getTimeStamp());
                    write(feedback, "Speed      : ", Math.round(network.getSpeed()  / MB) + " Mbps");
                    write(feedback, "BytesRecv  : ", Math.round(network.getBytesRecv() / MB) + " MB");
                    write(feedback, "BytesSent  : ", Math.round(network.getBytesSent() / MB) + " MB");
                    write(feedback, "PacketsRecv: ", network.getPacketsRecv());
                    write(feedback, "PacketsSent: ", network.getPacketsSent());
                    write(feedback, "InErrors   : ", network.getInErrors());
                    write(feedback, "OutErrors  : ", network.getOutErrors());
                    write(feedback, "MTU        : ", network.getMTU());
                    for (String ip : network.getIPv4addr()) {
                        write(feedback, "IPv4addr   : ", ip);
                    }
                    for (String ip : network.getIPv6addr()) {
                        write(feedback, "IPv6addr   : ", ip);
                    }
                    write(feedback, ">>> Network " + t + " >>> Network Interface", "");
                    write(feedback, "Name           : ", network.getNetworkInterface().getName());
                    write(feedback, "DisplayName    : ", network.getNetworkInterface().getDisplayName());
                    write(feedback, "Index          : ", network.getNetworkInterface().getIndex());
                    write(feedback, "MTU            : ", network.getNetworkInterface().getMTU());
                    write(feedback, "Loopback       : ", network.getNetworkInterface().isLoopback());
                    write(feedback, "PointToPoint   : ", network.getNetworkInterface().isPointToPoint());
                    write(feedback, "Up             : ", network.getNetworkInterface().isUp());
                    write(feedback, "Virtual        : ", network.getNetworkInterface().isVirtual());
                    if (network.getNetworkInterface().getHardwareAddress() != null) {
                        write(feedback, "HardwareAddress: ", Arrays.toString(network.getNetworkInterface().getHardwareAddress()));
                    }
                    if (network.getNetworkInterface().getParent() != null) {
                        write(feedback, "Parent         : ", network.getNetworkInterface().getParent().getName());
                    }
                    while (network.getNetworkInterface().getSubInterfaces().hasMoreElements()) {
                        write(feedback, "SubInterfaces  : ", network.getNetworkInterface().getSubInterfaces().nextElement().getName());
                    }
// FIXME: Hierdie gaan in infanite loop in...
//                    while (network.getNetworkInterface().getInetAddresses().hasMoreElements()) {
//                        InetAddress inetAddress = network.getNetworkInterface().getInetAddresses().nextElement();
//                        write(feedback, "InetAddresses - HostName         : ", inetAddress.getHostName());
//                        write(feedback, "InetAddresses - HostAddress      : ", inetAddress.getHostAddress());
//                        write(feedback, "InetAddresses - CanonicalHostName: ", inetAddress.getCanonicalHostName());
//                        write(feedback, "InetAddresses - Address          : ", Arrays.toString(inetAddress.getAddress()));
//                        write(feedback, "InetAddresses - AnyLocalAddress  : ", inetAddress.isAnyLocalAddress());
//                        write(feedback, "InetAddresses - LinkLocalAddress : ", inetAddress.isLinkLocalAddress());
//                        write(feedback, "InetAddresses - LoopbackAddress  : ", inetAddress.isLoopbackAddress());
//                        write(feedback, "InetAddresses - MCGlobal         : ", inetAddress.isMCGlobal());
//                        write(feedback, "InetAddresses - MCLinkLocal      : ", inetAddress.isMCLinkLocal());
//                        write(feedback, "InetAddresses - MCNodeLocal      : ", inetAddress.isMCNodeLocal());
//                        write(feedback, "InetAddresses - MCOrgLocal       : ", inetAddress.isMCOrgLocal());
//                        write(feedback, "InetAddresses - MCSiteLocal      : ", inetAddress.isMCSiteLocal());
//                        write(feedback, "InetAddresses - MulticastAddress : ", inetAddress.isMulticastAddress());
//                        write(feedback, "InetAddresses - SiteLocalAddress : ", inetAddress.isSiteLocalAddress());
//                    }
                    for (int i = 0; i < network.getNetworkInterface().getInterfaceAddresses().size(); i++) {
                        InterfaceAddress interfaceAddress = network.getNetworkInterface().getInterfaceAddresses().get(i);
                        write(feedback, ">>> Network " + t + " >>> Interface Address " + i, "");
                        write(feedback, "InterfaceAddress - NetworkPrefixLength: ", interfaceAddress.getNetworkPrefixLength());
                        InetAddress inetAddress = interfaceAddress.getAddress();
                        write(feedback, "InetAddresses - HostName         : ", inetAddress.getHostName());
                        write(feedback, "InetAddresses - HostAddress      : ", inetAddress.getHostAddress());
                        write(feedback, "InetAddresses - CanonicalHostName: ", inetAddress.getCanonicalHostName());
                        write(feedback, "InetAddresses - Address          : ", Arrays.toString(inetAddress.getAddress()));
                        write(feedback, "InetAddresses - AnyLocalAddress  : ", inetAddress.isAnyLocalAddress());
                        write(feedback, "InetAddresses - LinkLocalAddress : ", inetAddress.isLinkLocalAddress());
                        write(feedback, "InetAddresses - LoopbackAddress  : ", inetAddress.isLoopbackAddress());
                        write(feedback, "InetAddresses - MCGlobal         : ", inetAddress.isMCGlobal());
                        write(feedback, "InetAddresses - MCLinkLocal      : ", inetAddress.isMCLinkLocal());
                        write(feedback, "InetAddresses - MCNodeLocal      : ", inetAddress.isMCNodeLocal());
                        write(feedback, "InetAddresses - MCOrgLocal       : ", inetAddress.isMCOrgLocal());
                        write(feedback, "InetAddresses - MCSiteLocal      : ", inetAddress.isMCSiteLocal());
                        write(feedback, "InetAddresses - MulticastAddress : ", inetAddress.isMulticastAddress());
                        write(feedback, "InetAddresses - SiteLocalAddress : ", inetAddress.isSiteLocalAddress());
                        inetAddress = interfaceAddress.getBroadcast();
                        if (inetAddress != null) {
                            write(feedback, "Broadcast - HostName         : ", inetAddress.getHostName());
                            write(feedback, "Broadcast - HostAddress      : ", inetAddress.getHostAddress());
                            write(feedback, "Broadcast - CanonicalHostName: ", inetAddress.getCanonicalHostName());
                            write(feedback, "Broadcast - Address          : ", Arrays.toString(inetAddress.getAddress()));
                            write(feedback, "Broadcast - AnyLocalAddress  : ", inetAddress.isAnyLocalAddress());
                            write(feedback, "Broadcast - LinkLocalAddress : ", inetAddress.isLinkLocalAddress());
                            write(feedback, "Broadcast - LoopbackAddress  : ", inetAddress.isLoopbackAddress());
                            write(feedback, "Broadcast - MCGlobal         : ", inetAddress.isMCGlobal());
                            write(feedback, "Broadcast - MCLinkLocal      : ", inetAddress.isMCLinkLocal());
                            write(feedback, "Broadcast - MCNodeLocal      : ", inetAddress.isMCNodeLocal());
                            write(feedback, "Broadcast - MCOrgLocal       : ", inetAddress.isMCOrgLocal());
                            write(feedback, "Broadcast - MCSiteLocal      : ", inetAddress.isMCSiteLocal());
                            write(feedback, "Broadcast - MulticastAddress : ", inetAddress.isMulticastAddress());
                            write(feedback, "Broadcast - SiteLocalAddress : ", inetAddress.isSiteLocalAddress());
                        }
                    }
                }
                write(feedback, "", "");
                write(feedback, ">>> Operating System >>> Network Params", "");
                write(feedback, "HostName          : ", operatingSystem.getNetworkParams().getHostName());
                write(feedback, "DomainName        : ", operatingSystem.getNetworkParams().getDomainName());
                write(feedback, "Ipv4DefaultGateway: ", operatingSystem.getNetworkParams().getIpv4DefaultGateway());
                write(feedback, "Ipv6DefaultGateway: ", operatingSystem.getNetworkParams().getIpv6DefaultGateway());
                for (String dnsServer : operatingSystem.getNetworkParams().getDnsServers()) {
                    write(feedback, "DnsServers        : ", dnsServer);
                }
                write(feedback, "", "");
                write(feedback, "***** Power Sources *****", "");
                for (int t = 0; t < hardware.getPowerSources().length; t++) {
                    write(feedback, ">>> Power Source " + t, "");
                    PowerSource powerSource = hardware.getPowerSources()[t];
                    write(feedback, "Name             : ", powerSource.getName());
                    write(feedback, "RemainingCapacity: ", Math.round(powerSource.getRemainingCapacity() * 100.0) + "%");
                    if (powerSource.getTimeRemaining() >= 0) {
                        write(feedback, "TimeRemaining    : ", (powerSource.getTimeRemaining() / 60) + " minutes");
                    }
                    else 
                    if (powerSource.getTimeRemaining() == -1.0) {
                        write(feedback, "TimeRemaining    : ", "Calculating");
                    }
                    else 
                    if (powerSource.getTimeRemaining() == -2.0) {
                        write(feedback, "TimeRemaining    : ", "Unlimited");
                    }
                }
                write(feedback, "", "");
                write(feedback, "***** Processor *****", "");
                write(feedback, "Name                  : ", hardware.getProcessor().getName());
                write(feedback, "Identifier            : ", hardware.getProcessor().getIdentifier());
                write(feedback, "Family                : ", hardware.getProcessor().getFamily());
                write(feedback, "Model                 : ", hardware.getProcessor().getModel());
                write(feedback, "Vendor                : ", hardware.getProcessor().getVendor());
                write(feedback, "VendorFreq            : ", hardware.getProcessor().getVendorFreq());
                write(feedback, "ProcessorID           : ", hardware.getProcessor().getProcessorID());
                write(feedback, "Cpu64bit              : ", hardware.getProcessor().isCpu64bit());
                write(feedback, "LogicalProcessorCount : ", hardware.getProcessor().getLogicalProcessorCount());
                write(feedback, "PhysicalProcessorCount: ", hardware.getProcessor().getPhysicalProcessorCount());
                write(feedback, "PhysicalPackageCount  : ", hardware.getProcessor().getPhysicalPackageCount());
                write(feedback, "Interrupts            : ", hardware.getProcessor().getInterrupts());
                write(feedback, "ContextSwitches       : ", hardware.getProcessor().getContextSwitches());
                write(feedback, "Stepping              : ", hardware.getProcessor().getStepping());
                write(feedback, "SystemUptime          : ", (hardware.getProcessor().getSystemUptime() / 60) + " minutes");
                write(feedback, "SystemLoadAverage     : ", hardware.getProcessor().getSystemLoadAverage());
                write(feedback, ">>> Processor >>> System CPU Load", "");
                write(feedback, "SystemCpuLoad         : ", hardware.getProcessor().getSystemCpuLoad());
                write(feedback, "SystemCpuLoadBetweenTicks   : ", hardware.getProcessor().getSystemCpuLoadBetweenTicks());
                write(feedback, "SystemCpuLoadTicks - IDLE   : ", hardware.getProcessor().getSystemCpuLoadTicks()[CentralProcessor.TickType.IDLE.getIndex()]);
                write(feedback, "SystemCpuLoadTicks - USER   : ", hardware.getProcessor().getSystemCpuLoadTicks()[CentralProcessor.TickType.USER.getIndex()]);
                write(feedback, "SystemCpuLoadTicks - SYSTEM : ", hardware.getProcessor().getSystemCpuLoadTicks()[CentralProcessor.TickType.SYSTEM.getIndex()]);
                write(feedback, "SystemCpuLoadTicks - IOWAIT : ", hardware.getProcessor().getSystemCpuLoadTicks()[CentralProcessor.TickType.IOWAIT.getIndex()]);
                write(feedback, "SystemCpuLoadTicks - NICE   : ", hardware.getProcessor().getSystemCpuLoadTicks()[CentralProcessor.TickType.NICE.getIndex()]);
                write(feedback, "SystemCpuLoadTicks - STEAL  : ", hardware.getProcessor().getSystemCpuLoadTicks()[CentralProcessor.TickType.STEAL.getIndex()]);
                write(feedback, "SystemCpuLoadTicks - IRQ    : ", hardware.getProcessor().getSystemCpuLoadTicks()[CentralProcessor.TickType.IRQ.getIndex()]);
                write(feedback, "SystemCpuLoadTicks - SOFTIRQ: ", hardware.getProcessor().getSystemCpuLoadTicks()[CentralProcessor.TickType.SOFTIRQ.getIndex()]);
                for (int i = 0; i < hardware.getProcessor().getLogicalProcessorCount(); i++) {
                    write(feedback, ">>> Processor >>> Processor CPU Load " + i, "");
                    write(feedback, "ProcessorCpuLoadBetweenTicks   : ", hardware.getProcessor().getProcessorCpuLoadBetweenTicks()[i]);
                    write(feedback, "ProcessorCpuLoadTicks - IDLE   : ", hardware.getProcessor().getProcessorCpuLoadTicks()[i][CentralProcessor.TickType.IDLE.getIndex()]);
                    write(feedback, "ProcessorCpuLoadTicks - USER   : ", hardware.getProcessor().getProcessorCpuLoadTicks()[i][CentralProcessor.TickType.USER.getIndex()]);
                    write(feedback, "ProcessorCpuLoadTicks - SYSTEM : ", hardware.getProcessor().getProcessorCpuLoadTicks()[i][CentralProcessor.TickType.SYSTEM.getIndex()]);
                    write(feedback, "ProcessorCpuLoadTicks - IOWAIT : ", hardware.getProcessor().getProcessorCpuLoadTicks()[i][CentralProcessor.TickType.IOWAIT.getIndex()]);
                    write(feedback, "ProcessorCpuLoadTicks - NICE   : ", hardware.getProcessor().getProcessorCpuLoadTicks()[i][CentralProcessor.TickType.NICE.getIndex()]);
                    write(feedback, "ProcessorCpuLoadTicks - STEAL  : ", hardware.getProcessor().getProcessorCpuLoadTicks()[i][CentralProcessor.TickType.STEAL.getIndex()]);
                    write(feedback, "ProcessorCpuLoadTicks - IRQ    : ", hardware.getProcessor().getProcessorCpuLoadTicks()[i][CentralProcessor.TickType.IRQ.getIndex()]);
                    write(feedback, "ProcessorCpuLoadTicks - SOFTIRQ: ", hardware.getProcessor().getProcessorCpuLoadTicks()[i][CentralProcessor.TickType.SOFTIRQ.getIndex()]);
                }
                write(feedback, "", "");
                write(feedback, "***** Sensors *****", "");
                write(feedback, "CpuTemperature: ", hardware.getSensors().getCpuTemperature());
                write(feedback, "CpuVoltage    : ", hardware.getSensors().getCpuVoltage());
                for (int t = 0; t < hardware.getSensors().getFanSpeeds().length; t++) {
                write(feedback, "Fan " + t + " Speed   : ", hardware.getSensors().getFanSpeeds()[t]);
                }
                write(feedback, "", "");
                write(feedback, "***** Sound Cards *****", "");
                for (int t = 0; t < hardware.getSoundCards().length; t++) {
                    write(feedback, ">>> Sound Card " + t, "");
                    SoundCard soundCard = hardware.getSoundCards()[t];
                    write(feedback, "Name         : ", soundCard.getName());
                    write(feedback, "DriverVersion: ", soundCard.getDriverVersion());
                    write(feedback, "Codec        : ", soundCard.getCodec());
                }
                write(feedback, "", "");
                write(feedback, "***** USB Devices *****", "");
                for (int t = 0; t < hardware.getUsbDevices(true).length; t++) {
                    write(feedback, ">>> USB Device " + t, "");
                    UsbDevice usbDevice = hardware.getUsbDevices(true)[t];
                    writeNestedUSBDevices(feedback, "Controller[" + t + "]", usbDevice);
                }
                write(feedback, "", "");
                write(feedback, ">>> Operating System >>> Processes", "");
                write(feedback, "ThreadCount : ", operatingSystem.getThreadCount());
                write(feedback, "ProcessCount: ", operatingSystem.getProcessCount());
                write(feedback, ">>> Operating System >>> WildLog Process", "");
                OSProcess process = operatingSystem.getProcess(operatingSystem.getProcessId());
                write(feedback, "Name                   : ", process.getName());
                write(feedback, "ProcessId              : ", process.getProcessID());
                write(feedback, "ParentProcessID        : ", process.getParentProcessID());
                write(feedback, "Group                  : ", process.getGroup());
                write(feedback, "GroupID                : ", process.getGroupID());
                write(feedback, "User                   : ", process.getUser());
                write(feedback, "UserID                 : ", process.getUserID());
                write(feedback, "State                  : ", process.getState());
                write(feedback, "Priority               : ", process.getPriority());
                write(feedback, "StartTime              : ", UtilsTime.WL_DATE_FORMATTER_FOR_FILES_WITH_TIMESTAMP.format(UtilsTime.getLocalDateTimeFromDate(new Date(process.getStartTime()))));
                write(feedback, "UpTime                 : ", Math.round(((double) process.getUpTime()) / 1000.0 / 60.0) + " minutes");
                write(feedback, "KernelTime             : ", Math.round(((double) process.getKernelTime()) / 1000.0 / 60.0) + " minutes");
                write(feedback, "UserTime               : ", Math.round(((double) process.getUserTime()) / 1000.0 / 60.0) + " minutes");
                write(feedback, "ThreadCount            : ", process.getThreadCount());
                write(feedback, "CommandLine            : ", process.getCommandLine());
                write(feedback, "CurrentWorkingDirectory: ", process.getCurrentWorkingDirectory());
                write(feedback, "Path                   : ", process.getPath());
                write(feedback, "OpenFiles              : ", process.getOpenFiles());
                write(feedback, "BytesRead              : ", Math.round(process.getBytesRead() / MB) + " MB");
                write(feedback, "BytesWritten           : ", Math.round(process.getBytesWritten() / MB) + " MB");
                write(feedback, "ResidentSetSize        : ", Math.round(process.getResidentSetSize() / MB) + " MB");
                write(feedback, "VirtualSize            : ", Math.round(process.getVirtualSize() / MB) + " MB");
                write(feedback, "CpuPercent             : ", Math.round(process.calculateCpuPercent() * 100.0) + "%");
                // Save Java data
                write(feedback, "", "");
                write(feedback, "***** Java *****", "");
                write(feedback, ">>> Java Properties", "");
                write(feedback, "user.name      : ", System.getProperty("user.name"));
                write(feedback, "os.name        : ", System.getProperty("os.name"));
                write(feedback, "os.version     : ", System.getProperty("os.version"));
                write(feedback, "os.arch        : ", System.getProperty("os.arch"));
                write(feedback, ">>> Java Time", "");
                write(feedback, "Timezone       : ", TimeZone.getDefault().getDisplayName() + " [" + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("(z) VV")) + "]");
                write(feedback, ">>> Java Runtime", "");
                write(feedback, "JVM CPU cores  : ", Runtime.getRuntime().availableProcessors());
                write(feedback, "JVM Max Memory : ", Math.round(Runtime.getRuntime().maxMemory() / MB) + " MB");
                write(feedback, "JVM Used Memory: ", Math.round((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB) + " MB");
                write(feedback, ">>> Java Graphics", "");
                write(feedback, "MaximumWindowBounds: ", GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth() + " width");
                write(feedback, "MaximumWindowBounds: ", GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight() + "height");
                write(feedback, "Screen Count       : ", GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length);
                for (int t = 0; t < GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length; t++) {
                    write(feedback, ">>> Java Graphics >>> Graphics Device " + t, "");
                    GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[t];
                    write(feedback, "IDstring                  : ", graphicsDevice.getIDstring());
                    write(feedback, "Width                     : ", graphicsDevice.getDisplayMode().getWidth());
                    write(feedback, "Height                    : ", graphicsDevice.getDisplayMode().getHeight());
                    write(feedback, "FullScreenSupported       : ", graphicsDevice.isFullScreenSupported());
                    if (graphicsDevice.getAvailableAcceleratedMemory() < 0) {
                        write(feedback, "AvailableAcceleratedMemory: ", "unknown");
                    }
                    else {
                        write(feedback, "AvailableAcceleratedMemory: ", graphicsDevice.getAvailableAcceleratedMemory());
                    }
                    write(feedback, ">>> Java Graphics >>> Graphics Device " + t + " >>> Display Mode", "");
                    write(feedback, "BitDepth   : ", graphicsDevice.getDisplayMode().getBitDepth());
                    write(feedback, "RefreshRate: ", graphicsDevice.getDisplayMode().getRefreshRate());
                    write(feedback, "Width      : ", graphicsDevice.getDisplayMode().getWidth());
                    write(feedback, "Height     : ", graphicsDevice.getDisplayMode().getHeight());
                }
                write(feedback, ">>> Java Threads", "");
                write(feedback, "Thread Count: ", Thread.getAllStackTraces().size());
                int threadCounter = 0;
                for (Thread thread : Thread.getAllStackTraces().keySet()) {
                    write(feedback, ">>> Java Threads >>> Thread " + threadCounter++, "");
                    write(feedback, "Name       : ", thread.getName());
                    write(feedback, "ThreadGroup: ", thread.getThreadGroup().getName());
                    write(feedback, "Id         : ", thread.getId());
                    write(feedback, "Priority   : ", thread.getPriority());
                    write(feedback, "State      : ", thread.getState());
                    write(feedback, "Alive      : ", thread.isAlive());
                    write(feedback, "Daemon     : ", thread.isDaemon());
                    write(feedback, "Interrupted: ", thread.isInterrupted());
                }
                // Make sure all text is flushed to the feedback file
                feedback.flush();
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            // Open folder with the results
            UtilsFileProcessing.openFile(path);
        });
    }

    private void writeNestedUSBDevices(final PrintWriter inFeedback, String parentChain, UsbDevice inUsbDevice) {
        write(inFeedback, "Name            : ", inUsbDevice.getName());
        write(inFeedback, "Vendor          : ", inUsbDevice.getVendor());
        write(inFeedback, "VendorId        : ", inUsbDevice.getVendorId());
        write(inFeedback, "ProductId       : ", inUsbDevice.getProductId());
        write(inFeedback, "SerialNumber    : ", inUsbDevice.getSerialNumber());
        write(inFeedback, "ConnectedDevices: ", inUsbDevice.getConnectedDevices().length);
        for (int i = 0; i < inUsbDevice.getConnectedDevices().length; i++) {
            write(inFeedback, ">>> Connected Device " + i + " >>> USB Device Chain: " + parentChain, "");
            UsbDevice connectedUsbDevice = inUsbDevice.getConnectedDevices()[i];
            writeNestedUSBDevices(inFeedback, parentChain + "_Connected(" + i + ")", connectedUsbDevice);
        }
    }
    
    private void write(PrintWriter inWriter, String inLabel, Object inValue) {
        String text = inLabel + inValue;
        inWriter.println(text);
        WildLogApp.LOGGER.log(Level.INFO, text);
    }
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        tickService.shutdown();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblLoading;
    // End of variables declaration//GEN-END:variables
}
