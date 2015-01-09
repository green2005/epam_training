package com.epamtraining.vklite.commiters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;

import org.json.JSONObject;


public class MessageCommiter extends Commiter {
    private ContentResolver mResolver;
    private Context mContext;
    private static final String VK_ERROR_RESPONSE = "error";
    private static final String VK_ERROR_MSG = "error_msg";
    private static final String RESPONSE = "response";

    public MessageCommiter(CommiterCallback callback,Context context ) {
        super(callback, context );
        mContext = context;
        mResolver = context.getContentResolver();
    }

    @Override
    protected Cursor getPendingChanges() {
        /*
         mContext.getContentResolver().query(
                VKContentProvider.NEWS_CONTENT_URI,
                new String[]{VKContentProvider.NEWS_COLUMN_POST_ID}, VKContentProvider.NEWS_COLUMN_RAW_DATE + " = ?",
                new String[]{rawDate}, null);
         */
        Cursor cursor = mResolver.query(
                VKContentProvider.MESSAGES_CONTENT_URI,
                new String[]{VKContentProvider.MESSAGES_ID, VKContentProvider.MESSAGES_PENDING,
                        VKContentProvider.MESSAGES_COLUMN_BODY, VKContentProvider.MESSAGES_USER_FROM_ID,
                        VKContentProvider.MESSAGES_USER_ID
                }, VKContentProvider.MESSAGES_PENDING + " = ?",
                new String[]{"1"}, null
        );
        return cursor;
    }


    protected boolean checkIsResponseCorrect(String response) throws Exception{
        if (TextUtils.isEmpty(response)){
           throw new Exception(mContext.getResources().getString(R.string.response_is_empty));
        }
        JSONObject jo = new JSONObject(response);
        int id =  jo.optInt(RESPONSE, -1);
        String errorMsg ;
        if (id < 0 ){
            if (jo.has(VK_ERROR_RESPONSE)){
                errorMsg = jo.getJSONObject(VK_ERROR_RESPONSE).optString(VK_ERROR_MSG);
            } else
            {
                errorMsg = mContext.getResources().getString(R.string.unknown_server_error);
            };
            throw new Exception(errorMsg);
        }
        return true;
    }

    protected String getUrl(Cursor cr) throws Exception{
        String userId = cr.getString(cr.getColumnIndex(VKContentProvider.MESSAGES_USER_ID));
        String message = cr.getString(cr.getColumnIndex(VKContentProvider.MESSAGES_COLUMN_BODY));
        return Api.getMessagesCommitUrl(mContext, userId, message);
    }

    protected void setRecordAffected(Cursor cursor){
        int id = cursor.getInt(cursor.getColumnIndex(VKContentProvider.MESSAGES_ID));
        ContentValues contentValues = new ContentValues();
        contentValues.put(VKContentProvider.MESSAGES_PENDING, 0);
//        mResolver.update(VKContentProvider.MESSAGES_CONTENT_URI,
//                VKContentProvider.MESSAGES_ID + " = ?",
//                new String[]{id+""});

        mResolver.update(VKContentProvider.MESSAGES_CONTENT_URI,
                contentValues,VKContentProvider.MESSAGES_ID + " = ?",
                new String[]{id+""});

    }
}
