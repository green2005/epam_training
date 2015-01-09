package com.epamtraining.vklite.bo;

import android.content.Context;

import com.epamtraining.vklite.processors.WallProcessor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Wall extends BoItem {
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


    public Wall(JSONObject jo, DateFormat ft) throws Exception {
        try {
            mJo = jo;
            mRawDate = jo.optString(DATE);
            mId = jo.optString(ID);
            mPosterId = Math.abs(jo.optLong(POSTER_ID));
            if (jo.has(COPY_HISTORY)) {
                jo = jo.getJSONArray(COPY_HISTORY).getJSONObject(0);
            };
            mText = jo.optString(TEXT);
            java.util.Date time = new java.util.Date((long) Long.parseLong(mRawDate) * 1000);
            // DateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
            mDate =  ft.format(time);
            if (jo.has(ATTACHMENTS)) {
                JSONArray attachments = jo.getJSONArray(ATTACHMENTS);
                for (int i = 0; i < attachments.length(); i++) {
                    JSONObject attachment = attachments.getJSONObject(i);
                    String type = attachment.getString(TYPE);
                    if (type.equalsIgnoreCase(PHOTO)) {
                        mImageUrl = attachment.getJSONObject(PHOTO).getString(PHOTO_604);
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

    public String getText() throws Exception {
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

    public String getID() {
        return mJo.optString(ID);
    }

    public String getOwner_Id() throws Exception {
        return mJo.optString(POSTER_ID);
    }

    public Long getPosterId() {
        return mPosterId;
    }

    public String getFROM_ID() throws Exception {
        return mJo.getString(FROM_ID);
    }

}
