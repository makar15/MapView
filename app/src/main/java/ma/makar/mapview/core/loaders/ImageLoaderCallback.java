package ma.makar.mapview.core.loaders;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public interface ImageLoaderCallback {

    void onImageLoaded(@Nullable Bitmap bitmap);
}
