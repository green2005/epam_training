package com.epamtraining.vklite.imageLoader;

import android.graphics.BitmapFactory;
import android.util.Log;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ImageLoader {
    private class LoadingImages{
        private Map<Integer, HashSet<ImageView>> loadingImagesList;
        LoadingImages(){
            loadingImagesList = new ConcurrentHashMap<Integer,HashSet<ImageView>>();
        }

        void addImage(Integer hashCode, ImageView imageView){
            HashSet<ImageView> imageViews = loadingImagesList.get(hashCode);
            if (imageViews == null){
                imageViews = new HashSet<ImageView>();
                imageViews.add(imageView);
                loadingImagesList.put(hashCode, imageViews);
            } else
            {
                imageViews.add(imageView);
            }
        }

        void loadingDone(Integer hashCode){
            loadingImagesList.remove(hashCode);
        }

        boolean isImageLoading(Integer hashCode){
            return loadingImagesList.containsKey(hashCode);
        }

        HashSet<ImageView> getImageViews(Integer hashCode){
            return loadingImagesList.get(hashCode);
        }
    }

    private enum ImageThreadState{
      THREAD_DELETED,
      THREAD_NOT_EXISTS,
      THREAD_NOT_DELETED
    };

    private static int LOAD_IMAGE_DELAY = 400;
    private ImageCache mCache;

    private Context mContext;
    private long imagesLoaded = 0;
    private Handler mHandler;
    private LoadingImages mLoadingList;
    //private Map<Integer, Runnable> mLoadingList;
    private VKExecutor mExecutor;


    public ImageLoader(Context context) {
        mHandler = new Handler();
        mContext = context;
        mCache = new ImageCache(context);
        mExecutor = new VKExecutor(VKExecutor.ExecutorServiceType.BITMAP);
       // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) needBitmapRecycle = true;
        mLoadingList = new LoadingImages();//ConcurrentHashMap<Integer, Runnable>();
    }

    public void loadImage(final ImageView imageView, final String url) {
        if (imageView == null) {
            throw new IllegalArgumentException("ImageView cannot be null");
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
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mUrl.equals(mImageView.getTag()))
                                mImageView.setImageBitmap(bmp);
                        }
                    });
                } else {
                    final int imageHashCode = mUrl.hashCode();
                    if (mLoadingList.isImageLoading(imageHashCode)){
                        mLoadingList.addImage(imageHashCode, mImageView);
                        Log.d("image is loading", "image");
                    } else {
//                    Thread.sleep(LOAD_IMAGE_DELAY);
                        mLoadingList.addImage(imageHashCode, mImageView);
                        doLoadImage(mImageView, mUrl, mFileName, mHandler, imageHashCode);
                    }
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
                        fOut = new FileOutputStream(f);
                        copyStreams(is, fOut);
                    } finally {
                       is.close();
                        if (fOut != null) {
                            fOut.flush();
                            fOut.close();
                        }
                    }
                    FileInputStream bitmapStream = new FileInputStream(mFileName);
                    try {
                        final Bitmap bmp = BitmapFactory.decodeStream(bitmapStream);
                        mCache.addBitmapToLRUCache(imageUrl, bmp);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                    for (ImageView image:mLoadingList.getImageViews(imageHashCode)){
                                        if (imageUrl.equals(image.getTag()))
                                            image.setImageBitmap(bmp);
                                    }
                                mLoadingList.loadingDone(imageHashCode);
                            }
                        });
                    } finally {
                        bitmapStream.close();
                    }
                } catch (Exception e) {
                    mCache.removeImage(imageUrl);
                }
            }
        };
       // addLoadingImage(imageHashCode, imageThread);
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
//        synchronized (mLoadingList){
//            for (int hashCode  : mLoadingList.keySet()){
//                mExecutor.remove(mLoadingList.get(hashCode));
//            }
//            mLoadingList.clear();
//        }
    }

    public void clear() {
        mCache.clearImages();
    }

}
