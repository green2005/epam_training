package com.epamtraining.vklite.imageLoader;

import android.graphics.BitmapFactory;
import android.widget.ImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import com.epamtraining.vklite.os.VKExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ImageLoader {
    private enum ImageThreadState{
      THREAD_DELETED,
      THREAD_NOT_EXISTS,
      THREAD_NOT_DELETED
    };

    private static int LOAD_IMAGE_DELAY = 400;
    private ImageCache mCache;

    private Context mContext;
    private long imagesLoaded = 0;
    private boolean needBitmapRecycle = false;
    private Handler mHandler;
    private Map<Integer, Runnable> mLoadingList;
    private VKExecutor mExecutor;


    public ImageLoader(Context context) {
        mHandler = new Handler();
        mContext = context;
        mCache = new ImageCache(context);
        mExecutor = new VKExecutor(VKExecutor.ExecutorServiceType.BITMAP);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) needBitmapRecycle = true;
        mLoadingList = new ConcurrentHashMap<Integer, Runnable>();
    }

    private void  addLoadingImage(int hashCode, Runnable runnable){
            mLoadingList.put(hashCode, runnable);
    }

    private ImageThreadState delLoadingImage(int hashCode ){
            Runnable r =  mLoadingList.get(hashCode);
            if (r != null){
                if (mExecutor.remove(r)){
                    {
                        return ImageThreadState.THREAD_DELETED;}
                } else return ImageThreadState.THREAD_NOT_DELETED;
            } else
            {return ImageThreadState.THREAD_NOT_EXISTS;}
    }

    private void doneLoadingImage(int hashCode){
            mLoadingList.remove(hashCode);
    }

    public void loadImage(final ImageView imageView, final String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("ImageView cannot be null");
        }

        Bitmap oldBmp = null;
        if (needBitmapRecycle)
            if (imageView.getDrawable() != null)
                oldBmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        //imageView.setImageBitmap(null);
        if (oldBmp != null) {
            oldBmp.recycle();
            oldBmp = null;
        }
        imageView.setImageBitmap(null);
        imageView.setTag(url);
        if (!TextUtils.isEmpty(url)) {
           new LoadThread(mHandler, imageView, url ).start();

        }
    }

    private class LoadThread extends Thread {
        Handler mHandler;
        ImageView mImageView;
        String mUrl;
        String mFileName;

        LoadThread(Handler handler, ImageView imageView, String url ) {
            mHandler = handler;
            mImageView = imageView;
            mUrl = url;
            mFileName = mCache.getFileName(url);
        }

        public void run() {
            try {
                final Bitmap bmp = mCache.getImage(mUrl);
                if (bmp != null) {
                   // Thread.sleep(LOAD_IMAGE_DELAY);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mUrl.equals(mImageView.getTag()))
                                mImageView.setImageBitmap(bmp);
                        }
                    });
                } else {
                    final int imageHashCode = mUrl.hashCode();
                    ImageThreadState state = delLoadingImage(imageHashCode); //ищем поток, который грузит ту же картинку
                    if (state == ImageThreadState.THREAD_NOT_EXISTS)        //
                    Thread.sleep(LOAD_IMAGE_DELAY);
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                doLoadImage(mImageView, mUrl, mFileName, mHandler, imageHashCode);
                            }
                        };
                        mHandler.post(r);
                }
            } catch (Exception e) {
                e.printStackTrace();
            };
        }
    }

    private void doLoadImage(final ImageView imageView, final String imageUrl, final String mFileName, final Handler mHandler,
                             final int imageHashCode) {
        Runnable imageThread = new Runnable() {
            @Override
            public void run() {
                try {
                    File f = new File(mFileName);
                    f.createNewFile();
                    InputStream is = null;
                    FileOutputStream fOut = null;
                    URL url = new URL(imageUrl);
                    try {
                        is = url.openStream();
                        fOut = new FileOutputStream(mFileName);
                         copyStreams(is, fOut);
                    } finally {
                        doneLoadingImage(imageHashCode);
                        is.close();
                        if (fOut != null) {
                            fOut.flush();
                            fOut.close();
                        }
                    }
                    FileInputStream bitmapStream = new FileInputStream(mFileName);
                    try {
                        final Bitmap bmp = BitmapFactory.decodeStream(bitmapStream);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {

                                    if (imageUrl.equals(imageView.getTag()))
                                    imageView.setImageBitmap(bmp);
                            }
                        });
                    } finally {
                        bitmapStream.close();
                    }
                } catch (Exception e) {
                    //TODO exception handling
                    e.printStackTrace();
                }
            }
        };
        addLoadingImage(imageHashCode, imageThread);
        mExecutor.start(imageThread);
//        new VKExecutor(VKExecutor.ExecutorServiceType.BITMAP, imageThread).start();
    }

    private void copyStreams(InputStream streamSource, OutputStream streamDest) throws Exception {
        int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            int cnt = 0;
            while (cnt != -1) {
                cnt = streamSource.read(buffer, 0, BUFFER_SIZE);
                if (cnt != -1)
                    streamDest.write(buffer, 0, cnt);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public String getImageCacheDir() {
        return mCache.getFilecahedir();
    }

    public void stopLoadingImages(){
        synchronized (mLoadingList){
            for (int hashCode  : mLoadingList.keySet()){
                mExecutor.remove(mLoadingList.get(hashCode));
            }
            mLoadingList.clear();
        }
    }

    public void clear() {
        mCache.clearImages();
    }



}
