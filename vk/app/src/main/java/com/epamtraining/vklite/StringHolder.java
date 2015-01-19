package com.epamtraining.vklite;

public class StringHolder implements VKLocalService {
    private String mText;
    public StringHolder(String text){
        mText = text;
    }

    public String getString(){
        return mText;
    }
}
