package wildlog.ui.panel.bulkupload.editors;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import wildlog.ui.panel.bulkupload.renderers.ImageBoxRenderer;


public class ImageBoxEditor extends AbstractCellEditor implements TableCellEditor {
    private Object editorValue;

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }
    
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
        return ImageBoxRenderer.drawImageBoxes(value, table, row, column);
    }

}
