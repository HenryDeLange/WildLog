package wildlog.ui.maps.implementations;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Sighting;
import wildlog.maps.utils.UtilsGPS;
import wildlog.ui.maps.MapsBaseDialog;
import wildlog.ui.maps.implementations.helpers.AbstractMap;
import wildlog.ui.maps.implementations.helpers.MapFrameOnline;


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
                    setActiveSubCategoryTitle("Old WildLog Online Map");
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
                MapFrameOnline map = getOldOnlineMap();
                map.getPointLayer().clearPoints();
                for (Sighting sighting : inLstSightings) {
                    map.getPointLayer().addPoint(UtilsGPS.getLatDecimalDegree(sighting), UtilsGPS.getLonDecimalDegree(sighting), 
                            Color.ORANGE, sighting, WildLogApp.getApplication());
                }
                map.getPointLayer().loadPoints(Color.YELLOW);
                swingNode.setContent(map);
            }
        });
        AnchorPane.setTopAnchor(swingNode, 0.0);
        AnchorPane.setBottomAnchor(swingNode, 0.0);
        AnchorPane.setLeftAnchor(swingNode, 0.0);
        AnchorPane.setRightAnchor(swingNode, 0.0);
        parent.getChildren().add(swingNode);
        return parent;
    }
    
    private MapFrameOnline getOldOnlineMap() {
        final GeoPosition defaultPosition = new GeoPosition(
                WildLogApp.getApplication().getWildLogOptions().getDefaultLatitude(), 
                WildLogApp.getApplication().getWildLogOptions().getDefaultLongitude());
        JXMapKit mapOnline = new JXMapKit();
        MapFrameOnline mapOnlineFrame = new MapFrameOnline(mapOnline, WildLogApp.getApplication());
        mapOnlineFrame.setPreferredSize(new Dimension(950, 650));
        mapOnlineFrame.setLayout(new BorderLayout());
        mapOnline.setDefaultProvider(JXMapKit.DefaultProviders.OpenStreetMaps);
        mapOnline.setAddressLocationShown(false);
        mapOnline.setName("mapOnline");
        mapOnline.setAddressLocation(defaultPosition);
        mapOnline.setZoom(12);
        mapOnlineFrame.add(mapOnline, BorderLayout.CENTER);
        // Previous button
        JButton btnPrevMapPoint = new JButton();
        btnPrevMapPoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapOnlineFrame.getPointLayer().loadPrevClickedPoint(WildLogApp.getApplication());
            }
        });
        btnPrevMapPoint.setPreferredSize(new Dimension(60, 25));
        btnPrevMapPoint.setFocusPainted(false);
        btnPrevMapPoint.setIcon(new ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif")));
        btnPrevMapPoint.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPrevMapPoint.setToolTipText("Load the previous Observation in the information panel.");
        mapOnlineFrame.add(btnPrevMapPoint, BorderLayout.WEST);
        // Next button
        JButton btnNextMapPoint = new JButton();
        btnNextMapPoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapOnlineFrame.getPointLayer().loadNextClickedPoint(WildLogApp.getApplication());
            }
        });
        btnNextMapPoint.setPreferredSize(new Dimension(60, 25));
        btnNextMapPoint.setFocusPainted(false);
        btnNextMapPoint.setIcon(new ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif")));
        btnNextMapPoint.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNextMapPoint.setToolTipText("Load the next Observation in the information panel.");
        mapOnlineFrame.add(btnNextMapPoint, BorderLayout.EAST);
        return mapOnlineFrame;
    }
    
}
