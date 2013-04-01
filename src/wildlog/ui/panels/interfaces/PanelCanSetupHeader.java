package wildlog.ui.panels.interfaces;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    protected String tabLabel;
    protected URL tabIconURL;

    public void setupTabHeader() {
        doSetupTabHeader(tabLabel, tabIconURL);
    }

    private void doSetupTabHeader(String inLabel, URL inIconURL) {
        HeaderPanel tabHeader = new HeaderPanel();
        // FIXME: Issue waar die scrolling nie werk as mens nie in die panel is nie (die panel moet die hele tab vol stretch...)
        tabHeader.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        ImageIcon icon = new ImageIcon(inIconURL);
        tabHeader.add(new JLabel(icon));
        tabHeader.setIcon(icon);
        if (inLabel != null) {
            tabHeader.add(new JLabel(inLabel + " "));
            tabHeader.setLabel(inLabel);
        }
        else {
            tabHeader.add(new JLabel("[new] "));
            tabHeader.setLabel("[new]");
        }
        JButton btnClose = new JButton();
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setPreferredSize(new Dimension(12, 12));
        btnClose.setBackground(new Color(255, 000, 000));
        btnClose.setToolTipText("Close");
        btnClose.setIcon(new ImageIcon(WildLogApp.class.getResource("resources/icons/Close.gif")));
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeTab();
            }
        });
        tabHeader.add(btnClose);
        tabHeader.setBackground(new Color(0, 0, 0, 0));
        JTabbedPane tabbedPane = ((JTabbedPane)getParent());
        int index = tabbedPane.indexOfComponent(this);
        tabbedPane.setTabComponentAt(index, tabHeader);
        // Make tabs scrollable with the mouse
        tabHeader.setParentPanel(this);
        UtilsUI.attachMouseScrollToTabs(tabbedPane, tabHeader, -1);
    }

    public abstract void closeTab();

    public class HeaderPanel extends JPanel {
        private JPanel parentPanel;
        private String label;
        private Icon icon;

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

        public String getLabel() {
            return label;
        }

        public void setLabel(String inLabel) {
            label = inLabel;
        }
    }
}
