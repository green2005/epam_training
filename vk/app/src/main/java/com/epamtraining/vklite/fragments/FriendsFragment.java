package com.epamtraining.vklite.fragments;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.AdapterView;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.MainActivity;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.FriendsAdapter;
import com.epamtraining.vklite.processors.FriendsProcessor;
import com.epamtraining.vklite.processors.Processor;

import java.nio.charset.MalformedInputException;

public class FriendsFragment extends BoItemFragment implements LoaderManager.LoaderCallbacks<Cursor>, DataAdapterCallback {
    private BoItemAdapter mAdapter;
    private Processor mProcessor;
    private int requestCode = -1;

    private static final String[] fields = new String[]{
            VKContentProvider.NEWS_COULMN_ID,
            VKContentProvider.FRIEND_COLUMN_ID, VKContentProvider.FRIEND_COLUMN_FIRST_NAME
            , VKContentProvider.FRIEND_COLUMN_IMAGE_URL, VKContentProvider.FRIEND_COLUMN_LAST_NAME,
            VKContentProvider.FRIEND_COLUMN_NICK_NAME};


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

    @Override
    public String getDataUrl(int offset, String next_id) {
        return Api.getFriendsUrl(getActivity());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter.getCursor() != null) {
            if (requestCode == MainActivity.REQUEST_CODE_CHOOSE_FRIEND) {
                mAdapter.getCursor().moveToPosition(position);
                String userId = mAdapter.getCursor().getString(mAdapter.getCursor().getColumnIndex(VKContentProvider.FRIEND_COLUMN_ID));
                String firstName = mAdapter.getCursor().getString(mAdapter.getCursor().getColumnIndex(VKContentProvider.FRIEND_COLUMN_FIRST_NAME));
                String lastName = mAdapter.getCursor().getString(mAdapter.getCursor().getColumnIndex(VKContentProvider.FRIEND_COLUMN_LAST_NAME));
                //String userName = (firstName + " " +lastName).trim();
                Intent intent = new Intent();
                intent.putExtra(VKContentProvider.FRIEND_COLUMN_ID, userId);
                intent.putExtra(VKContentProvider.FRIEND_COLUMN_FIRST_NAME, firstName);
                intent.putExtra(VKContentProvider.FRIEND_COLUMN_LAST_NAME, lastName);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }
    }
}
