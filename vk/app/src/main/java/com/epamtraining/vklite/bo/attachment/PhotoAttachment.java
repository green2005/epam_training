package com.epamtraining.vklite.bo.attachment;


import com.epamtraining.vklite.bo.Attachments;

import org.json.JSONException;
import org.json.JSONObject;

public class PhotoAttachment implements Attachment {
    private static final String ID = "id";
    private static final String ALBUM_ID = "album_id";
    private static final String TEXT = "text";
    private static final String DATE = "date";
    private static final String URL_604 = "photo_604";
    private static final String OWNER_ID = "owner_id";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";

    private long mId;
    private long mAlbumId;
    private String mText;
    private String mDate;
    private long mOwnerId;
    private String mUrl;
    private int mWidth;
    private int mHeight;

    public PhotoAttachment(JSONObject jo) throws JSONException {
        mId = jo.optLong(ID);
        mAlbumId = jo.optLong(ALBUM_ID);
        mText = jo.optString(TEXT);
        mDate = jo.optString(DATE);
        mUrl = jo.optString(URL_604);
        mOwnerId = jo.optLong(OWNER_ID);
        mWidth = jo.optInt(WIDTH);
        mHeight = jo.optInt(HEIGHT);
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public long getAlbumId() {
        return mAlbumId;
    }

    @Override
    public String getText() {
        return mText;
    }

    @Override
    public String getDate() {
        return mDate;
    }

    @Override
    public long getOwnerId() {
        return mOwnerId;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getPhoto() {
        return null;
    }

    @Override
    public String getAttachmentType() {
        return Attachments.ATTACHMENT_PHOTO;
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }
}
