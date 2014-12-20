package com.epamtraining.servicesample.testserviceapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LoaderReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getExtras().getInt(LoaderService.PARAM_STATE);
        switch (state){
            case LoaderService.STATE_FINISHED:{
                Log.d("loader","finished");
                break;
            }
            case LoaderService.STATE_PROGRESS:{
                Log.d("loader","progress");
                break;
            }
            case LoaderService.STATE_STARTED:{
                Log.d("loader","started");
                break;
            }
        }
    }
}
