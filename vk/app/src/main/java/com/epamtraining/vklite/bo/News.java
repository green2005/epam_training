package com.epamtraining.vklite.bo;


import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class News extends BoItem implements Serializable {
    // private JSONObject mJO;
    private static final String DATE = "date";
    private static final String TEXT = "text";
    private static final String POST_ID = "post_id";
    private static final String ATTACHMENTS = "attachments";
    private static final String TYPE = "type";
    private static final String PHOTO = "photo";
    //TODO размер фоток??
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

    public News(JSONObject jo, DateFormat ft) {
        try {
            mRawDate = jo.optString(DATE);
            mPostID = jo.optString(POST_ID);
            mPosterID = Math.abs(jo.optLong(SOURCE_ID));

            if (jo.has("copy_history")) {
                jo = jo.getJSONArray("copy_history").getJSONObject(0);
            };
            mText = jo.optString(TEXT);
            java.util.Date time = new java.util.Date((long) Long.parseLong(mRawDate) * 1000);
           // DateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
            mDate = ft.format(time);
            if (jo.has(ATTACHMENTS)) {
                JSONArray attachments = jo.getJSONArray(ATTACHMENTS);
                for (int i = 0; i < attachments.length(); i++) {
                    JSONObject attachment = attachments.getJSONObject(i);
                    String type = attachment.getString(TYPE);
                    if (type.equalsIgnoreCase(PHOTO)) {
                        mImageUrl = attachment.getJSONObject(PHOTO).getString(PHOTO_604); //TODO getScreenSIZE and choose photo size
                    } else if (type.equalsIgnoreCase(LINK)) {
                        mUrlHref = attachment.getJSONObject(LINK).getString(URL);
                        mUrlTitle = attachment.getJSONObject(LINK).getString(TITLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public String getText() {
        return mText;
    }
}
