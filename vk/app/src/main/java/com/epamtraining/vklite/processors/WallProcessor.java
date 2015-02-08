package com.epamtraining.vklite.processors;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.bo.Wall;
import com.epamtraining.vklite.db.AttachmentsDBHelper;
import com.epamtraining.vklite.db.WallDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WallProcessor extends Processor {
    private static final String ITEMS = "items";
    private static final String OWNER = "owner_id";
    private int mRecordsFeched;
    private Context mContext;
    private AttachmentsDBHelper mAttachmentsHelper;
    private WallDBHelper mWallHelper;

    public WallProcessor(Context context) {
        super(context);
        mContext = context;
        mAttachmentsHelper = new AttachmentsDBHelper();
        mWallHelper = new WallDBHelper();
    }
//https://api.vk.com/method/wall.get?filters=owner&fields=photo_100&owner_id=25931259&extended=1&access_token=fc7f9128e8bfc5d01461fa7b2950705a4ff4f949142912314ad7bde930681e77a6e5dba58ce7ce9dc082c&v=5.26&offset=0
    @Override
    public void process(InputStream stream, String url, AdditionalInfoSource dataSource) throws Exception {
        Wall wallItem;
        String wallOwnerId = getWallOwnerId(url);
        mWallHelper.setWallOwnerId(wallOwnerId);
        JSONObject response = getVKResponseObject(stream);
        PostersProcessor posters = new PostersProcessor(response);
        posters.process();
        JSONArray wallItems = response.getJSONArray(ITEMS);
        List<ContentValues> attachContentValues = new ArrayList<>();
        List<ContentValues> wallContentValues = new ArrayList<>();
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        for (int i = 0; i < wallItems.length(); i++) {
            JSONObject jsonObject = wallItems.getJSONObject(i);
            wallItem = new Wall(jsonObject, dateFormat);
            wallItem.setUserInfo(posters.getPoster(Math.abs(wallItem.getPosterId())));
            List<ContentValues> attaches = mAttachmentsHelper.getContentValues(wallItem.getAttaches(), wallItem);
            if (attaches != null) {
                attachContentValues.addAll(attaches);
            }
            wallContentValues.add(mWallHelper.getContentValue(wallItem ));
        }
        if (isTopRequest(url, Api.OFFSET)) {
            mContext.getContentResolver().delete(WallDBHelper.CONTENT_URI,
                    WallDBHelper.WALL_OWNER_ID +" = ? ",
                    new String[]{wallOwnerId});
        }
        mRecordsFeched = wallItems.length();
        ContentValues vals[] = new ContentValues[wallContentValues.size()];
        wallContentValues.toArray(vals);
        ContentResolver resolver = mContext.getContentResolver();
        if (vals.length > 0) {
            resolver.bulkInsert(WallDBHelper.CONTENT_URI, vals);
        }
        ContentValues attaches[] = new ContentValues[attachContentValues.size()];
        attachContentValues.toArray(attaches);
        if (attaches.length > 0) {
            resolver.bulkInsert(AttachmentsDBHelper.CONTENT_URI, attaches);
        }
        resolver.notifyChange(WallDBHelper.CONTENT_URI, null);
    }

    private String getWallOwnerId(String url){
        Uri parsedFragment = Uri.parse(url);
        return parsedFragment.getQueryParameter(OWNER);
    }

    @Override
    public int getRecordsFetched() {
        return mRecordsFeched;
    }
}