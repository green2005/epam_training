package com.epam.training.image;


import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageDisplayer implements Displayer <Bitmap, ImageView>{

    @Override
    public void displayResult(Bitmap bitmap, ImageView imageView) {
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public String getUrl(ImageView imageView) {
        return (String)imageView.getTag();
    }

    @Override
    public void setUrl(String url, ImageView imageView) {
        imageView.setTag(url);
    }

    @Override
    public int getSize(Bitmap bitmap) {
        return bitmap.getByteCount();
    }
}
