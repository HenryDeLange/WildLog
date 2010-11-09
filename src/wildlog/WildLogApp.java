package wildlog;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.wms.WMSService;
import org.jdesktop.swingx.mapviewer.wms.WMSTileFactory;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.data.dataobjects.WildLogOptions;
import wildlog.data.dbi.DBI;
import wildlog.data.dbi.DBI_h2;
import wildlog.data.enums.Latitudes;
import wildlog.data.enums.Longitudes;
import wildlog.mapping.MapFrameOffline;
import wildlog.mapping.MapFrameOnline;

/**
 * The main class of the application.
 */
public class WildLogApp extends SingleFrameApplication {
    // Variables - This is actualy a very bad hack, but its the easiest and quickest to do for now...
    private Latitudes prevLat;
    private String prevLatDeg;
    private String prevLatMin;
    private String prevLatSec;
    private Longitudes prevLon;
    private String prevLonDeg;
    private String prevLonMin;
    private String prevLonSec;
    private boolean useOnlineMap = true;
    // Only open one MapFrame for the application (to reduce memory use)
    private MapFrameOffline mapOffline;
    private JXMapKit mapOnline;
    private MapFrameOnline mapOnlineFrame;

    // Getters and Setters
    public Latitudes getPrevLat() {
        if (prevLat == null)
            prevLat = Latitudes.NONE;
        return prevLat;
    }

    public void setPrevLat(Latitudes prevLat) {
        this.prevLat = prevLat;
    }

    public String getPrevLatDeg() {
        return prevLatDeg;
    }

    public void setPrevLatDeg(String prevLatDeg) {
        this.prevLatDeg = prevLatDeg;
    }

    public String getPrevLatMin() {
        return prevLatMin;
    }

    public void setPrevLatMin(String prevLatMin) {
        this.prevLatMin = prevLatMin;
    }

    public String getPrevLatSec() {
        return prevLatSec;
    }

    public void setPrevLatSec(String prevLatSec) {
        this.prevLatSec = prevLatSec;
    }

    public Longitudes getPrevLon() {
        if (prevLon == null)
            prevLon = Longitudes.NONE;
        return prevLon;
    }

    public void setPrevLon(Longitudes prevLon) {
        this.prevLon = prevLon;
    }

    public String getPrevLonDeg() {
        return prevLonDeg;
    }

    public void setPrevLonDeg(String prevLonDeg) {
        this.prevLonDeg = prevLonDeg;
    }

    public String getPrevLonMin() {
        return prevLonMin;
    }

    public void setPrevLonMin(String prevLonMin) {
        this.prevLonMin = prevLonMin;
    }

    public String getPrevLonSec() {
        return prevLonSec;
    }

    public void setPrevLonSec(String prevLonSec) {
        this.prevLonSec = prevLonSec;
    }


    @Override
    protected void initialize(String[] arg0) {
        System.out.println("STARTING UP WildLog...");
        super.initialize(arg0);
        File dataFolder = new File(File.separatorChar + "WildLog" + File.separatorChar + "Data" + File.separatorChar);
        dataFolder.mkdirs();
        File imagesFolder = new File(File.separatorChar + "WildLog" + File.separatorChar + "Images" + File.separatorChar);
        imagesFolder.mkdirs();

        //dbi = new DBI_derby();
        dbi = new DBI_h2();
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
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
        launch(WildLogApp.class, args);
    }

    @Override
    protected void shutdown() {
        super.shutdown();
        dbi.close();
        System.out.println("SHUTTING DOWN WildLog");
    }
    
    
    // Make sure the application uses the same DBI instance...
    // Might want to think of other ways to do this, but seems ok for now
    // The DBI is initialized in startup() and closed in shutdown()
    private DBI dbi;
    
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
            mapOnlineFrame.add(btnPrevMapPoint, new AbsoluteConstraints(0, 500, -1, -1));

            JButton btnNextMapPoint = new JButton("Next");
            btnNextMapPoint.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mapOnlineFrame.getPointLayer().loadNextClickedPoint(app);
                }
            });
            btnNextMapPoint.setPreferredSize(new Dimension(70, 25));
            mapOnlineFrame.add(btnNextMapPoint, new AbsoluteConstraints(70, 500, -1, -1));

            JButton btnLoadOpenStreetMap = new JButton("Open Street Map");
            btnLoadOpenStreetMap.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    mapOnline.setDefaultProvider(org.jdesktop.swingx.JXMapKit.DefaultProviders.OpenStreetMaps);
                    mapOnline.setAddressLocation(defaultPosition);
                    mapOnline.setZoom(12);
                }
            });
            btnLoadOpenStreetMap.setPreferredSize(new Dimension(150, 25));
            mapOnlineFrame.add(btnLoadOpenStreetMap, new AbsoluteConstraints(150, 500, -1, -1));
            
            JButton btnLoadNASA2 = new JButton("NASA: Mosaic");
            btnLoadNASA2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    WMSService wms = new WMSService();
                    //wms.setLayer("BMNG");
                    wms.setLayer("global_mosaic");
                    //wms.setLayer("daily_planet");
                    wms.setBaseUrl("http://wms.jpl.nasa.gov/wms.cgi?");
                    TileFactory fact = new WMSTileFactory(wms);
                    mapOnline.setTileFactory(fact);
                    mapOnline.setAddressLocation(defaultPosition);
                    mapOnline.setZoom(13);
                }
            });
            btnLoadNASA2.setPreferredSize(new Dimension(150, 25));
            mapOnlineFrame.add(btnLoadNASA2, new AbsoluteConstraints(300, 500, -1, -1));

            JButton btnLoadNASA1 = new JButton("NASA: Blue Marble");
            btnLoadNASA1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    WMSService wms = new WMSService();
                    wms.setLayer("BMNG");
                    //wms.setLayer("global_mosaic");
                    //wms.setLayer("daily_planet");
                    wms.setBaseUrl("http://wms.jpl.nasa.gov/wms.cgi?");
                    TileFactory fact = new WMSTileFactory(wms);
                    mapOnline.setTileFactory(fact);
                    mapOnline.setAddressLocation(defaultPosition);
                    mapOnline.setZoom(13);
                }
            });
            btnLoadNASA1.setPreferredSize(new Dimension(150, 25));
            mapOnlineFrame.add(btnLoadNASA1, new AbsoluteConstraints(450, 500, -1, -1));

            JButton btnLoadNASA3 = new JButton("NASA: Daily Planet");
            btnLoadNASA3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    WMSService wms = new WMSService();
                    //wms.setLayer("BMNG");
                    //wms.setLayer("global_mosaic");
                    wms.setLayer("daily_planet");
                    wms.setBaseUrl("http://wms.jpl.nasa.gov/wms.cgi?");
                    TileFactory fact = new WMSTileFactory(wms);
                    mapOnline.setTileFactory(fact);
                    mapOnline.setAddressLocation(defaultPosition);
                    mapOnline.setZoom(13);
                }
            });
            btnLoadNASA3.setPreferredSize(new Dimension(150, 25));
            mapOnlineFrame.add(btnLoadNASA3, new AbsoluteConstraints(600, 500, -1, -1));

            mapOnlineFrame.pack();
        }
        return mapOnlineFrame;
    }

    public void clearOnlinemap() {
        mapOnlineFrame = null;
    }

//    public void resetMapFrame() {
//        if (mapOffline != null)
//            mapOffline.getFrameForImageDrawing().setVisible(false);
//
//        mapOffline = null;
//    }

    public boolean isUseOnlineMap() {
        return useOnlineMap;
    }

    public void setUseOnlineMap(boolean useOnlineMap) {
        this.useOnlineMap = useOnlineMap;
    }

}
