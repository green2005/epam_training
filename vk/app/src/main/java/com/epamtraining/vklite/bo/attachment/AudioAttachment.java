package com.epamtraining.vklite.bo.attachment;

import com.epamtraining.vklite.bo.Attachments;

import org.json.JSONException;
import org.json.JSONObject;

public class AudioAttachment implements Attachment {
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String URL = "url";
    private static final String OWNER_ID = "owner_id";

    private long mId;
    private String mTitle;
    private long mOwnerId;
    private String mUrl;

    public AudioAttachment(JSONObject jo) throws JSONException {
        mId = jo.optLong(ID);
        mTitle = jo.optString(TITLE);
        mUrl = jo.optString(URL);
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
        return null;
    }

    @Override
    public String getDate() {
        return null;
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
        return null;
    }

    @Override
    public String getAttachmentType() {
        return Attachments.ATTACHMENT_AUDIO;
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
