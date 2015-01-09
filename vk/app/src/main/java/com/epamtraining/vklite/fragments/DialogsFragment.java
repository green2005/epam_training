package com.epamtraining.vklite.fragments;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.MainActivity;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.activities.ChooseFriendActivity;
import com.epamtraining.vklite.activities.MessagesActivity;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.DialogsAdapter;
import com.epamtraining.vklite.processors.DialogsProcessor;
import com.epamtraining.vklite.processors.Processor;

public class DialogsFragment extends BoItemFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, DataAdapterCallback {

    private static final String[] fields = new String[]{
            VKContentProvider.DIALOGS_COLUMN_USER_ID, VKContentProvider.DIALOGS_COLUMN_BODY,
            VKContentProvider.DIALOGS_COLUMN_DATE, VKContentProvider.DIALOGS_COLUMN_MESSAGE_ID,
            VKContentProvider.DIALOGS_COLUMN_TITLE, VKContentProvider.USERS_COLUMN_NAME,
            VKContentProvider.USERS_COLUMN_IMAGE,
            VKContentProvider.DIALOGS_COLUMN_ID
    };

    private BoItemAdapter mAdapter;
    private Processor mProcessor;


    @Override
    public FragmentType getItemFragmentType() {
        return FragmentType.DIALOGSFRAGMENT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DialogsAdapter(getActivity(), R.layout.item_post, null, getDataFields(), null, 0);
        mProcessor = new DialogsProcessor(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dialogs, menu);
        MenuItem item = menu.findItem(R.id.action_editmessage);
        if (item != null) {
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(getActivity(), ChooseFriendActivity.class);
                    startActivityForResult(intent, MainActivity.REQUEST_CODE_CHOOSE_FRIEND);
                    return true;
                }
            });
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case (MainActivity.REQUEST_CODE_CHOOSE_FRIEND): {
                    String userId = data.getStringExtra(VKContentProvider.FRIEND_COLUMN_ID);
                    String firstName = data.getStringExtra(VKContentProvider.FRIEND_COLUMN_FIRST_NAME);
                    String lastName = data.getStringExtra(VKContentProvider.FRIEND_COLUMN_LAST_NAME);
                    String userName = (firstName +" " +lastName).trim();
                    if (!TextUtils.isEmpty(userId)) {
                        startMessagesActivity(userId, userName);
                    }
                    break;
                }
            }
        }
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
        return Api.getDialogsUrl(getActivity(), offset + "");
    }

    public static DialogsFragment getNewFragment() {
        DialogsFragment dialogsFragment = new DialogsFragment();
        return dialogsFragment;
    }

    private void startMessagesActivity(String userId, String userName) {
        Intent intent = new Intent(this.getActivity(), MessagesActivity.class);
        //intent.putExtra(VKContentProvider.DIALOGS_COLUMN_MESSAGE_ID, messageId);
        intent.putExtra(VKContentProvider.USERS_COLUMN_ID, userId);
        intent.putExtra(VKContentProvider.USERS_COLUMN_NAME, userName);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter.getCursor() != null) {
            mAdapter.getCursor().moveToPosition(position);
            //String messageId = mAdapter.getCursor().getString(mAdapter.getCursor().getColumnIndex(VKContentProvider.DIALOGS_COLUMN_MESSAGE_ID));
            String userId = mAdapter.getCursor().getString(mAdapter.getCursor().getColumnIndex(VKContentProvider.USERS_COLUMN_ID));
            String userName = mAdapter.getCursor().getString(mAdapter.getCursor().getColumnIndex(VKContentProvider.USERS_COLUMN_NAME));
            startMessagesActivity(userId, userName);
        }
    }
}
