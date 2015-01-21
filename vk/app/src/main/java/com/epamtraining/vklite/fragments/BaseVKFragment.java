package com.epamtraining.vklite.fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.fragments.itemsWidgetHolder.CollectionViewWrapper;
import com.epamtraining.vklite.imageLoader.ImageLoader;
import com.epamtraining.vklite.processors.Processor;

//TODO create one more level BaseFragment without collectionview wrapper
//+Fragment with CollectionViewWrapper
//  - ListView fragment
//  - RecyclerView fragment
//  etc.
public abstract class BaseVKFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, DataAdapterCallback,
        Refreshable {
    private enum DataState {LOADING, NO_MORE_DATA, BROWSING}


    private View mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageLoader mImageLoader;
    private DataState mDataState = DataState.BROWSING;
    private DataSource mDataSource;
    private CollectionViewWrapper mItemsWidgetHolder;

    public abstract String[] getDataFields();

    public abstract BoItemAdapter getAdapter();

    public abstract Processor getProcessor();

    public abstract String getDataUrl(int offset, String next_id);

    public abstract Uri getContentsUri();

    public abstract int getLoaderId();

    public abstract CollectionViewWrapper getItemsWidgetHolder(View parentView, ImageLoader imageLoader);


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceId(), null);
        mImageLoader = ImageLoader.get(getActivity());
        mProgressBar = getProgressBar(view);
        mItemsWidgetHolder = getItemsWidgetHolder(view, mImageLoader);
        initFooterProgressView();
        initSwipeLayout(view);
        showProgress(View.VISIBLE);
        getLoaderManager().initLoader(getLoaderId(), null, this);
        //TODO wrap in refresh() method
        loadData(0, null);
        BoItemAdapter mAdapter = getAdapter();
        mAdapter.initAdapter(this, mImageLoader);
        mItemsWidgetHolder.setAdapter(mAdapter);
        onAfterCreateView(view);
        return view;
    }

    //could be overriden in child classes
    protected void onAfterCreateView(View view) {

    }

    //could be overriden in child classes
    protected SwipeRefreshLayout getSwipeRefreshLayout(View parentView) {
        return (SwipeRefreshLayout) parentView.findViewById(R.id.swipe_container);
    }

    //could be overriden in child classes
    protected View getFooterProgressView() {
        //TODO getActivity can return null
        return getActivity().getLayoutInflater().inflate(R.layout.footer_progress_layout, null);
    }

    //could be overriden in child classes
    protected View getProgressBar(View parentView) {
        return parentView.findViewById(R.id.progress);
    }

    //could be overriden in child classes
    protected int getLayoutResourceId() {
        return R.layout.fragment_list;
    }

    //could be overriden in child classes
    protected String getCursorLoaderSelection() {
        return null;
    }

    //could be overriden in child classes
    protected String[] getCursorLoaderSelectionArgs() {
        return null;
    }

    private void showProgress(int visibility) {
        if (mProgressBar == null) {
            return;
        }
        if (visibility == View.VISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mItemsWidgetHolder.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mItemsWidgetHolder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //TODO looks ok why commented? remove
        // mImageLoader.stopLoadingImages();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader cursorLoader = new CursorLoader(this.getActivity(),
                getContentsUri(), getDataFields(), getCursorLoaderSelection(), getCursorLoaderSelectionArgs(), null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        getAdapter().swapCursor(cursor);
        showProgress(View.GONE);
        loadDone();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getAdapter().swapCursor(null);
        //TODO some magic, check
        if ((!isDetached()) && (!isRemoving())) {
            loadDone();
        }
    }

    private void loadDone() {
        mItemsWidgetHolder.setFooterVisible(false);
        if (mSwipeRefreshLayout != null) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void onError(Exception e) {
        loadDone();
        //TODO create wrapper for log

        //TODO remove instance of and move this logic up
        if (e instanceof RuntimeException) {
            Log.e("Exception", e.getMessage());
            e.printStackTrace();
        }
        ErrorHelper.showError(getActivity(), e);
    }

    public void refreshData() {
        mDataState = DataState.BROWSING;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        //TODO move
        loadData(0, null);
    }

    @Override
    public void onGetMoreData(int offset, String id) {
        if (mDataState == DataState.BROWSING) {
            if (offset > 0) {
                mItemsWidgetHolder.setFooterVisible(true); //setFooterVisible(true);
            }
            loadData(offset, id);
        }
    }

    private void loadData(int offset, String id) {
        if (mDataState == DataState.BROWSING) {
            String url = getDataUrl(offset, id);
            if (mDataSource == null) {
                createDataSource();
            }
            mDataState = DataState.LOADING;
            mDataSource.fillData(url, getActivity());
        }
    }

    private void createDataSource() {
        DataSource.DataSourceCallbacks callBacks = new DataSource.DataSourceCallbacks() {
            @Override
            public void onError(final Exception e) {
                BaseVKFragment.this.onError(e);
                mDataState = DataState.BROWSING; //if error occures we give another chance to load data
            }

            @Override
            public void onLoadEnd(int recordsFetched) {
                if (recordsFetched == 0) {
                    mDataState = DataState.NO_MORE_DATA;
                    mItemsWidgetHolder.setFooterVisible(false);
                } else {
                    mDataState = DataState.BROWSING;
                }
                loadDone();
            }

            @Override
            public void onBeforeStart() {

            }
        };
        mDataSource = new DataSource(getProcessor(), callBacks);
    }

    private void initFooterProgressView() {
        View view = getFooterProgressView();
        mItemsWidgetHolder.addFooterView(view);
    }

    private void initSwipeLayout(View parentView) {
        mSwipeRefreshLayout = getSwipeRefreshLayout(parentView);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (mDataState == DataState.NO_MORE_DATA) {
                        mDataState = DataState.BROWSING;
                    }
                    loadData(0, null);
                }
            });
        }
    }

}

