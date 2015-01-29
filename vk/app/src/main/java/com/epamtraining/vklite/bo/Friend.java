package com.epamtraining.vklite.bo;

import org.json.JSONObject;

public class Friend extends BoItem {
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String NICKNAME = "nickname";
    private static final String PHOTO = "photo_100";
    private static final String ID = "id";

    private String mName;
    private String mFirstName;
    private String mLastName;
    private String mId;
    private String mNickName;
    private String mPhoto;

    public Friend(JSONObject jo) throws Exception {
        mFirstName = jo.optString(FIRST_NAME);
        mLastName = jo.optString(LAST_NAME);
        mName = (mFirstName + " " + mLastName).trim();
        mId = jo.getString(ID);
        mNickName = jo.optString(NICKNAME);
        mPhoto = jo.getString(PHOTO);
    }

    public String getName() {
        return mName;

    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }


    public String getId() {
        return mId;

    }

    public String getNick() {
        return mNickName;

    }

    public String getImageUrl() {
        return mPhoto;

    }

}
