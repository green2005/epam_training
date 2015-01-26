package com.epamtraining.vklite.commiters;


import android.content.Context;
import android.database.Cursor;
import android.os.Handler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

//TODO join with SyncAdapter
public abstract class Commiter {
    abstract protected Cursor getPendingChanges();                      //select records that are marked as pending
    abstract protected String getUrl(Cursor cursor) throws  Exception;                  //returns post url by cursor
    abstract protected boolean checkIsResponseCorrect(String response) throws Exception; //check if the server response correct
    abstract protected void setRecordAffected(Cursor cursor);           //set records not marked as pending

    private CommiterCallback mCallBack;
    private Context mContext;

    public Commiter(CommiterCallback callback, Context context ){
        if (context  == null){
            throw new IllegalArgumentException("Context parameter is null");
        }
        if (callback == null){
            throw new IllegalArgumentException("Callback parameter is null");
        }
        mCallBack = callback;
        mContext = context;
    }

    public void commit(){
        final Handler handler= new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getPendingChanges();
                try{
                    try {
                        commitPendingChanges(cursor);
                    }catch (final Exception e){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mCallBack.onException(e);
                            }
                        });
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallBack.onAfterExecute();
                        }
                    });
                } finally
                {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).start();
    }


    private void commitPendingChanges(Cursor cr) throws Exception{
        if (cr == null){return;}
        cr.moveToFirst();
        while (!cr.isAfterLast()){
            String url = getUrl(cr);
            if (post(url)){
                setRecordAffected(cr);
            }
            cr.moveToNext();
        }
    }

    private boolean post(String url) throws  Exception{
        URL serverUrl =
                new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection)serverUrl.openConnection();
        urlConnection.setRequestMethod("POST");
        Scanner httpResponseScanner = new Scanner(urlConnection.getInputStream());
        StringBuilder builder = new  StringBuilder();
        while(httpResponseScanner.hasNextLine()) {
            builder.append(httpResponseScanner.nextLine());
        }
        httpResponseScanner.close();
        return checkIsResponseCorrect(builder.toString());
    }

}
