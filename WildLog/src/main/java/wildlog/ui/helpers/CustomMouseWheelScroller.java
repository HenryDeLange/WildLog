package wildlog.ui.helpers;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JScrollPane;

/**
 * This class allows you to control the units scrolled for each mouse wheel
 * rotation relative to the unit increment value of the scroll bar. 
 * Specifying a scroll amount of 1, is equivalent to clicking the unit scroll button of the scroll bar once.
 * (Code from StackOverflow answer and in turn from a forum.)
 */
public final class CustomMouseWheelScroller implements MouseWheelListener {
    private JScrollPane scrollPane;
    private int scrollAmount = 0;
    private MouseWheelListener[] realListeners;

    /**
     * Convenience constructor to create the class with a scroll amount of 1.
     *
     * @param inScrollPane  the scroll pane being used by the mouse wheel
     */
    public CustomMouseWheelScroller(JScrollPane inScrollPane) {
        this(inScrollPane, 1);
    }

    /**
     * Create the class with the specified scroll amount.
     *
     * @param inScrollAmount  the scroll amount to by used for this scroll pane
     * @param inScrollPane  the scroll pane being used by the mouse wheel
     */
    public CustomMouseWheelScroller(JScrollPane inScrollPane, int inScrollAmount) {
        scrollPane = inScrollPane;
        setScrollAmount(inScrollAmount);
        install();
    }

    /**
     * Get the scroll amount
     *
     * @return the scroll amount.
     */
    public int getScrollAmount() {
        return scrollAmount;
    }

    /**
     * Set the scroll amount. Controls the amount the scrollpane will scroll for each mouse wheel rotation. 
     * The amount is relative to the unit increment value of the scrollbar being scrolled.
     *
     * @param inScrollAmount  an integer value. A value of zero will use the default scroll amount for your OS.
     */
    public void setScrollAmount(int inScrollAmount) {
        scrollAmount = inScrollAmount;
    }

    /**
     * Install this class as the default listener for MouseWheel events.
     */
    public void install() {
        if (realListeners != null) {
            return;
        }
        // Keep track of original listeners so we can use them to redispatch an altered MouseWheelEvent
        realListeners = scrollPane.getMouseWheelListeners();
        for (MouseWheelListener mwl : realListeners) {
            scrollPane.removeMouseWheelListener(mwl);
        }
        // Intercept events so they can be redispatched
        scrollPane.addMouseWheelListener(this);
    }

    /**
     * Remove the class as the default listener and reinstall the original listeners.
     */
    public void uninstall() {
        if (realListeners == null) {
            return;
        }
        // Remove this class as the default listener
        scrollPane.removeMouseWheelListener(this);
        // Install the default listeners
        for (MouseWheelListener mwl : realListeners) {
            scrollPane.addMouseWheelListener(mwl);
        }
        realListeners = null;
    }

    /**
     * Re-dispatch a MouseWheelEvent to the real MouseWheelListeners
     * @param inEvent
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent inEvent) {
        // Create an altered event to redispatch
        if (scrollAmount != 0) {
            inEvent = createScrollAmountEvent(inEvent);
        }
        // Redispatch the event to original MouseWheelListener
        for (MouseWheelListener mouseWheelListener : realListeners) {
            mouseWheelListener.mouseWheelMoved(inEvent);
        }
    }

    private MouseWheelEvent createScrollAmountEvent(MouseWheelEvent inEvent) {
        // Reset the scroll amount
        MouseWheelEvent mouseWheelEvent = new MouseWheelEvent(
                inEvent.getComponent(),
                inEvent.getID(),
                inEvent.getWhen(),
                inEvent.getModifiers(),
                inEvent.getX(),
                inEvent.getY(),
                inEvent.getXOnScreen(),
                inEvent.getYOnScreen(),
                inEvent.getClickCount(),
                inEvent.isPopupTrigger(),
                inEvent.getScrollType(),
                scrollAmount,
                inEvent.getWheelRotation());
        return mouseWheelEvent;
    }
    
}
