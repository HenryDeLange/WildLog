
import java.io.IOException;
import wildlog.mediaplayer.VideoInfo;
import wildlog.mediaplayer.VideoPlayer;

public class TestPlayer {
    public static void main(String[] args) throws InterruptedException, IOException {
        String videoFile = "C:\\WildLogToets\\WEI-DATA\\GSZ_Non-big-5_area-20200207T174520Z-001\\GSZ_Non-big-5_area\\IMG_0005.AVI";
        VideoInfo.getInfo(videoFile);
        VideoPlayer.playVideo(videoFile, 300);
    }
}
