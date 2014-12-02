package com.epamtraining.vklite.imageLoader;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class ImageCache {
    private Context mContext;
    private File filePath;

    public ImageCache(Context context) {
        mContext = context;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            filePath =  context.getExternalFilesDir(null);
        else
            filePath = context.getCacheDir();
        if (!filePath.exists())
            filePath.mkdirs();
    }

    public String getFileName(String imageUrl){
        return filePath.getAbsolutePath() + File.separator + imageUrl.hashCode() + "";
    }

    public Bitmap getImage(String url) {
        Bitmap bmp = null;
        String fileName = getFileName(url);
        File f = new File(fileName);
        if (f.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp = BitmapFactory.decodeFile(fileName, options);
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

