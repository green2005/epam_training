package com.epamtraining.vklite.processors;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.bo.Dialog;
import com.epamtraining.vklite.bo.Friend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;


public class DialogsProcessor extends Processor{
    private Context mContext;
    private static final String ITEMS = "items";

    public DialogsProcessor(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void process(InputStream stream, AdditionalInfoSource source) throws Exception {
        JSONObject response = getVKResponseObject(stream);
        JSONArray dialogsItems = response.getJSONArray(ITEMS);
        ContentValues contentValues[] = new ContentValues[dialogsItems.length()];
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        HashSet<String> userIds = new HashSet<>();
        for (int i = 0; i < dialogsItems.length(); i++){
            Dialog msg = new Dialog(dialogsItems.getJSONObject(i), dateFormat);
            ContentValues value = new ContentValues();
            value.put(VKContentProvider.DIALOGS_COLUMN_BODY, msg.getBody());
            value.put(VKContentProvider.DIALOGS_COLUMN_DATE, msg.getDate());
            value.put(VKContentProvider.DIALOGS_COLUMN_RAW_DATE, msg.getRawDate());
            value.put(VKContentProvider.DIALOGS_COLUMN_MESSAGE_ID, msg.getId());
            value.put(VKContentProvider.DIALOGS_COLUMN_TITLE, msg.getTitle());
            value.put(VKContentProvider.DIALOGS_COLUMN_USER_ID, msg.getUserId());
            userIds.add(msg.getUserId());
            contentValues[i] = value;
        }
        updateUserInfos(userIds, source);
        mContext.getContentResolver().delete(VKContentProvider.DIALOGS_CONTENT_URI, null, null);
        mContext.getContentResolver().bulkInsert(VKContentProvider.DIALOGS_CONTENT_URI, contentValues);
        mContext.getContentResolver().notifyChange(VKContentProvider.DIALOGS_CONTENT_URI, null);
    }

    private void updateUserInfos(Set<String> userIds, AdditionalInfoSource source) throws  Exception{
        StringBuilder stringBuilder = new StringBuilder();
        for (String user:userIds){
            stringBuilder.append(user);
            stringBuilder.append(",");
        }
        String ids = stringBuilder.toString();
        String uri = Api.getUsersUri(mContext, ids);
        InputStream stream = source.getAdditionalInfo(uri);
        JSONArray userItems = getVKResponseArray(stream);
        ContentValues[] contentValues = new ContentValues[userItems.length()];
        for (int i = 0 ; i < userItems.length(); i++){
            Friend userItem = new Friend(userItems.getJSONObject(i));
            ContentValues value = new ContentValues();
            value.put(VKContentProvider.USERS_COLUMN_ID, userItem.getId());
            value.put(VKContentProvider.USERS_COLUMN_NAME, userItem.getName());
            value.put(VKContentProvider.USERS_COLUMN_IMAGE, userItem.getImageUrl());
            contentValues[i] = value;
        }
        //TODO
        ContentResolver contentResolver = mContext.getContentResolver();
        contentResolver.delete(VKContentProvider.USERS_CONTENT_URI, null, null);
        contentResolver.bulkInsert(VKContentProvider.USERS_CONTENT_URI, contentValues);
    }


    @Override
    public int getRecordsFetched() {
        return 0;
    }
}
