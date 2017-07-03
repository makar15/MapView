package ma.makar.mapview.core.helpers;

import ma.makar.mapview.Constants;
import ma.makar.mapview.models.Coordinate;

public class MailUrlCreator implements UrlCreator<Coordinate> {

    @Override
    public String getUrl(Coordinate coordinate) {
        return String.format(
                Constants.TILE_URL,
                Constants.TILE_START_POSITION_X + coordinate.getX(),
                Constants.TILE_START_POSITION_Y + coordinate.getY());
    }
}
