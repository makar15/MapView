package ma.makar.mapview.di;

import android.content.Context;

import java.util.concurrent.ExecutorService;

import ma.makar.base.ObjectPool;
import ma.makar.mapview.cache.Storage;
import ma.makar.mapview.cache.TileDiskCache;
import ma.makar.mapview.cache.TileMemoryCache;
import ma.makar.mapview.core.TilePreLoader;
import ma.makar.mapview.core.TileRepository;
import ma.makar.mapview.core.TileRepositoryImpl;
import ma.makar.mapview.core.helpers.MailTileSizeProvider;
import ma.makar.mapview.core.helpers.MailUrlCreator;
import ma.makar.mapview.core.helpers.TileSizeProvider;
import ma.makar.mapview.core.helpers.UrlCreator;
import ma.makar.mapview.core.loaders.ImageLoader;
import ma.makar.mapview.core.loaders.TileLoader;
import ma.makar.mapview.core.loaders.TileLoaderImpl;
import ma.makar.mapview.models.Coordinate;
import ma.makar.mapview.models.Tile;
import ma.makar.mapview.multithreading.ExecutorServiceBuilder;
import ma.makar.mapview.multithreading.NamedThreadFactory;
import ma.makar.mapview.utils.CoordinatePool;

public class DependencyResolver {

    private static final String THREAD_FACTORY_NAME = "TileThread";

    private final ObjectPool<Coordinate> mCoordinatePool;
    private final TileRepository mTileRepository;
    private final TileSizeProvider mTileResource;
    private final TilePreLoader mTilePreLoader;

    public static DependencyResolver create(Context context) {
        return new DependencyResolver(context);
    }

    private DependencyResolver(Context context) {
        ExecutorService executorService = new ExecutorServiceBuilder()
                .setThreadFactory(new NamedThreadFactory(THREAD_FACTORY_NAME))
                .build();

        ImageLoader imageLoader = new ImageLoader(executorService);
        UrlCreator<Coordinate> urlCreator = new MailUrlCreator();

        mCoordinatePool = new CoordinatePool();

        Storage<Coordinate, Tile> tileDiskCache = new TileDiskCache(context);
        Storage<Coordinate, Tile> tileMemoryCache = new TileMemoryCache(mCoordinatePool);
        TileLoader tileLoader = new TileLoaderImpl(imageLoader, urlCreator);

        mTileRepository = new TileRepositoryImpl(tileLoader, tileDiskCache, tileMemoryCache);
        mTileResource = new MailTileSizeProvider();
        mTilePreLoader = new TilePreLoader(mTileRepository, mCoordinatePool, executorService);
    }

    public ObjectPool<Coordinate> getCoordinatePool() {
        return mCoordinatePool;
    }

    public TileRepository getTileRepository() {
        return mTileRepository;
    }

    public TilePreLoader getTilePreLoader() {
        return mTilePreLoader;
    }

    public TileSizeProvider getTileResource() {
        return mTileResource;
    }

}
