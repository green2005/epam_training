package com.epamtraining.vklite.fragments;

import android.support.v4.app.Fragment;

import com.epamtraining.vklite.R;

public enum FragmentMenuItem {
    NEWS_ITEM(R.string.news, R.drawable.ic_news),
    WALL_ITEM( R.string.wall, R.drawable.ic_wall),
    FRIEND_ITEM( R.string.friends, R.drawable.ic_friends),
    DIALOG_ITEM(R.string.messages, R.drawable.ic_messages),
    ;

    private int mNameResourceId;
    private int mImageResourceId;

    FragmentMenuItem(int nameResourceId, int imageResourceId) {
        mImageResourceId = imageResourceId;
        mNameResourceId = nameResourceId;
      }

    public boolean getIsMainActivityFragment(){
        return mNameResourceId != 0;
    }

    public int getNameResourceId() {
        return mNameResourceId;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public Fragment getNewFragment() {
        Fragment fragment = null;
        switch (this) {
            case DIALOG_ITEM: {
                fragment = DialogsFragment.getNewFragment();
                break;
            }
            case NEWS_ITEM: {
                fragment = NewsFragment.getNewFragment();
                break;
            }
            case FRIEND_ITEM: {
                fragment = FriendsFragment.getNewFragment(null);
                break;
            }
            case WALL_ITEM: {
                fragment = WallFragment.getNewFragment();
                break;
            }
        }
        return fragment;
    }
}
