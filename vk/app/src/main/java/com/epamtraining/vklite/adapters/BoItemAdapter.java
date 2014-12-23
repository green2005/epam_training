package com.epamtraining.vklite.adapters;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;

import com.epamtraining.vklite.imageLoader.ImageLoader;

public abstract class BoItemAdapter extends SimpleCursorAdapter {
    private ImageLoader mImageLoader;

    public BoItemAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mImageLoader = ImageLoader.getImageLoader(context);
    }

    public void onScrollStopped(){
        mImageLoader.resumeLoadingImages();
    }

    public void onScrollStarted(){
        mImageLoader.pauseLoadingImages();
    }

    public ImageLoader getImageLoader(){
        return mImageLoader;
    }

    public abstract void initAdapter(Activity activity, DataAdapterCallback callback);
    public abstract void onStop();
}
