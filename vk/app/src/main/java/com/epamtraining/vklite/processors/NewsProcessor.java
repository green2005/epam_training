package com.epamtraining.vklite.processors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.bo.News;
import com.epamtraining.vklite.os.VKExecutor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class NewsProcessor implements Processor {
    private static final String ITEMS = "items";
    private static final String RESPONSE = "response";
    private static final String NEXT_FROM = "next_from";

    private String mToken;
    private Context mContext;
    private String mEnd_id;

    public NewsProcessor(String token, Context context, String endID) {
        mToken = token;
        mContext = context;
        mEnd_id = endID;
    }

    @Override
    public String getUrl() {
        String s = "";
        if (!TextUtils.isEmpty(mEnd_id)) {
            //подгружаем данные
            Cursor cursor = mContext.getContentResolver().query(
                    VKContentProvider.NEWS_CONTENT_URI,
                    new String[]{VKContentProvider.NEWS_COLUMN_NEXT_FROM}, VKContentProvider.NEWS_COLUMN_POST_ID + " = ?",
                    new String[]{mEnd_id}, null);
            cursor.moveToFirst();
            s = cursor.getString(0);
            cursor.close();
            if (!TextUtils.isEmpty(s)) {
                s = "&start_from=" + s;
            }
        }
        return "https://api.vk.com/method/newsfeed.get?filters=post&fields=photo_100" +
                "&count=10&access_token=" + mToken + "&v="
                + API_KEY + s;
    }

    private String getPostIDByRawDate(String rawDate) {
        Cursor cursor = mContext.getContentResolver().query(
                VKContentProvider.NEWS_CONTENT_URI,
                new String[]{VKContentProvider.NEWS_COLUMN_POST_ID}, VKContentProvider.NEWS_COLUMN_RAW_DATE + " = ?",
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

        if (TextUtils.isEmpty(mEnd_id)) {
            Cursor cursor = mContext.getContentResolver().query(
                    VKContentProvider.NEWS_CONTENT_URI,
                    new String[]{"MAX(" + VKContentProvider.NEWS_COLUMN_RAW_DATE + ") AS max_date"}, null,
                    null, null);
            cursor.moveToFirst();
            maxDate = cursor.getString(0);
            cursor.close();
        }

        String s = new StringReader().readFromStream(stream);
        String next_from = "";
        JSONObject response = new JSONObject(s).getJSONObject(RESPONSE);
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
                value.put(VKContentProvider.NEWS_COLUMN_USERNAME, poster.getName());
                value.put(VKContentProvider.NEWS_COLUMN_USERIMAGE, poster.getmImageUrl());
            }
            if (i == newsItems.length() - 1) {
                value.put(VKContentProvider.NEWS_COLUMN_NEXT_FROM, next_from);
            }
            contentValues.add(value);
        }

        if ((delCache) && (TextUtils.isEmpty(mEnd_id))) {
            mContext.getContentResolver().delete(VKContentProvider.NEWS_CONTENT_URI,
                    VKContentProvider.NEWS_COLUMN_RAW_DATE + " <= ?",
                    new String[]{maxDate}); // удаляем старые записи
        }

        int i = 0;
        ContentValues vals[] = new ContentValues[contentValues.size()];
        contentValues.toArray(vals);

        if (vals.length > 0) {
            mContext.getContentResolver().bulkInsert(VKContentProvider.NEWS_CONTENT_URI, vals);
        }
        mContext.getContentResolver().notifyChange(VKContentProvider.NEWS_CONTENT_URI, null);
    }
}
