package com.epamtraining.vklite.bo;

import org.json.JSONObject;

import java.text.DateFormat;

public class Dialog extends BoItem {
    private static final String ID = "id";
    private static final String BODY = "body";
    private static final String TITLE = "title";
    private static final String DATE = "date";
    private static final String USERID = "user_id";
    private static final String MESSAGE = "message";

    private String mDate;
    private String mRawDate;
    private String mBody;
    private String mUserId;
    private String mId;
    private String mTitle;

    public Dialog(JSONObject jo, DateFormat ft) throws Exception {
        if (jo.has(MESSAGE)) {
            JSONObject message = jo.optJSONObject(MESSAGE);
            mId = message.optString(ID);
            mBody = message.optString(BODY);
            mUserId = message.optString(USERID);
            mRawDate = message.optString(DATE);
            mTitle = message.optString(TITLE);
            java.util.Date time = new java.util.Date(Long.parseLong(mRawDate) * 1000);
            mDate = ft.format(time);
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

}
