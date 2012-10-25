package wildlog.mapping;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.LayerHandler;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.MapHandler;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.MultipleSoloMapComponentException;
import com.bbn.openmap.event.PanMouseMode;
import com.bbn.openmap.gui.BasicMapPanel;
import com.bbn.openmap.gui.MapPanel;
import com.bbn.openmap.gui.OpenMapFrame;
import com.bbn.openmap.layer.shape.ShapeLayer;
import java.awt.Color;
import java.util.Properties;
import javax.swing.ImageIcon;
import org.jdesktop.application.Application;
import wildlog.mapping.layers.MapOfflinePointLayer;

public class MapFrameOffline {
    // Variables:
    private MapOfflinePointLayer pointLayer;
    private String title;
    // The BasicMapPanel automatically creates many default components, including the MapBean and the MapHandler.
    // You can extend the BasicMapPanel class if you like to add different functionality or different types of objects.
    private MapPanel mapPanel;
    // Create a Swing frame. The OpenMapFrame knows how to use the MapHandler to locate and place certain objects.
    private OpenMapFrame frame;
    private MapHandler mapHandler;
    private double defaultLatitude;
    private double defaultLongitude;



    // Constructor:
    public MapFrameOffline(String inTitle, double inDefaultLatitude, double inDefaultLongitude/*, boolean inUseWMS*/) {
        title = inTitle;
        defaultLatitude = inDefaultLatitude;
        defaultLongitude = inDefaultLongitude;
        initMap(/*inUseWMS*/);
    }


    // Private Methods:
    private void initMap(/*boolean inUseWMS*/) {
        pointLayer = new MapOfflinePointLayer();
        mapPanel = new BasicMapPanel();
        frame = new OpenMapFrame(title);
        mapHandler = mapPanel.getMapHandler();
        // Setup Icon for the Frame
        ImageIcon icon = new ImageIcon(Application.getInstance().getClass().getResource("resources/icons/WildLog Map Icon.gif"));
        frame.setIconImage(icon.getImage());

        // Add the frame to the MapHandler. This is the frame that will be used to show the map.
        mapHandler.add(frame);
        // Create and add a LayerHandler to the MapHandler. The LayerHandler manages Layers, whether they are part of
        // the map or not. layer.setVisible(true) will add it to the map. The LayerHandler has methods to do this, too.
        // The LayerHandler will find the MapBean in the MapHandler.
        mapHandler.add(new LayerHandler());
        // Add Mouse handling objects. The MouseDelegator manages the MouseModes, controlling which one receives events from the
        // MapBean. The active MouseMode sends events to the layers that want to receive events from it. The MouseDelegator
        // will find the MapBean in the MapHandler, and hook itself up to it.
        mapHandler.add(new MouseDelegator());
        // The MouseDelegator will find it via the MapHandler. Adding MouseMode first makes it active.
        final PanMouseMode panMouseMode = new PanMouseMode();
        panMouseMode.setLeaveShadow(false);
        mapHandler.add(panMouseMode);
        //NavMouseMode navMouseMode = new NavMouseMode();
        //mapHandler.add(navMouseMode);

        ShapeLayer shapeLayerBase = new ShapeLayer();
        Properties shapeLayerPropsBase = new Properties();
        shapeLayerPropsBase.put("prettyName", "World");
        shapeLayerPropsBase.put("lineColor", "000000");
        shapeLayerPropsBase.put("fillColor", "BDDE83");
        shapeLayerPropsBase.put("shapeFile", "WorldBase.shp");
        shapeLayerPropsBase.put("spatialIndex", "WorldBase.ssx");
        shapeLayerBase.setProperties(shapeLayerPropsBase);
        shapeLayerBase.setVisible(true);

        ShapeLayer shapeLayerSouthAfrica = new ShapeLayer();
        Properties shapeLayerPropsSouthAfrica = new Properties();
        shapeLayerPropsSouthAfrica.put("prettyName", "South Africa");
        shapeLayerPropsSouthAfrica.put("lineColor", "000000");
        shapeLayerPropsSouthAfrica.put("fillColor", "CDDE73");
        shapeLayerPropsSouthAfrica.put("shapeFile", "provinces.shp");
        shapeLayerPropsSouthAfrica.put("spatialIndex", "provinces.ssx");
        shapeLayerSouthAfrica.setProperties(shapeLayerPropsSouthAfrica);
        shapeLayerSouthAfrica.setVisible(true);

        ShapeLayer shapeLayerRoads = new ShapeLayer();
        Properties shapeLayerPropsRoads = new Properties();
        shapeLayerPropsRoads.put("prettyName", "South Africa");
        shapeLayerPropsRoads.put("lineColor", "A0A000");
        shapeLayerPropsRoads.put("fillColor", "A9A909");
        shapeLayerPropsRoads.put("shapeFile", "roads.shp");
        shapeLayerPropsRoads.put("spatialIndex", "roads.ssx");
        shapeLayerRoads.setProperties(shapeLayerPropsRoads);
        shapeLayerRoads.setVisible(true);

        ShapeLayer shapeLayerTowns = new ShapeLayer();
        Properties shapeLayerPropsTowns = new Properties();
        shapeLayerPropsTowns.put("prettyName", "South Africa");
        shapeLayerPropsTowns.put("lineColor", "000000");
        shapeLayerPropsTowns.put("fillColor", "BDDE83");
        shapeLayerPropsTowns.put("shapeFile", "town2.shp");
        shapeLayerPropsTowns.put("spatialIndex", "town2.ssx");
        shapeLayerTowns.setProperties(shapeLayerPropsTowns);
        shapeLayerTowns.setVisible(true);

        ShapeLayer shapeLayerProtectedLand = new ShapeLayer();
        Properties shapeLayerPropsProtectedLand = new Properties();
        shapeLayerPropsProtectedLand.put("prettyName", "Protected Land Areas");
        shapeLayerPropsProtectedLand.put("lineColor", "4E7F12");
        shapeLayerPropsProtectedLand.put("fillColor", "8EBA52");
        shapeLayerPropsProtectedLand.put("shapeFile", "protected_land.shp");
        shapeLayerPropsProtectedLand.put("spatialIndex", "protected_land.ssx");
        shapeLayerProtectedLand.setProperties(shapeLayerPropsProtectedLand);
        shapeLayerProtectedLand.setVisible(true);

        // TODO: Use this to add the spesies distribution maps
//        ShapeLayer shapeLayerMammals = new ShapeLayer();
//        Properties shapeLayerPropsMammals = new Properties();
//        shapeLayerPropsMammals.put("prettyName", "Mammals SA");
//        shapeLayerPropsMammals.put("lineColor", "4E7F12");
//        shapeLayerPropsMammals.put("fillColor", "AA5577");
//        shapeLayerPropsMammals.put("shapeFile", "Mammals(SA).shp");
//        // TODO Need to either select only one shape, or prefferabily load only the relevant shape file
//        // TODO: Are these ssx file still needed?
////        shapeLayerPropsMammals.put("spatialIndex", "protected_land.ssx");
//        shapeLayerMammals.setProperties(shapeLayerPropsMammals);
//        shapeLayerMammals.setVisible(true);

        // Last on top
        mapHandler.add(shapeLayerBase);
        mapHandler.add(shapeLayerSouthAfrica);
        mapHandler.add(shapeLayerRoads);
        //mapHandler.add(shapeLayerTowns); // Baie punte, so los dit tot ek layers kan aan en af sit met die mapping
//        mapHandler.add(shapeLayerMammals);
        mapHandler.add(shapeLayerProtectedLand);

        //if (inUseWMS) doWMS();
    }

    // Public Methods:
    public void showMap() {
        try {
            // Get the default MapBean that the BasicMapPanel created.
            MapBean mapBean = mapPanel.getMapBean();
            // Set the map's center and scale
            mapBean.setCenter(new LatLonPoint(defaultLatitude, defaultLongitude));
            mapBean.setScale(8500000f);

            // Add the points layer.
            // The LayerHandler will find the Layer in the MapHandler.
            pointLayer.setVisible(true);
            mapHandler.add(pointLayer);


            /*
            // Create an OpenMap toolbar
            ToolPanel toolBar = new ToolPanel();
            // Create the directional and zoom control tool
            OMToolSet omts = new OMToolSet();
            // Buttons to controle behaviour
            JButton btnMapView = new JButton("Add Point");
            btnMapView.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final JDialog dialog = new JDialog(new JFrame(), "Please enter:", true);
                    dialog.setLayout(new AbsoluteLayout());
                    JLabel label = new JLabel("Sub Area:");
                    label.setSize(40, 20);
                    dialog.add(label, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 4, 50, -1));
                    final JTextField textfield = new JTextField();
                    textfield.setSize(120, 20);
                    dialog.add(textfield, new org.netbeans.lib.awtextra.AbsoluteConstraints(51, 2, 150, -1));
                    JButton button = new JButton("Add");
                    button.addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            //dbi.createOrUpdate(new MapPoint(Latitudes.SOUTH, 27, 0, 0, Longitudes.EAST, 25, 0, 0, "test"));
                            dialog.dispose();
                        }
                    });
                    dialog.setSize(210, 84);
                    dialog.add(button, new org.netbeans.lib.awtextra.AbsoluteConstraints(1, 23, 200, -1));
                    dialog.setLocationRelativeTo(frame);
                    dialog.setVisible(true);
                    showMap();
                }
            });
            omts.add(btnMapView);
            // Add the ToolPanel and the OMToolSet to the MapHandler. The OpenMapFrame will find the ToolPanel and attach it
            // to the top part of its content pane, and the ToolPanel will find the OMToolSet and add it to itself.
            mapHandler.add(omts);
            mapHandler.add(toolBar);
            */

            // Display the frame
            frame.setSize(950, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (MultipleSoloMapComponentException msmce) {
            // The MapHandler is only allowed to have one of certain items. These items implement the SoloMapComponent
            // interface. The MapHandler can have a policy that determines what to do when duplicate instances of the
            // same type of object are added - replace or ignore.
            // In this example, this will never happen, since we are controlling that one MapBean, LayerHandler,
            // MouseDelegator, etc is being added to the MapHandler.
        }
        catch (Exception e) {
            System.out.println("Problem with Mapping...");
            e.printStackTrace();
        }
    }

    public void showMap(int inWidth, int inHeight, float inScale) {
        try {
            // Get the default MapBean that the BasicMapPanel created.
            MapBean mapBean = mapPanel.getMapBean();
            // Set the map's center and scale
            mapBean.setCenter(new LatLonPoint(-28.7f, 24.7f));
            mapBean.setScale(inScale);

            // Add the points layer.
            // The LayerHandler will find the Layer in the MapHandler.
            pointLayer.setVisible(true);
            mapHandler.add(pointLayer);

            // Display the frame
            frame.setSize(inWidth, inHeight);
            //frame.setLocationRelativeTo(null);
            frame.setVisible(false);
            frame.validate();
        } catch (MultipleSoloMapComponentException msmce) {
            // The MapHandler is only allowed to have one of certain items. These items implement the SoloMapComponent
            // interface. The MapHandler can have a policy that determines what to do when duplicate instances of the
            // same type of object are added - replace or ignore.
            // In this example, this will never happen, since we are controlling that one MapBean, LayerHandler,
            // MouseDelegator, etc is being added to the MapHandler.
        }
        catch (Exception e) {
            System.out.println("Problem with Mapping...");
            e.printStackTrace();
        }
    }

    public void addPoint(double inLat, double inLon, Color inColor) {
        if (pointLayer == null) pointLayer = new MapOfflinePointLayer();
        pointLayer.addPoint(inLat, inLon, 5, inColor);
    }

    public void clearPoints() {
        mapHandler.remove(pointLayer);
        pointLayer = new MapOfflinePointLayer();
    }

    public void changeTitle(String inTitle) {
        title = inTitle;
        frame.setTitle(title);
    }

    public OpenMapFrame getFrameForImageDrawing() {
        frame.validate();
        return frame;
    }

/*
    private void doWMS() {
        // Implement caching of images later
        try {
            OMGraphicList wmsList = new OMGraphicList();
            wmsList.clear();
            WMSPlugIn wms = new WMSPlugIn();
            wms.setWmsServer("wms.cgi");
            wms.setImageFormat("image/jpeg");
            wms.setLayers("global_mosaic");
            wms.setWmsVersion("1.1.1");
            wms.setStyles("visual");
            wms.setVendorSpecificNames("EPSG");
            wms.setVendorSpecificValues("4326");
            wms.setQueryHeader("http://wms.jpl.nasa.gov/wms.cgi");
            Projection p = mapPanel.getMapBean().getProjection();
            String bbox = "undefined";
            String height = "undefined";
            String width = "undefined";
            if (p != null) {
                bbox = Double.toString(p.getUpperLeft().getLongitude()) + ","
                        + Double.toString(p.getLowerRight().getLatitude()) + ","
                        + Double.toString(p.getLowerRight().getLongitude()) + ","
                        + Double.toString(p.getUpperLeft().getLatitude());
                height = Integer.toString(p.getHeight());
                width = Integer.toString(p.getWidth());
            }
            String queryString = wms.getQueryHeader() + "?"+ "wms_server="+ wms.getServerName() + "&layers=" + wms.getLayers() + "&service=OGC:WMS" +
                "&format=image/jpeg" + "&width="+ width +"&height="+ height+"&bbox="+ bbox + "&request=GetMap&srs=EPSG:4326&styles=visual";
            System.out.println(wms.createQueryString(mapPanel.getMapBean().getProjection()));
            System.out.println(queryString);
            java.net.URL url = null;
            try {
                System.out.println("Inside URL code");
                url = new java.net.URL(queryString);
                java.net.HttpURLConnection urlc =
                    (java.net.HttpURLConnection)url.openConnection();
                urlc.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14");
                System.out.println("Using Proxy: " + urlc.usingProxy());
                urlc.setDoInput(true);
                urlc.setDoOutput(true);
                urlc.setRequestMethod("GET");
                System.out.println(urlc.getContentType());
                // text
                if ( urlc.getContentType().startsWith("text")) {
                    System.out.println("Inside URL code: Text");
                    java.io.BufferedReader bin = new java.io.BufferedReader(
                        new java.io.InputStreamReader(urlc.getInputStream())
                    );
                    String st;
                    String message = "";
                    while ((st=bin.readLine()) != null) {
                        message += st;
                    }
                // image
                } else if ( urlc.getContentType().startsWith("image")) {
                    System.out.println("Inside URL code: Image");
                    urlc.disconnect();
                    ImageIcon ii = new ImageIcon(url);
                    OMRaster image = new OMRaster(0, 0, ii);
                    wmsList.add(image);
                } // end if image
            } catch (java.net.MalformedURLException murle) {
                System.out.println("Bad URL!");
            } catch (java.io.IOException ioe) {
                System.out.println("IO Exception");
            }
            wmsList.generate(p);
            PlugInLayer wmsLayer = new PlugInLayer();
            wmsLayer.setPlugIn(wms);
            wmsLayer.setList(wmsList);
            mapHandler.add(wmsLayer);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to load online background.", "Can't Load WMS Layer", JOptionPane.INFORMATION_MESSAGE);
        }
    }
*/
}
