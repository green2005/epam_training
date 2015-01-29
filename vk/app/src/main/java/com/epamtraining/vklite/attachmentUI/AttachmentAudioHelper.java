package com.epamtraining.vklite.attachmentui;


import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.db.AttachmentsDBHelper;

import java.io.IOException;

public class AttachmentAudioHelper implements MediaPlayer.OnCompletionListener,
        AttachmentHelper {
    private enum PlayStates {PLAYING, PAUSED, STOPPED}

    private ImageButton mPlayBtn;
    private String mUrl;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private PlayStates mState;

    public AttachmentAudioHelper(Context context) {
        super();
        mContext = context;
    }

    @Override
    public View getView(Cursor cursor, LayoutInflater inflater) {
        if (cursor == null) {
            return null;
        }
        View v = inflater.inflate(R.layout.item_attachment_audio, null);
        TextView title = (TextView) v.findViewById(R.id.tv_title);
        mPlayBtn = (ImageButton) v.findViewById(R.id.playbtn);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mState = PlayStates.STOPPED;
        mPlayBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (mState) {
                            case PAUSED: {
                                mMediaPlayer.start();
                                mState = PlayStates.PLAYING;
                                mPlayBtn.setImageResource(R.drawable.ic_pause);
                                break;
                            }
                            case PLAYING: {
                                mMediaPlayer.pause();
                                mState = PlayStates.PAUSED;
                                mPlayBtn.setImageResource(R.drawable.ic_play);
                                break;
                            }
                            case STOPPED: {
                                if (!TextUtils.isEmpty(mUrl)) {
                                    Uri uriSource = Uri.parse(mUrl);
                                    try {
                                        mMediaPlayer.reset();
                                        mMediaPlayer.setDataSource(mContext, uriSource);
                                        mMediaPlayer.prepare();
                                    } catch (IOException e) {
                                        ErrorHelper.showError(mContext, e);
                                    }
                                    mMediaPlayer.start();
                                    mState = PlayStates.PLAYING;
                                    mPlayBtn.setImageResource(R.drawable.ic_pause);
                                }
                                break;
                            }
                        }
                    }
                }
        );
        title.setText(Html.fromHtml(CursorHelper.getString(cursor, AttachmentsDBHelper.TITLE)));
        mUrl = CursorHelper.getString(cursor, AttachmentsDBHelper.URL);
        return v;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayBtn.setImageResource(R.drawable.ic_play);
    }
}
