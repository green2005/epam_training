package com.epamtraining.vklite.fragments;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.AdapterView;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.activities.MainActivity;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.db.FriendDBHelper;
import com.epamtraining.vklite.db.VKContentProvider;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.FriendsAdapter;
import com.epamtraining.vklite.processors.FriendsProcessor;
import com.epamtraining.vklite.processors.Processor;

public class FriendsFragment extends BoItemFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        DataAdapterCallback  {
    private BoItemAdapter mAdapter;
    private Processor mProcessor;
    private int requestCode = -1;

    private static final String[] fields =  FriendDBHelper.fields;


    public static FriendsFragment getNewFragment(Bundle bundle) {
        FriendsFragment friendsFragment = new FriendsFragment();
        if (bundle != null) {
            friendsFragment.setArguments(bundle);
        }
        return friendsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.containsKey(MainActivity.FRAGMENT_REQUEST)) {
                requestCode = arguments.getInt(MainActivity.FRAGMENT_REQUEST);
            }
        }
        mAdapter = new FriendsAdapter(getActivity(), R.layout.item_post, null, getDataFields(), null, 0);
        mProcessor = new FriendsProcessor(getActivity());
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

    @Override
    public String getDataUrl(int offset, String next_id) {
        return Api.getFriendsUrl(getActivity());
    }

    @Override
    public Uri getContentsUri() {
        return FriendDBHelper.CONTENT_URI;
    }

    @Override
    public int getLoaderId() {
        return LoaderManagerIds.FRIENDS.getId();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = mAdapter.getCursor();
        if (cursor != null) {
            if (requestCode == MainActivity.REQUEST_CODE_CHOOSE_FRIEND) {
                cursor.moveToPosition(position);
                String userId = CursorHelper.getString(cursor, FriendDBHelper.ID);
                String firstName =  CursorHelper.getString(cursor, FriendDBHelper.FIRST_NAME);
                String lastName =  CursorHelper.getString(cursor, FriendDBHelper.LAST_NAME);
                Intent intent = new Intent();
                intent.putExtra(FriendDBHelper.ID, userId);
                intent.putExtra(FriendDBHelper.FIRST_NAME, firstName);
                intent.putExtra(FriendDBHelper.LAST_NAME, lastName);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }
    }
}
