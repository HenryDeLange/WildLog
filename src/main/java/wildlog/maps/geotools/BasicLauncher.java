package wildlog.maps.geotools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javafx.embed.swing.JFXPanel;
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
        frame.add(jfxPanel);
        frame.setSize(850, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // CREATE MAP
        GeoToolsMapJavaFX map = new GeoToolsMapJavaFX(jfxPanel, false);
        // ADD LAYERS
        // GeoTiff
        try {
            File geotiff = new File("C:\\Users\\Henry\\Desktop\\Maps\\__FINALE_MAPS\\Earth Colours - Modern\\world_today_medium.tif");
//            File geotiff = new File("C:\\WildLogToets\\WildLog\\Maps\\Layers\\Climate_Temperature_Min\\02Feb.tif");
            GeoTiffReader reader = new GeoTiffReader(geotiff);
            Layer gridlayer = new GridReaderLayer(reader, GeoToolsLayerUtils.createGeoTIFFStyleRGB(reader));
            map.addLayer(gridlayer);
        }
        catch (DataSourceException ex) {
            ex.printStackTrace(System.err);
        }
        // Shapefile
        try {
            File shapefile = new File("C:\\Users\\Henry\\Desktop\\Maps\\__FINALE_MAPS\\Base - World\\Small\\world.shp");
            FileDataStore shapeStore = FileDataStoreFinder.getDataStore(shapefile);
            SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
            Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(shapeSource));
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
            FeatureLayer pointLayer = new FeatureLayer(collection, pointStyle, "TheLayerTitle");
// FIXME: Make the points selectable... (Maybe too small, or smomething weird about the layer or feature types...)
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

