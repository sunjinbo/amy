package com.sun.amy.views;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;

import com.sun.amy.R;
import com.sun.amy.configs.PrefKeys;
import com.sun.amy.configs.PrefSettings;
import com.sun.amy.utils.TimeUtils;

import java.io.IOException;

/**
 * MediaView class.
 */
public class MediaView extends FrameLayout implements
        View.OnClickListener,
        SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener {

    private View mRootView;
    private SurfaceView mSurfaceView;
    private ViewGroup mControlViewGroup;
    private ImageView mPlayImageView;
    private ImageView mMuteImageView;
    private ProgressBar mProgressBar;
    private TextView mCurrentPositionTextView;
    private TextView mTotalDurationTextView;

    private FrameLayout mCoverLayout;
    private TextView mCoverTitleTextView;

    private MediaPlayer mMediaPlayer;
    private SurfaceHolder mSurfaceHolder;

    private int mDisplayTime = 0;
    private boolean mIsPrepared = false;

    public MediaView(@NonNull Context context) {
        super(context);
        initView();
    }

    public MediaView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mDisplayTime = 0;

        if (mControlViewGroup.getVisibility() == INVISIBLE && mIsPrepared) {
            mControlViewGroup.setVisibility(VISIBLE);
        }

        return super.onTouchEvent(e);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_play) {
            if (mIsPrepared) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    mPlayImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                } else {
                    mMediaPlayer.start();
                    mPlayImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                }
            }
        } else if (view.getId() == R.id.iv_mute) {
            if (mIsPrepared) {
                if (isMute()) {
                    if (setMute(false)) {
                        mMuteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_unmute));
                        mMediaPlayer.setVolume(1f, 1f);
                    }
                } else {
                    if (setMute(true)) {
                        mMuteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_mute));
                        mMediaPlayer.setVolume(0f, 0f);
                    }
                }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mMediaPlayer.setDisplay(mSurfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // no need to implementation required
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mMediaPlayer.setDisplay(null);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // no need to implementation required
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mIsPrepared = false;
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mIsPrepared = true;
        mMediaPlayer.start();
        if (isMute()) {
            mMediaPlayer.setVolume(0f, 0f);
        } else {
            mMediaPlayer.setVolume(1f, 1f);
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public boolean setMute(boolean mute) {
        boolean success = true;
        try {
            PrefSettings.getSettings(getContext()).setValue(PrefKeys.KEY_MUTE, Boolean.toString(mute));
            PrefSettings.getSettings(getContext()).save();
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    public boolean isMute() {
        boolean mute = false;

        try {
            if (PrefSettings.getSettings(getContext()).containsKey(PrefKeys.KEY_MUTE)) {
                String keyValue = PrefSettings.getSettings(getContext()).getValue(PrefKeys.KEY_MUTE);
                mute = Boolean.parseBoolean(keyValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mute;
    }

    public void setVideoURI(Uri uri, String name, boolean isAudio) {
        try {
            if (isAudio) {
                mCoverTitleTextView.setText(name);
                mCoverLayout.setVisibility(VISIBLE);
            } else {
                mCoverTitleTextView.setText("");
                mCoverLayout.setVisibility(INVISIBLE);
            }

            if (mMediaPlayer != null) {
                mMediaPlayer.setDataSource(getContext(), uri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                mMediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mMediaPlayer != null && mIsPrepared && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void resume() {
        if (mMediaPlayer != null && mIsPrepared && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mIsPrepared = false;
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mIsPrepared = false;
        }

        if (mDisplayHandler != null) {
            mDisplayHandler.removeMessages(0);
        }
    }

    private void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.media_view, this, false);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mRootView.setLayoutParams(params);
        addView(mRootView);

        mSurfaceView = mRootView.findViewById(R.id.surface_view);
        mControlViewGroup = mRootView.findViewById(R.id.ly_control);
        mPlayImageView = mRootView.findViewById(R.id.iv_play);
        mMuteImageView = mRootView.findViewById(R.id.iv_mute);
        mProgressBar = mRootView.findViewById(R.id.progressbar);
        mCurrentPositionTextView = mRootView.findViewById(R.id.tv_position);
        mTotalDurationTextView = mRootView.findViewById(R.id.tv_duration);

        mCoverLayout = mRootView.findViewById(R.id.ly_cover);
        mCoverTitleTextView = mRootView.findViewById(R.id.tv_cover_title);

        if (isMute()) {
            mMuteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_mute));
        } else {
            mMuteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_unmute));
        }

        mPlayImageView.setOnClickListener(this);
        mMuteImageView.setOnClickListener(this);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);

        mDisplayHandler.sendEmptyMessageDelayed(0, 333);
    }

    private int getCurrentProgress(float currentTime, float totalTime) {
        float totalProcess = 100;
        return  (int) ( currentTime / totalTime * totalProcess);
    }

    private Handler mDisplayHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            try
            {
                if (mMediaPlayer != null) {

                    mDisplayTime += 50;
                    if (mDisplayTime > 2000) {
                        if (mControlViewGroup.getVisibility() == VISIBLE) {
                            mControlViewGroup.setVisibility(INVISIBLE);
                        }
                    }

                    if (mIsPrepared) {
                        int currentPosition = (int)((float)mMediaPlayer.getCurrentPosition() / 1000.0f);
                        int duration = (int)((float)mMediaPlayer.getDuration() / 1000.0f);
                        if (currentPosition >= 0 && duration >= 0l) {
                            int progress = getCurrentProgress(currentPosition, duration);
                            mProgressBar.setProgress(progress);
                            mCurrentPositionTextView.setText(TimeUtils.formatNumberToHourMinuteSecond((double)currentPosition));
                            mTotalDurationTextView.setText(TimeUtils.formatNumberToHourMinuteSecond((double)duration));
                        }
                    }

                    mDisplayHandler.sendEmptyMessageDelayed(0, 50);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    });
}
