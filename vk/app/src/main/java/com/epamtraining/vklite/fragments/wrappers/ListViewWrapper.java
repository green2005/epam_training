package com.epamtraining.vklite.fragments.wrappers;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.loader.ImageLoader;

public class ListViewWrapper
        extends CollectionViewWrapper<ListView, BoItemAdapter, AdapterView.OnItemClickListener, AbsListView.OnScrollListener> {
    private View mFooterView;


    public ListViewWrapper(int widgetResourceId, View parentView, ImageLoader imageLoader) {
        super(widgetResourceId, parentView, imageLoader);
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        getCollectionView().setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void setFooterView(View view) {
        if (view != null) {
            if (mFooterView != null) {
                getCollectionView().removeFooterView(mFooterView);
            }
            mFooterView = view;
            getCollectionView().addFooterView(view);
        }
    }

    @Override
    public void setAdapter(BoItemAdapter adapter) {
        getCollectionView().setAdapter(adapter);
    }

    @Override
    public void setFooterVisible(boolean isVisible) {
        if (mFooterView != null) {
            if (isVisible) {
                mFooterView.setVisibility(View.VISIBLE);
                getCollectionView().setFooterDividersEnabled(true);
            } else {
                if (getCollectionView().getFooterViewsCount() > 0) {
                    mFooterView.setVisibility(View.GONE);
                    getCollectionView().setFooterDividersEnabled(false);
                }
            }
        }
    }

    @Override
    public void initOnScrollListener() {
        getCollectionView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    getImageLoader().resumeLoadingImages();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    getImageLoader().pauseLoadingImages();
                }
            }
        });
    }
}
