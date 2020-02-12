package wildlog.mediaplayer;


public class VideoController {
    public enum VideoStatus { PLAYING, PAUSED, STOPPED };
    private VideoStatus status;

// TODO: Soek 'n manier om STOPPED goed te hanteer...
    
    public VideoController() {
        status = VideoStatus.PLAYING;
    }
    

    public VideoStatus getStatus() {
        return status;
    }

    public void setStatus(VideoStatus inStatus) {
        status = inStatus;
    }
    
}
