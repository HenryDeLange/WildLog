package wildlog.ui.maps.implementations;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.maps.geotools.GeoToolsLayerUtils;
import wildlog.ui.maps.DistributionLayersDialog;
import wildlog.ui.maps.LegendPopup;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractGeoToolsMap;


public class DistributionMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {SPECIES_DISTRIBUTION};
    private MapType activeMapType = MapType.SPECIES_DISTRIBUTION;
    private enum Transparency {
        Opaque      (1.0, 1.0), 
        Normal      (0.85, 0.7), 
        Transparent (0.3, 0.2);
        
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
        
    };
    private final ComboBox<Transparency> cmbColour;
    private final List<Path> lstLayers = new ArrayList<>(5);
    private final Color[] lstMapColours = new Color[] {
                                            new Color(232, 70, 19), 
                                            new Color(255, 126, 25), 
                                            new Color(255, 64, 57), 
                                            new Color(232, 158, 35), 
                                            new Color(255, 203, 36)};
    private final Map<String, Color> mapLegends = new HashMap<>(5);

    
    public DistributionMap(List<Sighting> inLstData, JLabel inChartDescLabel, JFXPanel inJFXPanel, MapsBaseDialog inMapsBaseDialog) {
        super("Distribution Maps (Species)", inLstData, inChartDescLabel, inJFXPanel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(6);
        // Maps
        Button btnDistributionMap = new Button("Select Species Distribution Map");
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
                // Setup the colours
                mapLegends.clear();
                int counter = 0;
                for (Path path : lstLayers) {
                    mapLegends.put(path.getFileName().toString(), lstMapColours[counter++]);
                }
            }
        });
        lstCustomButtons.add(btnDistributionMap);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        Button btnColour = new Button("Map Legend (Layer Colours)");
        btnColour.setCursor(Cursor.HAND);
        btnColour.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                LegendPopup dialog = new LegendPopup(baseDialog, mapLegends);
                dialog.setVisible(true);
            }
        });
        lstCustomButtons.add(btnColour);
        cmbColour = new ComboBox<>(FXCollections.observableArrayList(Transparency.values()));
        cmbColour.setVisibleRowCount(3);
        cmbColour.setCursor(Cursor.HAND);
        lstCustomButtons.add(cmbColour);
        setupShowCountriesButton();
        setupEnchanceContrastButton();
        // Select the default transparency
        cmbColour.getSelectionModel().select(Transparency.Normal);
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.SPECIES_DISTRIBUTION)) {
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
                        ex.printStackTrace(System.err);
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
                                cmbColour.getSelectionModel().getSelectedItem().getLineOpacity(),
                                cmbColour.getSelectionModel().getSelectedItem().getFillOpacity()));
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
