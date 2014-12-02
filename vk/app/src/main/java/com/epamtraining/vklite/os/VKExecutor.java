package com.epamtraining.vklite.os;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VKExecutor {
    public enum ExecutorServiceType {
        BITMAP, LOAD_DATA, CACHE
    }

    private static HashMap<ExecutorServiceType, ThreadPoolExecutor> executorsMap;
    private ThreadPoolExecutor mExecutor;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private Runnable mRunnable;

    static {
        executorsMap = new HashMap();
    }

    public VKExecutor(ExecutorServiceType executorServiceType, Runnable runnable){
        this(executorServiceType);
        mRunnable = runnable;
    }

    public VKExecutor(ExecutorServiceType executorServiceType){
        mExecutor = executorsMap.get(executorServiceType);
        if (mExecutor == null){
            mExecutor = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT, 0L, TimeUnit.MILLISECONDS, new LIFOLinkedBlockingDeque<Runnable>());
            executorsMap.put(executorServiceType, mExecutor);
        }
    }

    public void start(){
        if (mRunnable != null)
        mExecutor.execute(mRunnable);
    }

    public void start(Runnable runnable){
      if (runnable == null)
          throw  new IllegalArgumentException("Runnable cannot be null");
         mExecutor.execute(runnable);
    }

    public boolean remove(Runnable runnable){
        if (runnable == null)
            throw  new IllegalArgumentException("Runnable cannot be null");
        return mExecutor.remove(runnable);
     }

}
