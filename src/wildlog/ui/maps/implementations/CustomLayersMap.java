package wildlog.ui.maps.implementations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javax.swing.JLabel;
import org.geotools.data.DataSourceException;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.geotools.GeoToolsLayerUtils;
import wildlog.ui.maps.CustomMapLayersDialog;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractGeoToolsMap;


public class CustomLayersMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {CUSTOM};
    private MapType activeMapType = MapType.CUSTOM;
    private final List<Path> lstLayers = new ArrayList<>(5);
    
    
    public CustomLayersMap(List<Sighting> inLstData, JLabel inChartDescLabel, JFXPanel inJFXPanel, MapsBaseDialog inMapsBaseDialog) {
        super("Custom Layers Maps", inLstData, inChartDescLabel, inJFXPanel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(4);
        // Maps
        Button btnLayers = new Button("Select Custom Layers");
        btnLayers.setCursor(Cursor.HAND);
        btnLayers.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                CustomMapLayersDialog dialog = new CustomMapLayersDialog(baseDialog);
                dialog.setVisible(true);
                lstLayers.clear();
                lstLayers.addAll(dialog.getLstSelectedPaths());
            }
        });
        lstCustomButtons.add(btnLayers);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        Button btnColour = new Button("Change colours");
        btnColour.setCursor(Cursor.HAND);
        btnColour.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                // TODO
            }
        });
        lstCustomButtons.add(btnColour);
        setupEnchanceContrastButton();
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.CUSTOM)) {
            // Add selected layers
            for (Path layerPath : lstLayers) {
                if (layerPath.getFileName().toString().toLowerCase().endsWith(".tif") 
                        || layerPath.getFileName().toString().toLowerCase().endsWith(".tiff")) {
                    try {
                        GeoTiffReader reader = new GeoTiffReader(layerPath.toFile());
                        Layer gridLayer = new GridReaderLayer(reader, GeoToolsLayerUtils.createGeoTIFFStyleRGB(reader));
                        map.addLayer(gridLayer);
                    }
                    catch (DataSourceException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
                else
                if (layerPath.getFileName().toString().toLowerCase().endsWith(".shp")) {
                    try {
                        FileDataStore shapeStore = FileDataStoreFinder.getDataStore(layerPath.toFile());
                        SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
// FIXME: Set unique clours for each layer
                        Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(shapeSource));
                        map.addLayer(shapelayer);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }
            // Add Sightings
            map.addLayer(getLayerForSightings(lstData));
        }
    }
    
}
