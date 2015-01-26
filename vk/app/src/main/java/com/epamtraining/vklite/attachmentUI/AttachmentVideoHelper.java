package com.epamtraining.vklite.attachmentui;


import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.ResizableImageView;
import com.epamtraining.vklite.db.AttachmentsDBHelper;
import com.epamtraining.vklite.imageloader.ImageLoader;

public class AttachmentVideoHelper implements AttachmentHelper {
    private TextView mTitleView;
    private VideoView mVideoView;
    private ImageLoader mImageLoader;
    private ResizableImageView mImageView;
    private Context mContext;

    AttachmentVideoHelper(Context context, ImageLoader imageLoader) {
        mContext = context;
        mImageLoader = imageLoader;
    }

    @Override
    public View getView(Cursor cursor, LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.item_attachment_video, null);
        mTitleView = (TextView) v.findViewById(R.id.titleview);
        mVideoView = (VideoView) v.findViewById(R.id.videoview);
        mImageView = (ResizableImageView) v.findViewById(R.id.image);
        mTitleView.setText(Html.fromHtml(CursorHelper.getString(cursor, AttachmentsDBHelper.TITLE)));
        Linkify.addLinks(mTitleView, Linkify.ALL);
        String imageUrl = CursorHelper.getString(cursor, AttachmentsDBHelper.PHOTO);
        mImageLoader.loadImage(mImageView, imageUrl);
        /*
        String url = CursorHelper.getString(cursor, AttachmentsDBHelper.URL);
        //url = "https://www.youtube.com/watch?v=MfdSBO1IPYo";
        if (!TextUtils.isEmpty(url)) {
            MediaController mediacontroller = new MediaController(
                    mContext);
            //mediacontroller.setAnchorView(mUrl);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(url);
            mVideoView.setMediaController(mediacontroller);
           // mVideoView.setVideoURI(video);
           // mVideoView.start();
*/


        return v;
    }
}
