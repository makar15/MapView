package ma.makar.mapview.multithreading;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

    public final String name;

    public NamedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(name);
        return thread;
    }
}
