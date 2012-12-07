package wildlog.ui.utils;

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
import java.io.IOException;
import java.util.Date;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;


public class UtilsUI {
    private static Timer timer = new Timer(30, null);

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
                    // Build the copy popup
                    JPopupMenu clipboardPopup = new JPopupMenu();
                    JMenuItem copyUserNameItem = new JMenuItem("Copy to clipoard.");
                    copyUserNameItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String text = inTextField.getSelectedText();
                            if (text == null || text.isEmpty())
                                text = inTextField.getText();
                            doClipboardCopy(text);
                        }
                    });
                    clipboardPopup.add(copyUserNameItem);
                    if (!inCopyOnly) {
                        // Build the paste popup
                        JMenuItem copyPasswordItem = new JMenuItem("Paste from clipboard.");
                        copyPasswordItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent inNestedEvent) {
                                try {
                                    inTextField.setText(doClipboardPaste());
                                }
                                catch (UnsupportedFlavorException | IOException | ClassNotFoundException ex) {
                                    ex.printStackTrace(System.err);
                                }
                            }
                        });
                        clipboardPopup.add(copyPasswordItem);
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

    // Voorbeeld van animations in swing
//    public static void doAnimationSaveSuccess(final Color inFromColor, final Color inToColor, final JTextComponent inFeedbackField) {
//        if (!timer.isRunning()) {
//            timer.addActionListener(new ActionListener() {
//                private int counter = 0;
//                private boolean lastHalf = false;
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    counter++;
//                    if (!lastHalf) {
//                        Color newColor = new Color(
//                                inFromColor.getRed() - (inFromColor.getRed()-inToColor.getRed())/10*counter,
//                                inFromColor.getGreen() - (inFromColor.getGreen()-inToColor.getGreen())/10*counter,
//                                inFromColor.getBlue() - (inFromColor.getBlue()-inToColor.getBlue())/10*counter);
//                        inFeedbackField.setBackground(newColor);
//                    }
//                    else {
//                        Color newColor = new Color(
//                                inToColor.getRed() + (inFromColor.getRed()-inToColor.getRed())/10*counter,
//                                inToColor.getGreen() + (inFromColor.getGreen()-inToColor.getGreen())/10*counter,
//                                inToColor.getBlue() + (inFromColor.getBlue()-inToColor.getBlue())/10*counter);
//                        inFeedbackField.setBackground(newColor);
//                    }
//                    if (counter >= 10) {
//                        counter = 0;
//                        if (lastHalf) {
//                            timer.stop();
//                        }
//                        else {
//                            lastHalf = true;
//                        }
//                    }
//                }
//            });
//            timer.start();
//        }
//    }

}
