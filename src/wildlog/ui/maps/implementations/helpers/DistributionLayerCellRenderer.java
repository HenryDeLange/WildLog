package wildlog.ui.maps.implementations.helpers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class DistributionLayerCellRenderer extends DefaultTableCellRenderer {
    private int primaryColumn = -1;
    private final int[] leftAlignColumns;

    public DistributionLayerCellRenderer(int inPrimaryColumn, int... inLeftAlignColumns) {
        primaryColumn = inPrimaryColumn;
        leftAlignColumns = inLeftAlignColumns;
    }

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        super.getTableCellRendererComponent(inTable, inValue, inIsSelected, inHasFocus, inRow, inColumn);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setVerticalAlignment(JLabel.CENTER);
        if (inColumn == primaryColumn) {
            setFont(getFont().deriveFont(Font.BOLD, getFont().getSize()*1.1f));
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
        if (!inIsSelected) {
            if (inTable.getValueAt(inRow, 0) == null || inTable.getValueAt(inRow, 0).toString().isEmpty()) {
                // Creature present, but not linked (red)
                setBackground(new Color(248, 238, 237));
            }
            else {
                if (inTable.getValueAt(inRow, 1) == null || inTable.getValueAt(inRow, 1).toString().isEmpty()) {
                    // Layer present, but not linked (orange)
                    setBackground(new Color(245, 238, 224));
                }
                else {
                    // Linked (green)
                    setBackground(new Color(232, 242, 225));
                }
            }
        }
        return this;
    }

}
