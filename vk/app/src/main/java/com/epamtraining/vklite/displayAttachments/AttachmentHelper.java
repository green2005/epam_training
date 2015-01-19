package com.epamtraining.vklite.displayAttachments;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;

//TODO AdapterAttachmentHelper
public abstract class AttachmentHelper {
    public abstract View getView(View convertView, Cursor cursor, LayoutInflater inflater);
}
