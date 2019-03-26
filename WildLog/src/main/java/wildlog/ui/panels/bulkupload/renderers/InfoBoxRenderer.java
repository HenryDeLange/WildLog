package wildlog.ui.panels.bulkupload.renderers;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Location;
import wildlog.data.dataobjects.Visit;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.bulkupload.InfoBox;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadSightingWrapper;


public class InfoBoxRenderer implements TableCellRenderer {
    private final WildLogApp app;
    private final Location location;
    private final Visit visit;

    public InfoBoxRenderer(WildLogApp inApp, Location inLocation, Visit inVisit) {
        location = inLocation;
        visit = inVisit;
        app = inApp;
    }

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        return drawInfoBox(inTable, inValue, app, location, visit, inRow);
    }

    public static Component drawInfoBox(JTable inTable, Object inValue, WildLogApp inApp, Location inLocation, Visit inVisit, int inRow) {
        // Try to use the old panel if possible (I'm assuming it will be faster, but might use more memory and cause issues)
        BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)inValue;
        InfoBox infoBox = sightingWrapper.getInfoBox();
        if (infoBox == null) {
            infoBox = new InfoBox(inApp, sightingWrapper, inLocation, inVisit, inTable);
        } else {
            infoBox.populateUI();
        }
        if (inRow % 2 == 0) {
            infoBox.setRowBackground(BulkUploadPanel.tableBackgroundColor1);
        } else {
            infoBox.setRowBackground(BulkUploadPanel.tableBackgroundColor2);
        }
        infoBox.getLblCount().setText(Integer.toString(inRow + 1));
        return infoBox;
    }

}
