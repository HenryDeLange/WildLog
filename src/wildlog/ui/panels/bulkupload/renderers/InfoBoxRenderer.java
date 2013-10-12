package wildlog.ui.panels.bulkupload.renderers;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;
import wildlog.WildLogApp;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.bulkupload.InfoBox;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadSightingWrapper;


public class InfoBoxRenderer implements TableCellRenderer {
    private WildLogApp app;
    private JTable tblLocation;
    private JTextField txtVisit;

    public InfoBoxRenderer(WildLogApp inApp, JTable inTblLocation, JTextField inTxtVisit) {
        tblLocation = inTblLocation;
        txtVisit = inTxtVisit;
        app = inApp;
    }

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        return drawInfoBox(inTable, inValue, app, tblLocation, txtVisit, inRow);
    }

    public static Component drawInfoBox(JTable inTable, Object inValue, WildLogApp inApp, JTable inTblLocation, JTextField inTxtVisit, int inRow) {
        // Try to use the old panel if possible (I'm assuming it will be faster, but might use more memory and cause issues)
        BulkUploadSightingWrapper sightingWrapper = (BulkUploadSightingWrapper)inValue;
        InfoBox infoBox = sightingWrapper.getInfoBox();
        if (infoBox == null)
            infoBox = new InfoBox(inApp, sightingWrapper, inTblLocation, inTxtVisit, inTable);
        else
            infoBox.populateUI();
        if (inRow % 2 == 0)
            infoBox.setRowBackground(BulkUploadPanel.tableBackgroundColor1);
        else
            infoBox.setRowBackground(BulkUploadPanel.tableBackgroundColor2);
        return infoBox;
    }

}
