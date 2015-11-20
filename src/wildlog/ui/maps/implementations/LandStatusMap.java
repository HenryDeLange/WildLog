package wildlog.ui.maps.implementations;

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
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractGeoToolsMap;


public class LandStatusMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {FORESTS, FARMING, HUMAN_INFLUENCE, HUMAN_POPULATION, PROTECTED_AREAS_WORLD, PROTECTED_AREAS_LOCAL};
    private MapType activeMapType = MapType.PROTECTED_AREAS_WORLD;

    
    public LandStatusMap(List<Sighting> inLstData, JLabel inChartDescLabel, JFXPanel inJFXPanel, MapsBaseDialog inMapsBaseDialog) {
        super("Conservation Maps", inLstData, inChartDescLabel, inJFXPanel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(2);
        // Maps
        Button btnProtectedWorldMap = new Button("Protected Areas (World)");
        btnProtectedWorldMap.setCursor(Cursor.HAND);
        btnProtectedWorldMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.PROTECTED_AREAS_WORLD;
                setupChartDescriptionLabel("<html>.</html>");
            }
        });
        lstCustomButtons.add(btnProtectedWorldMap);
        Button btnProtectedLocalMap = new Button("Protected Areas (Local)");
        btnProtectedLocalMap.setCursor(Cursor.HAND);
        btnProtectedLocalMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.PROTECTED_AREAS_LOCAL;
                setupChartDescriptionLabel("<html>.</html>");
            }
        });
        lstCustomButtons.add(btnProtectedLocalMap);
        Button btnHumanInfluenceMap = new Button("Human Influence");
        btnHumanInfluenceMap.setCursor(Cursor.HAND);
        btnHumanInfluenceMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.HUMAN_INFLUENCE;
                setupChartDescriptionLabel("<html>.</html>");
            }
        });
        lstCustomButtons.add(btnHumanInfluenceMap);
        Button btnPopulationMap = new Button("Human Population");
        btnPopulationMap.setCursor(Cursor.HAND);
        btnPopulationMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.HUMAN_POPULATION;
                setupChartDescriptionLabel("<html>.</html>");
            }
        });
        lstCustomButtons.add(btnPopulationMap);
        Button btnForestsMap = new Button("Forested Areas");
        btnForestsMap.setCursor(Cursor.HAND);
        btnForestsMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.FORESTS;
                setupChartDescriptionLabel("<html>.</html>");
            }
        });
        lstCustomButtons.add(btnForestsMap);
        Button btnFarmingMap = new Button("Crop Farming Areas");
        btnFarmingMap.setCursor(Cursor.HAND);
        btnFarmingMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.FARMING;
                setupChartDescriptionLabel("<html>.</html>");
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
            createMapDefault(lstData, BundledMapLayers.FORESTS);
        }
        else
        if (activeMapType.equals(MapType.FARMING)) {
            createMapDefault(lstData, BundledMapLayers.FARMING_CROPS);
        }
        else
        if (activeMapType.equals(MapType.HUMAN_INFLUENCE)) {
            createMapDefault(lstData, BundledMapLayers.HUMAN_INFLUENCE);
        }
        else
        if (activeMapType.equals(MapType.HUMAN_POPULATION)) {
            createMapDefault(lstData, BundledMapLayers.HUMAN_POPULATION);
        }
        else
        if (activeMapType.equals(MapType.PROTECTED_AREAS_WORLD)) {
            createMapDefault(lstData, BundledMapLayers.PROTECTED_AREAS_WORLD);
        }
        else
        if (activeMapType.equals(MapType.PROTECTED_AREAS_LOCAL)) {
            createProtectedAreasLocalMap(lstData);
        }
    }
    
    protected void createProtectedAreasLocalMap(final List<Sighting> inLstSightings) {
        activeBaseLayer = BundledMapLayers.PROTECTED_AREAS_LOCAL_FORMAL;
        if (map.getLayerCount() == 0) {
            map.addLayer(getGeoTiffLayers(BundledMapLayers.EARTH_MODERN));
            map.addLayer(getShapeLayers(BundledMapLayers.PROTECTED_AREAS_LOCAL_FORMAL));
            map.addLayer(getShapeLayers(BundledMapLayers.PROTECTED_AREAS_LOCAL_INFORMAL));
            map.addLayer(getLayerForSightings(inLstSightings));
            if (showCountries) {
                map.addLayer(getShapeLayers(BundledMapLayers.BASE_WORLD));
            }
        }
        else {
            map.replaceLayer(0, getGeoTiffLayers(BundledMapLayers.EARTH_MODERN));
            map.replaceLayer(1, getShapeLayers(BundledMapLayers.PROTECTED_AREAS_LOCAL_FORMAL));
            map.replaceLayer(2, getShapeLayers(BundledMapLayers.PROTECTED_AREAS_LOCAL_INFORMAL));
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
