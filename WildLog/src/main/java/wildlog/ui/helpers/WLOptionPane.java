package wildlog.ui.helpers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.CLOSED_OPTION;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.VALUE_PROPERTY;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.getRootFrame;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.ui.dialogs.utils.UtilsDialog;

/**
 * The intention of this class is to work the same as the JOptionPane, but with some added features for WildLog. 
 * These dialogs will not paint the focus on their buttons and will show/hide the glasspane on their parent.
 * 
 * UPDATE:
 * This class now only adds the gray background and Hand Cursor, the focus problem is now handled by the UIManager instead.
 * Keeping the class for possible future use and avoid refactoring the other code again.
 * 
 */
public class WLOptionPane extends JOptionPane {

    public WLOptionPane() {
    }

    public WLOptionPane(Object message) {
        super(message);
    }

    public WLOptionPane(Object message, int messageType) {
        super(message, messageType);
    }

    public WLOptionPane(Object message, int messageType, int optionType) {
        super(message, messageType, optionType);
    }

    public WLOptionPane(Object message, int messageType, int optionType, Icon icon) {
        super(message, messageType, optionType, icon);
    }

    public WLOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options) {
        super(message, messageType, optionType, icon, options);
    }

    public WLOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options, Object initialValue) {
        super(message, messageType, optionType, icon, options, initialValue);
    }
    
    
    // Copied entirely from JOptionPane.
    public static int showConfirmDialog(Component parentComponent, Object message) throws HeadlessException {
        return showConfirmDialog(parentComponent, message, UIManager.getString("OptionPane.titleText"), YES_NO_CANCEL_OPTION);
    }
    
    // Copied entirely from JOptionPane.
    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType) throws HeadlessException {
        return showConfirmDialog(parentComponent, message, title, optionType, QUESTION_MESSAGE);
    }
    
    // Copied entirely from JOptionPane.
    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType) throws HeadlessException {
        return showConfirmDialog(parentComponent, message, title, optionType, messageType, null);
    }
    
    // Copied entirely from JOptionPane.
    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon) throws HeadlessException {
        return showOptionDialog(parentComponent, message, title, optionType, messageType, icon, null, null);
    }
    
    // Copied entirely from JOptionPane and UIManager.
    public static void showMessageDialog(Component parentComponent, Object message) throws HeadlessException {
        Locale l = (parentComponent == null) ? Locale.getDefault() : parentComponent.getLocale();
        showMessageDialog(parentComponent, message, UIManager.getString("OptionPane.messageDialogTitle", l), INFORMATION_MESSAGE);
    }
    
    // Copied entirely from JOptionPane.
    public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType) throws HeadlessException {
        showMessageDialog(parentComponent, message, title, messageType, null);
    }
    
    // Copied entirely from JOptionPane.
    public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType, Icon icon) throws HeadlessException {
        showOptionDialog(parentComponent, message, title, DEFAULT_OPTION, messageType, icon, null, null);
    }
    
    /**
     * Copied from JOptionPane. 
     * <b>Added some WildLog specific code...</b>
     */
    public static int showOptionDialog(Component parentComponent, Object message, String title, int optionType, 
            int messageType, Icon icon, Object[] options, Object initialValue) throws HeadlessException {
        try {
            WLOptionPane pane = new WLOptionPane(message, messageType, optionType, icon, options, initialValue);
            pane.setInitialValue(initialValue);
            pane.setComponentOrientation(((parentComponent == null) ? getRootFrame() : parentComponent).getComponentOrientation());
            int style = styleFromMessageType(messageType);
            JDialog dialog = pane.createDialog(parentComponent, title, style);
            pane.selectInitialValue();
            // BEGIN WildLog custom code
            processAllButtons(dialog);
            if (parentComponent != null && parentComponent instanceof RootPaneContainer) {
                UtilsDialog.addModalBackgroundPanel((RootPaneContainer) parentComponent, dialog);
            }
            //dialog.show(); // JDK code used show(), but since it is depricated I'm using setVisible() instead...
            dialog.setVisible(true);
            // END WildLog custom code
            dialog.dispose();
            Object selectedValue = pane.getValue();
            if (selectedValue == null)
                return CLOSED_OPTION;
            if (options == null) {
                if(selectedValue instanceof Integer)
                    return ((Integer)selectedValue).intValue();
                return CLOSED_OPTION;
            }
            for (int counter = 0, maxCounter = options.length;
                counter < maxCounter; counter++) {
                if (options[counter].equals(selectedValue))
                    return counter;
            }
            return CLOSED_OPTION;
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "There was a problem showing the custom WLOptionPane dialog...");
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            return JOptionPane.showOptionDialog(parentComponent, message, title, optionType, messageType, icon, options, initialValue);
        }
    }
    
    // Copied entirely from JOptionPane.
    private static int styleFromMessageType(int messageType) {
        switch (messageType) {
        case ERROR_MESSAGE:
            return JRootPane.ERROR_DIALOG;
        case QUESTION_MESSAGE:
            return JRootPane.QUESTION_DIALOG;
        case WARNING_MESSAGE:
            return JRootPane.WARNING_DIALOG;
        case INFORMATION_MESSAGE:
            return JRootPane.INFORMATION_DIALOG;
        case PLAIN_MESSAGE:
        default:
            return JRootPane.PLAIN_DIALOG;
        }
    }
    
    // Copied (entirely) from JOptionPane.
    private JDialog createDialog(Component parentComponent, String title, int style) throws HeadlessException {
        final JDialog dialog;
        Window window = WLOptionPane.getWindowForComponent(parentComponent);
        if (window instanceof Frame) {
            dialog = new JDialog((Frame)window, title, true);
        } 
        else {
            dialog = new JDialog((Dialog)window, title, true);
        }
// TODO: Ek is nie seker wat om met die code te doen nie...
//        if (window instanceof SwingUtilities.SharedOwnerFrame) {
//            WindowListener ownerShutdownListener = SwingUtilities.getSharedOwnerFrameShutdownListener();
//            dialog.addWindowListener(ownerShutdownListener);
//        }
        initDialog(dialog, style, parentComponent);
        return dialog;
    }
    
    // Copied entirely from JOptionPane.
    static Window getWindowForComponent(Component parentComponent)
        throws HeadlessException {
        if (parentComponent == null)
            return getRootFrame();
        if (parentComponent instanceof Frame || parentComponent instanceof Dialog)
            return (Window)parentComponent;
        return WLOptionPane.getWindowForComponent(parentComponent.getParent());
    }
    
    // Copied entirely from JOptionPane.
    private void initDialog(final JDialog dialog, int style, Component parentComponent) {
        dialog.setComponentOrientation(this.getComponentOrientation());
        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);
        dialog.setResizable(false);
        if (JDialog.isDefaultLookAndFeelDecorated()) {
            boolean supportsWindowDecorations =
              UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations) {
                dialog.setUndecorated(true);
                getRootPane().setWindowDecorationStyle(style);
            }
        }
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        final PropertyChangeListener listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                // Let the defaultCloseOperation handle the closing
                // if the user closed the window without selecting a button
                // (newValue = null in that case).  Otherwise, close the dialog.
                if (dialog.isVisible() && event.getSource() == WLOptionPane.this && (event.getPropertyName().equals(VALUE_PROPERTY)) 
                        && event.getNewValue() != null && event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
                    dialog.setVisible(false);
                }
            }
        };
        WindowAdapter adapter = new WindowAdapter() {
            private boolean gotFocus = false;
            public void windowClosing(WindowEvent we) {
                setValue(null);
            }

            public void windowClosed(WindowEvent e) {
                removePropertyChangeListener(listener);
                dialog.getContentPane().removeAll();
            }

            public void windowGainedFocus(WindowEvent we) {
                // Once window gets focus, set initial focus
                if (!gotFocus) {
                    selectInitialValue();
                    gotFocus = true;
                }
            }
        };
        dialog.addWindowListener(adapter);
        dialog.addWindowFocusListener(adapter);
        dialog.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                // reset value to ensure closing works properly
                setValue(JOptionPane.UNINITIALIZED_VALUE);
            }
        });
        addPropertyChangeListener(listener);
    }
    
    // Copied entirely from JOptionPane.
    public static String showInputDialog(Object message) throws HeadlessException {
        return showInputDialog(null, message);
    }
    
    // Copied entirely from JOptionPane.
    public static String showInputDialog(Object message, Object initialSelectionValue) {
        return showInputDialog(null, message, initialSelectionValue);
    }
    
    // Copied entirely from JOptionPane and UIManager.
    public static String showInputDialog(Component parentComponent, Object message) throws HeadlessException {
        Locale l = (parentComponent == null) ? Locale.getDefault() : parentComponent.getLocale();
        return showInputDialog(parentComponent, message, UIManager.getString("OptionPane.inputDialogTitle", l), QUESTION_MESSAGE);
    }
    
    // Copied entirely from JOptionPane and UIManager.
    public static String showInputDialog(Component parentComponent, Object message, Object initialSelectionValue) {
        Locale l = (parentComponent == null) ? Locale.getDefault() : parentComponent.getLocale();
        return (String)showInputDialog(parentComponent, message, UIManager.getString("OptionPane.inputDialogTitle", l), QUESTION_MESSAGE, null, null, initialSelectionValue);
    }
    
    // Copied entirely from JOptionPane.
    public static String showInputDialog(Component parentComponent, Object message, String title, int messageType) throws HeadlessException {
        return (String)showInputDialog(parentComponent, message, title, messageType, null, null, null);
    }
    
    /**
     * Copied from JOptionPane. 
     * <b>Added some WildLog specific code...</b>
     */
    public static Object showInputDialog(Component parentComponent, Object message, String title, int messageType, 
            Icon icon, Object[] selectionValues, Object initialSelectionValue) throws HeadlessException {
        try {
            WLOptionPane pane = new WLOptionPane(message, messageType, OK_CANCEL_OPTION, icon, null, null);
            pane.setWantsInput(true);
            pane.setSelectionValues(selectionValues);
            pane.setInitialSelectionValue(initialSelectionValue);
            pane.setComponentOrientation(((parentComponent == null) ? getRootFrame() : parentComponent).getComponentOrientation());
            int style = styleFromMessageType(messageType);
            JDialog dialog = pane.createDialog(parentComponent, title, style);
            pane.selectInitialValue();
            // BEGIN WildLog custom code
            processAllButtons(dialog);
            if (parentComponent != null && parentComponent instanceof RootPaneContainer) {
                UtilsDialog.addModalBackgroundPanel((RootPaneContainer) parentComponent, dialog);
            }
            //dialog.show(); // JDK code used show(), but since it is depricated I'm using setVisible() instead...
            dialog.setVisible(true);
            // END WildLog custom code
            dialog.dispose();
            Object value = pane.getInputValue();
            if (value == UNINITIALIZED_VALUE) {
                return null;
            }
            return value;
        }
        catch (Exception ex) {
            WildLogApp.LOGGER.log(Level.ERROR, "There was a problem showing the custom WLOptionPane dialog...");
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
            return JOptionPane.showInputDialog(parentComponent, message, title, messageType, icon, selectionValues, initialSelectionValue);
        }
    }
    
    /**
     * WildLog specific method to add the Hand Cursor on the buttons.
     */
    private static void processAllButtons(Container inContainer) {
        for (Component component : inContainer.getComponents()) {
            if (component instanceof Container) {
                processAllButtons((Container) component);
            }
            if (component instanceof JButton) {
                ((JButton) component).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }
    }
    
}
