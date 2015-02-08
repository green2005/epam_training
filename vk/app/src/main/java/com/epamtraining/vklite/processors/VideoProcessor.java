package com.epamtraining.vklite.processors;

import android.content.Context;
import android.os.Bundle;

import com.epamtraining.vklite.bo.Wall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class VideoProcessor extends Processor {
    private static final String ITEMS = "items";
    public static final String PLAYER = "player";
    private String mPlayer;

    public VideoProcessor(Context context) {
        super(context);
    }

    @Override
    public void process(InputStream stream, String url, AdditionalInfoSource dataSource) throws Exception {
        JSONObject response = getVKResponseObject(stream);
        JSONArray items = response.optJSONArray(ITEMS);
        if (items != null && items.length()>0){
            mPlayer = items.getJSONObject(0).optString(PLAYER);
        }
    }

    @Override
    public int getRecordsFetched() {
        return 1;
    }

    @Override
    public Bundle getResult() {
        Bundle b = new Bundle();
        b.putString(PLAYER, mPlayer);
        return b;
    }
}
