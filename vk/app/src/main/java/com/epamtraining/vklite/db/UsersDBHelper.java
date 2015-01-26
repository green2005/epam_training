package com.epamtraining.vklite.db;

import android.content.ContentValues;
import android.net.Uri;

import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.bo.Friend;

import java.util.List;

public class UsersDBHelper extends BODBHelper {
    public static final String TABLENAME = "Users";
    public static final String ID = "user_id";
    public static final String NAME = "name";
    public static final String IMAGE = "image_url";
    public static final String FULL_ID = TABLENAME+"_"+"user_id";

    public static final String IMAGE_FULL = TABLENAME + "_" + IMAGE;
    private static final String[] FIELDS = {ID, NAME, IMAGE};

    public static Uri CONTENT_URI = Uri.parse(VKContentProvider.CONTENT_URI_PREFIX
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
    public String getCreateTableDefinition() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table ");
        sb.append(TABLENAME);
        sb.append(" ( ");
        sb.append(ID + " text NOT NULL PRIMARY KEY, ");
        sb.append(NAME + " text ,");
        sb.append(IMAGE + " text )");
        return sb.toString();
    }

    @Override
    public ContentValues getContentValue(BoItem item) {
        if (item == null) {
            throw new IllegalArgumentException("BOItem is null");
        }
        if (!(item instanceof Friend)) {
            throw new IllegalArgumentException("Could process Dialogs only");
        }
        Friend friend = (Friend) item;
        ContentValues value = new ContentValues();
        value.put(ID, friend.getId());
        value.put(NAME, friend.getName());
        value.put(IMAGE, friend.getImageUrl());
        return value;
    }

    @Override
    public List<ContentValues> getContentValues(BoItem item, PostSourceId postSource) {
        throw new UnsupportedOperationException("Not realized");
    }
}
