package com.epamtraining.vklite.processors;

import com.epamtraining.vklite.os.VKExecutor;

import java.io.InputStream;

public  interface    Processor  {
    public static final String API_KEY="5.26";

    public  String getUrl();
    public  void process (InputStream stream)  throws Exception;
}
