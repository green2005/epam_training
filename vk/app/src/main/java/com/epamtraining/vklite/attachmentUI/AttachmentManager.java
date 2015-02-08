package com.epamtraining.vklite.attachmentui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.bo.Attachments;
import com.epamtraining.vklite.db.AttachmentsDBHelper;
import com.epamtraining.vklite.loader.ImageLoader;

public class AttachmentManager {
    private enum ATTACHMENT_TYPE {PHOTO, VIDEO, AUDIO}

    //TODO maps
    //TODO post
    //TODO repost

    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public AttachmentManager(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    private ATTACHMENT_TYPE getAttachmentType(Cursor attachmentsCursor) {
        String attachmentType = CursorHelper.getString(attachmentsCursor, AttachmentsDBHelper.ATTACHMENT_TYPE);
        switch (attachmentType) {
            case (Attachments.ATTACHMENT_PHOTO):
                return ATTACHMENT_TYPE.PHOTO;
            case (Attachments.ATTACHMENT_AUDIO):
                return ATTACHMENT_TYPE.AUDIO;
            case (Attachments.ATTACHMENT_VIDEO):
                return ATTACHMENT_TYPE.VIDEO;
            default:
                throw new IllegalArgumentException("Unknown attachment type");
        }
    }

    public View getView(Cursor cursor, ImageLoader imageLoader) {
        AttachmentHelper helper = null;
        ATTACHMENT_TYPE attachmentType = getAttachmentType(cursor);
        switch (attachmentType) {
            case PHOTO: {
                helper = new AttachmentPhotoHelper(imageLoader);
                break;
            }
            case VIDEO: {
                helper = new AttachmentVideoHelper(imageLoader, mContext);
                break;
            }
            case AUDIO: {
                helper = new AttachmentAudioHelper(mContext);
                break;
            }
        }
        if (helper != null) {
            return helper.getView(cursor, mLayoutInflater);
        } else {
            return null;
        }
    }
}
