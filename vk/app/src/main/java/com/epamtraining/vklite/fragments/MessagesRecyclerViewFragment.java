package com.epamtraining.vklite.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKApplication;
import com.epamtraining.vklite.adapters.CursorRecyclerViewAdapter;
import com.epamtraining.vklite.adapters.MessagesAdapter;
import com.epamtraining.vklite.adapters.SwappableAdapter;
import com.epamtraining.vklite.commiters.Commiter;
import com.epamtraining.vklite.commiters.CommiterCallback;
import com.epamtraining.vklite.commiters.MessageCommiter;
import com.epamtraining.vklite.db.MessagesDBHelper;
import com.epamtraining.vklite.db.UIQueryHelper;
import com.epamtraining.vklite.db.UsersDBHelper;
import com.epamtraining.vklite.imageloader.ImageLoader;
import com.epamtraining.vklite.processors.MessagesProcessor;
import com.epamtraining.vklite.processors.Processor;

import java.util.Date;

public class MessagesRecyclerViewFragment extends BaseRecyclerViewFragment {
    private static final String[] FIELDS = new String[]{
            MessagesDBHelper.FROM_ID,
            MessagesDBHelper.BODY,
            MessagesDBHelper.DATE,
            MessagesDBHelper.ID,
            BaseColumns._ID,
            MessagesDBHelper.IMAGE_URL,
            UsersDBHelper.NAME,
            UsersDBHelper.IMAGE,
            MessagesDBHelper.USER_DIALOG_ID,
            MessagesDBHelper.OUT
    };

    private static final String MESSAGE_EDIT = "msgEdit";

    private CursorRecyclerViewAdapter mAdapter;
    private Processor mProcessor;
    private String mUserId;
    private EditText msgEdit;

    public static MessagesRecyclerViewFragment getNewFragment(Bundle bundle) {
        MessagesRecyclerViewFragment messagesFragment = new MessagesRecyclerViewFragment();
        messagesFragment.setArguments(bundle);
        return messagesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();

        Bundle b = getArguments();
        if (b != null) {
            if (b.containsKey(UsersDBHelper.ID)) {
                mUserId = b.getString(UsersDBHelper.ID);
            }
            if (b.containsKey(UsersDBHelper.NAME)) {
                String userName = b.getString(UsersDBHelper.NAME);
                String title = String.format("%s %s", getResources().getString(R.string.messages), userName);
                getActivity().setTitle(title);
            }
        }

        mAdapter = new MessagesAdapter(activity, ImageLoader.get(activity));
        mProcessor = new MessagesProcessor(getActivity());
    }

    @Override
    protected String getCursorLoaderSelection() {
        return MessagesDBHelper.USER_DIALOG_ID + " = ? ";
    }

    @Override
    protected String[] getCursorLoaderSelectionArgs() {
        return new String[]{mUserId};//super.getCursorLoaderSelectionArgs();
    }

    @Override
    protected void onAfterCreateView(View view) {
        super.onAfterCreateView(view);
        msgEdit = (EditText) view.findViewById(R.id.messageedit);
        ImageButton mSendBtn = (ImageButton) view.findViewById(R.id.btnsend);
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
    }

    @Override
    public String[] getDataFields() {
        return FIELDS;
    }

    @Override
    public SwappableAdapter getAdapter() {
        return mAdapter;
    }


    @Override
    public Processor getProcessor() {
        return mProcessor;
    }

    @Override
    public String getDataUrl(int offset, String next_id) {
        return Api.getMessagesUrl(getActivity(), String.valueOf(offset), mUserId);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_messages_recycleview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(msgEdit.getText().toString())) {
            outState.putString(MESSAGE_EDIT, msgEdit.getText().toString());
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(MESSAGE_EDIT)) {
                msgEdit.setText(savedInstanceState.getString(MESSAGE_EDIT));
            }
        } else {
            SharedPreferences prefs = getActivity().getSharedPreferences(MESSAGE_EDIT, Context.MODE_PRIVATE);
            String s = prefs.getString(mUserId, "");
            if (!TextUtils.isEmpty(s)) {
                msgEdit.setText(s);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(mUserId);
                editor.apply();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!TextUtils.isEmpty(msgEdit.getText().toString())) {
            SharedPreferences prefs = getActivity().getSharedPreferences(MESSAGE_EDIT, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(mUserId, msgEdit.getText().toString());
            editor.apply();
        }
    }

    private void commitPendingMessages() {
        final Activity activity = getActivity();
        if (activity == null){
            return;
        }
        final ContentResolver resolver = activity.getContentResolver();
        if (resolver == null){
            return;
        }
        CommiterCallback commiterCallback = new CommiterCallback() {
            @Override
            public void onAfterExecute() {
                resolver.notifyChange(MessagesDBHelper.CONTENT_URI, null);
            }

            @Override
            public void onException(Exception e) {
                ErrorHelper.showError(activity, e);
                resolver.notifyChange(MessagesDBHelper.CONTENT_URI, null);
            }
        };
        Commiter messageCommiter = new MessageCommiter(commiterCallback, activity);
        messageCommiter.commit();
    }

    private void sendMessage(String message) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ContentValues values = new ContentValues();
        String currentUserId = Api.getUserId((VKApplication) activity.getApplication());
        values.put(MessagesDBHelper.FROM_ID, currentUserId);
        values.put(MessagesDBHelper.USER_ID, mUserId);
        values.put(MessagesDBHelper.USER_DIALOG_ID, mUserId);
        values.put(MessagesDBHelper.OUT, "1");
        Date date = new Date();
        long rawDate = date.getTime() / 1000;
        values.put(MessagesDBHelper.RAW_DATE, rawDate);
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(activity);
        String cDate = dateFormat.format(date);
        values.put(MessagesDBHelper.DATE, cDate);
        values.put(MessagesDBHelper.PENDING, "1");
        values.put(MessagesDBHelper.BODY, message);
        final ContentResolver resolver = activity.getContentResolver();
        if (resolver == null) {
            return;
        }
        new UIQueryHelper(resolver).insert(MessagesDBHelper.CONTENT_URI,
                values, new UIQueryHelper.OnInsertResultListener() {
                    @Override
                    public void onInsertSuccess() {
                        resolver.notifyChange(MessagesDBHelper.CONTENT_URI, null);
                        commitPendingMessages();
                    }

                    @Override
                    public void onError(Exception e) {
                        ErrorHelper.showError(activity, e);
                        resolver.notifyChange(MessagesDBHelper.CONTENT_URI, null);
                    }
                }
        );
    }

    @Override
    public Uri getContentsUri() {
        return MessagesDBHelper.CONTENT_URI;
    }

    @Override
    public int getLoaderId() {
        return LoaderManagerIds.MESSAGES.ordinal();
    }

}
