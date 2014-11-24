package com.epamtraining.vklite.Processors;

import com.epamtraining.vklite.bo.Friend;
import com.epamtraining.vklite.bo.News;
import com.epamtraining.vklite.os.VKExecutor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;


public class NewsProcessor implements Processor {
    private List<News> mNews;
    private String mToken;

    public NewsProcessor(List<News> newsItems, String token) {
        mToken = token;
        mNews = newsItems;
        if (mNews == null){
            throw  new IllegalArgumentException("List cannot be null");
        }
    }

    @Override
    public String getUrl() {
        return "https://api.vk.com/method/newsfeed.get?filters=post&fields=photo_200," +
                "&access_token=" + mToken + "&v="
                + this.API_KEY;

    }

    @Override
    public void process(InputStream stream) throws Exception {
        String s = new StringReader().readFromStream(stream);
           JSONArray friendItems = new JSONObject(s).getJSONObject("response").getJSONArray("items");
            for (int i = 0; i<friendItems.length(); i++){
                JSONObject jsonObject = friendItems.getJSONObject(i);
                News newsItem = new News(jsonObject);
                mNews.add(newsItem);
            }
    }

    @Override
    public VKExecutor.ExecutorServiceType getExecutorType() {
        return VKExecutor.ExecutorServiceType.LOAD_DATA;
    }
}
