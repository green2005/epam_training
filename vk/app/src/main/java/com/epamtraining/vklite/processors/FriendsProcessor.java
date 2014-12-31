package com.epamtraining.vklite.processors;


import android.content.ContentValues;
import android.content.Context;

import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.bo.Friend;
import com.epamtraining.vklite.os.VKExecutor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class FriendsProcessor extends Processor {
    private Context mContext;
    private static  final String ITEMS = "items";
    private int recordsFetched;


    public FriendsProcessor(  Context context)
    {
        super(context);
         mContext = context;
    }


    @Override
    public void process(InputStream stream) throws Exception {
        JSONObject response = getVKResponse(stream);
        JSONArray friendItems = response.getJSONArray(ITEMS);
        ContentValues[] vals = new ContentValues[friendItems.length()];
        for (int i = 0; i < friendItems.length(); i++) {
            JSONObject jsonObject = friendItems.getJSONObject(i);
            Friend friend = new Friend(jsonObject);
            ContentValues value = new ContentValues();
            value.put(VKContentProvider.FRIEND_COLUMN_ID, friend.getId());
            value.put(VKContentProvider.FRIEND_COLUMN_FIRST_NAME, friend.getFirstName());
            value.put(VKContentProvider.FRIEND_COLUMN_LAST_NAME, friend.getLastName());
            value.put(VKContentProvider.FRIEND_COLUMN_IMAGE_URL, friend.getImageUrl());
            value.put(VKContentProvider.FRIEND_COLUMN_NICK_NAME, friend.getNick());
            vals[i] = value;
        }
        recordsFetched = friendItems.length();
        mContext.getContentResolver().delete(VKContentProvider.FRIENDS_CONTENT_URI, null, null);
        if (vals.length > 0)
            mContext.getContentResolver().bulkInsert(VKContentProvider.FRIENDS_CONTENT_URI, vals);
    }

    @Override
    public int getRecordsFetched() {
        return recordsFetched;
    }
}
