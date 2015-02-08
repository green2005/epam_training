package com.epamtraining.vklite.adapters;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;

import com.epamtraining.vklite.loader.ImageLoader;

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
        implements SwappableAdapter {

    private Cursor mCursor;

    private boolean mDataValid;

    private int mRowIdColumn;

    private DataSetObserver mDataSetObserver;

    private ImageLoader mImageLoader;

    private DataAdapterCallback mGetDataCallBack;

    public CursorRecyclerViewAdapter(ImageLoader imageLoader) {
        if (imageLoader == null) {
            throw new IllegalArgumentException("ImageLoader parameter is null");
        }
        mDataValid = false;
        mImageLoader = imageLoader;
        mDataSetObserver = new RecycleObserver();
    }

    @Override
    public void initAdapter(DataAdapterCallback callback, ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        mGetDataCallBack = callback;
    }

    protected void loadMoreData(int offset, String nextId) {
        mGetDataCallBack.onGetMoreData(offset, nextId);
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow(BaseColumns._ID);
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    protected ImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    private class RecycleObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
        }
    }
}
