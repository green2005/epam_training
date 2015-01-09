package com.epamtraining.vklite.bo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;

public class Message extends BoItem {
    private static final String ID = "id";
    private static final String BODY = "body";
    private static final String TITLE = "title";
    private static final String DATE = "date";
    private static final String USERID = "user_id";
    private static final String FROMID = "from_id";
    private static final String ATTACHMENTS = "attachments";

    private static final String PHOTO_OBJECT = "photo";
    private static final String PHOTO_URL = "photo_130";
    private static final String OUT = "out";

    private String mDate;
    private String mRawDate;
    private String mBody;
    private String mFromId;
    private String mId;
    private String mTitle;
    private String mImageUrl;
    private String mOut;

    public Message(JSONObject jo, DateFormat ft) throws Exception {
        mId = jo.optString(ID);
        mBody = jo.optString(BODY);
        mFromId = jo.optString(FROMID);
        mRawDate = jo.optString(DATE);
        mTitle = jo.optString(TITLE);
        mOut = jo.optString(OUT);
        java.util.Date time = new java.util.Date((long) Long.parseLong(mRawDate) * 1000);
        mDate = ft.format(time);
        JSONArray attachments = jo.optJSONArray(ATTACHMENTS);
        if (attachments != null) {
            for (int i = 0; i < attachments.length(); i++) {
                JSONObject attachment = attachments.getJSONObject(i);
                JSONObject photo = attachment.optJSONObject(PHOTO_OBJECT);
                if (photo != null) {
                    mImageUrl = photo.optString(PHOTO_URL);
                    break;
                }
            }
        }
    }

    public String getId() {
        return mId;
    }

    public String getBody() {
        return mBody;
    }

    public String getFromId() {
        return mFromId;
    }

    public String getRawDate() {
        return mRawDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDate() {
        return mDate;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getOut(){
        return mOut;
    }

}
