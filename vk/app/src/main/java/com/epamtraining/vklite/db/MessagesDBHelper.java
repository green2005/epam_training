package com.epamtraining.vklite.db;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.bo.Message;

import java.util.List;

public class MessagesDBHelper extends BODBHelper{
    public static final String TABLENAME = "Messages";

    public static final String ID = "message_id";
    public static final String BODY = "body";
    public static final String TITLE = "title";
    public static final String RAW_DATE = "raw_date";
    public static final String DATE = "date";
    public static final String USER_ID = "user_id";
    public static final String FROM_ID = "from_id";
    public static final String IMAGE_URL = "image_url";
    public static final String OUT = "out";
    public static final String PENDING = "pending";
    public static final String USER_DIALOG_ID = "user_dialog_id";

    private static final String [] FIELDS = {BaseColumns._ID, ID, BODY, TITLE, RAW_DATE, DATE, USER_ID,
            FROM_ID, IMAGE_URL, OUT, PENDING, USER_DIALOG_ID};
    public static final Uri CONTENT_URI = Uri.parse(VKContentProvider.CONTENT_URI_PREFIX
            + VKContentProvider.AUTHORITY + "/" + TABLENAME);
    public static Uri CONTENT_URI_ID = Uri.parse(VKContentProvider.CONTENT_URI_PREFIX
            + VKContentProvider.AUTHORITY + "/" + TABLENAME+"/#");

    @Override
    public String getTableName() {
        return TABLENAME;
    }

    @Override
    protected String getFieldDefaultValue(String fieldName){
       return null;
    }

    @Override
    public String[] fieldNames() {
         return FIELDS;
    }

    @Override
    public String getCreateTableDefinition(){
        StringBuilder sb = new StringBuilder();
        sb.append("create table ");
        sb.append(getTableName());
        sb.append(" ( ");
        sb.append( BaseColumns._ID + " Integer NOT NULL PRIMARY KEY AUTOINCREMENT ,");
        sb.append(ID + " text ,");
        sb.append(BODY + " text ,");
        sb.append(TITLE + " text ,");
        sb.append(RAW_DATE + " text ,");
        sb.append(DATE + " text ,");
        sb.append(USER_ID + " text ,");
        sb.append(FROM_ID + " text ,");
        sb.append(IMAGE_URL + " text ,");
        sb.append(OUT + " text ,");
        sb.append(USER_DIALOG_ID+" text , ");
        sb.append(PENDING + " int default 0)");
        return sb.toString();
    }

    @Override
    public ContentValues getContentValue(BoItem item) {
        if (item == null){
            throw new IllegalArgumentException("BOItem is null");
        }
        if (!(item instanceof Message)){
            throw new IllegalArgumentException("Could process Message only");
        }
        Message msg = (Message) item;
        ContentValues value = new ContentValues();
        value.put( BODY, msg.getBody());
        value.put( DATE, msg.getDate());
        value.put( RAW_DATE, msg.getRawDate());
        value.put( ID, msg.getId());
        value.put( FROM_ID, msg.getFromId());
        value.put( OUT, msg.getOut());
        if (!TextUtils.isEmpty(msg.getmImageUrl())) {
            value.put( IMAGE_URL, msg.getmImageUrl());
        }
        value.put(PENDING, 0);
        return value;
    }

    @Override
    public List<ContentValues> getContentValues(BoItem item, PostSourceId postSource) {
         throw new UnsupportedOperationException("Not realized");
    }
}
