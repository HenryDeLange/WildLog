package wildlog.ui.utils;

import java.awt.Color;
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
import java.util.Date;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import wildlog.WildLogApp;
import wildlog.ui.panels.interfaces.PanelCanSetupHeader;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogThumbnailSizes;


public final class UtilsUI {
    public final static String TABLE_KEY_FILTER_CALLBACK_NAME = "DO_WILDLOG_UPDATE";

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
                    JMenuItem copyItem = new JMenuItem("Copy Selected Text",
                            UtilsImageProcessing.getScaledIcon(WildLogApp.class.getResource("resources/icons/copy.png"), WildLogThumbnailSizes.TINY.getSize()));
                    copyItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            doClipboardCopy(inTextField.getSelectedText());
                        }
                    });
                    clipboardPopup.add(copyItem);
                    if (!inCopyOnly) {
                        // Build the paste popup
                        JMenuItem pasteItem = new JMenuItem("Paste Selected Text",
                                UtilsImageProcessing.getScaledIcon(WildLogApp.class.getResource("resources/icons/paste.png"), WildLogThumbnailSizes.TINY.getSize()));
                        pasteItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    inTextField.replaceSelection(doClipboardPaste());
                                }
                                catch (UnsupportedFlavorException | IOException | ClassNotFoundException ex) {
                                    ex.printStackTrace(System.err);
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

    public static void attachKeyListernerToFilterTableRows(final JTextComponent inTxtSearch, final JTable inTable) {
        inTxtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent inEvent) {
                inTable.getSelectionModel().clearSelection();
                inTable.firePropertyChange(TABLE_KEY_FILTER_CALLBACK_NAME, 0, 1);
                if (inEvent.getKeyChar() == KeyEvent.VK_ESCAPE) {
                   inTxtSearch.setText("");
                }
                TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)inTable.getRowSorter();
                if (sorter == null) {
                    sorter = new TableRowSorter<>(inTable.getModel());
                }
                // Note: The regexFilter method seems to be able to take optional parameters...
                // The (?i) makes the matching ignore case...
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + inTxtSearch.getText()));
                // Kan dit ook glo so doen:
                //sorter.setRowFilter(RowFilter.regexFilter(Pattern.compile(txtSearchField.getText(), Pattern.CASE_INSENSITIVE).toString()));
                inTable.setRowSorter(sorter);
            }
        });
    }

    public static void attachKeyListernerToSelectKeyedRows(final JTable inTable) {
        inTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent inEvent) {
                if ((inEvent.getKeyChar() >= 'A' && inEvent.getKeyChar() <= 'z') || (inEvent.getKeyChar() >= '0' && inEvent.getKeyChar() <= '9')) {
                    int select = -1;
                    for (int t = 0; t < inTable.getRowSorter().getViewRowCount(); t++) {
                        if (inTable.getValueAt(t, 0).toString().toLowerCase().startsWith((""+inEvent.getKeyChar()).toLowerCase())) {
                            select = t;
                            // A new letter was selected by the user, so go to the first line.
                            if (inTable.getValueAt(inTable.getSelectedRow(), 0).toString().toLowerCase().charAt(0) != inEvent.getKeyChar()) {
                                break;
                            }
                            else {
                                // The same letter was pressed as the selected row, thus go to the next line (if it exists and matches).
                                if (t > inTable.getSelectedRow()) {
                                    break;
                                }
                            }
                        }
                    }
                    if (select >= 0) {
                        inTable.getSelectionModel().setSelectionInterval(select, select);
                        inTable.scrollRectToVisible(inTable.getCellRect(select, 0, true));
                        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
                                new MouseEvent(
                                        inTable,
                                        MouseEvent.MOUSE_RELEASED,
                                        new Date().getTime(),
                                        0,
                                        inTable.getMousePosition().x,
                                        inTable.getMousePosition().y,
                                        1,
                                        false));
                    }
                }
            }
        });
    }

    public static void attachMouseScrollToTabs(final JTabbedPane inTabbedPane, final JPanel inHeaderPanel, final int inFixedIndex) {
        class TabbedPaneMouseWheelScroller implements MouseWheelListener {
            @Override
            public void mouseWheelMoved(MouseWheelEvent inEvent) {
                int scrollCount = inEvent.getWheelRotation();
                int currentIndex = inTabbedPane.getSelectedIndex();
                int maxIndex = inTabbedPane.getTabCount()-1;
                int newIndex = currentIndex - scrollCount;
                if (newIndex > maxIndex) {
                    newIndex = maxIndex;
                }
                else
                if (newIndex < 0) {
                    newIndex = 0;
                }
                inTabbedPane.setSelectedIndex(newIndex);
            }
        }
        class TabSelectionMouseHandler extends MouseAdapter {
            @Override
            public void mouseClicked(MouseEvent inEvent) {
                if(SwingUtilities.isRightMouseButton(inEvent)) {
                    // Right clicked = show popup of all open tabs
                    JPopupMenu menu = new JPopupMenu();
                    int tabCount = inTabbedPane.getTabCount();
                    for(int i = 0; i < tabCount; i++) {
                        Object temp = inTabbedPane.getTabComponentAt(i);
                        if (temp instanceof PanelCanSetupHeader.HeaderPanel) {
                            final PanelCanSetupHeader.HeaderPanel headerPanel = (PanelCanSetupHeader.HeaderPanel)temp;
                            menu.add(new AbstractAction(headerPanel.getTitle(), headerPanel.getIcon()) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    inTabbedPane.setSelectedComponent(headerPanel.getParentPanel());
                                }
                            });
                        }
                        else {
                            final int finalIndex = i;
                            menu.add(new AbstractAction(inTabbedPane.getTitleAt(i), inTabbedPane.getIconAt(i)) {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    inTabbedPane.setSelectedIndex(finalIndex);
                                }
                            });
                        }
                    }
                    menu.show(inTabbedPane, inTabbedPane.getMousePosition().x, inTabbedPane.getMousePosition().y);
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
        }
        inHeaderPanel.addMouseWheelListener(new TabbedPaneMouseWheelScroller());
        inHeaderPanel.addMouseListener(new TabSelectionMouseHandler());
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
     * Vergelyk die twee Objects en kyk of hulle waarder eenders is. <br/>
     * <b>WARNING: Gebruik die toString() (case-sensitive) vir meeste classes.</b>
     * @param inObject1
     * @param inObject2
     * @return
     */
    public static boolean isTheSame(Object inObject1, Object inObject2) {
        if (inObject1 == null && inObject2 == null) {
            return true;
        }
        if ((inObject1 == null && inObject2 != null) || (inObject1 != null && inObject2 == null)) {
            return false;
        }
        if (inObject1 != null && inObject2 != null) {
            if (inObject1.toString() == null && inObject2.toString() == null) {
                return true;
            }
            if ((inObject1.toString() == null && inObject2.toString() != null) || (inObject1.toString() != null && inObject2.toString() == null)) {
                return false;
            }
            if (inObject1.toString().equals(inObject2.toString())) {
                return true;
            }
        }
        return false;
    }

}
