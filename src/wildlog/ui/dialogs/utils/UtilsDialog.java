package wildlog.ui.dialogs.utils;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
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


public class UtilsDialog {

    public static void addModalBackgroundPanel(RootPaneContainer inParentContainer, Window inWindow) {
        // Note: The actual background colour, etc. is set once in the WildLogApp class.
        final JPanel glassPane = (JPanel)inParentContainer.getGlassPane();
        glassPane.setVisible(true);
        inWindow.addWindowListener(new WindowAdapter() {
            public void windowDeactivated(WindowEvent we) {
                super.windowDeactivated(we);
                glassPane.setVisible(false);
            }
        });
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

    public static void showExifPopup(File inFile) {
        if (inFile != null) {
            if (inFile.exists()) {
                JFrame frame = new JFrame("EXIF Meta Data: " + inFile.getPath());
                JTextPane txtPane = new JTextPane();
                txtPane.setContentType("text/html");
                txtPane.setEditable(false);
                String temp = "";
                try {
                    Metadata meta = JpegMetadataReader.readMetadata(inFile);
                    Iterator<Directory> directories = meta.getDirectories().iterator();
                    breakAllWhiles: while (directories.hasNext()) {
                        Directory directory = (Directory)directories.next();
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
                    //frame.setLocationRelativeTo(((WildLogApp)Application.getInstance()).getMainView().getComponent());
                    frame.pack();
                    frame.setVisible(true);
                }
                catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
                catch (JpegProcessingException ex) {
                    ex.printStackTrace(System.err);
                    JOptionPane.showMessageDialog(null, "Could not process the file.", "Trying to show image meta data", JOptionPane.ERROR_MESSAGE);
                }
            }
            else {
                JOptionPane.showMessageDialog(null, "Could not access the file.", "Trying to show image meta data", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Could not access the file.", "Trying to show image meta data", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void showExifPopup(String inID, int inIndex, WildLogApp inApp) {
        List<WildLogFile> fotos = inApp.getDBI().list(new WildLogFile(inID));
        if (fotos.size() > 0) {
            String fileName = fotos.get(inIndex).getOriginalFotoLocation(true);
            showExifPopup(new File(fileName));
        }
    }

}