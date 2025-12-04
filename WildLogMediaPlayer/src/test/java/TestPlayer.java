
import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFrame;
import wildlog.mediaplayer.VideoController;
import wildlog.mediaplayer.VideoPanel;
import wildlog.mediaplayer.VideoPlayer;

public class TestPlayer {
    public static void main(String[] args) throws InterruptedException, IOException {
        Path videoPath = Paths.get("C:\\_temp\\IMG_0008.AVI");
//        VideoInfo.getInfo(videoFile);
        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setBackground(Color.GRAY);
        VideoController videoController = new VideoController();
        VideoPanel videoPanel = new VideoPanel(videoController, 300, 300);
        frame.add(videoPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        VideoPlayer.playVideo(videoPanel, videoPath, 300);
    }
}
