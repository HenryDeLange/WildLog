package wildlog.ui.maps.implementations;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javax.swing.JLabel;
import org.apache.logging.log4j.Level;
import org.geotools.data.DataSourceException;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.maps.geotools.GeoToolsLayerUtils;
import wildlog.ui.maps.DistributionLayersDialog;
import wildlog.ui.maps.LegendDialog;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractGeoToolsMap;


public class DistributionMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {SPECIES_DISTRIBUTION};
    private MapType activeMapType = MapType.SPECIES_DISTRIBUTION;
    private final List<Path> lstLayers = new ArrayList<>(5);
    private final Color[] lstDefaultMapColours = new Color[] {new Color(232, 70, 19), new Color(255, 126, 25), new Color(255, 64, 57), new Color(232, 158, 35), new Color(255, 203, 36)};
    private final Map<String, Color> mapLegends = new HashMap<>(5);
    private enum Transparency {
        OPAQUE      (1.0, 1.0), 
        NORMAL      (0.85, 0.7), 
        TRANSPARENT (0.3, 0.2);
        
        private final double lineOpacity;
        private final double fillOpacity;

        private Transparency(double inLineOpacity, double inFillOpacity) {
            lineOpacity = inLineOpacity;
            fillOpacity = inFillOpacity;
        }

        public double getLineOpacity() {
            return lineOpacity;
        }

        public double getFillOpacity() {
            return fillOpacity;
        }

        @Override
        public String toString() {
            switch (this) {
                case OPAQUE:
                    return "Not Transparent";
                case NORMAL:
                    return "Semi-Transparent";
                case TRANSPARENT:
                    return "Very Transparent";
                default:
                    return super.toString();
            }
        }
    };
    private final ComboBox<Transparency> cmbTransparity;

    
    public DistributionMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("Distribution Maps (Layer)", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(6);
        // Maps
        ToggleButton btnDistributionMap = new ToggleButton("Creature Distribution Map");
        btnDistributionMap.setToggleGroup(BUTTON_GROUP);
        btnDistributionMap.setCursor(Cursor.HAND);
        btnDistributionMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.SPECIES_DISTRIBUTION;
                DistributionLayersDialog dialog = new DistributionLayersDialog(mapsBaseDialog, lstData);
                dialog.setVisible(true);
                lstLayers.clear();
                if (dialog.getLstSelectedPaths() != null) {
                    lstLayers.addAll(dialog.getLstSelectedPaths());
                }
                // Setup the colours
                mapLegends.clear();
                int counter = 0;
                for (Path path : lstLayers) {
                    mapLegends.put(path.getFileName().toString(), lstDefaultMapColours[counter++]);
                }
            }
        });
        lstCustomButtons.add(btnDistributionMap);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        Button btnLegend = new Button("Map Legend (Layer Colours)");
        btnLegend.setCursor(Cursor.HAND);
        btnLegend.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                LegendDialog dialog = new LegendDialog(mapsBaseDialog, mapLegends);
                dialog.setVisible(true);
            }
        });
        lstCustomButtons.add(btnLegend);
        cmbTransparity = new ComboBox<>(FXCollections.observableArrayList(Transparency.values()));
        cmbTransparity.setVisibleRowCount(3);
        cmbTransparity.setCursor(Cursor.HAND);
        lstCustomButtons.add(cmbTransparity);
        setupShowCountriesButton();
        setupEnchanceContrastButton();
        // Select the default transparency
        cmbTransparity.getSelectionModel().select(Transparency.NORMAL);
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.SPECIES_DISTRIBUTION)) {
            setActiveSubCategoryTitle("Creature Distribution Map");
            setupChartDescriptionLabel("<html>This map shows the data points in WildLog on top of the selected species distribution map.</html>");
            // Add base layer
            map.addLayer(getGeoTiffLayers(BundledMapLayers.EARTH_MODERN));
            // Add cuntries
            if (showCountries) {
                map.addLayer(getShapeLayers(BundledMapLayers.BASE_WORLD));
            }
            // Add selected distribution layers
            for (Path layerPath : lstLayers) {
                if (layerPath.getFileName().toString().toLowerCase().endsWith(".tif") 
                        || layerPath.getFileName().toString().toLowerCase().endsWith(".tiff")) {
                    try {
                        GeoTiffReader reader = new GeoTiffReader(layerPath.toFile());
                        Layer gridLayer = new GridReaderLayer(reader, GeoToolsLayerUtils.createGeoTIFFStyleRGB(reader));
                        map.addLayer(gridLayer);
                    }
                    catch (DataSourceException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                }
                else
                if (layerPath.getFileName().toString().toLowerCase().endsWith(".shp")) {
                    try {
                        FileDataStore shapeStore = FileDataStoreFinder.getDataStore(layerPath.toFile());
                        SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
                        Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(
                                shapeSource, 
                                new Color(150, 60, 30), 
                                mapLegends.get(layerPath.getFileName().toString()), 
                                cmbTransparity.getSelectionModel().getSelectedItem().getLineOpacity(),
                                cmbTransparity.getSelectionModel().getSelectedItem().getFillOpacity()));
                        map.addLayer(shapelayer);
                    }
                    catch (IOException ex) {
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                }
            }
            // Add Sightings
            map.addLayer(getLayerForSightings(lstData));
        }
    }
    
}
