package com.epamtraining.vklite.processors;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.bo.Wall;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WallProcessor extends Processor {
    private static final String ITEMS = "items";
    private int mRecordsFeched;

    private Context mContext;

    public WallProcessor(Context context) {
        super(context);
        mContext = context;
    }

    private String getPostIDByRawDate(String rawDate) {
        Cursor cursor = mContext.getContentResolver().query(
                VKContentProvider.WALL_CONTENT_URI,
                new String[]{VKContentProvider.WALL_COLUMN_ITEM_ID}, VKContentProvider.WALL_COLUMN_RAW_DATE + " = ?",
                new String[]{rawDate}, null);
        try {
            cursor.moveToFirst();
            return cursor.getString(0);
        } finally {
            cursor.close();
        }
    }

    @Override
    public void process(InputStream stream) throws Exception {
        String maxDate = null;
        String postIDWithMaxDate = null;
        if (getIsTopRequest()) {
            Cursor cursor = mContext.getContentResolver().query(
                    VKContentProvider.WALL_CONTENT_URI,
                    new String[]{"MAX(" + VKContentProvider.WALL_COLUMN_RAW_DATE + ") AS max_date"}, null,
                    null, null);
            cursor.moveToFirst();
            maxDate = cursor.getString(0);
            cursor.close();
        }

        JSONObject response = getVKResponse(stream);
        PostersProcessor posters = new PostersProcessor(response);
        posters.process();
        JSONArray wallItems = response.getJSONArray(ITEMS);
        List<ContentValues> contentValues = new ArrayList<ContentValues>();
        boolean delCache = !(TextUtils.isEmpty(maxDate));
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);

        for (int i = 0; i < wallItems.length(); i++) {
            JSONObject jsonObject = wallItems.getJSONObject(i);
            Wall wallItem = new Wall(jsonObject, dateFormat);

            if (!TextUtils.isEmpty(maxDate)) {
                int compareDates = maxDate.compareTo(wallItem.getRawDate());
                if (compareDates > 0) {
                    delCache = false;
                    break;
                } else if (compareDates == 0) {
                    if (TextUtils.isEmpty(postIDWithMaxDate)) {
                        postIDWithMaxDate = getPostIDByRawDate(maxDate);
                    }
                    if (!TextUtils.isEmpty(postIDWithMaxDate)) {
                        if (postIDWithMaxDate.equals(wallItem.getID())) {
                            delCache = false;
                            break;
                        }
                    }
                }
            }
            if (i == 0) {
                posters.process();
            }
            ContentValues value = new ContentValues();
            value.put(VKContentProvider.WALL_COLUMN_TEXT, wallItem.getText());
            value.put(VKContentProvider.WALL_COLUMN_IMAGE_URL, wallItem.getImageUrl());
            value.put(VKContentProvider.WALL_COLUMN_DATE, wallItem.getDate());
            value.put(VKContentProvider.WALL_COLUMN_RAW_DATE, wallItem.getRawDate());
            value.put(VKContentProvider.WALL_COLUMN_ITEM_ID, wallItem.getID());
            PostersProcessor.Poster poster = posters.getPoster(wallItem.getPosterId());
            if (poster != null) {
                value.put(VKContentProvider.WALL_COLUMN_USERNAME, poster.getName());
                value.put(VKContentProvider.WALL_COLUMN_USERIMAGE, poster.getmImageUrl());
            }
            contentValues.add(value);
        }

        ////TODO !!!debug!!!! убрать нафик
       // if (getIsTopRequest()) {
     //       mContext.getContentResolver().delete(VKContentProvider.WALL_CONTENT_URI, null, null);
     //   }

        if ((delCache) && (getIsTopRequest())) {
            mContext.getContentResolver().delete(VKContentProvider.WALL_CONTENT_URI,
                    VKContentProvider.WALL_COLUMN_RAW_DATE + " <= ?",
                    new String[]{maxDate}); // delete old records
        }
        mRecordsFeched = wallItems.length();
        int i = 0;
        ContentValues vals[] = new ContentValues[contentValues.size()];//friendItems.length()];
        for (ContentValues value : contentValues) {
            vals[i++] = value;
        }
        if (vals.length > 0) {
            mContext.getContentResolver().bulkInsert(VKContentProvider.WALL_CONTENT_URI, vals);
        }
        mContext.getContentResolver().notifyChange(VKContentProvider.WALL_CONTENT_URI, null);
    }

    @Override
    public int getRecordsFetched() {
        return mRecordsFeched;
    }

}