package wildlog.ui.panels.interfaces;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import wildlog.WildLogApp;
import wildlog.ui.utils.UtilsUI;

public abstract class PanelCanSetupHeader extends JPanel {
    protected String tabTitle;
    protected URL tabIconURL;
    protected HeaderPanel tabHeader;

    public static enum TabTypes {
        LOCATION, ELEMENT, VISIT, SIGHTING, BULK_UPLOAD;
    }

    public class HeaderPanel extends JPanel {
        private JPanel parentPanel;
        private String title;
        private Icon icon;
        private TabTypes tabType;

        public JPanel getParentPanel() {
            return parentPanel;
        }

        public void setParentPanel(JPanel inParentPanel) {
            parentPanel = inParentPanel;
        }

        public Icon getIcon() {
            return icon;
        }

        public void setIcon(Icon inIcon) {
            icon = inIcon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String inTitle) {
            title = inTitle;
        }

        public TabTypes getTabType() {
            return tabType;
        }

        public void setTabType(TabTypes inTabType) {
            tabType = inTabType;
        }
    }

    public void setupTabHeader(TabTypes inTabType) {
        doSetupTabHeader(tabTitle, tabIconURL, inTabType);
    }

    private void doSetupTabHeader(String inTitleText, URL inIconURL, TabTypes inTabType) {
        HeaderPanel tempTabHeader = new HeaderPanel();
        tempTabHeader.setTabType(inTabType);
        // FIXME: Issue waar die scrolling nie werk as mens nie in die panel is nie (die panel moet die hele tab vol stretch...)
        tempTabHeader.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        // Setup Icon
        ImageIcon icon = new ImageIcon(inIconURL);
        tempTabHeader.add(new JLabel(icon));
        tempTabHeader.setIcon(icon);
        JLabel titleLabel = new JLabel();
        if (inTitleText != null) {
            titleLabel.setText(inTitleText + " ");
            tempTabHeader.setTitle(inTitleText);
        }
        else {
            titleLabel.setText("[new] ");
            tempTabHeader.setTitle("[new]");
        }
        Dimension dimension = titleLabel.getPreferredSize();
        dimension.height = 30;
        titleLabel.setPreferredSize(dimension);
        tempTabHeader.add(titleLabel);
        JButton btnClose = new JButton();
        btnClose.setFocusPainted(false);
        btnClose.setFocusable(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setPreferredSize(new Dimension(12, 12));
        btnClose.setToolTipText("Close this tab.");
        btnClose.setIcon(new ImageIcon(WildLogApp.class.getResource("resources/icons/Close.gif")));
        btnClose.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                closeTab();
            }
        });
        tempTabHeader.add(btnClose);
        tempTabHeader.setBackground(new Color(0, 0, 0, 0));
        // Keep reference to the tab header on the panel
        tabHeader = tempTabHeader;
        // Setup the tabPanel to display the custom tabheader
        JTabbedPane tabbedPane = ((JTabbedPane)getParent());
        int index = tabbedPane.indexOfComponent(this);
        tabbedPane.setTabComponentAt(index, tempTabHeader);
        // Make tabs scrollable with the mouse
        tempTabHeader.setParentPanel(this);
        UtilsUI.attachMouseScrollToTabs(tabbedPane, tempTabHeader, -1);
    }

    /**
     * Closes the tab. Some implementations may show a popup to ask whether to save the tab or not.
     * Return true if the tab was closes (regardless of being saved or not) and false if the
     * tab was not closed (for example the Cancel option was selected on the popup).
     * @return
     */
    public abstract boolean closeTab();

    public HeaderPanel getTabHeader() {
        return tabHeader;
    }

}
