package wildlog.ui.helpers.renderers.editors;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import wildlog.ui.helpers.renderers.ButtonTableRenderer;


public class ButtonTableEditor extends AbstractCellEditor implements TableCellEditor {
    private Object editorValue;

    public ButtonTableEditor() {
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
        return ButtonTableRenderer.drawButton(inTable, inValue, inRow);
    }

}
