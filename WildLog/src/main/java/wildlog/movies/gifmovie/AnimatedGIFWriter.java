package wildlog.movies.gifmovie;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
//  
// Created by Elliot Kroo on 2009-04-25.
// This work is licensed under the Creative Commons Attribution 3.0 Unported
// License. To view a copy of this license, visit
// http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
// Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
//
// Edited and slightly improved by yours truly (Henry de Lange) on Aug 2014.
//
public class AnimatedGIFWriter {
    private final ImageWriter imageWriter;
    private final ImageWriteParam imageWriteParam;
    private final IIOMetadata imageMetaData;

    /**
     * Creates a new GifSequenceWriter.
     *
     * @param inOutputStream the ImageOutputStream to be written to
     * @param inImageType one of the imageTypes specified in BufferedImage
     * @param inTimeBetweenFramesMS the time between frames in milliseconds
     * @param inLoopContinuously whether the GIF should loop repeatedly
     * @throws IIOException if no GIF ImageWriters are found
     */
    public AnimatedGIFWriter(ImageOutputStream inOutputStream, int inImageType, int inTimeBetweenFramesMS, boolean inLoopContinuously) 
            throws IIOException, IOException {
        imageWriter = getWriter();
        imageWriteParam = imageWriter.getDefaultWriteParam();
        ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(inImageType);
        imageMetaData = imageWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(inTimeBetweenFramesMS / 10));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by Mywild");
        IIOMetadataNode appEntensionsNode = getNode(root, "ApplicationExtensions");
        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");
        int loopCount;
        if (inLoopContinuously) {
            loopCount = 0;
        }
        else {
            loopCount = 1;
        }
        child.setUserObject(new byte[]{0x1, (byte) (loopCount & 0xFF), (byte) ((loopCount >> 8) & 0xFF)});
        appEntensionsNode.appendChild(child);
        imageMetaData.setFromTree(metaFormatName, root);
        imageWriter.setOutput(inOutputStream);
        imageWriter.prepareWriteSequence(null);
    }

    /**
     * Add an image to the GIF.
     * @param img the image to add
     * @throws IOException 
     */
    public void writeToGIF(RenderedImage img) throws IOException {
        imageWriter.writeToSequence(new IIOImage(img, null, imageMetaData), imageWriteParam);
    }

    /**
     * Close this AnimatedGIFWriter object. This does not close the underlying stream, just finishes off the GIF.
     * @throws java.io.IOException
     */
    public void finishGIF() throws IOException {
        imageWriter.endWriteSequence();
    }

    /**
     * Returns the first available GIF ImageWriter using ImageIO.getImageWritersBySuffix("gif").
     *
     * @return a GIF ImageWriter object
     * @throws IIOException if no GIF image writers are returned
     */
    private ImageWriter getWriter() throws IIOException {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
        if (!iter.hasNext()) {
            throw new IIOException("No GIF Image Writers Exist");
        }
        else {
            return iter.next();
        }
    }

    /**
     * Returns an existing child node, or creates and returns a new child node (if the requested node does not exist).
     *
     * @param inRootNode the <tt>IIOMetadataNode</tt> to search for the child node.
     * @param inNodeName the name of the child node.
     * @return the child node, if found or a new node created with the given name.
     */
    private IIOMetadataNode getNode(IIOMetadataNode inRootNode, String inNodeName) {
        int nNodes = inRootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (inRootNode.item(i).getNodeName().compareToIgnoreCase(inNodeName) == 0) {
                return ((IIOMetadataNode) inRootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(inNodeName);
        inRootNode.appendChild(node);
        return (node);
    }


//    public static void main(String[] args) throws Exception {
//        if (args.length > 1) {
//            // grab the output image type from the first image in the sequence
//            BufferedImage firstImage = ImageIO.read(new File(args[0]));
//
//            // create a new BufferedOutputStream with the last argument
//            ImageOutputStream output
//                    = new FileImageOutputStream(new File(args[args.length - 1]));
//
//            // create a gif sequence with the type of the first image, 1 second
//            // between frames, which loops continuously
//            AnimatedGIFWriter writer
//                    = new AnimatedGIFWriter(output, firstImage.getType(), 1, false);
//
//            // write out the first image to our sequence...
//            writer.writeToSequence(firstImage);
//            for (int i = 1; i < args.length - 1; i++) {
//                BufferedImage nextImage = ImageIO.read(new File(args[i]));
//                writer.writeToSequence(nextImage);
//            }
//
//            writer.close();
//            output.close();
//        }
//        else {
//            System.out.println(
//                    "Usage: java AnimatedGIFWriter [list of gif files] [output file]");
//        }
//    }
    
}
