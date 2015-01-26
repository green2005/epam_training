package com.epamtraining.vklite.bo;

import com.epamtraining.vklite.bo.attachment.Attachment;
import com.epamtraining.vklite.bo.attachment.PhotoAttachment;
import com.epamtraining.vklite.db.PostSourceId;

import org.json.JSONObject;

import java.text.DateFormat;

public class Wall extends BoItem implements PostSourceId {
    /*
    CREATE TABLE Wall(_id Integer not null PRIMARY KEY AUTOINCREMENT, postid text, from_id text,"+
            " owner_id text, rawDate text, date text, itemText text, post_type text, image_Url text, level int)";
     */
    private static final String DATE = "date";
    private static final String TEXT = "text";
    private static final String ID = "id";
    private static final String COPY_HISTORY = "copy_history";
    private static final String ATTACHMENTS = "attachments";
    private static final String POSTER_ID = "owner_id";
    private static final String CAN_COMMENT = "can_post";
    private static final String COMMENTS = "comments";

    private String mDate;
    private String mRawDate;
    private String mImageUrl;
    private String mId;
    private String mText;
    private long mPosterId;
    private String mUserName;
    private String mUserImage;
    private Attachments mAttaches;
    private int mImageWidth;
    private int mImageHeight;
    private int mCanComment;

    public Wall(JSONObject jo, DateFormat ft) throws Exception {
        mRawDate = jo.optString(DATE);
        mId = jo.optString(ID);
        mPosterId = jo.optLong(POSTER_ID); //Math.abs(jo.optLong(POSTER_ID));
        JSONObject comments = jo.optJSONObject(COMMENTS);
        if (comments != null){
            mCanComment = comments.optInt(CAN_COMMENT);
        }
        if (jo.has(COPY_HISTORY)) {
            jo = jo.getJSONArray(COPY_HISTORY).getJSONObject(0);
        }
        mText = jo.optString(TEXT);
        java.util.Date time = new java.util.Date( Long.parseLong(mRawDate) * 1000);
        mDate = ft.format(time);
        if (jo.has(ATTACHMENTS)) {
            mAttaches = new Attachments(jo.getJSONArray(ATTACHMENTS));
            for (Attachment attach : mAttaches.getAttachments()){
                if (attach instanceof PhotoAttachment){
                    mImageUrl = attach.getUrl(); //first image is shown in wall feed
                    mImageWidth = attach.getWidth();
                    mImageHeight = attach.getHeight();
                    break;
                }
            }
        }
    }

    public void setUserInfo(Poster poster) {
        mUserImage = poster.getImageUrl();
        mUserName = poster.getName();
    }

    public String getUserName() {
        return mUserName;
    }

    public String getUserImage() {
        return mUserImage;
    }

    public String getText() {
        return mText;
    }

    public String getDate() {
        return mDate;
    }

    public String getRawDate() {
        return mRawDate;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getId() {
        return mId;
    }

    public Attachments getAttaches(){ return mAttaches;}

    public int getCanComment(){
        return mCanComment;
    }

    public Long getPosterId() {
        return mPosterId;
    }


    public int getImageHeight(){
        return mImageHeight;
    }

    public int getImageWidth(){
        return mImageWidth;
    }

}
