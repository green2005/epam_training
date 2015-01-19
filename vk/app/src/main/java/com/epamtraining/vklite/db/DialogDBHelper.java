package com.epamtraining.vklite.db;


import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.bo.Dialog;

import java.util.List;

public class DialogDBHelper  extends BODBHelper {
    protected static final String TABLENAME = "Dialogs";
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + TABLENAME);
    public String [] fields = {BaseColumns._ID, ID ,BODY, TITLE, RAW_DATE, DATE, USER_ID};

    public static final String ID = "message_id";
    public static final String BODY = "body";
    public static final String TITLE = "title";
    public static final String RAW_DATE = "raw_date";
    public static final String DATE = "date";
    public static final String USER_ID = "user_id";

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
        if (item == null){
            throw new IllegalArgumentException("BOItem is null");
        }
        if (!(item instanceof Dialog)){
            throw new IllegalArgumentException("Could process Dialogs only");
        }
        Dialog msg = (Dialog) item;
        ContentValues value = new ContentValues();
        value.put(BODY, msg.getBody());
        value.put(DATE, msg.getDate());
        value.put(RAW_DATE, msg.getRawDate());
        value.put(ID, msg.getId());
        value.put(TITLE, msg.getTitle());
        value.put(USER_ID, msg.getUserId());
        return value;
    }

    @Override
    public List<ContentValues> getContentValues(BoItem item, PostSourceId postSource) {
         throw new UnsupportedOperationException("Not realized");
    }


}
