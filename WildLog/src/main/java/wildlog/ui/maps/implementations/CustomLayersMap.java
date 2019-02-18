package wildlog.ui.maps.implementations;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import org.geotools.styling.Style;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.geotools.GeoToolsLayerUtils;
import wildlog.ui.maps.CustomLayersDialog;
import wildlog.ui.maps.LegendDialog;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractGeoToolsMap;


public class CustomLayersMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {CUSTOM};
    private MapType activeMapType = MapType.CUSTOM;
    private final List<Path> lstLayers = new ArrayList<>(5);
    private final Color[] lstDefaultMapColours = new Color[] {
        new Color(232, 70, 19, 100), new Color(255, 126, 25, 100), new Color(255, 64, 57, 100), 
        new Color(232, 158, 35, 100), new Color(255, 203, 36, 100)};
    private Map<String, Color> mapLegends = new HashMap<>(5);
    
    
    public CustomLayersMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("Custom Layers Maps", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(4);
        // Maps
        ToggleButton btnLayers = new ToggleButton("Select Custom Layers");
        btnLayers.setToggleGroup(BUTTON_GROUP);
        btnLayers.setCursor(Cursor.HAND);
        btnLayers.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                CustomLayersDialog dialog = new CustomLayersDialog(mapsBaseDialog);
                dialog.setVisible(true);
                if (dialog.getLstSelectedPaths() != null) {
                    lstLayers.clear();
                    lstLayers.addAll(dialog.getLstSelectedPaths());
                }
                // Setup the colours
                mapLegends = new HashMap<>(lstLayers.size());
                int counter = 0;
                for (Path layerPath : lstLayers) {
                    if (layerPath.getFileName().toString().toLowerCase().endsWith(".shp")) {
                        Path stylePath = Paths.get(layerPath.toString().substring(0, layerPath.toString().lastIndexOf('.')) + ".sld");
                        // Moenie 'n legend wys vir layers wat 'n default style file gebruik nie
                        if (!Files.exists(stylePath)) {
                            if (counter < lstDefaultMapColours.length) {
                                mapLegends.put(layerPath.getFileName().toString(), lstDefaultMapColours[counter++]);
                            }
                            else {
                                mapLegends.put(layerPath.getFileName().toString(), Color.DARK_GRAY);
                            }
                        }
                    }
                }
            }
        });
        lstCustomButtons.add(btnLayers);
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
        setupEnchanceContrastButton();
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.CUSTOM)) {
            setActiveSubCategoryTitle("Custom Layers");
            setupChartDescriptionLabel("<html>This map displays the data points on a user defined list of custom layers.</html>");
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
                        WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    }
                }
                else
                if (layerPath.getFileName().toString().toLowerCase().endsWith(".shp")) {
                    try {
                        FileDataStore shapeStore = FileDataStoreFinder.getDataStore(layerPath.toFile());
                        SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
                        Path stylePath = Paths.get(layerPath.toString().substring(0, layerPath.toString().lastIndexOf('.')) + ".sld");
                        Style style;
                        if (Files.exists(stylePath)) {
                            style = GeoToolsLayerUtils.createShapefileStyleFile(shapeSource, stylePath);
                        }
                        else {
                            Color legend = mapLegends.get(layerPath.getFileName().toString());
                            style = GeoToolsLayerUtils.createShapefileStyleBasic(shapeSource, 
                                Color.BLACK, legend, legend.getAlpha()/255.0, legend.getAlpha()/255.0);
                        }
                        Layer shapelayer = new FeatureLayer(shapeSource, style, layerPath.getFileName().toString());
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