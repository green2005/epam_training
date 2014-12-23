package com.epamtraining.vklite;


import android.app.Application;
import android.content.Context;
import android.media.Image;
import android.text.TextUtils;

import com.epamtraining.vklite.imageLoader.ImageLoader;
import com.epamtraining.vklite.os.VKExecutor;

import java.util.HashMap;

public class VKApplication extends Application {
    private ImageLoader mImageLoader;
    private VKExecutor mExecutor;
    private HashMap<String, Object> mSystemServices;

    @Override
    public void onCreate() {
        super.onCreate();
        mSystemServices = new HashMap<String, Object>();
        mImageLoader = new ImageLoader(this);
        mExecutor = new VKExecutor();
        mSystemServices.put(ImageLoader.KEY, mImageLoader);
        mSystemServices.put(VKExecutor.KEY, mExecutor);
    }

    @Override
    public Object getSystemService(String name) {
        Object systemServiceResult = mSystemServices.get(name);
        if (systemServiceResult != null) {
            return systemServiceResult;
        } else {
            if (name.equalsIgnoreCase(ImageLoader.KEY)){
                mImageLoader = new ImageLoader(this);
                mSystemServices.put(ImageLoader.KEY, mImageLoader);
                return mImageLoader;
            } else
            if (name.equalsIgnoreCase(VKExecutor.KEY)){
                mExecutor = new VKExecutor();
                mSystemServices.put(VKExecutor.KEY, mExecutor);
                return mExecutor;
            }
            return super.getSystemService(name);
        }
    }

    public static <T> T get(Context context, String key) throws Exception{
        if ((context == null)||(TextUtils.isEmpty(key))){
            throw new IllegalArgumentException("Parameters are null");
        }
        T systemService = (T) context.getSystemService(key);
        if (systemService == null){
            systemService = (T)context.getApplicationContext().getSystemService(key);
            if (systemService == null){
                throw new Exception("Service by key "+ key + " is unavaliable");
            }
        }
        return systemService;
    }
}
