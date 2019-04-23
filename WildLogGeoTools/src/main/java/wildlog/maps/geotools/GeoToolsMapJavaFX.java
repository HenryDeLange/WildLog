package wildlog.maps.geotools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class GeoToolsMapJavaFX {
    private final JFXPanel jfxPanel;
    private final MapContent mapContent = new MapContent();
    private ImageView imageView;
    private boolean identifyIsActive = false;
    

    public GeoToolsMapJavaFX(JFXPanel inJFXPanel, boolean inEnhanceContrast) {
        jfxPanel = inJFXPanel;
        Group rootPane = new Group();
        Scene scene = new Scene(rootPane);
        jfxPanel.setScene(scene);
        imageView = new ImageView();
        if (inEnhanceContrast) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setContrast(0.15);
            colorAdjust.setBrightness(-0.15);
            colorAdjust.setSaturation(0.15);
            imageView.setEffect(colorAdjust);
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                rootPane.getChildren().add(imageView);
            }
        });
        // Add navigation listeners
        // ZOOM - SCROLLWHEEL
        imageView.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent inScrollEvent) {
// FIXME: Sit miskien 'n delay in om nie te veel onnodige setBounds te doen terwyl daar baie gescroll word nie...? (Soos die resize)
                final double ZOOM_SCALE = 0.9;
                if (inScrollEvent.getDeltaY() == 0) {
                    return;
                }
                double minX = mapContent.getViewport().getBounds().getMinX();
                double maxX = mapContent.getViewport().getBounds().getMaxX();
                double minY = mapContent.getViewport().getBounds().getMinY();
                double maxY = mapContent.getViewport().getBounds().getMaxY();
                if ((inScrollEvent.getDeltaY() > 0)) {
                    mapContent.getViewport().setBounds(new ReferencedEnvelope(
                            minX + getDistance(minX, maxX) * ZOOM_SCALE / 4.0, 
                            maxX - getDistance(minX, maxX) * ZOOM_SCALE / 4.0, 
                            minY + getDistance(minY, maxY) * ZOOM_SCALE / 4.0, 
                            maxY - getDistance(minY, maxY) * ZOOM_SCALE / 4.0, 
                            mapContent.getCoordinateReferenceSystem()));
                }
                else {
                    mapContent.getViewport().setBounds(new ReferencedEnvelope(
                            minX - getDistance(minX, maxX) * ZOOM_SCALE / 2.0, 
                            maxX + getDistance(minX, maxX) * ZOOM_SCALE / 2.0, 
                            minY - getDistance(minY, maxY) * ZOOM_SCALE / 2.0, 
                            maxY + getDistance(minY, maxY) * ZOOM_SCALE / 2.0, 
                            mapContent.getCoordinateReferenceSystem()));
                }
                reloadMap();
                inScrollEvent.consume();
            }
        });
        // ZOOM - DOUBLE CLICK + INDENTIFY
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2();
            
            @Override
            public void handle(MouseEvent inMouseEvent) {
                if (inMouseEvent.getClickCount() == 2) {
                    zoomIn();
                }
                else
                if (inMouseEvent.getClickCount() == 1 && (inMouseEvent.isControlDown() || identifyIsActive)) {
                    identifyIsActive = false;
                    imageView.getScene().getRoot().setCursor(Cursor.DEFAULT);
                    List<Map<String, String>> lstInfoForLayers = new ArrayList<>();
                    for (Layer layer : mapContent.layers()) {
                        if (layer instanceof FeatureLayer) {
                            FeatureLayer featureLayer = (FeatureLayer) layer;
                            Rectangle screenRect = new Rectangle((int) inMouseEvent.getSceneX() - 5, (int) inMouseEvent.getSceneY() - 5, 10, 10);
                            mapContent.getViewport().setScreenArea(new Rectangle(jfxPanel.getWidth(), jfxPanel.getHeight()));
                            mapContent.getViewport().setMatchingAspectRatio(true);
                            AffineTransform transform = mapContent.getViewport().getScreenToWorld();
                            Rectangle2D worldRect = transform.createTransformedShape(screenRect).getBounds2D();
                            ReferencedEnvelope bbox = new ReferencedEnvelope(worldRect, mapContent.getCoordinateReferenceSystem());
                            try {
                                String propertyName = featureLayer.getSimpleFeatureSource().getSchema().getGeometryDescriptor().getLocalName();
                                SimpleFeatureCollection features = featureLayer.getSimpleFeatureSource().getFeatures(
                                        filterFactory.bbox(propertyName, bbox.getMinX(), bbox.getMinY(), bbox.getMaxX(), bbox.getMaxY(), 
                                                bbox.getCoordinateReferenceSystem().toWKT()));
                                for (SimpleFeature simpleFeature : features.toArray(new SimpleFeature[]{})) {
                                    // Get the attributes to display
                                    Map<String, String> mapInfo = new LinkedHashMap<>();
                                    mapInfo.put("*** SHAPE LAYER ***", layer.getTitle());
                                    for (Property value : simpleFeature.getValue()) {
                                        if (value != null && value.getName() != null && value.getValue() != null 
                                                && !value.getName().toString().equals(propertyName)) {
                                            mapInfo.put(value.getName().toString(),  value.getValue().toString());
                                        }
                                    }
                                    if (!mapInfo.isEmpty()) {
                                        lstInfoForLayers.add(mapInfo);
                                    }
                                }
                            }
                            catch (Exception ex) {
                                ex.printStackTrace(System.err);
                            }
                        }
                        else
                        if (layer instanceof GridReaderLayer) {
                            GridReaderLayer gridReaderLayer = (GridReaderLayer) layer;
                            Map<String, String> mapInfo = new LinkedHashMap<>();
                            mapInfo.put("*** RASTER LAYER ***", gridReaderLayer.getTitle());
                            lstInfoForLayers.add(mapInfo);
                        }
                    }
                    // Die cursor is dodgy, so ek stel dit maar orals en wrapped in Swing + JavaFx runnables
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setCursor(Cursor.DEFAULT);
                                    imageView.getScene().setCursor(Cursor.DEFAULT);
                                    imageView.getScene().getRoot().setCursor(Cursor.DEFAULT);
                                }
                            });
                        }
                    });
                    if (!lstInfoForLayers.isEmpty()) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Alert dialog = new Alert(Alert.AlertType.INFORMATION);
                                dialog.setResizable(true);
                                dialog.setTitle("Indentify Map Features");
                                dialog.setHeaderText("The following features were identified at the selected location:");
                                ListView<String> listView = new ListView<>();
                                for (Map<String, String> mapInfo : lstInfoForLayers) {
                                    for (Map.Entry<String, String> entry : mapInfo.entrySet()) {
                                        listView.getItems().add(entry.getKey() + " : " + entry.getValue());
                                    }
                                }
                                GridPane.setVgrow(listView, Priority.ALWAYS);
                                GridPane.setHgrow(listView, Priority.ALWAYS);
                                GridPane content = new GridPane();
                                content.setPrefWidth(450);
                                content.setPrefHeight(350);
                                content.setMaxWidth(Double.MAX_VALUE);
                                content.add(listView, 0, 0);
                                dialog.getDialogPane().setContent(content);
                                dialog.initOwner(jfxPanel.getScene().getWindow());
                                dialog.showAndWait();
                            }
                        });
                    }
                }
            }
        });
        // PAN - MOUSE DRAG
        final DragDelta dragDelta = new DragDelta();
        imageView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent inMouseEvent) {
                dragDelta.x = imageView.getLayoutX() - inMouseEvent.getSceneX();
                dragDelta.y = imageView.getLayoutY() - inMouseEvent.getSceneY();
            }
        });
        imageView.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent inMouseEvent) {
                // Only do this when dragging
                if (imageView.getCursor() != null && imageView.getCursor().equals(Cursor.MOVE)) {
                    imageView.setCursor(Cursor.DEFAULT);
                    // Reload the new full frame
                    double dragDeltaX = inMouseEvent.getSceneX() + dragDelta.x;
                    double dragDeltaY = inMouseEvent.getSceneY() + dragDelta.y;
                    double minX = mapContent.getViewport().getBounds().getMinX();
                    double maxX = mapContent.getViewport().getBounds().getMaxX();
                    double minY = mapContent.getViewport().getBounds().getMinY();
                    double maxY = mapContent.getViewport().getBounds().getMaxY();
                    double screenDeltaScaleX = dragDeltaX / jfxPanel.getWidth();
                    double newMinX = minX - (getDistance(minX, maxX) * screenDeltaScaleX);
                    double newMaxX = maxX - (getDistance(minX, maxX) * screenDeltaScaleX);
                    double screenDeltaScaleY = dragDeltaY / jfxPanel.getHeight();
                    double newMinY = minY + (getDistance(minY, maxY) * screenDeltaScaleY);
                    double newMaxY = maxY + (getDistance(minY, maxY) * screenDeltaScaleY);
                    mapContent.getViewport().setBounds(new ReferencedEnvelope(
                            newMinX, newMaxX, newMinY, newMaxY, mapContent.getCoordinateReferenceSystem()));
                    reloadMap();
                    // Move the existing image back to the original position
                    imageView.setLayoutX(0.0);
                    imageView.setLayoutY(0.0);
                }
            }
        });
        imageView.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent inMouseEvent) {
                if (imageView.getCursor() == null || !imageView.getCursor().equals(Cursor.MOVE)) {
                    imageView.setCursor(Cursor.MOVE);
                }
                // Move the existing image
                imageView.setLayoutX(inMouseEvent.getSceneX() + dragDelta.x);
                imageView.setLayoutY(inMouseEvent.getSceneY() + dragDelta.y);
            }
        });
        // RESIZE - PANEL
        jfxPanel.addComponentListener(new ResizeComponentAdapter());
    }
    
// TODO: Doen dalk iets soortgelyk vir die scrollwheel zoom
    private class ResizeComponentAdapter extends ComponentAdapter implements ActionListener {
        private Timer delayTimer = null;
        
        @Override
        public void componentResized(ComponentEvent e) {
            if (delayTimer == null) {
                // Start waiting for DELAY to elapse.
                delayTimer = new Timer(500, this);
                delayTimer.start();
            }
            else {
                // Event came too soon, swallow it by resetting the timer..
                delayTimer.restart();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == this.delayTimer) {
                // Stop timer
                delayTimer.stop();
                delayTimer = null;
                // Resize
                reloadMap();
            }
        }
    }
    
    private BufferedImage getMapAsImage() {
        GTRenderer renderer = new StreamingRenderer();
        renderer.setMapContent(mapContent);
        ReferencedEnvelope mapBounds = mapContent.getViewport().getBounds();
        double mapContentRatio = mapBounds.getSpan(1) / mapBounds.getSpan(0);
        int imageHeight = (int) Math.round(jfxPanel.getWidth() * mapContentRatio);
// TODO: Kyk of ek nie die aantal setBounds (resizing) kan verminder nie
        double displayRatio = (double) jfxPanel.getHeight() / (double) imageHeight;
        mapBounds.setBounds(new ReferencedEnvelope(
                mapBounds.getMinX(), 
                mapBounds.getMaxX(), 
                mapBounds.getMinY() * displayRatio, 
                mapBounds.getMaxY() * displayRatio, 
                mapContent.getCoordinateReferenceSystem()));
        imageHeight = jfxPanel.getHeight();
        Rectangle imageBounds = new Rectangle(0, 0, jfxPanel.getWidth(), imageHeight);
        // Use TYPE_INT_ARGB for JavaFx to skip conversion step
        BufferedImage image = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_ARGB); 
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setPaint(Color.DARK_GRAY);
        graphics2D.fill(imageBounds);
        ((StreamingRenderer) renderer).paint(graphics2D, imageBounds, mapBounds);
        return image;
    }
    
    private double getDistance(double inMin, double inMax) {
        return Math.abs(inMax - inMin);
    }
    
    private static class DragDelta {
        double x;
        double y;
    }
    
    public void reloadMap() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (imageView.getImage() != null /*&& imageView.getImage() instanceof WritableImage*/) {
                    imageView.setImage(SwingFXUtils.toFXImage(getMapAsImage(), (WritableImage) imageView.getImage()));
                }
                else {
                    imageView.setImage(SwingFXUtils.toFXImage(getMapAsImage(), null));
                }
            }
        });
    }
    
    public void addLayer(Layer inLayer) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mapContent.addLayer(inLayer);
            }
        });
    }
    
    public void replaceLayer(int inIndex, Layer inNewLayer) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // The set() call will remove and dispose the old layer
                mapContent.layers().set(inIndex, inNewLayer);
            }
        });
    }
    
    public void removeLayer(int inIndex) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mapContent.layers().remove(inIndex);
            }
        });
    }
    
    public int getLayerCount() {
        return mapContent.layers().size();
    }
    
    public ReferencedEnvelope getBounds() {
        return mapContent.getViewport().getBounds();
    }
    
    public void setBounds(ReferencedEnvelope inReferencedEnvelope) {
         Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mapContent.getViewport().setBounds(inReferencedEnvelope);
            }
         });
    }
    
    public CoordinateReferenceSystem getMapCoordinateReferenceSystem() {
        return mapContent.getCoordinateReferenceSystem();
    }
    
    public void dispose() {
        mapContent.dispose();
    }
    
    public void zoomIn() throws MismatchedDimensionException {
        final double ZOOM_SCALE = 0.5;
        double minX = mapContent.getViewport().getBounds().getMinX();
        double maxX = mapContent.getViewport().getBounds().getMaxX();
        double minY = mapContent.getViewport().getBounds().getMinY();
        double maxY = mapContent.getViewport().getBounds().getMaxY();
        mapContent.getViewport().setBounds(new ReferencedEnvelope(
                minX + getDistance(minX, maxX) * ZOOM_SCALE / 4.0,
                maxX - getDistance(minX, maxX) * ZOOM_SCALE / 4.0,
                minY + getDistance(minY, maxY) * ZOOM_SCALE / 4.0,
                maxY - getDistance(minY, maxY) * ZOOM_SCALE / 4.0,
                mapContent.getCoordinateReferenceSystem()));
        reloadMap();
    }
    
    public void zoomOut() throws MismatchedDimensionException {
        final double ZOOM_SCALE = 0.5;
        double minX = mapContent.getViewport().getBounds().getMinX();
        double maxX = mapContent.getViewport().getBounds().getMaxX();
        double minY = mapContent.getViewport().getBounds().getMinY();
        double maxY = mapContent.getViewport().getBounds().getMaxY();
        mapContent.getViewport().setBounds(new ReferencedEnvelope(
                minX - getDistance(minX, maxX) * ZOOM_SCALE / 4.0,
                maxX + getDistance(minX, maxX) * ZOOM_SCALE / 4.0,
                minY - getDistance(minY, maxY) * ZOOM_SCALE / 4.0,
                maxY + getDistance(minY, maxY) * ZOOM_SCALE / 4.0,
                mapContent.getCoordinateReferenceSystem()));
        reloadMap();
    }
    
    public void setStartBounds(double inLat, double inLon, double inZoom) {
        // Delay the repositioning a little bit to give the jfxPanel time to get its final size
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        double ratio = (double) jfxPanel.getHeight() / (double) jfxPanel.getWidth();
                        double x1 = inLon - (inZoom * 0.5);
                        double x2 = inLon + (inZoom * 0.5);
                        double y1 = inLat - (inZoom * 0.5 * ratio);
                        double y2 = inLat + (inZoom * 0.5 * ratio);
                        mapContent.getViewport().setMatchingAspectRatio(true);
                        mapContent.getViewport().setBounds(new ReferencedEnvelope(
                                Math.min(x1, x2), Math.max(x1, x2),
                                Math.min(y1, y2), Math.max(y1, y2),
                                mapContent.getCoordinateReferenceSystem()));
                        reloadMap();
                    }
                });
            }
        });
    }
    
    public void identify() {
        identifyIsActive = true;
        // Die cursor is dodgy, so ek stel dit maar orals en wrapped in Swing + JavaFx runnables
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setCursor(Cursor.CROSSHAIR);
                        imageView.getScene().setCursor(Cursor.CROSSHAIR);
                        imageView.getScene().getRoot().setCursor(Cursor.CROSSHAIR);
                    }
                });
            }
        });
    }
    
}
