package wildlog.ui.helpers.cellrenderers;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.data.dataobjects.wrappers.SightingWrapper;


public class WildLogTreeCellRenderer extends DefaultTreeCellRenderer{
    private final static ImageIcon iconLocation = new ImageIcon(WildLogApp.class.getResource("resources/icons/Location.gif"));
    private final static ImageIcon iconVisit = new ImageIcon(WildLogApp.class.getResource("resources/icons/Visit.gif"));
    private final static ImageIcon iconElement = new ImageIcon(WildLogApp.class.getResource("resources/icons/Element.gif"));
    private final static ImageIcon iconSighting = new ImageIcon(WildLogApp.class.getResource("resources/icons/Sighting Small.gif"));
    private final static ImageIcon iconExpanded = new ImageIcon(WildLogApp.class.getResource("resources/icons/WildLog Icon Small.gif"));
    private final static ImageIcon iconSelected = new ImageIcon(WildLogApp.class.getResource("resources/icons/WildLog Icon Small Selected.gif"));


    public WildLogTreeCellRenderer(WildLogApp inApp) {
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            if (node.getUserObject() instanceof Location) {
                this.setIcon(iconLocation);
            }
            else
            if (node.getUserObject() instanceof Visit) {
                this.setIcon(iconVisit);
            }
            else
            if (node.getUserObject() instanceof Element) {
                this.setIcon(iconElement);
            }
            else
            if (node.getUserObject() instanceof SightingWrapper) {
                this.setIcon(iconSighting);
            }
            else {
                this.setIcon(iconExpanded);
            }
            if (expanded || hasFocus) {
                this.setIcon(iconExpanded);
            }
            if (sel) {
                this.setIcon(iconSelected);
            }
            this.setText(value.toString());
            return this;
        }
        else {
            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }
    }

}
