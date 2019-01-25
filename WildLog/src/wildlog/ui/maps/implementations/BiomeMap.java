package wildlog.ui.maps.implementations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javax.swing.JLabel;
import org.apache.logging.log4j.Level;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.maps.geotools.GeoToolsLayerUtils;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractGeoToolsMap;
import wildlog.utils.WildLogPaths;


public class BiomeMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {BIOMES_WORLD, BIOMES_LOCAL, BIOMES_GROUPS};
    private MapType activeMapType = MapType.BIOMES_WORLD;

    
    public BiomeMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("Biome Maps", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(7);
        // Maps
        ToggleButton btnBiomeWorld = new ToggleButton("Biomes (World)");
        btnBiomeWorld.setToggleGroup(BUTTON_GROUP);
        btnBiomeWorld.setCursor(Cursor.HAND);
        btnBiomeWorld.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.BIOMES_WORLD;
            }
        });
        lstCustomButtons.add(btnBiomeWorld);
        ToggleButton btnBiomeLocal = new ToggleButton("Biomes (Local)");
        btnBiomeLocal.setToggleGroup(BUTTON_GROUP);
        btnBiomeLocal.setCursor(Cursor.HAND);
        btnBiomeLocal.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.BIOMES_LOCAL;
            }
        });
        lstCustomButtons.add(btnBiomeLocal);
        ToggleButton btnEcoGroup = new ToggleButton("Eco-Groups (Local)");
        btnEcoGroup.setToggleGroup(BUTTON_GROUP);
        btnEcoGroup.setCursor(Cursor.HAND);
        btnEcoGroup.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.BIOMES_GROUPS;
            }
        });
        lstCustomButtons.add(btnEcoGroup);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        setupShowCountriesButton();
        setupEnchanceContrastButton();
    }

    @Override
    public void createMap(Scene inScene) {
// FIXME: ek dink die shapefiles is dalk steeds te groot want die maps is baie stadig (selfs QGIS sukkel bietjie)
        if (activeMapType.equals(MapType.BIOMES_WORLD)) {
            setActiveSubCategoryTitle("Woldwide Biomes");
            setupChartDescriptionLabel("<html>Biomes defined on a global scale.</html>");
            createBiomeMap(lstData, BundledMapLayers.BIOMES_WORLD);
        }
        else
        if (activeMapType.equals(MapType.BIOMES_LOCAL)) {
            setActiveSubCategoryTitle("Local Biomes");
            setupChartDescriptionLabel("<html>Biomes defined on a local scale.</html>");
            createBiomeMap(lstData, BundledMapLayers.BIOMES_LOCAL);
        }
        else
        if (activeMapType.equals(MapType.BIOMES_GROUPS)) {
            setActiveSubCategoryTitle("Local Eco-Groups");
            setupChartDescriptionLabel("<html>Eco-groups defined on a local scale.</html>");
            createBiomeMap(lstData, BundledMapLayers.BIOMES_LOCAL_GROUPS);
        }
    }
    
    protected void createBiomeMap(final List<Sighting> inLstSightings, final BundledMapLayers inBundledMapLayers) {
        if (map.getLayerCount() == 0) {
            map.addLayer(getGeoTiffLayers(BundledMapLayers.EARTH_MODERN));
            try {
                FileDataStore shapeStore = FileDataStoreFinder.getDataStore(
                        WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(inBundledMapLayers.getRelativePath()).toFile());
                SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
                Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleFile(shapeSource,
                        WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(inBundledMapLayers.getRelativePath().toString().substring(
                                0, inBundledMapLayers.getRelativePath().toString().lastIndexOf('.')) + ".sld")), 
                        inBundledMapLayers.name());
                map.addLayer(shapelayer);
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            map.addLayer(getLayerForSightings(inLstSightings));
            if (showCountries) {
                map.addLayer(getShapeLayers(BundledMapLayers.BASE_WORLD));
            }
        }
        else {
            map.replaceLayer(0, getGeoTiffLayers(BundledMapLayers.EARTH_MODERN));
            try {
                FileDataStore shapeStore = FileDataStoreFinder.getDataStore(
                        WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(inBundledMapLayers.getRelativePath()).toFile());
                SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
                Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleFile(shapeSource,
                        WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(inBundledMapLayers.getRelativePath().toString().substring(
                                0, inBundledMapLayers.getRelativePath().toString().lastIndexOf('.')) + ".sld")), 
                    inBundledMapLayers.name());
                map.replaceLayer(1, shapelayer);
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            map.replaceLayer(2, getLayerForSightings(inLstSightings));
            if (showCountries) {
                if (map.getLayerCount() != 4) {
                    map.addLayer(getShapeLayers(BundledMapLayers.BASE_WORLD));
                }
            }
            else {
                if (map.getLayerCount() == 4) {
                    map.removeLayer(3);
                }
            }
        }
    }

}
