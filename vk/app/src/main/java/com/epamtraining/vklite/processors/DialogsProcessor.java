package com.epamtraining.vklite.processors;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.db.DialogDBHelper;
import com.epamtraining.vklite.db.UsersDBHelper;
import com.epamtraining.vklite.db.VKContentProvider;
import com.epamtraining.vklite.bo.Dialog;
import com.epamtraining.vklite.bo.Friend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;


public class DialogsProcessor extends Processor {
    private Context mContext;
    private static final String ITEMS = "items";
    private int mRecordsFetched;

    public DialogsProcessor(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void process(InputStream stream, AdditionalInfoSource source) throws Exception {
        JSONObject response = getVKResponseObject(stream);
        JSONArray dialogsItems = response.getJSONArray(ITEMS);
        DialogDBHelper helper = new DialogDBHelper();
        ContentValues contentValues[] = new ContentValues[dialogsItems.length()];
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        HashSet<String> userIds = new HashSet<>();
        for (int i = 0; i < dialogsItems.length(); i++) {
            Dialog msg = new Dialog(dialogsItems.getJSONObject(i), dateFormat);
            ContentValues value = helper.getContentValue(msg);
            userIds.add(msg.getUserId());
            contentValues[i] = value;
        }
        mRecordsFetched = dialogsItems.length();
        updateUserInfos(userIds, source);
        if (getIsTopRequest()) {
            mContext.getContentResolver().delete(DialogDBHelper.CONTENT_URI, null, null);
        }
        if (contentValues.length > 0) {
            mContext.getContentResolver().bulkInsert(DialogDBHelper.CONTENT_URI, contentValues);
        }
        mContext.getContentResolver().notifyChange(DialogDBHelper.CONTENT_URI, null);
    }

    private void updateUserInfos(Set<String> userIds, AdditionalInfoSource source) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        for (String user : userIds) {
            stringBuilder.append(user);
            stringBuilder.append(",");
        }
        String ids = stringBuilder.toString();
        String uri = Api.getUsersUri(mContext, ids);
        InputStream stream = source.getAdditionalInfo(uri);
        JSONArray userItems = getVKResponseArray(stream);
        ContentValues[] contentValues = new ContentValues[userItems.length()];
        UsersDBHelper helper = new UsersDBHelper();
        for (int i = 0; i < userItems.length(); i++) {
            Friend userItem = new Friend(userItems.getJSONObject(i));
            ContentValues value = helper.getContentValue(userItem);
            contentValues[i] = value;
        }
        ContentResolver resolver = mContext.getContentResolver();
        if (getIsTopRequest()) {
            resolver.delete(UsersDBHelper.CONTENT_URI, null, null);
        }
        if (contentValues.length > 0) {
            resolver.bulkInsert(UsersDBHelper.CONTENT_URI, contentValues);
        }
    }


    @Override
    public int getRecordsFetched() {
        return mRecordsFetched;
    }
}
