package wildlog.ui.panels.bulkupload.editors;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import wildlog.ui.panels.bulkupload.renderers.ImageBoxRenderer;


public class ImageBoxEditor extends AbstractCellEditor implements TableCellEditor {
    private Object editorValue;

    @Override
    public boolean shouldSelectCell(EventObject inEvent) {
        return false;
    }

    @Override
    public boolean isCellEditable(EventObject inEvent) {
        return true;
    }

    @Override
    public Object getCellEditorValue() {
        return editorValue;
    }

    @Override
    public Component getTableCellEditorComponent(JTable inTable, Object inValue, boolean inIsSelected, int inRow, int inColumn) {
        editorValue = inValue;
        return ImageBoxRenderer.drawImageBoxes(inValue, inTable, inRow, inColumn);
    }

}
