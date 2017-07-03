package ma.makar.mapview.cache;

public interface Storage<T, E> {

    boolean save(T key, E value);

    E get(T key);

    void remove(T key);

    void clear();
}
