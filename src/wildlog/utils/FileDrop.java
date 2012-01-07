package wildlog.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * This class makes it easy to drag and drop files from the operating
 * system to a Java program. Any <tt>java.awt.Component</tt> can be
 * dropped onto.
 * 
 * This code is based on some code I found online (that was released in the public domain).
 * I did some cleanup and refactoring to improve it for my needs.
 */
public class FileDrop {

    /**
     * This method will add a DropTargetListerner to the provided Component and 
     * execute the provided Listener's filesDropped method when files are dropped 
     * in the component.
     */
    public static void SetupFileDrop(final Component inComponent, final boolean inIsRecursive, final Listener inListener) {
        // Make a drop listener
        DropTargetListener dropListener = new DropTargetListener() {

            @Override
            public void dragEnter(DropTargetDragEvent evt) {
                // Is this an acceptable drag event?
                if (isDragedDataAFileList(evt)) {
                    // Acknowledge that it's okay to enter
                    evt.acceptDrag(DnDConstants.ACTION_COPY);
                } else {
                    // Reject the drag event
                    evt.rejectDrag();
                }
            }

            @Override
            public void dragOver(DropTargetDragEvent evt) {
                // This is called continually as long as the mouse is
                // over the drag target.
                // DO NOTHING...
            }

            @Override
            public void drop(DropTargetDropEvent evt) {
                try {
                    // Get whatever was dropped
                    Transferable transferable = evt.getTransferable();
                    // Is it a file list?
                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        // Say we'll take it.
                        evt.acceptDrop(DnDConstants.ACTION_COPY);
                        // Alert listener to drop.
                        if (inListener != null) {
                            // Get a useful list
                            inListener.filesDropped((List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor));
                        }
                        // Mark that drop is completed.
                        evt.getDropTargetContext().dropComplete(true);
                    }
                    /* (Apparently for LINUX support)
                    else // this section will check for a reader flavor. 
                    {
                    DataFlavor[] flavors = tr.getTransferDataFlavors();
                    boolean handled = false;
                    for (int t = 0; t < flavors.length; t++)
                    {
                    if (flavors[t].isRepresentationClassReader())
                    {
                    // Say we'll take it.
                    // evt.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE );
                    evt.acceptDrop(java.awt.dnd.DnDConstants.ACTION_COPY);
                    log(out, "FileDrop: reader accepted.");
                    Reader reader = flavors[t].getReaderForText(tr);
                    BufferedReader br = new BufferedReader(reader);
                    if (listener != null)
                    {
                    listener.filesDropped(createFileList(br, out));
                    }
                    // Mark that drop is completed.
                    evt.getDropTargetContext().dropComplete(true);
                    log(out, "FileDrop: drop complete.");
                    handled = true;
                    break;
                    }
                    }
                    if (!handled)
                    {
                    log(out, "FileDrop: not a file list or reader - abort.");
                    evt.rejectDrop();
                    }
                    }*/
                } catch (IOException io) {
                    io.printStackTrace(System.err);
                    evt.rejectDrop();
                } catch (UnsupportedFlavorException ufe) {
                    ufe.printStackTrace(System.err);
                    evt.rejectDrop();
                }
            }

            @Override
            public void dragExit(DropTargetEvent evt) {
                // If it's a Swing component, reset its border
                // DO NOTHING...
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent evt) {
                // Is this an acceptable drag event?
                if (isDragedDataAFileList(evt)) {
                    evt.acceptDrag(DnDConstants.ACTION_COPY);
                } else {
                    evt.rejectDrag();
                }
            }
        };

        // Make the component (and possibly children) drop targets
        makeDropTarget(dropListener, inComponent, inIsRecursive);
    }

    private static void makeDropTarget(final DropTargetListener inDropListener, final Component inComponent, boolean inRecursive) {
        // Make drop target
        final DropTarget dropTarget = new DropTarget();
        try {
            dropTarget.addDropTargetListener(inDropListener);
        } catch (TooManyListenersException e) {
            e.printStackTrace(System.err);
        }

        // Listen for hierarchy changes and remove the drop target when the parent gets cleared out.
        inComponent.addHierarchyListener(new HierarchyListener() {

            @Override
            public void hierarchyChanged(HierarchyEvent evt) {
                Component parent = inComponent.getParent();
                if (parent == null) {
                    inComponent.setDropTarget(null);
                } else {
                    new DropTarget(inComponent, inDropListener);
                }
            }
        });
        if (inComponent.getParent() != null) {
            new DropTarget(inComponent, inDropListener);
        }

        if (inRecursive && (inComponent instanceof Container)) {
            // Get the container
            Container cont = (Container) inComponent;
            // Get it's components
            Component[] comps = cont.getComponents();
            // Set it's components as listeners also
            for (int i = 0; i < comps.length; i++) {
                makeDropTarget(inDropListener, comps[i], inRecursive);
            }
        }
    }

    /**
     * Determine if the dragged data is a file list. 
     */
    private static boolean isDragedDataAFileList(final DropTargetDragEvent evt) {
        boolean ok = false;
        // Get data flavors being dragged
        DataFlavor[] flavors = evt.getCurrentDataFlavors();
        // See if any of the flavors are a file list
        int i = 0;
        while (i < flavors.length) {
            // Is the flavor a file list?
            final DataFlavor curFlavor = flavors[i];
            if (curFlavor.equals(DataFlavor.javaFileListFlavor) /* (Apparently for LINUX support) 
                    || curFlavor.isRepresentationClassReader()*/) {
                ok = true;
                break;
            }
            i++;
        }
        return ok;
    }

    /**
     * Use this inner interface to implement the desired behavior when files are 
     * dropped.
     */
    public static interface Listener {

        /**
         * This method is called when files have been successfully dropped.
         */
        public abstract void filesDropped(List<File> inFiles);
    }
}
