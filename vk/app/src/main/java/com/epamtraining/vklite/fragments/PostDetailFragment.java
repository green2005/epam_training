package com.epamtraining.vklite.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.imageLoader.ImageLoader;

public class PostDetailFragment extends Fragment {
    private String mPostId;
    private String mUserId;
    private boolean mIsWallRecord;
    public static final String WALL = "wall";

    private ImageView mUserImageView;
    private TextView mUserName;
    private TextView mDate;
    private TextView mText;
    private ImageLoader mImageLoader;
    private ImageView mImage;

    public static PostDetailFragment getNewFragment(Bundle arguments) {
        PostDetailFragment fragment = new PostDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle b = getArguments();
        if (b != null) {
            if (b.containsKey(VKContentProvider.NEWS_COLUMN_POST_ID)) {
                mPostId = b.getString(VKContentProvider.NEWS_COLUMN_POST_ID);
            }
            if (b.containsKey(VKContentProvider.NEWS_COLUMN_OWNER_ID)) {
                mUserId = b.getString(VKContentProvider.NEWS_COLUMN_OWNER_ID);
            }
            if (b.containsKey(WALL)) {
                mIsWallRecord = b.getBoolean(WALL);
            }
        }
        View v = inflater.inflate(R.layout.fragment_post_detail, null);
        mUserImageView = (ImageView) v.findViewById(R.id.profileimageview);
        mUserName = (TextView) v.findViewById(R.id.usernametextview);
        mDate = (TextView) v.findViewById(R.id.date);
        mText = (TextView) v.findViewById(R.id.text);
       // mText.setMovementMethod(LinkMovementMethod.getInstance());
        mImageLoader = ImageLoader.getImageLoader(getActivity());
        mImage = (ImageView) v.findViewById(R.id.image);
        if (mImageLoader.getIsPaused()){
            mImageLoader.resumeLoadingImages();
        }
        if (mIsWallRecord) {
            populateWallPost(v);
        } else {
            populateNewsPost(v);
        }
        return v;
    }

    private void populateNewsPost(View v) {
      Cursor cursor = getActivity().getContentResolver().query(
                VKContentProvider.NEWS_CONTENT_URI,
                new String[]{VKContentProvider.NEWS_COLUMN_POST_ID,
                VKContentProvider.NEWS_COLUMN_USERNAME,
                        VKContentProvider.NEWS_COLUMN_USERIMAGE,
                        VKContentProvider.NEWS_COLUMN_IMAGE_URL,
                        VKContentProvider.NEWS_COLUMN_TEXT,
                        VKContentProvider.NEWS_COLUMN_DATE
                }, VKContentProvider.NEWS_COLUMN_POST_ID + " = ?",
                new String[]{mPostId}, null);
      try{
          if (cursor.getCount() == 0){
              ErrorHelper.showError(getActivity(), R.string.db_empty);
              return;
          }
          cursor.moveToFirst();
          mImageLoader.loadImage(mUserImageView, CursorHelper.getString(cursor, VKContentProvider.NEWS_COLUMN_USERIMAGE));
          mDate.setText(CursorHelper.getString(cursor, VKContentProvider.NEWS_COLUMN_DATE));
          mUserName.setText(CursorHelper.getString(cursor, VKContentProvider.NEWS_COLUMN_USERNAME));

          mText.setText(Html.fromHtml(CursorHelper.getString(cursor, VKContentProvider.NEWS_COLUMN_TEXT)));
          Linkify.addLinks(mText, Linkify.ALL);


          String imageUrl = CursorHelper.getString(cursor, VKContentProvider.NEWS_COLUMN_IMAGE_URL);
          if (!TextUtils.isEmpty(imageUrl)) {
              mImage.setVisibility(View.VISIBLE);
              mImageLoader.loadImage(mImage, CursorHelper.getString(cursor, VKContentProvider.NEWS_COLUMN_IMAGE_URL));
          }
      }finally {
          cursor.close();
      }
    }

    private void populateWallPost(View v) {
        Cursor cursor = getActivity().getContentResolver().query(
                VKContentProvider.WALL_CONTENT_URI,
                new String[]{VKContentProvider.WALL_COLUMN_ITEM_ID,
                        VKContentProvider.WALL_COLUMN_USERNAME,
                        VKContentProvider.WALL_COLUMN_USERIMAGE,
                        VKContentProvider.WALL_COLUMN_IMAGE_URL,
                        VKContentProvider.WALL_COLUMN_TEXT,
                        VKContentProvider.WALL_COLUMN_DATE
                }, VKContentProvider.WALL_COLUMN_ITEM_ID + " = ?",
                new String[]{mPostId}, null);
        try{
            if (cursor.getCount() == 0){
                ErrorHelper.showError(getActivity(), R.string.db_empty);
                return;
            }
            cursor.moveToFirst();
            mImageLoader.loadImage(mUserImageView, CursorHelper.getString(cursor, VKContentProvider.WALL_COLUMN_USERIMAGE));
            mDate.setText(CursorHelper.getString(cursor, VKContentProvider.WALL_COLUMN_DATE));
            mUserName.setText(CursorHelper.getString(cursor, VKContentProvider.WALL_COLUMN_USERNAME));
            mText.setText(Html.fromHtml(CursorHelper.getString(cursor, VKContentProvider.WALL_COLUMN_TEXT)));
            Linkify.addLinks(mText, Linkify.ALL);
            String imageUrl = CursorHelper.getString(cursor, VKContentProvider.WALL_COLUMN_IMAGE_URL);
            if (!TextUtils.isEmpty(imageUrl)) {
                mImage.setVisibility(View.VISIBLE);
                mImageLoader.loadImage(mImage, CursorHelper.getString(cursor, VKContentProvider.NEWS_COLUMN_IMAGE_URL));
            }
        }finally {
            cursor.close();
        }
    }


}
