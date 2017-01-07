package wildlog.ui.maps.implementations;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javax.swing.JLabel;
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


public class LandStatusMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {FORESTS, FARMING, HUMAN_INFLUENCE, HUMAN_POPULATION, PROTECTED_AREAS_WORLD, PROTECTED_AREAS_LOCAL};
    private MapType activeMapType = MapType.PROTECTED_AREAS_WORLD;

    
    public LandStatusMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("Conservation Maps", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(9);
        // Maps
        Button btnProtectedWorldMap = new Button("Protected Areas (World)");
        btnProtectedWorldMap.setCursor(Cursor.HAND);
        btnProtectedWorldMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.PROTECTED_AREAS_WORLD;
            }
        });
        lstCustomButtons.add(btnProtectedWorldMap);
        Button btnProtectedLocalMap = new Button("Protected Areas (Local)");
        btnProtectedLocalMap.setCursor(Cursor.HAND);
        btnProtectedLocalMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.PROTECTED_AREAS_LOCAL;
            }
        });
        lstCustomButtons.add(btnProtectedLocalMap);
        Button btnForestsMap = new Button("Forested Areas");
        btnForestsMap.setCursor(Cursor.HAND);
        btnForestsMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.FORESTS;
            }
        });
        lstCustomButtons.add(btnForestsMap);
        Button btnHumanInfluenceMap = new Button("Human Influence");
        btnHumanInfluenceMap.setCursor(Cursor.HAND);
        btnHumanInfluenceMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.HUMAN_INFLUENCE;
            }
        });
        lstCustomButtons.add(btnHumanInfluenceMap);
        Button btnPopulationMap = new Button("Human Population");
        btnPopulationMap.setCursor(Cursor.HAND);
        btnPopulationMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.HUMAN_POPULATION;
            }
        });
        lstCustomButtons.add(btnPopulationMap);
        Button btnFarmingMap = new Button("Crop Farming Areas");
        btnFarmingMap.setCursor(Cursor.HAND);
        btnFarmingMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.FARMING;
            }
        });
        lstCustomButtons.add(btnFarmingMap);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        setupShowCountriesButton();
        setupEnchanceContrastButton();
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.FORESTS)) {
            setActiveSubCategoryTitle("Forested Areas");
            setupChartDescriptionLabel("<html>A world map showing forested areas in dark green.</html>");
            createMapDefault(lstData, BundledMapLayers.FORESTS);
        }
        else
        if (activeMapType.equals(MapType.FARMING)) {
            setActiveSubCategoryTitle("Crop Farming Areas");
            setupChartDescriptionLabel("<html>This map indicates land areas used for crop farming. The darker the red is the higher the farming footprint.</html>");
            createMapDefault(lstData, BundledMapLayers.FARMING_CROPS);
        }
        else
        if (activeMapType.equals(MapType.HUMAN_INFLUENCE)) {
            setActiveSubCategoryTitle("Human Influence");
            setupChartDescriptionLabel("<html>This map shows areas with high human influence throughout the world. Red areas indicate high human influence, yellow is medium and green is low influence.</html>");
            createMapDefault(lstData, BundledMapLayers.HUMAN_INFLUENCE);
        }
        else
        if (activeMapType.equals(MapType.HUMAN_POPULATION)) {
            setActiveSubCategoryTitle("Human Population");
            setupChartDescriptionLabel("<html>This map indicates areas with high human populations. The darker the red is the higher the human population.</html>");
            createMapDefault(lstData, BundledMapLayers.HUMAN_POPULATION);
        }
        else
        if (activeMapType.equals(MapType.PROTECTED_AREAS_WORLD)) {
            setActiveSubCategoryTitle("Protected Areas (World)");
            setupChartDescriptionLabel("<html>A map showing protected natural areas across the world.</html>");
            createMapDefault(lstData, BundledMapLayers.PROTECTED_AREAS_WORLD);
        }
        else
        if (activeMapType.equals(MapType.PROTECTED_AREAS_LOCAL)) {
            setActiveSubCategoryTitle("Protected Areas (Local)");
            setupChartDescriptionLabel("<html>A map showing protected natural areas within a specific local context (for example South Africa).</html>");
            createProtectedAreasLocalMap(lstData);
        }
    }
    
    protected void createProtectedAreasLocalMap(final List<Sighting> inLstSightings) {
        activeBaseLayer = BundledMapLayers.PROTECTED_AREAS_LOCAL_FORMAL;
        if (map.getLayerCount() == 0) {
            map.addLayer(getGeoTiffLayers(BundledMapLayers.EARTH_MODERN));
            try {
                FileDataStore shapeStore = FileDataStoreFinder.getDataStore(
                        WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(BundledMapLayers.PROTECTED_AREAS_LOCAL_FORMAL.getRelativePath()).toFile());
                SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
                Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(shapeSource,
                        new Color(18, 133, 28), new Color(23, 168, 35), 0.7, 0.6));
                map.addLayer(shapelayer);
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            try {
                FileDataStore shapeStore = FileDataStoreFinder.getDataStore(
                        WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(BundledMapLayers.PROTECTED_AREAS_LOCAL_INFORMAL.getRelativePath()).toFile());
                SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
                Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(shapeSource,
                        new Color(18, 133, 28), new Color(23, 168, 35), 0.7, 0.6));
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
                        WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(BundledMapLayers.PROTECTED_AREAS_LOCAL_FORMAL.getRelativePath()).toFile());
                SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
                Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(shapeSource,
                        new Color(18, 133, 28), new Color(23, 168, 35), 0.7, 0.6));
                map.replaceLayer(1, shapelayer);
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            try {
                FileDataStore shapeStore = FileDataStoreFinder.getDataStore(
                        WildLogPaths.WILDLOG_MAPS.getAbsoluteFullPath().resolve(BundledMapLayers.PROTECTED_AREAS_LOCAL_INFORMAL.getRelativePath()).toFile());
                SimpleFeatureSource shapeSource = shapeStore.getFeatureSource();
                Layer shapelayer = new FeatureLayer(shapeSource, GeoToolsLayerUtils.createShapefileStyleBasic(shapeSource,
                        new Color(18, 133, 28), new Color(23, 168, 35), 0.7, 0.6));
                map.replaceLayer(2, shapelayer);
            }
            catch (IOException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            }
            map.replaceLayer(3, getLayerForSightings(inLstSightings));
            if (showCountries) {
                if (map.getLayerCount() != 5) {
                    map.addLayer(getShapeLayers(BundledMapLayers.BASE_WORLD));
                }
            }
            else {
                if (map.getLayerCount() == 5) {
                    map.removeLayer(4);
                }
            }
        }
    }
    
}
