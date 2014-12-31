package com.epamtraining.vklite;


import android.app.Application;
import android.content.Context;
import android.media.Image;
import android.text.TextUtils;

import com.epamtraining.vklite.imageLoader.ImageLoader;
import com.epamtraining.vklite.os.VKExecutor;

import java.util.HashMap;
import java.util.Map;

public class VKApplication extends Application {
    private ImageLoader mImageLoader;
    private VKExecutor mExecutor;
    private Map<String, Object> mSystemServices;
    private String mToken;

    @Override
    public void onCreate() {
        super.onCreate();
        initLocalServices();
    }

    @Override
    public Object getSystemService(String name) {
        if (mSystemServices == null){
            initLocalServices();
        }
        Object systemServiceResult = mSystemServices.get(name);
        if (systemServiceResult != null) {
            return systemServiceResult;
        } else {
            return super.getSystemService(name);
        }
    }

    public void setToken(String token){
        if (mSystemServices == null){
            initLocalServices();
        }
        mSystemServices.put(Api.TOKEN_KEY, token);
    }

    private void initLocalServices(){
        mSystemServices = new HashMap<String, Object>();
        mImageLoader = new ImageLoader(this);
        mExecutor = new VKExecutor();
        mSystemServices.put(ImageLoader.KEY, mImageLoader);
        mSystemServices.put(VKExecutor.KEY, mExecutor);
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
