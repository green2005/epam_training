package com.epamtraining.servicesample.testserviceapplication;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;


public class LoaderService extends Service {
    public static final int LOAD_IMAGES = 0;

    public static final int STATE_STARTED = 0;
    public static final int STATE_PROGRESS = 1;
    public static final int STATE_FINISHED = 2;

    public static final String PARAM_PENDING_INTENT = "param_pending_intent";
    public static final String PARAM_STATE = "state";
    public static final String PARAM_ACTIVITY_CONNECTION_TYPE = "connection_type";

    public static final int USE_BROADCAST_RECIEVER = 0;
    public static final int USE_PENDING_INTENT = 1;

    private PendingIntent mPendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int connectionType = intent.getIntExtra(PARAM_ACTIVITY_CONNECTION_TYPE, USE_BROADCAST_RECIEVER);

        if (connectionType == USE_PENDING_INTENT && intent.hasExtra(PARAM_PENDING_INTENT)) {
               mPendingIntent = intent.getParcelableExtra(LoaderService.PARAM_PENDING_INTENT);
        };

        final Handler handler= new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendMessageToactivity(connectionType, STATE_STARTED);
                        Thread.sleep(100); ///тут что-то выполняется
                        sendMessageToactivity(connectionType, STATE_PROGRESS);
                        Thread.sleep(800); ///тут что-то еще выполняется
                        sendMessageToactivity(connectionType, STATE_FINISHED);
                    } catch(final Exception e){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoaderService.this);
                                AlertDialog dialog = builder.create();
                                dialog.setMessage(e.getMessage());
                                dialog.setTitle("Error");
                                dialog.show();
                            }
                        });
                    }
                }
            }).start();
        //mPendingIntent.getCreatorPackage()
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendMessageToactivity (int connectionType, int state) throws Exception{
        switch (connectionType){
            case (USE_PENDING_INTENT):{
                if (mPendingIntent != null) {
                    mPendingIntent.send(LoaderService.this, state, null);
                }
                break;
            }
            case (USE_BROADCAST_RECIEVER):{
                Intent intent = new Intent();
                intent.setAction("com.epamtraining.servicesample.testservice");
                intent.putExtra(PARAM_STATE, state);
                sendBroadcast(intent);
                break;
            }
        }
    }
}
