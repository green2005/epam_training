package com.epamtraining.vklite.displayAttachments;

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
import com.epamtraining.vklite.imageLoader.ImageLoader;

public class AttachmentPhotoHelper extends AttachmentHelper{
    private ImageLoader mImageLoader;
    private TextView mDescription;
    private ResizableImageView mImageView;

    public AttachmentPhotoHelper(ImageLoader imageLoader){
        super();
        mImageLoader = imageLoader;
    }

    @Override
    public View getView(View convertView, Cursor cursor, LayoutInflater inflater) {
        if (cursor == null) {return convertView;}
        View v = convertView;
        if (v == null){
            v = inflater.inflate(R.layout.item_attachment_photo, null);
            mDescription = (TextView) v.findViewById(R.id.textview);
            mImageView = (ResizableImageView) v.findViewById(R.id.image);
        } else
        {
            v = convertView;
        }
        mDescription.setText(Html.fromHtml(CursorHelper.getString(cursor, AttachmentsDBHelper.TEXT)));
        Linkify.addLinks(mDescription, Linkify.ALL);

        mImageLoader.loadImage(mImageView, CursorHelper.getString(cursor, AttachmentsDBHelper.URL));
        return v;
    }
}
