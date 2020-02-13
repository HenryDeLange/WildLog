package wildlog.mediaplayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * Based on ImageFrame.java demo from https://github.com/artclarke/humble-video
 */
public class VideoPanel extends JPanel {
    private final Font font = new JLabel().getFont().deriveFont(13);
    private final int panelWidth;
    private final int panelHeight;
    private final VideoController controller;
    private int xOffset = 0;
    private int yOffset = 0;
    private Image image;

    
    public VideoPanel(VideoController inVideoController, int inWidth, int inHeight) {
        controller = inVideoController;
        panelWidth = inWidth;
        panelHeight = inHeight;
        VideoPanel.this.setBackground(Color.BLACK);
        VideoPanel.this.setSize(panelWidth, panelHeight);
        VideoPanel.this.setMinimumSize(new Dimension(panelWidth, panelHeight));
        VideoPanel.this.setPreferredSize(new Dimension(panelWidth, panelHeight));
        VideoPanel.this.setMinimumSize(new Dimension(panelWidth, panelHeight));
        // Play / Pause on click
        VideoPanel.this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent inEvent) {
                if (controller.getStatus() == VideoController.VideoStatus.PLAYING) {
                    controller.setStatus(VideoController.VideoStatus.PAUSED);
                }
                else
                if (controller.getStatus() == VideoController.VideoStatus.PAUSED) {
                    controller.setStatus(VideoController.VideoStatus.PLAYING);
                }
            }
        });
        // Stop playback when the panel is close
        VideoPanel.this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                controller.setStatus(VideoController.VideoStatus.STOPPED);
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    public VideoController getController() {
        return controller;
    }
    
    public void setImage(final Image newImage) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                image = newImage;
                xOffset = Math.abs(image.getWidth(null) - panelWidth) / 2;
                yOffset = Math.abs(image.getHeight(null) - panelHeight) / 2;
                revalidate();
                repaint();
            }
        });
    }

    @Override
    public synchronized void paint(Graphics g) {
        if (image != null) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, panelWidth, panelHeight);
            g.drawImage(image, xOffset, yOffset, this);
        }
        else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, panelWidth, panelHeight);
            g.setColor(Color.WHITE);
            drawCenteredString(g, "Loading...", new Rectangle(panelWidth, panelHeight), font);
        }
    }

    private void drawCenteredString(Graphics inG, String inText, Rectangle inRectangle, Font inFont) {
        FontMetrics metrics = inG.getFontMetrics(inFont);
        int x = inRectangle.x + (inRectangle.width - metrics.stringWidth(inText)) / 2;
        int y = inRectangle.y + ((inRectangle.height - metrics.getHeight()) / 2) + metrics.getAscent();
        inG.setFont(inFont);
        inG.drawString(inText, x, y);
    }

}
