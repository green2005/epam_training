package com.epamtraining.vklite;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

//TODO create helper to work with tables,
//TODO ideal create simple ORM

//TODO трэш, отрефакторить
public class VKContentProvider extends ContentProvider {
    private static final String DB_NAME = "VKLite";
    private static final int DB_VERSION = 1;

    private static final String NEWS_TABLENAME = "News";
    private static final String FRIENDS_TABLENAME = "Friends";
    private static final String WALL_TABLENAME = "Wall";
    private static final String DIALOGS_TABLENAME = "Dialogs";
    private static final String USERS_TABLENAME = "Users";
    private static final String MESSAGES_TABLENAME = "Messages";

    private static final String DB_DROP_NEWS = "DROP TABLE IF EXISTS News";
    private static final String DB_DROP_FRIENDS = "DROP TABLE IF EXISTS Friends";
    private static final String DB_DROP_WALL = "DROP TABLE IF EXISTS Wall";
    private static final String DB_DROP_DIALOGS = "DROP TABLE IF EXISTS Dialogs";
    private static final String DB_DROP_USERS = "DROP TABLE IF EXISTS Users";
    private static final String DB_DROP_MESSAGES = "DROP TABLE IF EXISTS Messages";

    private static final String DB_CREATE_NEWS = "CREATE TABLE News (_id Integer NOT NULL PRIMARY KEY AUTOINCREMENT,NewsText text,owner_id text,Url text,Date text,Image_Url text, Post_id text, Raw_Date text, Next_From text, username text, userimage text) ";
    private static final String DB_CREATE_FRIENDS = "CREATE TABLE Friends(_id Integer NOT NULL PRIMARY KEY AUTOINCREMENT, First_Name text, Last_Name text, Image_Url text, id text, Nick_Name text)";
    private static final String DB_CREATE_WALL = "CREATE TABLE Wall(_id Integer not null PRIMARY KEY AUTOINCREMENT, Post_id text, from_id text," +
            " owner_id text, rawDate text, date text, itemText text, post_type text, Image_Url text, level int, Url text, username text, userimage text)";
    private static final String DB_CREATE_DIALOGS = "CREATE TABLE Dialogs(_id Integer NOT NULL PRIMARY KEY AUTOINCREMENT, message_id text, body text, title text, Raw_Date text, Date text, user_id text )";
    private static final String DB_CREATE_USERS = "CREATE TABLE Users(user_id text NOT NULL PRIMARY KEY, name text, image_url text)";
    private static final String DB_CREATE_MESSAGES = "CREATE TABLE Messages(_id Integer NOT NULL PRIMARY KEY AUTOINCREMENT, message_id text, body text, Raw_Date text, Date text, from_id text,user_id text, image_url text, message_out text, pending int DEFAULT 0)";
    //table for message users

    public static final String NEWS_COLUMN_DATE = "Date";
    public static final String NEWS_COULMN_ID = "_id";
    public static final String NEWS_COLUMN_TEXT = "NewsText";
    public static final String NEWS_COLUMN_URL = "Url";
    public static final String NEWS_COLUMN_IMAGE_URL = "Image_Url";
    public static final String NEWS_COLUMN_POST_ID = "Post_id";
    public static final String NEWS_COLUMN_OWNER_ID = "owner_id";
    public static final String NEWS_COLUMN_RAW_DATE = "Raw_Date";
    public static final String NEWS_COLUMN_NEXT_FROM = "Next_From";
    public static final String NEWS_COLUMN_USERNAME = "username";
    public static final String NEWS_COLUMN_USERIMAGE = "userimage";

    public static final String FRIEND_COLUMN_ID = "id";
    public static final String FRIEND_COLUMN_FIRST_NAME = "First_Name";
    public static final String FRIEND_COLUMN_LAST_NAME = "Last_Name";
    public static final String FRIEND_COLUMN_NICK_NAME = "Nick_Name";
    public static final String FRIEND_COLUMN_IMAGE_URL = "Image_Url";

    public static final String WALL_COLUMN_ID = "_id";
    public static final String WALL_COLUMN_ITEM_ID = "Post_id";
    public static final String WALL_COLUMN_FROM_ID = "from_id";
    public static final String WALL_COLUMN_OWNER_ID = "owner_id";
    public static final String WALL_COLUMN_RAW_DATE = "rawDate";
    public static final String WALL_COLUMN_DATE = "date";
    public static final String WALL_COLUMN_TEXT = "itemText";
    public static final String WALL_COLUMN_POST_TYPE = "post_type";
    public static final String WALL_COLUMN_IMAGE_URL = "Image_Url";
    public static final String WALL_COLUMN_LEVEL = "level";
    public static final String WALL_COLUMN_URL = "Url";
    public static final String WALL_COLUMN_USERNAME = "username";
    public static final String WALL_COLUMN_USERIMAGE = "userimage";
    public static final String WALL_COLUMN_NEXT_FROM = "Next_From";

    public static final String DIALOGS_COLUMN_ID = "_id";
    public static final String DIALOGS_COLUMN_MESSAGE_ID = "message_id";
    public static final String DIALOGS_COLUMN_BODY = "body";
    public static final String DIALOGS_COLUMN_TITLE = "title";
    public static final String DIALOGS_COLUMN_RAW_DATE = "Raw_Date";
    public static final String DIALOGS_COLUMN_DATE = "Date";
    public static final String DIALOGS_COLUMN_USER_ID = "user_id";

    public static final String USERS_COLUMN_ID = "user_id";
    public static final String USERS_COLUMN_NAME = "name";
    public static final String USERS_COLUMN_IMAGE = "image_url";
    public static final String USERS_COLUMN_IMAGE_FULL = USERS_TABLENAME + "_" + USERS_COLUMN_IMAGE;

    public static final String MESSAGES_ID = "_id";
    public static final String MESSAGES_MESSAGE_ID = "message_id";
    public static final String MESSAGES_COLUMN_BODY = "body";
    public static final String MESSAGES_RAW_DATE = "Raw_Date";
    public static final String MESSAGES_DATE = "Date";
    public static final String MESSAGES_USER_ID = "user_id";
    public static final String MESSAGES_USER_FROM_ID = "from_id";
    public static final String MESSAGES_IMAGE_URL = "image_url";
    public static final String MESSAGES_OUT = "message_out";
    public static final String MESSAGES_PENDING = "pending";

    private static final String AUTHORITY = "com.epamtraining.vk";


    private static final int URI_NEWS = 1;
    private static final int URI_NEWS_ID = 2;
    private static final int URI_FRIENDS = 3;
    private static final int URI_FRIENDS_ID = 4;
    private static final int URI_WALL = 5;
    private static final int URI_WALL_ID = 6;
    private static final int URI_DIALOGS = 7;
    private static final int URI_DIALOGS_ID = 8;
    private static final int URI_USERS = 9;
    private static final int URI_USERS_ID = 10;
    private static final int URI_MESSAGES = 11;
    private static final int URI_MESSAGES_ID = 12;

    private static final String NEWS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + NEWS_TABLENAME;

    private static final String NEWS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + NEWS_TABLENAME;

    private static final String WALL_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + WALL_TABLENAME;

    private static final String WALL_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + WALL_TABLENAME;

    private static final String FRIENDS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + FRIENDS_TABLENAME;

    private static final String FRIENDS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + FRIENDS_TABLENAME;

    private static final String DIALOGS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + DIALOGS_TABLENAME;

    private static final String DIALOGS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + DIALOGS_TABLENAME;

    private static final String USERS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + USERS_TABLENAME;

    private static final String USERS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + USERS_TABLENAME;

    private static final String MESSAGES_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + MESSAGES_TABLENAME;

    private static final String MESSAGE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + MESSAGES_TABLENAME;

    public static final Uri NEWS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + NEWS_TABLENAME);

    public static final Uri FRIENDS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + FRIENDS_TABLENAME);

    public static final Uri WALL_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + WALL_TABLENAME);

    public static final Uri DIALOGS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + DIALOGS_TABLENAME);

    public static final Uri USERS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + USERS_TABLENAME);

    public static final Uri MESSAGES_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + MESSAGES_TABLENAME);

    private DBHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final UriMatcher uriMatcher;

    private static void addUri(String tableName, int itemUri, int itemIdUri) {
        uriMatcher.addURI(AUTHORITY, tableName, itemUri);
        uriMatcher.addURI(AUTHORITY, tableName + "/#", itemIdUri);
    }

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        addUri(NEWS_TABLENAME, URI_NEWS, URI_NEWS_ID);
        addUri(FRIENDS_TABLENAME, URI_FRIENDS, URI_FRIENDS_ID);
        addUri(WALL_TABLENAME, URI_WALL, URI_WALL_ID);
        addUri(DIALOGS_TABLENAME, URI_DIALOGS, URI_DIALOGS_ID);
        addUri(USERS_TABLENAME, URI_USERS, URI_USERS_ID);
        addUri(MESSAGES_TABLENAME, URI_MESSAGES, URI_MESSAGES_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName;
        switch (uriMatcher.match(uri)) {
            case URI_NEWS:
                tableName = NEWS_TABLENAME;
                break;
            case URI_FRIENDS: {
                tableName = FRIENDS_TABLENAME;
                break;
            }
            case URI_WALL: {
                tableName = WALL_TABLENAME;
                break;
            }
            case URI_USERS: {
                tableName = USERS_TABLENAME;
                break;
            }
            case URI_WALL_ID: {
                tableName = WALL_TABLENAME;
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WALL_COLUMN_ITEM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WALL_COLUMN_ITEM_ID + " = " + id;
                }
                break;
            }

            case URI_NEWS_ID: {
                tableName = NEWS_TABLENAME;
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = NEWS_COULMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + NEWS_COULMN_ID + " = " + id;
                }
                break;
            }
            case URI_FRIENDS_ID: {
                tableName = FRIENDS_TABLENAME;
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = FRIEND_COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + FRIEND_COLUMN_ID + " = " + id;
                }
                break;
            }
            case URI_DIALOGS: {
                tableName = DIALOGS_TABLENAME;
                break;
            }
            case URI_DIALOGS_ID: {
                tableName = DIALOGS_TABLENAME;
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = DIALOGS_COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + DIALOGS_COLUMN_ID + " = " + id;
                }
                break;
            }
            case URI_MESSAGES: {
                tableName = MESSAGES_TABLENAME;
                if (TextUtils.isEmpty(selection)) {
                    //TODO uncomment it
                   // selection = " ifnull("+MESSAGES_PENDING+",0)" + " <> 1";
                } else {
                   // selection = selection + " AND " + " ifnull("+MESSAGES_PENDING+",0)" + " <> 1 ";
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        mDb = mDbHelper.getWritableDatabase();
        int cnt = mDb.delete(tableName, selection, selectionArgs);
        //   getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_WALL_ID: {
                return WALL_CONTENT_ITEM_TYPE;
            }
            case URI_WALL: {
                return WALL_CONTENT_TYPE;
            }
            case URI_NEWS: {
                return NEWS_CONTENT_TYPE;
            }
            case URI_NEWS_ID: {
                return NEWS_CONTENT_ITEM_TYPE;
            }
            case URI_FRIENDS: {
                return FRIENDS_CONTENT_TYPE;
            }
            case URI_FRIENDS_ID: {
                return FRIENDS_CONTENT_ITEM_TYPE;
            }
            case URI_DIALOGS: {
                return DIALOGS_CONTENT_TYPE;
            }
            case URI_DIALOGS_ID: {
                return DIALOGS_CONTENT_ITEM_TYPE;
            }
            case URI_USERS: {
                return USERS_CONTENT_TYPE;
            }
            case URI_USERS_ID: {
                return USERS_CONTENT_ITEM_TYPE;
            }
            case URI_MESSAGES: {
                return MESSAGES_CONTENT_TYPE;
            }
            case URI_MESSAGES_ID: {
                return MESSAGE_CONTENT_ITEM_TYPE;
            }
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mDb = mDbHelper.getWritableDatabase();
        Uri resultUri;
        switch (uriMatcher.match(uri)) {
            case URI_NEWS: {
                long rowID = mDb.insert(NEWS_TABLENAME, null, values);
                resultUri = ContentUris.withAppendedId(NEWS_CONTENT_URI, rowID);
                break;
            }
            case URI_FRIENDS: {
                long rowID = mDb.insert(FRIENDS_TABLENAME, null, values);
                resultUri = ContentUris.withAppendedId(FRIENDS_CONTENT_URI, rowID);
                break;
            }
            case URI_WALL: {
                long rowID = mDb.insert(WALL_TABLENAME, null, values);
                resultUri = ContentUris.withAppendedId(WALL_CONTENT_URI, rowID);
                break;
            }
            case URI_DIALOGS: {
                long rowID = mDb.insert(DIALOGS_TABLENAME, null, values);
                resultUri = ContentUris.withAppendedId(DIALOGS_CONTENT_URI, rowID);
                break;
            }
            case URI_USERS: {
                long rowID = mDb.insert(USERS_TABLENAME, null, values);
                resultUri = ContentUris.withAppendedId(USERS_CONTENT_URI, rowID);
                break;
            }
            case URI_MESSAGES: {
                long rowID = mDb.insert(MESSAGES_TABLENAME, null, values);
                resultUri = ContentUris.withAppendedId(MESSAGES_CONTENT_URI, rowID);
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String tableName = null;
        Uri contentUri = null;

        switch (uriMatcher.match(uri)) {
            case URI_NEWS: {
                tableName = NEWS_TABLENAME;
                contentUri = NEWS_CONTENT_URI;
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = NEWS_COLUMN_RAW_DATE + " DESC";
                break;
            }
            case URI_FRIENDS: {
                tableName = FRIENDS_TABLENAME;
                contentUri = FRIENDS_CONTENT_URI;
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = FRIEND_COLUMN_LAST_NAME + " ASC";
                break;
            }
            case URI_NEWS_ID: {
                tableName = NEWS_TABLENAME;
                contentUri = NEWS_CONTENT_URI;
                String id = uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)) {
                    selection = selection + " AND " + NEWS_COULMN_ID + " = " + id;
                } else {
                    selection = NEWS_COULMN_ID + " = " + id;
                }
                break;
            }
            case URI_FRIENDS_ID: {
                tableName = FRIENDS_TABLENAME;
                contentUri = FRIENDS_CONTENT_URI;
                String id = uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)) {
                    selection = selection + " AND " + FRIEND_COLUMN_ID + " = " + id;
                } else {
                    selection = NEWS_COULMN_ID + " = " + id;
                }
                break;
            }
            case URI_WALL: {
                tableName = WALL_TABLENAME;
                contentUri = WALL_CONTENT_URI;
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = WALL_COLUMN_RAW_DATE + " DESC";
                break;
            }
            case URI_DIALOGS: {
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                StringBuilder sb = new StringBuilder();
                sb.append(DIALOGS_TABLENAME);
                sb.append(" LEFT OUTER JOIN ");
                sb.append(USERS_TABLENAME);
                sb.append(" ON (");
                sb.append(DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_USER_ID);
                sb.append(" = ");
                sb.append(USERS_TABLENAME + "." + USERS_COLUMN_ID);
                sb.append(")");
                tableName = sb.toString();

                queryBuilder.setTables(tableName);
                HashMap<String, String> mColumnMap = new HashMap<String, String>();

                mColumnMap.put(USERS_TABLENAME + "." + USERS_COLUMN_ID, USERS_TABLENAME + "." + USERS_COLUMN_ID + " as " + USERS_COLUMN_ID);
                mColumnMap.put(USERS_TABLENAME + "." + USERS_COLUMN_NAME, USERS_TABLENAME + "." + USERS_COLUMN_NAME + " as " + USERS_COLUMN_NAME);
                mColumnMap.put(USERS_TABLENAME + "." + USERS_COLUMN_IMAGE, USERS_TABLENAME + "." + USERS_COLUMN_IMAGE + " as " + USERS_COLUMN_IMAGE);
                mColumnMap.put(DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_TITLE, DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_TITLE + " as " + DIALOGS_COLUMN_TITLE);
                mColumnMap.put(DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_BODY, DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_BODY + " as " + DIALOGS_COLUMN_BODY);
                mColumnMap.put(DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_DATE, DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_DATE + " as " + DIALOGS_COLUMN_DATE);
                mColumnMap.put(DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_MESSAGE_ID, DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_MESSAGE_ID + " as " + DIALOGS_COLUMN_MESSAGE_ID);
                mColumnMap.put(DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_RAW_DATE, DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_RAW_DATE + " as " + DIALOGS_COLUMN_RAW_DATE);
                mColumnMap.put(DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_ID, DIALOGS_TABLENAME + "." + DIALOGS_COLUMN_ID + " as " + DIALOGS_COLUMN_ID);
                queryBuilder.setProjectionMap(mColumnMap);
                contentUri = DIALOGS_CONTENT_URI;
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DIALOGS_COLUMN_RAW_DATE + " ASC";
                }
                String[] projections = new String[mColumnMap.size()];
                mColumnMap.keySet().toArray(projections);

                Cursor cr = queryBuilder.query(mDb, projections, selection, selectionArgs, null, null,
                        sortOrder);
                cr.setNotificationUri(getContext().getContentResolver(), contentUri);
                return cr;
            }
            case URI_USERS: {
                tableName = USERS_TABLENAME;
                contentUri = USERS_CONTENT_URI;
                break;
            }
            case URI_MESSAGES: {
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                StringBuilder sb = new StringBuilder();
                sb.append(MESSAGES_TABLENAME);
                sb.append(" LEFT OUTER JOIN ");
                sb.append(USERS_TABLENAME);
                sb.append(" ON (");
                sb.append(MESSAGES_TABLENAME + "." + MESSAGES_USER_FROM_ID);
                sb.append(" = ");
                sb.append(USERS_TABLENAME + "." + USERS_COLUMN_ID);
                sb.append(")");
                tableName = sb.toString();

                queryBuilder.setTables(tableName);
                HashMap<String, String> mColumnMap = new HashMap<String, String>();
                //"CREATE TABLE Messages(_id Integer NOT NULL PRIMARY KEY AUTOINCREMENT, message_id text, body text,
                // Raw_Date text, Date text, user_id text, image_url text)";
                mColumnMap.put(MESSAGES_TABLENAME + "." + MESSAGES_USER_FROM_ID, MESSAGES_TABLENAME + "." + MESSAGES_USER_FROM_ID+ " as " + MESSAGES_USER_FROM_ID);
                mColumnMap.put(USERS_TABLENAME + "." + USERS_COLUMN_NAME, USERS_TABLENAME + "." + USERS_COLUMN_NAME + " as " + USERS_COLUMN_NAME);
                mColumnMap.put(USERS_TABLENAME + "." + USERS_COLUMN_IMAGE, USERS_TABLENAME + "." + USERS_COLUMN_IMAGE + " as " + USERS_COLUMN_IMAGE_FULL);
                mColumnMap.put(MESSAGES_TABLENAME + "." + MESSAGES_ID, MESSAGES_TABLENAME + "." + MESSAGES_ID + " as " + MESSAGES_ID);
                mColumnMap.put(MESSAGES_TABLENAME + "." + MESSAGES_MESSAGE_ID, MESSAGES_TABLENAME + "." + MESSAGES_MESSAGE_ID + " as " + MESSAGES_MESSAGE_ID);
                mColumnMap.put(MESSAGES_TABLENAME + "." + MESSAGES_COLUMN_BODY, MESSAGES_TABLENAME + "." + MESSAGES_COLUMN_BODY + " as " + MESSAGES_COLUMN_BODY);
                mColumnMap.put(MESSAGES_TABLENAME + "." + MESSAGES_DATE, MESSAGES_TABLENAME + "." + MESSAGES_DATE + " as " + MESSAGES_DATE);
                mColumnMap.put(MESSAGES_TABLENAME + "." + MESSAGES_RAW_DATE, MESSAGES_TABLENAME + "." + MESSAGES_RAW_DATE + " as " + MESSAGES_RAW_DATE);
                mColumnMap.put(MESSAGES_TABLENAME + "." + MESSAGES_IMAGE_URL, MESSAGES_TABLENAME + "." + MESSAGES_IMAGE_URL + " as " + MESSAGES_IMAGE_URL);
                mColumnMap.put(MESSAGES_TABLENAME + "." + MESSAGES_OUT, MESSAGES_TABLENAME + "." + MESSAGES_OUT + " as " + MESSAGES_OUT);
                mColumnMap.put(MESSAGES_TABLENAME + "." + MESSAGES_PENDING, MESSAGES_TABLENAME + "." + MESSAGES_PENDING + " as " + MESSAGES_PENDING);
                mColumnMap.put(MESSAGES_TABLENAME + "." + MESSAGES_USER_ID, MESSAGES_TABLENAME + "." + MESSAGES_USER_ID + " as " + MESSAGES_USER_ID);

                queryBuilder.setProjectionMap(mColumnMap);
                contentUri = MESSAGES_CONTENT_URI;
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = MESSAGES_RAW_DATE + " ASC";
                }
                String[] projections = new String[mColumnMap.size()];
                mColumnMap.keySet().toArray(projections);
                Cursor cr = queryBuilder.query(mDb, projections, selection, selectionArgs, null, null,
                        sortOrder);
                cr.setNotificationUri(getContext().getContentResolver(), contentUri);
                return cr;
            }

        }
        mDb = mDbHelper.getWritableDatabase();
        Cursor cr = mDb.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        cr.setNotificationUri(getContext().getContentResolver(), contentUri);
        return cr;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        int uriType = 0;
        int insertCount = 0;
        try {
            uriType = uriMatcher.match(uri);
            SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
            String tableName = null;

            switch (uriType) {
                case URI_NEWS:
                    tableName = NEWS_TABLENAME;
                    break;
                case URI_FRIENDS: {
                    tableName = FRIENDS_TABLENAME;
                    break;
                }
                case URI_WALL: {
                    tableName = WALL_TABLENAME;
                    break;
                }
                case URI_DIALOGS: {
                    tableName = DIALOGS_TABLENAME;
                    break;
                }
                case URI_USERS: {
                    tableName = USERS_TABLENAME;
                    try {
                        sqlDB.beginTransaction();
                        for (ContentValues value : values) {
                            long id = sqlDB.insertWithOnConflict(tableName, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                            if (id > 0)
                                insertCount++;
                        }
                        sqlDB.setTransactionSuccessful();
                    } catch (Exception e) {
                    } finally {
                        sqlDB.endTransaction();
                    }
                    return insertCount;

                }
                case URI_MESSAGES: {
                    tableName = MESSAGES_TABLENAME;
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            try {
                sqlDB.beginTransaction();
                for (ContentValues value : values) {
                    long id = sqlDB.insert(tableName, null, value);
                    if (id > 0)
                        insertCount++;
                }
                sqlDB.setTransactionSuccessful();
            } catch (Exception e) {
            } finally {
                sqlDB.endTransaction();
            }
        } catch (Exception e) {

        }
        return insertCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case URI_MESSAGES:
                rowsUpdated = sqlDB.update(MESSAGES_TABLENAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case URI_MESSAGES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(MESSAGES_TABLENAME,
                            values,
                            MESSAGES_MESSAGE_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(MESSAGES_TABLENAME,
                            values,
                            MESSAGES_MESSAGE_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                break;}
        };
        return rowsUpdated;
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_NEWS);
            db.execSQL(DB_CREATE_FRIENDS);
            db.execSQL(DB_CREATE_WALL);
            db.execSQL(DB_CREATE_DIALOGS);
            db.execSQL(DB_CREATE_USERS);
            db.execSQL(DB_CREATE_MESSAGES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DB_DROP_FRIENDS);
            db.execSQL(DB_DROP_NEWS);
            db.execSQL(DB_DROP_WALL);
            db.execSQL(DB_DROP_DIALOGS);
            db.execSQL(DB_DROP_USERS);
            db.execSQL(DB_DROP_MESSAGES);
            onCreate(db);
        }
    }
}
