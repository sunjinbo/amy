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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;

import com.sun.amy.R;
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
    private ImageView mControlImageView;
    private ProgressBar mProgressBar;
    private TextView mCurrentPositionTextView;
    private TextView mTotalDurationTextView;
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
        if (mControlImageView.getVisibility() == INVISIBLE && mIsPrepared) {
            mControlImageView.setVisibility(VISIBLE);
        }
        return super.onTouchEvent(e);
    }

    @Override
    public void onClick(View view) {
        if (mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
            } else {
                mMediaPlayer.start();
                mControlImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mMediaPlayer.setDisplay(mSurfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mMediaPlayer.setDisplay(null);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

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
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public void setVideoURI(Uri uri) {
        try {
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
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
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
            mIsPrepared = false;
        }
    }

    private void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.media_view, this, false);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mRootView.setLayoutParams(params);
        addView(mRootView);

        mSurfaceView = mRootView.findViewById(R.id.surface_view);
        mControlImageView = mRootView.findViewById(R.id.iv_control);
        mProgressBar = mRootView.findViewById(R.id.progressbar);
        mCurrentPositionTextView = mRootView.findViewById(R.id.tv_position);
        mTotalDurationTextView = mRootView.findViewById(R.id.tv_duration);

        mControlImageView.setOnClickListener(this);

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
            mDisplayTime += 50;
            if (mDisplayTime > 2000 && mControlImageView.getVisibility() == VISIBLE) {
                mControlImageView.setVisibility(INVISIBLE);
            }

            int currentPosition = (int)((float)mMediaPlayer.getCurrentPosition() / 1000.0f);
            int duration = (int)((float)mMediaPlayer.getDuration() / 1000.0f);
            if (currentPosition >= 0 && duration >= 0l) {
                int progress = getCurrentProgress(currentPosition, duration);
                mProgressBar.setProgress(progress);
                mCurrentPositionTextView.setText(TimeUtils.formatNumberToHourMinuteSecond((double)currentPosition));
                mTotalDurationTextView.setText(TimeUtils.formatNumberToHourMinuteSecond((double)duration));
            }

            mDisplayHandler.sendEmptyMessageDelayed(0, 50);
            return false;
        }
    });
}