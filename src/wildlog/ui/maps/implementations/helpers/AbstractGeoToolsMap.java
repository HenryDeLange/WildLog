package wildlog.ui.maps.implementations.helpers;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
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
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.maps.geotools.GeoToolsLayerUtils;
import wildlog.maps.geotools.GeoToolsMapJavaFX;
import wildlog.maps.utils.UtilsGps;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.utils.WildLogPaths;


public abstract class AbstractGeoToolsMap<T> extends AbstractMap<T> {
    protected final JFXPanel jfxPanel;
    protected GeoToolsMapJavaFX map = null;
    protected boolean showCountries = false;
    protected BundledMapLayers activeBaseLayer = null;
    protected MapsBaseDialog baseDialog;
    protected boolean enhanceContrast = false;

    public AbstractGeoToolsMap(String inMapButtonName, List<T> inList, JLabel inChartDescLabel, JFXPanel inJFXPanel, MapsBaseDialog inMapsBaseDialog) {
        super(inMapButtonName, inList, inChartDescLabel);
        jfxPanel = inJFXPanel;
        baseDialog = inMapsBaseDialog;
    }

    @Override
    public void loadMap(Scene inScene) {
        // Setup loading label
        final Label lblLoading = new Label("... LOADING ...");
        lblLoading.setPadding(new Insets(20));
        lblLoading.setFont(new Font(24));
        lblLoading.setTextAlignment(TextAlignment.CENTER);
        lblLoading.setAlignment(Pos.CENTER);
        inScene.setRoot(lblLoading);
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
                }
// FIXME: Daai bug is terug waar die map nie die eerste keer reg laai nie (ek glo omdat die width en hight nog nie beskikbaar is nie)
                // Create the new map
                map = new GeoToolsMapJavaFX(jfxPanel, enhanceContrast);
                // Reapply the map position and zoom
                if (bounds != null) {
                    map.setBounds(bounds);
                }
                // Continue to create the layers for the map
                createMap(inScene);
                // Refresh the map to display the added layers
                map.reloadMap();
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
        CheckBox chkEnhanceContrast = new CheckBox("Enhance Map Contrast");
        chkEnhanceContrast.setCursor(Cursor.HAND);
        chkEnhanceContrast.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                enhanceContrast = chkEnhanceContrast.isSelected();
            }
        });
        lstCustomButtons.add(chkEnhanceContrast);
    }

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
            gridLayer = new GridReaderLayer(reader, GeoToolsLayerUtils.createGeoTIFFStyleRGB(reader));
        }
        catch (DataSourceException ex) {
            ex.printStackTrace(System.err);
        }
        return gridLayer;
    }
    
    protected Layer getGeoTiffLayersForMonth(final BundledMapLayers inBundledMapLayers, int inMonth) {
        Layer gridLayer = null;
        try {
            GeoTiffReader reader = new GeoTiffReader(WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath()
                    .resolve(inBundledMapLayers.getRelativePathForMonth(inMonth)).toFile());
            gridLayer = new GridReaderLayer(reader, GeoToolsLayerUtils.createGeoTIFFStyleRGB(reader));
        }
        catch (DataSourceException ex) {
            ex.printStackTrace(System.err);
        }
        return gridLayer;
    }
    
    protected Layer getShapeLayers(final BundledMapLayers inBundledMapLayers) {
        Layer shapelayer = null;
        try {
            FileDataStore shapeStore = FileDataStoreFinder.getDataStore(
                    WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(inBundledMapLayers.getRelativePath()).toFile());
            SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
            shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(shapeSource));
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        return shapelayer;
    }
    
    protected Layer getLayerForSightings(final List<Sighting> inLstSightings) {
        FeatureLayer pointLayer = null;
        try {
            SimpleFeatureType type = DataUtilities.createType("MyPointType", "geom:Point,name:String,mydata:String");
            SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
            DefaultFeatureCollection collection = new DefaultFeatureCollection();
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
            for (Sighting sighting : inLstSightings) {
                builder.add(geometryFactory.createPoint(new Coordinate(UtilsGps.getLonDecimalDegree(sighting), UtilsGps.getLatDecimalDegree(sighting))));
                SimpleFeature feature = builder.buildFeature(Long.toString(sighting.getSightingCounter()), new Object[] {sighting.toString()});
                collection.add(feature);
            }
            Style pointStyle = SLD.createPointStyle("Circle", Color.DARK_GRAY, Color.LIGHT_GRAY, 0.7f, 10);
            pointLayer = new FeatureLayer(collection, pointStyle, "TheLayerTitle");
// FIXME: Make the points selectable... (Maybe too small, or something weird about the layer or feature types...)
        }
        catch (SchemaException | FactoryRegistryException ex) {
            ex.printStackTrace(System.err);
        }
        return pointLayer;
    }
    
}
