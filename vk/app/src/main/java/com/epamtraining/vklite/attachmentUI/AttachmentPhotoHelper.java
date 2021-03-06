package com.epamtraining.vklite.attachmentui;

import android.database.Cursor;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.ResizableImageView;
import com.epamtraining.vklite.db.AttachmentsDBHelper;
import com.epamtraining.vklite.loader.ImageLoader;

public class AttachmentPhotoHelper implements AttachmentHelper {
    private ImageLoader mImageLoader;

    public AttachmentPhotoHelper(ImageLoader imageLoader) {
        super();
        mImageLoader = imageLoader;
    }

    @Override
    public View getView(Cursor cursor, LayoutInflater inflater) {
        if (cursor == null) {
            return null;
        }
        View v = inflater.inflate(R.layout.item_attachment_photo, null);
        TextView description = (TextView) v.findViewById(R.id.textview);
        ImageView imageView = (ResizableImageView) v.findViewById(R.id.image);
        description.setText(Html.fromHtml(CursorHelper.getString(cursor, AttachmentsDBHelper.TEXT)));
        Linkify.addLinks(description, Linkify.ALL);
        mImageLoader.loadImage(imageView, CursorHelper.getString(cursor, AttachmentsDBHelper.URL));
        return v;
    }
}
