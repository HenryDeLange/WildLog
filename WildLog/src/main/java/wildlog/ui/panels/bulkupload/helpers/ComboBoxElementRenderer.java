package wildlog.ui.panels.bulkupload.helpers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.Element;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.utils.UtilsImageProcessing;


public class ComboBoxElementRenderer<T extends Element> implements ListCellRenderer<T> {
    private static final JLabel lblEmpty = new JLabel("Suggested", JLabel.CENTER);
    
    @Override
    public Component getListCellRendererComponent(JList<? extends T> inList, T inValue, int inIndex, boolean inIsSelected, boolean inCellHasFocus) {
        System.out.println("inCellHasFocus = " + inCellHasFocus);
        if (inIndex >= 0 && inValue != null) {
// FIXME: Hierdie word heeltemal te veel geroep om elke keer 'n DB lookup en icon generaion te doen. Cache dit op 'n manier...
            System.out.println("ComboBoxElementRenderer called for " + inValue);
            WildLogFile wildLogFile = WildLogApp.getApplication().getDBI().findWildLogFile(0, inValue.getWildLogFileID(), null, null, WildLogFile.class);
            ImageIcon icon;
            if (wildLogFile != null) {
                icon = new ImageIcon(wildLogFile.getAbsoluteThumbnailPath(WildLogThumbnailSizes.S0030_TINY).toString());
            }
            else {
                icon = UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0030_TINY);
            }
            JPanel pnlWrapper = new JPanel(new BorderLayout(5, 0));
            pnlWrapper.setPreferredSize(new Dimension(300, 32));
            JLabel lblElementIcon = new JLabel(icon);
            lblElementIcon.setBackground(Color.BLACK);
            lblElementIcon.setOpaque(true);
            lblElementIcon.setPreferredSize(new Dimension(WildLogThumbnailSizes.S0030_TINY.getSize(), WildLogThumbnailSizes.S0030_TINY.getSize()));
            pnlWrapper.add(lblElementIcon, BorderLayout.WEST);
            JLabel lblElementName = new JLabel(inValue.getPrimaryName());
            pnlWrapper.add(lblElementName, BorderLayout.CENTER);
            pnlWrapper.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 5));
            pnlWrapper.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (inIsSelected) {
                pnlWrapper.setBackground(new Color(128, 148, 118));
            }
            else {
                pnlWrapper.setBackground(Color.WHITE);
            }
            return pnlWrapper;
        }
        return lblEmpty;
    }
    
}
