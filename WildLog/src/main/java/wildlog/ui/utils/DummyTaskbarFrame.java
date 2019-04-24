package wildlog.ui.utils;

import java.awt.Image;
import javax.swing.JFrame;

/**
 * Word gebruik om 'n JDialog in die taskbar te wys, bv. vir login of database upgrade.
 */
public class DummyTaskbarFrame extends JFrame {
    public DummyTaskbarFrame(String inTitle, Image inIcon) {
        super(inTitle);
        setUndecorated(true);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImage(inIcon);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
