package ma.makar.mapview.core.loaders;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

import ma.makar.mapview.models.Tile;

public interface TileLoadedCallback {

    @MainThread
    void onLoaded(@Nullable Tile tile);
}
