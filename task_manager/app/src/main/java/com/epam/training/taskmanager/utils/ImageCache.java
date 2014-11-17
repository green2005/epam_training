package com.epam.training.taskmanager.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageCache {
    private Context mContext;
    private File filePath;

    public ImageCache(Context context){
        mContext = context;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            filePath = new File(android.os.Environment.getExternalStorageDirectory(),mContext.getPackageName());
        else
            filePath = context.getCacheDir();
        if (!filePath.exists()){
            filePath.mkdirs();
        }
    }

    public Bitmap getImage(String url){
        Bitmap bmp = null;
        String fileName =filePath.getAbsolutePath() +File.separator +url.hashCode() + "";
        File f = new File(fileName);
        if (f.exists()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bmp = BitmapFactory.decodeFile(fileName, options);
        }
        return bmp;
    }

    public void clearImages(){
        for (File f :filePath.listFiles()){
            f.delete();
        }
    }

    public String getFilecahedir(){
        return filePath.getAbsolutePath();
    }

}
