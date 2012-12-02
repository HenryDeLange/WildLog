package wildlog.movies.jpegmovie;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;


public class JpgToMovie implements ControllerListener, DataSinkListener {
    private final Object waitSyncLock = new Object();
    private final Object waitFileSyncLock = new Object();
    private boolean stateTransitionOK = true;
    private boolean fileDone = false;
    private boolean fileSuccess = true;

    public boolean createMovieFromJpgs(int inSize, float inFrameRate, List<String> inFiles, String outputURL) {
        // Configure the processor
        Processor processor;
        try {
            processor = Manager.createProcessor(new ImageDataSource(inSize, inFrameRate, inFiles));
        }
        catch (Exception e) {
            System.err.println("Yikes!  Cannot create a processor from the data source. ");
            e.printStackTrace(System.err);
            return false;
        }
        // Add controllerListener
        processor.addControllerListener(this);

        // Put the Processor into configured state so we can set some processing options on the processor.
        processor.configure();
        if (!waitForState(processor, Processor.Configured)) {
            System.err.println("Failed to configure the processor.");
            return false;
        }

        // Set the output content descriptor to QuickTime.
        processor.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));
        // Query for the processor for supported formats. Then set it on the processor.
        TrackControl trackControl[] = processor.getTrackControls();
        Format format[] = trackControl[0].getSupportedFormats();
        if (format == null || format.length <= 0) {
            System.err.println("The mux does not support the input format: " + trackControl[0].getFormat());
            return false;
        }
        trackControl[0].setFormat(format[0]);

        // We are done with configuring the processor. Now realize it.
        processor.realize();
        if (!waitForState(processor, Processor.Realized)) {
            System.err.println("Failed to realize the processor.");
            return false;
        }

        // Generate the output media locators.
        MediaLocator mediaLocator;
        if ((mediaLocator = createMediaLocator(outputURL)) == null) {
            System.err.println("Cannot build media locator from: " + outputURL);
            return false;
        }

        // Now, we'll need to create a DataSink.
        DataSink dataSink;
        if ((dataSink = createDataSink(processor, mediaLocator)) == null) {
            System.err.println("Failed to create a DataSink for the given output MediaLocator: " + mediaLocator);
            return false;
        }
        dataSink.addDataSinkListener(this);

        // OK, we can now start the actual transcoding.
        fileDone = false;
        processor.start();
        try {
            dataSink.start();
        }
        catch (IOException e) {
            System.err.println("IO error during processing: ");
            e.printStackTrace(System.err);
            return false;
        }

        // Wait for EndOfStream event.
        waitForFileDone();

        // Cleanup the dataSink and the ControllListener.
        try {
            dataSink.close();
        }
        catch (Exception e) {
            System.err.println("Error closing dataSink: ");
            e.printStackTrace(System.err);
        }
        // Remove the controllerListener
        processor.removeControllerListener(this);

        return true;
    }

    private MediaLocator createMediaLocator(String inURL) {
        MediaLocator mediaLocator;
        if (inURL.indexOf(":") > 0) {
            mediaLocator = new MediaLocator(inURL);
        }
        else
        if (inURL.startsWith(File.separator)) {
            mediaLocator = new MediaLocator("file:" + inURL);
        }
        else {
            String file = "file:" + System.getProperty("user.dir") + File.separator + inURL;
            mediaLocator = new MediaLocator(file);
        }
        return mediaLocator;
    }

    private DataSink createDataSink(Processor inProcessor, MediaLocator inMediaLocator) {
        DataSource dataSource;
        if ((dataSource = inProcessor.getDataOutput()) == null) {
            System.err.println("Something is really wrong: the processor does not have an output DataSource");
            return null;
        }
        DataSink dataSink;
        try {
            dataSink = Manager.createDataSink(dataSource, inMediaLocator);
            dataSink.open();
        }
        catch (Exception e) {
            System.err.println("Cannot create the DataSink: ");
            e.printStackTrace(System.err);
            return null;
        }
        return dataSink;
    }

    /**
     * Block until the processor has transitioned to the given state.
     * Return false if the transition failed.
     */
    private boolean waitForState(Processor inProcessor, int inState) {
        synchronized (waitSyncLock) {
            try {
                while(stateTransitionOK && inProcessor.getState() < inState) {
                    waitSyncLock.wait();
                }
            }
            catch(InterruptedException ex) {
                // I thinks this exception is propabily not a big deal in this case,
                // just logging it for the record and to troubleshoot possible future problems
                ex.printStackTrace(System.err);
            }
        }
        return stateTransitionOK;
    }

    /**
     * Block until file writing is done.
     */
    private boolean waitForFileDone()
    {
        synchronized(waitFileSyncLock) {
            try {
                while(!fileDone) {
                    waitFileSyncLock.wait();
                }
            }
            catch(InterruptedException ex) {
                // I thinks this exception is propabily not a big deal in this case,
                // just logging it for the record and to troubleshoot possible future problems
                ex.printStackTrace(System.err);
            }
        }
        return fileSuccess;
    }

    @Override
    public void controllerUpdate(ControllerEvent evt) {
        if(evt instanceof ConfigureCompleteEvent ||
            evt instanceof RealizeCompleteEvent ||
            evt instanceof PrefetchCompleteEvent) {
            synchronized(waitSyncLock) {
                stateTransitionOK = true;
                waitSyncLock.notifyAll();
            }
        }
        else
        if(evt instanceof ResourceUnavailableEvent) {
            synchronized(waitSyncLock) {
                stateTransitionOK = false;
                waitSyncLock.notifyAll();
            }
        }
        else
        if(evt instanceof EndOfMediaEvent) {
            if (evt != null) {
                evt.getSourceController().stop();
                evt.getSourceController().close();
            }
        }
    }

    @Override
    public void dataSinkUpdate(DataSinkEvent evt) {
        if(evt instanceof EndOfStreamEvent) {
            synchronized(waitFileSyncLock) {
                fileDone = true;
                fileSuccess = true;
                waitFileSyncLock.notifyAll();
            }
        }
        else
        if(evt instanceof DataSinkErrorEvent) {
            synchronized(waitFileSyncLock) {
                fileDone = true;
                fileSuccess = false;
                waitFileSyncLock.notifyAll();
            }
        }
    }

}
