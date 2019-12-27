package wildlog.ui.panels.bulkupload.helpers;

import java.awt.Dimension;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * This creates a JComboBox where the expanded droplist can be wider than the JComboBox itself.
 * Based on: https://stackoverflow.com/questions/956003/how-can-i-change-the-width-of-a-jcombobox-dropdown-list
 */
public class WideComboBox<E> extends JComboBox<E> {
    private boolean layingOut = false;

    public WideComboBox() {
    }

    public WideComboBox(final E items[]) {
        super(items);
    }

    public WideComboBox(Vector items) {
        super(items);
    }

    public WideComboBox(ComboBoxModel aModel) {
        super(aModel);
    }

    @Override
    public void doLayout() {
        try {
            layingOut = true;
            super.doLayout();
        }
        finally {
            layingOut = false;
        }
    }

    @Override
    public Dimension getSize() {
        Dimension dimension = super.getSize();
        if (!layingOut) {
            dimension.width = Math.max(dimension.width, getPreferredSize().width);
        }
        return dimension;
    }
}
