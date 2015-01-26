package com.epamtraining.vklite.db;


import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.bo.Wall;

import java.util.List;

public class WallDBHelper extends BODBHelper {
    public static final String TABLENAME = "Wall";
    public static Uri CONTENT_URI = Uri.parse(VKContentProvider.CONTENT_URI_PREFIX
            + VKContentProvider.AUTHORITY + "/" + TABLENAME);
    public static Uri CONTENT_URI_ID = Uri.parse(VKContentProvider.CONTENT_URI_PREFIX
            + VKContentProvider.AUTHORITY + "/" + TABLENAME + "/#");

    public static final String POST_ID = "post_id";
    public static final String FROM_ID = "from_id";
    public static final String OWNER_ID = "owner_id";
    public static final String RAW_DATE = "rawDate";
    public static final String DATE = "date";
    public static final String TEXT = "itemText";
    public static final String POST_TYPE = "post_type";
    public static final String IMAGE_URL = "Image_Url";
    public static final String IMAGE_WIDTH = "ImageWidth";
    public static final String IMAGE_HEIGHT = "ImageHeight";
    public static final String LEVEL = "level";
    public static final String URL = "url";
    public static final String USERNAME = "username";
    public static final String USERIMAGE = "userimage";
    public static final String NEXT_FROM = "next_From";
    public static final String CAN_COMMENT = "can_comment";

    public static final String[] FIELDS = {BaseColumns._ID, POST_ID, RAW_DATE, DATE, OWNER_ID,
            IMAGE_URL, IMAGE_WIDTH, IMAGE_HEIGHT, TEXT, NEXT_FROM, USERIMAGE, USERNAME,
            URL, POST_TYPE, LEVEL, FROM_ID, CAN_COMMENT
    };

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
        if (!(item instanceof Wall)) {
            throw new IllegalArgumentException("Could process Wall only");
        }
        Wall wallItem = (Wall) item;
        ContentValues value = new ContentValues();
        value.put(WallDBHelper.TEXT, wallItem.getText());
        value.put(WallDBHelper.IMAGE_URL, wallItem.getImageUrl());
        value.put(WallDBHelper.DATE, wallItem.getDate());
        value.put(WallDBHelper.RAW_DATE, wallItem.getRawDate());
        value.put(WallDBHelper.POST_ID, wallItem.getId());
        value.put(WallDBHelper.USERNAME, wallItem.getUserName());
        value.put(WallDBHelper.USERIMAGE, wallItem.getUserImage());
        value.put(WallDBHelper.OWNER_ID, wallItem.getPosterId());
        value.put(WallDBHelper.IMAGE_WIDTH, wallItem.getImageWidth());
        value.put(WallDBHelper.IMAGE_HEIGHT, wallItem.getImageHeight());
        value.put(WallDBHelper.CAN_COMMENT, wallItem.getCanComment());
        return value;
    }

    @Override
    public List<ContentValues> getContentValues(BoItem item, PostSourceId postSource) {
        throw new UnsupportedOperationException("Not realized");
    }
}
