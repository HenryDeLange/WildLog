package wildlog.ui.helpers.cellrenderers;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;


public class DateCellRenderer extends DefaultTableCellRenderer {
    private final static SimpleDateFormat formater = new SimpleDateFormat("dd MMM yyyy");

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        super.getTableCellRendererComponent(inTable, inValue, inIsSelected, inHasFocus, inRow, inColumn);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalAlignment(SwingConstants.CENTER);
        if (inValue != null) {
            // Use SimpleDateFormat class to get a formatted String from Date object.
            String strDate = formater.format((Date) inValue);
            // Sorting algorithm will work with model value. So you dont need to worry
            // about the renderer's display value.
            setText(strDate);
        }
        else {
            setText("");
        }
        if (!inTable.isEnabled()) {
            this.setEnabled(false);
        }
        return this;
    }

}
