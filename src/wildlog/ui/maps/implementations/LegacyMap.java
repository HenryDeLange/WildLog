package wildlog.ui.maps.implementations;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.MapFrameOnline;
import wildlog.maps.utils.UtilsGps;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractMap;


public class LegacyMap extends AbstractMap<Sighting> {
    private enum MapType {POINT_MAP_OPENSTREETMAP};
    private MapType activeMapType = MapType.POINT_MAP_OPENSTREETMAP;
    private Parent displayedMap;

    
    public LegacyMap(List<Sighting> inLstData, JLabel inChartDescLabel, MapsBaseDialog inMapsBaseDialog) {
        super("Old Legacy Maps", inLstData, inChartDescLabel, inMapsBaseDialog);
        lstCustomButtons = new ArrayList<>(1);
        // Maps
        Button btnPointMapOpenStreetMap = new Button("Old WildLog Online Map");
        btnPointMapOpenStreetMap.setCursor(Cursor.HAND);
        btnPointMapOpenStreetMap.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                activeMapType = MapType.POINT_MAP_OPENSTREETMAP;
            }
        });
        lstCustomButtons.add(btnPointMapOpenStreetMap);
    }

    @Override
    public void createMap(Scene inScene) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayedMap = null;
                if (activeMapType.equals(MapType.POINT_MAP_OPENSTREETMAP)) {
                    setupChartDescriptionLabel("<html>This is the old \"Online Map\" based on OpenStreetMap that was used in WildLog v4.2.2 and older.</html>");
                    displayedMap = createPointMapOSM(lstData);
                }
                inScene.setRoot(displayedMap);
            }
        });
    }

    private Parent createPointMapOSM(final List<Sighting> inLstSightings) {
        AnchorPane parent = new AnchorPane();
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MapFrameOnline map = WildLogApp.getApplication().getMapOnline();
                map.getPointLayer().clearPoints();
                for (Sighting sighting : inLstSightings) {
                    map.getPointLayer().addPoint(UtilsGps.getLatDecimalDegree(sighting), UtilsGps.getLonDecimalDegree(sighting), 
                            Color.ORANGE, sighting, WildLogApp.getApplication());
                }
                map.getPointLayer().loadPoints(Color.YELLOW);
                swingNode.setContent(map.getRootPane());
            }
        });
        AnchorPane.setTopAnchor(swingNode, 0.0);
        AnchorPane.setBottomAnchor(swingNode, 0.0);
        AnchorPane.setLeftAnchor(swingNode, 0.0);
        AnchorPane.setRightAnchor(swingNode, 0.0);
        parent.getChildren().add(swingNode);
        return parent;
    }
    
}
