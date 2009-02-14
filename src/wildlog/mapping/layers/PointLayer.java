/*
 * PointLayer.java is part of WildLog
 *
 * Copyright (C) 2009 Henry James de Lange
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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