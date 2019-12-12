package wildlog.ui.helpers;

import java.awt.Component;
import java.awt.Insets;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class ComboBoxFixer {

    private ComboBoxFixer() {
    }

    public static void configureComboBoxes(final JComboBox<?> inComboBox) {
        if (UIManager.getLookAndFeel().getName().equals("Windows")) {
            inComboBox.setRenderer(new NoFocusPaintedComboBoxRenderer());
        }
    }

    /**
     * Based on com.sun.java.swing.plaf.windows.WindowsComboBoxUI.WindowsComboBoxRenderer
     */
    private static class NoFocusPaintedComboBoxRenderer extends BasicComboBoxRenderer {

        public NoFocusPaintedComboBoxRenderer() {
            super();
            Insets insets = getBorder().getBorderInsets(this);
            setBorder(new EmptyBorder(0, 2, 0, insets.right));
        }

        @Override
        public Component getListCellRendererComponent(JList<?> inList, Object inValue, int inIndex, boolean inIsSelected, boolean inCellHasFocus) {
            Component renderedComponent = super.getListCellRendererComponent(inList, inValue, inIndex, inIsSelected, inCellHasFocus);
            if (renderedComponent instanceof JComponent) {
                JComponent component = (JComponent) renderedComponent;
                if (inIndex == -1) {
                    component.setOpaque(false);
                    component.setForeground(inList.getForeground());
                }
                else {
                    component.setOpaque(true);
                }
            }
            return renderedComponent;
        }
    }

}
