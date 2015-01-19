package com.epamtraining.vklite.bo;

import org.json.JSONObject;

import java.text.DateFormat;

public class Comment extends BoItem{
    private static final String DATE = "date";
    private static final String ID = "id";
    private static final String SOURCE_ID = "from_id";
    private static final String TEXT = "text";

    private String mText;
    private String mDate;
    private String mRawDate;
    private String mID;
    private long mPosterID;
    private String mUserName;
    private String mUserImage;

    public Comment(JSONObject jo, DateFormat ft) throws Exception{
        mRawDate = jo.optString(DATE);
        mID = jo.optString(ID);
        mPosterID = jo.optLong(SOURCE_ID);// Math.abs(jo.optLong(SOURCE_ID));
        mText = jo.optString(TEXT);

        java.util.Date time = new java.util.Date((long) Long.parseLong(mRawDate) * 1000);
        mDate = ft.format(time);
    }

    public void setUserInfo(Poster poster){
        mUserImage = poster.getImageUrl();
        mUserName = poster.getName();
    }

    public String getUserName(){
        return mUserName;
    }

    public String getUserImage(){
        return mUserImage;
    }

    public String getRawDate() {
        return mRawDate;
    }

    public String getDate() {
        return mDate;
    }

    public Long getPosterId(){ return mPosterID; }


    public String getText() {
        return mText;
    }

    public String getId() {
        return mID;
    }
}



