package com.epamtraining.vklite.os;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.epamtraining.vklite.VKApplication;
import com.epamtraining.vklite.VKLocalService;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VKExecutor implements VKLocalService{


    private ThreadPoolExecutor mExecutor;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private Runnable mRunnable;
    public static final String KEY = "Executor";

    public VKExecutor() {
        mExecutor = new ThreadPoolExecutor(CPU_COUNT, CPU_COUNT, 0L, TimeUnit.MILLISECONDS, new LIFOLinkedBlockingDeque<Runnable>());
      }

      public void start() {
        if (mRunnable != null) {
            mExecutor.execute(mRunnable);
        }
    }

    public static VKExecutor getExecutor(Context context){
        try {
            return VKApplication.get(context, VKExecutor.KEY);
        } catch (Exception e) {
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle("Exception");
            b.setMessage(e.getMessage());
            b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = b.create();
            dialog.show();
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
