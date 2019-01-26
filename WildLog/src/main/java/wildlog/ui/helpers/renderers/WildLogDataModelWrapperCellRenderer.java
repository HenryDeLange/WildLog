package wildlog.ui.helpers.renderers;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class WildLogDataModelWrapperCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        if (inValue != null) {
            super.getTableCellRendererComponent(inTable, ((WildLogTableModelDataWrapper) inValue).getTextValue(), inIsSelected, inHasFocus, inRow, inColumn);
        }
        else {
            super.getTableCellRendererComponent(inTable, inValue, inIsSelected, inHasFocus, inRow, inColumn);
        }
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setVerticalAlignment(JLabel.CENTER);
        setHorizontalAlignment(JLabel.CENTER);
        setToolTipText(getText());
        if (!inTable.isEnabled()) {
            this.setEnabled(false);
        }
        return this;
    }

}
