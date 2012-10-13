package wildlog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dbi.DBI;
import wildlog.data.dbi.DBI_h2;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.MapFrameOffline;
import wildlog.mapping.MapFrameOnline;
import wildlog.utils.FilePaths;

/**
 * The main class of the application.
 */
public class WildLogApp extends SingleFrameApplication {
    // Variables - This is actualy a very bad hack, but its the easiest and quickest to do for now...
    private Latitudes prevLat;
    private int prevLatDeg;
    private int prevLatMin;
    private float prevLatSec;
    private Longitudes prevLon;
    private int prevLonDeg;
    private int prevLonMin;
    private float prevLonSec;
    private boolean useOnlineMap = true;
    // Only open one MapFrame for the application (to reduce memory use)
    private MapFrameOffline mapOffline;
    private JXMapKit mapOnline;
    private MapFrameOnline mapOnlineFrame;
    private int threadCount;
    // Make sure the application uses the same DBI instance...
    // The DBI is initialized in startup() and closed in shutdown()
    private DBI dbi;

    // Getters and Setters
    public Latitudes getPrevLat() {
        if (prevLat == null)
            prevLat = Latitudes.NONE;
        return prevLat;
    }

    public void setPrevLat(Latitudes inPrevLat) {
        prevLat = inPrevLat;
    }

    public int getPrevLatDeg() {
        return prevLatDeg;
    }

    public void setPrevLatDeg(int inPrevLatDeg) {
        prevLatDeg = inPrevLatDeg;
    }

    public int getPrevLatMin() {
        return prevLatMin;
    }

    public void setPrevLatMin(int inPrevLatMin) {
        prevLatMin = inPrevLatMin;
    }

    public float getPrevLatSec() {
        return prevLatSec;
    }

    public void setPrevLatSec(float inPrevLatSec) {
        prevLatSec = inPrevLatSec;
    }

    public Longitudes getPrevLon() {
        if (prevLon == null)
            prevLon = Longitudes.NONE;
        return prevLon;
    }

    public void setPrevLon(Longitudes inPrevLon) {
        prevLon = inPrevLon;
    }

    public int getPrevLonDeg() {
        return prevLonDeg;
    }

    public void setPrevLonDeg(int inPrevLonDeg) {
        prevLonDeg = inPrevLonDeg;
    }

    public int getPrevLonMin() {
        return prevLonMin;
    }

    public void setPrevLonMin(int inPrevLonMin) {
        prevLonMin = inPrevLonMin;
    }

    public float getPrevLonSec() {
        return prevLonSec;
    }

    public void setPrevLonSec(float inPrevLonSec) {
        prevLonSec = inPrevLonSec;
    }


    @Override
    protected void initialize(String[] arg0) {
        System.out.println("STARTING UP WildLog..."
                + new SimpleDateFormat("dd MMM yyyy (HH:mm:ss)").format(Calendar.getInstance().getTime()));
        super.initialize(arg0);
        // Get the threadcount
        threadCount = (int)(Runtime.getRuntime().availableProcessors() * 1.5);
        if (threadCount < 3)
            threadCount = 3;
        File dataFolder = new File(FilePaths.WILDLOG_DATA.getFullPath());
        dataFolder.mkdirs();
        File imagesFolder = new File(FilePaths.WILDLOG_IMAGES.getFullPath());
        imagesFolder.mkdirs();
        //dbi = new DBI_derby();
        dbi = new DBI_h2();
        // Check to do monthly backup
        File dirs = new File(FilePaths.WILDLOG_BACKUPS_MONTHLY.getFullPath() + "Backup (" + new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()) + ")");
        if (!dirs.exists()) {
            dbi.doBackup(FilePaths.WILDLOG_BACKUPS_MONTHLY);
        }
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        // Show the main frame
        ImageIcon icon = new ImageIcon(getClass().getResource("resources/icons/WildLog Icon.gif"));
        WildLogView view = new WildLogView(this);
        view.getFrame().setIconImage(icon.getImage());
        show(view);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
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
        // Make sure the Settings folder exists
        File folder = new File(FilePaths.WILDLOG_SETTINGS.toString());
        folder.mkdirs();
        // Configure to log to a logging file
        if (args != null && args.length == 1) {
            if ("log_to_file".equalsIgnoreCase(args[0])) {
                try {
                    PrintStream fileStream = null;
                    // Saving the orginal stream
                    fileStream = new PrintStream(new FileOutputStream(FilePaths.WILDLOG_SETTINGS.toString() + "errorlog.txt", true));
                    // Redirecting console output to file
                    System.setOut(fileStream);
                    // Redirecting runtime exceptions to file
                    System.setErr(fileStream);
                }
                catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
        // Try to read the workspace file
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(FilePaths.WILDLOG_SETTINGS.toString() + "wildloghome"));
            FilePaths.setWorkspacePrefix(reader.readLine());
        }
        catch (IOException ex) {
            ex.printStackTrace();
            FileWriter writer = null;
            try {
                writer = new FileWriter(FilePaths.WILDLOG_SETTINGS.toString() + "wildloghome");
                writer.write(File.separator);
            }
            catch (IOException ioex) {
                ioex.printStackTrace();
            }
            finally {
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    }
                    catch (IOException ioex) {
                        ioex.printStackTrace();
                    }
                }
            }
            // Try to load the new file
            try {
                BufferedReader reader = new BufferedReader(
                        new FileReader(FilePaths.WILDLOG_SETTINGS.toString() + "wildloghome"));
                FilePaths.setWorkspacePrefix(reader.readLine());
            }
            catch (IOException ioex) {
                ioex.printStackTrace();
                System.exit(-1);
            }
        }
        // Launch the application
        launch(WildLogApp.class, args);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        if (dbi != null)
            dbi.close();
        System.out.println("SHUTTING DOWN WildLog - "
                + new SimpleDateFormat("dd MMM yyyy (HH:mm:ss)").format(Calendar.getInstance().getTime()));
    }


    public DBI getDBI() {
        return dbi;
    }


    public MapFrameOffline getMapOffline() {
        // Setup MapFrame - Note: If this is in the constructor the frame keeps poping up when the application starts
        if (mapOffline == null) {
            WildLogOptions options = getDBI().find(new WildLogOptions());
            mapOffline = new MapFrameOffline("WildLog Map - Offline", options.getDefaultLatitude(), options.getDefaultLongitude()/*, useOnlineMap*/);
        }
        return mapOffline;
    }

    public MapFrameOnline getMapOnline() {
        // Setup MapFrame - Note: If this is in the constructor the frame keeps poping up when the application starts
        if (mapOnlineFrame == null) {
            WildLogOptions options = getDBI().find(new WildLogOptions());
            final GeoPosition defaultPosition = new GeoPosition(options.getDefaultLatitude(), options.getDefaultLongitude());

            mapOnline = new JXMapKit();

            mapOnlineFrame = new MapFrameOnline("WildLog Map - Online", mapOnline, this);
            mapOnlineFrame.setPreferredSize(new Dimension(758, 560));
            mapOnlineFrame.setLayout(new AbsoluteLayout());
            ImageIcon icon = new ImageIcon(Application.getInstance().getClass().getResource("resources/icons/WildLog Map Icon.gif"));
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
        }
        return mapOnlineFrame;
    }

    public void clearOnlinemap() {
        mapOnlineFrame = null;
    }

    public boolean isUseOnlineMap() {
        return useOnlineMap;
    }

    public void setUseOnlineMap(boolean useOnlineMap) {
        this.useOnlineMap = useOnlineMap;
    }

    public int getThreadCount() {
        return threadCount;
    }

}
