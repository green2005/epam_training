package com.epamtraining.vklite;

import android.content.Context;
import android.content.res.Resources;

import com.epamtraining.vklite.Processors.FriendsProcessor;
import com.epamtraining.vklite.Processors.Processor;
import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.os.VKExecutor;

import java.net.URL;
import java.util.List;
import java.util.logging.Handler;

public class DataSource {
    interface DataSourceCallbacks {
        public void onError(Exception e);

        public void onLoadEnd();

        public void onBeforeStart();
    }

    private Exception mException;
    private Processor mProcessor;
    private android.os.Handler mHandler;
    private DataSourceCallbacks mCallbacks;

    public DataSource(Processor processor, DataSourceCallbacks callbacks) {
        mProcessor = processor;
        mCallbacks = callbacks;
    }

    public void fillData() {
        mHandler = new android.os.Handler();
        Runnable dataLoader = new Runnable() {
            @Override
            public void run() {
                try {
                    Context xn;
                    URL url = new URL(mProcessor.getUrl());
                    mProcessor.process(url.openStream());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallbacks.onLoadEnd();
                        }
                    });
                } catch (Exception e) {
                    mException = e;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCallbacks.onError(mException);
                        }
                    });
                }
            }
        };
        mCallbacks.onBeforeStart();
        new VKExecutor(mProcessor.getExecutorType(), dataLoader).start();
    }
}
