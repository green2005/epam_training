package com.epamtraining.vklite.bo;


import android.text.TextUtils;

import com.epamtraining.vklite.bo.attachment.Attachment;
import com.epamtraining.vklite.bo.attachment.PhotoAttachment;
import com.epamtraining.vklite.db.PostSourceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class News extends BoItem implements Serializable, PostSourceId {
    // private JSONObject mJO;
    private static final String DATE = "date";
    private static final String TEXT = "text";
    private static final String POST_ID = "post_id";
    private static final String ATTACHMENTS = "attachments";
    private static final String TYPE = "type";
    private static final String PHOTO = "photo";
    private static final String PHOTO_604 = "photo_604";
    private static final String LINK = "link";
    private static final String TITLE = "title";
    private static final String URL = "url";
    private static  final String SOURCE_ID = "source_id";

    private String mText;
    private String mDate;
    private String mRawDate;
    private String mImageUrl;
    private String mUrlTitle;
    private String mUrlHref;
    private String mPostID;
    private long mPosterID;
    private String mUserName;
    private String mUserImage;
    private String nextId;
    private Attachments mAttaches;

    public News(JSONObject jo, DateFormat ft) throws Exception{
            mRawDate = jo.optString(DATE);
            mPostID = jo.optString(POST_ID);
            mPosterID = jo.optLong(SOURCE_ID);// Math.abs(jo.optLong(SOURCE_ID));

            if (jo.has("copy_history")) {
                jo = jo.getJSONArray("copy_history").getJSONObject(0);
            };
            mText = jo.optString(TEXT);
            java.util.Date time = new java.util.Date((long) Long.parseLong(mRawDate) * 1000);
           // DateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
            mDate = ft.format(time);
        if (jo.has(ATTACHMENTS)) {
            mAttaches = new Attachments(jo.getJSONArray(ATTACHMENTS));
            for (Attachment attach : mAttaches.getAttachments()){
                if (attach instanceof PhotoAttachment){
                    mImageUrl = attach.getUrl(); //first image is shown in wall feed
                    break;
                }
            }
        }

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

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getUrlTitle() {
        return mUrlTitle;
    }

    public String getPostId() {
        return mPostID;
    }

    public String getUrlHref() {
        return mUrlHref;
    }

    public Long getPosterId(){ return mPosterID; }

    public Attachments getAttaches(){ return mAttaches;};

    public String getText() {
        return mText;
    }

    @Override
    public String getId() {
        return getPostId();
    }
}
