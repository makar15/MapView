package ma.makar.mapview.utils;

import ma.makar.base.ObjectPool;
import ma.makar.mapview.models.Coordinate;

public class CoordinatePool extends ObjectPool<Coordinate> {

    private static final int MIN_COORDINATES = 100;
    private static final int MAX_COORDINATES = 300;
    private static final long VALIDATION_INTERVAL_TO_SEC = 5;

    public CoordinatePool() {
        super(MIN_COORDINATES, MAX_COORDINATES, VALIDATION_INTERVAL_TO_SEC);
    }

    @Override
    protected Coordinate createObject() {
        return new Coordinate();
    }
}
