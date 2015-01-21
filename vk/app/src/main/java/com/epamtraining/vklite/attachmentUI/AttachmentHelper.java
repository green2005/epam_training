package com.epamtraining.vklite.attachmentUI;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;

public interface AttachmentHelper {
    public View getView( Cursor cursor, LayoutInflater inflater);
}
