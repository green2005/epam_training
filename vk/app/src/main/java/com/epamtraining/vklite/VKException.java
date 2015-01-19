package com.epamtraining.vklite;

public class VKException extends Exception {
    public VKException(){
        super();
    }

    public VKException(String message){
        super(message);
    }

    public VKException(Throwable cause){
        super(cause);
    }

    public VKException(String message, Throwable cause){
        super(message, cause);
    }
}
