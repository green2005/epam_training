package com.epamtraining.vklite.attachmentui;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;

public interface AttachmentHelper {
    public View getView( Cursor cursor, LayoutInflater inflater);
}
