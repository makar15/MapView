package ma.makar.mapview.models;

public class Coordinate {

    private int mX;
    private int mY;

    public Coordinate() {
    }

    public Coordinate(int x, int y) {
        mX = x;
        mY = y;
    }

    public void setX(int x) {
        mX = x;
    }

    public void setY(int y) {
        mY = y;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "X = " + mX +
                ", Y = " + mY +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Coordinate that = (Coordinate) o;
        return mX == that.mX && mY == that.mY;
    }

    @Override
    public int hashCode() {
        return  31 * mX + mY;
    }

}
