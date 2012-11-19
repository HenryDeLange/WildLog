package wildlog.ui.panels.bulkupload.renderers;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.ui.panels.bulkupload.BulkUploadPanel;
import wildlog.ui.panels.bulkupload.ImageBox;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panels.bulkupload.helpers.BulkUploadImageListWrapper;


public class ImageBoxRenderer implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        return drawImageBoxes(inValue, inTable, inRow, inColumn);
    }

    public static JPanel drawImageBoxes(Object inValue, JTable inTable, int inRow, int inColumn) {
// FIXME: Possible issue still present where for some reason (after this "performance increases") a cell sometimes does not render or only slow rendering, but it seems to show again when scrolling or hovering over...
        // Note: Java alsready only calls this method for visible rows, so no need to check that
        BulkUploadImageListWrapper imageListWrapper = (BulkUploadImageListWrapper)inValue;
        JPanel panel = new JPanel(new AbsoluteLayout());
        if (inRow%2 == 0)
            panel.setBackground(BulkUploadPanel.tableBackgroundColor1);
        else
            panel.setBackground(BulkUploadPanel.tableBackgroundColor2);
        final int imageBoxSize = 240;
        final int imagesPerRow = (int)((inTable.getColumnModel().getColumn(inColumn).getWidth()) / imageBoxSize);
        int posX = 0;
        int posY = -1;
        for (BulkUploadImageFileWrapper imageWrapper : imageListWrapper.getImageList()) {
            if (posX == 0)
                posY++;
            // Try to use the old panel if possible (I'm assuming it will be faster, but might use more memory and cause issues)
            ImageBox imageBox = imageWrapper.getImageBox();
            if (imageBox == null)
                imageBox = new ImageBox(imageWrapper, inTable);
            else
                imageBox.populateUI();
            imageBox.setBackground(panel.getBackground());
            panel.add(imageBox, new AbsoluteConstraints(imageBoxSize*posX++, imageBoxSize*posY, imageBoxSize, imageBoxSize));
            if (posX == imagesPerRow)
                posX = 0;
        }
        if (inTable.getRowHeight(inRow) != imageBoxSize*(posY+1)) {
            inTable.setRowHeight(inRow, imageBoxSize*(posY+1));
        }
        return panel;
    }

}
