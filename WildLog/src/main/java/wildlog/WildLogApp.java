package wildlog;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
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
import java.util.Collections;
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
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.ColorUIResource;
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
import wildlog.data.enums.system.WildLogUserTypes;
import wildlog.ui.dialogs.UserLoginDialog;
import wildlog.ui.dialogs.WorkspacePickerDialog;
import wildlog.ui.dialogs.WorkspaceSyncDialog;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.ProgressbarTask;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.inaturalist.helpers.INatProgressbarTask;
import wildlog.utils.UtilsTime;
import wildlog.utils.LoggingPrintStream;
import wildlog.utils.NamedThreadFactory;
import wildlog.utils.UtilsConcurency;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsRestore;
import wildlog.utils.WildLogApplicationTypes;
import wildlog.utils.WildLogPaths;

/**
 * The main class of the application.
 */
// Note: Ek kan nie regtig die SwingAppFramework los nie want die progressbar en paar ander goed gebruik dit. Ek sal dan daai goed moet oorskryf...
public class WildLogApp extends Application {
    private static WildLogApp INSTANCE = null;
    public static String WILDLOG_VERSION = "6.1.1";
    public static Class APPLICATION_CLASS = WildLogApp.class;
    public static WildLogApplicationTypes WILDLOG_APPLICATION_TYPE = WildLogApplicationTypes.WILDLOG;
    public static String WILDLOG_USER_NAME = "WildLogUser"; // Default username (when user management is off)
    public static WildLogUserTypes WILDLOG_USER_TYPE = WildLogUserTypes.OWNER; // Default user type (when user management is off)
    public static Logger LOGGER;
    private static Path ACTIVE_WILDLOG_SETTINGS_FOLDER;
    private static Path ACTIVEWILDLOG_CODE_FOLDER;
    private static String iNaturalistToken;
    private static boolean useNimbusLF = false;
    private static boolean useH2AutoServer = true;
    private static ScheduledExecutorService dailyBackupExecutorService = Executors.newScheduledThreadPool(1, new NamedThreadFactory("WL_DailyBackup"));
    private WildLogView view;
    private WildLogOptions wildLogOptions;
    private int threadCount;
    private boolean triggerSync = false;
    private long triggerSyncWorkspaceID = 0;
    /** 
    * Make sure the application uses the same WildLogDBI instance...
    * The WildLogDBI is initialized in startup() and closed in shutdown()
    * */
    private WildLogDBI dbi;
    
    
    @Override
    protected void initialize(String[] arg0) {
        super.initialize(arg0);
        try {
            // Setup the Look and Feel
            if (useNimbusLF) {
                // Try to set the Nimbus look and feel
                // While the Windows Look and Feel is the primary LF it isn't available on all OSes, but Nimbus LF provides a decent
                // look that is fairly consistant over different OSes. Thus shis should be the default for Linux (Ubuntu) and I guess Mac.
                // Note: Seems like the Application framework already does UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()), so I dont have to do it here.
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
            try {
                // Remove the dotted border around controls which is not consistent with Windows
                UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
                UIManager.put("ToggleButton.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
                UIManager.put("CheckBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
                UIManager.put("TabbedPane.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
                UIManager.put("RadioButton.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
                UIManager.put("Slider.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
                UIManager.put("List.focusCellHighlightBorder", BorderFactory.createEmptyBorder());
                // NOTE: JComboBox kan nie so werk nie, vir dit moet ek 'n nuwe Renderer gebruik. (Sien ComboBoxFixer.java)
            }
            catch (Exception e) {
                WildLogApp.LOGGER.log(Level.INFO, "Could not remove dotted border from focus controls. The application will continue to launch, but there may be some display problems...");
            }
            // Select the workspace to use
            WildLogApp.LOGGER.log(Level.INFO, "Choosing workspace...");
            WorkspacePickerDialog workspacePicker = new WorkspacePickerDialog();
            workspacePicker.setVisible(true);
            if (!workspacePicker.isSelectionMade()) {
                WildLogApp.LOGGER.log(Level.DEBUG, "No workspace was selected...");
                exit();
                return;
            }
            if (Files.notExists(WildLogPaths.getFullWorkspacePrefix()) 
                    || !Files.isWritable(WildLogPaths.getFullWorkspacePrefix()) || !Files.isReadable(WildLogPaths.getFullWorkspacePrefix())) {
                WildLogApp.LOGGER.log(Level.WARN, "Workspace not valid: " + WildLogPaths.getFullWorkspacePrefix().toString());
                WLOptionPane.showMessageDialog(null,
                        "Unable to setup the Workspace at: " + WildLogPaths.getFullWorkspacePrefix().toString(),
                        "Incorrect Workspace!", 
                        JOptionPane.ERROR_MESSAGE);
                exit();
                return;
            }
            triggerSync = workspacePicker.isTriggerImmediateSync();
            triggerSyncWorkspaceID = workspacePicker.getWorkspaceID();
            // Proceed to open the selected workspace
            WildLogApp.LOGGER.log(Level.INFO, "Initializing workspace...");
            // Get the threadcount
            threadCount = Runtime.getRuntime().availableProcessors();
            if (threadCount < 3) {
                threadCount = 3;
            }
            if (threadCount > 10) {
                threadCount = 10;
            }
            WildLogApp.LOGGER.log(Level.INFO, "ThreadPools will be created using {} threads for {} processors.", 
                    threadCount, Runtime.getRuntime().availableProcessors());
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
            boolean busyWithRestore = false;
            do {
                openedWorkspace = !busyWithRestore && openWorkspace();
                if (!busyWithRestore && openedWorkspace == false) {
                    int choice = WLOptionPane.showOptionDialog(getMainFrame(),
                            "<html>The WildLog Workspace at <b>" + WildLogPaths.getFullWorkspacePrefix().toString() + "</b> could not be opened. "
                                    + "<br/>A database upgrade might be in progress, or the Workspace is no longer accessible, or another workspace is open, or the Workspace might been corrupted."
                                    + "<br/>If the problem persists please consult the manual to restore a previous backup or contact support@mywild.co.za for help.</html>",
                            "WildLog Workspace Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, 
                            new String[] { "Restore a Database Backup", "Exit" }, null);
                    if (choice == 0) {
                        busyWithRestore = true;
                        UtilsRestore.doDatabaseRestore();
                    }
                    else {
                        exit(); // Lyk of dit beter werk as quit(null) om een of ander rede...
                    }
                }
            } while (openedWorkspace == false);
            // Load the WildLogOptions
            wildLogOptions = dbi.findWildLogOptions(WildLogOptions.class);
            WildLogApp.LOGGER.log(Level.INFO, "Workspace opened with ID: {} [{}]", new Object[]{wildLogOptions.getWorkspaceName(), Long.toString(wildLogOptions.getWorkspaceID())});
            // Check whether it is time to upload the logs
            if (!triggerSync) {
                uploadLogs();
            }
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            WLOptionPane.showMessageDialog(null,
                    "Unable to initialse the Workspace...",
                    "Startup Error!", 
                    JOptionPane.ERROR_MESSAGE);
            exit();
        }
    }

    private boolean openWorkspace() {
        try {
            WildLogApp.LOGGER.log(Level.INFO, "Opening Workspace database at: {}", WildLogPaths.getFullWorkspacePrefix().toString());
            dbi = new WildLogDBI_h2(!triggerSync, useH2AutoServer);
            // NOTE:
            // The "!triggerSync" above won't prevent the WildLog Options from being created.
            // It still happens in doUpdates() method.
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
        // JavaFX setting to make it faster to start 
        Platform.setImplicitExit(false);
        // Perform login (optional)
        if (dbi.countUsers() > 0) {
            UserLoginDialog dialog = new UserLoginDialog();
            dialog.setVisible(true);
            // Exit if login was incorrect
            if (!dialog.isLoginSuccess()) {
                exit();
                return;
            }
            WildLogApp.LOGGER.log(Level.INFO, "Successful login attempt...");
            // Exit if the user type is not allowed to access this application type
            if ((WILDLOG_USER_TYPE == WildLogUserTypes.VOLUNTEER || WILDLOG_USER_TYPE == WildLogUserTypes.STUDENT)
                    && WILDLOG_APPLICATION_TYPE != WildLogApplicationTypes.WILDLOG_WEI_VOLUNTEER) {
                WLOptionPane.showMessageDialog(null,
                        "This user can only use the Volunteer application.",
                        "Incorrect Application Type!", 
                        JOptionPane.ERROR_MESSAGE);
                exit();
                return;
            }
        }
        // Show the main frame
        view = new WildLogView();
        // Setup the exit listener
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        view.setLocationRelativeTo(null);
        view.setVisible(true);
        // Setup the glasspane
        UtilsDialog.setupGlassPaneOnMainFrame(view);
        // Do the auto backup (after the view has been shown because it uses the progressbar
        Path folderPath = WildLogPaths.WILDLOG_BACKUPS_AUTO.getAbsoluteFullPath()
                .resolve("Backup (" + UtilsTime.WL_DATE_FORMATTER_FOR_AUTO_BACKUP.format(LocalDateTime.now()) + ")");
        if (!Files.exists(folderPath)) {
            // Do the backup
            UtilsConcurency.kickoffProgressbarTask(this, new ProgressbarTask(this) {
                @Override
                protected Object doInBackground() throws Exception {
                    setMessage("Starting the Database Backup (Auto)");
                    // First clean out empty folders (for example failed backups)
                    try {
                        UtilsFileProcessing.deleteRecursiveOnlyEmptyFolders(WildLogPaths.WILDLOG_BACKUPS.getAbsoluteFullPath().toFile());
                    }
                    catch (IOException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                    setMessage("Busy with the Database Backup (Auto)");
                    dbi.doBackup(folderPath.toAbsolutePath());
                    setMessage("Done with the Database Backup (Auto)");
                    return null;
                }
            });
        }
        // For WEI added a scheduled task to kick off daily auto backups
        dailyBackupExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Path folderPath = WildLogPaths.WILDLOG_BACKUPS_DAILY.getAbsoluteFullPath()
                        .resolve("Backup (" + UtilsTime.WL_DATE_FORMATTER_FOR_DAILY_BACKUP.format(LocalDateTime.now()) + ")");
                if (!Files.exists(folderPath)) {
                    // Do the backup
                    UtilsConcurency.kickoffProgressbarTask(WildLogApp.this, new ProgressbarTask(WildLogApp.this) {
                        @Override
                        protected Object doInBackground() throws Exception {
                            setMessage("Starting the Database Backup (Daily)");
                            // First clean out empty folders (for example failed backups)
                            try {
                                UtilsFileProcessing.deleteRecursiveOnlyEmptyFolders(WildLogPaths.WILDLOG_BACKUPS.getAbsoluteFullPath().toFile());
                            }
                            catch (IOException ex) {
                                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                            }
                            setMessage("Busy with the Database Backup (Daily)");
                            dbi.doBackup(folderPath.toAbsolutePath());
                            // If there are more than 7 backups then delete the oldest one
                            List<String> lstDailyBackups = new ArrayList<>(Arrays.asList(WildLogPaths.WILDLOG_BACKUPS_DAILY.getAbsoluteFullPath().toFile().list()));
                            if (lstDailyBackups.size() > 7) {
                                Collections.sort(lstDailyBackups);
                                for (int t = 0; t < lstDailyBackups.size() - 7; t++) {
                                    Path oldBackupToDelete = WildLogPaths.WILDLOG_BACKUPS_DAILY.getAbsoluteFullPath().resolve(lstDailyBackups.get(t));
                                    WildLogApp.LOGGER.log(Level.INFO, "Deleting old daily backup: {}", oldBackupToDelete.toString());
                                    UtilsFileProcessing.deleteRecursive(oldBackupToDelete.toFile());
                                }
                            }
                            setMessage("Done with the Database Backup (Daily)");
                            return null;
                        }
                    });
                }
            }
        }, 3, 60 * 12, TimeUnit.MINUTES);
        // Add the exit listener
        addExitListener(new ExitListener() {
            
            @Override
            public boolean canExit(EventObject inEvent) {
                boolean doShutdown = true;
                try {
                    // Waarsku as daar 'n Bulk Import tab oop is (bang mens druk die close van die image viewer twee keer en maak dan per ongeluk WildLog toe...)
                    boolean foundBulkImport = false;
                    for (int t = 0; t < getMainFrame().getTabbedPane().getTabCount(); t++) {
                        if (getMainFrame().getTabbedPane().getComponentAt(t) instanceof BulkUploadPanel) {
                            foundBulkImport = true;
                            break;
                        }
                    }
                    if (foundBulkImport) {
                        int result = WLOptionPane.showConfirmDialog(getMainFrame(),
                                "<html>There is an unsaved Bulk Import tab. Unsaved changes will be lost."
                                        + "<br><b>Continue to exit WildLog?</b></html>",
                                "Warning! Unsaved Bulk Import...", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (result != JOptionPane.YES_OPTION) {
                            return false;
                        }
                    }
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
                                        + "<br><b>Wait for the processes to finish before exiting WildLog?</b></html>",
                                "Warning! Unfinished Processes...", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (result != JOptionPane.NO_OPTION) {
                            doShutdown = false;
                        }
                        else {
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
                    }
                    // There is a very small chance that a task can be added to the INATQUEUE while the progressbar is busy stopping.
                    // In that case the task might never get processed if there are no future iNat progressbars created to pick it up.
                    // That is why the INATQUEUE is checked before the application exits.
                    else
                    if (INatProgressbarTask.getINatQueueSize() > 0) {
                        WildLogApp.LOGGER.log(Level.WARN, "Not all iNaturalist uploads were processed! There are still {} tasks in the queue.", INatProgressbarTask.getINatQueueSize());
                        int result = WLOptionPane.showConfirmDialog(getMainFrame(),
                                "<html>There are pending iNaturalist uploads. "
                                        + "<br>Would you like to attempt to perform these uploads before exiting WildLog?</html>",
                                "Warning! Unfinished iNaturalist Uploads...", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (result == JOptionPane.YES_OPTION) {
                            INatProgressbarTask iNatTask = new INatProgressbarTask(WildLogApp.getApplication());
                            UtilsConcurency.kickoffProgressbarTask(WildLogApp.getApplication(), iNatTask);
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
            public void willExit(EventObject inEvent) {
                if (dbi != null) {
                    dbi.close();
                    WildLogApp.LOGGER.log(Level.INFO, "Closing workspace...");
                }
            }
        });
    }
    
    @Override
    protected void ready() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Check whether the Sync Dialog should be shown on launch
                if (triggerSync) {
                    // Set the correct workspace ID
                    wildLogOptions = dbi.findWildLogOptions(WildLogOptions.class);
                    wildLogOptions.setID(triggerSyncWorkspaceID);
                    wildLogOptions.setWorkspaceID(triggerSyncWorkspaceID);
                    wildLogOptions.setAuditTime(-1); // Default is 0, so -1 ensures an update
                    dbi.updateWildLogOptions(wildLogOptions, true);
                    // Show the sync popup
                    WorkspaceSyncDialog dialog = new WorkspaceSyncDialog();
                    dialog.setVisible(true);
                }
            }
        });
    }
    
    /**
     * A convenient static getter for the application instance.
     * @return the instance of WildLogApp
     */
    public static WildLogApp getApplication() {
        if (INSTANCE == null) {
            INSTANCE = (WildLogApp) Application.getInstance(APPLICATION_CLASS);
        }
        return INSTANCE;
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
        WildLogApp.LOGGER.log(Level.INFO, "STARTING UP WildLog - {}", UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(LocalDateTime.now()));
        WildLogApp.LOGGER.log(Level.INFO, "WildLog Setting Folder: {}", ACTIVE_WILDLOG_SETTINGS_FOLDER.toAbsolutePath().toString());
        WildLogApp.LOGGER.log(Level.INFO, "WildLog Application Folder: {}", ACTIVEWILDLOG_CODE_FOLDER.toAbsolutePath().toString());
        WildLogApp.LOGGER.log(Level.INFO, "WildLog Version: {}", WILDLOG_VERSION);
        WildLogApp.LOGGER.log(Level.INFO, "WildLog Application Type: {}", WILDLOG_APPLICATION_TYPE);
        WildLogApp.LOGGER.log(Level.INFO, "Command Line Arguments: {}", Arrays.toString(args));
        // Launch the Swing application on the event dispatch thread
        launch(APPLICATION_CLASS, args);
    }

    @Override
    protected void shutdown() {
        dailyBackupExecutorService.shutdown();
        super.shutdown();
        WildLogApp.LOGGER.log(Level.INFO, "SHUTTING DOWN WildLog - {}", UtilsTime.WL_DATE_FORMATTER_WITH_HHMMSS.format(LocalDateTime.now()));
        WildLogApp.LOGGER.log(Level.INFO, "");
        Platform.exit();
        //FIXME: Soms shutdown dit nie reg nie... Java 13 upgrade issue?
    }

    public WildLogDBI getDBI() {
        return dbi;
    }

    public WildLogOptions getWildLogOptions() {
        return wildLogOptions;
    }

    public void setWildLogOptionsAndSave(WildLogOptions inWildLogOptions) {
        wildLogOptions = inWildLogOptions;
        dbi.updateWildLogOptions(wildLogOptions, false);
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
    
    public static String getINaturalistToken() {
        return iNaturalistToken;
    }

    public static void setINaturalistToken(String inINaturalistToken) {
        iNaturalistToken = inINaturalistToken;
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
                    JOptionPane.showMessageDialog(null, // Using JOptionPane and null to prevent the glasspane from being used (it will remove the existing one)
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
    
    private void uploadLogs() {
        // Upload the logs and user data to the MyWild DB once a week (using the existance of the auto backup folder as indicator)
        Path folderPath = WildLogPaths.WILDLOG_BACKUPS_AUTO.getAbsoluteFullPath()
                .resolve("Backup (" + UtilsTime.WL_DATE_FORMATTER_FOR_AUTO_BACKUP.format(LocalDateTime.now()) + ")");
        if (!Files.exists(folderPath)) {
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
                                printStream.print("&NumberOfFiles=" + dbi.countWildLogFiles(0, -1));
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

}
