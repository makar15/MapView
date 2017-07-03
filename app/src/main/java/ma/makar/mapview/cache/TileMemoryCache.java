package ma.makar.mapview.cache;

import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;

import ma.makar.base.Assert;
import ma.makar.base.ObjectPool;
import ma.makar.mapview.models.Coordinate;
import ma.makar.mapview.models.Tile;

public class TileMemoryCache implements Storage<Coordinate, Tile> {

    private static final String TAG = "TileMemoryCache";
    private static final int SHARE_FREE_MEMORY_FOR_CACHE = 4;

    private final LruCache<Coordinate, Tile> mMemoryCache;

    public TileMemoryCache(ObjectPool<Coordinate> coordinatePool) {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / SHARE_FREE_MEMORY_FOR_CACHE;

        mMemoryCache = new TileLruCache(cacheSize, coordinatePool) {
            @Override
            protected int sizeOf(Coordinate key, Tile tile) {
                return tile.isNotEmpty() ? tile.bitmap.getByteCount() / 1024 : super.sizeOf(key, tile);
            }
        };
    }

    @Override
    public boolean save(Coordinate coordinate, Tile tile) {
        Assert.assertNotNull(coordinate);
        Assert.assertNotNull(tile);
        if (get(coordinate) == null) {
            mMemoryCache.put(coordinate, tile);
            return true;
        }
        Log.w(TAG, coordinate.toString() + " already saved to memory cache");
        return false;
    }

    @Nullable
    @Override
    public Tile get(Coordinate coordinate) {
        Assert.assertNotNull(coordinate);
        return mMemoryCache.get(coordinate);
    }

    @Override
    public void remove(Coordinate coordinate) {
        Assert.assertNotNull(coordinate);
        mMemoryCache.remove(coordinate);
    }

    @Override
    public void clear() {
        mMemoryCache.evictAll();
    }
}
