package wildlog.ui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import org.apache.logging.log4j.Level;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import wildlog.WildLogApp;
import wildlog.ui.helpers.ScrollingMenu;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;


public final class UtilsUI {

    private UtilsUI() {
    }

    public static void doClipboardCopy(String inText) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection text = new StringSelection(inText);
        clipboard.setContents(text, text);
    }

    public static String doClipboardPaste() throws UnsupportedFlavorException, IOException, ClassNotFoundException {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        return (String)clipboard.getData(DataFlavor.stringFlavor);
    }

    public static void attachClipboardPopup(final JTextComponent inTextField) {
        attachClipboardPopup(inTextField, false);
    }

    public static void attachClipboardPopup(final JTextComponent inTextField, final boolean inCopyOnly) {
        inTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseClicked(e);
            }

            @Override
            public void mouseClicked(MouseEvent inEvent) {
                if ((inEvent.isPopupTrigger() || SwingUtilities.isRightMouseButton(inEvent))) {
                    JPopupMenu clipboardPopup = new JPopupMenu();
                    // Build the copy popup
                    JMenuItem copyItem = new JMenuItem("Copy Selected Text", new ImageIcon(WildLogApp.class.getResource("resources/icons/copy.png")));
                    copyItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            doClipboardCopy(inTextField.getSelectedText());
                        }
                    });
                    clipboardPopup.add(copyItem);
                    if (!inCopyOnly) {
                        // Build the paste popup
                        JMenuItem pasteItem = new JMenuItem("Paste Selected Text", new ImageIcon(WildLogApp.class.getResource("resources/icons/paste.png")));
                        pasteItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    inTextField.replaceSelection(doClipboardPaste());
                                }
                                catch (UnsupportedFlavorException | IOException | ClassNotFoundException ex) {
                                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                }
                            }
                        });
                        clipboardPopup.add(pasteItem);
                    }
                    // Wrap up and show up the popup
                    clipboardPopup.pack();
                    clipboardPopup.show(inEvent.getComponent(), inEvent.getPoint().x, inEvent.getPoint().y);
                    clipboardPopup.setVisible(true);
                }
            }
        });
    }

    public static void attachKeyListernerToFilterTableRows(final JTextComponent inTxtSearch, final JTable inTable, final int... inColumnsToSearch) {
        inTxtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent inEvent) {
                inTable.getSelectionModel().clearSelection();
                if (inEvent.getKeyChar() == KeyEvent.VK_ESCAPE) {
                   inTxtSearch.setText("");
                }
                TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)inTable.getRowSorter();
                if (sorter == null) {
                    sorter = new TableRowSorter<>(inTable.getModel());
                }
                // Note: The regexFilter method seems to be able to take optional parameters...
                // The (?i) makes the matching ignore case...
                if (inColumnsToSearch == null || inColumnsToSearch.length == 0) {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(inTxtSearch.getText()), new int[] {1}));
                }
                else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(inTxtSearch.getText()), inColumnsToSearch));
                }
                // Kan dit ook glo so doen:
                //sorter.setRowFilter(RowFilter.regexFilter(Pattern.compile(txtSearchField.getText(), Pattern.CASE_INSENSITIVE).toString()));
                inTable.setRowSorter(sorter);
                // As daar net een ry is, select hom.
                if (inTable.getRowCount() == 1) {
                    inTable.getSelectionModel().setSelectionInterval(0, 0);
                }
                else {
                    inTable.getSelectionModel().clearSelection();
                }
                int x;
                int y;
                if (inTable.getMousePosition() != null) {
                    x = inTable.getMousePosition().x;
                    y = inTable.getMousePosition().y;
                }
                else {
                    x = inTable.getX();
                    y = inTable.getY();
                }
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                        new GeneratedMouseEvent(inTable, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 1, false));
            }
        });
    }

    public static void attachKeyListernerToSelectKeyedRows(final JTable inTable) {
        inTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), "selectFirstRow");
        inTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), "selectLastRow");
        inTable.addKeyListener(new KeyAdapter() {
            private long lastEvent = 0;
            private String currentWord;

            @Override
            public void keyPressed(KeyEvent inEvent) {
                if ((inEvent.getKeyChar() >= 'A' && inEvent.getKeyChar() <= 'z')
                        || (inEvent.getKeyChar() >= '0' && inEvent.getKeyChar() <= '9')
                        || (inEvent.getKeyChar() == ' ')) {
                    if (inEvent.getWhen() - lastEvent < 1000) {
                        // Handle this as part of the same word
                        currentWord = currentWord + inEvent.getKeyChar();
                    }
                    else {
                        // Handle this as a new event
                        currentWord = ""+inEvent.getKeyChar();
                    }
                    // Select the row
                    for (int t = 0; t < inTable.getRowSorter().getViewRowCount(); t++) {
                        if (inTable.getModel().getValueAt(inTable.convertRowIndexToModel(t), 1).toString().toLowerCase().startsWith(currentWord.toLowerCase())) {
                            inTable.getSelectionModel().setSelectionInterval(t, t);
                            inTable.scrollRectToVisible(inTable.getCellRect(t, 0, true));
                            int x;
                            int y;
                            if (inTable.getMousePosition() != null) {
                                x = inTable.getMousePosition().x;
                                y = inTable.getMousePosition().y;
                            }
                            else {
                                x = inTable.getX();
                                y = inTable.getY();
                            }
                            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                                    new GeneratedMouseEvent(inTable, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 1, false));
                            break;
                        }
                    }
                    // Record the event time
                    lastEvent = inEvent.getWhen();
                }
                else {
                    if (inEvent.getKeyChar() == KeyEvent.VK_ESCAPE) {
                        lastEvent = 0;
                        currentWord = "";
                    }
                    else
                    if (inEvent.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                        if (currentWord.length() >= 1) {
                            lastEvent = inEvent.getWhen();
                            currentWord = currentWord.substring(0, currentWord.length()-1);
                        }
                    }
                }
            }
        });
    }

    public static void attachMouseScrollToTabs(final JTabbedPane inTabbedPane, final JPanel inHeaderPanel, final int inFixedIndex) {
        inHeaderPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent inEvent) {
                int scrollCount = inEvent.getWheelRotation();
                int currentIndex = inTabbedPane.getSelectedIndex();
                int maxIndex = inTabbedPane.getTabCount()-1;
                int newIndex = currentIndex /*-*/+ scrollCount;
                if (newIndex > maxIndex) {
                    newIndex = maxIndex;
                }
                else
                if (newIndex < 0) {
                    newIndex = 0;
                }
                inTabbedPane.setSelectedIndex(newIndex);
            }
        });
        inHeaderPanel.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                // For this to work on Mac/Linux and Windows both presses and released must be used
//                mouseReleased(e);
//            }
            @Override
            public void mouseReleased(MouseEvent inEvent) {
                if (SwingUtilities.isMiddleMouseButton(inEvent)) {
                    if (inEvent.getSource() instanceof PanelCanSetupHeader.HeaderPanel) {
                        ((PanelCanSetupHeader) ((PanelCanSetupHeader.HeaderPanel) inEvent.getSource()).getParentPanel()).closeTab();
                    }
                }
                else
                // Use popup and rightclick check to be sure to catch the event on both Mac/Linux and Windows
                if(inEvent.isPopupTrigger() || SwingUtilities.isRightMouseButton(inEvent)) {
                    ScrollingMenu scrMenu = new ScrollingMenu();
                    int tabCount = inTabbedPane.getTabCount();
                    for(int i = 0; i < tabCount; i++) {
                        Object temp = inTabbedPane.getTabComponentAt(i);
                        if (temp instanceof PanelCanSetupHeader.HeaderPanel) {
                            final PanelCanSetupHeader.HeaderPanel headerPanel = (PanelCanSetupHeader.HeaderPanel)temp;
                            scrMenu.add(new AbstractAction(headerPanel.getTitle(), headerPanel.getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    inTabbedPane.setSelectedComponent(headerPanel.getParentPanel());
                                }
                            });
                        }
                        else {
                            final int finalIndex = i;
                            scrMenu.add(new AbstractAction(inTabbedPane.getTitleAt(i), inTabbedPane.getIconAt(i)) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    inTabbedPane.setSelectedIndex(finalIndex);
                                }
                            });
                        }
                    }
                    if (inTabbedPane.getMousePosition() != null) {
                        scrMenu.show(inTabbedPane, inTabbedPane.getMousePosition().x, inTabbedPane.getMousePosition().y);
                    }
                }
                else {
                    // Left click = select tab
                    if (inFixedIndex < 0) {
                        inTabbedPane.setSelectedComponent(((PanelCanSetupHeader.HeaderPanel)inHeaderPanel).getParentPanel());
                    }
                    else {
                        inTabbedPane.setSelectedIndex(inFixedIndex);
                    }
                }
            }
        });
    }

    public static Timer doAnimationForFlashingBorder(final Color inFromColor, final Color inToColor, final JComponent inComponent) {
        final Timer timer = new Timer(100, null);
        timer.addActionListener(new ActionListener() {
            private int counter = 0;
            private boolean lastHalf = false;
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                if (!lastHalf) {
                    Color newColor = new Color(
                            inFromColor.getRed() - (inFromColor.getRed()-inToColor.getRed())/10*counter,
                            inFromColor.getGreen() - (inFromColor.getGreen()-inToColor.getGreen())/10*counter,
                            inFromColor.getBlue() - (inFromColor.getBlue()-inToColor.getBlue())/10*counter);
                    inComponent.setBorder(new LineBorder(newColor, 3));
                }
                else {
                    Color newColor = new Color(
                            inToColor.getRed() + (inFromColor.getRed()-inToColor.getRed())/10*counter,
                            inToColor.getGreen() + (inFromColor.getGreen()-inToColor.getGreen())/10*counter,
                            inToColor.getBlue() + (inFromColor.getBlue()-inToColor.getBlue())/10*counter);
                    inComponent.setBorder(new LineBorder(newColor, 3));
                }
                if (counter >= 10) {
                    counter = 0;
                    if (lastHalf) {
                        lastHalf = false;
                        timer.restart();
                    }
                    else {
                        lastHalf = true;
                    }
                }
            }
        });
        timer.start();
        return timer;
    }

    /**
     * This class is used to mark the mouse event that are generated to "fake" the click on the tables (to re-use code that loads the images, etc.)
     * @see MouseEvent
     */
    public static class GeneratedMouseEvent extends MouseEvent {

        public GeneratedMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger, int button) {
            super(source, id, when, modifiers, x, y, clickCount, popupTrigger, button);
        }

        public GeneratedMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger) {
            super(source, id, when, modifiers, x, y, clickCount, popupTrigger);
        }

        public GeneratedMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int xAbs, int yAbs, int clickCount, boolean popupTrigger, int button) {
            super(source, id, when, modifiers, x, y, xAbs, yAbs, clickCount, popupTrigger, button);
        }

    }
    
}
