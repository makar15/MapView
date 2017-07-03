package ma.makar.base;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

public class CloseableUtils {

    private static final String TAG = "CloseableUtils";

    public static boolean close(@Nullable Closeable closeable) {
        if (closeable == null) {
            Log.w(TAG, "Stream is null Object");
            return false;
        }

        try {
            closeable.close();
            Log.d(TAG, "Successfully closed : " + closeable.getClass());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error on closing : " + closeable.getClass());
            return false;
        }
    }
}
