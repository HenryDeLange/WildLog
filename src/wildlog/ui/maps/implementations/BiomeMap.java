package wildlog.ui.maps.implementations;

import java.util.ArrayList;
import java.util.List;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractGeoToolsMap;


public class BiomeMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {TEMPERATURE_MIN, TEMPERATURE_MEAN, TEMPERATURE_MAX, PRECIPITATION_AVERAGE, PRECIPITATION_MONTHLY};
    private MapType activeMapType = MapType.TEMPERATURE_MEAN;

    
    public BiomeMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("Biome Maps", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(10);
        // Maps
        ToggleButton btnTemperatureMinMap = new ToggleButton("Global Biomes");
        btnTemperatureMinMap.setToggleGroup(BUTTON_GROUP);
        btnTemperatureMinMap.setCursor(Cursor.HAND);
        btnTemperatureMinMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.TEMPERATURE_MIN;
            }
        });
        lstCustomButtons.add(btnTemperatureMinMap);
        ToggleButton btnTemperatureMeanMap = new ToggleButton("Local Biomes");
        btnTemperatureMeanMap.setToggleGroup(BUTTON_GROUP);
        btnTemperatureMeanMap.setCursor(Cursor.HAND);
        btnTemperatureMeanMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.TEMPERATURE_MEAN;
            }
        });
        lstCustomButtons.add(btnTemperatureMeanMap);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        setupShowCountriesButton();
        setupEnchanceContrastButton();
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.TEMPERATURE_MIN)) {
            setActiveSubCategoryTitle("Temperature (Monthly Minimum)");
            setupChartDescriptionLabel("<html><b><u>Minimum</u> Temperature.</b> Red is high, yellow medium and blue is low temperature.</html>");
//            createMapDefaultForMonth(lstData, BundledMapLayers.CLIMATE_TEMPERATURE_MIN, activeMonth);
        }
        else
        if (activeMapType.equals(MapType.TEMPERATURE_MEAN)) {
            setActiveSubCategoryTitle("Temperature (Monthly Average)");
            setupChartDescriptionLabel("<html><b><u>Mean</u> Temperature.</b> Red is high, yellow medium and blue is low temperature.</html>");
//            createMapDefaultForMonth(lstData, BundledMapLayers.CLIMATE_TEMPERATURE_MEAN, activeMonth);
        }
        else
        if (activeMapType.equals(MapType.TEMPERATURE_MAX)) {
            setActiveSubCategoryTitle("Temperature (Monthly Maximum)");
            setupChartDescriptionLabel("<html><b><u>Maximum</u> Temperature.</b> Red is high, yellow medium and blue is low temperature.</html>");
//            createMapDefaultForMonth(lstData, BundledMapLayers.CLIMATE_TEMPERATURE_MAX, activeMonth);
        }
        else
        if (activeMapType.equals(MapType.PRECIPITATION_AVERAGE)) {
            setActiveSubCategoryTitle("Aridity (Annual Average)");
            setupChartDescriptionLabel("<html><b>Average <u>Annual</u> Aridity.</b> Blue indicates low, green medium and white high aridity.</html>");
            createMapDefault(lstData, BundledMapLayers.CLIMATE_PRECIPITATION_AVERAGE);
        }
        else
        if (activeMapType.equals(MapType.PRECIPITATION_MONTHLY)) {
            setActiveSubCategoryTitle("Precipitation (Monthly Average)");
            setupChartDescriptionLabel("<html><b>Mean <u>Monthly</u> Precipitation.</b> Blue indicates high, green medium and white low percipitation.</html>");
//            createMapDefaultForMonth(lstData, BundledMapLayers.CLIMATE_PERCIPITATION_MONTHLY, activeMonth);
        }
    }

}
