package com.epamtraining.vklite.bo.attachment;


import com.epamtraining.vklite.bo.Attachments;

import org.json.JSONException;
import org.json.JSONObject;

public class VideoAttachment implements Attachment {
    private static final String ID = "id";
    private static final String TEXT = "description";
    private static final String TITLE = "title";
    private static final String DATE = "date";
    private static final String PHOTO = "photo_320";
    private static final String URL = "link";
    private static final String OWNER_ID = "owner_id";

    private long mId;
    private String mText;
    private String mDate;
    private long mOwnerId;
    private String mUrl;
    private String mPhoto;
    private String mTitle;

    public VideoAttachment(JSONObject jo) throws JSONException {
        mId = jo.optLong(ID);
        mText = jo.optString(TEXT);
        mDate = jo.optString(DATE);
        mUrl = jo.optString(URL);
        mPhoto = jo.optString(PHOTO);
        mTitle = jo.optString(TITLE);
        mOwnerId = jo.optLong(OWNER_ID);
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
        return 0;
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
        return mTitle;
    }

    @Override
    public String getPhoto() {
        return mPhoto;
    }

    @Override
    public String getAttachmentType() {
        return Attachments.ATTACHMENT_VIDEO;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
