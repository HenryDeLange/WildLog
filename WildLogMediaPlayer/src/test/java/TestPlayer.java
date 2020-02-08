
import java.io.IOException;
import wildlog.mediaplayer.GetContainerInfo;
import wildlog.mediaplayer.VideoPlayer;

public class TestPlayer {
    public static void main(String[] args) throws InterruptedException, IOException {
        String videoFile = "C:\\WildLogToets\\WEI-DATA\\GSZ_Non-big-5_area-20200207T174520Z-001\\GSZ_Non-big-5_area\\IMG_0005.AVI";
        GetContainerInfo.getInfo(videoFile);
        VideoPlayer.playVideo(videoFile, 300);
    }
}
