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

public class FriendsProcessor implements Processor {
    private String mToken;
    private Context mContext;

    public FriendsProcessor(String token, Context context) {
        mToken = token;
        mContext = context;
    }

    @Override
    public String getUrl() {
        return "https://api.vk.com/method/users.search?fields=photo_100,online,nickname&count=100&city=1&access_token=" + mToken + "&v="
                + this.API_KEY;
    }

    @Override
    public void process(InputStream stream) throws Exception {
            String s = new StringReader().readFromStream(stream);
            JSONArray friendItems = new JSONObject(s).getJSONObject("response").getJSONArray("items");
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
            mContext.getContentResolver().delete(VKContentProvider.FRIENDS_CONTENT_URI, null, null);
            if (vals.length > 0)
                mContext.getContentResolver().bulkInsert(VKContentProvider.FRIENDS_CONTENT_URI, vals);
    }
}
