package com.epamtraining.vklite.auth;


import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.processors.StringReader;

import org.apache.http.auth.AuthenticationException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;


public class AuthHelper {
    interface AuthCallBack {
        public void onSuccess(String token, String currentUserId);

        public void onError(Exception e);
    }

    public static final String TOKEN = "token";
    public static final String USER_ID = "user_id";
    public static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static final String CLIENT_ID = "4639292";
    public static final String SCOPE = "notify,friends,photos,audio,video,docs,status,notes,pages,wall,groups,messages,offline,notifications";
    public static final String AUTORIZATION_URL = "https://oauth.vk.com/authorize?client_id=" + CLIENT_ID + "&" +
            "scope=" + SCOPE + "&redirect_uri=" + REDIRECT_URL + "&display=touch&response_type=token";

    private static final String USERS_URL = "https://api.vk.com/method/users.get?" +
            "access_token=%s&FIELDS=photo_100&v=%s";
    private static final String ID = "id";
    private static final String RESPONSE = "response";


    public static boolean proceedRedirectURL(Activity activity, String url, AuthCallBack callbacks) {
        if (url.startsWith(REDIRECT_URL)) {
            Uri uri = Uri.parse(url);
            String fragment = uri.getFragment();
            Uri parsedFragment = Uri.parse("http://temp.com?" + fragment);
            String accessToken = parsedFragment.getQueryParameter("access_token");
            if (!TextUtils.isEmpty(accessToken)) {
                fillUserInfo(accessToken, activity, callbacks);
                // callbacks.onSuccess(accessToken);
                return true;
            } else {
                String error = parsedFragment.getQueryParameter("error");
                String errorDescription = parsedFragment.getQueryParameter("error_description");
                String errorReason = parsedFragment.getQueryParameter("error_reason");
                if (!TextUtils.isEmpty(error)) {
                    callbacks.onError(new AuthenticationException(error + ", reason : " + errorReason + "(" + errorDescription + ")"));
                    return false;
                } else {
                }
            }
        }
        return false;
    }

    private static void fillUserInfo(final String token, Activity activity, final AuthCallBack callbacks) {
        //Api.getUsersUri(thi,"");
        final Handler handler= new Handler();
        new Thread(new Runnable() {
            String currentId;
            @Override
            public void run() {
                    try {
                        String href = String.format(USERS_URL, token, Api.API_KEY);
                        URL url = new URL(href);
                        InputStream stream = url.openStream();
                        String s = new StringReader().readFromStream(stream);
                        JSONObject jo = new JSONObject(s);
                        currentId = jo.optJSONArray(RESPONSE).getJSONObject(0).optInt("id")+"";
                    }catch (final Exception e){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callbacks.onError(e);
                            }
                        });
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callbacks.onSuccess(token, currentId);
                        }
                    });
            }
        }).start();

    }

}
