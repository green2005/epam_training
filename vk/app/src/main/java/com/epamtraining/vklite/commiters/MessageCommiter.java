package com.epamtraining.vklite.commiters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.db.MessagesDBHelper;

import org.json.JSONObject;


public class MessageCommiter extends Commiter {
    private ContentResolver mResolver;
    private Context mContext;
    private static final String VK_ERROR_RESPONSE = "error";
    private static final String VK_ERROR_MSG = "error_msg";
    private static final String RESPONSE = "response";

    public MessageCommiter(CommiterCallback callback, Context context) {
        super(callback, context);
        mContext = context;
        mResolver = context.getContentResolver();
    }

    @Override
    protected Cursor getPendingChanges() {
        return mResolver.query(
                MessagesDBHelper.CONTENT_URI,
                new String[]{BaseColumns._ID, MessagesDBHelper.PENDING,
                        MessagesDBHelper.BODY, MessagesDBHelper.FROM_ID,
                        MessagesDBHelper.USER_ID
                }, MessagesDBHelper.PENDING + " = ?",
                new String[]{"1"}, null
        );
    }


    protected boolean checkIsResponseCorrect(String response) throws Exception {
        if (TextUtils.isEmpty(response)) {
            throw new Exception(mContext.getResources().getString(R.string.response_is_empty));
        }
        JSONObject jo = new JSONObject(response);
        int id = jo.optInt(RESPONSE, -1);
        String errorMsg;
        if (id < 0) {
            if (jo.has(VK_ERROR_RESPONSE)) {
                errorMsg = jo.getJSONObject(VK_ERROR_RESPONSE).optString(VK_ERROR_MSG);
            } else {
                errorMsg = mContext.getResources().getString(R.string.unknown_server_error);
            }
            throw new Exception(errorMsg);
        }
        return true;
    }

    protected String getUrl(Cursor cr) throws Exception {
        String userId = cr.getString(cr.getColumnIndex(MessagesDBHelper.USER_ID));
        String message = cr.getString(cr.getColumnIndex(MessagesDBHelper.BODY));
        return Api.getMessagesCommitUrl(mContext, userId, message);
    }

    protected void setRecordAffected(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
        ContentValues contentValues = new ContentValues();
        contentValues.put(MessagesDBHelper.PENDING, 0);

        mResolver.update(MessagesDBHelper.CONTENT_URI,
                contentValues, BaseColumns._ID + " = ?",
                new String[]{String.valueOf(id)});

    }
}
