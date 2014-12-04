package com.epamtraining.vklite.imageLoader;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import java.io.File;

public class ImageCache {
    private Context mContext;
    private File filePath;
    private LruCache<String, Bitmap> mLruCache;

    public ImageCache(Context context) {
        mContext = context;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            filePath = context.getExternalFilesDir(null);
        else
            filePath = context.getCacheDir();
        if (!filePath.exists())
            filePath.mkdirs();

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                  return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToLRUCache(String url, Bitmap bitmap) {
        if (mLruCache.get(url) == null) {
            mLruCache.put(url, bitmap);
        }
    }

    private Bitmap getBitmapFromLRUCache(String url) {
        return mLruCache.get(url);
    }

    public String getFileName(String imageUrl) {
        return filePath.getAbsolutePath() + File.separator + imageUrl.hashCode();
    }

    public void removeImage(String url){
        String fileName = getFileName(url);
        File f = new File(fileName);
        if (f.exists()) f.delete();
        mLruCache.remove(url);
    }

    public Bitmap getImage(String url) {
        Bitmap bmp = getBitmapFromLRUCache(url);
        if (bmp == null) {
            String fileName = getFileName(url);
            File f = new File(fileName);
            if (f.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bmp = BitmapFactory.decodeFile(fileName, options);
                addBitmapToLRUCache(url, bmp);
            }
        }
        return bmp;
    }

    public void clearImages() {
        for (File f : filePath.listFiles()) {
            f.delete();
        }
    }

    public String getFilecahedir() {
        return filePath.getAbsolutePath();
    }

}

