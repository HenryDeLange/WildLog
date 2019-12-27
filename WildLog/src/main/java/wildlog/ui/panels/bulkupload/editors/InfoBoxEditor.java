package wildlog.ui.panels.bulkupload.editors;

import java.awt.Component;
import java.util.EventObject;
import java.util.Map;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.ui.panels.bulkupload.renderers.InfoBoxRenderer;


public class InfoBoxEditor extends AbstractCellEditor implements TableCellEditor {
    private Object editorValue;
    private final WildLogApp app;
    private final Location location;
    private final Visit visit;
    private final Map<Element, Integer> mapElementSuggestions;

    public InfoBoxEditor(WildLogApp inApp, Location inLocation, Visit inVisit, Map<Element, Integer> inMapElementSuggestions) {
        location = inLocation;
        visit = inVisit;
        app = inApp;
        mapElementSuggestions = inMapElementSuggestions;
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
        return InfoBoxRenderer.drawInfoBox(inTable, inValue, app, location, visit, inRow, mapElementSuggestions);
    }

}
