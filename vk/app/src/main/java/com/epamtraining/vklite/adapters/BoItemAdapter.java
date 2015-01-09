package com.epamtraining.vklite.adapters;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.epamtraining.vklite.imageLoader.ImageLoader;

public abstract class BoItemAdapter extends SimpleCursorAdapter {
    private ImageLoader mImageLoader;
    private DataAdapterCallback mGetDataCallBack;

    public BoItemAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    public void initAdapter(DataAdapterCallback callback, ImageLoader imageLoader){
        mImageLoader = imageLoader;
        mGetDataCallBack = callback;
    }

    @Override
    public int getCount() {
        if (getCursor() != null)
            return getCursor().getCount();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return getCursor();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    protected ImageLoader getImageLoader(){
        return mImageLoader;
    }

    protected void loadMoreData(int offset, String nextId){
        mGetDataCallBack.onGetMoreData(offset, nextId);
    }

    protected void populateImageView(ImageView imageView, String imageUrl){
        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setVisibility(View.GONE);
        } else {
            if (getImageLoader() != null) {
                imageView.setVisibility(View.VISIBLE);
                getImageLoader().loadImage(imageView, imageUrl);
            }
        }
    }

    public void onStop(){
        mImageLoader.stopLoadingImages();
    };
}