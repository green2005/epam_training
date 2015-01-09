package com.epamtraining.vklite.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.MessagesAdapter;
import com.epamtraining.vklite.commiters.Commiter;
import com.epamtraining.vklite.commiters.CommiterCallback;
import com.epamtraining.vklite.commiters.MessageCommiter;
import com.epamtraining.vklite.processors.MessagesProcessor;
import com.epamtraining.vklite.processors.Processor;

import java.util.Date;

public class MessagesFragment extends BoItemFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, DataAdapterCallback {
    private static final String[] fields = new String[]{
            VKContentProvider.MESSAGES_USER_FROM_ID,
            VKContentProvider.MESSAGES_COLUMN_BODY,
            VKContentProvider.MESSAGES_DATE,
            VKContentProvider.MESSAGES_MESSAGE_ID,
            VKContentProvider.MESSAGES_ID,
            VKContentProvider.MESSAGES_IMAGE_URL,
            VKContentProvider.USERS_COLUMN_NAME,
            VKContentProvider.USERS_COLUMN_IMAGE,
            VKContentProvider.MESSAGES_OUT
    };

    private static final String MESSAGE_EDIT = "msgEdit";

    private BoItemAdapter mAdapter;
    private Processor mProcessor;
    private String mUserId;
    private  ListView mMessagesListView;
    private EditText msgEdit;
    private ImageButton mSendBtn;

    public static MessagesFragment getNewFragment(Bundle bundle ){
        MessagesFragment messagesFragment = new MessagesFragment();
        messagesFragment.setArguments(bundle);
        return messagesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MessagesAdapter(getActivity(), R.layout.item_post, null, getDataFields(), null, 0);
        mProcessor = new MessagesProcessor(getActivity());
        Bundle b = getArguments();
        if (b != null){
            if (b.containsKey(VKContentProvider.USERS_COLUMN_ID)) {
                mUserId = b.getString(VKContentProvider.USERS_COLUMN_ID);
            }
            if (b.containsKey(VKContentProvider.USERS_COLUMN_NAME)){
                String userName = b.getString(VKContentProvider.USERS_COLUMN_NAME);
                String title = getResources().getString(R.string.messages)+" "+userName;
                getActivity().setTitle(title);
            }
        }
    }

    @Override
    public FragmentType getItemFragmentType() {
        return FragmentType.MESSAGESFRAGMENT;
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
        return Api.getMessagesUrl(getActivity(), offset+"", mUserId);
    }

    protected void onAfterCreateView(View view) {
       super.onAfterCreateView(view);
        mMessagesListView = (ListView)view.findViewById(R.id.itemsList);
        msgEdit = (EditText)view.findViewById(R.id.messageedit);
        mSendBtn = (ImageButton)view.findViewById(R.id.btnsend);
        mSendBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msgEdit.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    msgEdit.setText("");
                    sendMessage(message);
                }
            }
        });
        if (mMessagesListView != null){
            mMessagesListView.setDividerHeight(0);
            mMessagesListView.setItemsCanFocus(false);
            mMessagesListView.setFocusable(false);
            mMessagesListView.setFocusableInTouchMode(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(MESSAGE_EDIT)){
                msgEdit.setText(savedInstanceState.getString(MESSAGE_EDIT));
            }
        } else
        {
            SharedPreferences prefs = getActivity().getSharedPreferences(MESSAGE_EDIT, Context.MODE_PRIVATE);
            String s = prefs.getString(mUserId,"");
            if (!TextUtils.isEmpty(s)){
                msgEdit.setText(s);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(mUserId);
                editor.commit();
            }
        }
    }

    private void sendMessage(String message){
        ContentValues values = new ContentValues();
        String currentUserId = Api.getUserId(getActivity());
        values.put(VKContentProvider.MESSAGES_USER_FROM_ID, currentUserId);
        values.put(VKContentProvider.MESSAGES_USER_ID, mUserId);
        values.put(VKContentProvider.MESSAGES_OUT, "1");
        Date date = new Date();
        long rawDate = date.getTime()/1000;
        values.put(VKContentProvider.MESSAGES_RAW_DATE, rawDate);
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
        String cDate = dateFormat.format(date);
        values.put(VKContentProvider.MESSAGES_DATE, cDate);
        values.put(VKContentProvider.MESSAGES_PENDING,"1");
        values.put(VKContentProvider.MESSAGES_COLUMN_BODY, message);
        getActivity().getContentResolver().insert(VKContentProvider.MESSAGES_CONTENT_URI, values);
        getActivity().getContentResolver().notifyChange(VKContentProvider.MESSAGES_CONTENT_URI, null);
        commitPendingMessages();
    }

    private void commitPendingMessages(){
        CommiterCallback commiterCallback = new CommiterCallback() {
            @Override
            public void onAfterExecute() {
                getActivity().getContentResolver().notifyChange(VKContentProvider.MESSAGES_CONTENT_URI, null);
            }

            @Override
            public void onException(Exception e) {
                ErrorHelper.showError(getActivity(), e);
                getActivity().getContentResolver().notifyChange(VKContentProvider.MESSAGES_CONTENT_URI, null);
            }
        };
        Commiter messageCommiter = new MessageCommiter(commiterCallback, getActivity());
        messageCommiter.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!TextUtils.isEmpty(msgEdit.getText().toString())) {
            SharedPreferences prefs = getActivity().getSharedPreferences(MESSAGE_EDIT, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(mUserId, msgEdit.getText().toString());
            editor.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(msgEdit.getText().toString())){
            outState.putString(MESSAGE_EDIT, msgEdit.getText().toString());
        }
    }

    protected int getLayoutResourceId(){
        return R.layout.fragment_messages;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //
    }
}
