package com.epam.training.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ImageView;

import com.epam.training.taskmanager.CoreApplication;
import com.epam.training.taskmanager.helper.DataManager;
import com.epam.training.taskmanager.os.assist.LIFOLinkedBlockingDeque;
import com.epam.training.taskmanager.processing.BitmapProcessor;
import com.epam.training.taskmanager.processing.Processor;
import com.epam.training.taskmanager.source.CachedHttpDataSource;
import com.epam.training.taskmanager.source.DataSource;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by IstiN on 10.12.2014.
 */
public class MySuperImageLoader<ProcessingResult, DestinationView> {

    public static final String KEY = "MySuperImageLoader";
    //TODO generate max memory based on device specification
    public static final int MAX_SIZE = (int) ((Runtime.getRuntime().maxMemory() / 1024) / 8);

    private Displayer<ProcessingResult, DestinationView> mDisplayer;

    private AtomicBoolean isPause = new AtomicBoolean(false);

    public static MySuperImageLoader get(Context context) {
        return CoreApplication.get(context, KEY);
    }

    private class ComparableRunnable implements Runnable {

        private Handler mHandler;

        private DataManager.Callback<ProcessingResult> callback;
        private String s;
        private DataSource<InputStream, String> dataSource;
        private Processor<ProcessingResult, InputStream> processor;

        public ComparableRunnable(Handler handler, DataManager.Callback<ProcessingResult> callback, String s, DataSource<InputStream, String> dataSource, Processor<ProcessingResult, InputStream> processor) {
            mHandler = handler;
            this.callback = callback;
            this.s = s;
            this.dataSource = dataSource;
            this.processor = processor;
        }

        @Override
        public void run() {
            try {
                InputStream result = dataSource.getResult(s);
                final ProcessingResult source = processor.process(result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDone(source);
                    }
                });
            } catch (final Exception e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(e);
                    }
                });
            }
        }
    }

    private Context mContext;

    private DataSource<InputStream, String> mDataSource;

    private Processor<ProcessingResult, InputStream> mProcessor;

    private DataManager.MySuperLoader<ProcessingResult, InputStream, String> mMySuperLoader;

    private LruCache<String, ProcessingResult> mLruCache = new LruCache<String, ProcessingResult>(MAX_SIZE) {

        @Override
        protected int sizeOf(String key, ProcessingResult value) {
            //TODO check correct calculation of bitmap size
            return  mDisplayer.getSize(value);
        }
    };

    public MySuperImageLoader(Context context, DataSource<InputStream, String> dataSource, Processor<ProcessingResult, InputStream> processor,
                              Displayer<ProcessingResult, DestinationView> displayer
                              ) {
        this.mContext = context;
        this.mDataSource = dataSource;
        this.mProcessor = processor;
        this.mDisplayer = displayer;
        //TODO can be customizable
        this.mMySuperLoader = new DataManager.MySuperLoader<ProcessingResult, InputStream, String>() {

            private ExecutorService executorService = new ThreadPoolExecutor(5, 5, 0, TimeUnit.MILLISECONDS,
                    new LIFOLinkedBlockingDeque<Runnable>());

            @Override
            public void load(final DataManager.Callback<ProcessingResult> callback, final String s, final DataSource<InputStream, String> dataSource, final Processor<ProcessingResult, InputStream> processor) {
                callback.onDataLoadStart();
                final Looper looper = Looper.myLooper();
                final Handler handler = new Handler(looper);
                executorService.execute(new ComparableRunnable(handler, callback, s, dataSource, processor));
            }
        };
    }

    public MySuperImageLoader(Context context, Processor<ProcessingResult, InputStream> processor, Displayer<ProcessingResult, DestinationView> displayer) {
        this(context, CachedHttpDataSource.get(context), processor, displayer);
    }

    public void pause() {
        isPause.set(true);
    }

    private final Object mDelayedLock = new Object();

    public void resume() {
        isPause.set(false);
        synchronized (mDelayedLock) {
            for (DestinationView destinationView : delayedImagesViews) {
                String url = mDisplayer.getUrl(destinationView);
                //Object tag = destinationView.getTag();
                if (url != null) {
                    loadAndDisplay((String) url, destinationView);
                }
            }
            delayedImagesViews.clear();
        }
    }

    private Set<DestinationView> delayedImagesViews = new HashSet<DestinationView>();

    public void loadAndDisplay(final String url, final DestinationView destinationView) {
        ProcessingResult processingResult = mLruCache.get(url);

        mDisplayer.setUrl(url, destinationView);
        mDisplayer.displayResult(processingResult, destinationView);

        if (processingResult != null) {
            return;
        }
        if (isPause.get()) {
            synchronized (mDelayedLock) {
                delayedImagesViews.add(destinationView);
            }
            return;
        }
        if (!TextUtils.isEmpty(url)) {
            DataManager.loadData(new DataManager.Callback<ProcessingResult>() {
                @Override
                public void onDataLoadStart() {

                }

                @Override
                public void onDone(ProcessingResult processingResult) {
                    if (processingResult != null) {
                        mLruCache.put(url, processingResult);
                    }
                    if (url.equals(mDisplayer.getUrl(destinationView))) {
                        mDisplayer.displayResult(processingResult, destinationView);
                    }
                }

                @Override
                public void onError(Exception e) {

                }

            }, url, mDataSource, mProcessor, mMySuperLoader);
        }
    }
}
