package wildlog.ui.helpers.renderers;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;


public class IconCellRenderer extends DefaultTableCellRenderer {
    private final int size;
    private final boolean transparent;

    public IconCellRenderer(int inSize) {
        size = inSize;
        transparent = false;
    }
    
    public IconCellRenderer(int inSize, boolean inTransparent) {
        size = inSize;
        transparent = inTransparent;
    }

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        setSize(size, size);
        if (inValue == null) {
            setIcon(null);
            setText("");
        }
        else
        if (inValue instanceof ImageIcon) {
            setIcon((ImageIcon) inValue);
            setText("");
        }
        else {
            setIcon(null);
            setText(inValue.toString());
        }
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalAlignment(SwingConstants.CENTER);
        if (!transparent) {
            setBackground(Color.BLACK);
        }
        else {
            if (inIsSelected) {
                setBackground(inTable.getSelectionBackground());
                setForeground(inTable.getSelectionForeground());
            }
            else {
                setBackground(inTable.getBackground());
                setForeground(inTable.getForeground());
            }
        }
        return this;
    }

}
