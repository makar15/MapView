package ma.makar.mapview.models;

import android.graphics.Bitmap;

public class Tile {

    public final Coordinate coordinate;
    public final Bitmap bitmap;

    private Tile(Coordinate coordinate, Bitmap bitmap) {
        this.coordinate = coordinate;
        this.bitmap = bitmap;
    }

    public boolean isNotEmpty() {
        return coordinate != null && bitmap != null;
    }

    public static Tile getNew(Coordinate coordinate, Bitmap bitmap) {
        return new Tile(coordinate, bitmap);
    }
}
