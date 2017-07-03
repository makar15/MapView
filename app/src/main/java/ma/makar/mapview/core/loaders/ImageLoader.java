package ma.makar.mapview.core.loaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

import ma.makar.base.CloseableUtils;

public class ImageLoader {

    private static final String TAG = "ImageLoader";
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    private final ExecutorService mExecutor;

    public ImageLoader(ExecutorService executor) {
        mExecutor = executor;
    }

    public void loadInBackground(String url, ImageLoaderCallback callback) {
        LoadBitmap loadBitmap = new LoadBitmap(url, callback);
        mExecutor.submit(loadBitmap);
    }

    public void load(String url, ImageLoaderCallback callback) {
        Log.d(TAG, "Starting loading by next url : " + url);
        InputStream input = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();
            input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            Log.d(TAG, "Successfully loading by next url : " + url);
            callback.onImageLoaded(bitmap);
        } catch (Throwable e) {
            Log.e(TAG, "Error loading new tile by next url : " + url, e);
            callback.onImageLoaded(null);
        } finally {
            CloseableUtils.close(input);
        }
    }

    public class LoadBitmap implements Runnable {

        private final String mUrl;
        private final ImageLoaderCallback mCallback;

        public LoadBitmap(String url, ImageLoaderCallback callback) {
            mUrl = url;
            mCallback = callback;
        }

        @Override
        public void run() {
            load(mUrl, mCallback);
        }
    }
}
