package com.epam.training.taskmanager.processing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.epam.training.taskmanager.source.HttpDataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by IstiN on 14.11.2014.
 */
public class BitmapProcessor implements Processor<Bitmap, InputStream> {
    String mImageFilePath;
    String mUrl;
    ImageView mImageView;

    public BitmapProcessor(String imageFilePath, String url, ImageView imageView) {
        mImageFilePath = imageFilePath;
        mUrl = url;
        mImageView = imageView;
    }

    @Override
    public Bitmap process(InputStream inputStream) throws Exception {
        try {
               if (!mUrl.equals(mImageView.getTag())) {
                    return null;
                }
            Bitmap bmp = null;
            if (!TextUtils.isEmpty(mImageFilePath)) {
                String fileName = mImageFilePath + File.separator + mUrl.hashCode();
                FileOutputStream fOut = new FileOutputStream(fileName);
                try {
                    copyStream(inputStream, fOut);
                } finally {
                    fOut.flush();
                    fOut.close();
                }
                FileInputStream fIs = new FileInputStream(fileName);
                try {
                    bmp = BitmapFactory.decodeStream(fIs);
                } finally {
                    fIs.close();
                }
            } else {
                bmp = BitmapFactory.decodeStream(inputStream);
            }
            return bmp;
        } finally {
            HttpDataSource.close(inputStream);
        }
    }

    private static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

}
