package com.epamtraining.vklite.processors;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.bo.News;
import com.epamtraining.vklite.db.AttachmentsDBHelper;
import com.epamtraining.vklite.db.NewsDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;


public class NewsProcessor extends Processor {
    private static final String ITEMS = "items";
    private static final String NEXT_FROM = "next_from";

    private Context mContext;
    private int mRecordsFetched;
    private AttachmentsDBHelper mAttachmentDBHelper;
    private NewsDBHelper mNewsDBHelper;

    public NewsProcessor(Context context) {
        super(context);
        mContext = context;
        mAttachmentDBHelper = new AttachmentsDBHelper();
        mNewsDBHelper = new NewsDBHelper();
    }

    @Override
    public void process(InputStream stream, String url, AdditionalInfoSource source) throws Exception {
        JSONObject response = getVKResponseObject(stream);
        PostersProcessor posters = new PostersProcessor(response);
        posters.process();
        JSONArray newsItems = response.getJSONArray(ITEMS);
        List<ContentValues> contentValues = new ArrayList<>();
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        String nextNewsId = response.getString(NEXT_FROM);
        List<ContentValues> attachContentValues = new ArrayList<>();

        for (int i = 0; i < newsItems.length(); i++) {
            JSONObject jsonObject = newsItems.getJSONObject(i);
            News newsItem = new News(jsonObject, dateFormat);

            List<ContentValues> attaches = mAttachmentDBHelper.getContentValues(newsItem.getAttaches(), newsItem);
            if (attaches != null) {
                attachContentValues.addAll(attaches);
            }
            newsItem.setUserInfo(posters.getPoster(Math.abs(newsItem.getPosterId())));
            ContentValues value = mNewsDBHelper.getContentValue(newsItem);
            if (i == newsItems.length() - 1) {
                value.put(NewsDBHelper.NEXT_FROM, nextNewsId);
            }
            contentValues.add(value);
        }
        if (isTopRequest(url, Api.NEWS_START_FROM)) {
            mContext.getContentResolver().delete(NewsDBHelper.CONTENT_URI,
                    null,
                    null);
        }
        ContentValues vals[] = new ContentValues[contentValues.size()];
        contentValues.toArray(vals);

        mRecordsFetched = newsItems.length();
        ContentResolver resolver = mContext.getContentResolver();
        if (vals.length > 0) {
            resolver.bulkInsert(NewsDBHelper.CONTENT_URI, vals);
        }

        ContentValues attaches[] = new ContentValues[attachContentValues.size()];
        attachContentValues.toArray(attaches);
        if (attaches.length > 0) {
            resolver.bulkInsert(AttachmentsDBHelper.CONTENT_URI, attaches);
        }
        resolver.notifyChange(NewsDBHelper.CONTENT_URI, null);
    }

    @Override
    public int getRecordsFetched() {
        return mRecordsFetched;
    }

}
