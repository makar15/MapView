package ma.makar.mapview.cache;

import android.util.LruCache;

import ma.makar.base.ObjectPool;
import ma.makar.mapview.models.Coordinate;
import ma.makar.mapview.models.Tile;

public class TileLruCache extends LruCache<Coordinate, Tile> {

    private final ObjectPool<Coordinate> mCoordinatePool;

    public TileLruCache(int maxSize, ObjectPool<Coordinate> coordinatePool) {
        super(maxSize);
        mCoordinatePool = coordinatePool;
    }

    @Override
    protected void entryRemoved(boolean evicted, Coordinate key, Tile oldValue, Tile newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (newValue == null) {
            mCoordinatePool.returnObject(key);
        }
    }
}
