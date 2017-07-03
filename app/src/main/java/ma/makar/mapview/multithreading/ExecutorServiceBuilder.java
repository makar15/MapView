package ma.makar.mapview.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import ma.makar.base.Assert;

public class ExecutorServiceBuilder {

    private static final int DEFAULT_THREAD_POOL_SIZE = 3;
    private static final String DEFAULT_THREAD_FACTORY_NAME = "DefaultThread";

    private ThreadFactory mFactory = new NamedThreadFactory(DEFAULT_THREAD_FACTORY_NAME);
    private int mThreadPoolSize = DEFAULT_THREAD_POOL_SIZE;

    public ExecutorServiceBuilder setPoolSize(int threadPoolSize) {
        if (threadPoolSize <= 0) {
            Assert.fail("The value of threadPoolSize must be greater than 0 ");
            return this;
        }
        mThreadPoolSize = threadPoolSize;
        return this;
    }

    public ExecutorServiceBuilder setThreadFactory(ThreadFactory factory) {
        if (factory == null) {
            Assert.fail("Factory is null Object");
            return this;
        }
        mFactory = factory;
        return this;
    }

    public ExecutorService build() {
        return Executors.newFixedThreadPool(mThreadPoolSize, mFactory);
    }
}
