package wildlog.ui.panel.bulkupload.editors;

import java.awt.Component;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import wildlog.WildLogApp;
import wildlog.ui.panel.bulkupload.InfoBox;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadSightingWrapper;


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
        return new InfoBox(app, (BulkUploadSightingWrapper)value, txtLocation, txtVisit, table);
    }

}
