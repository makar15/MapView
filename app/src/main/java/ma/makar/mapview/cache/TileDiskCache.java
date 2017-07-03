package ma.makar.mapview.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ma.makar.base.CloseableUtils;
import ma.makar.base.FileUtils;
import ma.makar.mapview.models.Coordinate;
import ma.makar.mapview.models.Tile;

public class TileDiskCache implements Storage<Coordinate, Tile> {

    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static final int COMPRESS_QUALITY = 100;
    private static final String NAME_CACHE_DIR = "tiles";

    private final File mCacheDir;

    public TileDiskCache(Context context) {
        mCacheDir = FileUtils.getDiskCacheDir(context, NAME_CACHE_DIR);
    }

    @Override
    @WorkerThread
    public boolean save(Coordinate coordinate, Tile tile) {
        File image = getFile(coordinate);
        FileOutputStream out = null;
        boolean savedSuccessfully = false;
        try {
            out = new FileOutputStream(image);
            savedSuccessfully = tile.bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, out);
        } catch (IOException ignored) {
        } finally {
            CloseableUtils.close(out);
        }
        return savedSuccessfully;
    }

    @Nullable
    @Override
    public Tile get(Coordinate coordinate) {
        File file = getFile(coordinate);
        Bitmap bitmap = null;
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return bitmap == null ? null : Tile.getNew(coordinate, bitmap);
    }

    @Override
    public void remove(Coordinate coordinate) {
        getFile(coordinate).delete();
    }

    @Override
    public void clear() {
        File[] files = mCacheDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    private String generateName(Coordinate coordinate) {
        return String.valueOf((coordinate.getX() + "." + coordinate.getY()));
    }

    private File getFile(Coordinate coordinate) {
        String name = generateName(coordinate);
        if (!mCacheDir.exists()) {
            mCacheDir.mkdir();
        }
        return new File(mCacheDir, name);
    }
}
