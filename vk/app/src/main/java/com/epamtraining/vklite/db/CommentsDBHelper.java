package com.epamtraining.vklite.db;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.bo.Comment;

import java.util.List;

public class CommentsDBHelper extends  BODBHelper{
    public static String TABLENAME = "Comments";
    public static final String DATE = "Date";
    public static final String TEXT = "Text";
    public static final String COMMENT_ID = "comment_id";
    public static final String OWNER_ID = "owner_id";
    public static final String RAW_DATE = "Raw_Date";
    public static final String USERNAME = "username";
    public static final String USERIMAGE = "userimage";
    public static final String POST_ID = "post_id";
    public static final String PENDING = "pending";
    public static final Uri CONTENT_URI = Uri.parse(VKContentProvider.CONTENT_URI_PREFIX
            + VKContentProvider.AUTHORITY + "/" + TABLENAME);
    public static Uri CONTENT_URI_ID = Uri.parse(VKContentProvider.CONTENT_URI_PREFIX
            + VKContentProvider.AUTHORITY + "/" + TABLENAME+"/#");

    public static final String[] FIELDS = {BaseColumns._ID, COMMENT_ID, RAW_DATE, DATE, OWNER_ID,
                                            TEXT,  USERIMAGE, USERNAME, POST_ID, PENDING};

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
        if (!(item instanceof Comment)) {
            throw new IllegalArgumentException("Could process Comments only");
        }
        Comment commentItem = (Comment) item;
        ContentValues value = new ContentValues();
        value.put(TEXT, commentItem.getText());
        value.put(DATE, commentItem.getDate());
        value.put(RAW_DATE, commentItem.getRawDate());
        value.put(COMMENT_ID, commentItem.getId());
        value.put(USERIMAGE, commentItem.getUserImage());
        value.put(USERNAME, commentItem.getUserName());
        return value;
    }

    @Override
    public List<ContentValues> getContentValues(BoItem item, PostSourceId postSource) {
        throw new UnsupportedOperationException("Not realized");
    }
}
