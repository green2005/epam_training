package com.epamtraining.vklite.processors;


import android.content.Context;
import android.os.Bundle;

import org.json.JSONArray;

import java.io.InputStream;

public class UserInfoProcessor extends Processor {
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String IMAGE = "photo_100";

    public static final String USER_NAME = "userName";
    public static final String USER_IMAGE = "image";
    public static final String USER_INFO = "userInfo";

   private String mUserName;
    private String mUserImage;

    public UserInfoProcessor(Context context) {
        super(context);
     }

    @Override
    public void process(InputStream stream, String url, AdditionalInfoSource dataSource) throws Exception {
        JSONArray response = getVKResponseArray(stream);
        if (response.length() > 0) {
            mUserName = (response.getJSONObject(0).optString(FIRST_NAME) + " " +
                    response.getJSONObject(0).optString(LAST_NAME)).trim();
            mUserImage = response.getJSONObject(0).optString(IMAGE);
        }
    }

    @Override
    public int getRecordsFetched() {
        return 0;
    }

    @Override
    public Bundle getResult() {
        Bundle b = new Bundle();
        b.putString(UserInfoProcessor.USER_NAME, mUserName);
        b.putString(UserInfoProcessor.USER_IMAGE, mUserImage);
        return b;
    }
}
