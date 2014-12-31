package com.epamtraining.vklite;

import android.content.Context;

import com.epamtraining.vklite.processors.Processor;
import com.epamtraining.vklite.os.VKExecutor;

import java.io.InputStream;
import java.net.URL;

public class DataSource {
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
        mHandler = new android.os.Handler();
    }

    private InputStream getInputStream(String href, Context context) throws Exception {
        URL url = new URL(href);
        return url.openStream();
    }

    public void fillData(final String url, final Context context) {
        Runnable dataLoader = new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = getInputStream(url, context);
                    mProcessor.process(inputStream);
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
