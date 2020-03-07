package wildlog.utils;

import java.awt.Dimension;
import java.awt.Label;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.logging.log4j.Level;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;
import wildlog.WildLogApp;
import wildlog.ui.dialogs.utils.UtilsDialog;


public final class UtilsConcurency {

    private UtilsConcurency() {
    }

    public static void kickoffProgressbarTask(WildLogApp inApp, Task inTask) {
        ApplicationContext appContext = inApp.getContext();
        TaskMonitor taskMonitor = appContext.getTaskMonitor();
        TaskService taskService = appContext.getTaskService();
        taskService.execute(inTask);
        taskMonitor.setForegroundTask(inTask);
        taskMonitor.setAutoUpdateForegroundTask(false);
    }

    /**
     * This method will block the current thread and wait to try and <b>shutdown the ExecutorService</b> gracefully.
     * If it fails it will try to force the shutdown.
     * Tasks will be allowed about 120 minutes to complete, which should really be enough unless something went very wrong.
     * @param inExecutorService
     * @return true if successful, or false if terminated early
     */
    public static boolean waitForExecutorToShutdown(ExecutorService inExecutorService) {
        if (inExecutorService != null) {
            inExecutorService.shutdown();
            try {
                final int TOTAL_RETRIES_ALLOWED = 24;
                int count = 0;
                while(!inExecutorService.awaitTermination(10, TimeUnit.MINUTES) && count < TOTAL_RETRIES_ALLOWED) {
                    count++;
                    WildLogApp.LOGGER.log(Level.WARN, "ExecutorService expired while shutting down... Retry: {} of {}", new Object[]{count, TOTAL_RETRIES_ALLOWED});
                }
                if (!inExecutorService.isTerminated()) {
                    List<Runnable> terminationList  = inExecutorService.shutdownNow();
                    if (terminationList == null) {
                        terminationList = new ArrayList<>(0);
                    }
                    WildLogApp.LOGGER.log(Level.ERROR, "ExecutorService SHUTDOWN ERROR... Unexecuted tasks remaining: {}", terminationList.size());
                    return false;
                }
                else {
                    WildLogApp.LOGGER.log(Level.INFO, "ExecutorService shutdown correctly...");
                }
            }
            catch (InterruptedException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                return false;
            }
        }
        return true;
    }

    /**
     * <b>NOTE: This method is not currently used, so it might be buggy, see waitForExecutorToRunTasksWithPopup() instead.</b>
     * This method will block the current thread and wait to try and shutdown the ExecutorService gracefully.
     * <b>The ExecutorService will be shutdown</b>, if it fails it will try to force the shutdown.
     * Tasks will be allowed about 60 minutes to complete, which should really be enough unless something went very wrong. <br/>
     * <b>In addition this method will show a popup message (semi-modal) if the task is taking longer than about 1 second.
     * @param inExecutorService - The service to stop and wait for.
     * @param inParent - If the parent is a JDialog pass it in, otherwise use null to use the application's main frame.
     * @return
     */
    @Deprecated
    public static boolean waitForExecutorToShutdownWithPopup(final ExecutorService inExecutorService, JDialog inParent) {
        if (inExecutorService != null) {
            inExecutorService.shutdown();
            try {
                // Do the initial short wait
                if (!inExecutorService.awaitTermination(1150, TimeUnit.MILLISECONDS)) {
                    // If we are still running the popup should be displayed and then continue the work
                    final JDialog popup;
                    if (inParent != null && inParent instanceof JDialog) {
                        popup = new JDialog(inParent, "Long Running Process", false);
                        popup.setVisible(false);
                        UtilsDialog.addModalBackgroundPanel(inParent, popup);
                    }
                    else {
                        popup = new JDialog(WildLogApp.getApplication().getMainFrame(), "Long Running Process", false);
                        popup.setVisible(false);
                        UtilsDialog.addModalBackgroundPanel(WildLogApp.getApplication().getMainFrame(), popup);
                    }
                    popup.setEnabled(false);
                    ImageIcon icon = new ImageIcon(WildLogApp.class.getResource("resources/icons/WildLog Icon.gif"));
                    popup.setIconImage(icon.getImage());
                    popup.add(new Label("  Busy processing. Please wait..."));
                    popup.setMinimumSize(new Dimension(250, 100));
                    popup.pack();
                    if (inParent instanceof JDialog) {
                        UtilsDialog.setDialogToCenter(inParent, popup);
                    }
                    else {
                        UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), popup);
                    }
                    popup.setVisible(true);
                    // Continue trying to finish the work
                    final ResultWrapper resultWrapper = new ResultWrapper(true);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            final int TOTAL_RETRIES_ALLOWED = 10;
                            int count = 0;
                            try {
                                while(!inExecutorService.awaitTermination(6, TimeUnit.MINUTES) && count < TOTAL_RETRIES_ALLOWED) {
                                    count++;
                                    WildLogApp.LOGGER.log(Level.INFO, "ExecutorService expired while shutting down... Retry: {} of {}", new Object[]{count, TOTAL_RETRIES_ALLOWED});
                                }
                            }
                            catch (InterruptedException ex) {
                                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                                resultWrapper.result = false;
                            }
                            if (!inExecutorService.isTerminated()) {
                                List<Runnable> terminationList  = inExecutorService.shutdownNow();
                                if (terminationList == null) {
                                    terminationList = new ArrayList<>(0);
                                }
                                WildLogApp.LOGGER.log(Level.ERROR, "ExecutorService Shutdown Error... Unexecuted tasks remaining: {}", terminationList.size());
                                resultWrapper.result = false;
                            }
                            popup.setVisible(false);
                            popup.dispose();
                        }
                    });
                    if (resultWrapper.result == false) {
                        return false;
                    }
                }
            }
            catch (InterruptedException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                return false;
            }
        }
        return true;
    }
    
    /**
     * This method will block the current thread and wait to try and run all the submitted tasks. The ExecutorService is not shutdown.
     * If the timeout expires it will cancel all remaining tasks and log an error.
     * Tasks will be allowed about 10 minutes to complete, which should really be enough unless something went very wrong.
     * @param <T>
     * @param inExecutorService
     * @param inTaskList
     * @return
     */
    public static <T> boolean waitForExecutorToRunTasks(ExecutorService inExecutorService, Collection<? extends Callable<T>> inTaskList) {
        if (inExecutorService != null) {
            try {
                List<Future<T>> listResults = inExecutorService.invokeAll(inTaskList, 30, TimeUnit.MINUTES);
                for (Future<T> future : listResults) {
                    if (future.isCancelled()) {
                        WildLogApp.LOGGER.log(Level.ERROR, "ExecutorService Error... Due to the timeout some tasks were cancled.", 
                                new Exception("STACKTRACE of current ExecutorService:"));
                        return false;
                    }
                }
            }
            catch (CancellationException | InterruptedException | RejectedExecutionException ex) {
                WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
                return false;
            }
        }
        return true;
    }

    /**
     * This method will block the current thread and wait to try and run all the submitted tasks. 
     * <b>The ExecutorService is not shutdown.</b>
     * If the timeout expires it will cancel all remaining tasks and log an error.
     * Tasks will be allowed about 10 minutes to complete, which should really be enough unless something went very wrong. <br/>
     * <b>In addition this method will show a popup message (semi-modal) if the task is taking longer than about 1 second.
     * @param <T>
     * @param inExecutorService - The service to stop and wait for.
     * @param inTaskList - The tasks to run
     * @param inRunWhenDone - Code that needs to run after the SwingWorker is done (to update the UI)
     * @param inParentContainer - If the parent is a JDialog or JFrame pass it in, otherwise use null to use the application's main frame.
     * @param inMessage - The message to show in the popup
     * @return
     */
    public static <T> boolean waitForExecutorToRunTasksWithPopup(final ExecutorService inExecutorService, 
            final Collection<? extends Callable<T>> inTaskList, final Runnable inRunWhenDone, 
            final RootPaneContainer inParentContainer, final String inMessage) {
        // Create the popup
        JDialog popup;
        if (inParentContainer instanceof JDialog) {
            popup = new JDialog((JDialog) inParentContainer, "Long Running Process", true);
            popup.setVisible(false);
        }
        else
        if (inParentContainer instanceof JFrame) {
            popup = new JDialog((JFrame) inParentContainer, "Long Running Process", true);
            popup.setVisible(false);
        }
        else {
            popup = new JDialog(WildLogApp.getApplication().getMainFrame(), "Long Running Process", false);
            popup.setVisible(false);
        }
        popup.setEnabled(false);
        ImageIcon icon = new ImageIcon(WildLogApp.class.getResource("resources/icons/WildLog Icon.gif"));
        popup.setIconImage(icon.getImage());
        popup.add(new Label(inMessage));
        popup.setMinimumSize(new Dimension(250, 100));
        popup.pack();
        // Create a swing worker that will do the running
        SwingWorker swingWorkerUpload = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                boolean result = waitForExecutorToRunTasks(inExecutorService, inTaskList);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        popup.setVisible(false);
                        popup.dispose();
                    }
                });
                return result;
            }

            @Override
            protected void done() {
                super.done();
                if (inRunWhenDone != null) {
                    SwingUtilities.invokeLater(inRunWhenDone);
                }
            }
        };
        swingWorkerUpload.execute();
        try {
            // Lock the GUI thread until the popup appears to prevent further user actions
            return ((Boolean) swingWorkerUpload.get(1150, TimeUnit.MILLISECONDS));
        }
        catch (InterruptedException | ExecutionException ex) {
            WildLogApp.LOGGER.log(Level.ERROR, ex.toString(), ex);
        }
        catch (TimeoutException ex) {
            // The upload is taking long, so show the popup
            if (inParentContainer instanceof JDialog) {
                UtilsDialog.setDialogToCenter((JDialog) inParentContainer, popup);
                UtilsDialog.addModalBackgroundPanel(inParentContainer, popup);
            }
            else
            if (inParentContainer instanceof JFrame) {
                UtilsDialog.setDialogToCenter((JFrame) inParentContainer, popup);
                UtilsDialog.addModalBackgroundPanel(inParentContainer, popup);
            }
            else {
                UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), popup);
                UtilsDialog.addModalBackgroundPanel(WildLogApp.getApplication().getMainFrame(), popup);
            }
            popup.setVisible(true);
        }
        return false;
    }

    private static class ResultWrapper {
        public boolean result;

        protected ResultWrapper(boolean result) {
            this.result = result;
        }

    }

}
