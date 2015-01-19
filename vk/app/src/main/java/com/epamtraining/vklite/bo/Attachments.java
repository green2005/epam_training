package com.epamtraining.vklite.bo;


import android.view.View;

import com.epamtraining.vklite.bo.attachment.Attachment;
import com.epamtraining.vklite.bo.attachment.AudioAttachment;
import com.epamtraining.vklite.bo.attachment.PhotoAttachment;
import com.epamtraining.vklite.bo.attachment.VideoAttachment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Attachments extends BoItem {
    private static final String TYPE = "type";
    public static final String ATTACHMENT_PHOTO = "photo";
    public static final String ATTACHMENT_AUDIO = "audio";
    public static final String ATTACHMENT_VIDEO = "video";

    private List<Attachment> mAttachments;

    public Attachments(JSONArray jAttachments) throws JSONException{
        mAttachments = new ArrayList<>();
        for (int i = 0 ;i<jAttachments.length(); i++){
            Attachment attachment = getAttachment(jAttachments.getJSONObject(i));
            if (attachment != null){
                mAttachments.add(attachment);
            }
        }
    }

    public List<Attachment> getAttachments(){
        return mAttachments;
    }

    private Attachment getAttachment(JSONObject jo) throws JSONException{
        switch (jo.optString(TYPE)){
            case(ATTACHMENT_PHOTO):{
                return new PhotoAttachment(jo.optJSONObject(ATTACHMENT_PHOTO));
            }
            case (ATTACHMENT_VIDEO):{
                return new VideoAttachment(jo.optJSONObject(ATTACHMENT_VIDEO));
            }
            case (ATTACHMENT_AUDIO):{
                return new AudioAttachment(jo.optJSONObject(ATTACHMENT_AUDIO));
            }
            default:{
                return null;
            }
        }
    }
}
