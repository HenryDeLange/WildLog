package wildlog.ui.panel.bulkupload.renderers;

import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellRenderer;
import wildlog.ui.panel.bulkupload.ImageBox;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageListWrapper;


public class ImageBoxRenderer  implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        BulkUploadImageListWrapper imageListWrapper = (BulkUploadImageListWrapper)value;
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (BulkUploadImageFileWrapper imageWrapper : imageListWrapper.getImageList()) {
            panel.add(new ImageBox(imageWrapper));
        }
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        return scroll;
    }

}
