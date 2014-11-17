package com.epam.training.taskmanager.os;

import android.os.Handler;

import com.epam.training.taskmanager.os.assist.LIFOLinkedBlockingDeque;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by IstiN on 24.10.2014.
 */
public abstract class AsyncTask<Params, Progress, Result> {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();


    private static HashMap<String, ExecutorService> executorsMap;
    private ExecutorService mExecutor;

    static {
        executorsMap = new HashMap();
    }

    /*
    We may need named Thread Pools, for different types of tasks -
        loading data,
        loading images,
        caching operations
     */
    public AsyncTask(String poolName) {
        mExecutor = executorsMap.get(poolName);
        if (mExecutor == null){
            mExecutor = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT, 0L, TimeUnit.MILLISECONDS, new LIFOLinkedBlockingDeque<Runnable>());
           executorsMap.put(poolName, mExecutor);
        }
    }

    protected void onPreExecute() {

    }

    protected void onPostExecute(Result processingResult) {

    }

    protected abstract Result doInBackground(Params... params) throws Exception;

    public void execute(final Params... params) {
        final Handler handler = new Handler();
        onPreExecute();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    final Result result = doInBackground(params);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPostExecute(result);
                        }
                    });
                } catch (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onPostException(e);
                        }
                    });
                }
            }
        });
    }

    protected abstract void onPostException(Exception e);
}
