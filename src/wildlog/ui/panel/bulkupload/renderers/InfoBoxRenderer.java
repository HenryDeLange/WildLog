package wildlog.ui.panel.bulkupload.renderers;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import wildlog.WildLogApp;
import wildlog.ui.panel.bulkupload.InfoBox;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadSightingWrapper;


public class InfoBoxRenderer implements TableCellRenderer {
    private WildLogApp app;

    public InfoBoxRenderer(WildLogApp inApp) {
        app = inApp;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return new InfoBox(app, (BulkUploadSightingWrapper)value);
    }

}
