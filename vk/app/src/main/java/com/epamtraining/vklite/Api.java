package com.epamtraining.vklite;

import android.content.Context;
import android.text.TextUtils;

import java.net.URLEncoder;

public class Api {
    public static final String API_KEY="5.26";
    public static final String TOKEN_KEY = "token";
    public static final String USERID_KEY = "userid";

    private static final String ENCODE_FORMAT = "UTF-8"; //used for url encoding
    private static final String FRIENDS_URL = "https://api.vk.com/method/friends.get?fields=photo_100,nickname&order=name&access_token=%s&v=%s";
    // "https://api.vk.com/method/users.search?fields=photo_100,online,nickname&count=100&city=1&access_token=%s&v=%s";
    private static final String NEWS_URL =      "https://api.vk.com/method/newsfeed.get?filters=post&fields=photo_100" +
            "&count=10&access_token=%s&v=%s%s";
    private static final String WALL_URL = "https://api.vk.com/method/wall.get?filters=owner&fields=photo_100" +
            "&extended=1&access_token=%s&v=%s%s";
    private static  final String DIALOGS_URL = "https://api.vk.com/method/messages.getDialogs?"  +
            "access_token=%s&v=%s%s";

    private static  final String USERS_URL = "https://api.vk.com/method/users.get?"  +
            "access_token=%s&fields=photo_100&user_ids=%s&v=%s";

    private static final String MESSAGES_URL = "https://api.vk.com/method/messages.getHistory?" +
            "access_token=%s&rev=1&user_id=%s&offset=%s&v=%s&count=200";

    private static final String MESSAGES_COMMIT_URL = "https://api.vk.com/method/messages.send?" +
            "access_token=%s&user_id=%s&message=%s&v=%s";


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

    public static String getUserId(Context context){
        try {
            return VKApplication.get(context, USERID_KEY);
        } catch (Exception e) {
            ErrorHelper.showError(context, e);
        }
        return null;
    }

    public static void setUserId(VKApplication application, String userId){
        application.setUserId(userId);
    }

    public static String getMessagesCommitUrl(Context context, String userId, String message) throws Exception{
        String token = getToken(context);
        String msg = URLEncoder.encode(message, ENCODE_FORMAT);
        return String.format(MESSAGES_COMMIT_URL, token, userId, msg, API_KEY);
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

    public static String getMessagesUrl(Context context, String offset, String user_id){
        String token = getToken(context);
        return String.format(MESSAGES_URL, token, user_id, offset, API_KEY);
        // "https://api.vk.com/method/messages.getHistory?" +
        //"access_token=%s&rev=1&user_id=%s&offset=%s&v=%s"
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

    public static String getUsersUri(Context context, String userIds){
        String token = getToken(context);
        return String.format(USERS_URL, token, userIds, API_KEY);
    }

}
