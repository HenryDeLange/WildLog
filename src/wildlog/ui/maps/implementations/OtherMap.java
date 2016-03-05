package wildlog.ui.maps.implementations;

import java.util.ArrayList;
import java.util.List;
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

    
    public OtherMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("Other Maps", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(5);
        // Maps
        Button btnAridityMap = new Button("Show Altitude");
        btnAridityMap.setCursor(Cursor.HAND);
        btnAridityMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.ALTITUDE;
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
        if (activeMapType.equals(MapType.ALTITUDE)) {
            setupChartDescriptionLabel("<html>A map of the world indicating the altitude. The darker the colour the higher the altitude.</html>");
            createMapDefault(lstData, BundledMapLayers.ALTITUDE);
        }
    }
    
}
