package wildlog.ui.maps.implementations.helpers;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import org.jdesktop.swingx.JXMapKit;
import wildlog.WildLogApp;


public class MapFrameOnline extends JPanel {
    private final JXMapKit map;
    private final MapOnlinePointLayer pointLayer;

    public MapFrameOnline(JXMapKit inMap, final WildLogApp inApp) {
        map = inMap;
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setPreferredSize(new Dimension(450, 550));
        JScrollPane scrollPane = new JScrollPane(textPane);
        map.getMainMap().add(scrollPane);
        textPane.setText("<u><b>Information Box:</b></u> "
                + "<br/><br/>Click on a point on the map to view more details. "
                + "<br/><br/>If you clicked on a point with more than one Observation use the buttons on the left/right of the map to cycle between the Observations. "
                + "<br/><br/>Clicking on an area on the map without a Observation will make this Information Box dialog dissapear.");
        scrollPane.setLocation(new Point(50, 15));
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
        map.getMainMap().addMouseListener(new MouseListener() {
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

}
