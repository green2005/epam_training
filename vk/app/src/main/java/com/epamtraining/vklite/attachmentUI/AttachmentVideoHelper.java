package com.epamtraining.vklite.attachmentui;


import android.database.Cursor;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.ResizableImageView;
import com.epamtraining.vklite.db.AttachmentsDBHelper;
import com.epamtraining.vklite.imageloader.ImageLoader;

public class AttachmentVideoHelper implements AttachmentHelper {
    private ImageLoader mImageLoader;

    AttachmentVideoHelper(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    @Override
    public View getView(Cursor cursor, LayoutInflater inflater) {
        if (cursor == null) {
            return null;
        }
        View v = inflater.inflate(R.layout.item_attachment_video, null);
        TextView titleView = (TextView) v.findViewById(R.id.titleview);
        ResizableImageView imageView = (ResizableImageView) v.findViewById(R.id.image);
        titleView.setText(Html.fromHtml(CursorHelper.getString(cursor, AttachmentsDBHelper.TITLE)));
        Linkify.addLinks(titleView, Linkify.ALL);
        String imageUrl = CursorHelper.getString(cursor, AttachmentsDBHelper.PHOTO);
        mImageLoader.loadImage(imageView, imageUrl);
        return v;
    }
}
