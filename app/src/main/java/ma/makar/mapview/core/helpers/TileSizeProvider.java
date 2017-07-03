package ma.makar.mapview.core.helpers;

public interface TileSizeProvider {

    int getTileWidth();

    int getTileHeight();

    int getCountTileByWidth();

    int getCountTileByHeight();
}
