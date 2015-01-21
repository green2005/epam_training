package com.epamtraining.vklite.attachmentUI;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.bo.Attachments;
import com.epamtraining.vklite.db.AttachmentsDBHelper;
import com.epamtraining.vklite.imageLoader.ImageLoader;

public class AttachmentManager {
    public static final int PHOTO = 0;
    public static final int VIDEO = 1;
    public static final int AUDIO = 2;
    //TODO maps
    //TODO post
    //TODO repost

    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public AttachmentManager(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
    }

    private int getAttachmentType(Cursor attachmentsCursor) {
        String attachmentType = CursorHelper.getString(attachmentsCursor, AttachmentsDBHelper.ATTACHMENT_TYPE);
        switch (attachmentType) {
            case (Attachments.ATTACHMENT_PHOTO):
                return AttachmentManager.PHOTO;
            case (Attachments.ATTACHMENT_AUDIO):
                return AttachmentManager.AUDIO;
            case (Attachments.ATTACHMENT_VIDEO):
                return AttachmentManager.VIDEO;
            default:
                throw new IllegalArgumentException("Unknown attachment type");
        }
    }

    public View getView(Cursor cursor, ImageLoader imageLoader) {
        AttachmentHelper helper = null;
        int attachmentType = getAttachmentType(cursor);
        switch (attachmentType) {
            case PHOTO: {
                helper = new AttachmentPhotoHelper(imageLoader);
                break;
            }
            case VIDEO: {
                helper = new AttachmentVideoHelper(mContext, imageLoader);
                break;
            }
            case AUDIO: {
                helper = new AttachmentAudioHelper(mContext);
                break;
            }
        }
        if (helper != null) {
            View v = helper.getView(cursor, mLayoutInflater);
            return v;
        } else {
            return null;
        }
    }
}
