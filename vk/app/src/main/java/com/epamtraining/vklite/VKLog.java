package com.epamtraining.vklite;

import android.util.Log;

public class VKLog {
    public static int v(String tag, String msg){
        return Log.d(tag, msg);
    }

    public static int d(String tag, String msg){
        return Log.d(tag, msg);
    }

    public static int i(String tag, String msg){
        return Log.i(tag, msg);
    }

    public static int w(String tag, String msg){
        return Log.w(tag, msg);
    }

    public static int e(String tag, String msg){
        return Log.e(tag, msg);
    }
}
