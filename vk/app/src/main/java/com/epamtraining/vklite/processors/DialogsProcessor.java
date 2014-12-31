package com.epamtraining.vklite.processors;


import android.content.Context;

import java.io.InputStream;

public class DialogsProcessor extends Processor{
    public DialogsProcessor(Context context) {
        super(context);
    }

    @Override
    public void process(InputStream stream) throws Exception {

    }

    @Override
    public int getRecordsFetched() {
        return 0;
    }
}
