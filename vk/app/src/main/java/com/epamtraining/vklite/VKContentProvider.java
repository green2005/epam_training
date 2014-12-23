package  com.epamtraining.vklite;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

public class VKContentProvider extends ContentProvider {
    private static final String DB_NAME = "VKLite";
    private static final int DB_VERSION = 1;
    private static final String DB_DROP_NEWS = "DROP TABLE IF EXISTS News";
    private static final String DB_DROP_FRIENDS = "DROP TABLE IF EXISTS Friends";
    private static final String DB_DROP_WALL = "DROP TABLE IF EXISTS Wall";

    private static final String DB_CREATE_NEWS = "CREATE TABLE News (_id Integer NOT NULL PRIMARY KEY AUTOINCREMENT,NewsText text,Url text,Date text,Image_Url text, Post_id text, Raw_Date text, Next_From text, username text, userimage text) ";
    private static final String DB_CREATE_FRIENDS =   "CREATE TABLE Friends(_id Integer NOT NULL PRIMARY KEY AUTOINCREMENT, First_Name text, Last_Name text, Image_Url text, id text, Nick_Name text)";
    private static final String DB_CREATE_WALL = "CREATE TABLE Wall(_id Integer not null PRIMARY KEY AUTOINCREMENT, id text, from_id text,"+
            " owner_id text, rawDate text, date text, itemText text, post_type text, Image_Url text, level int, Url text, username text, userimage text)";

    public static final String NEWS_COLUMN_DATE = "Date";
    public static final String NEWS_COULMN_ID = "_id";
    public static final String NEWS_COLUMN_TEXT = "NewsText";
    public static final String NEWS_COLUMN_URL = "Url";
    public static final String NEWS_COLUMN_IMAGE_URL = "Image_Url";
    public static final String NEWS_COLUMN_POST_ID = "Post_id";
    public static final String NEWS_COLUMN_RAW_DATE = "Raw_Date";
    public static final String NEWS_COLUMN_NEXT_FROM = "Next_From";
    public static final String NEWS_COLUMN_USERNAME ="username";
    public static final String NEWS_COLUMN_USERIMAGE = "userimage";

    public static final String FRIEND_COLUMN_ID = "id";
    public static final String FRIEND_COLUMN_FIRST_NAME = "First_Name";
    public static final String FRIEND_COLUMN_LAST_NAME = "Last_Name";
    public static final String FRIEND_COLUMN_NICK_NAME = "Nick_Name";
    public static final String FRIEND_COLUMN_IMAGE_URL = "Image_Url";

    public static final String WALL_COLUMN_ID = "_id";
    public static final String WALL_COLUMN_ITEM_ID = "id";
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

    private static final String AUTHORITY = "com.epamtraining.vk";
    private static final String NEWS_TABLENAME = "News";
    private static final String FRIENDS_TABLENAME = "Friends";
    private static  final String WALL_TABLENAME = "Wall";

    private static final int URI_NEWS = 1;
    private static final int URI_NEWS_ID = 2;
    private static  final int URI_FRIENDS = 3;
    private static final int URI_FRIENDS_ID = 4;
    private static  final int URI_WALL = 5;
    private static final int URI_WALL_ID = 6;

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

    public static final Uri NEWS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + NEWS_TABLENAME);

    public static final Uri FRIENDS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + FRIENDS_TABLENAME);

    public static final Uri WALL_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + WALL_TABLENAME);

    private DBHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final UriMatcher uriMatcher;

    private static void addUri(String tableName, int itemUri, int itemIdUri){
        uriMatcher.addURI(AUTHORITY, tableName, itemUri);
        uriMatcher.addURI(AUTHORITY, tableName + "/#", itemIdUri);
    }

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        addUri(NEWS_TABLENAME, URI_NEWS, URI_NEWS_ID);
        addUri(FRIENDS_TABLENAME, URI_FRIENDS, URI_FRIENDS_ID);
        addUri(WALL_TABLENAME, URI_WALL, URI_WALL_ID);
    }

    public VKContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName;
        switch (uriMatcher.match(uri)) {
            case URI_NEWS:
                tableName = NEWS_TABLENAME;
                break;
            case URI_FRIENDS:{
                tableName = FRIENDS_TABLENAME;
                break;
            }
            case URI_WALL:{
                tableName = WALL_TABLENAME;
                break;
            }
            case URI_WALL_ID:{
                tableName = WALL_TABLENAME;
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WALL_COLUMN_ITEM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WALL_COLUMN_ITEM_ID + " = " + id;
                }
                break;
            }

            case URI_NEWS_ID:{
                tableName = NEWS_TABLENAME;
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = NEWS_COULMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + NEWS_COULMN_ID + " = " + id;
                }
                break;}
            case URI_FRIENDS_ID:{
                tableName = FRIENDS_TABLENAME;
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = FRIEND_COLUMN_ID + " = " + id;
                } else {
                    selection = selection + " AND " + FRIEND_COLUMN_ID + " = " + id;
                }
                break;
            }

            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        mDb = mDbHelper.getWritableDatabase();
        int cnt = mDb.delete(tableName, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_WALL_ID:{
                return WALL_CONTENT_ITEM_TYPE;
            }
            case URI_WALL:{
                return WALL_CONTENT_TYPE;
            }
            case URI_NEWS: {
                return NEWS_CONTENT_TYPE;
            }
            case URI_NEWS_ID: {
                return NEWS_CONTENT_ITEM_TYPE;
            }
            case URI_FRIENDS:{
                return FRIENDS_CONTENT_TYPE;
            }
            case URI_FRIENDS_ID:{
                return FRIENDS_CONTENT_ITEM_TYPE;
            }
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mDb = mDbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case URI_NEWS:{
                long rowID = mDb.insert(NEWS_TABLENAME, null, values);
                Uri resultUri = ContentUris.withAppendedId(NEWS_CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            }
            case URI_FRIENDS:{
                long rowID = mDb.insert(FRIENDS_TABLENAME, null, values);
                Uri resultUri = ContentUris.withAppendedId(FRIENDS_CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            }
            case URI_WALL:{
                long rowID = mDb.insert(WALL_TABLENAME, null, values);
                Uri resultUri = ContentUris.withAppendedId(WALL_CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            }

            default:{
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
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
                tableName =  NEWS_TABLENAME;
                contentUri = NEWS_CONTENT_URI;
               if (TextUtils.isEmpty(sortOrder))
                    sortOrder = NEWS_COLUMN_RAW_DATE +" DESC";
                break;
            }
            case URI_FRIENDS:{
                tableName =  FRIENDS_TABLENAME;
                contentUri = FRIENDS_CONTENT_URI;
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = FRIEND_COLUMN_LAST_NAME +" ASC";
                break;
            }
            case URI_NEWS_ID: {
                tableName =  NEWS_TABLENAME;
                contentUri = NEWS_CONTENT_URI;
                String id = uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)){
                    selection = selection +" AND "+ NEWS_COULMN_ID +" = "+ id;
                } else{
                    selection =  NEWS_COULMN_ID +" = "+ id;
                }
                break;
            }
            case URI_FRIENDS_ID:{
                tableName =  FRIENDS_TABLENAME;
                contentUri = FRIENDS_CONTENT_URI;
                String id = uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)){
                    selection = selection +" AND "+ FRIEND_COLUMN_ID +" = "+ id;
                } else{
                    selection =  NEWS_COULMN_ID +" = "+ id;
                }
                break;
            }
            case URI_WALL:{
                tableName =  WALL_TABLENAME;
                contentUri = WALL_CONTENT_URI;
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = WALL_COLUMN_RAW_DATE +" DESC";
                break;
            }
        }
        mDb = mDbHelper.getWritableDatabase();
        Cursor cr =  mDb.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        cr.setNotificationUri(getContext().getContentResolver(), contentUri);
        return cr;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){

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
                case URI_FRIENDS:{
                    tableName = FRIENDS_TABLENAME;
                    break;
                }
                case URI_WALL:{
                    tableName = WALL_TABLENAME;
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
            // getContext.getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {

        }
        return insertCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        //now its nothing to update ...
        throw new UnsupportedOperationException("Not yet implemented");
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
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DB_DROP_FRIENDS);
            db.execSQL(DB_DROP_NEWS);
            db.execSQL(DB_DROP_WALL);
            onCreate(db);
        }
    }
}
