package wildlog.ui.helpers;

import javax.swing.DefaultListSelectionModel;

/**
 * This selection model will make the selection behave as if CTRL is held down while clicking. 
 * In other words, rows will stay selected until re-clicked.
 */
public class CtrlClickSelectionModel extends DefaultListSelectionModel {
    private boolean gestureStarted = false;

    @Override
    public void setSelectionInterval(int inIndex0, int inIndex1) {
        if(!gestureStarted){
            if (isSelectedIndex(inIndex0)) {
                super.removeSelectionInterval(inIndex0, inIndex1);
            } else {
                super.addSelectionInterval(inIndex0, inIndex1);
            }
        }
        gestureStarted = true;
    }

    @Override
    public void setValueIsAdjusting(boolean isAdjusting) {
        if (isAdjusting == false) {
            gestureStarted = false;
        }
    }

}
