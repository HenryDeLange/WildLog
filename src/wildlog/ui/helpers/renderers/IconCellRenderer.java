package wildlog.ui.helpers.renderers;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;


public class IconCellRenderer extends DefaultTableCellRenderer {
    private final int size;

    public IconCellRenderer(int inSize) {
        size = inSize;
    }

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        setSize(size, size);
        setIcon((ImageIcon)inValue);
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalAlignment(SwingConstants.CENTER);
        setText("");
        setBackground(Color.BLACK);
        return this;
    }

}
