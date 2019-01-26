package wildlog.ui.helpers.renderers;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;

/**
 * Based on the code from JTable.BooleanRenderer (which I can't easily extend since it isn't public).
 */
public class SelectedIndicatorCellRenderer extends JCheckBox implements TableCellRenderer, UIResource {
    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public SelectedIndicatorCellRenderer() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        setBorderPainted(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        if (inIsSelected) {
            setForeground(inTable.getSelectionForeground());
            super.setBackground(inTable.getSelectionBackground());
        }
        else {
            setForeground(inTable.getForeground());
            setBackground(inTable.getBackground());
        }
        setSelected(inIsSelected);
        setBorder(noFocusBorder);
        return this;
    }
    
}
