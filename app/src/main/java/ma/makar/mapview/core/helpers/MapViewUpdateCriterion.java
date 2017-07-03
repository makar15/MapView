package ma.makar.mapview.core.helpers;

public class MapViewUpdateCriterion {

    public interface MapViewUpdateListener {
        void onReady();
    }

    private final MapViewUpdateListener mUpdateListener;
    private boolean isStopped;

    public MapViewUpdateCriterion(MapViewUpdateListener updateListener) {
        mUpdateListener = updateListener;
    }

    public void onTileLoaded() {
        if (!isStopped) {
            mUpdateListener.onReady();
        }
    }

    public void stop() {
        isStopped = true;
    }

    public void start() {
        isStopped = false;
        mUpdateListener.onReady();
    }
}
