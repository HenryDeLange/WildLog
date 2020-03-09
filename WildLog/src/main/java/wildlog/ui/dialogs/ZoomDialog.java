package wildlog.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.enums.system.WildLogThumbnailSizes;
import wildlog.mediaplayer.VideoController;
import wildlog.mediaplayer.VideoPanel;
import wildlog.mediaplayer.VideoPlayer;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.utils.UtilsFileProcessing;
import wildlog.utils.UtilsImageProcessing;
import wildlog.utils.WildLogFileExtentions;


public class ZoomDialog extends JDialog {
    private final List<Path> filesToView;
    private int fileIndex;
    private VideoPanel videoPanel = null;

    
    public ZoomDialog(JFrame inParent, List<Path> inFilesToView, int inStartIndex) {
        super(inParent, true);
        WildLogApp.LOGGER.log(Level.INFO, "[ZoomDialog]");
        filesToView = inFilesToView;
        fileIndex = inStartIndex;
        initComponents();
        // Setup modal background
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        // Add escape listener
        UtilsDialog.addEscapeKeyListener(this);
        // Setup listeners for navigating with arrow keys
        getRootPane().registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnPrevActionPerformed(null);
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        btnNextActionPerformed(null);
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        // Setup the first image
        if (filesToView == null || filesToView.isEmpty() || fileIndex >= filesToView.size() || fileIndex < 0) {
            // No files to display
            setTitle("Zoom Popup - No Files");
            lblZoomedFile.setIcon(UtilsImageProcessing.getScaledIconForNoFiles(WildLogThumbnailSizes.S0300_NORMAL));
            pack();
            UtilsDialog.setDialogToCenter(getParent(), this);
        }
        else {
            setupFile(inFilesToView.get(fileIndex));
        }
        // Set size
        lblZoomedFile.setMinimumSize(new Dimension(WildLogThumbnailSizes.S0700_VERY_LARGE.getSize(), WildLogThumbnailSizes.S0700_VERY_LARGE.getSize()));
        lblZoomedFile.setPreferredSize(new Dimension(WildLogThumbnailSizes.S0700_VERY_LARGE.getSize(), WildLogThumbnailSizes.S0700_VERY_LARGE.getSize()));
        lblZoomedFile.setMaximumSize(new Dimension(WildLogThumbnailSizes.S0700_VERY_LARGE.getSize(), WildLogThumbnailSizes.S0700_VERY_LARGE.getSize()));
        pack();
        UtilsDialog.setDialogToCenter(getParent(), this);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        lblZoomedFile = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        btnPrev.setBackground(new java.awt.Color(0, 0, 0));
        btnPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Previous.gif"))); // NOI18N
        btnPrev.setToolTipText("");
        btnPrev.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });
        getContentPane().add(btnPrev, java.awt.BorderLayout.LINE_START);

        btnNext.setBackground(new java.awt.Color(0, 0, 0));
        btnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/Next.gif"))); // NOI18N
        btnNext.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        getContentPane().add(btnNext, java.awt.BorderLayout.LINE_END);

        lblZoomedFile.setBackground(new java.awt.Color(0, 0, 0));
        lblZoomedFile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblZoomedFile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblZoomedFile.setOpaque(true);
        lblZoomedFile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblZoomedFileMouseReleased(evt);
            }
        });
        getContentPane().add(lblZoomedFile, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        if (fileIndex > 0) {
            fileIndex = fileIndex - 1;
        }
        else {
            if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                Toolkit.getDefaultToolkit().beep();
            }
            fileIndex = filesToView.size() - 1;
        }
        if (filesToView.size() > 1) {
            // If a video was busy playing then stop it
            if (videoPanel != null) {
                videoPanel.getController().setStatus(VideoController.VideoStatus.STOPPED);
            }
            // Load the previous file
            setupFile(filesToView.get(fileIndex));
        }
    }//GEN-LAST:event_btnPrevActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        if (fileIndex < filesToView.size() - 1) {
            fileIndex = fileIndex + 1;
        }
        else {
            if (WildLogApp.getApplication().getWildLogOptions().isEnableSounds()) {
                Toolkit.getDefaultToolkit().beep();
            }
            fileIndex = 0;
        }
        if (filesToView.size() > 1) {
            // If a video was busy playing then stop it
            if (videoPanel != null) {
                videoPanel.getController().setStatus(VideoController.VideoStatus.STOPPED);
            }
            // Load the next file
            setupFile(filesToView.get(fileIndex));
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void lblZoomedFileMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblZoomedFileMouseReleased
        UtilsFileProcessing.openFile(filesToView.get(fileIndex));
    }//GEN-LAST:event_lblZoomedFileMouseReleased

    private void setupFile(Path inPath) {
        setTitle("Zoom Popup - " + inPath.getFileName().toString());
        // Setup the file based on the different file types
        if (WildLogFileExtentions.Images.isKnownExtention(inPath)) {
            ImageIcon imageIcon = UtilsImageProcessing.getScaledIcon(inPath, WildLogThumbnailSizes.S0700_VERY_LARGE.getSize(), true);
            lblZoomedFile.setIcon(imageIcon);
            // Replace the video panel with the image label
            if (videoPanel != null) {
                remove(videoPanel);
                videoPanel = null;
                add(lblZoomedFile, BorderLayout.CENTER);
                invalidate();
                repaint();
            }
        }
        else
        if (WildLogFileExtentions.Movies.isKnownExtention(inPath)) {
            //lblZoomedFile.setIcon(UtilsImageProcessing.getScaledIconForMovies(WildLogThumbnailSizes.S0300_NORMAL));
            // Stop the old video
            if (videoPanel != null) {
                videoPanel.getController().setStatus(VideoController.VideoStatus.STOPPED);
                remove(videoPanel);
                videoPanel = null;
            }
            // Replace the image label with the new video panel
            videoPanel = new VideoPanel(new VideoController(), WildLogThumbnailSizes.S0700_VERY_LARGE.getSize(), WildLogThumbnailSizes.S0700_VERY_LARGE.getSize());
            remove(lblZoomedFile);
            add(videoPanel, BorderLayout.CENTER);
            invalidate();
            repaint();
            // Play the video
            // Note: Die video decoding moet op sy eie thread gebeur
// TODO: Better thread handeling? (not a fan of making floating threads)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    VideoPlayer.playVideo(videoPanel, inPath, WildLogThumbnailSizes.S0700_VERY_LARGE.getSize());
                }
            }).start();
        }
        else {
            lblZoomedFile.setIcon(UtilsImageProcessing.getScaledIconForOtherFiles(WildLogThumbnailSizes.S0300_NORMAL));
            // Replace the video panel with the image label
            if (videoPanel != null) {
                remove(videoPanel);
                videoPanel = null;
                add(lblZoomedFile, BorderLayout.CENTER);
                invalidate();
                repaint();
            }
        }
        // Don't set the tooltip. If it is set then sometimes the tooltip uses the initial ESC press.
//        lblZoomedFile.setToolTipText(inPath.getFileName().toString());
        // Recenter
        UtilsDialog.setDialogToCenter(getParent(), this);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JLabel lblZoomedFile;
    // End of variables declaration//GEN-END:variables
}
