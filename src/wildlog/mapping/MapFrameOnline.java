package wildlog.mapping;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import org.jdesktop.swingx.JXMapKit;
import wildlog.WildLogApp;
import wildlog.mapping.layers.MapOnlinePointLayer;
import wildlog.mapping.other.WildLogMapMouseListener;
import wildlog.mapping.other.WildLogScrollPanel;

/**
 *
 * @author Henry
 */
public class MapFrameOnline extends JFrame{
    private JXMapKit map;
    private MapOnlinePointLayer pointLayer;

    public MapFrameOnline(String inTitle, JXMapKit inMap, WildLogApp inApp) {
        super(inTitle);
        map = inMap;
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setPreferredSize(new Dimension(330, 450));
        WildLogScrollPanel scrollPane = new WildLogScrollPanel(textPane);
        map.getMainMap().add(scrollPane);
        textPane.setText("Information Box <br/><br/> Click on 'n point on the map to view more details. You can also select different base layers by pressing the corresponding butons at the bottom of the map. Click on the map to make this dialog dissapear. <br/><br/> <b>TODO: Add pagenation for overlapping points!</b>");
        scrollPane.setLocation(new Point(15, 15));
        //scrollPane.setVisible(false);
        pointLayer = new MapOnlinePointLayer(map);
        setupMouseListener(inApp);
    }

    public JXMapKit getMap() {
        return map;
    }

    public MapOnlinePointLayer getPointLayer() {
        return pointLayer;
    }

    private void setupMouseListener(final WildLogApp inApp) {
        map.getMainMap().addMouseListener(new WildLogMapMouseListener() {
            @Override
            public void mouseClicked(MouseEvent event) {
                pointLayer.showPopup(event, inApp);
            }
            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    public void showMap(Color inColor) {
        pointLayer.loadPoints(inColor);
        this.setVisible(true);
    }
    
}
