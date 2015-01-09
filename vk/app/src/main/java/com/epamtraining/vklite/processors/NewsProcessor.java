package com.epamtraining.vklite.processors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.bo.News;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class NewsProcessor extends Processor {
    private static final String ITEMS = "items";
    private static final String NEXT_FROM = "next_from";

    private Context mContext;
    private int mRecordsFetched;

    public NewsProcessor(Context context) {
        super(context);
        mContext = context;
    }

    private String getPostIDByRawDate(String rawDate) {
        Cursor cursor = mContext.getContentResolver().query(
                VKContentProvider.NEWS_CONTENT_URI,
                new String[]{VKContentProvider.NEWS_COLUMN_POST_ID}, VKContentProvider.NEWS_COLUMN_RAW_DATE + " = ?",
                new String[]{rawDate}, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getString(0);
            } else
            {return null;}
        } finally {
            cursor.close();
        }
    }

    @Override
    public void process(InputStream stream, AdditionalInfoSource source) throws Exception {
        String maxDate = null;
        String postIDWithMaxDate = null;

        if (getIsTopRequest()) {
            //TODO create helper to work with cursors
            Cursor cursor = mContext.getContentResolver().query(
                    VKContentProvider.NEWS_CONTENT_URI,
                    new String[]{"MAX(" + VKContentProvider.NEWS_COLUMN_RAW_DATE + ") AS max_date"}, null,
                    null, null);
            cursor.moveToFirst();
            maxDate = cursor.getString(0);
            cursor.close();
        }

        String next_from = "";
        JSONObject response = getVKResponseObject(stream);
        PostersProcessor posters = new PostersProcessor(response);
        next_from = response.getString(NEXT_FROM);
        JSONArray newsItems = response.getJSONArray(ITEMS);
        List<ContentValues> contentValues = new ArrayList<ContentValues>();
        boolean delCache = !(TextUtils.isEmpty(maxDate));
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);

        for (int i = 0; i < newsItems.length(); i++) {
            JSONObject jsonObject = newsItems.getJSONObject(i);
            News newsItem = new News(jsonObject, dateFormat);
            if (!TextUtils.isEmpty(maxDate)) {
                int compareDates = maxDate.compareTo(newsItem.getRawDate());
                if (compareDates > 0) {
                    delCache = false;
                    break;
                } else if (compareDates == 0) {
                    if (TextUtils.isEmpty(postIDWithMaxDate)) {
                        postIDWithMaxDate = getPostIDByRawDate(maxDate);
                    }
                    if (!TextUtils.isEmpty(postIDWithMaxDate)) {
                        if (postIDWithMaxDate.equals(newsItem.getPostId())) {
                            delCache = false;
                            break;
                        }
                    }
                }
            }

            if (i == 0) {
                posters.process();
            }
            PostersProcessor.Poster poster = posters.getPoster(newsItem.getPosterId());
            ContentValues value = new ContentValues();
            value.put(VKContentProvider.NEWS_COLUMN_TEXT, newsItem.getText());
            value.put(VKContentProvider.NEWS_COLUMN_IMAGE_URL, newsItem.getImageUrl());
            value.put(VKContentProvider.NEWS_COLUMN_DATE, newsItem.getDate());
            value.put(VKContentProvider.NEWS_COLUMN_RAW_DATE, newsItem.getRawDate());
            value.put(VKContentProvider.NEWS_COLUMN_POST_ID, newsItem.getPostId());
            if (poster != null) {
                value.put(VKContentProvider.NEWS_COLUMN_OWNER_ID, newsItem.getPosterId());
                value.put(VKContentProvider.NEWS_COLUMN_USERNAME, poster.getName());
                value.put(VKContentProvider.NEWS_COLUMN_USERIMAGE, poster.getmImageUrl());
            }
            if (i == newsItems.length() - 1) {
                value.put(VKContentProvider.NEWS_COLUMN_NEXT_FROM, next_from);
            }
            contentValues.add(value);
        }

        if ((delCache) && (getIsTopRequest())) {
            mContext.getContentResolver().delete(VKContentProvider.NEWS_CONTENT_URI,
                    VKContentProvider.NEWS_COLUMN_RAW_DATE + " <= ?",
                    new String[]{maxDate}); //delete old records from cache
        }

        int i = 0;
        ContentValues vals[] = new ContentValues[contentValues.size()];
        contentValues.toArray(vals);
        mRecordsFetched = newsItems.length();
        if (vals.length > 0) {
            mContext.getContentResolver().bulkInsert(VKContentProvider.NEWS_CONTENT_URI, vals);
        }
        mContext.getContentResolver().notifyChange(VKContentProvider.NEWS_CONTENT_URI, null);
    }

    @Override
    public int getRecordsFetched() {
        return mRecordsFetched;
    }

}
