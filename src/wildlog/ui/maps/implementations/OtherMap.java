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


public class OtherMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {BASIC_WORLD, ALTITUDE};
    private MapType activeMapType = MapType.BASIC_WORLD;

    
    public OtherMap(List<Sighting> inLstData, JLabel inChartDescLabel, JFXPanel inJFXPanel, MapsBaseDialog inMapsBaseDialog) {
        super("Other Maps", inLstData, inChartDescLabel, inJFXPanel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(3);
        // Maps
        Button btnBasicWorldMap = new Button("Basic World");
        btnBasicWorldMap.setCursor(Cursor.HAND);
        btnBasicWorldMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.BASIC_WORLD;
                setupChartDescriptionLabel("<html>A basic map of the world with major rivers and lakes.</html>");
            }
        });
        lstCustomButtons.add(btnBasicWorldMap);
        Button btnAridityMap = new Button("Altitude");
        btnAridityMap.setCursor(Cursor.HAND);
        btnAridityMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.ALTITUDE;
                setupChartDescriptionLabel("<html>A map of the world indicating the altitude. The darker the colour the higher the altitude.</html>");
            }
        });
        lstCustomButtons.add(btnAridityMap);
// TODO: OpenMap het 'n time of day layer wat basies wys hoe die son (dag/nag) oor die aarde lê
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        setupShowCountriesButton();
        setupEnchanceContrastButton();
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.BASIC_WORLD)) {
// TODO: Base world, rivers en lakes
            createMapDefault(lstData, BundledMapLayers.ALTITUDE);
        }
        else 
        if (activeMapType.equals(MapType.ALTITUDE)) {
            createMapDefault(lstData, BundledMapLayers.ALTITUDE);
        }
    }
    
}
