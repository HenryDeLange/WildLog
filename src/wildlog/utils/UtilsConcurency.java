package wildlog.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;
import wildlog.WildLogApp;


public class UtilsConcurency {

    public static void kickoffProgressbarTask(WildLogApp inApp, Task inTask) {
        ApplicationContext appContext = inApp.getContext();
        TaskMonitor taskMonitor = appContext.getTaskMonitor();
        TaskService taskService = appContext.getTaskService();
        taskService.execute(inTask);
        taskMonitor.setForegroundTask(inTask);
        taskMonitor.setAutoUpdateForegroundTask(false);
    }

    public static boolean tryAndWaitToShutdownExecutorService(ExecutorService inExecutorService) {
        inExecutorService.shutdown();
        try {
            int count = 0;
            while(!inExecutorService.awaitTermination(6, TimeUnit.MINUTES) && count < 10) {
                count++;
                System.out.println("Bulk Upload: Timer expired while loading images... Resetting... " + count);
            }
            if (!inExecutorService.isTerminated()) {
                System.err.println("Bulk Upload Error: Terminating bulk import... " + count);
                inExecutorService.shutdownNow();
                return false;
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
        return true;
    }

}
