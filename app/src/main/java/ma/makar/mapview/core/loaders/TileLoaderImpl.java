package ma.makar.mapview.core.loaders;

import ma.makar.mapview.core.helpers.UrlCreator;
import ma.makar.mapview.models.Coordinate;

public class TileLoaderImpl implements TileLoader {

    private final ImageLoader mImageLoader;
    private final UrlCreator<Coordinate> mUrlCreator;

    public TileLoaderImpl(ImageLoader imageLoader, UrlCreator<Coordinate> urlCreator) {
        mImageLoader = imageLoader;
        mUrlCreator = urlCreator;
    }

    @Override
    public void loadTile(Coordinate coordinate, ImageLoaderCallback callback){
        mImageLoader.loadInBackground(mUrlCreator.getUrl(coordinate), callback);
    }
}
