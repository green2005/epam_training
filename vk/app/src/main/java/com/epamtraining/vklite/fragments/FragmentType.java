package com.epamtraining.vklite.fragments;

import android.net.Uri;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;

public enum FragmentType {
    NEWSFRAGMENT(1, R.string.news, R.drawable.ic_news, VKContentProvider.NEWS_CONTENT_URI),
    WALLFRAGMENT(2, R.string.wall, R.drawable.ic_wall, VKContentProvider.WALL_CONTENT_URI),
    FRIENDFRAGMENT(3, R.string.friends, R.drawable.ic_friends, VKContentProvider.FRIENDS_CONTENT_URI),
    DIALOGSFRAGMENT(4, R.string.messages, R.drawable.ic_messages, VKContentProvider.DIALOGS_CONTENT_URI),
    MESSAGESFRAGMENT(5, 0, 0, VKContentProvider.MESSAGES_CONTENT_URI), ;
    ;

    private int mId;  // used for loader manager
    private int mNameResourceId;
    private int mImageResourceId;
    private Uri mContentUri;


    FragmentType(int id, int nameResourceId, int imageResourceId, Uri contentUri) {
        mId = id;
        mImageResourceId = imageResourceId;
        mNameResourceId = nameResourceId;
        mContentUri = contentUri;
      }

    public boolean getIsMainActivityFragment(){
        return mNameResourceId != 0;
    }

    public int getId() {
        return mId;
    }

    public Uri getContentUri() {
        return mContentUri;
    }

    public int getNameResourceId() {
        return mNameResourceId;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }
/*
    public String getDataUrl(Context context, int offset, String next_postID) {
        switch (this) {
            case DIALOGSFRAGMENT: {
                return Api.getDialogsUrl(context, offset + "");
            }
            case WALLFRAGMENT: {
                return Api.getWallUrl(context, offset + "");
            }
            case NEWSFRAGMENT: {
                return Api.getNewsUrl(context, next_postID);
            }
            case FRIENDFRAGMENT: {
                return Api.getFriendsUrl(context);
            }
        }
        return null;
    }
*/
    public BoItemFragment getNewFragment() {
        BoItemFragment fragment = null;
        switch (this) {
            case DIALOGSFRAGMENT: {
                fragment = DialogsFragment.getNewFragment();
                break;
            }
            case NEWSFRAGMENT: {
                fragment = NewsFragment.getNewFragment();
                break;
            }
            case FRIENDFRAGMENT: {
                fragment = FriendsFragment.getNewFragment(null);
                break;
            }
            case WALLFRAGMENT: {
                fragment = WallFragment.getNewFragment();
                break;
            }
        }
        return fragment;
    }
}
