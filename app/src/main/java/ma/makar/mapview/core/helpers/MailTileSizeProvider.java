package ma.makar.mapview.core.helpers;

import ma.makar.mapview.Constants;

public class MailTileSizeProvider implements TileSizeProvider {

    @Override
    public int getTileWidth() {
        return Constants.TILE_SIZE_X;
    }

    @Override
    public int getTileHeight() {
        return Constants.TILE_SIZE_Y;
    }

    @Override
    public int getCountTileByWidth() {
        return Constants.COUNT_TILE_X;
    }

    @Override
    public int getCountTileByHeight() {
        return Constants.COUNT_TILE_Y;
    }
}
