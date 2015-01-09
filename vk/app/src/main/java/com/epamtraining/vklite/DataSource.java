package com.epamtraining.vklite;

import android.content.Context;

import com.epamtraining.vklite.processors.AdditionalInfoSource;
import com.epamtraining.vklite.processors.Processor;
import com.epamtraining.vklite.os.VKExecutor;

import java.io.InputStream;
import java.net.URL;

public class DataSource implements AdditionalInfoSource {


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
        //TODO remove package name from Hanler
        mHandler = new android.os.Handler();
    }

    private InputStream getInputStream(String href) throws Exception {
        URL url = new URL(href);
        return url.openStream();
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
                    mProcessor.process(inputStream, DataSource.this);
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
