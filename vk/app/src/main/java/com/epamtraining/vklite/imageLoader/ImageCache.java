package com.epamtraining.vklite.imageLoader;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageCache {
    private Context mContext;
    private File filePath;
    private LruCache<String, Bitmap> mLruCache;
    private Object mLruObject = new Object();
    private Object mFileObject = new Object();

    public ImageCache(Context context) {
        mContext = context;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            filePath = context.getExternalFilesDir(null);  /*Returns absolute paths to application-specific directories on all
                                                            * external storage devices where the application can place persistent files
                                                            * it owns. These files are internal to the application, and not typically
                                                            * visible to the user as media.*/
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
        synchronized (mLruObject) {
            if (mLruCache.get(url) == null) {
                mLruCache.put(url, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromLRUCache(String url) {
        synchronized (mLruObject) {
            return mLruCache.get(url);
        }
    }

    public String getFileName(String imageUrl) {
        return filePath.getAbsolutePath() + File.separator + getMD5(imageUrl);
    }

    public void removeImage(String url) {
        String fileName = getFileName(url);
        File f = new File(fileName);
        synchronized (mFileObject) {
            if (f.exists()) f.delete();
        }
        synchronized (mLruObject) {
            mLruCache.remove(url);
        }
    }

    public Bitmap getImage(String url) {
        Bitmap bmp;
        synchronized (mLruObject) {
            bmp = getBitmapFromLRUCache(url);
        }
        if (bmp == null) {
            String fileName = getFileName(url);
            synchronized (mFileObject) {
                File f = new File(fileName);
                if (f.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bmp = BitmapFactory.decodeFile(fileName, options);
                    addBitmapToLRUCache(url, bmp);
                }
            }
        }
        return bmp;
    }

    public void putImage(String imageUrl, InputStream is) throws Exception {
        if (is == null) {
            throw new Exception("InputStream is null");
        }
        FileOutputStream fOut = null;
        try {
            String mFileName = getFileName(imageUrl);
            synchronized (mFileObject) {
                File f = new File(mFileName);
                f.createNewFile();
                fOut = new FileOutputStream(f);
                copyStreams(is, fOut);
            }
        } finally {
            is.close();
            if (fOut != null) {
                fOut.flush();
                fOut.close();
            }
        }
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

    public void clearImages() {
        for (File f : filePath.listFiles()) {
            f.delete();
        }
    }

    private String getMD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public String getFilecahedir() {
        return filePath.getAbsolutePath();
    }

}

