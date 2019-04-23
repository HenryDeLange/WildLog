package wildlog.maps.geotools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.JFXPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.geotools.data.DataSourceException;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.FactoryRegistryException;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Only used in the Maven project for easy launching and testing.
 */
public class BasicLauncher {
    
    private static void initAndShowGUI() {
        // This method is invoked on the Swing EDT thread
        JFrame frame = new JFrame("Swing and JavaFX");
        final JFXPanel jfxPanel = new JFXPanel();
        frame.setLayout(new BorderLayout());
        frame.add(jfxPanel, BorderLayout.CENTER);
        frame.setSize(850, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // CREATE MAP
        GeoToolsMapJavaFX map = new GeoToolsMapJavaFX(jfxPanel, false);
        // ADD BUTTONS
        JButton btnZoomIn = new JButton("Zoom In");
        btnZoomIn.addActionListener((e) -> { map.zoomIn(); });
        frame.add(btnZoomIn, BorderLayout.WEST);
        JButton btnZoomOut = new JButton("Zoom Out");
        btnZoomOut.addActionListener((e) -> { map.zoomOut(); });
        frame.add(btnZoomOut, BorderLayout.EAST);
        JButton btnIdentify = new JButton("Identify");
        btnIdentify.addActionListener((e) -> { map.identify(); });
        btnIdentify.setPreferredSize(new Dimension(0, 50));
        frame.add(btnIdentify, BorderLayout.NORTH);
        JButton btnReposition = new JButton("Reposition");
        btnReposition.addActionListener((e) -> { map.setStartBounds(-33.733778, 26.569936, 2); });
        btnReposition.setPreferredSize(new Dimension(0, 50));
        frame.add(btnReposition, BorderLayout.SOUTH);
        // ADD LAYERS
        // GeoTiff
        try {
            File geotiff = new File("..\\WildLogMapData\\src\\main\\resources\\Layers\\Earth_Modern\\world_modern.tif");
            GeoTiffReader reader = new GeoTiffReader(geotiff);
            GridReaderLayer gridlayer = new GridReaderLayer(reader, GeoToolsLayerUtils.createGeoTIFFStyleRGB(reader), "world - base layer");
            map.addLayer(gridlayer);
        }
        catch (DataSourceException ex) {
            ex.printStackTrace(System.err);
        }
        // Shapefile
        try {
            File shapefile = new File("..\\WildLogMapData\\src\\main\\resources\\Layers\\Base_World\\world.shp");
            FileDataStore shapeStore = FileDataStoreFinder.getDataStore(shapefile);
            SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
            Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(shapeSource, 
                    Color.BLACK, Color.BLUE, 0.75, 0.5), "world - countries");
            map.addLayer(shapelayer);
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        // Custom Data Points
        try {
            SimpleFeatureType type = DataUtilities.createType("MyPointType", "geom:Point,name:String,mydata:String");
            SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
            DefaultFeatureCollection collection = new DefaultFeatureCollection();
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
            builder.add(geometryFactory.createPoint(new Coordinate(1, 1)));
            SimpleFeature feature = builder.buildFeature("MyFeature1", new Object[] {"test1", "test111"});
            collection.add(feature);
            builder.add(geometryFactory.createPoint(new Coordinate(5, 5)));
            feature = builder.buildFeature("MyFeature2", new Object[] {"test2", "test222222"});
            feature.setAttribute("name", "The Name 2");
            feature.setAttribute("mydata", "The Name 222222");
            collection.add(feature);
            Style pointStyle = SLD.createPointStyle("Circle", Color.MAGENTA, Color.MAGENTA, 0.9f, 15);
            FeatureLayer pointLayer = new FeatureLayer(collection, pointStyle, "custom points");
            map.addLayer(pointLayer);
        }
        catch (SchemaException | FactoryRegistryException ex) {
            ex.printStackTrace(System.err);
        }
        // Web layers (NASA Weather)
// TODO
        // ADF
//        File adf = new File("C:\\Users\\Henry\\Desktop\\Maps\\Temperature\\Mean\\tmean_5m_esri\\tmean\\tmean_1\\w001001.adf");
//        BaseGDALGridCoverage2DReader reader = new AIGReader(adf);
        
        // Set the startup bounds
        map.setStartBounds(-28.2, 24.7, 20);
        // Enable the placing of points
        map.setPlacePoint(true);
        // RELOAD MAP (to display the added layers)
        map.reloadMap();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initAndShowGUI();
            }
        });
    }

}

