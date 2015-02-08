package com.epamtraining.vklite.attachmentui;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.ResizableImageView;
import com.epamtraining.vklite.db.AttachmentsDBHelper;
import com.epamtraining.vklite.loader.ImageLoader;
import com.epamtraining.vklite.processors.Processor;
import com.epamtraining.vklite.processors.VideoProcessor;

public class AttachmentVideoHelper implements AttachmentHelper, View.OnClickListener {
    private ImageLoader mImageLoader;
    private String mVideoId;
    private Context mContext;

    ResizableImageView mImageView;
    ImageView mPlayImageView;

    AttachmentVideoHelper(ImageLoader imageLoader, Context context) {
        mImageLoader = imageLoader;
        mContext = context;
    }

    @Override
    public View getView(Cursor cursor, LayoutInflater inflater) {
        if (cursor == null) {
            return null;
        }
        View v = inflater.inflate(R.layout.item_attachment_video, null);
        TextView titleView = (TextView) v.findViewById(R.id.titleview);
        mImageView = (ResizableImageView) v.findViewById(R.id.image);
        mPlayImageView = (ImageView) v.findViewById(R.id.playImage);
        mPlayImageView.setOnClickListener(this);
        mImageView.setOnClickListener(this);
        titleView.setText(Html.fromHtml(CursorHelper.getString(cursor, AttachmentsDBHelper.TITLE)));
        Linkify.addLinks(titleView, Linkify.ALL);
        String imageUrl = CursorHelper.getString(cursor, AttachmentsDBHelper.PHOTO);
        mVideoId = String.format("%s_%s", CursorHelper.getString(cursor, AttachmentsDBHelper.OWNER_ID),
                CursorHelper.getString(cursor, AttachmentsDBHelper.ATTACHMENT_ID)
        );
        mImageLoader.loadImage(mImageView, imageUrl);
        return v;
    }


    private void showVideo(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mContext.startActivity(i);
    }

    public void playClick(View v) {
        Processor processor = new VideoProcessor(mContext);
        DataSource ds = new DataSource(processor, new DataSource.DataSourceCallbacksResult() {
            @Override
            public void onResult(Bundle result) {
                if (result != null && result.containsKey(VideoProcessor.PLAYER)) {
                    String player = result.getString(VideoProcessor.PLAYER);
                    showVideo(player);
                }
            }

            @Override
            public void onError(Exception e) {
                ErrorHelper.showError(mContext, e);
            }

            @Override
            public void onBeforeStart() {

            }

            @Override
            public void onLoadEnd(int recordsFetched) {

            }
        });
        ds.fillData(Api.getVideoUrl(mContext, mVideoId), mContext);
    }

    @Override
    public void onClick(View v) {
        playClick(v);
    }
}
