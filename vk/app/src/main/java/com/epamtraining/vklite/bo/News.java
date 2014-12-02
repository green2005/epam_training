package com.epamtraining.vklite.bo;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class News implements Serializable{
   // private JSONObject mJO;
    private static final String DATE = "date";
    private static final String TEXT = "text";
    private static final String POSTID = "post_id";
    private String mText;
    private String mDate;
    private String mRawDate;
    private String mImageUrl;
    private String mUrlTitle;
    private String mUrlHref;

    public News(JSONObject jo) {
        try {
            mText = jo.getString(TEXT);
            mRawDate = jo.getString(DATE);
            java.util.Date time = new java.util.Date((long) Long.parseLong(mRawDate) * 1000);
            DateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
            mDate = ft.format(time);
            if (jo.has("attachments")){
                JSONArray attachments = jo.getJSONArray("attachments");
                for (int i = 0; i < attachments.length(); i++){
                    JSONObject attachment = attachments.getJSONObject(i);
                    String type = attachment.getString("type");
                    if (type.equalsIgnoreCase("photo")){
                        mImageUrl = attachment.getJSONObject("photo").getString("photo_604");
                    } else
                    if (type.equalsIgnoreCase("link")){
                        mUrlHref = attachment.getJSONObject("link").getString("url");
                        mUrlTitle = attachment.getJSONObject("link").getString("title");
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
    }

    public String getRawDate(){
        return mRawDate;
    }

    public String getDate(){
            return mDate;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public String getUrlTitle(){
        return mUrlTitle;
    }

    public String getUrlHref(){
        return mUrlHref;
    }

    public String getText(){
            return mText;
    }
}
