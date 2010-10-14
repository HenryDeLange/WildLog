package wildlog.ui.panel.interfaces;

import wildlog.WildLogApp;

/**
 *
 * @author DeLangeH
 */
public abstract class PanelCanSetupHeader extends javax.swing.JPanel {
    protected int imageIndex;
    protected WildLogApp app;

    public abstract void setupTabHeader();
}
