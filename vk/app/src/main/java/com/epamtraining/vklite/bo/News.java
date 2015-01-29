package com.epamtraining.vklite.bo;


import com.epamtraining.vklite.bo.attachment.Attachment;
import com.epamtraining.vklite.bo.attachment.PhotoAttachment;
import com.epamtraining.vklite.db.PostSourceId;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;

public class News extends BoItem implements Serializable, PostSourceId {
    private static final String DATE = "date";
    private static final String TEXT = "text";
    private static final String POST_ID = "post_id";
    private static final String ATTACHMENTS = "attachments";
    private static final String SOURCE_ID = "source_id";
    private static final String CAN_COMMENT = "can_post";
    private static final String COMMENTS = "comments";

    private String mText;
    private String mDate;
    private String mRawDate;
    private String mImageUrl;
    private int mImageWidth;
    private int mImageHeight;
    private String mPostID;
    private long mPosterID;
    private String mUserName;
    private String mUserImage;
    private Attachments mAttaches;
    private int mCanComment;

    public News(JSONObject jo, DateFormat ft) throws Exception {
        mRawDate = jo.optString(DATE);
        mPostID = jo.optString(POST_ID);
        mPosterID = jo.optLong(SOURCE_ID);// Math.abs(jo.optLong(SOURCE_ID));
        JSONObject comments = jo.optJSONObject(COMMENTS);
        if (comments != null) {
            mCanComment = comments.optInt(CAN_COMMENT);
        }

        if (jo.has("copy_history")) {
            jo = jo.getJSONArray("copy_history").getJSONObject(0);
        }
        mText = jo.optString(TEXT);
        java.util.Date time = new java.util.Date(Long.parseLong(mRawDate) * 1000);
        mDate = ft.format(time);

        if (jo.has(ATTACHMENTS)) {
            mAttaches = new Attachments(jo.getJSONArray(ATTACHMENTS));
            for (Attachment attach : mAttaches.getAttachments()) {
                if (attach instanceof PhotoAttachment) {
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

    public String getRawDate() {
        return mRawDate;
    }

    public String getDate() {
        return mDate;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getPostId() {
        return mPostID;
    }


    public Long getPosterId() {
        return mPosterID;
    }

    public int getImageWidth() {
        return mImageWidth;
    }

    public int getImageHeight() {
        return mImageHeight;
    }

    public Attachments getAttaches() {
        return mAttaches;
    }

    public String getText() {
        return mText;
    }

    public int getCanComment() {
        return mCanComment;
    }

    @Override
    public String getId() {
        return getPostId();
    }
}
