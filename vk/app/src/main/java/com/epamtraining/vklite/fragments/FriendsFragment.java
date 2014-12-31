package com.epamtraining.vklite.fragments;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.FriendsAdapter;
import com.epamtraining.vklite.bo.Friend;
import com.epamtraining.vklite.processors.FriendsProcessor;
import com.epamtraining.vklite.processors.NewsProcessor;
import com.epamtraining.vklite.processors.Processor;

import java.util.List;

public class FriendsFragment extends BoItemFragment implements  LoaderManager.LoaderCallbacks<Cursor> , DataAdapterCallback {
    private BoItemAdapter mAdapter;
    private Processor mProcessor;

    private static final String[] fields= new String[] {
            VKContentProvider.NEWS_COULMN_ID ,
            VKContentProvider.FRIEND_COLUMN_ID,VKContentProvider.FRIEND_COLUMN_FIRST_NAME
            , VKContentProvider.FRIEND_COLUMN_IMAGE_URL, VKContentProvider.FRIEND_COLUMN_LAST_NAME,
            VKContentProvider.FRIEND_COLUMN_NICK_NAME};



    public static FriendsFragment getNewFragment( ){
        FriendsFragment friendsFragment = new FriendsFragment();
        return friendsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAdapter = new FriendsAdapter(getActivity(), R.layout.post_listview_item, null, getDataFields(), null, 0);
        mProcessor = new FriendsProcessor(getActivity());
    }

    @Override
    public FragmentType getItemFragmentType() {
        return FragmentType.FRIENDFRAGMENT;
    }



    @Override
    public String[] getDataFields() {
        return fields;
    }

    @Override
    public BoItemAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public Processor getProcessor() {
        return mProcessor;
    }
}
