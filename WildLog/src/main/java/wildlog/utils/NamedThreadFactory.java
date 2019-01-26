package wildlog.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class NamedThreadFactory implements ThreadFactory {
    private String name;

    public NamedThreadFactory(String inName) {
        name = inName;
    }

    @Override
    public Thread newThread(Runnable inRunnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(inRunnable);
        thread.setName(name);
        return thread;
    }

}
