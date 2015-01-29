package com.epamtraining.vklite.fragments.wrappers;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.epamtraining.vklite.adapters.CursorRecyclerViewAdapter;
import com.epamtraining.vklite.imageloader.ImageLoader;

public class RecyclerViewWrapper
        extends CollectionViewWrapper<RecyclerView, CursorRecyclerViewAdapter, RecyclerView.OnItemTouchListener, RecyclerView.OnScrollListener> {

    public RecyclerViewWrapper(int widgetResourceId, View parentView, ImageLoader imageLoader) {
        super(widgetResourceId, parentView, imageLoader);
    }

    @Override
    public void setFooterView(View view) {

    }

    @Override
    public void setAdapter(CursorRecyclerViewAdapter cursorRecyclerViewAdapter) {
        getCollectionView().setAdapter(cursorRecyclerViewAdapter);
    }

    @Override
    public void setFooterVisible(boolean isVisible) {

    }

    @Override
    public void initOnScrollListener() {
        //not yet implemented
        // it wasn't still in need
    }

    @Override
    public void setOnItemClickListener(RecyclerView.OnItemTouchListener onItemTouchListener) {

    }
}
