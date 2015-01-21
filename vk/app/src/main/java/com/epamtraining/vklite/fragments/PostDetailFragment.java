package com.epamtraining.vklite.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.CommentAdapter;
import com.epamtraining.vklite.db.AttachmentsDBHelper;
import com.epamtraining.vklite.db.CommentsDBHelper;
import com.epamtraining.vklite.db.NewsDBHelper;
import com.epamtraining.vklite.db.WallDBHelper;
import com.epamtraining.vklite.attachmentUI.AttachmentManager;
import com.epamtraining.vklite.imageLoader.ImageLoader;
import com.epamtraining.vklite.processors.CommentsProcessor;
import com.epamtraining.vklite.processors.Processor;


public class PostDetailFragment extends BaseVKListViewFragment {
    private String mPostId;
    private String mUserId;
    private boolean mIsWallRecord;
    public static final String WALL = "wall";
    private ImageLoader mImageLoader;

    private BoItemAdapter mAdapter;
    private CommentsProcessor mProcessor;

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
                mUserId = b.getString(WallDBHelper.OWNER_ID);
            } else {
                mPostId = b.getString(NewsDBHelper.POST_ID);
                mUserId = b.getString(NewsDBHelper.OWNER_ID);
            }
        }
        mAdapter = new CommentAdapter(getActivity(), R.layout.item_post, null, CommentsDBHelper.fields, null, 0);
        mProcessor = new CommentsProcessor(getActivity());
        mProcessor.setPostId(mPostId);
    }

    protected void onAfterCreateView(View view) {
        prepareHeaderView(view);
    }

    private void prepareHeaderView(View parentView) {
        View headerView;
        if (mIsWallRecord) {
            headerView = getWallHeader();
        } else {
            headerView = getNewsHeader();
        }
        ListView listView = (ListView) parentView.findViewById(R.id.itemsList);
        Cursor cursor = getAttachmentsCursor();
        try {
            LinearLayout la = (LinearLayout) headerView.findViewById(R.id.attachment_layout);
            prepareAttachments(la, cursor); //attachments
            prepareCommentsTitleView(la);       //comments title
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (headerView != null) {
            listView.addHeaderView(headerView);
        }
    }

    private void prepareAttachments(LinearLayout la, Cursor attachmentsCursor) {
        if (attachmentsCursor == null) return;
        attachmentsCursor.moveToFirst();
        AttachmentManager manager = new AttachmentManager(getActivity());
        while (!attachmentsCursor.isAfterLast()) {
            View attachmentView = manager.getView(
                    attachmentsCursor,
                    mImageLoader
            );
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, 0f);
            la.addView(attachmentView, params);
            if (!attachmentsCursor.moveToNext()) {
                break;
            }
        }
    }

    private void prepareCommentsTitleView(LinearLayout la) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View commentsTitleView = inflater.inflate(R.layout.item_comments_title, null);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 0f);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        la.addView(commentsTitleView, params);
    }

    private View getNewsHeader() {
        Cursor cursor = getActivity().getContentResolver().query(
                NewsDBHelper.CONTENT_URI,
                NewsDBHelper.fields
                , NewsDBHelper.POST_ID + " = ?",
                new String[]{mPostId}, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.getCount() == 0) {
                ErrorHelper.showError(getActivity(), R.string.db_empty); //it's an error because we've been watching this item before
                return null;
            }
            cursor.moveToFirst();
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.item_post_header, null);
            TextView userNameView = (TextView) v.findViewById(R.id.usernametextview);
            ImageView userImageView = (ImageView) v.findViewById(R.id.profileimageview);
            TextView dateView = (TextView) v.findViewById(R.id.date);
            TextView textView = (TextView) v.findViewById(R.id.text);

            userNameView.setText(CursorHelper.getString(cursor, NewsDBHelper.USERNAME));
            dateView.setText(CursorHelper.getString(cursor, NewsDBHelper.DATE));
            textView.setText(Html.fromHtml(CursorHelper.getString(cursor, NewsDBHelper.TEXT)));
            //TODO do not use in adapter getView
            Linkify.addLinks(textView, Linkify.ALL);
            mImageLoader.loadImage(userImageView, CursorHelper.getString(cursor, NewsDBHelper.USERIMAGE));
            return v;
        } finally {
            cursor.close();
        }
    }

    private Cursor getAttachmentsCursor() {
        return getActivity().getContentResolver().query(
                AttachmentsDBHelper.CONTENT_URI,
                AttachmentsDBHelper.fields
                , AttachmentsDBHelper.POST_ID + " = ?",
                new String[]{mPostId}, null);
    }

    private View getWallHeader() {
        //TODO database call in the UI thread!!! critical!
        Cursor cursor = getActivity().getContentResolver().query(
                WallDBHelper.CONTENT_URI,
                WallDBHelper.fields
                , WallDBHelper.POST_ID + " = ?",
                new String[]{mPostId}, null);
        if (cursor == null) {
            return null;
        }

        try {
            if (cursor.getCount() == 0) {
                //TODO change to some valid message
                ErrorHelper.showError(getActivity(), R.string.db_empty);
                return null;
            }
            cursor.moveToFirst();
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.item_post_header, null);

            TextView userNameView = (TextView) v.findViewById(R.id.usernametextview);
            ImageView userImageView = (ImageView) v.findViewById(R.id.profileimageview);
            TextView dateView = (TextView) v.findViewById(R.id.date);
            TextView textView = (TextView) v.findViewById(R.id.text);

            userNameView.setText(CursorHelper.getString(cursor, WallDBHelper.USERNAME));
            dateView.setText(CursorHelper.getString(cursor, WallDBHelper.DATE));
            textView.setText(Html.fromHtml(CursorHelper.getString(cursor, WallDBHelper.TEXT)));
            Linkify.addLinks(textView, Linkify.ALL);
            mImageLoader.loadImage(userImageView, CursorHelper.getString(cursor, WallDBHelper.USERIMAGE));
            return v;
        } finally {
            cursor.close();
        }
    }

    public String getCursorLoaderSelection(){
        return CommentsDBHelper.POST_ID + " = ?";//,
    }

    public String[] getCursorLoaderSelectionArgs(){
        return new String[]{mPostId};
    }

    @Override
    public String[] getDataFields() {
        return CommentsDBHelper.fields;
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
        return LoaderManagerIds.COMMENTS.getId();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_post_detail;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
