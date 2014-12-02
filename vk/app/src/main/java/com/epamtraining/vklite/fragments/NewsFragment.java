package com.epamtraining.vklite.fragments;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.MainActivity;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.bo.News;
import com.epamtraining.vklite.os.ObjectSerializer;
import com.epamtraining.vklite.processors.FriendsProcessor;
import com.epamtraining.vklite.processors.NewsProcessor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>
{
    private List<News> mItems;
    private ProgressBar mProgressBar;
    private ListView mLvItems;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NewsAdapter adapter;
    private String mToken;
    private FragmentDataProvider mDataProvider;
    private String[] fields= new String[] { VKContentProvider.NEWS_COULMN_ID,VKContentProvider.NEWS_COLUMN_IMAGE_URL
        , VKContentProvider.NEWS_COLUMN_DATE, VKContentProvider.NEWS_COLUMN_URL, VKContentProvider.NEWS_COLUMN_TEXT};

    public static Fragment getNewsFragment(FragmentDataProvider dataProvider, String token) {
        NewsFragment fragment = new NewsFragment();
        fragment.mDataProvider = dataProvider;
        fragment.mToken = token;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, null);
        mItems = new ArrayList<News>();
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mLvItems = (ListView) v.findViewById(R.id.itemsList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        showProgress(View.VISIBLE);
        getLoaderManager().initLoader(0, null, this);
        refreshContent();

        adapter = new NewsAdapter(getActivity(),R.layout.news_listview_item,null, fields, null, 0);
        adapter.initImageSizes(getActivity());
        mLvItems.setAdapter(adapter);
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.onStop();
    }



    private void showProgress(int visibility) {
        if (visibility == View.VISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mLvItems.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mLvItems.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        android.support.v4.content.Loader  cursorLoader = new android.support.v4.content.CursorLoader(this.getActivity(),
                VKContentProvider.NEWS_CONTENT_URI, fields, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
         adapter.swapCursor(cursor);
         showProgress(View.GONE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    private void refreshContent(){
        NewsProcessor newsProcessor = new NewsProcessor(mToken, getActivity());
        DataSource ds = new DataSource(newsProcessor,  new DataSource.DataSourceCallbacks() {
            @Override
            public void onError(Exception e) {
                //TODO доделать
            }

            @Override
            public void onLoadEnd() {
                 if (!isResumed()) return;
                getLoaderManager().restartLoader(0, null, NewsFragment.this);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onBeforeStart() {

            }
        });
        ds.fillData(DataSource.DataLocation.WEB, getActivity());
    }
}
