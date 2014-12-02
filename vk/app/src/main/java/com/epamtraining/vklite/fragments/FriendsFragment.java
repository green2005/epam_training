package com.epamtraining.vklite.fragments;


import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.bo.Friend;
import com.epamtraining.vklite.processors.FriendsProcessor;
import com.epamtraining.vklite.processors.NewsProcessor;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment implements BoItemFragment, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private List<Friend> items;
    private FragmentDataProvider mProvider;
    private ListView mLvItems;
    private ProgressBar mProgressBar;
    private FriendsAdapter adapter;
    private String mToken;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String[] fields= new String[] {
            VKContentProvider.NEWS_COULMN_ID ,
            VKContentProvider.FRIEND_COLUMN_ID,VKContentProvider.FRIEND_COLUMN_FIRST_NAME
            , VKContentProvider.FRIEND_COLUMN_IMAGE_URL, VKContentProvider.FRIEND_COLUMN_LAST_NAME,
            VKContentProvider.FRIEND_COLUMN_NICK_NAME};

    FriendsFragment(){
        super();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    public static Fragment getFriendsFragment(FragmentDataProvider provider, String token)  {
        FriendsFragment friends = new FriendsFragment();
        friends.setToken(token);
        friends.setProvider(provider);
        return friends;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, null);

        items =new ArrayList<Friend>();
        mProgressBar = (ProgressBar)v.findViewById(R.id.progress);
        mLvItems = (ListView) v.findViewById(R.id.itemsList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               refreshContent();
            }
        });
        showProgress(View.VISIBLE);
        adapter = new FriendsAdapter(getActivity(),R.layout.news_listview_item,null, fields, null, 0);
        mLvItems.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
        refreshContent();
        //mProvider.fillData(items, this);
      return v;
    }

    private void setToken(String token){
        mToken = token;
    }

    @Override
    public void onError(Exception e) {
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        showProgress(View.GONE);
    }

    private void setProvider(FragmentDataProvider provider){
        mProvider = provider;
    }

    @Override
    public void onDataLoaded() {
        showProgress(View.GONE);
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        adapter.notifyDataSetChanged();
    }

    private void showProgress(int visible){
        mProgressBar.setVisibility(visible);
        if (mProgressBar.getVisibility() == View.VISIBLE){
           mLvItems.setVisibility(View.GONE);
        } else
        {mLvItems.setVisibility(View.VISIBLE);}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
       // Toast.makeText(getActivity(),"onPause",Toast.LENGTH_SHORT);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    private void refreshContent(){
        FriendsProcessor friendsProcessor = new FriendsProcessor(mToken, getActivity());
        DataSource ds = new DataSource(friendsProcessor,  new DataSource.DataSourceCallbacks() {
            @Override
            public void onError(Exception e) {
                //TODO доделать
            }

            @Override
            public void onLoadEnd() {
                getLoaderManager().restartLoader(0, null, FriendsFragment.this);
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


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        android.support.v4.content.Loader  cursorLoader = new android.support.v4.content.CursorLoader(this.getActivity(),
                VKContentProvider.FRIENDS_CONTENT_URI, fields, null, null, null);
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
}
