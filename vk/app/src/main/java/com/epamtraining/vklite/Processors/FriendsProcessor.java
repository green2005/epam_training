package com.epamtraining.vklite.Processors;


import com.epamtraining.vklite.bo.Friend;
import com.epamtraining.vklite.os.VKExecutor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class FriendsProcessor implements  Processor{
    private List<Friend> mFriends;
    private String mToken;

    public  FriendsProcessor(List<Friend> friends, String token){
        mFriends = friends;
        mToken = token;
        if (mFriends == null){
            throw  new IllegalArgumentException("List cannot be null");
        }
    }

    @Override
    public String getUrl() {
        return   "https://api.vk.com/method/users.search?fields=photo_200_orig,online,nickname&count=100&city=1&access_token="+mToken+"&v="
                +this.API_KEY;
      }

    @Override
    public void process(InputStream stream) throws Exception{
       String s = new StringReader().readFromStream(stream);
           JSONArray friendItems = new JSONObject(s).getJSONObject("response").getJSONArray("items");
            for (int i = 0; i<friendItems.length(); i++){
                JSONObject jsonObject = friendItems.getJSONObject(i);
                Friend friend = new Friend(jsonObject);
                mFriends.add(friend);
            }
    }

    @Override
    public VKExecutor.ExecutorServiceType getExecutorType() {
        return VKExecutor.ExecutorServiceType.LOAD_DATA;
    }
}
