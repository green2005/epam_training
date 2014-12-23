package com.epamtraining.vklite.fragments;


import android.app.Activity;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.FriendsAdapter;
import com.epamtraining.vklite.bo.Friend;

import java.util.List;

public class FriendsFragment extends BoItemFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> , DataAdapterCallback {
    private List<Friend> items;
    private FragmentDataProvider mProvider;
    private ListView mLvItems;
    private ProgressBar mProgressBar;
    private FriendsAdapter adapter;
    private String mToken;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final String[] fields= new String[] {
            VKContentProvider.NEWS_COULMN_ID ,
            VKContentProvider.FRIEND_COLUMN_ID,VKContentProvider.FRIEND_COLUMN_FIRST_NAME
            , VKContentProvider.FRIEND_COLUMN_IMAGE_URL, VKContentProvider.FRIEND_COLUMN_LAST_NAME,
            VKContentProvider.FRIEND_COLUMN_NICK_NAME};




    public static Fragment getFriendsFragment(Activity activity, FragmentDataProvider dataProvider)  {
        FriendsFragment fragment = new FriendsFragment();
        FriendsAdapter adapter =  new FriendsAdapter(activity, R.layout.post_listview_item, null, fields, null, 0);
        fragment.init(fields, VKContentProvider.FRIENDS_CONTENT_URI, adapter, dataProvider);
        return fragment;
    }


  }
