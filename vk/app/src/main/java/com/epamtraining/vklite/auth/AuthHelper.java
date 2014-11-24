package com.epamtraining.vklite.auth;


import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.auth.AuthenticationException;

public class AuthHelper {
    interface AuthCallBack{
        public void onSuccess(String token);
        public void onError(Exception e);
    }
    public static final String TOKEN = "token";
    public static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static  final String CLIENT_ID = "4639292";
    public static final String SCOPE = "notify,friends,photos,audio,video,docs,status,notes,pages,wall,groups,messages,offline,notifications";
    public static final String AUTORIZATION_URL = "https://oauth.vk.com/authorize?client_id="+CLIENT_ID+"&"+
            "scope="+SCOPE+"&redirect_uri=" + REDIRECT_URL + "&display=touch&response_type=token";

    public static boolean proceedRedirectURL(Activity activity, String url, AuthCallBack callbacks) {
        if (url.startsWith(REDIRECT_URL)) {
            Uri uri = Uri.parse(url);
            String fragment = uri.getFragment();
            Uri parsedFragment = Uri.parse("http://temp.com?" + fragment);
            String accessToken = parsedFragment.getQueryParameter("access_token");
            if (!TextUtils.isEmpty(accessToken)) {
                callbacks.onSuccess(accessToken);
                return true;
            } else {
                String error = parsedFragment.getQueryParameter("error");
                String errorDescription = parsedFragment.getQueryParameter("error_description");
                String errorReason = parsedFragment.getQueryParameter("error_reason");
                if (!TextUtils.isEmpty(error)) {
                    callbacks.onError(new AuthenticationException(error+", reason : " + errorReason +"("+errorDescription+")"));
                    return false;
                } else {
                    //WTF?
                }
            }
        }
        return false;
    }
}
