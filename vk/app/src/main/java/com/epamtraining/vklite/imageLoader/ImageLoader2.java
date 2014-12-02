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
import java.util.HashMap;
import java.util.Map;


public class ImageLoader2 {
    private static int LOAD_IMAGE_DELAY = 400;
    private ImageCache mCache;

    private Context mContext;
    private long imagesLoaded = 0;
    private boolean needBitmapRecycle = false;
    private Handler mHandler;
    private Map<Integer, ImageView> mLoadingList;

    public ImageLoader2(Context context) {
        mHandler = new Handler();
        mContext = context;
        mCache = new ImageCache(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) needBitmapRecycle = true;
        mLoadingList = new HashMap<Integer, ImageView>();
    }

    private void  addLoadingImage(int cache, ImageView imageView){
        synchronized (mLoadingList){
            mLoadingList.put(cache, imageView);
        }
    }

    private void delLoadingImage(int cache ){
        synchronized (mLoadingList){
           if (mLoadingList.containsKey(cache)){
               mLoadingList.remove(cache);
           }
        }
    }

    private ImageView getImageViewByUrlCache(int cache){
        synchronized (mLoadingList){
            return mLoadingList.get(cache);
        }
    }

    private boolean getIsImageLoading(int cache){
        synchronized (mLoadingList){
            return  mLoadingList.containsKey(cache);}
            /*){
                return mLoadingList.get(cache) == null;
            } else
                return false;
        }
        */
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
                    Thread.sleep(LOAD_IMAGE_DELAY);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mUrl.equals(mImageView.getTag()))
                                mImageView.setImageBitmap(bmp);
                        }
                    });
                } else {
                    final int imageHashCode = mUrl.hashCode();
                    if (getIsImageLoading(imageHashCode)){
                        addLoadingImage(imageHashCode, mImageView);//картинка грузится в другом потоке
                                                       // сохраняем imageView , чтобы после загрузки картинки
                                                       //загрузить ее содержимое в ImageView
                        return;
                    } else
                    {
                        addLoadingImage(imageHashCode, null);
                    }
                    Thread.sleep(LOAD_IMAGE_DELAY);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            doLoadImage(mImageView, mUrl, mFileName, mHandler, imageHashCode);
                        }
                    });
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
                       // if (!imageUrl.equals(imageView.getTag()))
                     //       return;
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
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ImageView imw = getImageViewByUrlCache(imageHashCode);
                                if ((imw != null)&&(imageUrl.equals(imw.getTag())))
                                    imw.setImageBitmap(bmp);

                                     delLoadingImage(imageHashCode);

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
        new VKExecutor(VKExecutor.ExecutorServiceType.BITMAP, imageThread).start();
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

        /*   );


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
    }*/

    public String getImageCacheDir() {
        return mCache.getFilecahedir();
    }

    public void clear() {
        mCache.clearImages();
    }
}
