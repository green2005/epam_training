package com.epamtraining.vklite.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class VKContentProvider extends ContentProvider {

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
    private static final int URI_ATTACHMENTS = 13;
    private static final int URI_ATTACHMENTS_ID = 14;
    private static final int URI_COMMENTS = 15;
    private static final int URI_COMMENTS_ID = 16;
    private static HashMap<Integer, BODBHelper> sBoDbHelpers;


    private DBManager mDbManager;
    private static final UriMatcher uriMatcher;

    private static void addUri(BODBHelper dbHelper, int itemUri, int itemIdUri) {
        sBoDbHelpers.put(itemIdUri, dbHelper);
        sBoDbHelpers.put(itemUri, dbHelper);
        uriMatcher.addURI(BODBHelper.AUTHORITY, dbHelper.getTableName(), itemUri);
        uriMatcher.addURI(BODBHelper.AUTHORITY, dbHelper.getTableName() + "/#", itemIdUri);
    }

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sBoDbHelpers = new HashMap();
        addUri(new DialogDBHelper(), URI_DIALOGS, URI_DIALOGS_ID);
        addUri(new NewsDBHelper(), URI_NEWS, URI_NEWS_ID);
        addUri(new FriendDBHelper(), URI_FRIENDS, URI_FRIENDS_ID);
        addUri(new WallDBHelper(), URI_WALL, URI_WALL_ID);
        addUri(new MessagesDBHelper(), URI_MESSAGES, URI_MESSAGES_ID);
        addUri(new UsersDBHelper(), URI_USERS, URI_USERS_ID);
        addUri(new AttachmentsDBHelper(), URI_ATTACHMENTS, URI_ATTACHMENTS_ID);
        addUri(new CommentsDBHelper(), URI_COMMENTS, URI_COMMENTS_ID);
    }

    private BODBHelper getDBHelper(int helperUri) {
        BODBHelper helper = sBoDbHelpers.get(helperUri);
        if (helper == null) {
            throw new IllegalArgumentException("Unknown Uri");
        }
        return sBoDbHelpers.get(helperUri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName;
        int helperId = uriMatcher.match(uri);
        BODBHelper helper = getDBHelper(helperId);
        if (helper == null) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        tableName = helper.getTableName();
        switch (helperId) {
            case URI_WALL_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WallDBHelper.POST_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WallDBHelper.POST_ID + " = " + id;
                }
                break;
            }

            case URI_NEWS_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = NewsDBHelper.POST_ID + " = " + id;
                } else {
                    selection = selection + " AND " + NewsDBHelper.POST_ID + " = " + id;
                }
                break;
            }
            case URI_FRIENDS_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = FriendDBHelper.ID + " = " + id;
                } else {
                    selection = selection + " AND " + FriendDBHelper.ID + " = " + id;
                }
                break;
            }
            case URI_DIALOGS_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = FriendDBHelper.ID + " = " + id;
                } else {
                    selection = selection + " AND " + FriendDBHelper.ID + " = " + id;
                }
                break;
            }
            case URI_MESSAGES: {
                if (TextUtils.isEmpty(selection)) {
                        selection = " ifnull(" + MessagesDBHelper.PENDING + ",0)" + " <> 1";
                } else {
                       selection = selection + " AND " + " ifnull(" + MessagesDBHelper.PENDING + ",0)" + " <> 1 ";
                }
                break;
            }
        }
        SQLiteDatabase mDb = mDbManager.getWritableDatabase();
        int cnt = mDb.delete(tableName, selection, selectionArgs);
        return cnt;
    }


    @Override
    public String getType(Uri uri) {
        int helperId = uriMatcher.match(uri);
        BODBHelper helper = getDBHelper(helperId);
        if (helper == null) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        switch (helperId) {
            case URI_WALL_ID: {
                return helper.getContentItemType();
            }
            case URI_WALL: {
                return helper.getContentType();
            }
            case URI_NEWS: {
                return helper.getContentType();
            }
            case URI_NEWS_ID: {
                return helper.getContentItemType();
            }
            case URI_FRIENDS: {
                return helper.getContentType();
            }
            case URI_FRIENDS_ID: {
                return helper.getContentItemType();
            }
            case URI_DIALOGS: {
                return helper.getContentType();
            }
            case URI_DIALOGS_ID: {
                return helper.getContentItemType();
            }
            case URI_USERS: {
                return helper.getContentType();
            }
            case URI_USERS_ID: {
                return helper.getContentItemType();
            }
            case URI_MESSAGES: {
                return helper.getContentType();
            }
            case URI_MESSAGES_ID: {
                return helper.getContentItemType();
            }
            case URI_ATTACHMENTS: {
                return helper.getContentType();
            }
            case URI_ATTACHMENTS_ID: {
                return helper.getContentItemType();
            }
            case URI_COMMENTS :{
                return helper.getContentType();
            }
            case URI_COMMENTS_ID:{
                return helper.getContentItemType();
            }
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbManager.getWritableDatabase();
        Uri resultUri;
        int helperUriId = uriMatcher.match(uri);
        BODBHelper helper = getDBHelper(helperUriId);
        if (helper == null) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        long rowID = db.insert(helper.getTableName(), null, values);
        resultUri = ContentUris.withAppendedId(helper.getContentUri(), rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public boolean onCreate() {
        HashSet<BODBHelper> helpers = new HashSet<>(sBoDbHelpers.values());
        mDbManager = new DBManager(getContext(), helpers);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String tableName = null;
        Uri contentUri = null;
        int uriHelperId = uriMatcher.match(uri);
         BODBHelper helper = getDBHelper(uriHelperId);
        if (helper == null) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        tableName = helper.getTableName();
        contentUri = helper.getContentUri();
        SQLiteDatabase db = mDbManager.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case URI_NEWS: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = NewsDBHelper.RAW_DATE + " DESC";
                break;
            }
            case URI_COMMENTS:{
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = CommentsDBHelper.RAW_DATE + " DESC";
                break;
            }
            case URI_FRIENDS: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = FriendDBHelper.LAST_NAME + " ASC";
                break;
            }
            case URI_NEWS_ID: {
                String id = uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)) {
                    selection = selection + " AND " + NewsDBHelper.POST_ID + " = " + id;
                } else {
                    selection = NewsDBHelper.POST_ID + " = " + id;
                }
                break;
            }
            case URI_FRIENDS_ID: {
                String id = uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)) {
                    selection = selection + " AND " + FriendDBHelper.ID + " = " + id;
                } else {
                    selection = FriendDBHelper.ID + " = " + id;
                }
                break;
            }
            case URI_WALL: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = WallDBHelper.RAW_DATE + " DESC";
                break;
            }
            case URI_DIALOGS: {
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                StringBuilder sb = new StringBuilder();
                sb.append(DialogDBHelper.TABLENAME);
                sb.append(" LEFT OUTER JOIN ");
                sb.append(UsersDBHelper.TABLENAME);
                sb.append(" ON (");
                sb.append(DialogDBHelper.TABLENAME + "." + DialogDBHelper.USER_ID);
                sb.append(" = ");
                sb.append(UsersDBHelper.TABLENAME + "." + UsersDBHelper.ID);
                sb.append(")");
                tableName = sb.toString();

                queryBuilder.setTables(tableName);
                HashMap<String, String> mColumnMap = new HashMap<>();

                mColumnMap.put(UsersDBHelper.TABLENAME + "." + UsersDBHelper.ID, UsersDBHelper.TABLENAME + "." + UsersDBHelper.ID + " as " + UsersDBHelper.ID);
                mColumnMap.put(UsersDBHelper.TABLENAME + "." + UsersDBHelper.NAME, UsersDBHelper.TABLENAME + "." + UsersDBHelper.NAME + " as " + UsersDBHelper.NAME);
                mColumnMap.put(UsersDBHelper.TABLENAME + "." + UsersDBHelper.IMAGE, UsersDBHelper.TABLENAME + "." + UsersDBHelper.IMAGE + " as " + UsersDBHelper.IMAGE);
                mColumnMap.put(DialogDBHelper.TABLENAME + "." + DialogDBHelper.TITLE, DialogDBHelper.TABLENAME + "." + DialogDBHelper.TITLE + " as " + DialogDBHelper.TITLE);
                mColumnMap.put(DialogDBHelper.TABLENAME + "." + DialogDBHelper.BODY, DialogDBHelper.TABLENAME + "." + DialogDBHelper.BODY + " as " + DialogDBHelper.BODY);
                mColumnMap.put(DialogDBHelper.TABLENAME + "." + DialogDBHelper.DATE, DialogDBHelper.TABLENAME + "." + DialogDBHelper.DATE + " as " + DialogDBHelper.DATE);
                mColumnMap.put(DialogDBHelper.TABLENAME + "." + DialogDBHelper.ID, DialogDBHelper.TABLENAME + "." + DialogDBHelper.ID + " as " + DialogDBHelper.ID);
                mColumnMap.put(DialogDBHelper.TABLENAME + "." + DialogDBHelper.RAW_DATE, DialogDBHelper.TABLENAME + "." + DialogDBHelper.RAW_DATE + " as " + DialogDBHelper.RAW_DATE);
                mColumnMap.put(DialogDBHelper.TABLENAME + "." + BaseColumns._ID, DialogDBHelper.TABLENAME + "." + BaseColumns._ID + " as " + BaseColumns._ID);
                queryBuilder.setProjectionMap(mColumnMap);
                contentUri = helper.getContentUri();
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DialogDBHelper.RAW_DATE + " DESC";
                }
                String[] projections = new String[mColumnMap.size()];
                mColumnMap.keySet().toArray(projections);

                Cursor cr = queryBuilder.query(db, projections, selection, selectionArgs, null, null,
                        sortOrder);
                cr.setNotificationUri(getContext().getContentResolver(), contentUri);
                return cr;
            }

            case URI_MESSAGES: {
                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                StringBuilder sb = new StringBuilder();
                sb.append(MessagesDBHelper.TABLENAME);
                sb.append(" LEFT OUTER JOIN ");
                sb.append(UsersDBHelper.TABLENAME);
                sb.append(" ON (");
                sb.append(MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.FROM_ID);
                sb.append(" = ");
                sb.append(UsersDBHelper.TABLENAME + "." + UsersDBHelper.ID);
                sb.append(")");
                tableName = sb.toString();

                queryBuilder.setTables(tableName);
                HashMap<String, String> mColumnMap = new HashMap<String, String>();
                //"CREATE TABLE Messages(_id Integer NOT NULL PRIMARY KEY AUTOINCREMENT, message_id text, body text,
                // Raw_Date text, Date text, user_id text, image_url text)";
                mColumnMap.put(MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.FROM_ID, MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.FROM_ID + " as " + MessagesDBHelper.FROM_ID);
                mColumnMap.put(UsersDBHelper.TABLENAME + "." + UsersDBHelper.NAME, UsersDBHelper.TABLENAME + "." + UsersDBHelper.NAME + " as " + UsersDBHelper.NAME);
                mColumnMap.put(UsersDBHelper.TABLENAME + "." + UsersDBHelper.IMAGE, UsersDBHelper.TABLENAME + "." + UsersDBHelper.IMAGE + " as " + UsersDBHelper.IMAGE_FULL);
                mColumnMap.put(MessagesDBHelper.TABLENAME + "." + BaseColumns._ID, MessagesDBHelper.TABLENAME + "." + BaseColumns._ID + " as " + BaseColumns._ID);
                mColumnMap.put(MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.ID, MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.ID + " as " + MessagesDBHelper.ID);
                mColumnMap.put(MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.BODY, MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.BODY + " as " + MessagesDBHelper.BODY);
                mColumnMap.put(MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.DATE, MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.DATE + " as " + MessagesDBHelper.DATE);
                mColumnMap.put(MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.RAW_DATE, MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.RAW_DATE + " as " + MessagesDBHelper.RAW_DATE);
                mColumnMap.put(MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.IMAGE_URL, MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.IMAGE_URL + " as " + MessagesDBHelper.IMAGE_URL);
                mColumnMap.put(MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.OUT, MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.OUT + " as " + MessagesDBHelper.OUT);
                mColumnMap.put(MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.PENDING, MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.PENDING + " as " + MessagesDBHelper.PENDING);
                mColumnMap.put(MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.USER_ID, MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.USER_ID + " as " + MessagesDBHelper.USER_ID);

                queryBuilder.setProjectionMap(mColumnMap);
                contentUri = helper.getContentUri();
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = MessagesDBHelper.RAW_DATE + " DESC";
                }
                String[] projections = new String[mColumnMap.size()];
                mColumnMap.keySet().toArray(projections);
                Cursor cr = queryBuilder.query(db, projections, selection, selectionArgs, null, null,
                        sortOrder);
                cr.setNotificationUri(getContext().getContentResolver(), contentUri);
                return cr;
            }

        }
        Cursor cr = db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        cr.setNotificationUri(getContext().getContentResolver(), contentUri);
        return cr;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        int uriType = 0;
        int insertCount = 0;
        uriType = uriMatcher.match(uri);
        BODBHelper helper = getDBHelper(uriType);
        if (helper == null) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        SQLiteDatabase sqlDB = mDbManager.getWritableDatabase();
        String tableName = helper.getTableName();
        switch (uriType) {
            case URI_USERS: {
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
                break;
            }
            default: {
                sqlDB.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long id = sqlDB.insert(tableName, null, value);
                        if (id > 0)
                            insertCount++;
                    }
                    sqlDB.setTransactionSuccessful();
                } finally {
                    sqlDB.endTransaction();
                }
                break;
            }
        }
        return insertCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = mDbManager.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case URI_MESSAGES:
                rowsUpdated = sqlDB.update(MessagesDBHelper.TABLENAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case URI_MESSAGES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(MessagesDBHelper.TABLENAME,
                            values,
                            MessagesDBHelper.ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(MessagesDBHelper.TABLENAME,
                            values,
                            MessagesDBHelper.ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                    break;
                }
        }
        ;
        return rowsUpdated;
    }

}
