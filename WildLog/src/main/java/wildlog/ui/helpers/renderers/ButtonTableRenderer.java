package wildlog.ui.helpers.renderers;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import wildlog.ui.panels.PanelTabSightings;


public class ButtonTableRenderer implements TableCellRenderer {

    public ButtonTableRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        return drawButton(inTable, inValue, inRow);
    }

    public static Component drawButton(JTable inTable, Object inValue, int inRow) {
        String buttonText = (String) inValue;
        JButton button = new JButton(buttonText);
        button.setIcon(new ImageIcon(ButtonTableRenderer.class.getResource("/wildlog/resources/icons/FilterSightings.png")));
        button.setPreferredSize(new Dimension(350, 50));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent inEvent) {
                ((PanelTabSightings) ((JButton) inEvent.getSource()).getParent().getParent().getParent().getParent().getParent()).showFilterDialog();
            }
        });
        return button;
    }

}
