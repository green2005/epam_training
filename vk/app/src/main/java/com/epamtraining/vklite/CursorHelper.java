package com.epamtraining.vklite;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;


public class CursorHelper {
    public static String getString(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static Cursor getCursor(Uri dataUri, String[] projection, ContentResolver  resolver){
        if (resolver == null){
            throw new IllegalArgumentException("Resolver is null");
        }
        if (dataUri == null){
            throw new IllegalArgumentException("URI is null");
        }
        Cursor cursor = resolver.query(
                dataUri,
                projection, null,
                null, null);
        return cursor;
    }
}
