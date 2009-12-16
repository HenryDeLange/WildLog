/*
 * WildLogTreeCellRenderer.java is part of WildLog
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

package wildlog.utils.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.wrappers.SightingWrapper;

/**
 *
 * @author Henry
 */
public class WildLogTreeCellRenderer extends DefaultTreeCellRenderer{
    private WildLogApp app;


    public WildLogTreeCellRenderer(WildLogApp inApp) {
        app = inApp;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            JLabel label = null;
            if (node.getUserObject() instanceof Location) {
                label = new JLabel(value.toString(), new ImageIcon(app.getClass().getResource("resources/icons/Location.gif")), 0);
            }
            else
            if (node.getUserObject() instanceof Visit) {
                label = new JLabel(value.toString(), new ImageIcon(app.getClass().getResource("resources/icons/Visit.gif")), 0);
            }
            else
            if (node.getUserObject() instanceof Element) {
                label = new JLabel(value.toString(), new ImageIcon(app.getClass().getResource("resources/icons/Element.gif")), 0);
            }
            else
            if (node.getUserObject() instanceof SightingWrapper) {
                label = new JLabel(value.toString(), new ImageIcon(app.getClass().getResource("resources/icons/Sighting Small.gif")), 0);
            }
            else {
                label = new JLabel(value.toString(), new ImageIcon(app.getClass().getResource("resources/icons/WildLog Icon Small.gif")), 0);
            }
            if (expanded || hasFocus)
                label = new JLabel(value.toString(), new ImageIcon(app.getClass().getResource("resources/icons/WildLog Icon Small.gif")), 0);
            return label;
        }
        else
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }

}
