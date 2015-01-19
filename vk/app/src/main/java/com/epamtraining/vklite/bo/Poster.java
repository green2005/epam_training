package com.epamtraining.vklite.bo;


public class Poster {
    private String mName;
    private String mImageUrl;

    public Poster(String name, String imageUrl) {
        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
}
