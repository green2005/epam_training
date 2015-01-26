package com.epamtraining.vklite.adapters;

import android.database.Cursor;

import com.epamtraining.vklite.imageloader.ImageLoader;

public interface SwappableAdapter {
    public Cursor swapCursor(Cursor cursor);
    public void initAdapter(DataAdapterCallback callback, ImageLoader imageLoader);
}
