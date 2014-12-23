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

public class WallProcessor implements Processor {
    private static final String RESPONSE = "response";
    private static final String ITEMS = "items";

    private String mToken;
    private Context mContext;
    private int mOffset;

    public WallProcessor(String token, Context context, int offset) {
        mToken = token;
        mContext = context;
        mOffset = offset;
    }

    @Override
    public String getUrl() {
        String s = "";
        if (mOffset > 0) {
            //подгружаем данные
            s = "&offset=" + mOffset;
        }
        return "https://api.vk.com/method/wall.get?filters=owner&fields=photo_100" +
                "&extended=1&access_token=" + mToken + "&v=" + API_KEY + s;
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
        if (mOffset == 0) {
            Cursor cursor = mContext.getContentResolver().query(
                    VKContentProvider.WALL_CONTENT_URI,
                    new String[]{"MAX(" + VKContentProvider.WALL_COLUMN_RAW_DATE + ") AS max_date"}, null,
                    null, null);
            cursor.moveToFirst();
            maxDate = cursor.getString(0);
            cursor.close();
        }

        String s = new StringReader().readFromStream(stream);
        JSONObject response = new JSONObject(s).getJSONObject(RESPONSE);
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
            if (i == 0){
                posters.process();
            }
            ContentValues value = new ContentValues();
            value.put(VKContentProvider.WALL_COLUMN_TEXT, wallItem.getText());
            value.put(VKContentProvider.WALL_COLUMN_IMAGE_URL, wallItem.getImageUrl());
            value.put(VKContentProvider.WALL_COLUMN_DATE, wallItem.getDate());
            value.put(VKContentProvider.WALL_COLUMN_RAW_DATE, wallItem.getRawDate());
            value.put(VKContentProvider.WALL_COLUMN_ITEM_ID, wallItem.getID());
            PostersProcessor.Poster poster = posters.getPoster(wallItem.getPosterId());
            if (poster != null){
                value.put(VKContentProvider.WALL_COLUMN_USERNAME, poster.getName());
                value.put(VKContentProvider.WALL_COLUMN_USERIMAGE, poster.getmImageUrl());
            }
            contentValues.add(value);
        }

        ////TODO !!!debug!!!! убрать нафик
    //    if (mOffset == 0) {
     //       mContext.getContentResolver().delete(VKContentProvider.WALL_CONTENT_URI, null, null);
     //   }

        if ((delCache) && (mOffset == 0)) {
            mContext.getContentResolver().delete(VKContentProvider.WALL_CONTENT_URI,
                    VKContentProvider.WALL_COLUMN_RAW_DATE + " <= ?",
                    new String[]{maxDate}); // удаляем старые записи
        }

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
}
