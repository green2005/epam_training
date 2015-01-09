package com.epamtraining.vklite.imageLoader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.VKApplication;
import com.epamtraining.vklite.os.VKExecutor;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;


public class ImageLoader {
    private class LoadingImages {
        private Map<String, CopyOnWriteArraySet<ImageView>> mLoadingImagesList;
        private Map<String, Runnable> mRunningRunnables;

        LoadingImages() {
            mLoadingImagesList = new ConcurrentHashMap<String, CopyOnWriteArraySet<ImageView>>();
            mRunningRunnables = new ConcurrentHashMap<String, Runnable>();
        }

        Set<ImageView> getImageViewsByUrl(String url) {
            return mLoadingImagesList.get(url);
        }

        void addImage(String url, ImageView imageView) {
            CopyOnWriteArraySet<ImageView> imageViews = mLoadingImagesList.get(url);
            if (imageViews == null) {
                imageViews = new CopyOnWriteArraySet<ImageView>();
                imageViews.add(imageView);
                mLoadingImagesList.put(url, imageViews);
            } else {
                imageViews.add(imageView);
            }
        }

        void addThread(String url, Runnable imageRunnable) {
            mRunningRunnables.put(url, imageRunnable);
        }

        Set<Runnable> getThreads() {
            return new HashSet<Runnable>(mRunningRunnables.values());
        }

        void loadingDone(String url) {
            mLoadingImagesList.remove(url);
            mRunningRunnables.remove(url);
        }

        //  boolean isImageLoading(String url) {
        //      return mLoadingImagesList.containsKey(url);
        //  }

        private void clear() {
            mRunningRunnables.clear();
            mLoadingImagesList.clear();
        }

        Set<ImageView> getImageViews(String url) {
            return mLoadingImagesList.get(url);
        }
    }
    //private static int LOAD_IMAGE_DELAY = 400;
    private ImageCache mCache;
    private Handler mHandler;
    private LoadingImages mLoadingList;
    private VKExecutor mExecutor;
    public static final String KEY = "ImageLoader";
    private AtomicBoolean mIsResumed ;
    private LinkedList<Map<String,ImageView>> mPausedImages;

    public ImageLoader(Context context) {
        mHandler = new Handler();
        mCache = new ImageCache(context);
        mExecutor = new VKExecutor();
        // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) needBitmapRecycle = true;
        mLoadingList = new LoadingImages();
        mIsResumed = new AtomicBoolean(true);
        mPausedImages = new LinkedList<>();
    }

    public static ImageLoader getImageLoader(Context context) {
        try {
            return VKApplication.get(context, ImageLoader.KEY);
        } catch (Exception e) {
            ErrorHelper.showError(context, e);
        }
        return null;
    }

    private Bitmap scaleToFitWidth(Bitmap b, int width)
    {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

    public void loadImage(final ImageView imageView, final String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("ImageView cannot be null");
        }
        imageView.setImageBitmap(null);
        imageView.setTag(url);
        if (!TextUtils.isEmpty(url)) {
            Bitmap bmp = mCache.getBitmapFromLRUCache(url);
            if (bmp != null) {
                imageView.setImageBitmap(bmp);
             } else {
                if (mIsResumed.get()) {
                    Thread imageLoadThread = new ImageLoadThread(mHandler, imageView, url);
                    mLoadingList.addThread(url, imageLoadThread);
                    mExecutor.start(imageLoadThread);
                } else
                {   Map<String, ImageView> map = new HashMap<>();
                    map.put(url, imageView);
                    mPausedImages.push(map);
                }
            }
        }
    }

    public void pauseLoadingImages(){
        mIsResumed.set(false);
    }

    public boolean getIsPaused(){
        return  !mIsResumed.get();
    }

    public void resumeLoadingImages(){
        mIsResumed.set(true);
        while (!mPausedImages.isEmpty() && mIsResumed.get()) {
            Map<String, ImageView> map = mPausedImages.pop();
            for (String url:map.keySet()){
              ImageView imageView = map.get(url);
              if (url.equals(imageView.getTag())){
                  loadImage(imageView, url);
              }
            }
        }
        //mPausedImages.clear();
    }

    private class ImageLoadThread extends Thread {
        Handler mHandler;
        ImageView mImageView;
        String mUrl;

        ImageLoadThread(Handler handler, ImageView imageView, String url) {
            mHandler = handler;
            mImageView = imageView;
            mUrl = url;
        }

        public void run() {
            try {
                Set<ImageView> imageViews = mLoadingList.getImageViews(mUrl);
                if (imageViews != null) {
                    //image is already loading
                    imageViews.add(mImageView);
                } else {
                    //try to find it in cache
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
                        //                    Thread.sleep(LOAD_IMAGE_DELAY);
                        mLoadingList.addImage(mUrl, mImageView);
                        doLoadImage(mUrl, mHandler);
                    }
                }
            } catch (Exception e) {
                mCache.removeImage(mUrl);
                mLoadingList.loadingDone(mUrl);
            };
        }
    }

    private void doLoadImage(final String imageUrl, final Handler mHandler) {
        InputStream is = null;
        try {
            URL url = new URL(imageUrl);
            is = url.openStream();
            mCache.putImage(imageUrl, is);
            final Bitmap bmp = mCache.getImage(imageUrl);
            mCache.addBitmapToLRUCache(imageUrl, bmp);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Set<ImageView> imageViews = mLoadingList.getImageViews(imageUrl);
                    if (imageViews != null) {
                        for (ImageView image : imageViews) {
                            if (imageUrl.equals(image.getTag()))
                                image.setImageBitmap(bmp);
                        }
                        mLoadingList.loadingDone(imageUrl);
                    }
                }
            });
        } catch (Exception e) {
            mCache.removeImage(imageUrl);
            mLoadingList.loadingDone(imageUrl);
        }
    }

    public void stopLoadingImages() {
        for (Runnable r : mLoadingList.getThreads()) {
            mExecutor.remove(r);
        }
        mLoadingList.clear();
    }

    public void clear() {
        mCache.clearImages();
    }
}
