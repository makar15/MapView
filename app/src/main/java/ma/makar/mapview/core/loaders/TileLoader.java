package ma.makar.mapview.core.loaders;

import ma.makar.mapview.models.Coordinate;

public interface TileLoader {

    void loadTile(Coordinate coordinate, ImageLoaderCallback callback);
}
