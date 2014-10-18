package wildlog.ui.helpers.cellrenderers;

import java.awt.Component;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import wildlog.ui.utils.UtilsTime;


public class DateTimeCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        super.getTableCellRendererComponent(inTable, inValue, inIsSelected, inHasFocus, inRow, inColumn);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setVerticalAlignment(SwingConstants.CENTER);
        setHorizontalAlignment(SwingConstants.CENTER);
        if (inValue != null) {
            // Use SimpleDateFormat class to get a formatted String from Date object.
            String strDate = UtilsTime.WL_DATE_FORMATTER_WITH_HHMM.format((Date) inValue);
            // Sorting algorithm will work with model value. So you dont need to worry
            // about the renderer's display value.
            setText(strDate);
        }
        else {
            setText("");
        }
        setToolTipText(getText());
        if (!inTable.isEnabled()) {
            this.setEnabled(false);
        }
        return this;
    }

}
