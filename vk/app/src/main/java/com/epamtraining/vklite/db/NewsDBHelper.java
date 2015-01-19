package com.epamtraining.vklite.db;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.bo.News;

import java.util.List;

public class NewsDBHelper extends BODBHelper {
    private static final String TABLENAME = "News";

    public static final String DATE = "Date";
    public static final String TEXT = "NewsText";
    public static final String URL = "Url";
    public static final String IMAGE_URL = "Image_Url";
    public static final String POST_ID = "Post_id";
    public static final String OWNER_ID = "owner_id";
    public static final String RAW_DATE = "Raw_Date";
    public static final String NEXT_FROM = "Next_From";
    public static final String USERNAME = "username";
    public static final String USERIMAGE = "userimage";
    public static final String[] fields = {BaseColumns._ID, POST_ID, RAW_DATE, DATE, OWNER_ID, IMAGE_URL, TEXT, NEXT_FROM, USERIMAGE, USERNAME, URL};

    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + TABLENAME);

    @Override
    public String getTableName() {
        return TABLENAME;
    }

    @Override
    public String[] fieldNames() {
        return fields;
    }

    @Override
    public Uri getContentUri() {
        return CONTENT_URI;
    }

    @Override
    public ContentValues getContentValue(BoItem item) {
        if (item == null) {
            throw new IllegalArgumentException("BOItem is null");
        }
        if (!(item instanceof News)) {
            throw new IllegalArgumentException("Could process News only");
        }
        News newsItem = (News) item;
        ContentValues value = new ContentValues();
        value.put(NewsDBHelper.TEXT, newsItem.getText());
        value.put(NewsDBHelper.IMAGE_URL, newsItem.getImageUrl());
        value.put(NewsDBHelper.DATE, newsItem.getDate());
        value.put(NewsDBHelper.RAW_DATE, newsItem.getRawDate());
        value.put(NewsDBHelper.POST_ID, newsItem.getPostId());
        value.put(NewsDBHelper.OWNER_ID, newsItem.getPosterId());
        value.put(NewsDBHelper.USERNAME, newsItem.getUserName());
        value.put(NewsDBHelper.USERIMAGE, newsItem.getUserImage());
        return value;
    }

    @Override
    public List<ContentValues> getContentValues(BoItem item, PostSourceId postSource) {
          throw new UnsupportedOperationException("Not realized");
        //return null;
    }
}
