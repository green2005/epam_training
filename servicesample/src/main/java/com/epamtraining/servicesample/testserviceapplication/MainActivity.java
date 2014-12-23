package com.epamtraining.servicesample.testserviceapplication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Method;


public class MainActivity extends ActionBarActivity {

    private class LoadingThread extends Thread {
        private volatile boolean isActive = true;

        public void stopThread() {
            isActive = false;
        }

        @Override
        public void run() {
            super.run();
            try {
                int i = 0;
                while (isActive) {
                    this.sleep(400);
                    Log.d("thread", "Thread is running" + i++);
                }

            } catch (InterruptedException e) {
                Log.d("Thread", "It's killed");
            }
            ;
        }
    }

    private LoadingThread mLoadingThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            //результат запуска сервиса с BroadCastReceiver
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getExtras().getInt(LoaderService.PARAM_STATE);
                processServiceResult(state);
            }
        };
        IntentFilter intentFilter = new IntentFilter(LoaderService.ACTION_NAME);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, LoaderService.class));
        super.onDestroy();
    }

    public void startPendingIntentService(View view) {
        Intent i = new Intent();
        PendingIntent pendingIntent = createPendingResult(LoaderService.LOAD_IMAGES, i, 0);
        Intent intent = new Intent(this, LoaderService.class);
        intent.putExtra(LoaderService.PARAM_ACTIVITY_CONNECTION_TYPE, LoaderService.USE_PENDING_INTENT);
        intent.putExtra(LoaderService.PARAM_PENDING_INTENT, pendingIntent);
        startService(intent);
    }

    public void startBroadCastService(View view) {
        Intent intent = new Intent(this, LoaderService.class);
        intent.putExtra(LoaderService.PARAM_ACTIVITY_CONNECTION_TYPE, LoaderService.USE_BROADCAST_RECIEVER);
        startService(intent);
    }

    public void stopThread(View v) {
        if (mLoadingThread != null) {
            LoadingThread tmpThread = mLoadingThread;
            mLoadingThread = null;
            tmpThread.stopThread();
        }
    }

    public void stopThread2(View v) {
        if (mLoadingThread != null) {
            mLoadingThread.interrupt();
            mLoadingThread = null;
        }
    }

    public void startThread(View v) {
        mLoadingThread = new LoadingThread();
        mLoadingThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //результат запуска сервиса с PendingIntent
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoaderService.LOAD_IMAGES) {
            processServiceResult(resultCode);
        }
    }

    private void processServiceResult(int state) {
        switch (state) {
            case LoaderService.STATE_FINISHED: {
                Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show();
                break;
            }
            case LoaderService.STATE_PROGRESS: {
                Toast.makeText(this, "progress", Toast.LENGTH_SHORT).show();
                break;
            }
            case LoaderService.STATE_STARTED: {
                Toast.makeText(this, "started", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
