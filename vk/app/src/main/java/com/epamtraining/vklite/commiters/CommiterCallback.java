package com.epamtraining.vklite.commiters;

public interface CommiterCallback{
    public void onAfterExecute();
    public void onException(Exception e);
}
