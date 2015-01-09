package com.epamtraining.vklite.bo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;

//"CREATE TABLE Dialogs(_id Integer NOT NULL PRIMARY KEY AUTOINCREMENT, message_id text, body text, title text, Raw_Date text, Date text, user_id text, user_name text, user_image text)";
public class Dialog extends BoItem {
    private static final String ID = "id";
    private static final String BODY = "body";
    private static final String TITLE = "title";
    private static final String DATE = "date";
    private static final String USERID = "user_id";
    private static final String MESSAGE = "message";
    private static final String ATTACHMENTS = "attachments";

    private static final String PHOTO_OBJECT = "photo";
    private static final String PHOTO_URL = "photo_130";

    private String mDate;
    private String mRawDate;
    private String mBody;
    private String mUserId;
    private String mId;
    private String mTitle;
    private String mImageUrl;

    public Dialog(JSONObject jo, DateFormat ft) throws Exception {
        if (jo.has(MESSAGE)) {
            JSONObject message = jo.optJSONObject(MESSAGE);
            mId = message.optString(ID);
            mBody = message.optString(BODY);
            mUserId = message.optString(USERID);
            mRawDate = message.optString(DATE);
            mTitle = message.optString(TITLE);
            java.util.Date time = new java.util.Date((long) Long.parseLong(mRawDate) * 1000);
            mDate = ft.format(time);
            JSONArray attachments = message.optJSONArray(ATTACHMENTS);
            if (attachments != null) {
                for (int i = 0; i < attachments.length(); i++) {
                    JSONObject attachment = attachments.getJSONObject(i);
                    JSONObject photo = attachment.optJSONObject(PHOTO_OBJECT);
                    if (photo != null) {
                        mImageUrl = photo.optString(PHOTO_URL);
                    }
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

    public String getUserId() {
        return mUserId;
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

    public String getmImageUrl(){ return  mImageUrl; }

}
