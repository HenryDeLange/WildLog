package wildlog.movies.jpegmovie;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;

public class ImageStream implements PullBufferStream {
	private List<String> images;
	private int size;
	private VideoFormat format;
	private int nextImage = 0;
	private boolean ended = false;
	private float frameRate = 1;

	public ImageStream(int inSize, float inFrameRate, List<String> inImageList) {
		size = inSize;
		images = inImageList;
		frameRate = inFrameRate;
		format = new VideoFormat(
			VideoFormat.JPEG,
			new Dimension(size, size),
			Format.NOT_SPECIFIED,
			Format.byteArray,
			frameRate
			);
	}

	@Override
	public boolean willReadBlock() {
		// We should never need to block assuming data are read from files.
		return false;
	}

	/**
	 * This is called from the Processor to read a frame worth of video data.
	 */
	@Override
	public void read(Buffer inBuffer) throws IOException {
		// Check if we've finished all the frames.
		if (nextImage >= images.size()) {
			// We are done. Set EndOfMedia.
			inBuffer.setEOM(true);
			inBuffer.setOffset(0);
			inBuffer.setLength(0);
			ended = true;
			return;
		}
		// Not the end of file yet, so continue processing.
		// Open a random access file for the next image.
		String imageFile = (String) images.get(nextImage++);
		// Should resize the images before processing (this will most likely be much slower...)
		BufferedImage originalBufferedImage = ImageIO.read(new File(imageFile));
		int finalHeight = size;
		int finalWidth = size;
		if (originalBufferedImage.getHeight() >= originalBufferedImage.getWidth()) {
                    if (originalBufferedImage.getHeight() >= size) {
                            double ratio = (double)originalBufferedImage.getHeight() / (double)size;
                            finalWidth = (int)((double)originalBufferedImage.getWidth() / ratio);
                    }
                    else {
                            double ratio = (double)size / (double)originalBufferedImage.getHeight();
                            finalWidth = (int)((double)originalBufferedImage.getWidth() * ratio);
                    }
		}
		else {
                    if (originalBufferedImage.getWidth() >= size) {
                            double ratio = (double)originalBufferedImage.getWidth() / (double)size;
                            finalHeight = (int)((double)originalBufferedImage.getHeight() / ratio);
                    }
                    else {
                            double ratio = (double)size / (double)originalBufferedImage.getWidth();
                            finalHeight = (int)((double)originalBufferedImage.getHeight() * ratio);
                    }
                }
		BufferedImage resizedBufferedImage = new BufferedImage(size, size, originalBufferedImage.getType());
		Graphics2D graphics2D = resizedBufferedImage.createGraphics();
		graphics2D.drawImage(originalBufferedImage, (size - finalWidth)/2, (size - finalHeight)/2, finalWidth, finalHeight, null);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(resizedBufferedImage, "JPEG", outputStream);
		inBuffer.setData(outputStream.toByteArray());
		graphics2D.dispose();
		inBuffer.setLength(outputStream.size());
		inBuffer.setFormat(new VideoFormat(
			format.getEncoding(),
			new Dimension(size, size),
			format.getMaxDataLength(),
			format.getDataType(),
			format.getFrameRate()
			));
//		}
		// Wrap up the setting of the Buffer
		inBuffer.setOffset(0);
		inBuffer.setFlags(inBuffer.getFlags() | Buffer.FLAG_KEY_FRAME);
	}

	@Override
	public boolean endOfStream() {
		return ended;
	}

	@Override
	public Format getFormat() {
		// Return the format of each video frame.
		return format;
	}

	@Override
	public ContentDescriptor getContentDescriptor() {
		return new ContentDescriptor(ContentDescriptor.RAW);
	}

	// NOTE: The rest of the methods are not fully implemented (I don't think they are really needed)...

	@Override
	public long getContentLength() {
		// Do nothing
		return 0;
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