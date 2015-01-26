package com.epamtraining.vklite.db;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.bo.News;

import java.util.List;

public class NewsDBHelper extends BODBHelper {
    public static final String TABLENAME = "News";

    public static final String DATE = "Date";
    public static final String TEXT = "NewsText";
    public static final String URL = "Url";
    public static final String IMAGE_URL = "Image_Url";
    public static final String IMAGE_WIDTH = "ImageWidth";
    public static final String IMAGE_HEIGHT = "ImageHeight";
    public static final String POST_ID = "Post_id";
    public static final String OWNER_ID = "owner_id";
    public static final String RAW_DATE = "Raw_date";
    public static final String NEXT_FROM = "Next_from";
    public static final String USERNAME = "username";
    public static final String USERIMAGE = "userimage";
    public static final String CAN_COMMENT = "can_comment";

    public static final String[] FIELDS = {BaseColumns._ID, POST_ID, RAW_DATE, DATE,
            OWNER_ID, IMAGE_URL, IMAGE_WIDTH, IMAGE_HEIGHT,
            TEXT, NEXT_FROM, USERIMAGE, USERNAME, URL, CAN_COMMENT};

    public static final Uri CONTENT_URI = Uri.parse(VKContentProvider.CONTENT_URI_PREFIX
            + VKContentProvider.AUTHORITY + "/" + TABLENAME);
    public static Uri CONTENT_URI_ID = Uri.parse(VKContentProvider.CONTENT_URI_PREFIX
            + VKContentProvider.AUTHORITY + "/" + TABLENAME + "/#");

    @Override
    public String getTableName() {
        return TABLENAME;
    }

    @Override
    public String[] fieldNames() {
        return FIELDS;
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
        value.put(NewsDBHelper.DATE, newsItem.getDate());
        value.put(NewsDBHelper.RAW_DATE, newsItem.getRawDate());
        value.put(NewsDBHelper.POST_ID, newsItem.getPostId());
        value.put(NewsDBHelper.OWNER_ID, newsItem.getPosterId());
        value.put(NewsDBHelper.USERNAME, newsItem.getUserName());
        value.put(NewsDBHelper.USERIMAGE, newsItem.getUserImage());
        value.put(NewsDBHelper.IMAGE_URL, newsItem.getImageUrl());
        value.put(NewsDBHelper.IMAGE_WIDTH, newsItem.getImageWidth());
        value.put(NewsDBHelper.IMAGE_HEIGHT, newsItem.getImageHeight());
        value.put(NewsDBHelper.CAN_COMMENT, newsItem.getCanComment());
        return value;
    }

    @Override
    public List<ContentValues> getContentValues(BoItem item, PostSourceId postSource) {
        throw new UnsupportedOperationException("Not realized");
        //return null;
    }
}
