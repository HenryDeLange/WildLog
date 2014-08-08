package wildlog.ui.helpers;

import javax.swing.DefaultListSelectionModel;

/**
 * This selection model will make the selection behave as if CTRL is held down while clicking. 
 * In other words, rows will stay selected until re-clicked.
 */
public class CtrlClickSelectionModel extends DefaultListSelectionModel {
    
    @Override
    public void setSelectionInterval(int index0, int index1) {
        if(isSelectedIndex(index0)) {
            removeSelectionInterval(index0, index1);
        }
        else {
            addSelectionInterval(index0, index1);
        }
    }
    
}
