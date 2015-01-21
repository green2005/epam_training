package com.epamtraining.vklite.fragments.itemsWidgetHolder;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.imageLoader.ImageLoader;

//TODO rename
public class ListViewWidgetHolder implements CollectionViewWrapper {
    private ImageLoader mImageLoader;
    private ListView mListView;
    private View mFooterView;

    //TODO move to abstract class
    public ListViewWidgetHolder(int widgetResourceId, @Nullable View parentView, ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        mListView = (ListView) parentView.findViewById(widgetResourceId);
        if (mListView == null) {
            throw new IllegalArgumentException(String.format("Could not find resource with id %d ", widgetResourceId));
        }
        //TODO move to abstract template class
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    mImageLoader.resumeLoadingImages();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > 0) {
                    mImageLoader.pauseLoadingImages();
                }
            }
        });
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        //TODO getCollectionView()
        mListView.setOnItemClickListener(onItemClickListener);
    }

    //TODO change to setFooter or change logic with footer member
    @Override
    public void addFooterView(View view) {
        if (view != null) {
            //OR if (mFooterView != null) {
            // throw new IlleagalStateException("footer already added")
            //}
            mFooterView = view;
            mListView.addFooterView(view);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        if ((visibility != View.VISIBLE) && (visibility != View.INVISIBLE) &&
                (visibility != View.GONE)) {
            throw new IllegalArgumentException("Unknown visibility parameter");
        }
        //TODO move to common class
        mListView.setVisibility(visibility);
    }

    @Override
    public void setAdapter(BoItemAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    @Override
    public void setFooterVisible(boolean isVisible) {
        if (mFooterView != null) {
            if (isVisible) {
                mFooterView.setVisibility(View.VISIBLE);
                mListView.setFooterDividersEnabled(true);
            } else {
                if (mListView.getFooterViewsCount() > 0) {
                    mFooterView.setVisibility(View.GONE);
                    mListView.setFooterDividersEnabled(false);
                }
            }
        }
    }

}
