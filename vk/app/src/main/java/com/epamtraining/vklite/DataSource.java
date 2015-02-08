package com.epamtraining.vklite;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.epamtraining.vklite.os.VKExecutor;
import com.epamtraining.vklite.processors.AdditionalInfoSource;
import com.epamtraining.vklite.processors.Processor;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DataSource implements AdditionalInfoSource {
    //  private static  final int CONNECT_TIMEOUT = 7000;
    //  private static final int READ_TIMEOUT = 10000;

    public interface  DataSourceCallbacks{
        public void onError(Exception e);
        public void onBeforeStart();
        public void onLoadEnd(int recordsFetched);
    }

    public interface DataSourceCallbacksResult extends DataSourceCallbacks{
        public void onResult(Bundle result);
    }

    private Processor mProcessor;
    private android.os.Handler mHandler;
    private DataSourceCallbacks mCallbacks;

    public DataSource(Processor processor, DataSourceCallbacks callbacks) {
        mProcessor = processor;
        mCallbacks = callbacks;
        mHandler = new Handler();
    }

    private InputStream getInputStream(String href) throws Exception {
        URL url = new URL(href);
        URLConnection con = url.openConnection();
        // con.setConnectTimeout(CONNECT_TIMEOUT);
        //  con.setReadTimeout(READ_TIMEOUT);
        return con.getInputStream();
    }

    @Override
    public InputStream getAdditionalInfo(String href) throws Exception {
        return getInputStream(href);
    }

    public void fillData(final String url, final Context context) {
        Runnable dataLoader = new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = getInputStream(url);
                    mProcessor.process(inputStream, url, DataSource.this);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallbacks.onLoadEnd(mProcessor.getRecordsFetched());
                            if (mCallbacks instanceof  DataSourceCallbacksResult) {
                                ((DataSourceCallbacksResult) mCallbacks).onResult(mProcessor.getResult());
                            }
                        }
                    });
                } catch (final Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            VKLog.e("Exception", e.getMessage());
                            e.printStackTrace();
                            mCallbacks.onError(e);
                        }
                    });
                }
            }
        };
        mCallbacks.onBeforeStart();
        VKExecutor executor = VKExecutor.getExecutor(context);
        if (executor != null) {
            executor.start(dataLoader);
        }
    }
}
