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
import com.bbn.openmap.gui.OMToolSet;
import com.bbn.openmap.gui.OpenMapFrame;
import com.bbn.openmap.gui.ToolPanel;
import com.bbn.openmap.layer.GraticuleLayer;
import com.bbn.openmap.layer.shape.ShapeLayer;
import java.awt.Color;
import java.util.Properties;
import wildlog.data.dbi.DBI;
import wildlog.mapping.layers.PointLayer;

public class MapFrame {
    // Variables:
    private PointLayer pointLayer;
    private String title;
    //private DBI dbi;
    /*
     * The BasicMapPanel automatically creates many default
     * components, including the MapBean and the MapHandler.
     * You can extend the BasicMapPanel class if you like to
     * add different functionality or different types of
     * objects.
     */
    MapPanel mapPanel;
    // Create a Swing frame. The OpenMapFrame knows how to use
    // the MapHandler to locate and place certain objects.
    OpenMapFrame frame;

    // Constructor:
    public MapFrame(DBI inDBI) {
        pointLayer = new PointLayer();
        title = "WildLog Map";
        //dbi = inDBI;
        mapPanel = new BasicMapPanel();
        frame = new OpenMapFrame(title);
        // Size the frame appropriately
        frame.setSize(850, 700);
    }

    public MapFrame(DBI inDBI, String inTitle) {
        pointLayer = new PointLayer();
        title = inTitle;
        //dbi = inDBI;
        mapPanel = new BasicMapPanel();
        frame = new OpenMapFrame(title);
        // Size the frame appropriately
        frame.setSize(850, 700);
    }

    // Methods:
    public void showMap() {
        try {
            // Get the default MapHandler the BasicMapPanel created.
            MapHandler mapHandler = mapPanel.getMapHandler();
            mapHandler.add(frame);
            // Get the default MapBean that the BasicMapPanel created.
            MapBean mapBean = mapPanel.getMapBean();

            // Set the map's center
            mapBean.setCenter(new LatLonPoint(-29.0f, 25.0f));
            // Set the map's scale 1:120 million
            mapBean.setScale(12050000f);

            /*
             * Create and add a LayerHandler to the MapHandler. The
             * LayerHandler manages Layers, whether they are part of
             * the map or not. layer.setVisible(true) will add it to
             * the map. The LayerHandler has methods to do this, too.
             * The LayerHandler will find the MapBean in the
             * MapHandler.
             */
            mapHandler.add(new LayerHandler());

            // Add Mouse handling objects. The MouseDelegator manages the
            // MouseModes, controlling which one receives events from the
            // MapBean. The active MouseMode sends events to the layers
            // that want to receive events from it. The MouseDelegator
            // will find the MapBean in the MapHandler, and hook itself up
            // to it.
            mapHandler.add(new MouseDelegator());

            // Add MouseMode. The MouseDelegator will find it via the MapHandler.
            // Adding MouseMode first makes it active.
            PanMouseMode panMouseMode = new PanMouseMode();
            panMouseMode.setLeaveShadow(false);
            mapHandler.add(panMouseMode);
            //NavMouseMode navMouseMode = new NavMouseMode();
            //mapHandler.add(navMouseMode);

            // Add the points layer.
            // The LayerHandler will find the Layer in the MapHandler.
            pointLayer.setVisible(true);

            /*
             * Create a ShapeLayer to show world political boundaries.
             * Set the properties of the layer.
             */
            /*
            ShapeLayer shapeLayerBase = new ShapeLayer();
            Properties shapeLayerPropsBase = new Properties();
            shapeLayerPropsBase.put("prettyName", "World");
            shapeLayerPropsBase.put("lineColor", "000000");
            shapeLayerPropsBase.put("fillColor", "BDDE83");
            shapeLayerPropsBase.put("shapeFile", "wildlog/mapping/data/shape/WorldBase.shp");
            shapeLayerPropsBase.put("spatialIndex", "wildlog/mapping/data/shape/WorldBase.ssx");
            shapeLayerBase.setProperties(shapeLayerPropsBase);
            shapeLayerBase.setVisible(true);
            */

            ShapeLayer shapeLayerSouthAfrica = new ShapeLayer();
            Properties shapeLayerPropsSouthAfrica = new Properties();
            shapeLayerPropsSouthAfrica.put("prettyName", "South Africa");
            shapeLayerPropsSouthAfrica.put("lineColor", "000000");
            shapeLayerPropsSouthAfrica.put("fillColor", "CDDE73");
            shapeLayerPropsSouthAfrica.put("shapeFile", "wildlog/mapping/data/shape/provinces.shp");
            shapeLayerPropsSouthAfrica.put("spatialIndex", "wildlog/mapping/data/shape/provinces.ssx");
            shapeLayerSouthAfrica.setProperties(shapeLayerPropsSouthAfrica);
            shapeLayerSouthAfrica.setVisible(true);

            ShapeLayer shapeLayerRoads = new ShapeLayer();
            Properties shapeLayerPropsRoads = new Properties();
            shapeLayerPropsRoads.put("prettyName", "South Africa");
            shapeLayerPropsRoads.put("lineColor", "A0A000");
            shapeLayerPropsRoads.put("fillColor", "A9A909");
            shapeLayerPropsRoads.put("shapeFile", "wildlog/mapping/data/shape/roads.shp");
            shapeLayerPropsRoads.put("spatialIndex", "wildlog/mapping/data/shape/roads.ssx");
            shapeLayerRoads.setProperties(shapeLayerPropsRoads);
            shapeLayerRoads.setVisible(true);
            /*
            ShapeLayer shapeLayerTowns = new ShapeLayer();
            Properties shapeLayerPropsTowns = new Properties();
            shapeLayerPropsTowns.put("prettyName", "South Africa");
            shapeLayerPropsTowns.put("lineColor", "000000");
            shapeLayerPropsTowns.put("fillColor", "BDDE83");
            shapeLayerPropsTowns.put("shapeFile", "wildlog/mapping/data/shape/town2.shp");
            shapeLayerPropsTowns.put("spatialIndex", "wildlog/mapping/data/shape/town2.ssx");
            shapeLayerTowns.setProperties(shapeLayerPropsTowns);
            shapeLayerTowns.setVisible(true);
            */
            ShapeLayer shapeLayerProtectedLand = new ShapeLayer();
            Properties shapeLayerPropsProtectedLand = new Properties();
            shapeLayerPropsProtectedLand.put("prettyName", "Protected Land Areas");
            shapeLayerPropsProtectedLand.put("lineColor", "4E7F12");
            shapeLayerPropsProtectedLand.put("fillColor", "8EBA52");
            shapeLayerPropsProtectedLand.put("shapeFile", "wildlog/mapping/data/shape/protected_land.shp");
            shapeLayerPropsProtectedLand.put("spatialIndex", "wildlog/mapping/data/shape/protected_land.ssx");
            shapeLayerProtectedLand.setProperties(shapeLayerPropsProtectedLand);
            shapeLayerProtectedLand.setVisible(true);

            ShapeLayer shapeLayerProtectedSea = new ShapeLayer();
            Properties shapeLayerPropsProtectedSea = new Properties();
            shapeLayerPropsProtectedSea.put("prettyName", "Protected Sea Areas");
            shapeLayerPropsProtectedSea.put("lineColor", "208090");
            shapeLayerPropsProtectedSea.put("fillColor", "00BBDD");
            shapeLayerPropsProtectedSea.put("shapeFile", "wildlog/mapping/data/shape/protected_Sea.shp");
            shapeLayerPropsProtectedSea.put("spatialIndex", "wildlog/mapping/data/shape/protected_Sea.ssx");
            shapeLayerProtectedSea.setProperties(shapeLayerPropsProtectedSea);
            shapeLayerProtectedSea.setVisible(true);

            // Last on top
            //mapHandler.add(shapeLayerBase);
            mapHandler.add(shapeLayerSouthAfrica);
            mapHandler.add(shapeLayerRoads);
            //mapHandler.add(shapeLayerTowns);
            mapHandler.add(shapeLayerProtectedSea);
            mapHandler.add(shapeLayerProtectedLand);
            //mapHandler.add(new GraticuleLayer());
            mapHandler.add(pointLayer);

            // Create an OpenMap toolbar
            //ToolPanel toolBar = new ToolPanel();
            // Create the directional and zoom control tool
            //OMToolSet omts = new OMToolSet();

            /*
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
                            dbi.createOrUpdate(new MapPoint(Latitudes.SOUTH, 27, 0, 0, Longitudes.EAST, 25, 0, 0, "test"));
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
            */

            /*
             * Add the ToolPanel and the OMToolSet to the MapHandler.
             * The OpenMapFrame will find the ToolPanel and attach it
             * to the top part of its content pane, and the ToolPanel
             * will find the OMToolSet and add it to itself.
             */
            //mapHandler.add(omts);
            //mapHandler.add(toolBar);
            // Display the frame
            frame.setVisible(true);

        } catch (MultipleSoloMapComponentException msmce) {
            // The MapHandler is only allowed to have one of certain
            // items. These items implement the SoloMapComponent
            // interface. The MapHandler can have a policy that
            // determines what to do when duplicate instances of the
            // same type of object are added - replace or ignore.

            // In this example, this will never happen, since we are
            // controlling that one MapBean, LayerHandler,
            // MouseDelegator, etc is being added to the MapHandler.
        }
        catch (Exception e) {
            System.out.println("Problem with Mapping...");
            e.printStackTrace();
        }
    }

    public void addPoint(float inLat, float inLon, Color inColor) {
        if (pointLayer == null) pointLayer = new PointLayer();
        pointLayer.addPoint(inLat, inLon, 5, inColor);
    }

    public void clearPoints() {
        pointLayer = new PointLayer();
    }

}
