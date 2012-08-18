package wildlog.ui.panel.bulkupload.editors;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import wildlog.ui.panel.bulkupload.ImageBox;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageListWrapper;


public class ImageBoxEditor extends AbstractCellEditor implements TableCellEditor {
    private Object editorValue;

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public Object getCellEditorValue() {
        return editorValue;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        editorValue = value;
        BulkUploadImageListWrapper imageListWrapper = (BulkUploadImageListWrapper)value;
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (BulkUploadImageFileWrapper imageWrapper : imageListWrapper.getImageList()) {
            panel.add(new ImageBox(imageWrapper));
        }
        JScrollPane scroll = new JScrollPane(panel);
        return scroll;
    }

}
