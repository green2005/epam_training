package com.epamtraining.vklite.processors;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

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

    @Override
    public void process(InputStream stream, AdditionalInfoSource dataSource) throws Exception {
        Wall wallItem;
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
            wallContentValues.add(mWallHelper.getContentValue(wallItem));
        }
        if (getIsTopRequest()) {
            mContext.getContentResolver().delete(WallDBHelper.CONTENT_URI,
                    null,
                    null);
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

    @Override
    public int getRecordsFetched() {
        return mRecordsFeched;
    }

}