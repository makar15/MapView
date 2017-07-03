package ma.makar.base;

import android.os.Handler;
import android.os.Looper;

public class ThreadUtils {

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            sHandler.post(runnable);
        }
    }
}
