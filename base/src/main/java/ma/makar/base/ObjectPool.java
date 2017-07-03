package ma.makar.base;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class ObjectPool<T> {

    private final ConcurrentLinkedQueue<T> mPool;

    public ObjectPool(final int minObjects, final int maxObjects, long validationInterval) {
        mPool = new ConcurrentLinkedQueue<>();
        addObjects(minObjects);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable poolSizeUpdater = new PoolSizeUpdater(minObjects, maxObjects);
        executorService.scheduleWithFixedDelay(poolSizeUpdater, validationInterval,
                validationInterval, TimeUnit.SECONDS);
    }

    protected abstract T createObject();

    public T getObject() {
        T object = mPool.poll();
        if (object == null) {
            object = createObject();
        }
        return object;
    }

    public void returnObject(T object) {
        if (object == null) {
            return;
        }
        mPool.offer(object);
    }

    private void addObjects(int sizeToBeAdded) {
        for (int i = 0; i < sizeToBeAdded; i++) {
            mPool.add(createObject());
        }
    }

    private void pullObjects(int sizeToBeRemoved) {
        for (int i = 0; i < sizeToBeRemoved; i++) {
            mPool.poll();
        }
    }

    private class PoolSizeUpdater implements Runnable {

        private final int mMinObjects;
        private final int mMaxObjects;

        PoolSizeUpdater(int minObjects, int maxObjects) {
            mMinObjects = minObjects;
            mMaxObjects = maxObjects;
        }

        @Override
        public void run() {
            int size = mPool.size();

            if (size < mMinObjects) {
                int sizeToBeAdded = mMinObjects + size;
                addObjects(sizeToBeAdded);
            } else if (size > mMaxObjects) {
                int sizeToBeRemoved = size - mMaxObjects;
                pullObjects(sizeToBeRemoved);
            }
        }
    }
}
