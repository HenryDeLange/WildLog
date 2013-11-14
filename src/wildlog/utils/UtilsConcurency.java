package wildlog.utils;

import java.awt.Dimension;
import java.awt.Label;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
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
     * This method will block the current thread and wait to try and shutdown the ExecutorService gracefully.
     * If it fails it will try to force the shutdown.
     * Tasks will be allowed about 50 minutes to complete, which should really be enough unless something went very wrong.
     * @param inExecutorService
     * @return
     */
    public static boolean waitForExecutorToShutdown(ExecutorService inExecutorService) {
        if (inExecutorService != null) {
            inExecutorService.shutdown();
            try {
                int count = 0;
                while(!inExecutorService.awaitTermination(6, TimeUnit.MINUTES) && count < 10) {
                    count++;
                    System.out.println("ExecutorService expired while shutting down... Retry: " + count);
                }
                if (!inExecutorService.isTerminated()) {
                    List<Runnable> terminationList  = inExecutorService.shutdownNow();
                    if (terminationList == null) {
                        terminationList = new ArrayList<>(0);
                    }
                    System.err.println("ExecutorService Shutdown Error... Unexecuted tasks remaining: " + terminationList.size());
                    return false;
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return true;
    }

    /**
     * This method will block the current thread and wait to try and shutdown the ExecutorService gracefully.
     * If it fails it will try to force the shutdown.
     * Tasks will be allowed about 50 minutes to complete, which should really be enough unless something went very wrong. <br/>
     * <b>In addition this method will show a popup message (semi-modal) if the task is taking longer than about 1 second.
     * @param inExecutorService - The service to stop and wait for.
     * @param inParent - If the parent is a JDialog pass it in, otherwise use null to use the application's main frame.
     * @return
     */
    public static boolean waitForExecutorToShutdownWithPopup(final ExecutorService inExecutorService, JDialog inParent) {
        if (inExecutorService != null) {
            inExecutorService.shutdown();
            try {
                // Do the initial short wait
                if (!inExecutorService.awaitTermination(1150, TimeUnit.MILLISECONDS)) {
                    // If we are still running the popup should be displayed and then continue the work
                    final JDialog popup;
                    if (inParent instanceof JDialog) {
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
                    UtilsDialog.setDialogToCenter(WildLogApp.getApplication().getMainFrame(), popup);
                    popup.setVisible(true);
                    // Continue trying to finish the work
                    final ResultWrapper resultWrapper = new ResultWrapper(true);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            int count = 0;
                            try {
                                while(!inExecutorService.awaitTermination(6, TimeUnit.MINUTES) && count < 10) {
                                    count++;
                                    System.out.println("ExecutorService expired while shutting down... Retry: " + count);
                                }
                            }
                            catch (InterruptedException ex) {
                                ex.printStackTrace(System.err);
                                resultWrapper.result = false;
                            }
                            if (!inExecutorService.isTerminated()) {
                                List<Runnable> terminationList  = inExecutorService.shutdownNow();
                                if (terminationList == null) {
                                    terminationList = new ArrayList<>(0);
                                }
                                System.err.println("ExecutorService Shutdown Error... Unexecuted tasks remaining: " + terminationList.size());
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
                ex.printStackTrace(System.err);
                return false;
            }
        }
        return true;
    }

    private static class ResultWrapper {
        public boolean result;

        protected ResultWrapper(boolean result) {
            this.result = result;
        }

    }

}
