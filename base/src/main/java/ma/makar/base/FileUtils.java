package ma.makar.base;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileUtils {

    public static File getDiskCacheDir(Context context, String uniqueName) {
        boolean installMediaOrStorage = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable();

        String cachePath = installMediaOrStorage && context.getExternalCacheDir() != null
                ? context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }
}
