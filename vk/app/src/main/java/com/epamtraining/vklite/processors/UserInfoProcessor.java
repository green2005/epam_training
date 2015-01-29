package com.epamtraining.vklite.processors;


import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.io.InputStream;

public class UserInfoProcessor extends Processor {
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String IMAGE = "photo_100";

    public static final String USER_NAME = "userName";
    public static final String USER_IMAGE = "image";
    public static final String USER_INFO = "userInfo";

    private Context mContext;

    public UserInfoProcessor(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void process(InputStream stream, String url, AdditionalInfoSource dataSource) throws Exception {
        JSONArray response = getVKResponseArray(stream);
        if (response.length() > 0) {
            String userName = (response.getJSONObject(0).optString(FIRST_NAME) + " " +
                    response.getJSONObject(0).optString(LAST_NAME)).trim();
            String image = response.getJSONObject(0).optString(IMAGE);
            SharedPreferences prefs = mContext.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(USER_NAME, userName);
            editor.putString(USER_IMAGE, image);
            editor.apply();
        }
    }

    @Override
    public int getRecordsFetched() {
        return 0;
    }
}
