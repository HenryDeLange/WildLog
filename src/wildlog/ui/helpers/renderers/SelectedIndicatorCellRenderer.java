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

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        }
        else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setSelected(isSelected);
        setBorder(noFocusBorder);
        return this;
    }
    
}
