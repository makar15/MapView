package ma.makar.mapview.core;

import android.support.annotation.Nullable;

import ma.makar.mapview.core.loaders.TileLoadedCallback;
import ma.makar.mapview.models.Coordinate;
import ma.makar.mapview.models.Tile;

public interface TileRepository {

    void load(Coordinate coordinate, TileLoadedCallback callback);

    @Nullable
    Tile getHotTile(Coordinate coordinate);
}
