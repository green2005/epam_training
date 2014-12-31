package com.epamtraining.vklite;

import android.content.Context;
import android.text.TextUtils;

public class Api {
    public static final String API_KEY="5.26";
    public static final String TOKEN_KEY = "token";
    private static final String FRIENDS_URL = "https://api.vk.com/method/users.search?fields=photo_100,online,nickname&count=100&city=1&access_token=%s&v=%s";
    private static final String NEWS_URL =      "https://api.vk.com/method/newsfeed.get?filters=post&fields=photo_100" +
            "&count=10&access_token=%s&v=%s%s";
    private static final String WALL_URL = "https://api.vk.com/method/wall.get?filters=owner&fields=photo_100" +
            "&extended=1&access_token=%s&v=%s%s";
    private static  final String DIALOGS_URL = "https://api.vk.com/method/messages.getDialogs?"  +
            "access_token=%s&v=%s%s";


    public static String getToken(Context context){
        try {
            return VKApplication.get(context, TOKEN_KEY);
        } catch (Exception e) {
            ErrorHelper.showError(context, e);
        }
        return null;
    }

    public static void setToken(VKApplication application, String token){
        application.setToken(token);
    }

    public static String getFriendsUrl(Context context){
        String token = getToken(context);
        return String.format(FRIENDS_URL, token, API_KEY).toString();
    }

    public static String getWallUrl(Context context, String offset){
        String token = getToken(context);
        String sOffset = "";
        if (!TextUtils.isEmpty(offset)){
            sOffset = String.format("&offset=%s",offset);
        }
        return String.format(WALL_URL, token, API_KEY, sOffset);
    }

    public static String getDialogsUrl(Context context, String offset){
        String token = getToken(context);
        String sOffset = "";
        if (!TextUtils.isEmpty(offset)){
            sOffset = String.format("&offset=%s",offset);
        }
        return String.format(DIALOGS_URL, token, API_KEY, sOffset);
    }

    public static  String getNewsUrl(Context context, String endId){
        String token = getToken(context);
        String sEndId = "";
        if (!TextUtils.isEmpty(endId)){
            sEndId =  String.format("&start_from=%s",endId);
        }
        return String.format(NEWS_URL, token, API_KEY, sEndId);
    }

}
