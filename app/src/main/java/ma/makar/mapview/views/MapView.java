package ma.makar.mapview.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import ma.makar.base.ObjectPool;
import ma.makar.mapview.core.TilePreLoader;
import ma.makar.mapview.core.TileRepository;
import ma.makar.mapview.core.helpers.MapViewUpdateCriterion;
import ma.makar.mapview.core.helpers.TileSizeProvider;
import ma.makar.mapview.core.loaders.TileLoadedCallback;
import ma.makar.mapview.models.Coordinate;
import ma.makar.mapview.models.Tile;

public class MapView extends View {

    private static final String TAG = "MapView";
    private static final int EXTRA_VISIBLE_COUNT_TILE = 1;

    private MapViewUpdateCriterion mMapViewUpdateCriterion;
    private TileRepository mTileRepository;
    private TilePreLoader mTilePreLoader;
    private ObjectPool<Coordinate> mCoordinatePool;

    private final GestureDetector mGestureDetector = new GestureDetector(getContext(),
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    if (!mIsAttached) {
                        Log.w(TAG, "Not attached to View");
                        return true;
                    }
                    mMapViewUpdateCriterion.stop();
                    translateView(-distanceX, -distanceY);
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    initiateLoadVisibleTiles();
                    return super.onSingleTapUp(e);
                }
            });

    private final TileLoadedCallback mTileLoadedCallback = new TileLoadedCallback() {
        @Override
        public void onLoaded(@Nullable Tile tile) {
            if (tile != null && mIsAttached) {
                mMapViewUpdateCriterion.onTileLoaded();
            }
        }
    };

    private final MapViewUpdateCriterion.MapViewUpdateListener mMapViewUpdateListener =
            new MapViewUpdateCriterion.MapViewUpdateListener() {
                @Override
                public void onReady() {
                    postInvalidate();
                }
            };

    private int mTileWidth;
    private int mTileHeight;
    private int mCountTileByWidth;
    private int mCountTileByHeight;

    private int mFirstVisibleColumn;
    private int mLastVisibleColumn;
    private int mFirstVisibleRow;
    private int mLastVisibleRow;
    private boolean mIsAttached = false;

    private RectF mFirstRectDrawTile;
    private RectF mMapRect;
    private RectF mFrameRect;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mFrameRect = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        initiateLoadVisibleTiles();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsAttached) {
            Log.w(TAG, "Not attached to View");
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            mMapViewUpdateCriterion.start();
            initiateLoadVisibleTiles();
        }
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mIsAttached) {
            Log.w(TAG, "Not attached to View");
            return;
        }

        int positionTileX = mFirstVisibleColumn;
        int positionTileY = mFirstVisibleRow;

        float startDrawX = mFirstRectDrawTile.left;
        float startDrawY = mFirstRectDrawTile.top;

        float endDrawX = Math.min(mFrameRect.right, mMapRect.right);
        float endDrawY = Math.min(mFrameRect.bottom, mMapRect.bottom);

        while (startDrawY < endDrawY) {
            while (startDrawX < endDrawX) {
                Coordinate coordinate = mCoordinatePool.getObject();
                coordinate.setX(positionTileX);
                coordinate.setY(positionTileY);
                Tile tile = mTileRepository.getHotTile(coordinate);
                if (tile != null && tile.isNotEmpty()) {
                    canvas.drawBitmap(tile.bitmap, startDrawX, startDrawY, null);
                }

                positionTileX += 1;
                startDrawX = startDrawX + mTileWidth;
            }

            positionTileX = mFirstVisibleColumn;
            positionTileY += 1;

            startDrawX = mFirstRectDrawTile.left;
            startDrawY = startDrawY + mTileHeight;
        }
    }

    public void attachToView(TileRepository repository,
                             TilePreLoader tilePreLoader,
                             TileSizeProvider resource,
                             ObjectPool<Coordinate> coordinatePool) {
        mMapViewUpdateCriterion = new MapViewUpdateCriterion(mMapViewUpdateListener);
        mTileRepository = repository;
        mTilePreLoader = tilePreLoader;
        mCoordinatePool = coordinatePool;

        mTileWidth = resource.getTileWidth();
        mTileHeight = resource.getTileHeight();
        mCountTileByWidth = resource.getCountTileByWidth();
        mCountTileByHeight = resource.getCountTileByHeight();

        if (mTileWidth <= 0 || mTileHeight <= 0 || mCountTileByWidth <= 0 || mCountTileByHeight <= 0) {
            throw new IllegalArgumentException("Invalid tile settings");
        }

        mMapRect = new RectF(0, 0, mTileWidth * mCountTileByWidth, mTileHeight * mCountTileByHeight);
        mFirstRectDrawTile = new RectF();
        mIsAttached = true;
        Log.d(TAG, "On attach to View");
    }

    private void translateView(float dx, float dy) {
        if (!mFrameRect.contains(mMapRect)) {
            mMapRect.left += dx;
            mMapRect.right += dx;
            mMapRect.top += dy;
            mMapRect.bottom += dy;
            checkMoveBounds();
        }

        updateVisibleValue();
        postInvalidate();
    }

    private void checkMoveBounds() {
        float diff = mMapRect.left - mFrameRect.left;
        if (diff > 0) {
            mMapRect.left -= diff;
            mMapRect.right -= diff;

        }
        diff = mMapRect.right - mFrameRect.right;
        if (diff < 0) {
            mMapRect.left -= diff;
            mMapRect.right -= diff;
        }
        diff = mMapRect.top - mFrameRect.top;
        if (diff > 0) {
            mMapRect.top -= diff;
            mMapRect.bottom -= diff;
        }
        diff = mMapRect.bottom - mFrameRect.bottom;
        if (diff < 0) {
            mMapRect.top -= diff;
            mMapRect.bottom -= diff;
        }
    }

    private int getColumnTile(float screenX) {
        return (int) (screenX - mMapRect.left) / mTileWidth;
    }

    private int getRowTile(float screenY) {
        return (int) (screenY - mMapRect.top) / mTileHeight;
    }

    private RectF getFrameBoundsTile(RectF source, int raw, int column) {
        source.set(0, 0, mTileWidth, mTileHeight);
        source.offsetTo(raw * mTileWidth + mMapRect.left, column * mTileHeight + mMapRect.top);
        return source;
    }

    private void initiateLoadVisibleTiles() {
        if (!mIsAttached) {
            Log.w(TAG, "Not attached to View");
            return;
        }

        updateVisibleValue();
        int startByWidth = mFirstVisibleColumn - EXTRA_VISIBLE_COUNT_TILE;
        if (startByWidth < 0) {
            startByWidth = 0;
        }
        int endByWidth = mLastVisibleColumn + EXTRA_VISIBLE_COUNT_TILE;
        if (endByWidth >= mCountTileByWidth) {
            endByWidth = mCountTileByWidth - 1;
        }
        int startByHeight = mFirstVisibleRow - EXTRA_VISIBLE_COUNT_TILE;
        if (startByHeight < 0) {
            startByHeight = 0;
        }
        int endByHeight = mLastVisibleRow + EXTRA_VISIBLE_COUNT_TILE;
        if (endByHeight >= mCountTileByHeight) {
            endByHeight = mCountTileByHeight - 1;
        }

        mTilePreLoader.initiateLoading(startByWidth, endByWidth, startByHeight, endByHeight,
                mTileLoadedCallback);
    }

    private void updateVisibleValue() {
        mFirstVisibleColumn = getColumnTile(mFrameRect.left);
        mFirstVisibleRow = getRowTile(mFrameRect.top);
        mLastVisibleColumn = getColumnTile(mFrameRect.right);
        mLastVisibleRow = getRowTile(mFrameRect.bottom);
        mFirstRectDrawTile = getFrameBoundsTile(mFirstRectDrawTile,
                mFirstVisibleColumn, mFirstVisibleRow);
    }
}
