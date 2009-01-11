package wildlog.mapping.layers;

import java.awt.Color;

import com.bbn.openmap.Layer;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.omGraphics.OMPoint;
import java.util.ArrayList;
import java.util.List;


public class PointLayer extends Layer {
    // Variables:
    private OMGraphicList omgraphics;
    private List<OMPoint> pointList;

    // Constructor:
    public PointLayer() {
        omgraphics = new OMGraphicList();
        createGraphics(omgraphics);
    }

    // Methods:
    public OMGraphicList createGraphics(OMGraphicList graphics) {
        graphics.clear();
        if (pointList != null) {
            for (int t = 0; t < pointList.size(); t++) {
                graphics.addOMGraphic(pointList.get(t));
            }
        }
        return graphics;
    }

    public void setPointList(List<OMPoint> inList) {
        pointList = inList;
    }

    public void addPoint(float inLat, float inLon, int inSize, Color inColor) {
        if (pointList == null) pointList = new ArrayList<OMPoint>(1);
        OMPoint point = new OMPoint(inLat, inLon, inSize);
        point.setLinePaint(Color.BLACK);
        point.setFillColor(inColor);
        pointList.add(point);
        createGraphics(omgraphics);
    }

    //----------------------------------------------------------------------
    // Layer overrides
    //----------------------------------------------------------------------

    /**
     * Renders the graphics list. It is important to make this routine
     * as fast as possible since it is called frequently by Swing, and
     * the User Interface blocks while painting is done.
     */
    public void paint(java.awt.Graphics g) {
        omgraphics.render(g);
    }

    //----------------------------------------------------------------------
    // ProjectionListener interface implementation
    //----------------------------------------------------------------------

    /**
     * Handler for <code>ProjectionEvent</code>s. This function is
     * invoked when the <code>MapBean</code> projection changes. The
     * graphics are reprojected and then the Layer is repainted.
     * <p>
     * 
     * @param e the projection event
     */
    public void projectionChanged(ProjectionEvent e) {
        omgraphics.project(e.getProjection(), true);
        repaint();
    }
}