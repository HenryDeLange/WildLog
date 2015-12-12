package wildlog.ui.maps.implementations;

import java.awt.Color;
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
import javax.swing.JLabel;
import org.geotools.data.DataSourceException;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.maps.geotools.GeoToolsLayerUtils;
import wildlog.ui.maps.DistributionLayersDialog;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractGeoToolsMap;


public class DistributionMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {SPECIES_DISTRIBUTION};
    private MapType activeMapType = MapType.SPECIES_DISTRIBUTION;
    private boolean isTransparent = false;
    private final List<Path> lstLayers = new ArrayList<>(5);

    
    public DistributionMap(List<Sighting> inLstData, JLabel inChartDescLabel, JFXPanel inJFXPanel, MapsBaseDialog inMapsBaseDialog) {
        super("Species Maps", inLstData, inChartDescLabel, inJFXPanel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(7);
        // Maps
        Button btnDistributionMap = new Button("Distribution Map");
        btnDistributionMap.setCursor(Cursor.HAND);
        btnDistributionMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                String elementName = null;
                for (Sighting sighting : lstData) {
                    if (elementName == null) {
                        elementName = sighting.getElementName();
                        continue;
                    }
                    if (!elementName.equalsIgnoreCase(sighting.getElementName())) {
                        // The list contains more than one Creature
                        elementName = null;
                        break;
                    }
                }
                String scientificName = null;
                if (elementName != null) {
                    scientificName = WildLogApp.getApplication().getDBI().find(new Element(elementName)).getScientificName();
                }
                DistributionLayersDialog dialog = new DistributionLayersDialog(baseDialog, scientificName);
                dialog.setVisible(true);
                lstLayers.clear();
                if (dialog.getLstSelectedPaths() != null) {
                    lstLayers.addAll(dialog.getLstSelectedPaths());
                }
            }
        });
        lstCustomButtons.add(btnDistributionMap);
        // Options
        setupShowCountriesButton();
        setupEnchanceContrastButton();
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.SPECIES_DISTRIBUTION)) {
            // Add base layer
            map.addLayer(getGeoTiffLayers(BundledMapLayers.EARTH_MODERN));
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
                        Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(shapeSource, 
                                new Color(150, 60, 30), new Color(190, 80, 50), 0.8, 0.4));
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
