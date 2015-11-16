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


public class PrecipitationMap extends AbstractGeoToolsMap<Sighting> {
    private enum MapType {PRECIPITATION_AVERAGE, PRECIPITATION_MONTHLY};
    private MapType activeMapType = MapType.PRECIPITATION_AVERAGE;
    private boolean animateMonths = false;
    private int activeMonth = 0;
    private final ComboBox<String> cmbMonths;
    private final Timer timer = new Timer("WL_MonthTimer_Precipitation", true);

    
    public PrecipitationMap(List<Sighting> inLstData, JLabel inChartDescLabel, JFXPanel inJFXPanel, MapsBaseDialog inMapsBaseDialog) {
        super("Precipitation Maps", inLstData, inChartDescLabel, inJFXPanel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(9);
        // Maps
        Button btnAridityMap = new Button("Annual Aridity");
        btnAridityMap.setCursor(Cursor.HAND);
        btnAridityMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.PRECIPITATION_AVERAGE;
                setupChartDescriptionLabel("<html><b>Average <u>Annual</u> Aridity.</b> Blue indicates low, green medium and white high aridity.</html>");
            }
        });
        lstCustomButtons.add(btnAridityMap);
        Button btnPrecipitationMap = new Button("Monthly Precipitation");
        btnPrecipitationMap.setCursor(Cursor.HAND);
        btnPrecipitationMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.PRECIPITATION_MONTHLY;
                setupChartDescriptionLabel("<html><b>Average <u>Monthly</u> Precipitation.</b> Blue indicates high, green medium and white low percipitation.</html>");
                if (animateMonths) {
                    doMonthAnimation(timer, 2500);
                }
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
                activeMapType = MapType.PRECIPITATION_MONTHLY;
                setupChartDescriptionLabel("<html><b>Average <u>Monthly</u> Precipitation.</b> Blue indicates high, green medium and white low percipitation.</html>");
            }
        });
        lstCustomButtons.add(cmbMonths);
        CheckBox chkAnimateMonths = new CheckBox("Animate Months");
        chkAnimateMonths.setCursor(Cursor.HAND);
        chkAnimateMonths.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                animateMonths = chkAnimateMonths.isSelected();
                activeMapType = MapType.PRECIPITATION_MONTHLY;
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
        if (activeMapType.equals(MapType.PRECIPITATION_AVERAGE)) {
            createMapDefault(lstData, BundledMapLayers.CLIMATE_PRECIPITATION_AVERAGE);
        }
        else {
            if (activeMapType.equals(MapType.PRECIPITATION_MONTHLY)) {
                createMapDefaultForMonth(lstData, BundledMapLayers.CLIMATE_PERCIPITATION_MONTHLY, activeMonth);
            }
        }
    }
    
    private void doMonthAnimation(Timer inTimer, long inDelay) {
        final AbstractMap thisHandle = this;
        inTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (animateMonths && BundledMapLayers.CLIMATE_PERCIPITATION_MONTHLY.equals(activeBaseLayer) 
                        && baseDialog.getActiveMap() == thisHandle) {
                    long startTime = System.currentTimeMillis();
// FIXME: Hierdie is nie baie smooth nie, maar werk OK vir nou. 
//         - Kyk dalk later of dit smoother (vinniger en meer voorspelbaar kan wees) as ek die layers move en nie replace nie.
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
