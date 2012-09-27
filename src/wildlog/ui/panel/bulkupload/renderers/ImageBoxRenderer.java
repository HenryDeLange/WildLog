package wildlog.ui.panel.bulkupload.renderers;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;
import wildlog.ui.panel.bulkupload.BulkUploadPanel;
import wildlog.ui.panel.bulkupload.ImageBox;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageFileWrapper;
import wildlog.ui.panel.bulkupload.helpers.BulkUploadImageListWrapper;


public class ImageBoxRenderer implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable inTable, Object inValue, boolean inIsSelected, boolean inHasFocus, int inRow, int inColumn) {
//        System.out.println("ImageBox Renderer " + inRow + "-" + inColumn);
        return drawImageBoxes(inValue, inTable, inRow, inColumn);
    }

    public static JPanel drawImageBoxes(Object inValue, JTable inTable, int inRow, int inColumn) {
//        BulkUploadImageListWrapper imageListWrapper = (BulkUploadImageListWrapper)inValue;
//        if (imageListWrapper.getImageList().isEmpty()) {
//            return null;
//        }
////        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
//        JPanel panel = new JPanel(new BoxLayout(inTable, inRow));
//        Color backgroundColor = new Color(235-(inRow%2*25), 246-(inRow%2*25), 220-(inRow%2*25));
//panel.setBackground(new Color(200, 100, 100));
//panel.setBorder(new EmptyBorder(0,0,0,0));
//        for (BulkUploadImageFileWrapper imageWrapper : imageListWrapper.getImageList()) {
//            ImageBox imageBox = new ImageBox(imageWrapper, inTable);
//            imageBox.setBackground(backgroundColor);
//            panel.add(imageBox);
//        }
//        final int imageBoxHeight = 250;
//        final int randomBuffer = 0; // FIXME: I don't know why I need to correct the space like this... Somewhere some buffering is being set...
//        int newHeight = imageBoxHeight * (int)Math.ceil((double)((double)imageListWrapper.getImageList().size()
//                / (int)((inTable.getColumnModel().getColumn(inColumn).getWidth() + randomBuffer) / imageBoxHeight)));
//        if (inTable.getRowHeight(inRow) != newHeight) {
//            System.out.println("Changing row height....................... " + inRow + " " + newHeight);
//            inTable.setRowHeight(inRow, newHeight);
//        }
//        return panel;
        BulkUploadImageListWrapper imageListWrapper = (BulkUploadImageListWrapper)inValue;
//        if (imageListWrapper.getImageList().isEmpty()) {
//            return null;
//        }
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
            ImageBox imageBox = new ImageBox(imageWrapper, inTable);
            imageBox.setBackground(panel.getBackground());
            panel.add(imageBox, new AbsoluteConstraints(imageBoxSize*posX++, imageBoxSize*posY, imageBoxSize, imageBoxSize));
            if (posX == imagesPerRow)
                posX = 0;
        }
        if (inTable.getRowHeight(inRow) != imageBoxSize*(posY+1)) {
//            System.out.println("Changing row height....................... " + inRow + " " + imageBoxSize*(posY+1));
            inTable.setRowHeight(inRow, imageBoxSize*(posY+1));
        }
        return panel;
    }

}
