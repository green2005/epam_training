package com.epamtraining.vklite.fragments.wrappers;

import android.view.View;

import com.epamtraining.vklite.loader.ImageLoader;

public abstract class CollectionViewWrapper<CollectionView extends View, Adapter, OnItemClickListener, OnScrollListener> {
    private CollectionView mCollectionView;
    private ImageLoader mImageLoader;

    public abstract void setFooterView(View view);

    public abstract void setAdapter(Adapter adapter);

    public abstract void setFooterVisible(boolean isVisible);

    public abstract void initOnScrollListener();

    public abstract void setOnItemClickListener(OnItemClickListener onItemClickListener);


    CollectionViewWrapper(int widgetResourceId, View parentView, ImageLoader imageLoader) {
        if (parentView == null) {
            throw new IllegalArgumentException("parentView parameter cannot be null");
        }

        if (imageLoader == null) {
            throw new IllegalArgumentException("imageLoader parameter cannot be null");
        }
        mImageLoader = imageLoader;
        View view = parentView.findViewById(widgetResourceId);
        if (view == null) {
            throw new IllegalArgumentException(String.format("Could not find resource with Id %d ", widgetResourceId));
        }
        mCollectionView = (CollectionView) view;
        initOnScrollListener();
    }

    public CollectionView getCollectionView() {
        return mCollectionView;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void setVisibility(int visibility) {
        if ((visibility != View.VISIBLE) && (visibility != View.INVISIBLE) &&
                (visibility != View.GONE)) {
            throw new IllegalArgumentException("Unknown visibility parameter");
        }
        getCollectionView().setVisibility(visibility);
    }


}



