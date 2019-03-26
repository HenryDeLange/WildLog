package wildlog;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.EventObject;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javafx.application.Platform;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.InsetsUIResource;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.jdesktop.application.Application;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dbi.WildLogDBI;
import wildlog.data.dbi.WildLogDBI_h2;
import wildlog.data.enums.WildLogUserTypes;
import wildlog.ui.dialogs.LoginDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.WLFileChooser;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.helpers.filters.WorkspaceFilter;
import wildlog.ui.utils.WildLogMainView;
import wildlog.utils.UtilsTime;
import wildlog.utils.LoggingPrintStream;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.WildLogApplicationTypes;
import wildlog.utils.WildLogPaths;

/**
 * The main class of the application.
 */
// Note: Ek kan nie regtig die SwingAppFramework los nie want die progressbar en paar ander goed gebruik dit. Ek sal dan daai goed moet oorskryf...
public class WildLogApp extends Application {
    public static String WILDLOG_VERSION = "6.0.0.beta";
    public static WildLogApplicationTypes WILDLOG_APPLICATION_TYPE = WildLogApplicationTypes.WILDLOG;
    public static String WILDLOG_USER_NAME = "WildLogUser"; // Default username (when user management is off)
    public static WildLogUserTypes WILDLOG_USER_TYPE = WildLogUserTypes.OWNER; // Default user type (when user management is off)
    public static Logger LOGGER;
    private static Path ACTIVE_WILDLOG_SETTINGS_FOLDER;
    private static Path ACTIVEWILDLOG_CODE_FOLDER;
    private static String iNaturalistToken;
    private static boolean useNimbusLF = false;
    private static boolean useH2AutoServer = true;
    private static Class viewClass = WildLogView.class;
    private WildLogMainView view;
    private WildLogOptions wildLogOptions;
    private int threadCount;
    /** 
    * Make sure the application uses the same WildLogDBI instance...
    * The WildLogDBI is initialized in startup() and closed in shutdown()
    * */
    private WildLogDBI dbi;


    @Override
    protected void initialize(String[] arg0) {
        WildLogApp.LOGGER.log(Level.INFO, "Initializing workspace...");
        super.initialize(arg0);
        // Get the threadcount
        threadCount = (int)(Runtime.getRuntime().availableProcessors() * 1.5);
        if (threadCount < 3) {
            threadCount = 3;
        }
        // Make sure all the basic data/file folders are in place
        try {
            Files.createDirectories(WildLogPaths.getFullWorkspacePrefix());
            Files.createDirectories(WildLogPaths.WILDLOG_DATA.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_FILES.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_FILES_IMAGES.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_FILES_MOVIES.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_FILES_OTHER.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_THUMBNAILS.getAbsoluteFullPath());
            Files.createDirectories(WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath());
            // Create the workspace indicator file
            Files.write(WildLogPaths.WILDLOG_WORKSPACE_INDICATOR.getAbsoluteFullPath(), 
                    WildLogApp.WILDLOG_VERSION.getBytes(), StandardOpenOption.CREATE);
            // Create the workspace license file that applies to the actual data captured in the workspace
            if (Files.notExists(WildLogPaths.WILDLOG_WORKSPACE_DATA_LICENSE.getAbsoluteFullPath())) {
                UtilsFileProcessing.createFileFromStream(
                        WildLogApp.class.getResourceAsStream("/" + WildLogPaths.WILDLOG_WORKSPACE_DATA_LICENSE.getRelativePath().toString()), 
                        WildLogPaths.WILDLOG_WORKSPACE_DATA_LICENSE.getAbsoluteFullPath());
            }
            // Create the workspace read only folder indicators
            if (Files.notExists(WildLogPaths.WILDLOG_DATA_READONLY_INDICATOR.getAbsoluteFullPath())) {
                UtilsFileProcessing.createFileFromBytes("The files in this folder are managed by WildLog. Never move or delete a file from this folder.".getBytes(),
                        WildLogPaths.WILDLOG_DATA_READONLY_INDICATOR.getAbsoluteFullPath());
            }
            if (Files.notExists(WildLogPaths.WILDLOG_FILES_READONLY_INDICATOR.getAbsoluteFullPath())) {
                UtilsFileProcessing.createFileFromBytes("The files in this folder are managed by WildLog. Never move or delete a file from this folder.".getBytes(),
                        WildLogPaths.WILDLOG_FILES_READONLY_INDICATOR.getAbsoluteFullPath());
            }
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        // Open the database
        // If this fails then it might be corrupt or already open. Ask the user to select a new workspace folder.
        boolean openedWorkspace;
        do {
            openedWorkspace = openWorkspace();
            if (openedWorkspace == false) {
                int choice = WLOptionPane.showConfirmDialog(getMainFrame(),
                        "<html>The WildLog Workspace at <b>" + WildLogPaths.getFullWorkspacePrefix().toString() + "</b> could not be opened. "
                                + "<br/>It is likely that another instance of WildLog already has this Workspace open."
                                + "<br/>If the problem persists please consult the Manual or contact support@mywild.co.za for help."
                                + "<br/><br/>You can <b>press OK to select another Workspace</b>, or press Cancel to close this instance of WildLog.</html>",
                        "WildLog Workspace Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                if (choice == JOptionPane.OK_OPTION) {
                    configureWildLogHomeBasedOnFileBrowser(null, true);
                }
                else {
                    quit(null);
                }
            }
        }
        while (openedWorkspace == false);
        // Load the WildLogOptions
        wildLogOptions = dbi.findWildLogOptions(WildLogOptions.class);
        WildLogApp.LOGGER.log(Level.INFO, "Workspace opened with ID: {} [{}]", new Object[]{wildLogOptions.getWorkspaceName(), Long.toString(wildLogOptions.getWorkspaceID())});
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
                    WildLogApp.LOGGER.log(Level.INFO, "Latest WildLog version: {}", checkForUpdates());
                }
            }, 10, TimeUnit.SECONDS);
            // Try to upload log data
            if (wildLogOptions.isUploadLogs()) {
                executor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        WildLogApp.LOGGER.log(Level.INFO, "WEB CALL: Start uploading log file...");
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
                                String logFileSnippit = "";
                                ByteArrayOutputStream logFileZip = new ByteArrayOutputStream();
                                try {
                                    byte[] fileBytes = Files.readAllBytes(ACTIVE_WILDLOG_SETTINGS_FOLDER.resolve("wildlog_errorlog.txt")); // The first (main) log file
                                    String logInfo = new String(fileBytes, Charset.defaultCharset());
                                    int errorCount = -1;
                                    int startIndex = 0;
                                    while (startIndex >= 0) {
                                        startIndex = logInfo.indexOf("ERROR", startIndex + 1);
                                        errorCount++;
                                    }
                                    logFileSnippit = "Recent Error Count = " + errorCount;
                                }
                                catch (IOException  ex) {
                                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                }
                                // Get a Zipped archive of all the error logs
                                List<String> fileList = new ArrayList<>(Arrays.asList(ACTIVE_WILDLOG_SETTINGS_FOLDER.toFile().list()));
                                if (fileList != null && !fileList.isEmpty()) {
                                    for (int t = fileList.size() - 1; t >= 0; t--) {
                                        if (!fileList.get(t).startsWith("wildlog_errorlog")) {
                                            fileList.remove(t);
                                        }
                                    }
                                    if (!fileList.isEmpty()) {
                                        byte[] buffer = new byte[1024];
                                        try {
                                            try (ZipOutputStream zipOutputStream = new ZipOutputStream(logFileZip)) {
                                                for (String fileString : fileList) {
                                                    ZipEntry zipEntry = new ZipEntry(fileString);
                                                    zipOutputStream.putNextEntry(zipEntry);
                                                    try (FileInputStream fileInputStream = new FileInputStream(ACTIVE_WILDLOG_SETTINGS_FOLDER.toAbsolutePath().resolve(fileString).toString())) {
                                                        int len;
                                                        while ((len = fileInputStream.read(buffer)) > 0) {
                                                            zipOutputStream.write(buffer, 0, len);
                                                        }
                                                    }
                                                }
                                                zipOutputStream.closeEntry();
                                            }
                                        }
                                        catch (IOException ex) {
                                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                        }
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
                                printStream.print("&NumberOfElements=" + dbi.countElements(null, null));
                                printStream.print("&NumberOfLocations=" + dbi.countLocations(null));
                                printStream.print("&NumberOfVisits=" + dbi.countVisits(null, 0));
                                printStream.print("&NumberOfSightings=" + dbi.countSightings(0, 0, 0, 0));
                                printStream.print("&NumberOfFiles=" + dbi.countWildLogFiles(null, null));
                                printStream.print("&PartialLog=" + logFileSnippit);
                                printStream.print("&ZippedLog=" + Base64.getEncoder().encodeToString(logFileZip.toByteArray()).replaceAll("\\+", "%2B"));
                                // Have to get the input stream in order to actually send the request
                                try (InputStream inputStream = con.getInputStream()) {
                                    StringBuilder response = new StringBuilder(35);
                                    byte[] respBuffer = new byte[1096];
                                    while (inputStream.read(respBuffer) >= 0) {
                                        response.append(new String(respBuffer).trim());
                                        respBuffer = new byte[1096]; // Need to get rid of the old bytes that were read (if the last string is shorter)
                                    }
                                    WildLogApp.LOGGER.log(Level.INFO, "WEB RESPONSE (uploadWildLogInfo): {}", response.toString());
                                }
                            }
                        }
                        catch (Exception ex) {
                            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                        }
                    }
                }, 20, TimeUnit.SECONDS);
                executor.shutdown();
            }
        }
    }

    private boolean openWorkspace() {
        try {
            WildLogApp.LOGGER.log(Level.INFO, "Opening Workspace at: {}", WildLogPaths.getFullWorkspacePrefix().toString());
            dbi = new WildLogDBI_h2(useH2AutoServer);
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "Could not open the Workspace. Will try to ask the user to try a new Workspace folder.");
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex); // This will technicall log the error twice, but doing it to playsafe
            return false;
        }
        return true;
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        Platform.setImplicitExit(false);
        if (useNimbusLF) {
            // Try to set the Nimbus look and feel
            // While the Windows Look and Feel is the primary LF it isn't available on all OSes, but Nimbus LF provides a decent
            // look that is fairly consistant over different OSes. Thus shis should be the default for Linux (Ubuntu) and I guess Mac.
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        // Make the global button margins smaller, because Nimbus ignores the setting on the buttons
                        UIManager.getLookAndFeelDefaults().put("Button.contentMargins", new InsetsUIResource(2,2,2,2));
                        UIManager.getLookAndFeelDefaults().put("ToggleButton.contentMargins", new InsetsUIResource(2,2,2,2));
                        UIManager.getLookAndFeelDefaults().put("OptionPane.sameSizeButtons", true);
                        break;
                    }
                }
            }
            catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                // We'll try to go ahead without the Nimbus LookAndFeel
                WildLogApp.LOGGER.log(Level.INFO, "Could not load the Nimbus Look and Feel. The application will continue to launch, but there may be some display problems...");
            }
        }
        // Perform login (optional)
        if (dbi.countUsers() > 0) {
            LoginDialog dialog = new LoginDialog(null);
            dialog.setVisible(true);
            // Exit if login was incorrect
            if (!dialog.isLoginSuccess()) {
                WildLogApp.LOGGER.log(Level.WARN, "Failed login attempt...");
                WLOptionPane.showMessageDialog(null,
                        "This Workspace can only be accessed using a valid username and password.",
                        "Incorrect Login!", 
                        JOptionPane.ERROR_MESSAGE);
                exit();
                return;
            }
            WildLogApp.LOGGER.log(Level.INFO, "Successful login attempt...");
            // Exit if the user type is not allowed to access this application type
            if (WILDLOG_USER_TYPE == WildLogUserTypes.VOLUNTEER && WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
                WLOptionPane.showMessageDialog(null,
                        "This user can only use the Volunteer application.",
                        "Incorrect Application Type!", 
                        JOptionPane.ERROR_MESSAGE);
                exit();
                return;
            }
        }
        try {
            // Show the main frame
            view = (WildLogMainView) viewClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            WLOptionPane.showMessageDialog(null,
                    "The main view could not be created during startup...",
                    "Startup Error!", 
                    JOptionPane.ERROR_MESSAGE);
            exit();
        }
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
                        int result = WLOptionPane.showConfirmDialog(getMainFrame(),
                                "<html>There are background processes running that have not finished yet. "
                                        + "<br>It is <b>recommended to wait for these processes to finish</b>."
                                        + "<br>(See the progressbar at the bottom right hand corner for details.)"
                                        + "<br><b>Continue to Exit WildLog?</b></html>",
                                "Warning! Unfinished Processes...", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (result == JOptionPane.YES_OPTION) {
                            WildLogApp.LOGGER.log(Level.INFO, "Trying to stop running processes before exiting...");
                            // Try to stop the running processes before the DB gets closed
                            TaskService taskService = getContext().getTaskService();
                            taskService.shutdownNow();
                            try {
                                taskService.awaitTermination(3, TimeUnit.SECONDS);
                            }
                            catch (InterruptedException ex) {
                                // This will break lots of threads... SO lets just ignore the errors :P
                                //LOGGER.log(Level.SEVERE, ex.toString(), ex);
                            }
                        }
                        else {
                            doShutdown = false;
                        }
                    }
                }
                catch (Exception ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
                return doShutdown;
            }
            @Override
            public void willExit(EventObject event) {
                if (dbi != null) {
                    dbi.close();
                    WildLogApp.LOGGER.log(Level.INFO, "Closing workspace...");
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
        System.out.println("Startup args = " + Arrays.toString(args));
        // Set default startup settings
        ACTIVE_WILDLOG_SETTINGS_FOLDER = WildLogPaths.DEFAUL_SETTINGS_FOLDER.getRelativePath().toAbsolutePath().normalize();
        System.setProperty("settingsFolderLocation", ACTIVE_WILDLOG_SETTINGS_FOLDER.toAbsolutePath().toString());
        WildLogApp.LOGGER = LogManager.getLogger("WildLogApp");
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
            // Print the properties to the logs
            WildLogApp.LOGGER.log(Level.INFO, "WildLog Properties: {}", props.toString());
            // Set the settings
            useNimbusLF = Boolean.parseBoolean(props.getProperty("useNimbus"));
            useH2AutoServer = Boolean.parseBoolean(props.getProperty("useAutoServer"));
            if (props.getProperty("settingsFolderLocation") != null && !props.getProperty("settingsFolderLocation").trim().isEmpty()) {
                ACTIVE_WILDLOG_SETTINGS_FOLDER = Paths.get(props.getProperty("settingsFolderLocation")).normalize().toAbsolutePath().normalize();
                // Once the setting folder has been determined the logger can be reconfigured to use it instead of the default one
                System.setProperty("settingsFolderLocation", ACTIVE_WILDLOG_SETTINGS_FOLDER.toAbsolutePath().toString());
                LoggerContext context = (LoggerContext) LogManager.getContext(false);
                context.reconfigure();
            }
            // Get the active application folder
            ACTIVEWILDLOG_CODE_FOLDER = Paths.get(WildLogApp.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        }
        catch (IOException | URISyntaxException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            WLOptionPane.showMessageDialog(null,
                        "Could not load the settings from the properties on startup. WildLog will continue to start using the default settings.",
                        "Problem Starting WildLog", 
                        JOptionPane.WARNING_MESSAGE);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
            }
        }
        // After the startup properties have been loaded continue to start the application
        try {
            // Make sure the Settings folder exists
            Files.createDirectories(ACTIVE_WILDLOG_SETTINGS_FOLDER);
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        // Setup the Logger to include the uncaught exceptions (usually printed to System.err)
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                WildLogApp.LOGGER.log(Level.ERROR, e.getMessage(), e);
            }
        });
        // Setup the System.out and System.err to redirect to the Logger
        System.setOut(new LoggingPrintStream(System.out, Level.INFO));
        System.setErr(new LoggingPrintStream(System.err, Level.ERROR));
        // Add a new line between application startups
        WildLogApp.LOGGER.log(Level.INFO, "");
        // Try to read the settings file containing the wildloghome (active workspace)
        try {
            configureWildLogHomeBasedOnSettingsFile();
        }
        catch (IOException ex) {
            // Daar was 'n probleem om die wildloghome settings file te lees, probeer om 'n nuwe wildloghome file te maak
            WildLogApp.LOGGER.log(Level.INFO, "Could not find the wildloghome file. Will try to create it...");
            WildLogApp.LOGGER.log(Level.INFO, ex.toString(), ex);
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
                WildLogApp.LOGGER.log(Level.ERROR, ioex.toString(), ioex);
            }
            finally {
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    }
                    catch (IOException ioex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ioex.toString(), ioex);
                    }
                }
            }
            // As ek steeds nie 'n wildloghome file kan gelees kry nie vra die user vir 'n wildloghome om te gebruik
            try {
                configureWildLogHomeBasedOnSettingsFile();
            }
            catch (IOException ioex) {
                WildLogApp.LOGGER.log(Level.ERROR, ioex.toString(), ioex);
                configureWildLogHomeBasedOnFileBrowser(null, true);
            }
        }
        WildLogApp.LOGGER.log(Level.INFO, "STARTING UP WildLog - {}", UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(LocalDateTime.now()));
        WildLogApp.LOGGER.log(Level.INFO, "WildLog Setting Folder: {}", ACTIVE_WILDLOG_SETTINGS_FOLDER.toAbsolutePath().toString());
        WildLogApp.LOGGER.log(Level.INFO, "WildLog Application Folder: {}", ACTIVEWILDLOG_CODE_FOLDER.toAbsolutePath().toString());
        WildLogApp.LOGGER.log(Level.INFO, "WildLog Version: {}", WILDLOG_VERSION);
        WildLogApp.LOGGER.log(Level.INFO, "WildLog Application Type: {}", WILDLOG_APPLICATION_TYPE);
        WildLogApp.LOGGER.log(Level.INFO, "Command Line Arguments: {}", Arrays.toString(args));
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
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
            }
        }
    }

    public static boolean configureWildLogHomeBasedOnFileBrowser(final JFrame inParent, boolean inTerminateIfNotSelected) {
        WLFileChooser fileChooser = new WLFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setDialogTitle("Please select the WildLog Workspace to use.");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setFileFilter(new WorkspaceFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setMultiSelectionEnabled(false);
        int result = fileChooser.showOpenDialog(inParent);
        if (result == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
            Path selectedPath;
            if (fileChooser.getSelectedFile().isDirectory()) {
                selectedPath = fileChooser.getSelectedFile().toPath();
            }
            else {
                selectedPath = fileChooser.getSelectedFile().getParentFile().toPath();
            }
            WildLogPaths.setWorkspacePrefix(selectedPath.toAbsolutePath().toString());
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
        WildLogApp.LOGGER.log(Level.INFO, "SHUTTING DOWN WildLog - {}", UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(LocalDateTime.now()));
        WildLogApp.LOGGER.log(Level.INFO, "");
        Platform.exit();
    }

    public WildLogDBI getDBI() {
        return dbi;
    }

    public WildLogOptions getWildLogOptions() {
        return wildLogOptions;
    }

    public void setWildLogOptionsAndSave(WildLogOptions inWildLogOptions) {
        wildLogOptions = inWildLogOptions;
        dbi.updateWildLogOptions(wildLogOptions);
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

    public WildLogMainView getMainFrame() {
        return view;
    }
    
    public String checkForUpdates() {
        try {
            // Open a connection to the site
            WildLogApp.LOGGER.log(Level.INFO, "WEB CALL (getLatestWildLogVersion)");
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
                    WildLogApp.LOGGER.log(Level.INFO, "WEB RESPONSE (getLatestWildLogVersion): {}", response.toString());
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
                                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                }
                            }
                        }
                    });
                    editorPane.setEditable(false);
                    editorPane.setBackground(label.getBackground());
                    WLOptionPane.showMessageDialog(getMainFrame(),
                            editorPane,
                            "A new WildLog update is available!", JOptionPane.INFORMATION_MESSAGE);
                }
                return response.toString();
            }
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return "Unknown";
    }

    public static String getINaturalistToken() {
        return iNaturalistToken;
    }

    public static void setINaturalistToken(String inINaturalistToken) {
        iNaturalistToken = inINaturalistToken;
    }

    public static void setViewClass(Class inViewClass) {
        viewClass = inViewClass;
    }

}
