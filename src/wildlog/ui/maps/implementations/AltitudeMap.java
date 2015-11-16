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


public class AltitudeMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {ALTITUDE};
    private MapType activeMapType = MapType.ALTITUDE;

    
    public AltitudeMap(List<Sighting> inLstData, JLabel inChartDescLabel, JFXPanel inJFXPanel, MapsBaseDialog inMapsBaseDialog) {
        super("Altitude Maps", inLstData, inChartDescLabel, inJFXPanel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(2);
        // Maps
        Button btnAridityMap = new Button("World Altitude");
        btnAridityMap.setCursor(Cursor.HAND);
        btnAridityMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.ALTITUDE;
                setupChartDescriptionLabel("<html>A map of the world indicating the altitude. The darker the colour the higher the altitude.</html>");
            }
        });
        lstCustomButtons.add(btnAridityMap);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        setupShowCountriesButton();
        setupEnchanceContrastButton();
// TODO: Add option to use other contrast enhancement options
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.ALTITUDE)) {
            createMapDefault(lstData, BundledMapLayers.ALTITUDE);
        }
    }
    
}
