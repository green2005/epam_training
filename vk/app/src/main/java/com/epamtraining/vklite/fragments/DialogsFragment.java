package com.epamtraining.vklite.fragments;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.activities.MainActivity;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.db.DialogDBHelper;
import com.epamtraining.vklite.db.FriendDBHelper;
import com.epamtraining.vklite.db.NewsDBHelper;
import com.epamtraining.vklite.db.UsersDBHelper;
import com.epamtraining.vklite.db.VKContentProvider;
import com.epamtraining.vklite.activities.ChooseFriendActivity;
import com.epamtraining.vklite.activities.MessagesActivity;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.DialogsAdapter;
import com.epamtraining.vklite.processors.DialogsProcessor;
import com.epamtraining.vklite.processors.Processor;

public class DialogsFragment extends BaseVKListViewFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, DataAdapterCallback  {

    //TODO UPPERCASE
    private static final String[] fields = new String[]{
            DialogDBHelper.USER_ID, DialogDBHelper.BODY,
            DialogDBHelper.DATE, DialogDBHelper.ID,
            DialogDBHelper.TITLE, UsersDBHelper.NAME,
            UsersDBHelper.IMAGE,
            UsersDBHelper.ID
    };

    private BoItemAdapter mAdapter;
    private Processor mProcessor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO maybe create abstract method createAdapter()?
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
                    String userId = data.getStringExtra(FriendDBHelper.ID);
                    String firstName = data.getStringExtra(FriendDBHelper.FIRST_NAME);
                    String lastName = data.getStringExtra(FriendDBHelper.LAST_NAME);
                    String userName = (firstName + " " + lastName).trim();
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
    public String getDataUrl(int offset, String /*TODO rename*/next_id) {
        //TODO String.valueOf()
        return Api.getDialogsUrl(getActivity(), offset + "");
    }

    @Override
    public Uri getContentsUri() {
        return DialogDBHelper.CONTENT_URI;
    }

    @Override
    public int getLoaderId() {
        return LoaderManagerIds.DIALOGS.getId();
    }

    public static DialogsFragment getNewFragment() {
        //TODO check idea warnings
        DialogsFragment dialogsFragment = new DialogsFragment();
        return dialogsFragment;
    }

    private void startMessagesActivity(String userId, String userName) {
        Intent intent = new Intent(this.getActivity(), MessagesActivity.class);
        intent.putExtra(UsersDBHelper.ID, userId);
        intent.putExtra(UsersDBHelper.NAME, userName);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = mAdapter.getCursor();
        if (cursor != null) {
                cursor.moveToPosition(position);
                //String messageId = mAdapter.getCursor().getString(mAdapter.getCursor().getColumnIndex(VKContentProvider.DIALOGS_COLUMN_MESSAGE_ID));
                String userId = CursorHelper.getString(cursor, DialogDBHelper.USER_ID);
                String userName = CursorHelper.getString(cursor, UsersDBHelper.NAME);
                startMessagesActivity(userId, userName);
        }
    }
}
