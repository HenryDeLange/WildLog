package wildlog.ui.dialogs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import mediautil.gen.Log;
import mediautil.gen.directio.IterativeReader;
import mediautil.gen.directio.IterativeWriter;
import mediautil.image.jpeg.LLJTran;
import mediautil.image.jpeg.LLJTranException;
import org.apache.logging.log4j.Level;
import wildlog.WildLogApp;
import wildlog.data.dataobjects.WildLogFile;
import wildlog.data.enums.WildLogThumbnailSizes;
import wildlog.ui.dialogs.utils.UtilsDialog;
import wildlog.ui.helpers.WLOptionPane;
import wildlog.utils.UtilsImageProcessing;


public class CropDialog extends JDialog {
    private final WildLogFile wildLogFile;
    private Path iNaturalistUploadFile = null;

    
    public CropDialog(JFrame inParent, WildLogFile inWildLogFile) {
        super(inParent, true);
        WildLogApp.LOGGER.log(Level.INFO, "[CropDialog]");
        wildLogFile = inWildLogFile;
        initComponents();
        // Setup modal background
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        doSetup();
    }
    
    public CropDialog(JDialog inParent, WildLogFile inWildLogFile) {
        super(inParent, true);
        WildLogApp.LOGGER.log(Level.INFO, "[CropDialog]");
        wildLogFile = inWildLogFile;
        initComponents();
        // Setup modal background
        UtilsDialog.addModalBackgroundPanel(inParent, this);
        doSetup();
    }
    
    private void doSetup() {
        UtilsDialog.addModalBackgroundPanel(this, null);
        // Add escape listener
        UtilsDialog.addEscapeKeyListener(this);
        // Setup the image
        setTitle("Crop Popup - " + wildLogFile.getFilename());
        ImageIcon imageIcon = UtilsImageProcessing.getScaledIcon(wildLogFile.getAbsolutePath(), WildLogThumbnailSizes.VERY_LARGE.getSize(), true);
        ((CroppingPanel) pnlImage).setImage(imageIcon.getImage(), imageIcon.getIconWidth(), imageIcon.getIconHeight());
        pnlImage.setMinimumSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
        pnlImage.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
        pnlImage.setMaximumSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
        // Pack the dailog on the screen
        pack();
        UtilsDialog.setDialogToCenter(getParent(), this);
    }
    
    
    private final class CroppingPanel extends JPanel {
        private final Color FILL_COLOUR = new Color(0.25f, 0.25f, 0.25f, 0.5f);
        private BufferedImage backgroundImg;
        private int beginX;
        private int beginY;
        private int dragX;
        private int dragY;
        private int endX;
        private int endY;
        private int imageWidth;
        private int imageHeight;
        private boolean dragging = false;
        private boolean cropped = false;

        CroppingPanel() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    dragging = false;
                    cropped = false;
                    beginX = e.getX();
                    beginY = e.getY();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                        dragging = false;
                        cropped = false;
                        beginX = 0;
                        beginY = 0;
                        endX = 0;
                        endY = 0;
                    }
                    else {
                        dragging = false;
                        cropped = true;
                        endX = e.getX();
                        endY = e.getY();
                    }
                    repaint();
                }
            });
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    dragging = true;
                    cropped = false;
                    dragX = e.getX();
                    dragY = e.getY();
                    repaint();
                }
            });
        }
        
        public void setImage(Image inImage, int inWidth, int inHeight) {
            imageWidth = inWidth;
            imageHeight = inHeight;
            backgroundImg = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = backgroundImg.getGraphics();
            g.drawImage(inImage, 0, 0, this);
            g.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImg != null) {
                g.drawImage(backgroundImg, 0, 0, this);
            }
            // Draw crop box
            if (dragging) {
                g.setColor(Color.RED);
                g.drawRect(Math.min(beginX, dragX), Math.min(beginY, dragY), 
                        Math.abs(beginX - dragX), Math.abs(beginY - dragY));
            }
            else 
            if (cropped) {
                g.setColor(Color.GREEN);
                int firstX = Math.min(beginX, endX);
                int firstY = Math.min(beginY, endY);
                int lastX = Math.max(beginX, endX);
                int lastY = Math.max(beginY, endY);
                int boxWidth = Math.abs(beginX - endX);
                int boxHeight = Math.abs(beginY - endY);
                g.drawRect(firstX, firstY, boxWidth, boxHeight);
                g.setColor(Color.WHITE);
                g.drawRect(firstX - 1, firstY - 1, boxWidth + 2, boxHeight + 2);
                g.setColor(FILL_COLOUR);
                g.fillRect(0, 0, firstX, imageHeight);
                g.fillRect(lastX, 0, imageWidth - lastX, imageHeight);
                g.fillRect(firstX, 0, boxWidth, firstY);
                g.fillRect(firstX, lastY, boxWidth, imageHeight - lastY);
            }
            g.dispose();
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public int getBeginX() {
            return beginX;
        }

        public int getBeginY() {
            return beginY;
        }

        public int getEndX() {
            return endX;
        }

        public int getEndY() {
            return endY;
        }
        
    }

    public void setINaturalistUploadFile(Path inINaturalistUploadFile) {
        iNaturalistUploadFile = inINaturalistUploadFile;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlImage = new CroppingPanel();
        btnCrop = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        pnlImage.setBackground(new java.awt.Color(0, 0, 0));
        pnlImage.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        getContentPane().add(pnlImage, java.awt.BorderLayout.CENTER);

        btnCrop.setBackground(new java.awt.Color(0, 0, 0));
        btnCrop.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnCrop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/wildlog/resources/icons/OK.png"))); // NOI18N
        btnCrop.setToolTipText("Save the cropped image as one of the linked images in the Worskspace.");
        btnCrop.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCrop.setFocusPainted(false);
        btnCrop.setMargin(new java.awt.Insets(5, 5, 5, 5));
        btnCrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCropActionPerformed(evt);
            }
        });
        getContentPane().add(btnCrop, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCropActionPerformed
        getGlassPane().setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // Stuur die file na iNaturalist
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Raise the Debug Level which is normally LEVEL_INFO. Only Error messages will be printed by MediaUtil.
                    Log.debugLevel = Log.LEVEL_ERROR;
                    // Initialize LLJTran and Read the entire Image including Appx markers
                    LLJTran lljTran = new LLJTran(wildLogFile.getAbsolutePath().toFile());
                    // If you pass the 2nd parameter as false, Exif information is not loaded and hence will not be written.
                    lljTran.read(LLJTran.READ_ALL, true);
                    lljTran.transform(LLJTran.CROP);
                    // Get a name for the new cropped file
                    WildLogFile croppedWildLogFile = new WildLogFile(wildLogFile.getId(), wildLogFile.getFilename(), 
                            wildLogFile.getDBFilePath(), wildLogFile.getFileType(), new Date(), null, -1);
                    if (iNaturalistUploadFile == null) {
                        while (Files.exists(croppedWildLogFile.getAbsolutePath())) {
                            String newFilename = croppedWildLogFile.getFilename();
                            newFilename = newFilename.substring(0, newFilename.lastIndexOf('.')) + "_c.jpg";
                            croppedWildLogFile.setDBFilePath(croppedWildLogFile.getRelativePath().getParent().resolve(newFilename).toString());
                            croppedWildLogFile.setFilename(newFilename);
                        }
                    }
                    else {
                        croppedWildLogFile.setDBFilePath(iNaturalistUploadFile.toAbsolutePath().toString());
                    }
                    // Save the Image which is already transformed as specified by the input transformation earlier, along with the Exif header.
                    try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(croppedWildLogFile.getAbsolutePath().toFile()))) {
                        CroppingPanel panel = ((CroppingPanel) pnlImage);
                        double ratio = ((double) lljTran.getWidth()) / ((double) panel.getImageWidth());
                        int firstX = Math.min(panel.getBeginX(), panel.getEndX());
                        int firstY = Math.min(panel.getBeginY(), panel.getEndY());
                        int lastX = Math.max(panel.getBeginX(), panel.getEndX());
                        int lastY = Math.max(panel.getBeginY(), panel.getEndY());
                        // Based on the code in lljTran.save()
                        IterativeWriter writer = lljTran.initWrite(outputStream, LLJTran.CROP, LLJTran.OPT_WRITE_ALL,
                                new Rectangle(
                                        (int) (firstX * ratio),
                                        (int) (firstY * ratio),
                                        (int) ((lastX - firstX) * ratio),
                                        (int) ((lastY - firstY) * ratio)),
                                0);
                        while (writer.nextWrite(10000000) == IterativeReader.CONTINUE);
                    }
                    if (iNaturalistUploadFile == null) {
                        croppedWildLogFile.setFileDate(UtilsImageProcessing.getDateFromFileDate(croppedWildLogFile.getAbsolutePath()));
                        croppedWildLogFile.setFileSize(Files.size(croppedWildLogFile.getAbsolutePath()));
                        WildLogApp.getApplication().getDBI().createWildLogFile(croppedWildLogFile);
                    }
                    // Cleanup
                    lljTran.freeMemory();
                }
                catch (IOException | LLJTranException ex) {
                    WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                    WLOptionPane.showMessageDialog(CropDialog.this,
                            "There was an unexpected error while trying to crop the current image.",
                            "Could Not Crop Image", JOptionPane.ERROR_MESSAGE);
                }
                setVisible(false);
                dispose();
            }
        });
    }//GEN-LAST:event_btnCropActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCrop;
    private javax.swing.JPanel pnlImage;
    // End of variables declaration//GEN-END:variables
}
