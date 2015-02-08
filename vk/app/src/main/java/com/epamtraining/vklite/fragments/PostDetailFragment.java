package com.epamtraining.vklite.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.CommentAdapter;
import com.epamtraining.vklite.attachmentui.AttachmentManager;
import com.epamtraining.vklite.commiters.CommentCommiter;
import com.epamtraining.vklite.commiters.Commiter;
import com.epamtraining.vklite.commiters.CommiterCallback;
import com.epamtraining.vklite.db.AttachmentsDBHelper;
import com.epamtraining.vklite.db.CommentsDBHelper;
import com.epamtraining.vklite.db.MessagesDBHelper;
import com.epamtraining.vklite.db.NewsDBHelper;
import com.epamtraining.vklite.db.UIQueryHelper;
import com.epamtraining.vklite.db.WallDBHelper;
import com.epamtraining.vklite.loader.ImageLoader;
import com.epamtraining.vklite.processors.CommentsProcessor;
import com.epamtraining.vklite.processors.Processor;

import java.util.Date;


public class PostDetailFragment extends BaseListViewFragment {
    private String mPostId;
    private String mUserId;
    private boolean mIsWallRecord;
    public static final String WALL = "wall";
    private static final String COMMENT = "comment";
    private ImageLoader mImageLoader;

    private BoItemAdapter mAdapter;
    private CommentsProcessor mProcessor;

    private RelativeLayout editingLayout;
    private EditText commentText;

    public static PostDetailFragment getNewFragment(Bundle arguments) {
        PostDetailFragment fragment = new PostDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoader.get(getActivity());
        Bundle b = getArguments();
        if (b != null) {
            if (b.containsKey(WALL)) {
                mIsWallRecord = b.getBoolean(WALL);
            }
            if (mIsWallRecord) {
                mPostId = b.getString(WallDBHelper.POST_ID);
                mUserId = b.getString(WallDBHelper.WALL_OWNER_ID);
            } else {
                mPostId = b.getString(NewsDBHelper.POST_ID);
                mUserId = b.getString(NewsDBHelper.OWNER_ID);
            }
        }
        mAdapter = new CommentAdapter(getActivity(), R.layout.item_post, null, CommentsDBHelper.FIELDS, null, 0);
        mProcessor = new CommentsProcessor(getActivity());
        mProcessor.setPostId(mPostId);
    }

    @Override
    protected void onAfterCreateView(View parentView) {
        prepareHeaderView(parentView);
    }

    private void prepareHeaderView(View parentView) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(activity);
        View headerView = inflater.inflate(R.layout.item_post_header, null);
        if (mIsWallRecord) {
            fillWallHeader(headerView);
        } else {
            fillNewsHeader(headerView);
        }
        LinearLayout la = (LinearLayout) headerView.findViewById(R.id.attachment_layout);
        addAttachments(la);
        ListView listView = getCollectionViewWrapper().getCollectionView();
        listView.addHeaderView(headerView);

        editingLayout = (RelativeLayout) parentView.findViewById(R.id.editinglayout);
        commentText = (EditText) editingLayout.findViewById(R.id.messageedit);
        ImageButton sendButton = (ImageButton) editingLayout.findViewById(R.id.btnsend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(commentText.getText().toString());
                commentText.setText("");
            }
        });
    }

    private void sendComment(String comment) {
        if (TextUtils.isEmpty(comment)) {
            return;
        }
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        ContentValues values = new ContentValues();
        Date date = new Date();
        long rawDate = date.getTime() / 1000;
        values.put(MessagesDBHelper.RAW_DATE, rawDate);
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
        String cDate = dateFormat.format(date);

        values.put(CommentsDBHelper.POST_ID, mPostId);
        values.put(CommentsDBHelper.DATE, cDate);
        values.put(CommentsDBHelper.PENDING, 1);
        values.put(CommentsDBHelper.RAW_DATE, rawDate);
        values.put(CommentsDBHelper.TEXT, comment);
        values.put(CommentsDBHelper.OWNER_ID, mUserId);
        String userName = Api.getUserName(activity.getApplication());
        values.put(CommentsDBHelper.USERNAME, userName);
        String userImage = Api.getUserImage(activity.getApplication());
        values.put(CommentsDBHelper.USERIMAGE, userImage);
        new UIQueryHelper(activity.getContentResolver()).insert(CommentsDBHelper.CONTENT_URI,
                values, new UIQueryHelper.OnInsertResultListener() {
                    @Override
                    public void onInsertSuccess() {
                        activity.getContentResolver().notifyChange(CommentsDBHelper.CONTENT_URI, null);
                        commitPendingComments();
                    }

                    @Override
                    public void onError(Exception e) {
                        ErrorHelper.showError(activity, e);
                        activity.getContentResolver().notifyChange(CommentsDBHelper.CONTENT_URI, null);
                    }
                }
        );
        // activity.getContentResolver().insert(CommentsDBHelper.CONTENT_URI, values);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(COMMENT)) {
                commentText.setText(savedInstanceState.getString(COMMENT));
            }
        } else {
            SharedPreferences prefs = getActivity().getSharedPreferences(COMMENT, Context.MODE_PRIVATE);
            String s = prefs.getString(mPostId, "");
            if (!TextUtils.isEmpty(s)) {
                commentText.setText(s);
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove(mPostId);
                editor.apply();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(commentText.getText().toString())) {
            outState.putString(COMMENT, commentText.getText().toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!TextUtils.isEmpty(commentText.getText().toString())) {
            SharedPreferences prefs = getActivity().getSharedPreferences(COMMENT, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(mPostId, commentText.getText().toString());
            editor.apply();
        }
    }

    private void commitPendingComments() {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        CommiterCallback commiterCallback = new CommiterCallback() {
            @Override
            public void onAfterExecute() {
                activity.getContentResolver().notifyChange(CommentsDBHelper.CONTENT_URI, null);
            }

            @Override
            public void onException(Exception e) {
                ErrorHelper.showError(getActivity(), e);
                activity.getContentResolver().notifyChange(CommentsDBHelper.CONTENT_URI, null);
            }
        };
        Commiter commentCommiter = new CommentCommiter(commiterCallback, activity);
        commentCommiter.commit();
    }

    private void addAttachments(final LinearLayout linearLayout) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        final AttachmentManager manager = new AttachmentManager(activity);
        UIQueryHelper.OnQueryResultListener listener = new UIQueryHelper.OnQueryResultListener() {
            @Override
            public void onQueryResult(Cursor cursor) {
                if (cursor == null) {
                    return;
                } ///wtf
                try {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        View attachmentView = manager.getView(
                                cursor,
                                mImageLoader
                        );
                        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT, 0f);
                        linearLayout.addView(attachmentView, params);
                        if (!cursor.moveToNext()) {
                            break;
                        }
                    }
                } finally {
                    cursor.close();
                }
                addCommentsTitle(linearLayout);       //comments title
            }
        };

        new UIQueryHelper(activity.getContentResolver()).query(
                listener,
                AttachmentsDBHelper.CONTENT_URI,
                AttachmentsDBHelper.FIELDS
                , AttachmentsDBHelper.POST_ID + " = ?",
                new String[]{mPostId}, null);
    }

    private void addCommentsTitle(LinearLayout linearLayout) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(activity);
        View commentsTitleView = inflater.inflate(R.layout.item_comments_title, null);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 0f);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        linearLayout.addView(commentsTitleView, params);
    }

    private void fillNewsHeader(final View headerView) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        UIQueryHelper.OnQueryResultListener listener = new UIQueryHelper.OnQueryResultListener() {
            @Override
            public void onQueryResult(Cursor cursor) {
                if (cursor == null) {
                    return;
                } ///wtf
                try {
                    if (cursor.getCount() == 0) { ///wtf
                        //TODO change to some valid message
                        ErrorHelper.showError(getActivity(), R.string.db_empty);
                        return;
                    }
                    cursor.moveToFirst();
                    TextView userNameView = (TextView) headerView.findViewById(R.id.usernametextview);
                    ImageView userImageView = (ImageView) headerView.findViewById(R.id.profileimageview);
                    TextView dateView = (TextView) headerView.findViewById(R.id.date);
                    TextView textView = (TextView) headerView.findViewById(R.id.text);
                    userNameView.setText(CursorHelper.getString(cursor, NewsDBHelper.USERNAME));
                    dateView.setText(CursorHelper.getString(cursor, NewsDBHelper.DATE));

                    //Spanned sp = Html.fromHtml("");

                    textView.setText(Html.fromHtml(CursorHelper.getString(cursor, NewsDBHelper.TEXT)));
                    //TODO do not use in adapter getView
                    Linkify.addLinks(textView, Linkify.ALL);
                    mImageLoader.loadImage(userImageView, CursorHelper.getString(cursor, NewsDBHelper.USERIMAGE));
                    setCanCommentPost(CursorHelper.getInt(cursor, NewsDBHelper.CAN_COMMENT));
                } finally {
                    cursor.close();
                }
            }
        };

        new UIQueryHelper(activity.getContentResolver()).query(
                listener,
                NewsDBHelper.CONTENT_URI,
                NewsDBHelper.FIELDS
                , NewsDBHelper.POST_ID + " = ?",
                new String[]{mPostId}, null);
    }

    private void fillWallHeader(final View headerView) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        UIQueryHelper.OnQueryResultListener listener = new UIQueryHelper.OnQueryResultListener() {
            @Override
            public void onQueryResult(Cursor cursor) {
                if (cursor == null) {
                    return;
                } ///wtf
                try {
                    if (cursor.getCount() == 0) { ///wtf
                        //TODO change to some valid message
                        ErrorHelper.showError(getActivity(), R.string.db_empty);
                        return;
                    }
                    cursor.moveToFirst();
                    TextView userNameView = (TextView) headerView.findViewById(R.id.usernametextview);
                    ImageView userImageView = (ImageView) headerView.findViewById(R.id.profileimageview);
                    TextView dateView = (TextView) headerView.findViewById(R.id.date);
                    TextView textView = (TextView) headerView.findViewById(R.id.text);
                    userNameView.setText(CursorHelper.getString(cursor, WallDBHelper.USERNAME));
                    dateView.setText(CursorHelper.getString(cursor, WallDBHelper.DATE));
                    textView.setText(Html.fromHtml(CursorHelper.getString(cursor, WallDBHelper.TEXT)));
                    Linkify.addLinks(textView, Linkify.ALL);
                    mImageLoader.loadImage(userImageView, CursorHelper.getString(cursor, WallDBHelper.USERIMAGE));
                    setCanCommentPost(CursorHelper.getInt(cursor, WallDBHelper.CAN_COMMENT));
                } finally {
                    cursor.close();
                }
            }
        };

        new UIQueryHelper(activity.getContentResolver()).query(
                listener,
                WallDBHelper.CONTENT_URI,
                WallDBHelper.FIELDS,
                WallDBHelper.POST_ID + " = ? and " + WallDBHelper.WALL_OWNER_ID +" = ? ",
                new String[]{mPostId, mUserId}, null
        );
    }

    private void setCanCommentPost(int canCommentPost) {
        if (canCommentPost == 1) {
            editingLayout.setVisibility(View.VISIBLE);
        } else {
            editingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public String getCursorLoaderSelection() {
        return CommentsDBHelper.POST_ID + " = ?";//,
    }

    @Override
    public String[] getCursorLoaderSelectionArgs() {
        return new String[]{mPostId};
    }

    @Override
    public String[] getDataFields() {
        return CommentsDBHelper.FIELDS;
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
        return Api.getCommentsUri(getActivity(), mUserId, mPostId, offset + "");
    }

    @Override
    public Uri getContentsUri() {
        return CommentsDBHelper.CONTENT_URI;
    }


    @Override
    public int getLoaderId() {
        return LoaderManagerIds.COMMENTS.ordinal();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_post_detail;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
