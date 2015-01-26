package com.epamtraining.vklite.db;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

public class UIQueryHelper {
    public interface OnQueryResultListener {
        public void onQueryResult(Cursor cursor);
    }

    public interface OnInsertResultListener{
        public void onInsertSuccess();
        public void onError(Exception e);
    }

    private ContentResolver mResolver;

    public UIQueryHelper(ContentResolver contentResolver) {
        if (contentResolver == null) {
            throw new IllegalArgumentException("ContentResolver cannot be null");
        }
        mResolver = contentResolver;
    }

    public void insert(final Uri uri, final ContentValues values,final OnInsertResultListener insertResultListener){
        if (uri == null) {
            throw new IllegalArgumentException("Uri cannot be null");
        }
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mResolver.insert(uri, values);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            insertResultListener.onInsertSuccess();
                        }
                    });
                } catch (Exception e)
                {
                    insertResultListener.onError(e);
                }
            }
        }).start();

    }

    public void query(final OnQueryResultListener queryResultListener, final Uri uri,
                      final String[] projection, final String selection, final String[] selectionArgs,
                      final String sortOrder) {
        if (uri == null) {
            throw new IllegalArgumentException("Uri cannot be null");
        }
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = mResolver.query(uri, projection, selection, selectionArgs, sortOrder);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        queryResultListener.onQueryResult(cursor);
                    }
                });
            }
        }).start();
    }
}
