package com.epamtraining.vklite.imageLoader;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;

import com.epamtraining.vklite.os.VKExecutor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


public class ImageLoader {
    private class LoadingImages {
        private Map<String, CopyOnWriteArraySet<ImageView>> mLoadingImagesList;
        private Map<String, Runnable> mRunningThreads;

        LoadingImages() {
            mLoadingImagesList = new ConcurrentHashMap<String, CopyOnWriteArraySet<ImageView>>();
            mRunningThreads = new ConcurrentHashMap<String, Runnable>();
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

        void addThread(String url, Runnable imageThread) {
            mRunningThreads.put(url, imageThread);
        }

        Set<Runnable> getThreads() {
            HashSet<Runnable> threads = new HashSet<Runnable>();
            for (String s : mRunningThreads.keySet()) {
                threads.add(mRunningThreads.get(s));
            }
            return threads;
        }

        void loadingDone(String url) {
            mLoadingImagesList.remove(url);
            mRunningThreads.remove(url);
        }

        boolean isImageLoading(String url) {
            return mLoadingImagesList.containsKey(url);
        }

        private void clear() {
            mRunningThreads.clear();
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

    public ImageLoader(Context context) {
        mHandler = new Handler();
        mCache = new ImageCache(context);
        mExecutor = new VKExecutor(VKExecutor.ExecutorServiceType.BITMAP);
        // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) needBitmapRecycle = true;
        mLoadingList = new LoadingImages();
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
                new LoadThread(mHandler, imageView, url).start();
            }
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
                    if (mLoadingList.isImageLoading(mUrl)) {
                        mLoadingList.addImage(mUrl, mImageView);
                        Log.d("image is loading", "image");
                    } else {
//                    Thread.sleep(LOAD_IMAGE_DELAY);
                        mLoadingList.addImage(mUrl, mImageView);
                        doLoadImage(mImageView, mUrl, mHandler);
                    }
                }
            } catch (Exception e) {
                mCache.removeImage(mUrl);
                mLoadingList.loadingDone(mUrl);
            }
            ;
        }
    }

    private void doLoadImage(final ImageView imageView, final String imageUrl, final Handler mHandler) {
        Runnable imageThread = new Runnable() {
            @Override
            public void run() {
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
        };
        mLoadingList.addThread(imageUrl, imageThread);
        mExecutor.start(imageThread);
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
