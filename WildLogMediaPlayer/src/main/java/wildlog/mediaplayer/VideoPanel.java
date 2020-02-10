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

/**
 * Based on ImageFrame.java demo from https://github.com/artclarke/humble-video
 */
public class VideoPanel extends JPanel {
    private final Font font = new JLabel().getFont().deriveFont(13);
    private final int panelWidth;
    private final int panelHeight;
    private int xOffset = 0;
    private int yOffset = 0;
    private Image image;
// TODO: Kry 'n manier om te pause / play
    private boolean playing = true;

    
    public VideoPanel(int inWidth, int inHeight) {
        panelWidth = inWidth;
        panelHeight = inHeight;
        VideoPanel.this.setBackground(Color.BLACK);
        VideoPanel.this.setSize(panelWidth, panelHeight);
        VideoPanel.this.setMinimumSize(new Dimension(panelWidth, panelHeight));
        VideoPanel.this.setPreferredSize(new Dimension(panelWidth, panelHeight));
        VideoPanel.this.setMinimumSize(new Dimension(panelWidth, panelHeight));
        VideoPanel.this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent inEvent) {
                if ((inEvent.isPopupTrigger() || SwingUtilities.isRightMouseButton(inEvent))) {
                    playing = !playing;
                    System.out.println("Playing = " + playing);
                }
            }
        });
    }

    
    public boolean setImage(final Image newImage) {
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
        return playing;
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
