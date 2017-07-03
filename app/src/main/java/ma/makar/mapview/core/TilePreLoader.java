package ma.makar.mapview.core;

import java.util.concurrent.ExecutorService;

import ma.makar.base.ObjectPool;
import ma.makar.mapview.core.loaders.TileLoadedCallback;
import ma.makar.mapview.models.Coordinate;

public class TilePreLoader {

    private final TileRepository mRepository;
    private final ObjectPool<Coordinate> mCoordinatePool;
    private final ExecutorService mExecutor;

    public TilePreLoader(TileRepository repository,
                         ObjectPool<Coordinate> coordinatePool,
                         ExecutorService executor) {
        mRepository = repository;
        mCoordinatePool = coordinatePool;
        mExecutor = executor;
    }

    public void initiateLoading(final int startX, final int endX, final int startY, final int endY,
                                final TileLoadedCallback callback) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = startX; i <= endX; i++) {
                    for (int j = startY; j <= endY; j++) {
                        Coordinate coordinate = mCoordinatePool.getObject();
                        coordinate.setX(i);
                        coordinate.setY(j);
                        if (mRepository.getHotTile(coordinate) == null) {
                            mRepository.load(coordinate, callback);
                        }
                    }
                }
            }
        });
    }
}
