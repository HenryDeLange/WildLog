package wildlog.ui.panel.bulkupload.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import wildlog.ui.panel.bulkupload.ImageBox;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageListWrapper;


public class ImageBoxRenderer  implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return drawImageBoxes(value, table, row, column);
    }

    public static JPanel drawImageBoxes(Object value, JTable table, int row, int column) {
        BulkUploadImageListWrapper imageListWrapper = (BulkUploadImageListWrapper)value;
        if (imageListWrapper.getImageList().isEmpty()) {
            return null;
        }
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(new Color(235, 246, 220));
        for (BulkUploadImageFileWrapper imageWrapper : imageListWrapper.getImageList()) {
            panel.add(new ImageBox(imageWrapper, table));
        }
        table.setRowHeight(row, 250 *
                (int)Math.ceil((double)(250 * imageListWrapper.getImageList().size())
                / (double)table.getColumnModel().getColumn(column).getWidth()));
        return panel;
    }

}
