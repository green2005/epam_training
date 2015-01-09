package com.epamtraining.vklite.processors;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.bo.Friend;
import com.epamtraining.vklite.bo.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;


public class MessagesProcessor extends Processor {
    private Context mContext;
    private static final String ITEMS = "items";
    private int mRecordsFetched;

    public MessagesProcessor(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void process(InputStream stream, AdditionalInfoSource dataSource) throws Exception {
        JSONObject response = getVKResponseObject(stream);
        JSONArray items = response.getJSONArray(ITEMS);
        ContentValues contentValues[] = new ContentValues[items.length()];
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        HashSet<String> userIds = new HashSet<>();
        for (int i = 0; i < items.length(); i++){
            Message msg = new Message(items.getJSONObject(i), dateFormat);
            ContentValues value = new ContentValues();
            value.put(VKContentProvider.MESSAGES_COLUMN_BODY, msg.getBody());
            value.put(VKContentProvider.MESSAGES_DATE, msg.getDate());
            value.put(VKContentProvider.MESSAGES_RAW_DATE, msg.getRawDate());
            value.put(VKContentProvider.MESSAGES_MESSAGE_ID, msg.getId());
            value.put(VKContentProvider.MESSAGES_USER_FROM_ID, msg.getFromId());
            value.put(VKContentProvider.MESSAGES_MESSAGE_ID, msg.getId());
            value.put(VKContentProvider.MESSAGES_OUT, msg.getOut());
            if (!TextUtils.isEmpty(msg.getmImageUrl())) {
                value.put(VKContentProvider.MESSAGES_IMAGE_URL, msg.getmImageUrl());
            }
            userIds.add(msg.getFromId());
            contentValues[i] = value;
        }
        mRecordsFetched = items.length();
        updateUserInfos(userIds, dataSource);
        if (getIsTopRequest()) {
            mContext.getContentResolver().delete(VKContentProvider.MESSAGES_CONTENT_URI, null, null);
        }
        mContext.getContentResolver().bulkInsert(VKContentProvider.MESSAGES_CONTENT_URI, contentValues);
        mContext.getContentResolver().notifyChange(VKContentProvider.MESSAGES_CONTENT_URI, null);
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
        //mContext.getContentResolver().delete(VKContentProvider.USERS_CONTENT_URI, null, null);
        mContext.getContentResolver().bulkInsert(VKContentProvider.USERS_CONTENT_URI, contentValues);
    }
    @Override
    public int getRecordsFetched() {
        return mRecordsFetched;
    }
}
