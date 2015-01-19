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
    private static String FROM_ID = "from_id";
    private static String DATE = "date";
    private static String TEXT = "text";
    private static String ID = "id";
    private static String COPY_HISTORY = "copy_history";
    private static final String ATTACHMENTS = "attachments";
    private static final String TYPE = "type";
    private static final String PHOTO = "photo";
    private static final String LINK = "link";

    private static final String PHOTO_604 = "photo_604";
    private static final String TITLE = "title";
    private static final String URL = "url";
    private static final String POSTER_ID = "owner_id";

    private JSONObject mJo;
    private String mDate;
    private String mRawDate;
    private String mImageUrl;
    private String mId;
    private String mText;
    private String mUrlHref;
    private String mUrlTitle;
    private String mPostId;
    private long mPosterId;
    private String mUserName;
    private String mUserImage;
    private String mOwnerId;
    private Attachments mAttaches;


    public Wall(JSONObject jo, DateFormat ft) throws Exception {
        mJo = jo;
        mRawDate = jo.optString(DATE);
        mId = jo.optString(ID);
        mPosterId = jo.optLong(POSTER_ID); //Math.abs(jo.optLong(POSTER_ID));
        if (jo.has(COPY_HISTORY)) {
            jo = jo.getJSONArray(COPY_HISTORY).getJSONObject(0);
        };
        mText = jo.optString(TEXT);
        java.util.Date time = new java.util.Date((long) Long.parseLong(mRawDate) * 1000);
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

    public Attachments getAttaches(){ return mAttaches;};

    public String getOwner_Id() {
        return mJo.optString(POSTER_ID);
    }

    public Long getPosterId() {
        return mPosterId;
    }

    public String getFROM_ID() throws Exception {
        return mJo.getString(FROM_ID);
    }


}
