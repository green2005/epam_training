package com.epamtraining.vklite;

import android.database.Cursor;
import android.widget.TextView;

public class CursorHelper {

    public static String getString(Cursor cursor, String columnName){
        return cursor.getString(cursor.getColumnIndex(columnName));
    }


    //TODO I don't like it
    public static void setText(TextView textView, Cursor cursor, String columnName){
        textView.setText(getString(cursor, columnName));
    }

}
