package wildlog.ui.panel.bulkupload.renderers;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;
import wildlog.WildLogApp;
import wildlog.ui.panel.bulkupload.InfoBox;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadSightingWrapper;


public class InfoBoxRenderer implements TableCellRenderer {
    private WildLogApp app;
    private JTextField txtLocation;
    private JTextField txtVisit;

    public InfoBoxRenderer(WildLogApp inApp, JTextField inTxtLocation, JTextField inTxtVisit) {
        txtLocation = inTxtLocation;
        txtVisit = inTxtVisit;
        app = inApp;
    }

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        return drawInfoBox(inTable, inValue, app, txtLocation, txtVisit);
    }

    public static Component drawInfoBox(JTable inTable, Object inValue, WildLogApp inApp, JTextField inTxtLocation, JTextField inTxtVisit) {
        // Try to use the old panel if possible (I'm assuming it will be faster, but might use more memory and cause issues)
        BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)inValue;
        InfoBox infoBox = sightingWrapper.getInfoBox();
        if (infoBox == null)
            infoBox = new InfoBox(inApp, sightingWrapper, inTxtLocation, inTxtVisit, inTable);
        else
            infoBox.populateUI();
        return infoBox;
    }

}
