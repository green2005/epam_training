package com.epamtraining.vklite.adapters;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.DisplayMetrics;

import com.epamtraining.vklite.imageLoader.ImageLoader;

public abstract class BoItemAdapter extends SimpleCursorAdapter {
    private ImageLoader mImageLoader;
    private DataAdapterCallback mGetDataCallBack;
    private int mImageWidth;

    public BoItemAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    public void initAdapter(Activity activity, DataAdapterCallback callback, ImageLoader imageLoader){
        mImageLoader = imageLoader;
        mGetDataCallBack = callback;
        setMaxImageSize(activity);
    }

    private void setMaxImageSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        mImageWidth = Math.min(screenHeight, screenWidth);
    }

    protected ImageLoader getImageLoader(){
        return mImageLoader;
    }

    protected void loadMoreData(int offset, String nextId){
        mGetDataCallBack.onGetMoreData(offset, nextId);
    }



    public void onStop(){
        mImageLoader.stopLoadingImages();
    };
}
