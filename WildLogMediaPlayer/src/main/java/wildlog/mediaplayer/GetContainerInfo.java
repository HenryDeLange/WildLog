package wildlog.mediaplayer;

import java.io.IOException;

import io.humble.video.Decoder;
import io.humble.video.Demuxer;
import io.humble.video.DemuxerFormat;
import io.humble.video.DemuxerStream;
import io.humble.video.Global;
import io.humble.video.KeyValueBag;

/**
 * Based on the GetContainerInfo.java demo from https://github.com/artclarke/humble-video
 */
public class GetContainerInfo {

    /**
     * Parse information from a file, and also optionally print information about what formats, containers and codecs fit into that file.
     */
    public static void getInfo(String inFilePath) throws InterruptedException, IOException {
        // In Humble, all objects have special contructors named 'make'.
        // A Demuxer opens up media containers, parses  and de-multiplexes the streams of media data without those containers.
        final Demuxer demuxer = Demuxer.make();
        // We open the demuxer by pointing it at a URL.
        demuxer.open(inFilePath, null, false, true, null, null);
        // Once we've opened a demuxer, Humble can make a guess about the
        // DemuxerFormat. Humble supports over 100+ media container formats.
        final DemuxerFormat format = demuxer.getFormat();
        System.out.printf("URL: '%s' (%s: %s)\n", demuxer.getURL(), format.getLongName(), format.getName());
        // Many programs that make containers, such as iMovie or Adobe Elements, will insert meta-data about the container. 
        // Here we extract that meta data and print it.
        KeyValueBag metadata = demuxer.getMetaData();
        System.out.println("MetaData:");
        for (String key : metadata.getKeys()) {
            System.out.printf("  %s: %s\n", key, metadata.getValue(key));
        }
        // There are a few other key pieces of information that are interesting for most containers; 
        // The duration, the starting time, and the estimated bit-rate.
        // This code extracts all three.
        final String formattedDuration = formatTimeStamp(demuxer.getDuration());
        System.out.printf("Duration: %s, start: %f, bitrate: %d kb/s\n", formattedDuration,
                demuxer.getStartTime() == Global.NO_PTS ? 0 : demuxer.getStartTime() / 1000000.0,
                demuxer.getBitRate() / 1000);
        // Finally, a container consists of several different independent streams of data called Streams. 
        // In Humble there are two objects that represent streams:
        // DemuxerStream (when you are reading) and MuxerStreams (when you are writing).
        // First find the number of streams in this container.
        int ns = demuxer.getNumStreams();
        // Now, let's iterate through each of them.
        for (int i = 0; i < ns; i++) {
            DemuxerStream stream = demuxer.getStream(i);
            metadata = stream.getMetaData();
            // Language is usually embedded as metadata in a stream.
            final String language = metadata.getValue("language");
            // We will only be able to make a decoder for streams we can actually decode, so the caller should check for null.
            Decoder d = stream.getDecoder();
            System.out.printf(" Stream #0.%1$d (%2$s): %3$s\n", i, language, d != null ? d.toString() : "unknown coder");
            System.out.println("  Metadata:");
            for (String key : metadata.getKeys()) {
                System.out.printf("    %s: %s\n", key, metadata.getValue(key));
            }
        }
    }

    /**
     * Pretty prints a timestamp (in {@link Global.NO_PTS} units) into a string.
     */
    private static String formatTimeStamp(long duration) {
        if (duration == Global.NO_PTS) {
            return "00:00:00.00";
        }
        double d = 1.0 * duration / Global.DEFAULT_PTS_PER_SECOND;
        int hours = (int) (d / (60 * 60));
        int mins = (int) ((d - hours * 60 * 60) / 60);
        int secs = (int) (d - hours * 60 * 60 - mins * 60);
        int subsecs = (int) ((d - (hours * 60 * 60.0 + mins * 60.0 + secs)) * 100.0);
        return String.format("%1$02d:%2$02d:%3$02d.%4$02d", hours, mins, secs, subsecs);
    }

}
