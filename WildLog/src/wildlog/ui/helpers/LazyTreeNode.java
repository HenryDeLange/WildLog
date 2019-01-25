package wildlog.ui.helpers;

import javax.swing.tree.DefaultMutableTreeNode;


public class LazyTreeNode extends DefaultMutableTreeNode {
    private boolean isLeafNode;

    public LazyTreeNode(Object userObject, boolean isLeafNode) {
        super(userObject);
        this.isLeafNode = isLeafNode;
    }
    
    @Override
    public boolean isLeaf() {
        return isLeafNode;
    }
    
}
