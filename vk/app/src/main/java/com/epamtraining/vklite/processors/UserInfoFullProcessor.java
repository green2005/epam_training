package com.epamtraining.vklite.processors;


import android.content.Context;
import android.os.Bundle;

import com.epamtraining.vklite.bo.UserInfoFull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;

public class UserInfoFullProcessor extends Processor {
    public static final String USER_INFO = "userinfo";
    private UserInfoFull mUserInfo;
   // private Context mContext;

    public UserInfoFullProcessor(Context context) {
        super(context);
     //   mContext = context;
    }

    @Override
    public void process(InputStream stream, String url, AdditionalInfoSource dataSource) throws Exception {
        JSONArray response = getVKResponseArray(stream);
        java.text.DateFormat dateFormat = DateFormat.getDateTimeInstance(); //android.text.format.DateFormat.getDateFormat(mContext);
        if (response.length() > 0) {
            JSONObject jUser = response.getJSONObject(0);
            mUserInfo = new UserInfoFull(jUser, dateFormat);
        }
    }

    @Override
    public int getRecordsFetched() {
        return 0;
    }

    @Override
    public Bundle getResult() {
        Bundle b = new Bundle();
        b.putParcelable(USER_INFO, mUserInfo);
        return b;
    }
}