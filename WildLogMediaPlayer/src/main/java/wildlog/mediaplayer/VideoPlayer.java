package wildlog.mediaplayer;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerStream;
import io.humble.video.Global;
import io.humble.video.MediaDescriptor;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Based on the DecodeAndPlayVideo.java demo from https://github.com/artclarke/humble-video
 */
public class VideoPlayer {

    /**
     * Opens a file, and plays the video from it on a screen at the right rate.
     */
    public static void playVideo(VideoPanel inVideoPanel, Path inFilePath, int inBoxSize) {
        Demuxer demuxer = null;
        try {
            // Start by creating a container object, in this case a demuxer since we are reading, to get video data from.
            demuxer = Demuxer.make();
            // Open the demuxer with the filename passed on.
            demuxer.open(inFilePath.toAbsolutePath().normalize().toString(), null, false, true, null, null);
            // Query how many streams the call to open found
            int numStreams = demuxer.getNumStreams();
            // Iterate through the streams to find the first video stream
            int videoStreamId = -1;
            long streamStartTime = Global.NO_PTS;
            Decoder videoDecoder = null;
            for (int i = 0; i < numStreams; i++) {
                final DemuxerStream stream = demuxer.getStream(i);
                streamStartTime = stream.getStartTime();
                final Decoder decoder = stream.getDecoder();
                if (decoder != null && decoder.getCodecType() == MediaDescriptor.Type.MEDIA_VIDEO) {
                    videoStreamId = i;
                    videoDecoder = decoder;
                    break;
                }
            }
            if (videoStreamId == -1) {
                throw new RuntimeException("Could not find video stream in container: " + inFilePath);
            }
            // Now we have found the video stream in this file.
            // Let's open up our decoder so it can do work.
            videoDecoder.open(null, null);
            // Setup the media picture
            final MediaPicture picture = MediaPicture.make(videoDecoder.getWidth(), videoDecoder.getHeight(), videoDecoder.getPixelFormat());
            // A converter object we'll use to convert the picture in the video to a BGR_24 format that Java Swing can work with.
            // You can still access the data directly in the MediaPicture if you prefer, 
            // but this abstracts away from this demo most of that byte-conversion work.
            // Go read the source code for the converters if you're a glutton for punishment.
            int scaledWidth = inBoxSize;
            int scaledHeight = inBoxSize;
            if (picture.getWidth() > picture.getHeight()) {
                scaledHeight = (int) (((double) picture.getHeight()) / ((double) picture.getWidth()) * ((double) inBoxSize));
            }
            else
            if (picture.getWidth() < picture.getHeight()) {
                scaledWidth = (int) (((double) picture.getWidth()) / ((double) picture.getHeight()) * ((double) inBoxSize));
            }
            final MediaPictureConverter converter = MediaPictureConverterFactory.createConverter(MediaPictureConverterFactory.HUMBLE_BGR_24, 
                    picture.getFormat(), picture.getWidth(), picture.getHeight(), scaledWidth, scaledHeight);
            // Media playback, like comedy, is all about timing.
            // Here we're going to introduce <b>very very basic</b> timing. 
            // This code is deliberately kept simple (i.e. doesn't worry about A/V drift, garbage collection pause time, etc.) 
            // because that will quickly make things more complicated.
            // But the basic idea is there are two clocks:
            // - Player Clock: The time that the player sees (relative to the system clock).
            // - Stream Clock: Each stream has its own clock, and the ticks are measured in units of time-bases
            // And we need to convert between the two units of time. 
            // Each MediaPicture and MediaAudio object have associated time stamps, 
            // and much of the complexity in video players goes into making sure the right picture (or sound) is seen (or heard) at the right time. 
            // This is actually very tricky and many folks get it wrong -- watch enough Netflix and you'll see what I mean -- audio and video slightly out of sync. 
            // But for this demo, we're erring for 'simplicity' of code, not correctness. It is beyond the scope of this demo to make a full fledged video player.
            // Calculate the time BEFORE we start playing.
            long systemStartTime = System.nanoTime();
            // Set units for the system time, which because we used System.nanoTime will be in nanoseconds.
            final Rational systemTimeBase = Rational.make(1, 1000000000);
            // All the MediaPicture objects decoded from the videoDecoder will share this timebase.
            final Rational streamTimebase = videoDecoder.getTimeBase();
            // Now, we start walking through the container looking at each packet. 
            // This is a decoding loop, and as you work with Humble you'll write a lot of these.
            // Notice how in this loop we reuse all of our objects to avoid reallocating them. 
            // Each call to Humble resets objects to avoid unnecessary reallocation.
            long pauseTime = 0;
            BufferedImage image = null;
            final MediaPacket packet = MediaPacket.make();
            while (demuxer.read(packet) >= 0) {
                // Now we have a packet, let's see if it belongs to our video stream
                if (packet.getStreamIndex() == videoStreamId) {
                    // A packet can actually contain multiple sets of samples (or frames of samples in decoding speak). 
                    // So, we may need to call decode multiple times at different offsets in the packet's data. We capture that here.
                    int offset = 0;
                    int bytesRead = 0;
                    do {
                        try {
                            bytesRead += videoDecoder.decode(picture, packet, offset);
                            if (picture.isComplete()) {
                                image = displayVideoAtCorrectTime(
                                        streamStartTime, pauseTime, picture, converter, image, inVideoPanel, systemStartTime, systemTimeBase, streamTimebase);
                            }
                            offset += bytesRead;
                        }
                        catch (Exception ex) {
                            // WildLog NOTE: The camera trap files can be very "dirty", 
                            //               so if something goes wrong then ignore the error 
                            //               and continue to try and read the remaining data
                            //System.err.println(ex.getMessage());
                            break;
                        }
                        // Check whether the video is paused
                        pauseTime = pauseTime + handlePauseStatus(inVideoPanel.getController());
                    }
                    while (offset < packet.getSize());
                }
            }
            // Some video decoders (especially advanced ones) will cache
            // video data before they begin decoding, so when you are done you need to flush them. 
            // The convention to flush Encoders or Decoders in Humble Video
            // is to keep passing in null until incomplete samples or packets are returned.
            do {
                // Decode the video
                videoDecoder.decode(picture, null, 0);
                if (picture.isComplete()) {
                    try {
                        image = displayVideoAtCorrectTime(
                                streamStartTime, pauseTime, picture, converter, image, inVideoPanel, systemStartTime, systemTimeBase, streamTimebase);
                    }
                    catch (Exception ex) {
                        // WildLog NOTE: The camera trap files can be very "dirty", 
                        //               so if something goes wrong then ignore the error 
                        //               and continue to try and read the remaining data
                        //System.err.println(ex.getMessage());
                    }
                }
            }
            while (picture.isComplete());
        }
        catch(IOException | InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
        finally {
            // It is good practice to close demuxers when you're done to free up file handles. 
            // Humble will EVENTUALLY detect if nothing else references this demuxer and close it then, 
            // but get in the habit of cleaning up after yourself, and your future girlfriend/boyfriend will appreciate it.
            if (demuxer != null) {
                try {
                    demuxer.close();
                }
                catch(IOException | InterruptedException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    /**
     * Takes the video picture and displays it at the right time.
     */
    private static BufferedImage displayVideoAtCorrectTime(long streamStartTime, long inPauseTime, final MediaPicture picture, 
            final MediaPictureConverter converter, BufferedImage image, final VideoPanel inVideoPanel, long systemStartTime,
            final Rational systemTimeBase, final Rational streamTimebase) throws InterruptedException {
        // Convert streamTimestamp into system units (i.e. nano-seconds)
        long streamTimestamp = systemTimeBase.rescale(picture.getTimeStamp() - streamStartTime, streamTimebase);
        // Get the current clock time, with our most accurate clock
        long systemTimestamp = System.nanoTime() - inPauseTime;
        // Loop in a sleeping loop until we're within 1 ms of the time for that video frame.
        // A real video player needs to be much more sophisticated than this.
        while (streamTimestamp > (systemTimestamp - systemStartTime + 1000000)) {
            Thread.sleep(1);
            systemTimestamp = System.nanoTime() - inPauseTime;
        }
        // Finally, convert the image from Humble format into Java images.
        image = converter.toImage(image, picture);
        // And ask the UI thread to repaint with the new image.
        inVideoPanel.setImage(image);
        return image;
    }
    
    private static long handlePauseStatus(VideoController inController) {
        if (inController.getStatus() == VideoController.VideoStatus.PAUSED) {
            try {
                long pauseTime = System.nanoTime();
                boolean wasPaused = false;
                while (inController.getStatus() == VideoController.VideoStatus.PAUSED) {
                    Thread.sleep(100);
                    wasPaused = true;
                }
                if (wasPaused) {
                    return System.nanoTime() - pauseTime;
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return 0;
    }

}
