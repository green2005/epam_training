package com.epamtraining.vklite.os;

import android.content.Context;

import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.VKApplication;
import com.epamtraining.vklite.VKLocalService;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VKExecutor implements VKLocalService {


    private ThreadPoolExecutor mExecutor;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    public static final String KEY = "Executor";

    public VKExecutor() {
        mExecutor = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT, 0L, TimeUnit.MILLISECONDS, new LIFOLinkedBlockingDeque<Runnable>());
    }

    public static VKExecutor getExecutor(Context context) {
        try {
            return VKApplication.get(context, VKExecutor.KEY);
        } catch (Exception e) {
            ErrorHelper.showError(context, e);
        }
        return null;
    }

    public void start(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        mExecutor.execute(runnable);
    }

    public boolean remove(Runnable runnable) {
        if (runnable == null)
            throw new IllegalArgumentException("Runnable cannot be null");
        return mExecutor.remove(runnable);
    }
}
