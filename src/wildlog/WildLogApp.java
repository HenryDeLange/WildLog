package wildlog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EventObject;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.InsetsUIResource;
import org.jdesktop.application.Application;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dbi.WildLogDBI;
import wildlog.data.dbi.WildLogDBI_h2;
import wildlog.mapping.MapFrameOffline;
import wildlog.mapping.MapFrameOnline;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.utils.WildLogPaths;

/**
 * The main class of the application.
 */
// Note: Ek kan nie regtig die SwingAppFramework los nie want die progressbar en paar ander goed gebruik dit. Ek sal dan daai goed moet oorskryf...
public class WildLogApp extends Application {
    private static Path ACTIVE_WILDLOG_SETTINGS_FOLDER;
    private static boolean useNimbusLF = false;
    // Only open one MapFrame for the application (to reduce memory use)
    private MapFrameOffline mapOffline;
    private JXMapKit mapOnline;
    private MapFrameOnline mapOnlineFrame;
    // Other settings
    private WildLogOptions wildLogOptions;
    private int threadCount;
    // Make sure the application uses the same WildLogDBI instance...
    // The WildLogDBI is initialized in startup() and closed in shutdown()
    private WildLogDBI dbi;
    private WildLogView view;
    // TODO: Ek dink nie ek gebruik hierdie executor huidiglik nie... Dis half vir future use...
//    private ExecutorService sharedExecutor;


    @Override
    protected void initialize(String[] arg0) {
        System.out.println("Initializing workspace...");
        super.initialize(arg0);
        // Get the threadcount
        threadCount = (int)(Runtime.getRuntime().availableProcessors() * 1.5);
        if (threadCount < 3) {
            threadCount = 3;
        }
//        sharedExecutor = Executors.newFixedThreadPool(threadCount, new NamedThreadFactory("WildLogShared-NeverShutdown"));
        // Makse sure all the basic data/file folders are in place
        try {
            Files.createDirectories(WildLogPaths.getFullWorkspacePrefix());
            Files.createDirectories(WildLogPaths.WILDLOG_DATA.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_FILES.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_FILES_IMAGES.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_FILES_MOVIES.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_FILES_OTHER.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_THUMBNAILS.getAbsoluteFullPath());
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        // Open the database
        // If this fails then it might be corrupt or already open. Ask the user to select a new workspace folder.
        boolean openedWorkspace;
        do {
            openedWorkspace = openWorkspace();
            if (openedWorkspace == false) {
                UtilsDialog.showDialogBackgroundWrapper(getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(getMainFrame(),
                                "The WildLog Workspace could not be opened. It might be in use or broken. Please select another Workspace to open.",
                                "WildLog Workspace Error", JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                });
                configureWildLogHomeBasedOnFileBrowser(null, true);
            }
        }
        while (openedWorkspace == false);
        // Check to do monthly backup
        File dirs = new File(WildLogPaths.WILDLOG_BACKUPS_MONTHLY.getAbsoluteFullPath() + "Backup (" + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()) + ")");
        if (!dirs.exists()) {
            dbi.doBackup(WildLogPaths.WILDLOG_BACKUPS_MONTHLY);
        }
        // Load the WildLogOptions
        wildLogOptions = dbi.find(new WildLogOptions());
    }

    private boolean openWorkspace() {
        try {
            dbi = new WildLogDBI_h2();
            System.out.println("Workspace opened at: " + WildLogPaths.getFullWorkspacePrefix().toString());
        }
        catch (Exception ex) {
            System.err.println("Could not open the Workspace. Will try to ask the user to try a new Workspace folder.");
            ex.printStackTrace(System.err); // This will technicall log the error twice, but doing it to playsafe
            return false;
        }
        return true;
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        if (useNimbusLF) {
            // Try to set the Nimbus look and feel
            // While the Windows Look and Feel is the primary LF it isn't available on all OSes, but Nimbus LF provides a decent
            // look that is fairly consistant over different OSes. Thus shis should be the default for Linux (Ubuntu) and I guess Mac.
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        // Make the global button margins smaller, because Nimbus ignores the setting on the buttons
                        UIManager.getLookAndFeelDefaults().put("Button.contentMargins", new InsetsUIResource(0,0,0,0));
                        UIManager.getLookAndFeelDefaults().put("ToggleButton.contentMargins", new InsetsUIResource(0,0,0,0));
                        break;
                    }
                }
            }
            catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                // We'll try to go ahead without the Nimbus LookAndFeel
                System.out.println("Could not load the Nimbus Look and Feel. The application will continue to launch, but there may be some display problems...");
            }
        }
        // Show the main frame
        view = new WildLogView(this);
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        view.setLocationRelativeTo(null);
        view.setVisible(true);
        // Setup the glassPane for modal popups
        JPanel glassPane = (JPanel)view.getGlassPane();
        glassPane.setLayout(new BorderLayout());
        JPanel background = new JPanel();
        background.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.25f));
        glassPane.add(background, BorderLayout.CENTER);
        glassPane.addMouseListener(new MouseAdapter() {});
        glassPane.addKeyListener(new KeyAdapter() {});
        addExitListener(new ExitListener() {
            @Override
            public boolean canExit(EventObject event) {
                boolean doShutdown = true;
                try {
                    // Waarsku as daar tabs is wat nie gesave is nie
                    if (!view.closeAllTabs()) {
                        return false;
                    }
                    // Waarsku as daar progressbar tasks is wat nog hardloop
                    TaskMonitor taskMonitor = getContext().getTaskMonitor();
                    if (taskMonitor.getTasks() != null && !taskMonitor.getTasks().isEmpty()) {
                        int result = UtilsDialog.showDialogBackgroundWrapper(getMainFrame(), new UtilsDialog.DialogWrapper() {
                            @Override
                            public int showDialog() {
                                return JOptionPane.showConfirmDialog(getMainFrame(),
                                        "<html>There are background processes running that have not finished yet. "
                                                + "<br>It is <b>recommended to wait for these processes to finish</b>."
                                                + "<br>(See the progressbar at the bottom right hand corner for details.)"
                                                + "<br><b>Continue to Exit WildLog?</b></html>",
                                        "Warning! Unfinished Processes...", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                                }
                        });
                        if (result == JOptionPane.YES_OPTION) {
                            System.out.println("Trying to stop running processes before exiting...");
                            // Try to stop the running processes before the DB gets closed
                            TaskService taskService = getContext().getTaskService();
                            taskService.shutdownNow();
                            try {
                                taskService.awaitTermination(3, TimeUnit.SECONDS);
                            }
                            catch (InterruptedException ex) {
                                // This will break lots of threads... SO lets just ignore the errors :P
                                //ex.printStackTrace(System.err);
                            }
                        }
                        else {
                            doShutdown = false;
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
                return doShutdown;
            }
            @Override
            public void willExit(EventObject event) {
                if (dbi != null) {
                    dbi.close();
                    System.out.println("Closing workspace...");
                }
            }
        });
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of WildLogApp
     */
    public static WildLogApp getApplication() {
        return Application.getInstance(WildLogApp.class);
    }

    /**
     * Main method launching the application.
     * @param args
     */
    public static void main(String[] args) {
        // Set default startup settings
        ACTIVE_WILDLOG_SETTINGS_FOLDER = WildLogPaths.DEFAUL_SETTINGS_FOLDER.getRelativePath().toAbsolutePath();
        boolean logToFile = false;
        useNimbusLF = true;
        // Load the startup settings from the properties file
        BufferedReader reader = null;
        try {
            Path propsPath;
            if (args.length == 1 && args[0].toLowerCase().startsWith("properties=")) {
                // If started normally then this arg will be present (from the exe and inside NetBeans)
                propsPath = Paths.get(args[0].substring("properties=".length()));
            }
            else {
                // As a backup if the arg was not set, then try to get hold of the file in the same folder as the JAR
                propsPath = Paths.get(WildLogApp.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                        .getParent().resolve("wildlog.properties");
            }
            reader = new BufferedReader(new FileReader(propsPath.toFile()));
            Properties props = new Properties();
            props.load(reader);
            // Set the settings
            logToFile = Boolean.parseBoolean(props.getProperty("logToFile"));
            useNimbusLF = Boolean.parseBoolean(props.getProperty("useNimbus"));
            if (props.getProperty("settingsFolderLocation") != null && !props.getProperty("settingsFolderLocation").trim().isEmpty()) {
                ACTIVE_WILDLOG_SETTINGS_FOLDER = Paths.get(props.getProperty("settingsFolderLocation")).toAbsolutePath();
            }
        }
        catch (IOException | URISyntaxException ex) {
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null,
                        "Could not load the settings from the properties on startup. WildLog will continue to start using the default settings.",
                        "Problem Starting WildLog", JOptionPane.WARNING_MESSAGE);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
        // After the startup properties have been loaded continue to start the application
        try {
            // Make sure the Settings folder exists
            Files.createDirectories(ACTIVE_WILDLOG_SETTINGS_FOLDER);
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        // Enable logging to file
        if (logToFile) {
            try {
                // Saving the orginal stream
                PrintStream fileStream = new PrintStream(new FileOutputStream(ACTIVE_WILDLOG_SETTINGS_FOLDER.resolve("errorlog.txt").toFile(), true), true);
                // Redirecting console output to file
                System.setOut(fileStream);
                // Redirecting runtime exceptions to file
                System.setErr(fileStream);
            }
            catch (FileNotFoundException ex) {
                ex.printStackTrace(System.err);
            }
        }
        // Try to read the settings file containing the wildloghome (active workspace)
        try {
            configureWildLogHomeBasedOnSettingsFile();
        }
        catch (IOException ex) {
            // Daar was 'n probleem om die wildloghome settings file te lees, probeer om 'n nuwe wildloghome file te maak
            System.out.println("Could not find the wildloghome file. Will try to create it...");
            ex.printStackTrace(System.out);
            FileWriter writer = null;
            try {
                writer = new FileWriter(ACTIVE_WILDLOG_SETTINGS_FOLDER.resolve("wildloghome").toFile());
                if (new File(File.separator).canWrite()) {
                    writer.write(File.separator);
                }
                else {
                    writer.write(System.getProperty("user.home") + File.separatorChar);
                }
            }
            catch (IOException ioex) {
                ioex.printStackTrace(System.err);
            }
            finally {
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    }
                    catch (IOException ioex) {
                        ioex.printStackTrace(System.err);
                    }
                }
            }
            // As ek steeds nie 'n wildloghome file kan gelees kry nie vra die user vir 'n wildloghome om te gebruik
            try {
                configureWildLogHomeBasedOnSettingsFile();
            }
            catch (IOException ioex) {
                ioex.printStackTrace(System.err);
                configureWildLogHomeBasedOnFileBrowser(null, true);
            }
        }
        System.out.println("STARTING UP WildLog - "
                + new SimpleDateFormat("dd MMM yyyy (HH:mm:ss)").format(Calendar.getInstance().getTime()));
        System.out.println("WildLog Setting Folder: " + ACTIVE_WILDLOG_SETTINGS_FOLDER.toAbsolutePath().toString());
        // Launch the Swing application on the event dispatch thread
        launch(WildLogApp.class, args);
    }

    private static void configureWildLogHomeBasedOnSettingsFile() throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(ACTIVE_WILDLOG_SETTINGS_FOLDER.resolve("wildloghome").toFile()));
            WildLogPaths.setWorkspacePrefix(reader.readLine());
        }
        // No catch: The error should be thrown if something goes wrong
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    public static boolean configureWildLogHomeBasedOnFileBrowser(final JFrame inParent, boolean inTerminateIfNotSelected) {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setDialogTitle("Please select the WildLog Workspace to use.");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = UtilsDialog.showDialogBackgroundWrapper(inParent, new UtilsDialog.DialogWrapper() {
            @Override
            public int showDialog() {
                return fileChooser.showOpenDialog(inParent);
            }
        });
        if (result == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
            WildLogPaths.setWorkspacePrefix(fileChooser.getSelectedFile().getAbsolutePath());
            return true;
        }
        else {
            if (inTerminateIfNotSelected) {
                Application.getInstance().exit();
            }
        }
        return false;
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        System.out.println("SHUTTING DOWN WildLog - " + new SimpleDateFormat("dd MMM yyyy (HH:mm:ss)").format(Calendar.getInstance().getTime()));
        System.out.println();
    }

    public WildLogDBI getDBI() {
        return dbi;
    }

    public MapFrameOffline getMapOffline() {
        // Setup MapFrame - Note: If this is in the constructor the frame keeps poping up when the application starts
        if (mapOffline == null) {
            mapOffline = new MapFrameOffline("WildLog Map - Offline", getWildLogOptions().getDefaultLatitude(), getWildLogOptions().getDefaultLongitude()/*, useOnlineMap*/);
            // Setup the escape key
            final JFrame thisHandler = (JFrame)mapOffline.getFrameForImageDrawing();
            ActionListener escListiner = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            thisHandler.setVisible(false);
                        }
                    };
            thisHandler.getRootPane().registerKeyboardAction(
                    escListiner,
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);
            }
        return mapOffline;
    }

    public MapFrameOnline getMapOnline() {
        // Setup MapFrame - Note: If this is in the constructor the frame keeps poping up when the application starts
        if (mapOnlineFrame == null) {
            final GeoPosition defaultPosition = new GeoPosition(getWildLogOptions().getDefaultLatitude(), getWildLogOptions().getDefaultLongitude());

            mapOnline = new JXMapKit();

            mapOnlineFrame = new MapFrameOnline("WildLog Map - Online", mapOnline, this);
            mapOnlineFrame.setPreferredSize(new Dimension(758, 560));
            mapOnlineFrame.setLayout(new AbsoluteLayout());
            ImageIcon icon = new ImageIcon(WildLogApp.class.getResource("resources/icons/WildLog Map Icon.gif"));
            mapOnlineFrame.setIconImage(icon.getImage());

            mapOnline.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
            mapOnline.setPreferredSize(new Dimension(750, 500));
            mapOnline.setAddressLocationShown(false);
            mapOnline.setName("mapOnline");
            mapOnline.setAddressLocation(defaultPosition);
            mapOnline.setZoom(12);
            mapOnlineFrame.add(mapOnline, new AbsoluteConstraints(0, 0, -1, -1), 0);

            // Add buttons
            final WildLogApp app = this;
            JButton btnPrevMapPoint = new JButton("Prev");
            btnPrevMapPoint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mapOnlineFrame.getPointLayer().loadPrevClickedPoint(app);
                }
            });
            btnPrevMapPoint.setPreferredSize(new Dimension(70, 25));
            mapOnlineFrame.add(btnPrevMapPoint, new AbsoluteConstraints(0, 505, -1, -1));

            JButton btnNextMapPoint = new JButton("Next");
            btnNextMapPoint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mapOnlineFrame.getPointLayer().loadNextClickedPoint(app);
                }
            });
            btnNextMapPoint.setPreferredSize(new Dimension(70, 25));
            mapOnlineFrame.add(btnNextMapPoint, new AbsoluteConstraints(70, 505, -1, -1));
            mapOnlineFrame.pack();
            mapOnlineFrame.setResizable(false);
            // Setup the escape key
            final JFrame thisHandler = (JFrame)mapOnlineFrame;
            ActionListener escListiner = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            thisHandler.setVisible(false);
                        }
                    };
            thisHandler.getRootPane().registerKeyboardAction(
                    escListiner,
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);
        }
        return mapOnlineFrame;
    }

    public void clearOnlinemap() {
        mapOnlineFrame = null;
    }

    public WildLogOptions getWildLogOptions() {
        return wildLogOptions;
    }

    public void setWildLogOptions(WildLogOptions inWildLogOptions) {
        wildLogOptions = inWildLogOptions;
        dbi.createOrUpdate(wildLogOptions);
    }

    public int getThreadCount() {
        return threadCount;
    }

    public static Path getACTIVE_WILDLOG_SETTINGS_FOLDER() {
        return ACTIVE_WILDLOG_SETTINGS_FOLDER;
    }

    public WildLogView getMainFrame() {
        return view;
    }

//    /**
//     * This ExecutorService is shared and available to the entire application and
//     * should thus only process tasks that does not require the service to
//     * shutdown (or wait for execution to finish).
//     * @return
//     */
//    public ExecutorService getSharedExecutor() {
//        return sharedExecutor;
//    }

}
