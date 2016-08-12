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


public class EarthMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {MODERN, HISTORIC};
    private MapType activeMapType = MapType.MODERN;

    
    public EarthMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("World Maps (Offline)", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(5);
        // Maps
        Button btnModernMap = new Button("Modern World");
        btnModernMap.setCursor(Cursor.HAND);
        btnModernMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.MODERN;
            }
        });
        lstCustomButtons.add(btnModernMap);
        Button btnHistoricMap = new Button("Pre-industrial World");
        btnHistoricMap.setCursor(Cursor.HAND);
        btnHistoricMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.HISTORIC;
            }
        });
        lstCustomButtons.add(btnHistoricMap);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        setupShowCountriesButton();
        setupEnchanceContrastButton();
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.MODERN)) {
            setActiveSubCategoryTitle("Modern World");
            setupChartDescriptionLabel("<html>A map of the world in modern times.</html>");
            createMapDefault(lstData, BundledMapLayers.EARTH_MODERN);
        }
        else
        if (activeMapType.equals(MapType.HISTORIC)) {
            setActiveSubCategoryTitle("Pre-industrial World");
            setupChartDescriptionLabel("<html>A map of the ideal modern world, uneffected by modern human development.</html>");
            createMapDefault(lstData, BundledMapLayers.EARTH_HISTORIC_IDEAL);
        }
    }
    
}
