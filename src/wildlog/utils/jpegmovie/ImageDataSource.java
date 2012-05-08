package wildlog.utils.jpegmovie;

import java.util.List;
import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

public class ImageDataSource extends PullBufferDataSource {
	private ImageStream streams[];

	public ImageDataSource(int inSize, float inFrameRate, List<String> inImageList) {
		streams = new ImageStream[] {
			new ImageStream(inSize, inFrameRate, inImageList)
		};
	}

	/**
	 * Content type is of RAW since we are sending buffers of video frames
	 * without a container format.
	 */
	@Override
	public String getContentType() {
		return ContentDescriptor.RAW;
	}
	
	@Override
	public PullBufferStream[] getStreams() {
		return streams;
	}
	
	@Override
	public Time getDuration() {
		// The duration isn't required, but in theory one can use the framerate 
		// and number of images to get the correct value.
		return DURATION_UNKNOWN;
	}
	
	// NOTE: The rest of the methods are not fully implemented (I don't think they are really needed)...

	@Override
	public void setLocator(MediaLocator source) {
		// Do nothing
	}

	@Override
	public MediaLocator getLocator() {
		// Do nothing
		return null;
	}
	
	@Override
	public void connect() {
		// Do nothing
	}

	@Override
	public void disconnect() {
		// Do nothing
	}

	@Override
	public void start() {
		// Do nothing
	}

	@Override
	public void stop() {
		// Do nothing
	}

	@Override
	public Object[] getControls() {
		// Do nothing
		return new Object[0];
	}

	@Override
	public Object getControl(String type) {
		// Do nothing
		return null;
	}
}