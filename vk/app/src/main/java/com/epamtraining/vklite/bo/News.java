package com.epamtraining.vklite.bo;


import android.net.Uri;
import android.text.Html;
import android.text.Spanned;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class News implements Serializable{
   // private JSONObject mJO;
    private static final String DATE = "date";
    private static final String TEXT = "text";
    private String mText;
    private String mDate;

    public News(JSONObject jo) {
       // mJO = jo;
        try {
            mText = jo.getString(TEXT);

           // mText = Html.fromHtml(text);

            String timeStamp = jo.getString(DATE);
            java.util.Date time = new java.util.Date((long) Long.parseLong(timeStamp) * 1000);
            DateFormat ft = new SimpleDateFormat("dd.MM.yyyy");
            mDate = ft.format(time);
        }catch(Exception e){e.printStackTrace();}

    }

    public String getDate(){
        try {
            return mDate;
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public String getText(){
            return mText;
    }
}
