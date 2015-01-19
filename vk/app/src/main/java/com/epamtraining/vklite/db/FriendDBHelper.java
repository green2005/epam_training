package com.epamtraining.vklite.db;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.bo.Friend;

import java.util.List;

public class FriendDBHelper extends BODBHelper {
     private static final String TABLENAME = "Friends";
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + TABLENAME);

    public static final String ID = "id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String NICK_NAME = "nick_name";
    public static final String IMAGE_URL = "image_url";
    public static final String[] fields =  {BaseColumns._ID, ID, FIRST_NAME, LAST_NAME, NICK_NAME, IMAGE_URL};


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
        if (!(item instanceof Friend)){
            throw new IllegalArgumentException("Could process Friend only");
        }
        Friend friend = (Friend) item;
        ContentValues value = new ContentValues();
        value.put(ID, friend.getId());
        value.put(FIRST_NAME, friend.getFirstName());
        value.put( LAST_NAME, friend.getLastName());
        value.put( IMAGE_URL, friend.getImageUrl());
        value.put( NICK_NAME, friend.getNick());
        return value;
    }

    @Override
    public List<ContentValues> getContentValues(BoItem item, PostSourceId postSource) {
          throw new UnsupportedOperationException("Not realized");
    }
}
