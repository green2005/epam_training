package com.epamtraining.vklite.displayAttachments;

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

    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public AttachmentManager(Context context) {
       mLayoutInflater = LayoutInflater.from(context);
       mContext = context;
    }

    private int getAttachmentType(Cursor attachmentsCursor){
        String attachmentType = CursorHelper.getString(attachmentsCursor, AttachmentsDBHelper.ATTACHMENT_TYPE);
        switch (attachmentType) {
            case (Attachments.ATTACHMENT_PHOTO): {
                return AttachmentManager.PHOTO;
            }
            case (Attachments.ATTACHMENT_AUDIO): {
                return AttachmentManager.AUDIO;
            }
            case (Attachments.ATTACHMENT_VIDEO):{
                return AttachmentManager.VIDEO;
            }
            default:{
                throw new IllegalArgumentException("Unknown attachment type");
            }
        }
    }

    public View getView(View convertView, Cursor cursor, ImageLoader imageLoader) {
        AttachmentHelper helper = null;
        int attachmentType = getAttachmentType(cursor);
        switch (attachmentType) {
            case PHOTO: {
                if (convertView == null) {
                    helper = new AttachmentPhotoHelper(imageLoader);
                } else {
                    helper = (AttachmentPhotoHelper) convertView.getTag();
                }
                break;
            }
            case VIDEO: {
                if (convertView == null) {
                    helper = new AttachmentVideoHelper(mContext, imageLoader);
                } else {
                    helper = (AttachmentVideoHelper) convertView.getTag();
                }
                break;
            }
            case AUDIO: {
                if (convertView == null) {
                    helper = new AttachmentAudioHelper(mContext);
                } else {
                    helper = (AttachmentAudioHelper) convertView.getTag();
                }
                break;
            }
        }
        if (helper != null) {
            View v = helper.getView(convertView, cursor, mLayoutInflater);
            v.setTag(helper);
            return v;
        } else {
            return null;
        }
    }
}
