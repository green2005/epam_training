package com.epamtraining.vklite.commiters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.db.CommentsDBHelper;
import com.epamtraining.vklite.db.MessagesDBHelper;

import org.json.JSONObject;

public class CommentCommiter extends Commiter {
    private static final String VK_ERROR_RESPONSE = "error";
    private static final String VK_ERROR_MSG = "error_msg";

    private ContentResolver mResolver;
    private Context mContext;

    public CommentCommiter(CommiterCallback callback, Context context) {
        super(callback, context);
        mContext = context;
        mResolver = context.getContentResolver();
    }

    @Override
    protected Cursor getPendingChanges() {
        return mResolver.query(
                CommentsDBHelper.CONTENT_URI,
                new String[]{BaseColumns._ID, CommentsDBHelper.PENDING,
                        CommentsDBHelper.TEXT, CommentsDBHelper.OWNER_ID,
                        CommentsDBHelper.POST_ID
                }, MessagesDBHelper.PENDING + " = ?",
                new String[]{"1"}, null
        );
    }

    @Override
    protected String getUrl(Cursor cr) throws Exception {
        String userId = CursorHelper.getString(cr, CommentsDBHelper.OWNER_ID);
        String message = CursorHelper.getString(cr, CommentsDBHelper.TEXT);
        String postId = CursorHelper.getString(cr, CommentsDBHelper.POST_ID);
        return Api.getCommentsCommitUrl(mContext, userId, postId, message);
    }

    @Override
    protected boolean checkIsResponseCorrect(String response) throws Exception {
        if (TextUtils.isEmpty(response)) {
            throw new Exception(mContext.getResources().getString(R.string.response_is_empty));
        }
        JSONObject jo = new JSONObject(response);
        String errorMsg;
        if (jo.has(VK_ERROR_RESPONSE)) {
            errorMsg = jo.getJSONObject(VK_ERROR_RESPONSE).optString(VK_ERROR_MSG);
            throw new Exception(errorMsg);
        }
        return true;
    }

    @Override
    protected void setRecordAffected(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
        ContentValues contentValues = new ContentValues();
        contentValues.put(CommentsDBHelper.PENDING, 0);
        mResolver.update(CommentsDBHelper.CONTENT_URI,
                contentValues, BaseColumns._ID + " = ?",
                new String[]{String.valueOf(id)});
    }
}
