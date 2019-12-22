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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.system.WildLogFileType;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.ui.utils.UtilsUI;


public final class UtilsDialog {

    private UtilsDialog() {
    }
    
    public static void setupGlassPaneOnMainFrame(JFrame inFrame) {
        // Setup the glassPane for modal popups
        JPanel glassPane = (JPanel) inFrame.getGlassPane();
        glassPane.setLayout(new BorderLayout());
        JPanel background = new JPanel();
        background.setBackground(new Color(0.22f, 0.26f, 0.20f, 0.25f));
        glassPane.add(background, BorderLayout.CENTER);
        glassPane.addMouseListener(new MouseAdapter() {});
        glassPane.addKeyListener(new KeyAdapter() {});
    }

    public static void addModalBackgroundPanel(RootPaneContainer inParentContainer, Window inPopupWindow) {
        // Note: The actual background colour, etc. is set once in the WildLogApp class.
        final JPanel glassPane = (JPanel) inParentContainer.getGlassPane();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Maak seker die gebeur na enige setVisible(false) wat dalk vanaf 'n onlangse vorige popup kan plaasvind
                glassPane.setVisible(true);
            }
        });
        inPopupWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // The JavaFX stuff sometimes doesn't shutdown correctly, so I'm putting this in a try-catch to make sure the glasspane gets disabled afterwards
                try {
                    super.windowClosed(e);
                }
                catch (Exception ex) {
                    WildLogApp.LOGGER.log(Level.WARN, "Swing-JavaFX integration error:");
                    WildLogApp.LOGGER.log(Level.WARN, ex.toString(), ex);
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        glassPane.setVisible(false);
                    }
                });
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

    public static <T extends Window & RootPaneContainer> ActionListener addEscapeKeyListener(final T inPopup) {
        ActionListener escListiner = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inPopup.setVisible(false);
                inPopup.dispose();
            }
        };
        inPopup.getRootPane().registerKeyboardAction(
                escListiner, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        return escListiner;
    }

    public static void showExifPopup(final WildLogApp inApp, Path inFile) {
        if (inFile != null) {
            if (Files.exists(inFile)) {
                final JFrame frame = new JFrame("EXIF Meta Data: " + inFile);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setIconImage(new ImageIcon(WildLogApp.class.getResource("resources/icons/EXIF.png")).getImage());
                JTextPane txtPane = new JTextPane();
                txtPane.setContentType("text/html");
                txtPane.setEditable(false);
                String temp = "<html>";
                // Note: Gebruik <p> en <div> eerder as <br /> want die <br /> copy nie reg na die clipboard toe nie...
                try {
                    Metadata meta = JpegMetadataReader.readMetadata(inFile.toFile());
                    Iterator<Directory> directories = meta.getDirectories().iterator();
                    while (directories.hasNext()) {
                        Directory directory = directories.next();
                        temp = temp + "<p><u><b>" + directory.getName() + "</b></u></p>";
                        Collection<Tag> tags = directory.getTags();
                        for (Tag tag : tags) {
                            String name = tag.getTagName();
                            String description = tag.getDescription();
                            temp = temp + "<div><b>" + name + ":</b> " + description + "</div>";
                        }
                        temp = temp + "<div></div>";
                    }
                    txtPane.setText(temp + "</html>");
                    txtPane.setCaretPosition(0);
                    UtilsUI.attachClipboardPopup(txtPane, true, false);
                    JScrollPane scroll = new JScrollPane(txtPane);
                    scroll.setPreferredSize(new Dimension(580, 750));
                    frame.getContentPane().add(scroll);
                    frame.pack();
                    frame.setLocationRelativeTo(inApp.getMainFrame());
                    ActionListener escListiner = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.setVisible(false);
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
                    WildLogApp.LOGGER.log(Level.ERROR, "Error showing EXIF data for: {}", inFile);
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                }
                catch (JpegProcessingException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, "Error showing EXIF data for: {}", inFile);
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    WLOptionPane.showMessageDialog(inApp.getMainFrame(),
                            "Could not process the file.", "Can't show image meta data!", JOptionPane.ERROR_MESSAGE);
                }
            }
            else {
                WildLogApp.LOGGER.log(Level.ERROR, "Error showing EXIF data for: {}", inFile);
                WLOptionPane.showMessageDialog(inApp.getMainFrame(),
                        "Could not access the file.", "Can't show image meta data!", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            WildLogApp.LOGGER.log(Level.ERROR, "Error showing EXIF data for: {}", inFile);
            WLOptionPane.showMessageDialog(inApp.getMainFrame(),
                    "Could not access the file.", "Can't show image meta data!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void showExifPopup(long inID, int inIndex, final WildLogApp inApp) {
        List<WildLogFile> files = inApp.getDBI().listWildLogFiles(inID, null, WildLogFile.class);
        if (files.size() > 0) {
            if (WildLogFileType.IMAGE.equals(files.get(inIndex).getFileType())) {
                showExifPopup(inApp, files.get(inIndex).getAbsolutePath());
            }
            else {
                WLOptionPane.showMessageDialog(inApp.getMainFrame(),
                        "Only image metadata can be shown.", "Not An Image", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

}
