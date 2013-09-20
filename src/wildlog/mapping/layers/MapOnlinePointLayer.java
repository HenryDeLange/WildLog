package wildlog.mapping.layers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JTextPane;
import org.jdesktop.swingx.JXMapKit;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.mapviewer.WaypointRenderer;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.interfaces.DataObjectWithHTML;
import wildlog.html.utils.UtilsHTMLExportTypes;
import wildlog.mapping.helpers.WildLogScrollPanel;

/**
 *
 * @author Henry
 */
public class MapOnlinePointLayer {
    class MapPoint {
        public double latitude;
        public double longitude;
        public DataObjectWithHTML objectWithHTML;
    }
    final private JXMapKit map;
    private List<MapPoint> points = new ArrayList<MapPoint>();
    private List<MapPoint> clickedPoints = new ArrayList<MapPoint>();
    private int clickedPointIndex = -1;

    public MapOnlinePointLayer(JXMapKit inMap) {
        map = inMap;
    }

    public void clearPoints() {
        points.clear();
        clickedPoints.clear();
        map.getMainMap().setOverlayPainter(null);
//        for (MouseListener mouse : map.getMainMap().getMouseListeners())
//            if (mouse instanceof WildLogMapMouseListener)
//                map.getMainMap().removeMouseListener(mouse);
    }

    public void addPoint(final double inLatitude, final double inLongitude, final Color inColor, DataObjectWithHTML inObjectWithHTML, WildLogApp inApp) {
        MapPoint point = new MapPoint();
        point.latitude = inLatitude;
        point.longitude = inLongitude;
        point.objectWithHTML = inObjectWithHTML;
        points.add(point);
    }

    public int showPopup(MouseEvent inEvent, WildLogApp inApp) {
        clickedPoints.clear();
        for (MapPoint point : points) {
            GeoPosition gp = new GeoPosition(point.latitude, point.longitude);
            //convert to world bitmap
            Point2D gp_pt = map.getMainMap().getTileFactory().geoToPixel(gp, map.getMainMap().getZoom());
            //convert to screen
            Rectangle rect = map.getMainMap().getViewportBounds();
            Point converted_gp_pt = new Point((int)gp_pt.getX()-rect.x, (int)gp_pt.getY()-rect.y);
            //check if near the mouse
            if(converted_gp_pt.distance(inEvent.getPoint()) < 12) {
                clickedPoints.add(point);
            }
            else {
                WildLogScrollPanel scrollPane = null;
                for (Component comp : map.getMainMap().getComponents()) {
                    if (comp instanceof WildLogScrollPanel) {
                        scrollPane = (WildLogScrollPanel)comp;
                        break;
                    }
                }
                if (scrollPane != null)
                    scrollPane.setVisible(false);
            }
        }
        if (clickedPoints.size() > 0) {
            clickedPointIndex = -1;
            loadNextClickedPoint(inApp);
        }
        return clickedPoints.size();
    }

    public void loadNextClickedPoint(WildLogApp inApp) {
        clickedPointIndex++;
        if (clickedPointIndex >= clickedPoints.size())
            clickedPointIndex = 0;
        loadClickedPoint(inApp);
    }

    public void loadPrevClickedPoint(WildLogApp inApp) {
        clickedPointIndex--;
        if (clickedPointIndex < 0)
            clickedPointIndex = clickedPoints.size()-1;
        loadClickedPoint(inApp);
    }

    private void loadClickedPoint(WildLogApp inApp) {
        if (clickedPointIndex >= 0 && clickedPointIndex < clickedPoints.size()) {
            WildLogScrollPanel scrollPane = null;
            for (Component comp : map.getMainMap().getComponents()) {
                if (comp instanceof WildLogScrollPanel) {
                    scrollPane = (WildLogScrollPanel)comp;
                    JTextPane textPane = (JTextPane)scrollPane.getViewport().getComponent(0);
                    textPane.setText("Showing " + (clickedPointIndex+1) + " of " + clickedPoints.size() + "<br/>" + clickedPoints.get(clickedPointIndex).objectWithHTML.toHTML(false, true, inApp, UtilsHTMLExportTypes.ForMap, null));
                    break;
                }
            }
            scrollPane.setLocation(new Point(50, 15));
            scrollPane.setVisible(true);
        }
    }

    public void loadPoints(final Color inColor) {
        //create a Set of waypoints
        Set<Waypoint> waypoints = new HashSet<Waypoint>(points.size());
        for (MapPoint point : points) {
            waypoints.add(new Waypoint(point.latitude, point.longitude));
        }

        //create a WaypointPainter to draw the points
        WaypointPainter painter = new WaypointPainter();
        painter.setWaypoints(waypoints);
        /* Optionele point painter. Ek kan dalk die WayPoint class extend en 'n
         * custom ElementType filed by sit om die punte ander kleure te gee.
         */
        painter.setRenderer(new WaypointRenderer() {
            @Override
            public boolean paintWaypoint(Graphics2D g, JXMapViewer map, Waypoint wp) {
                g.setColor(inColor);
                g.fillRect(-10,-10,+10,+10);
                g.setColor(Color.BLACK);
                g.drawRect(-10,-10,+10,+10);
                return true;
            }
        });
        map.getMainMap().setOverlayPainter(painter);
    }

}
