package com.epam.training.taskmanager.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.epam.training.taskmanager.MainActivity;
import com.epam.training.taskmanager.helper.DataManager;
import com.epam.training.taskmanager.processing.BitmapProcessor;
import com.epam.training.taskmanager.source.HttpDataSource;

public class ImageLoader {
    private static int LOAD_IMAGE_DELAY = 400;
    private ImageCache mCache;

    private Context mContext;
    private long imagesLoaded = 0;
    private boolean needBitmapRecycle = false;

    public ImageLoader(Context context) {
        mContext = context;
        mCache = new ImageCache(context);
         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) needBitmapRecycle = true;
    }

    public void loadImage(final ImageView imageView, final String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("ImageView cannot be null");
        }

        Bitmap oldBmp = null;
        if (needBitmapRecycle)
            if (imageView.getDrawable() != null)
            oldBmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        imageView.setImageBitmap(null);
        if (oldBmp != null){
            oldBmp.recycle();
            oldBmp = null;
        }
        imageView.setImageBitmap(null);

        imageView.setTag(url);
        if (!TextUtils.isEmpty(url)) {
            //TODO add delay and cancel old request or create limited queue - done
            //TODO create sync Map to check existing request and existing callbacks
            //TODO create separate thread pool for manager  - done
            Handler handler = new Handler();
            new LoadThread(handler, imageView, url).start();
        }
    }

    private class LoadThread extends Thread {
        Handler mHandler;
        ImageView mImageView;
        String mUrl;

        LoadThread(Handler handler, ImageView imageView, String url) {
            mHandler = handler;
            mImageView = imageView;
            mUrl = url;
        }

        public void run() {
            try {
              Thread.sleep(LOAD_IMAGE_DELAY);
              final Bitmap bmp = mCache.getImage(mUrl);
               if (bmp != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mUrl.equals(mImageView.getTag()))
                            mImageView.setImageBitmap(bmp);
                        }
                    });
                } else {
                    Thread.sleep(LOAD_IMAGE_DELAY);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            doLoadImage(mImageView, mUrl);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } ;
        }
    }
  
    private void doLoadImage(final ImageView imageView, final String url) {
        if (!url.equals(imageView.getTag())) {
            Log.d("ImageLoadCanceled", "");
            return; // the tag has changed, may be listView scrolling
        }
        DataManager.loadData(new DataManager.Callback<Bitmap>() {
            @Override
            public void onDataLoadStart() {
            }

            @Override
            public void onDone(final Bitmap bitmap) {
                imagesLoaded++;
                Log.d("ImagesLoaded", imagesLoaded + "");
                if (url.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                }
           }

            @Override
            public void onError(Exception e) {
            }
        }, url, HttpDataSource.get(mContext), new BitmapProcessor(getImageCacheDir(), url, imageView), MainActivity.LOAD_BITMAP_POOL);
    }

    public String getImageCacheDir(){
        return mCache.getFilecahedir();
    }

    public void clear() {
        mCache.clearImages();
    }
}
