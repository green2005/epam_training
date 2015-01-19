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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.db.CommentsDBHelper;
import com.epamtraining.vklite.imageLoader.ImageLoader;
import com.epamtraining.vklite.processors.Processor;

//TODO split, create 2-3 abstraction
public abstract class BaseVKListViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, DataAdapterCallback,
        AdapterView.OnItemClickListener, Refreshable {
    private enum DataState {LOADING, NO_MORE_DATA, BROWSING}

    ;
    private ProgressBar mProgressBar;
    //TODO will be new one with RecyclerView
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mFooterView;
    private ImageLoader mImageLoader;
    private DataState mDataState = DataState.BROWSING;
    private DataSource mDataSource;

    public abstract String[] getDataFields();

    public abstract BoItemAdapter getAdapter();

    public abstract Processor getProcessor();

    public abstract String getDataUrl(int offset, String next_id);

    public abstract Uri getContentsUri();

    public abstract int getLoaderId();


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutResourceId(), null);
        mImageLoader = ImageLoader.get(getActivity());
        if (mImageLoader.getIsPaused()) {
            mImageLoader.resumeLoadingImages();
        }
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        //TODO do not map to ListView
        mListView = (ListView) v.findViewById(R.id.itemsList);
        mListView.setOnItemClickListener(this);
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
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mDataState == DataState.NO_MORE_DATA) {
                    mDataState = DataState.BROWSING;
                }
                loadData(0, null);
            }
        });

        showProgress(View.VISIBLE);
        getLoaderManager().initLoader(getLoaderId(), null, this);
        mFooterView = getFooterView();
        loadData(0, null);
        BoItemAdapter mAdapter = getAdapter();
        mAdapter.initAdapter(this, mImageLoader);
        mListView.setAdapter(mAdapter);
        onAfterCreateView(v);
        return v;
    }

    protected void onAfterCreateView(View view) {

    }

    protected int getLayoutResourceId() {
        return R.layout.fragment_list;
    }

    private void showProgress(int visibility) {
        if (visibility == View.VISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // mImageLoader.stopLoadingImages();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader cursorLoader = new CursorLoader(this.getActivity(),
                getContentsUri(), getDataFields(), getSelection(), getSelectionArgs(), null);
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
        if ((!isDetached()) && (!isRemoving())) {
            loadDone();
        }
    }

    protected String getSelection(){
        return null;
    }

    protected String[] getSelectionArgs(){
        return null;
    }

    private void loadDone() {
        setFooterVisible(false);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void onError(Exception e) {
        loadDone();
        if (e instanceof RuntimeException) {
            Log.e("Exception",e.getMessage());
            e.printStackTrace();
            ErrorHelper.showError(getActivity(), e);
            //throw new Exception(e);
        } else {
            ErrorHelper.showError(getActivity(), e);
        }
    }

    public void refreshData() {
        mDataState = DataState.BROWSING;
        mSwipeRefreshLayout.setRefreshing(true);
        loadData(0, null);
    }

    @Override
    public void onGetMoreData(int offset, String id) {
        if (mDataState == DataState.BROWSING) {
            if (offset > 0) {
                setFooterVisible(true);
            }
            loadData(offset, id);
        }
    }

    private void loadData(int offset, String id) {
        if (mDataState == DataState.BROWSING
                ) {
            //TODO REMOVE this magic (top request)
            //we need it because should clear cache when retrieving data for the first time
            //and not to clear it in the case of pagination
            // it's used in NewsProcessor, WallProcessor, MessagesProcessor etc..

            getProcessor().setIsTopRequest(offset == 0);
            String url = getDataUrl(offset, id); //getItemFragmentType().getDataUrl(getActivity(), offset, id);
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
                BaseVKListViewFragment.this.onError(e);
                mDataState = DataState.BROWSING; //if error occures we give another chance to load data
            }

            @Override
            public void onLoadEnd(int recordsFetched) {
                if (recordsFetched == 0) {
                    mDataState = DataState.NO_MORE_DATA;
                    setFooterVisible(false);
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


    private View getFooterView() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.footer_progress_layout, null);
        mListView.addFooterView(view);
        return view;
    }

    private void setFooterVisible(boolean isVisible) {
        //TODO change to local variable
        ListView listView = mListView;
        if (listView != null && mFooterView != null) {
            if (isVisible) {
                mFooterView.setVisibility(View.VISIBLE);
                listView.setFooterDividersEnabled(true);
            } else {
                if (listView.getFooterViewsCount() > 0) {
                    mFooterView.setVisibility(View.GONE);
                    listView.setFooterDividersEnabled(false);
                }
            }
        }
    }

}

