package ma.makar.mapview.core;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import ma.makar.base.Assert;
import ma.makar.base.ThreadUtils;
import ma.makar.mapview.cache.Storage;
import ma.makar.mapview.core.loaders.ImageLoaderCallback;
import ma.makar.mapview.core.loaders.TileLoadedCallback;
import ma.makar.mapview.core.loaders.TileLoader;
import ma.makar.mapview.models.Coordinate;
import ma.makar.mapview.models.Tile;

public class TileRepositoryImpl implements TileRepository {

    private final TileLoader mTileLoader;
    private final Storage<Coordinate, Tile> mTileDiskCache;
    private final Storage<Coordinate, Tile> mTileMemoryCache;
    private final Map<Coordinate, TileLoadedCallback> mLoadingMap = new HashMap<>();
    private final Object mLock = new Object();

    public TileRepositoryImpl(TileLoader tileLoader,
                              Storage<Coordinate, Tile> tileDiskCache,
                              Storage<Coordinate, Tile> tileMemoryCache) {
        mTileLoader = tileLoader;
        mTileDiskCache = tileDiskCache;
        mTileMemoryCache = tileMemoryCache;

        BatchThread batchThread = new BatchThread();
        batchThread.setName(BatchThread.THREAD_NAME);
        batchThread.start();
    }

    /**
     * Checking Memory Cache, checking Disk Cache
     * otherwise, network request, then save in Caches
     */
    @Override
    public void load(final Coordinate coordinate, final TileLoadedCallback callback) {
        Assert.assertNotNull(callback);
        Tile tile = getHotTile(coordinate);
        if (tile != null && tile.isNotEmpty()) {
            loadRunOnUiThread(tile, callback);
            return;
        }

        synchronized (mLock) {
            mLoadingMap.put(coordinate, callback);
        }
    }

    @Nullable
    @Override
    public Tile getHotTile(Coordinate coordinate) {
        return mTileMemoryCache.get(coordinate);
    }

    private void loadRunOnUiThread(@Nullable final Tile tile, final TileLoadedCallback callback) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(tile);
            }
        });
    }

    private class BatchThread extends Thread {
        private static final String THREAD_NAME = "batch_thread";
        private static final String TAG = "BatchThread";
        private static final int TIME_FOR_SLEEP_TO_MILLIS = 500;

        @Override
        public void run() {
            while (!Thread.interrupted()) {

                HashMap<Coordinate, TileLoadedCallback> items;
                synchronized (mLock) {
                    items = new HashMap<>(mLoadingMap);
                }

                for (Map.Entry<Coordinate, TileLoadedCallback> entry : items.entrySet()) {
                    final Coordinate coordinate = entry.getKey();
                    final TileLoadedCallback callback = entry.getValue();

                    Tile tile = mTileDiskCache.get(coordinate);
                    if (tile != null && tile.isNotEmpty()) {
                        mTileMemoryCache.save(coordinate, tile);
                        loadRunOnUiThread(tile, callback);
                        continue;
                    }

                    mTileLoader.loadTile(coordinate, new ImageLoaderCallback() {
                        @Override
                        public void onImageLoaded(@Nullable final Bitmap bitmap) {
                            if (bitmap == null) {
                                Log.w(TAG, "Loaded bitmap is null Object");
                                loadRunOnUiThread(null, callback);
                                return;
                            }
                            Tile newTile = Tile.getNew(coordinate, bitmap);
                            mTileMemoryCache.save(coordinate, newTile);
                            loadRunOnUiThread(newTile, callback);
                            mTileDiskCache.save(coordinate, newTile);
                        }
                    });
                }

                mLoadingMap.clear();
                try {
                    Thread.sleep(TIME_FOR_SLEEP_TO_MILLIS);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Thread was interrupted");
                }
            }
        }
    }
}
