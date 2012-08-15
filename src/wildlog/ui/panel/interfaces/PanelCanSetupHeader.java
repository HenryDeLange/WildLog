package wildlog.ui.panel.interfaces;

import javax.swing.JPanel;
import wildlog.WildLogApp;

public abstract class PanelCanSetupHeader extends JPanel {
    protected int imageIndex;
    protected WildLogApp app;

    public abstract void setupTabHeader();
}
