package com.epamtraining.vklite.displayAttachments;


import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.ResizableImageView;
import com.epamtraining.vklite.db.AttachmentsDBHelper;
import com.epamtraining.vklite.imageLoader.ImageLoader;

import java.io.IOException;

public class AttachmentAudioHelper extends AttachmentHelper implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    private enum PlayStates {PLAYING, PAUSED, STOPPED}

    ;

    private ImageButton mPlayBtn;
    private ImageButton mStopBtn;
    private SeekBar mProgress;
    private TextView mTitle;
    private String mUrl;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private PlayStates mState;
    private Handler mHandler;
    private int mMediaLength;

    public AttachmentAudioHelper(Context context) {
        super();
        mContext = context;
    }

    @Override
    public View getView(View convertView, Cursor cursor, LayoutInflater inflater) {
        if (cursor == null) {
            return convertView;
        }
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.item_attachment_audio, null);
            mTitle = (TextView) v.findViewById(R.id.tv_title);
            mProgress = (SeekBar) v.findViewById(R.id.progress);
            mPlayBtn = (ImageButton) v.findViewById(R.id.playbtn);
            mStopBtn = (ImageButton) v.findViewById(R.id.stopbtn);
            mProgress.setMax(99);
            mHandler = new Handler();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mState = PlayStates.STOPPED;
            mStopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if (mMediaPlayer.isPlaying()){
                       mMediaPlayer.stop();
                       mState = PlayStates.STOPPED;
                       mPlayBtn.setImageResource(R.drawable.ic_play);

                   }
                }
            });

            mPlayBtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            switch (mState) {
                                case PAUSED: {
                                    mMediaPlayer.start();
                                    mState = PlayStates.PLAYING;
                                    mPlayBtn.setImageResource(R.drawable.ic_pause);
                                    seekBarNotification();
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
                                        mMediaLength = mMediaPlayer.getDuration();
                                        mMediaPlayer.start();
                                        mState = PlayStates.PLAYING;
                                        mPlayBtn.setImageResource(R.drawable.ic_pause);
                                        seekBarNotification();
                                    }
                                    break;
                                }
                            }
                        }
                    }

            );

        } else {
            v = convertView;
        }
        mTitle.setText(Html.fromHtml(CursorHelper.getString(cursor, AttachmentsDBHelper.TITLE)));
        mUrl = CursorHelper.getString(cursor, AttachmentsDBHelper.URL);

        return v;
    }

    private void seekBarNotification() {
        mProgress.setProgress((int) (((float) mMediaPlayer.getCurrentPosition() / mMediaLength) * 100));
        if (mMediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    seekBarNotification();
                }
            };
            mHandler.postDelayed(notification, 1000);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mProgress.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayBtn.setImageResource(R.drawable.ic_play);
    }
}
