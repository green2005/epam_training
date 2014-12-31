package com.epamtraining.vklite.adapters;


import android.content.Context;
import android.database.Cursor;

public class DialogsAdapter extends BoItemAdapter{

    public DialogsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }
}
