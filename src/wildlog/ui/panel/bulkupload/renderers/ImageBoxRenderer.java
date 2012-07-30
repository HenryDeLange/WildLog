package wildlog.ui.panel.bulkupload.renderers;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import wildlog.ui.panel.bulkupload.ImageBox;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageFileWrapper;


public class ImageBoxRenderer  implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return new ImageBox((BulkUploadImageFileWrapper)value);
    }

}
