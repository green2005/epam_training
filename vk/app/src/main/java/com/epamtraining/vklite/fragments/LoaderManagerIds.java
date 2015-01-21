package com.epamtraining.vklite.fragments;

public enum LoaderManagerIds {
    //TODO 0,1,2, change to ordinal()?
    DIALOGS(0), FRIENDS(1), MESSAGES(2), NEWS(3), WALL(4), COMMENTS(5);

    private int mId;
    LoaderManagerIds(int id){
        mId = id;
    }

    public int getId(){
        return mId;
    }
}
