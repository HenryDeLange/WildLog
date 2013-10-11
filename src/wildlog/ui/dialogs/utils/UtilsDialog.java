package wildlog.ui.dialogs.utils;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;


public final class UtilsDialog {

    private UtilsDialog() {
    }

    public static void addModalBackgroundPanel(RootPaneContainer inParentContainer, Window inPopupWindow) {
        // Note: The actual background colour, etc. is set once in the WildLogApp class.
        final JPanel glassPane = (JPanel)inParentContainer.getGlassPane();
        glassPane.setVisible(true);
        inPopupWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                glassPane.setVisible(false);
            }
        });
    }

    public static void addModalBackgroundPanel(JDialog inParentContainer, Window inPopupWindow) {
        // Setup the glassPane for modal popups
        final JPanel glassPane = (JPanel)inParentContainer.getGlassPane();
        glassPane.setLayout(new BorderLayout());
        JPanel background = new JPanel();
        background.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.25f));
        glassPane.removeAll();
        glassPane.add(background, BorderLayout.CENTER);
        // The inWindow will be null if we just want to set the background on a dialog's own GlassPane (for JOptionPane popups).
        if (inPopupWindow != null) {
            glassPane.setVisible(true);
            // Setup the hiding of the pane
            inPopupWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    glassPane.setVisible(false);
                }
            });
        }
    }

    public static void setDialogToCenter(Component inParentComponent, Component inComponentToCenter) {
        Point point = inParentComponent.getLocation();
        inComponentToCenter.setLocation(
                point.x + (inParentComponent.getWidth() - inComponentToCenter.getWidth())/2,
                point.y + (inParentComponent.getHeight() - inComponentToCenter.getHeight())/2);
    }

    public static ActionListener addEscapeKeyListener(final JDialog inDialog) {
        ActionListener escListiner = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        inDialog.dispose();
                    }
                };
        inDialog.getRootPane().registerKeyboardAction(
                escListiner,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        return escListiner;
    }

    public static void showExifPopup(final WildLogApp inApp, File inFile) {
        if (inFile != null) {
            if (inFile.exists()) {
                final JFrame frame = new JFrame("EXIF Meta Data: " + inFile.getPath());
                frame.setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/EXIF.png")).getImage());
                JTextPane txtPane = new JTextPane();
                txtPane.setContentType("text/html");
                txtPane.setEditable(false);
                String temp = "";
                try {
                    Metadata meta = JpegMetadataReader.readMetadata(inFile);
                    Iterator<Directory> directories = meta.getDirectories().iterator();
                    breakAllWhiles: while (directories.hasNext()) {
                        Directory directory = directories.next();
                        Collection<Tag> tags = directory.getTags();
                        for (Tag tag : tags) {
                            String name = tag.getTagName();
                            String description = tag.getDescription();
                            temp = temp + "<b>" + name + ":</b> " + description + "<br/>";
                        }
                    }
                    txtPane.setText(temp);
                    txtPane.setCaretPosition(0);
                    JScrollPane scroll = new JScrollPane(txtPane);
                    scroll.setPreferredSize(new Dimension(500, 750));
                    frame.getContentPane().add(scroll);
                    frame.pack();
                    frame.setLocationRelativeTo(inApp.getMainFrame());
                    ActionListener escListiner = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.dispose();
                        }
                    };
                    frame.getRootPane().registerKeyboardAction(
                            escListiner,
                            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                            JComponent.WHEN_IN_FOCUSED_WINDOW);
                    frame.setVisible(true);
                }
                catch (IOException ex) {
                    System.err.println("Error showing EXIF data for: " + inFile);
                    ex.printStackTrace(System.err);
                }
                catch (JpegProcessingException ex) {
                    System.err.println("Error showing EXIF data for: " + inFile);
                    ex.printStackTrace(System.err);
                    UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
                        @Override
                        public int showDialog() {
                            JOptionPane.showMessageDialog(inApp.getMainFrame(),
                                    "Could not process the file.",
                                    "Can't show image meta data!", JOptionPane.ERROR_MESSAGE);
                            return -1;
                        }
                    });
                }
            }
            else {
                System.err.println("Error showing EXIF data for: " + inFile);
                UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
                    @Override
                    public int showDialog() {
                        JOptionPane.showMessageDialog(inApp.getMainFrame(),
                                "Could not access the file.",
                                "Can't show image meta data!", JOptionPane.ERROR_MESSAGE);
                        return -1;
                    }
                });
            }
        }
        else {
            System.err.println("Error showing EXIF data for: " + inFile);
            UtilsDialog.showDialogBackgroundWrapper(inApp.getMainFrame(), new UtilsDialog.DialogWrapper() {
                @Override
                public int showDialog() {
                    JOptionPane.showMessageDialog(inApp.getMainFrame(),
                            "Could not access the file.",
                            "Can't show image meta data!", JOptionPane.ERROR_MESSAGE);
                    return -1;
                }
            });
        }
    }

    public static void showExifPopup(String inID, int inIndex, WildLogApp inApp) {
        List<WildLogFile> files = inApp.getDBI().list(new WildLogFile(inID));
        if (files.size() > 0) {
            showExifPopup(inApp, files.get(inIndex).getAbsolutePath().toFile());
        }
    }

    public interface DialogWrapper {
        public int showDialog();
    }

    public static int showDialogBackgroundWrapper(RootPaneContainer inParentContainer, DialogWrapper inDialogWrapper) {
        // TODO: Maybe one day replace this method with a propper custom message/dialog class that will work in a similar way to the JOptionPane, but is setup to use the Glasspane, etc.
        if (inParentContainer != null) {
            inParentContainer.getGlassPane().setVisible(true);
        }
        int result = inDialogWrapper.showDialog();
        if (inParentContainer != null) {
            inParentContainer.getGlassPane().setVisible(false);
        }
        return result;
    }

}
