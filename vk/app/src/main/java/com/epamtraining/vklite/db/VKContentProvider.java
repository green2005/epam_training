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
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VKContentProvider extends ContentProvider {

    public static final String CONTENT_TYPE_PREFIX = "vnd.android.cursor.dir/vnd.";
    public static final String CONTENT_ITEM_TYPE_PREFIX = "vnd.android.cursor.item/vnd.";
    public static final String AUTHORITY = "com.epamtraining.vk";
    public static final String CONTENT_URI_PREFIX = "content://";

    private static enum URI_TYPE {
        NEWS(NewsDBHelper.TABLENAME, NewsDBHelper.CONTENT_URI),
        NEWS_ID(NewsDBHelper.TABLENAME, NewsDBHelper.CONTENT_URI_ID),
        FRIENDS(FriendDBHelper.TABLENAME, FriendDBHelper.CONTENT_URI),
        FRIENDS_ID(FriendDBHelper.TABLENAME, FriendDBHelper.CONTENT_URI_ID),
        WALL(WallDBHelper.TABLENAME, WallDBHelper.CONTENT_URI),
        WALL_ID(WallDBHelper.TABLENAME, WallDBHelper.CONTENT_URI_ID),
        DIALOGS(DialogDBHelper.TABLENAME, DialogDBHelper.CONTENT_URI),
        DIALOGS_ID(DialogDBHelper.TABLENAME, DialogDBHelper.CONTENT_URI_ID),
        USERS(UsersDBHelper.TABLENAME, UsersDBHelper.CONTENT_URI),
        USERS_ID(UsersDBHelper.TABLENAME, UsersDBHelper.CONTENT_URI_ID),
        MESSAGES(MessagesDBHelper.TABLENAME, MessagesDBHelper.CONTENT_URI),
        MESSAGES_ID(MessagesDBHelper.TABLENAME, MessagesDBHelper.CONTENT_URI_ID),
        ATTACHMENTS(AttachmentsDBHelper.TABLENAME, AttachmentsDBHelper.CONTENT_URI),
        ATTACHMENTS_ID(AttachmentsDBHelper.TABLENAME, AttachmentsDBHelper.CONTENT_URI_ID),
        COMMENTS(CommentsDBHelper.TABLENAME, CommentsDBHelper.CONTENT_URI),
        COMMENTS_ID(CommentsDBHelper.TABLENAME, CommentsDBHelper.CONTENT_URI_ID),;
        private String mTableName;
        private Uri mContentUri;

        URI_TYPE(String tableName, Uri contentUri) {
            mTableName = tableName;
            mContentUri = contentUri;
        }

        public String getTableName() {
            return mTableName;
        }

        public Uri getContentUri() {
            return mContentUri;
        }
    }

    /*private static final int URI_NEWS = 1;
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
  */
    private static Map<Integer, URI_TYPE> sUriTypes;


    private DBManager sDbManager;
    private static final UriMatcher uriMatcher;

    private static void addUri(URI_TYPE uri_item, URI_TYPE uri_item_id) {
        uriMatcher.addURI(AUTHORITY, uri_item.getTableName(), uri_item.ordinal());
        uriMatcher.addURI(AUTHORITY, uri_item_id.getTableName() + "/#", uri_item_id.ordinal());
        sUriTypes.put(uri_item.ordinal(), uri_item);
        sUriTypes.put(uri_item_id.ordinal(), uri_item_id);
    }

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriTypes = new ConcurrentHashMap<>();
        addUri(URI_TYPE.DIALOGS, URI_TYPE.DIALOGS_ID);
        addUri(URI_TYPE.NEWS, URI_TYPE.NEWS_ID);
        addUri(URI_TYPE.FRIENDS, URI_TYPE.FRIENDS_ID);
        addUri(URI_TYPE.WALL, URI_TYPE.WALL_ID);
        addUri(URI_TYPE.MESSAGES, URI_TYPE.MESSAGES_ID);
        addUri(URI_TYPE.USERS, URI_TYPE.USERS_ID);
        addUri(URI_TYPE.ATTACHMENTS, URI_TYPE.ATTACHMENTS_ID);
        addUri(URI_TYPE.COMMENTS, URI_TYPE.COMMENTS_ID);
    }

    private URI_TYPE getUriTypeByUri(Uri uri) {
        int uriTypeId = uriMatcher.match(uri);
        URI_TYPE uriType = sUriTypes.get(uriTypeId);
        if (uriType == null) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        } else {
            return uriType;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName;
        URI_TYPE uriType = getUriTypeByUri(uri);
        tableName = uriType.getTableName();
        switch (uriType) {
            case WALL_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WallDBHelper.POST_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WallDBHelper.POST_ID + " = " + id;
                }
                break;
            }

            case NEWS_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = NewsDBHelper.POST_ID + " = " + id;
                } else {
                    selection = selection + " AND " + NewsDBHelper.POST_ID + " = " + id;
                }
                break;
            }
            case FRIENDS_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = FriendDBHelper.ID + " = " + id;
                } else {
                    selection = selection + " AND " + FriendDBHelper.ID + " = " + id;
                }
                break;
            }
            case DIALOGS_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = FriendDBHelper.ID + " = " + id;
                } else {
                    selection = selection + " AND " + FriendDBHelper.ID + " = " + id;
                }
                break;
            }
            case COMMENTS:{
                if (TextUtils.isEmpty(selection)) {
                    selection = " ifnull(" + CommentsDBHelper.PENDING + ",0)" + " <> 1";
                } else {
                    selection = selection + " AND " + " ifnull(" + CommentsDBHelper.PENDING + ",0)" + " <> 1 ";
                }
                break;
            }
            case MESSAGES: {
                if (TextUtils.isEmpty(selection)) {
                    selection = " ifnull(" + MessagesDBHelper.PENDING + ",0)" + " <> 1";
                } else {
                    selection = selection + " AND " + " ifnull(" + MessagesDBHelper.PENDING + ",0)" + " <> 1 ";
                }
                break;
            }
        }
        SQLiteDatabase mDb = sDbManager.getWritableDatabase();
        return mDb.delete(tableName, selection, selectionArgs);
    }

    private String getContentType(URI_TYPE uriType, String prefix) {
        return String.format("%s%s.%s", prefix, AUTHORITY, uriType.getTableName());
    }


    @Override
    public String getType(Uri uri) {
        URI_TYPE uriType = getUriTypeByUri(uri);
        switch (uriType) {
            case WALL_ID: {
                return getContentType(uriType, CONTENT_ITEM_TYPE_PREFIX);
            }
            case WALL: {
                return getContentType(uriType, CONTENT_TYPE_PREFIX);
            }
            case NEWS: {
                return getContentType(uriType, CONTENT_TYPE_PREFIX);
            }
            case NEWS_ID: {
                return getContentType(uriType, CONTENT_ITEM_TYPE_PREFIX);
            }
            case FRIENDS: {
                return getContentType(uriType, CONTENT_TYPE_PREFIX);
            }
            case FRIENDS_ID: {
                return getContentType(uriType, CONTENT_ITEM_TYPE_PREFIX);
            }
            case DIALOGS: {
                return getContentType(uriType, CONTENT_TYPE_PREFIX);
            }
            case DIALOGS_ID: {
                return getContentType(uriType, CONTENT_ITEM_TYPE_PREFIX);
            }
            case USERS: {
                return getContentType(uriType, CONTENT_TYPE_PREFIX);
            }
            case USERS_ID: {
                return getContentType(uriType, CONTENT_ITEM_TYPE_PREFIX);
            }
            case MESSAGES: {
                return getContentType(uriType, CONTENT_TYPE_PREFIX);
            }
            case MESSAGES_ID: {
                return getContentType(uriType, CONTENT_ITEM_TYPE_PREFIX);
            }
            case ATTACHMENTS: {
                return getContentType(uriType, CONTENT_TYPE_PREFIX);
            }
            case ATTACHMENTS_ID: {
                return getContentType(uriType, CONTENT_ITEM_TYPE_PREFIX);
            }
            case COMMENTS: {
                return getContentType(uriType, CONTENT_TYPE_PREFIX);
            }
            case COMMENTS_ID: {
                return getContentType(uriType, CONTENT_ITEM_TYPE_PREFIX);
            }
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = sDbManager.getWritableDatabase();
        Uri resultUri;
        long rowID;
        URI_TYPE uriType = getUriTypeByUri(uri);
        db.beginTransaction();
        try {
            rowID = db.insert(uriType.getTableName(), null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        resultUri = ContentUris.withAppendedId(uriType.getContentUri(), rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public boolean onCreate() {
         //moved to singleTon, bug on 9 or 10 api
        if (sDbManager == null) {
            sDbManager = new DBManager(getContext());
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        URI_TYPE uriType = getUriTypeByUri(uri);
        Uri contentUri = uriType.getContentUri();
        String tableName  = uriType.getTableName();
        SQLiteDatabase db = sDbManager.getReadableDatabase();
        switch (uriType) {
            case  NEWS: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = NewsDBHelper.RAW_DATE + " DESC";
                break;
            }
            case  COMMENTS: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = CommentsDBHelper.RAW_DATE + " DESC";
                break;
            }
            case  FRIENDS: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = FriendDBHelper.LAST_NAME + " ASC";
                break;
            }
            case  NEWS_ID: {
                String id = uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)) {
                    selection = selection + " AND " + NewsDBHelper.POST_ID + " = " + id;
                } else {
                    selection = NewsDBHelper.POST_ID + " = " + id;
                }
                break;
            }
            case  FRIENDS_ID: {
                String id = uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection)) {
                    selection = selection + " AND " + FriendDBHelper.ID + " = " + id;
                } else {
                    selection = FriendDBHelper.ID + " = " + id;
                }
                break;
            }
            case  WALL: {
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = WallDBHelper.RAW_DATE + " DESC";
                break;
            }
            case  DIALOGS: {
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

            case  MESSAGES: {
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
                mColumnMap.put(MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.USER_DIALOG_ID, MessagesDBHelper.TABLENAME + "." + MessagesDBHelper.USER_DIALOG_ID + " as " + MessagesDBHelper.USER_DIALOG_ID);
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
 int insertCount = 0;
        URI_TYPE uriType = getUriTypeByUri(uri);
        SQLiteDatabase sqlDB = sDbManager.getWritableDatabase();
        String tableName = uriType.getTableName();
        switch (uriType) {
            case USERS: {
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
        URI_TYPE uriType = getUriTypeByUri(uri);
        SQLiteDatabase sqlDB = sDbManager.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case COMMENTS:
                rowsUpdated = sqlDB.update(CommentsDBHelper.TABLENAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case COMMENTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(CommentsDBHelper.TABLENAME,
                            values,
                            BaseColumns._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(CommentsDBHelper.TABLENAME,
                            values,
                            BaseColumns._ID  + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                    break;
                }

            case MESSAGES:
                rowsUpdated = sqlDB.update(MessagesDBHelper.TABLENAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case MESSAGES_ID:
                id = uri.getLastPathSegment();
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
        return rowsUpdated;
    }

}
