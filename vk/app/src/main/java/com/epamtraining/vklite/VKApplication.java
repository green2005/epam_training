package com.epamtraining.vklite;


import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.epamtraining.vklite.loader.ImageLoader;
import com.epamtraining.vklite.os.VKExecutor;

import java.util.HashMap;
import java.util.Map;

public class VKApplication extends Application {
    private Map<String, VKLocalService> mSystemServices;

    @Override
    public void onCreate() {
        super.onCreate();
        initLocalServices();
    }

    @Override
    public Object getSystemService(String name) {
        if (mSystemServices == null) {
            initLocalServices();
        }
        Object systemServiceResult = mSystemServices.get(name);
        if (systemServiceResult != null) {
            if (systemServiceResult instanceof StringHolder) {
                return ((StringHolder) systemServiceResult).getString();
            } else {
                return systemServiceResult;
            }
        } else {
            return super.getSystemService(name);
        }
    }

    public void setToken(String token) {
        if (mSystemServices == null) {
            initLocalServices();
        }
        mSystemServices.put(Api.TOKEN_KEY, new StringHolder(token));
    }

    public void setUserId(String userId) {
        if (mSystemServices == null) {
            initLocalServices();
        }
        mSystemServices.put(Api.USERID_KEY, new StringHolder(userId));
    }

    public void setUserInfo(String userName, String image) {
        if (mSystemServices == null) {
            initLocalServices();
        }
        mSystemServices.put(Api.USERID_NAME, new StringHolder(userName));
        mSystemServices.put(Api.USERID_IMAGE, new StringHolder(image));
    }

    private void initLocalServices() {
        mSystemServices = new HashMap<>();
        ImageLoader imageLoader = new ImageLoader(this);
        VKExecutor executor = new VKExecutor();
        mSystemServices.put(ImageLoader.KEY, imageLoader);
        mSystemServices.put(VKExecutor.KEY, executor);
    }

    public static <T> T get(Context context, String key) {
        if ((context == null) || (TextUtils.isEmpty(key))) {
            throw new IllegalArgumentException("Parameters are null");
        }
        T systemService = (T) context.getSystemService(key);
        if (systemService == null) {
            systemService = (T) context.getApplicationContext().getSystemService(key);
            if (systemService == null) {
                throw new IllegalArgumentException(String.format("Service by key %s is unavaliable", key));
            }
        }
        return systemService;
    }
}
