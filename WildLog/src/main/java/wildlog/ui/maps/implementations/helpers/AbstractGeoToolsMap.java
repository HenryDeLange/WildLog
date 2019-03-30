package wildlog.ui.maps.implementations.helpers;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javax.swing.JLabel;
import org.apache.logging.log4j.Level;
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
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.data.dataobjects.interfaces.DataObjectWithGPS;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.maps.geotools.GeoToolsLayerUtils;
import wildlog.maps.geotools.GeoToolsMapJavaFX;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.utils.WildLogPaths;


public abstract class AbstractGeoToolsMap<T extends DataObjectWithGPS> extends AbstractMap<T> {
    protected GeoToolsMapJavaFX map = null;
    protected boolean showCountries = false;
    protected BundledMapLayers activeBaseLayer = null;
    protected boolean enhanceContrast = false;

    public AbstractGeoToolsMap(String inMapButtonName, List<T> inList, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super(inMapButtonName, inList, inChartDescLabel, inMapsBaseDialog);
    }

    @Override
    public void loadMap() {
        // Setup waiting cursor
        mapsBaseDialog.getGlassPane().setVisible(true);
        mapsBaseDialog.getGlassPane().setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        // Setup loading label
        final Label lblLoading = new Label("... LOADING ...");
        lblLoading.setPadding(new Insets(20));
        lblLoading.setFont(new Font(24));
        lblLoading.setTextAlignment(TextAlignment.CENTER);
        lblLoading.setAlignment(Pos.CENTER);
        mapsBaseDialog.getJFXMapPanel().getScene().setRoot(lblLoading);
        // Create the map
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Create the map when the initial JFXPanel size is available. 
                // The map needs to be recreated, because the root node must be reset.
                // Remember the map position and zoom
                ReferencedEnvelope bounds = null;
                if (map != null) {
                    bounds = map.getBounds();
                    // Dispose the old map
                    map.dispose();
                }
                // Create the new map
                map = new GeoToolsMapJavaFX(mapsBaseDialog.getJFXMapPanel(), enhanceContrast);
                // Reapply the map position and zoom
                if (bounds != null) {
                    map.setBounds(bounds);
                }
                // Continue to create the layers for the map
                createMap(mapsBaseDialog.getJFXMapPanel().getScene());
                // Add the watermark overlay
                applyWatermark();
                // Refresh the map to display the added layers
                map.reloadMap();
                // Hide waiting cursor
                mapsBaseDialog.getGlassPane().setCursor(java.awt.Cursor.getDefaultCursor());
                mapsBaseDialog.getGlassPane().setVisible(false);
            }
        });
    }
    
    protected void setupShowCountriesButton() {
        CheckBox chkShowCountries = new CheckBox("Show Countries");
        chkShowCountries.setCursor(Cursor.HAND);
        chkShowCountries.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                showCountries = chkShowCountries.isSelected();
            }
        });
        lstCustomButtons.add(chkShowCountries);
    }
    
    protected void setupEnchanceContrastButton() {
        CheckBox chkEnhanceContrast = new CheckBox("Enhance Contrast");
        chkEnhanceContrast.setCursor(Cursor.HAND);
        chkEnhanceContrast.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                enhanceContrast = chkEnhanceContrast.isSelected();
            }
        });
        lstCustomButtons.add(chkEnhanceContrast);
    }

    @Override
    public abstract void createMap(Scene inScene);
    
    protected void createMapDefault(final List<Sighting> inLstSightings, final BundledMapLayers inBundledMapLayers) {
        activeBaseLayer = inBundledMapLayers;
        if (map.getLayerCount() == 0) {
            map.addLayer(getGeoTiffLayers(inBundledMapLayers));
            map.addLayer(getLayerForSightings(inLstSightings));
            if (showCountries) {
                map.addLayer(getShapeLayers(BundledMapLayers.BASE_WORLD));
            }
        }
        else {
            map.replaceLayer(0, getGeoTiffLayers(inBundledMapLayers));
            map.replaceLayer(1, getLayerForSightings(inLstSightings));
            if (showCountries) {
                if (map.getLayerCount() != 3) {
                    map.addLayer(getShapeLayers(BundledMapLayers.BASE_WORLD));
                }
            }
            else {
                if (map.getLayerCount() == 3) {
                    map.removeLayer(2);
                }
            }
        }
    }
    
    protected void createMapDefaultForMonth(final List<Sighting> inLstSightings, final BundledMapLayers inBundledMapLayers, int inMonth) {
        activeBaseLayer = inBundledMapLayers;
        if (map.getLayerCount() == 0) {
            map.addLayer(getGeoTiffLayersForMonth(inBundledMapLayers, inMonth));
            map.addLayer(getLayerForSightings(inLstSightings));
            if (showCountries) {
                map.addLayer(getShapeLayers(BundledMapLayers.BASE_WORLD));
            }
        }
        else {
            map.replaceLayer(0, getGeoTiffLayersForMonth(inBundledMapLayers, inMonth));
            map.replaceLayer(1, getLayerForSightings(inLstSightings));
            if (showCountries) {
                if (map.getLayerCount() != 3) {
                    map.addLayer(getShapeLayers(BundledMapLayers.BASE_WORLD));
                }
            }
            else {
                if (map.getLayerCount() == 3) {
                    map.removeLayer(2);
                }
            }
        }
    }
    
    protected Layer getGeoTiffLayers(final BundledMapLayers inBundledMapLayers) {
        Layer gridLayer = null;
        try {
            GeoTiffReader reader = new GeoTiffReader(WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath()
                    .resolve(inBundledMapLayers.getRelativePath()).toFile());
            gridLayer = new GridReaderLayer(reader, GeoToolsLayerUtils.createGeoTIFFStyleRGB(reader), inBundledMapLayers.name());
        }
        catch (DataSourceException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return gridLayer;
    }
    
    protected Layer getGeoTiffLayersForMonth(final BundledMapLayers inBundledMapLayers, int inMonth) {
        Layer gridLayer = null;
        try {
            GeoTiffReader reader = new GeoTiffReader(WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath()
                    .resolve(inBundledMapLayers.getRelativePathForMonth(inMonth)).toFile());
            gridLayer = new GridReaderLayer(reader, GeoToolsLayerUtils.createGeoTIFFStyleRGB(reader), inBundledMapLayers.name());
        }
        catch (DataSourceException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return gridLayer;
    }
    
    protected Layer getShapeLayers(final BundledMapLayers inBundledMapLayers) {
        Layer shapelayer = null;
        try {
            FileDataStore shapeStore = FileDataStoreFinder.getDataStore(
                    WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(inBundledMapLayers.getRelativePath()).toFile());
            SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
            shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(shapeSource,
                    Color.BLACK, Color.BLACK, 0.8, 0.0), inBundledMapLayers.name());
        }
        catch (IOException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return shapelayer;
    }
    
    protected Layer getLayerForSightings(final List<Sighting> inLstSightings) {
        System.out.println("getting lsighting layer");
// TODO: Figure uit hoe om die map te zoom na waar die punte geplot is
        FeatureLayer pointLayer = null;
        try {
            SimpleFeatureType type = DataUtilities.createType("WildLogPointType", "geom:Point,name:String,mydata:String");
            SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
            DefaultFeatureCollection collection = new DefaultFeatureCollection();
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
            for (Sighting sighting : inLstSightings) {
                if (UtilsGPS.hasGPSData(sighting)) {
                    builder.add(geometryFactory.createPoint(new Coordinate(UtilsGPS.getLonDecimalDegree(sighting), UtilsGPS.getLatDecimalDegree(sighting))));
                    SimpleFeature feature = builder.buildFeature(Long.toString(sighting.getID()), new Object[] {sighting.toString()});
                    collection.add(feature);
                }
            }
            if (collection.isEmpty()) {
                // Ek moet steeds iets in die collection sit andersins gooi GeoTools 'n NullPointerException
                collection.add(builder.buildFeature("Nothing"));
            }
            Style pointStyle = GeoToolsLayerUtils.createPointStyle(new Color(80, 15, 5), new Color(175, 30, 20), 0.8, 0.5, 14);
            pointLayer = new FeatureLayer(collection, pointStyle, "WildLogPointLayer");
        }
        catch (SchemaException | FactoryRegistryException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        return pointLayer;
    }
    
    @Override
    public void dispose() {
        if (map != null) {
            map.dispose();
        }
    }
    
    public void zoomIn() {
        if (map != null) {
            map.zoomIn();
        }
    }
    
    public void zoomOut() {
        if (map != null) {
            map.zoomOut();
        }
    }
    
    public void identify() {
        if (map != null) {
            map.identify();
        }
    }
    
}
