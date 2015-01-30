package wildlog.ui.helpers.renderers;

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


public class WildLogTreeCellRenderer extends DefaultTreeCellRenderer {
    private final static ImageIcon iconLocation = new ImageIcon(WildLogApp.class.getResource("resources/icons/Location.gif"));
    private final static ImageIcon iconVisit = new ImageIcon(WildLogApp.class.getResource("resources/icons/Visit.gif"));
    private final static ImageIcon iconElement = new ImageIcon(WildLogApp.class.getResource("resources/icons/Element.gif"));
    private final static ImageIcon iconSighting = new ImageIcon(WildLogApp.class.getResource("resources/icons/Sighting Small.gif"));
    private final static ImageIcon iconExpanded = new ImageIcon(WildLogApp.class.getResource("resources/icons/WildLog Icon Small.gif"));
    private final static ImageIcon iconSelected = new ImageIcon(WildLogApp.class.getResource("resources/icons/WildLog Icon Small Selected.gif"));


    public WildLogTreeCellRenderer() {
    }

    @Override
    public Component getTreeCellRendererComponent(JTree inTree, Object inValue, boolean inSelected, boolean inExpanded, boolean inLeaf, int inRow, boolean inHasFocus) {
        if (inValue instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)inValue;
            if (node.getUserObject() instanceof Location) {
                setIcon(iconLocation);
            }
            else
            if (node.getUserObject() instanceof Visit) {
                setIcon(iconVisit);
            }
            else
            if (node.getUserObject() instanceof Element) {
                setIcon(iconElement);
            }
            else
            if (node.getUserObject() instanceof SightingWrapper) {
                setIcon(iconSighting);
            }
            else {
                setIcon(iconExpanded);
            }
            if (inExpanded || inHasFocus) {
                setIcon(iconExpanded);
            }
            if (inSelected) {
                setIcon(iconSelected);
            }
            setText(inValue.toString());
            return this;
        }
        else {
            return super.getTreeCellRendererComponent(inTree, inValue, inSelected, inExpanded, inLeaf, inRow, inHasFocus);
        }
    }

}
