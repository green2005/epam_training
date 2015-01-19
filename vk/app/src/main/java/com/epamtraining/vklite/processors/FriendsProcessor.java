package com.epamtraining.vklite.processors;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.epamtraining.vklite.db.FriendDBHelper;
import com.epamtraining.vklite.db.VKContentProvider;
import com.epamtraining.vklite.bo.Friend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class FriendsProcessor extends Processor {
    private Context mContext;
    private static final String ITEMS = "items";
    private int mRecordsFetched;

    public FriendsProcessor(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void process(InputStream stream, AdditionalInfoSource dataSource) throws Exception {
        JSONObject response = getVKResponseObject(stream);
        JSONArray friendItems = response.getJSONArray(ITEMS);
        FriendDBHelper helper = new FriendDBHelper();
        ContentValues[] vals = new ContentValues[friendItems.length()];
        for (int i = 0; i < friendItems.length(); i++) {
            JSONObject jsonObject = friendItems.getJSONObject(i);
            Friend friend = new Friend(jsonObject);
            ContentValues value = helper.getContentValue(friend);
            vals[i] = value;
        }
        mRecordsFetched = friendItems.length();
        ContentResolver resolver = mContext.getContentResolver();
        if (resolver != null) {
            resolver.delete(FriendDBHelper.CONTENT_URI, null, null);
            if (vals.length > 0)
                resolver.bulkInsert(FriendDBHelper.CONTENT_URI, vals);
            resolver.notifyChange(FriendDBHelper.CONTENT_URI, null);
        }
    }

    @Override
    public int getRecordsFetched() {
        return mRecordsFetched;
    }
}
