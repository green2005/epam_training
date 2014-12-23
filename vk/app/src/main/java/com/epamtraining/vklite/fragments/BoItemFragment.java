package com.epamtraining.vklite.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;

public class BoItemFragment extends android.support.v4.app.Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>, DataAdapterCallback {
    private ProgressBar mProgressBar;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BoItemAdapter mAdapter;
    private boolean mIsDataLoading = false;
    private String[] mDataFields;
    private Uri mContentUri;
    private FragmentDataProvider mDataProvider;
    private View mFooterView;

    public void init(String[] dataFields, Uri contentUri, SimpleCursorAdapter adapter, FragmentDataProvider dataProvider) {
        mDataFields = dataFields;
        mContentUri = contentUri;
        mAdapter = (BoItemAdapter) adapter;
        mDataProvider = dataProvider;
        mAdapter.onScrollStopped();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, null);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mListView = (ListView) v.findViewById(R.id.itemsList);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
               if (scrollState == SCROLL_STATE_IDLE){
                   //stopped
                   mAdapter.onScrollStopped();
                   Log.d("scroll stopped","");
               }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem>0) {
                    mAdapter.onScrollStarted();
                }
                Log.d("scroll scrolled",firstVisibleItem + ";" + visibleItemCount + ";" + totalItemCount);
                //started
               // Toast.makeText(getActivity(),"started",Toast.LENGTH_SHORT).show();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDataProvider.loadData(BoItemFragment.this, 0, null);
            }
        });
        showProgress(View.VISIBLE);
        getLoaderManager().initLoader(0, null, this);
        mFooterView = getFooterView();
        // setFooterVisible(false);
        mDataProvider.loadData(BoItemFragment.this, 0, null);
        //mAdapter = new NewsAdapter(getActivity(), R.layout.post_listview_item, null, mDataFields, null, 0);
        mAdapter.initAdapter(getActivity(), this);
        mListView.setAdapter(mAdapter);
        return v;
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
        //  setFooterVisible(false);
        mAdapter.onStop();
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        android.support.v4.content.Loader cursorLoader = new android.support.v4.content.CursorLoader(this.getActivity(),
                mContentUri, mDataFields, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        showProgress(View.GONE);
        //mIsDataLoading = false;
        //  setFooterVisible(false);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
//        mIsDataLoading = false;
    }

    public void onError(Exception e) {
        // обработка ошибок выполняется в MainActivity
        mIsDataLoading = false;
         setFooterVisible(false);
    }

    public void onBeforeStart() {
    }

    public void onDataLoaded() {
        mIsDataLoading = false;
        setFooterVisible(false);
    }

    @Override
    public void onGetMoreData(int offset, String id) {
        if (!mIsDataLoading) {
            mIsDataLoading = true;
            if (offset > 0) {
                setFooterVisible(true);
            }
            mDataProvider.loadData(BoItemFragment.this, offset, id);
        }
    }

    private View getFooterView() {
        View view = mListView.inflate(getActivity(), R.layout.footer_progress_layout, null);
        mListView.addFooterView(view);
        return view;
    }

    private void setFooterVisible(boolean isVisible) {
        if (mFooterView != null && mListView != null) {
            if (isVisible) {
                mFooterView.setVisibility(View.VISIBLE);
            } else {
                if (mListView.getFooterViewsCount() > 0) {
                    mFooterView.setVisibility(View.GONE);
                    //mListView.removeFooterView(mFooterView);
                }
                //mFooterView.setVisibility(View.GONE);
            }
        }
    }

}
