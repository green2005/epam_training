package com.epamtraining.vklite;

import android.content.Context;

import com.epamtraining.vklite.processors.Processor;
import com.epamtraining.vklite.os.VKExecutor;

import java.io.InputStream;
import java.net.URL;

public class DataSource {
    public interface DataSourceCallbacks {
        public void onError(Exception e);

        public void onLoadEnd();

        public void onBeforeStart();
    }

    public enum DataLocation {WEB, ASSETS}

    ;

    private Exception mException;
    private Processor mProcessor;
    private android.os.Handler mHandler;
    private DataSourceCallbacks mCallbacks;

    public DataSource(Processor processor, DataSourceCallbacks callbacks) {
        mProcessor = processor;
        mCallbacks = callbacks;
        mHandler = new android.os.Handler();
    }

    private InputStream getInputStream(Context context) throws Exception {
        URL url = new URL(mProcessor.getUrl());
        return url.openStream();
    }

    public void fillData(final Context context) {
        Runnable dataLoader = new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = getInputStream(context);
                    mProcessor.process(inputStream);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallbacks.onLoadEnd();
                        }
                    });
                } catch (final Exception e) {
                    mException = e;
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
        // /new VKExecutor(dataLoader).start();
    }
}
