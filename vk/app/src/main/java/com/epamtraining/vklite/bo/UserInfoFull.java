package com.epamtraining.vklite.bo;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

import java.text.DateFormat;

public class UserInfoFull implements Parcelable {

    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String IMAGE = "photo_100";
    private static final String STATUS = "status";
    private static final String LAST_SEEN = "last_seen";
    private static final String TIME = "time";
    private static final String COUNTERS = "counters";

    private static final String VIDEOS = "videos";
    private static final String FOTO = "photos";
    private static final String FRIENDS = "friends";
    private static final String GROUPS = "groups";
    private static final String AUDIOS = "audios";
    private static final String SUBSCRIBERS = "followers";
    private static final String CAN_WRITE_MESSAGE = "can_write_private_message";


    private String mUserName;
    private String mUserImage;
    private String mStatus;
    private String mTime;

    private String mVideos;
    private String mFoto;
    private String mFriends;
    private String mGroups;
    private String mAudios;
    private String mFollowers;
    private String mCanWriteMessage;
    private static final int FIELD_COUNT = 11;

    public UserInfoFull(JSONObject jUserInfo, DateFormat df) {

        mUserName = (jUserInfo.optString(FIRST_NAME) + " " +
                jUserInfo.optString(LAST_NAME)).trim();
        mUserImage = jUserInfo.optString(IMAGE);
        mStatus = jUserInfo.optString(STATUS);
        mCanWriteMessage = jUserInfo.optString(CAN_WRITE_MESSAGE);
        JSONObject jTime = jUserInfo.optJSONObject(LAST_SEEN);
        if (jTime != null) {
            mTime = jTime.optString(TIME);
            java.util.Date time = new java.util.Date( Long.parseLong(mTime) * 1000);
            mTime = df.format(time);
        }
        JSONObject counters = jUserInfo.optJSONObject(COUNTERS);
        if (counters != null) {
            mVideos = counters.optString(VIDEOS);
            mAudios = counters.optString(AUDIOS);
            mFoto = counters.optString(FOTO);
            mFollowers = counters.optString(SUBSCRIBERS);
            mFriends = counters.optString(FRIENDS);
            mGroups = counters.optString(GROUPS);
        }
    }


    public UserInfoFull(String[] params) {
        if (params.length != FIELD_COUNT) {
            throw new IllegalArgumentException("unknown parameters");
        }
        mStatus = params[0];
        mTime = params[1];
        mVideos = params[2];
        mFoto = params[3];
        mFriends = params[4];
        mGroups = params[5];
        mAudios = params[6];
        mFollowers = params[7];
        mUserName = params[8];
        mUserImage = params[9];
        mCanWriteMessage = params[10];
    }

    public String getStatus() {
        return mStatus;
    }

    public String getTime() {
        return mTime;
    }

    public String getVideos() {
        return mVideos;
    }

    public String getFoto() {
        return mFoto;
    }

    public String getFriends() {
        return mFriends;
    }

    public String getGroups() {
        return mGroups;
    }

    public String getAudios() {
        return mAudios;
    }

    public String getFollowers() {
        return mFollowers;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getUserImage() {
        return mUserImage;
    }

    public String getCanWriteMessage(){
        return mCanWriteMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{getStatus(),
                getTime(),
                getVideos(),
                getFoto(),
                getFriends(),
                getGroups(),
                getAudios(),
                getFollowers(),
                getUserName(),
                getUserImage(),
                getCanWriteMessage()
        });
    }

    public static final Creator<UserInfoFull> CREATOR = new Creator() {

        @Override
        public Object createFromParcel(Parcel source) {
            String[] params = new String[FIELD_COUNT];
            source.readStringArray(params);
            return new UserInfoFull(params);
        }

        @Override
        public Object[] newArray(int size) {
            return new UserInfoFull[size];
        }
    };
}
