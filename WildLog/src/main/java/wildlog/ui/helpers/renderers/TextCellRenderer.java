package wildlog.ui.helpers.renderers;

import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class TextCellRenderer extends DefaultTableCellRenderer {
    private int primaryColumn = -1;
    private final int[] leftAlignColumns;

    public TextCellRenderer(int inPrimaryColumn, int... inLeftAlignColumns) {
        primaryColumn = inPrimaryColumn;
        leftAlignColumns = inLeftAlignColumns;
    }

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        super.getTableCellRendererComponent(inTable, inValue, inIsSelected, inHasFocus, inRow, inColumn);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setVerticalAlignment(JLabel.CENTER);
        if (inColumn == primaryColumn) {
            setFont(getFont().deriveFont(Font.BOLD, getFont().getSize()*1.2f));
            setHorizontalAlignment(JLabel.LEFT);
        }
        else {
            setHorizontalAlignment(JLabel.CENTER);
            // If specific left alignment columns where specified, then overwrite the alignment
            if (leftAlignColumns != null) {
                for (int col : leftAlignColumns) {
                    if (inColumn == col) {
                        setHorizontalAlignment(JLabel.LEFT);
                        break;
                    }
                }
            }
        }
        setToolTipText(getText());
        if (!inTable.isEnabled()) {
            this.setEnabled(false);
        }
        return this;
    }

}
