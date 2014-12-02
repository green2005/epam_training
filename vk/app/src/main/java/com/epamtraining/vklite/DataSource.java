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

    private InputStream getIputStream(DataLocation location, Context context) throws  Exception{
             switch (location) {
                case ASSETS: {
                    String assetName = mProcessor.getAssetName();
                    return context.getAssets().open(assetName);
                }
                case WEB: {
                    URL url = new URL(mProcessor.getUrl());
                    return url.openStream();
                }
            };
         return null;
    }

    public void fillData(final DataLocation dataLocation, final Context context) {
        Runnable dataLoader = new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = getIputStream(dataLocation, context);
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
        new VKExecutor(mProcessor.getExecutorType(), dataLoader).start();
    }
}
