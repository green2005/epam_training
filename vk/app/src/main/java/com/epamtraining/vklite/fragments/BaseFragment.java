package com.epamtraining.vklite.fragments;


import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.SwappableAdapter;
import com.epamtraining.vklite.fragments.wrappers.CollectionViewWrapper;
import com.epamtraining.vklite.imageloader.ImageLoader;
import com.epamtraining.vklite.processors.Processor;

//TODO create one more level BaseFragment without collectionview wrapper
//+Fragment with CollectionViewWrapper
//  - ListView fragment
//  - RecyclerView fragment
//  etc.
public abstract class BaseFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        DataAdapterCallback,
        Refreshable {
    private enum DataState {LOADING, NO_MORE_DATA, BROWSING}


    private View mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DataState mDataState = DataState.BROWSING;
    private DataSource mDataSource;
    private CollectionViewWrapper mCollectionViewWrapper;

    public abstract String[] getDataFields();

    public abstract SwappableAdapter getAdapter();

    public abstract Processor getProcessor();

    public abstract String getDataUrl(int offset, String next_id);

    public abstract Uri getContentsUri();

    public abstract int getLoaderId();

    public abstract CollectionViewWrapper newInstanceCollectionViewWrapper(View parentView, ImageLoader imageLoader);

    protected abstract CollectionViewWrapper getCollectionViewWrapper();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceId(), null);
        ImageLoader imageLoader = ImageLoader.get(getActivity());
        mProgressBar = getProgressBar(view);
        mCollectionViewWrapper = newInstanceCollectionViewWrapper(view, imageLoader);
        initFooterProgressView();
        initSwipeLayout(view);
        showProgress(View.VISIBLE);
        getLoaderManager().initLoader(getLoaderId(), null, this);
        refresh();
        SwappableAdapter mAdapter = getAdapter();
        mAdapter.initAdapter(this, imageLoader);
        mCollectionViewWrapper.setAdapter(mAdapter);
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
        Activity activity = getActivity();
        if (activity != null) {
            return activity.getLayoutInflater().inflate(R.layout.footer_progress_layout, null);
        } else {return null;}
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
            mCollectionViewWrapper.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mCollectionViewWrapper.setVisibility(View.VISIBLE);
        }
    }

    private void refresh(){
        loadData(0, null);
    }

    @Override
    public void onStop() {
        super.onStop();
     }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getActivity(),
                getContentsUri(), getDataFields(), getCursorLoaderSelection(), getCursorLoaderSelectionArgs(), null);
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
    }

    private void loadDone() {
        mCollectionViewWrapper.setFooterVisible(false);
        if (mSwipeRefreshLayout != null) {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void onError(Exception e) {
        loadDone();
        ErrorHelper.showError(getActivity(), e);
    }

    public void refreshData() {
        mDataState = DataState.BROWSING;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        refresh();
    }

    @Override
    public void onGetMoreData(int offset, String id) {
        if (mDataState == DataState.BROWSING) {
            if (offset > 0) {
                mCollectionViewWrapper.setFooterVisible(true); //setFooterVisible(true);
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
                BaseFragment.this.onError(e);
                mDataState = DataState.BROWSING; //if error occures we give another chance to load data
            }

            @Override
            public void onLoadEnd(int recordsFetched) {
                if (recordsFetched == 0) {
                    mDataState = DataState.NO_MORE_DATA;
                    mCollectionViewWrapper.setFooterVisible(false);
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
        if (view != null) {
            mCollectionViewWrapper.setFooterView(view);
        }
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
                    refresh();
                }
            });
        }
    }

}

