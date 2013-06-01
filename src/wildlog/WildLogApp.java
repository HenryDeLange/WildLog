package wildlog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.jdesktop.application.Application;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dbi.DBI;
import wildlog.data.dbi.DBI_h2;
import wildlog.mapping.MapFrameOffline;
import wildlog.mapping.MapFrameOnline;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.utils.WildLogPaths;

/**
 * The main class of the application.
 */
// Note: Ek kan nie regtig die SwingAppFramework los nie want die progressbar en paar ander goed gebruik dit. Ek sal dan daai goed moet oorskryf...
public class WildLogApp extends Application {
    private static String WILDLOG_SETTINGS_FOLDER = (System.getProperty("user.home") + File.separatorChar + "WildLog Settings" + File.separatorChar);
    // Only open one MapFrame for the application (to reduce memory use)
    private MapFrameOffline mapOffline;
    private JXMapKit mapOnline;
    private MapFrameOnline mapOnlineFrame;
    // Other settings
    private WildLogOptions wildLogOptions;
    private int threadCount;
    // Make sure the application uses the same DBI instance...
    // The DBI is initialized in startup() and closed in shutdown()
    private DBI dbi;
    private WildLogView view;

    @Override
    protected void initialize(String[] arg0) {
        System.out.println("Initializing workspace...");
        super.initialize(arg0);
        // Get the threadcount
        threadCount = (int)(Runtime.getRuntime().availableProcessors() * 1.5);
        if (threadCount < 3)
            threadCount = 3;
        // Makse sure all the basic data/file folders are in place
        File tempFolder = new File(WildLogPaths.WILDLOG_DATA.getFullPath());
        tempFolder.mkdirs();
        tempFolder = new File(WildLogPaths.WILDLOG_FILES.getFullPath());
        tempFolder.mkdirs();
        tempFolder = new File(WildLogPaths.WILDLOG_FILES_IMAGES.getFullPath());
        tempFolder.mkdirs();
        tempFolder = new File(WildLogPaths.WILDLOG_FILES_MOVIES.getFullPath());
        tempFolder.mkdirs();
        tempFolder = new File(WildLogPaths.WILDLOG_FILES_OTHER.getFullPath());
        tempFolder.mkdirs();
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
        File dirs = new File(WildLogPaths.WILDLOG_BACKUPS_MONTHLY.getFullPath() + "Backup (" + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()) + ")");
        if (!dirs.exists()) {
            dbi.doBackup(WildLogPaths.WILDLOG_BACKUPS_MONTHLY);
        }
        // Load the WildLogOptions
        wildLogOptions = dbi.find(new WildLogOptions());
    }

    private boolean openWorkspace() {
        try {
            dbi = new DBI_h2(this);
            System.out.println("Workspace opened at: " + WildLogPaths.concatPaths(true, WildLogPaths.getFullWorkspacePrefix(), WildLogPaths.WILDLOG.toString()));
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
        // Show the main frame
        view = new WildLogView(this);
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent inEvent) {
                quit(null);
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
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of WildLogBetaApp
     */
    public static WildLogApp getApplication() {
        return Application.getInstance(WildLogApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        boolean logToFile = false;
        // Check for a overwritten settings folder and configuration to log to a logging file or not
        if (args != null && args.length >= 1) {
            for (String arg : args) {
                if (arg != null && arg.toLowerCase().startsWith("settings_folder_location=".toLowerCase())) {
                    // Voorbeeld: settings_folder_location="./WildLog Settings/"
                    WILDLOG_SETTINGS_FOLDER = arg.substring("settings_folder_location=".length());
                }
                else
                if (arg != null && "log_to_file".equalsIgnoreCase(arg.toLowerCase())) {
                    logToFile = true;
                }
// Die storie werk nie regtig in Ubuntu nie...
//                else
//                if (arg != null && arg.startsWith("font_size=")) {
//                    // Change the font size to a standard size (in an attempt to have the panels render the same on other platforms, especially Ubuntu...).. Dit werk nie in Ubuntu nie...
//                    try {
//                        int newFontSize = Integer.parseInt(arg.substring("font_size=".length()));
//                        UIDefaults uiDefaults = UIManager.getDefaults();
//                        Enumeration keys = uiDefaults.keys();
//                        while (keys.hasMoreElements()) {
//                            Object key = keys.nextElement();
//                            if ((key instanceof String) && (((String)key).endsWith(".font"))) {
//                                FontUIResource font = (FontUIResource)UIManager.get(key);
//                                uiDefaults.put(key, new FontUIResource(font.getName(), font.getStyle(), newFontSize));
//                            }
//                        }
//                    }
//                    catch (NumberFormatException ex) {
//                        ex.printStackTrace(System.err);
//                    }
//                }
            }
        }
        // Make sure the Settings folder exists
        File folder = new File(WILDLOG_SETTINGS_FOLDER);
        folder.mkdirs();
        // Enable logging to file
        if (logToFile) {
            try {
                // Saving the orginal stream
                PrintStream fileStream = new PrintStream(new FileOutputStream(WildLogPaths.concatPaths(true, WILDLOG_SETTINGS_FOLDER, "errorlog.txt"), true));
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
                writer = new FileWriter(WildLogPaths.concatPaths(true, WILDLOG_SETTINGS_FOLDER, "wildloghome"));
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
        System.out.println("WildLog Setting Folder: " + WILDLOG_SETTINGS_FOLDER);
        // Launch the application
        launch(WildLogApp.class, args);
    }

    private static void configureWildLogHomeBasedOnSettingsFile() throws IOException {
        // Using try-with-resource to close the reader when done, the error should still be thrown if something goes wrong
        try (BufferedReader reader = new BufferedReader(new FileReader(WildLogPaths.concatPaths(true, WILDLOG_SETTINGS_FOLDER, "wildloghome")))) {
            WildLogPaths.setWorkspacePrefix(reader.readLine());
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
        view.setVisible(false);
        view.dispose();
        if (dbi != null)
            dbi.close();
        System.out.println("SHUTTING DOWN WildLog - "
                + new SimpleDateFormat("dd MMM yyyy (HH:mm:ss)").format(Calendar.getInstance().getTime()));
        System.out.println();
    }

    public DBI getDBI() {
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

    public static String getWILDLOG_SETTINGS_FOLDER() {
        return WILDLOG_SETTINGS_FOLDER;
    }

    public WildLogView getMainFrame() {
        return view;
    }

}
