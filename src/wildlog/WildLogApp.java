package wildlog;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EventObject;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.InsetsUIResource;
import org.jdesktop.application.Application;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dbi.WildLogDBI;
import wildlog.data.dbi.WildLogDBI_h2;
import wildlog.mapping.MapFrameOffline;
import wildlog.mapping.MapFrameOnline;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.utils.UtilsTime;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.WildLogPaths;

/**
 * The main class of the application.
 */
// Note: Ek kan nie regtig die SwingAppFramework los nie want die progressbar en paar ander goed gebruik dit. Ek sal dan daai goed moet oorskryf...
public class WildLogApp extends Application {
    public static String WILDLOG_VERSION = "4.3.beta";
    private static Path ACTIVE_WILDLOG_SETTINGS_FOLDER;
    private static Path ACTIVEWILDLOG_CODE_FOLDER;
    private static boolean useNimbusLF = false;
    private static boolean logToFile = false;
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


    @Override
    protected void initialize(String[] arg0) {
        System.out.println("Initializing workspace...");
        super.initialize(arg0);
        // Get the threadcount
        threadCount = (int)(Runtime.getRuntime().availableProcessors() * 1.5);
        if (threadCount < 3) {
            threadCount = 3;
        }
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
        // Load the WildLogOptions
        wildLogOptions = dbi.find(new WildLogOptions());
        System.out.println("Workspace opened with ID: " + wildLogOptions.getWorkspaceID());
        // Check to do monthly backup and try to upload the logs and user data to the MyWild DB
        Path folderPath = WildLogPaths.WILDLOG_BACKUPS_MONTHLY.getAbsoluteFullPath()
                .resolve("Backup (" + UtilsTime.WL_DATE_FORMATTER_FOR_BACKUP_MONTHLY.format(LocalDateTime.now()) + ")");
        if (!Files.exists(folderPath)) {
            // Do the backup
            dbi.doBackup(folderPath.toAbsolutePath());
            // Do some online calls
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("WL_MyWildCalls"));
            // Try to check the latest version
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    checkForUpdates();
                }
            }, 10, TimeUnit.SECONDS);
            // Try to upload log data
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Open a connection to the site
                        URL url = new URL("http://www.mywild.co.za/wildlog/uploadWildLogInfo.php");
                        URLConnection con = url.openConnection();
                        // Activate the output
                        con.setDoOutput(true);
                        try (PrintStream printStream = new PrintStream(con.getOutputStream())) {
                            // Load some of the info needed
                            long maxMemory = Runtime.getRuntime().maxMemory();
                            GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                            int width = graphicsDevice.getDisplayMode().getWidth();
                            int height = graphicsDevice.getDisplayMode().getHeight();
                            String info = "OS Name    : " + System.getProperty("os.name") + "\n"
                                        + "OS version : " + System.getProperty("os.version") + "\n"
                                        + "OS Arch    : " + System.getProperty("os.arch") + "\n"
                                        + "Timezone   : " + TimeZone.getDefault().getDisplayName() + " [" + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("(z) VV")) + "]\n"
                                        + "JVM CPU cores   : " + Runtime.getRuntime().availableProcessors() + "\n"
                                        + "JVM Used Memory : " + Math.round((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024.0*1024.0) * 100) / 100.0 + " MB" + "\n"
                                        + "JVM Max Memory  : " + (maxMemory == Long.MAX_VALUE ? "no limit" : Math.round((maxMemory) / (1024.0*1024.0) * 100) / 100.0) + " MB" + "\n"
                                        + "Screen Size : " + width + "x" + height + "\n"
                                        + "Screen Count: " + GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
                            String logFileSnippit = "(Not using logfile)";
                            if (logToFile) {
                                try {
                                    byte[] fileBytes = Files.readAllBytes(ACTIVE_WILDLOG_SETTINGS_FOLDER.resolve("errorlog.txt"));
                                    String logInfo = new String(fileBytes, Charset.defaultCharset());
                                    int logLength = 99999;
                                    if (logInfo.length() > logLength) {
                                        
                                        
// FIXME: Daar is erns steeds fout met die log file upload/truncate/etc. besigheid...
                                        
                                        
                                        logFileSnippit = "...\n...\n" + logInfo.substring(logInfo.length() - logLength);
                                    }
                                    else {
                                        logFileSnippit = logInfo;
                                    }
                                    // Om die file se path beter te lees in die uploaded log (want dit haal die snaakse karakters uit)
                                    logFileSnippit = logFileSnippit.replace(File.separatorChar, '^');
                                }
                                catch (IOException  ex) {
                                    ex.printStackTrace(System.err);
                                }
                            }
                            // Send the parameters to the site
                            //printStream.print("DateAndTime=" + "set by server");
                            printStream.print("&WildLogVersion=" + WILDLOG_VERSION);
                            printStream.print("&WorkspaceDatabaseVersion=" + wildLogOptions.getDatabaseVersion());
                            printStream.print("&WorkspaceID=" + wildLogOptions.getWorkspaceID());
                            printStream.print("&WorkspaceName=" + wildLogOptions.getWorkspaceName());
                            //printStream.print("&IP=" + "set by server");
                            printStream.print("&SystemUsername=" + System.getProperty("user.name"));
                            printStream.print("&SystemInfo=" + info);
                            printStream.print("&NumberOfElements=" + dbi.count(new Element()));
                            printStream.print("&NumberOfLocations=" + dbi.count(new Location()));
                            printStream.print("&NumberOfVisits=" + dbi.count(new Visit()));
                            printStream.print("&NumberOfSightings=" + dbi.count(new Sighting()));
                            printStream.print("&NumberOfFiles=" + dbi.count(new WildLogFile()));
                            printStream.print("&PartialLog=" + logFileSnippit);
                            // Have to get the input stream in order to actually send the request
                            try (InputStream inputStream = con.getInputStream()) {
                                StringBuilder response = new StringBuilder(35);
                                byte[] respBuffer = new byte[1096];
                                while (inputStream.read(respBuffer) >= 0) {
                                    response.append(new String(respBuffer).trim());
                                    respBuffer = new byte[1096]; // Need to get rid of the old bytes that were read (if the last string is shorter)
                                }
                                System.out.println("WEB RESPONSE (uploadWildLogInfo): " + response.toString());
                            }
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }, 20, TimeUnit.SECONDS);
            // Reduce the size of the log file if it is getting too big
            if (logToFile) {
                executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        Path logFile = ACTIVE_WILDLOG_SETTINGS_FOLDER.resolve("errorlog.txt");
                        if (logFile.toFile().length() > 131072) {
                            try {
                                String logFileSnippit;
                                byte[] fileBytes = Files.readAllBytes(logFile);
                                String logInfo = new String(fileBytes, Charset.defaultCharset());
                                int logLength = 99999;
                                if (logInfo.length() > logLength) {
                                    logFileSnippit = "...FILE TRUNCATED..." + System.lineSeparator() + logInfo.substring(logInfo.length() - logLength);
                                }
                                else {
                                    logFileSnippit = logInfo;
                                }
                                Files.write(logFile, logFileSnippit.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                            }
                            catch (IOException ex) {
                                ex.printStackTrace(System.err);
                            }
                        }
                    }
                }, 40, TimeUnit.SECONDS);
            }
            executor.shutdown();
        }
    }

    private boolean openWorkspace() {
        try {
            System.out.println("Opening Workspace at: " + WildLogPaths.getFullWorkspacePrefix().toString());
            dbi = new WildLogDBI_h2();
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
        UtilsDialog.setupGlassPaneOnMainFrame(view);
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
        ACTIVE_WILDLOG_SETTINGS_FOLDER = WildLogPaths.DEFAUL_SETTINGS_FOLDER.getRelativePath().toAbsolutePath().normalize();
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
                ACTIVE_WILDLOG_SETTINGS_FOLDER = Paths.get(props.getProperty("settingsFolderLocation")).normalize().toAbsolutePath().normalize();
            }
            // Get the active application folder
            ACTIVEWILDLOG_CODE_FOLDER = Paths.get(WildLogApp.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
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
                + UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(LocalDateTime.now()));
        System.out.println("WildLog Setting Folder: " + ACTIVE_WILDLOG_SETTINGS_FOLDER.toAbsolutePath().toString());
        System.out.println("WildLog Application Folder: " + ACTIVEWILDLOG_CODE_FOLDER.toAbsolutePath().toString());
        System.out.println("WildLog Version: " + WILDLOG_VERSION);
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
        System.out.println("SHUTTING DOWN WildLog - " + UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(LocalDateTime.now()));
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
            final JFrame mapHandler = (JFrame)mapOffline.getFrameForImageDrawing();
            ActionListener escListiner = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            mapHandler.setVisible(false);
                        }
                    };
            mapHandler.getRootPane().registerKeyboardAction(
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
            mapOnlineFrame.setPreferredSize(new Dimension(950, 650));
            mapOnlineFrame.setLayout(new BorderLayout());
            mapOnlineFrame.setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/WildLog Map Icon.gif")).getImage());
            mapOnline.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
//            mapOnline.setPreferredSize(new Dimension(950, 650));
            mapOnline.setAddressLocationShown(false);
            mapOnline.setName("mapOnline");
            mapOnline.setAddressLocation(defaultPosition);
            mapOnline.setZoom(12);
            mapOnlineFrame.add(mapOnline, BorderLayout.CENTER);
            final WildLogApp app = this;
            // Previous button
            JButton btnPrevMapPoint = new JButton();
            btnPrevMapPoint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mapOnlineFrame.getPointLayer().loadPrevClickedPoint(app);
                }
            });
            btnPrevMapPoint.setPreferredSize(new Dimension(60, 25));
            btnPrevMapPoint.setFocusPainted(false);
            btnPrevMapPoint.setIcon(new ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif")));
            btnPrevMapPoint.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnPrevMapPoint.setToolTipText("Load the previous Observation in the information panel.");
            mapOnlineFrame.add(btnPrevMapPoint, BorderLayout.WEST);
            // Next button
            JButton btnNextMapPoint = new JButton();
            btnNextMapPoint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mapOnlineFrame.getPointLayer().loadNextClickedPoint(app);
                }
            });
            btnNextMapPoint.setPreferredSize(new Dimension(60, 25));
            btnNextMapPoint.setFocusPainted(false);
            btnNextMapPoint.setIcon(new ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif")));
            btnNextMapPoint.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnNextMapPoint.setToolTipText("Load the next Observation in the information panel.");
            mapOnlineFrame.add(btnNextMapPoint, BorderLayout.EAST);
            mapOnlineFrame.pack();
//            mapOnlineFrame.setResizable(false);
            // Setup the escape key
            final JFrame mapHandler = (JFrame)mapOnlineFrame;
            ActionListener escListiner = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            mapHandler.setVisible(false);
                        }
                    };
            mapHandler.getRootPane().registerKeyboardAction(
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

    public void setWildLogOptionsAndSave(WildLogOptions inWildLogOptions) {
        wildLogOptions = inWildLogOptions;
        dbi.createOrUpdate(wildLogOptions);
    }

    public int getThreadCount() {
        return threadCount;
    }

    public static Path getACTIVE_WILDLOG_SETTINGS_FOLDER() {
        return ACTIVE_WILDLOG_SETTINGS_FOLDER;
    }

    public static Path getACTIVEWILDLOG_CODE_FOLDER() {
        return ACTIVEWILDLOG_CODE_FOLDER;
    }

    public WildLogView getMainFrame() {
        return view;
    }
    
    public void checkForUpdates() {
        try {
            // Open a connection to the site
            URL url = new URL("http://www.mywild.co.za/wildlog/getLatestWildLogVersion.php");
            URLConnection con = url.openConnection();
            // Activate the output
            con.setDoOutput(true);
            // Have to get the input stream in order to actually send the request
            try (InputStream inputStream = con.getInputStream()) {
                StringBuilder response = new StringBuilder(5);
                byte[] respBuffer = new byte[1096];
                while (inputStream.read(respBuffer) >= 0) {
                    response.append(new String(respBuffer).trim());
                    respBuffer = new byte[1096]; // Need to get rid of the old bytes that were read (if the last string is shorter)
                }
                if (!WILDLOG_VERSION.equalsIgnoreCase(response.toString())) {
                    System.out.println("WEB RESPONSE (getLatestWildLogVersion): " + response.toString());
                    // Show message with download link
                    JLabel label = new JLabel();
                    Font font = label.getFont();
                    String style = "font-family:" + font.getFamily() + ";font-weight:normal" + ";font-size:" + font.getSize() + "pt;";
                    JEditorPane editorPane = new JEditorPane("text/html", "<html><body style=\"" + style + "\">"
                            + "To download WildLog v" + response.toString() 
                            + " go to <a href=\"http://software.mywild.co.za/p/download-wildlog.html\">http://software.mywild.co.za/p/download-wildlog.html</a>"
                            + " or visit <a href=\"http://software.mywild.co.za\">http://software.mywild.co.za</a> for information about the new release."
                            + "</body></html>");
                    editorPane.addHyperlinkListener(new HyperlinkListener() {
                        @Override
                        public void hyperlinkUpdate(HyperlinkEvent inHyperlinkEvent) {
                            if (inHyperlinkEvent.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                                try {
                                    Desktop.getDesktop().browse(inHyperlinkEvent.getURL().toURI());
                                }
                                catch (IOException | URISyntaxException ex) {
                                    ex.printStackTrace(System.err);
                                }
                            }
                        }
                    });
                    editorPane.setEditable(false);
                    editorPane.setBackground(label.getBackground());
                    JOptionPane.showMessageDialog(WildLogApp.getApplication().getMainFrame(),
                            editorPane,
                            "A new WildLog update is available!", 
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

}
