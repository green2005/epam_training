package com.epamtraining.vklite.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


class DBManager extends SQLiteOpenHelper {
    private static final String DB_NAME = "VKLite";
    private static final int DB_VERSION = 24;
    private static Set<BODBHelper> sHelpers;

    static {
        sHelpers = new HashSet<>();
        sHelpers.add(new AttachmentsDBHelper());
        sHelpers.add(new CommentsDBHelper());
        sHelpers.add(new DialogDBHelper());
        sHelpers.add(new FriendDBHelper());
        sHelpers.add(new MessagesDBHelper());
        sHelpers.add(new NewsDBHelper());
        sHelpers.add(new UsersDBHelper());
        sHelpers.add(new WallDBHelper());
    }

    DBManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       dropTables(db);
       createTables(db);
    }

    private void createTables(SQLiteDatabase db){
        List<String> additionalSQL = new ArrayList<>();
        for (BODBHelper helper : sHelpers){
            db.execSQL(helper.getCreateTableDefinition());
            List<String> list = helper.getAdditionalSQL();
            if (list != null){
                additionalSQL.addAll(list);
                //additional sql for creating triggers, foreign keys etc...
            }
        };
        //make two passes because all tables should be created first
        for (String sql:additionalSQL){
            db.execSQL(sql);
        }
    }

    private void dropTables(SQLiteDatabase db){
        for (BODBHelper helper : sHelpers){
            db.execSQL(helper.getDropTableDefinition());
        }
    }
}
