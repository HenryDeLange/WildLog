package wildlog.ui.maps.implementations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javax.swing.JLabel;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.geotools.BundledMapLayers;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractGeoToolsMap;
import wildlog.ui.maps.implementations.helpers.AbstractMap;


public class ClimateMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {TEMPERATURE_MIN, TEMPERATURE_MEAN, TEMPERATURE_MAX, PRECIPITATION_AVERAGE, PRECIPITATION_MONTHLY};
    private MapType activeMapType = MapType.TEMPERATURE_MEAN;
    private boolean animateMonths = false;
    private int activeMonth = 0;
    private final ComboBox<String> cmbMonths;
    private final Timer timer = new Timer("WL_MonthTimer_Climate", true);

    
    public ClimateMap(List<Sighting> inLstData, JLabel inChartDescLabel, JFXPanel inJFXPanel, MapsBaseDialog inMapsBaseDialog) {
        super("Climate Maps", inLstData, inChartDescLabel, inJFXPanel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(10);
        // Maps
        Button btnTemperatureMinMap = new Button("Temperature (Monthly Minimum)");
        btnTemperatureMinMap.setCursor(Cursor.HAND);
        btnTemperatureMinMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.TEMPERATURE_MIN;
            }
        });
        lstCustomButtons.add(btnTemperatureMinMap);
        Button btnTemperatureMeanMap = new Button("Temperature (Monthly Average)");
        btnTemperatureMeanMap.setCursor(Cursor.HAND);
        btnTemperatureMeanMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.TEMPERATURE_MEAN;
            }
        });
        lstCustomButtons.add(btnTemperatureMeanMap);
        Button btnTemperatureMaxMap = new Button("Temperature (Monthly Maximum)");
        btnTemperatureMaxMap.setCursor(Cursor.HAND);
        btnTemperatureMaxMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.TEMPERATURE_MAX;
            }
        });
        lstCustomButtons.add(btnTemperatureMaxMap);
        Button btnAridityMap = new Button("Aridity (Annual Average)");
        btnAridityMap.setCursor(Cursor.HAND);
        btnAridityMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.PRECIPITATION_AVERAGE;
            }
        });
        lstCustomButtons.add(btnAridityMap);
        Button btnPrecipitationMap = new Button("Precipitation (Monthly Average)");
        btnPrecipitationMap.setCursor(Cursor.HAND);
        btnPrecipitationMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.PRECIPITATION_MONTHLY;
            }
        });
        lstCustomButtons.add(btnPrecipitationMap);
        // Options
        lstCustomButtons.add(new Label("Map Options:"));
        cmbMonths = new ComboBox<>(FXCollections.observableArrayList(new String[] {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"}));
        cmbMonths.setVisibleRowCount(12);
        cmbMonths.setCursor(Cursor.HAND);
        cmbMonths.getSelectionModel().clearSelection();
        cmbMonths.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMonth = cmbMonths.getSelectionModel().getSelectedIndex();
            }
        });
        lstCustomButtons.add(cmbMonths);
        CheckBox chkAnimateMonths = new CheckBox("Animate Months");
        chkAnimateMonths.setCursor(Cursor.HAND);
        chkAnimateMonths.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                animateMonths = chkAnimateMonths.isSelected();
                if (animateMonths) {
                    doMonthAnimation(timer, 2500);
                }
            }
        });
        lstCustomButtons.add(chkAnimateMonths);
        setupShowCountriesButton();
        setupEnchanceContrastButton();
        // Setup default selected month
        cmbMonths.getSelectionModel().select(LocalDate.now().getMonthValue() -1);
    }

    @Override
    public void createMap(Scene inScene) {
        if (activeMapType.equals(MapType.TEMPERATURE_MIN)) {
            setupChartDescriptionLabel("<html><b><u>Minimum</u> Temperature.</b> Red is high, yellow medium and blue is low temperature.</html>");
            createMapDefaultForMonth(lstData, BundledMapLayers.CLIMATE_TEMPERATURE_MIN, activeMonth);
        }
        else
        if (activeMapType.equals(MapType.TEMPERATURE_MEAN)) {
            setupChartDescriptionLabel("<html><b><u>Mean</u> Temperature.</b> Red is high, yellow medium and blue is low temperature.</html>");
            createMapDefaultForMonth(lstData, BundledMapLayers.CLIMATE_TEMPERATURE_MEAN, activeMonth);
        }
        else
        if (activeMapType.equals(MapType.TEMPERATURE_MAX)) {
            setupChartDescriptionLabel("<html><b><u>Maximum</u> Temperature.</b> Red is high, yellow medium and blue is low temperature.</html>");
            createMapDefaultForMonth(lstData, BundledMapLayers.CLIMATE_TEMPERATURE_MAX, activeMonth);
        }
        else
        if (activeMapType.equals(MapType.PRECIPITATION_AVERAGE)) {
            setupChartDescriptionLabel("<html><b>Average <u>Annual</u> Aridity.</b> Blue indicates low, green medium and white high aridity.</html>");
            createMapDefault(lstData, BundledMapLayers.CLIMATE_PRECIPITATION_AVERAGE);
        }
        else
        if (activeMapType.equals(MapType.PRECIPITATION_MONTHLY)) {
            setupChartDescriptionLabel("<html><b>Mean <u>Monthly</u> Precipitation.</b> Blue indicates high, green medium and white low percipitation.</html>");
            createMapDefaultForMonth(lstData, BundledMapLayers.CLIMATE_PERCIPITATION_MONTHLY, activeMonth);
        }
    }
    
    private void doMonthAnimation(Timer inTimer, long inDelay) {
        final AbstractMap thisHandle = this;
        inTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (animateMonths && (BundledMapLayers.CLIMATE_TEMPERATURE_MIN.equals(activeBaseLayer) 
                        || BundledMapLayers.CLIMATE_TEMPERATURE_MEAN.equals(activeBaseLayer) 
                        || BundledMapLayers.CLIMATE_TEMPERATURE_MAX.equals(activeBaseLayer)
                        || BundledMapLayers.CLIMATE_PERCIPITATION_MONTHLY.equals(activeBaseLayer))
                        && baseDialog.getActiveMap() == thisHandle) {
                    long startTime = System.currentTimeMillis();
// FIXME: Hierdie is nie baie smooth nie, maar werk OK vir nou. Kyk dalk later of dit smoother (vinniger en meer voorspelbaar kan wees) as ek die layers move en nie replace nie.
                    map.replaceLayer(0, getGeoTiffLayersForMonth(activeBaseLayer, activeMonth++));
                    if (activeMonth > 11) {
                        activeMonth = 0;
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            cmbMonths.getSelectionModel().select(activeMonth);
                        }
                    });
                    doMonthAnimation(inTimer, inDelay - (System.currentTimeMillis() - startTime));
                }
            }
        }, inDelay);
    }

}
