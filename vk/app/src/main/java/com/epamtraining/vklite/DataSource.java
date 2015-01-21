package com.epamtraining.vklite;

import android.content.Context;
import android.os.Handler;

import com.epamtraining.vklite.processors.AdditionalInfoSource;
import com.epamtraining.vklite.processors.Processor;
import com.epamtraining.vklite.os.VKExecutor;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DataSource implements AdditionalInfoSource {
    private static  final int CONNECT_TIMEOUT = 3000;
    private static final int READ_TIMEOUT = 5000;


    public interface DataSourceCallbacks {
        public void onError(Exception e);
        public void onLoadEnd(int recordsFetched);
        public void onBeforeStart();
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
        con.setConnectTimeout(CONNECT_TIMEOUT);
        con.setReadTimeout(READ_TIMEOUT);
        return con.getInputStream();
    }

    @Override
    public InputStream getAdditionalInfo(String href)throws Exception {
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
                        }
                    });
                } catch (final Exception e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
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
