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
    private static final int imageBoxSizeWidth = 235;
    private static final int imageBoxSizeHeight = 240;
    
    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
        return drawImageBoxes(inValue, inTable, inRow, inColumn);
    }

    public static JPanel drawImageBoxes(Object inValue, JTable inTable, int inRow, int inColumn) {
        // Note: Java already only calls this method for visible rows, so no need to check that
        BulkUploadImageListWrapper imageListWrapper = (BulkUploadImageListWrapper)inValue;
        JPanel panel = new JPanel(new AbsoluteLayout());
        if (inRow % 2 == 0) {
            panel.setBackground(BulkUploadPanel.tableBackgroundColor1);
        } 
        else {
            panel.setBackground(BulkUploadPanel.tableBackgroundColor2);
        }
        final int imagesPerRow = (int)((inTable.getColumnModel().getColumn(inColumn).getWidth()) / imageBoxSizeWidth);
        int posX = 0;
        int posY = -1;
// FIXME: Die storie is miskien performacne heavy as daar baie images in een sighting is...
        for (BulkUploadImageFileWrapper imageWrapper : imageListWrapper.getImageList()) {
            if (posX == 0) {
                posY++;
            }
            ImageBox imageBox = new ImageBox(imageWrapper, inTable);
            imageBox.setRowBackground(panel.getBackground());
            panel.add(imageBox, new AbsoluteConstraints(imageBoxSizeWidth*posX++, imageBoxSizeHeight*posY, imageBoxSizeWidth, imageBoxSizeHeight));
            if (posX == imagesPerRow) {
                posX = 0;
            }
        }
        if (inTable.getRowHeight(inRow) != imageBoxSizeHeight*(posY+1)) {
            inTable.setRowHeight(inRow, imageBoxSizeHeight*(posY+1));
        }
        return panel;
    }

}
