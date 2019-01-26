package wildlog.ui.helpers;

import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.RootPaneContainer;
import javax.swing.filechooser.FileSystemView;
import wildlog.ui.dialogs.utils.UtilsDialog;

/**
 * The intention of this class is to work the same as the JFileChooser, but with some added features for WildLog. 
 * These dialogs will not paint the focus on their buttons and will show/hide the glasspane on their parent.
 */
public class WLFileChooser extends JFileChooser {

    public WLFileChooser() {
    }

    public WLFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
    }

    public WLFileChooser(File currentDirectory) {
        super(currentDirectory);
    }

    public WLFileChooser(FileSystemView fsv) {
        super(fsv);
    }

    public WLFileChooser(File currentDirectory, FileSystemView fsv) {
        super(currentDirectory, fsv);
    }

    public WLFileChooser(String currentDirectoryPath, FileSystemView fsv) {
        super(currentDirectoryPath, fsv);
    }
    
    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException {
        JDialog dialog = super.createDialog(parent);
        // BEGIN WildLog custom code
        processAllButtons(dialog);
        if (parent != null && parent instanceof RootPaneContainer) {
            UtilsDialog.addModalBackgroundPanel((RootPaneContainer) parent, dialog);
        }
        // END WildLog custom code
        return dialog;
    }
    
    /**
     * WildLog specific method to hide the focus on the buttons.
     */
    private static void processAllButtons(Container inContainer) {
        for (Component component : inContainer.getComponents()) {
            if (component instanceof Container) {
                processAllButtons((Container) component);
            }
            if (component instanceof JButton) {
                ((JButton) component).setFocusPainted(false);
            }
        }
    }
    
}
