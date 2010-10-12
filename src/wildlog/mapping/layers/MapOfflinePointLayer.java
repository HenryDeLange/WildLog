package wildlog.mapping.layers;

import java.awt.Color;

import com.bbn.openmap.Layer;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.omGraphics.OMPoint;
import java.util.ArrayList;
import java.util.List;


public class MapOfflinePointLayer extends Layer {
    // Variables:
    private OMGraphicList omgraphics;
    private List<OMPoint> pointList;

    // Constructor:
    public MapOfflinePointLayer() {
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

    @Override
    public void paint(java.awt.Graphics g) {
        omgraphics.render(g);
    }

    @Override
    public void projectionChanged(ProjectionEvent e) {
        omgraphics.project(e.getProjection(), true);
        repaint();
    }
}