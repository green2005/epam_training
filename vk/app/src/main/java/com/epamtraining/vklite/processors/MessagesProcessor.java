package com.epamtraining.vklite.processors;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.db.MessagesDBHelper;
import com.epamtraining.vklite.db.UsersDBHelper;
import com.epamtraining.vklite.db.VKContentProvider;
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
        MessagesDBHelper helper = new MessagesDBHelper();
        for (int i = 0; i < items.length(); i++){
            Message msg = new Message(items.getJSONObject(i), dateFormat);
            ContentValues value =  helper.getContentValue(msg);
            userIds.add(msg.getFromId());
            contentValues[i] = value;
        }
        mRecordsFetched = items.length();
        ContentResolver resolver =  mContext.getContentResolver();
        updateUserInfos(userIds, dataSource, resolver);
        if (getIsTopRequest()) {
           resolver.delete(MessagesDBHelper.CONTENT_URI, null, null);
        }

        resolver.bulkInsert(MessagesDBHelper.CONTENT_URI, contentValues);
        resolver.notifyChange(MessagesDBHelper.CONTENT_URI, null);
    }

    private void updateUserInfos(Set<String> userIds, AdditionalInfoSource source, ContentResolver resolver) throws  Exception{
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
        UsersDBHelper helper = new UsersDBHelper();
        for (int i = 0 ; i < userItems.length(); i++){
            Friend userItem = new Friend(userItems.getJSONObject(i));
            ContentValues value =helper.getContentValue(userItem);
            contentValues[i] = value;
        }
         resolver.bulkInsert(UsersDBHelper.CONTENT_URI, contentValues);
    }
    @Override
    public int getRecordsFetched() {
        return mRecordsFetched;
    }
}
