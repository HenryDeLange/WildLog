package wildlog.ui.panels.bulkupload.editors;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import wildlog.WildLogApp;
import wildlog.ui.panels.bulkupload.renderers.InfoBoxRenderer;


public class InfoBoxEditor extends AbstractCellEditor implements TableCellEditor {
    private Object editorValue;
    private WildLogApp app;
    private JTextField txtLocation;
    private JTextField txtVisit;

    public InfoBoxEditor(WildLogApp inApp, JTextField inTxtLocation, JTextField inTxtVisit) {
        txtLocation = inTxtLocation;
        txtVisit = inTxtVisit;
        app = inApp;
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
        return InfoBoxRenderer.drawInfoBox(inTable, inValue, app, txtLocation, txtVisit, inRow);
    }

}
