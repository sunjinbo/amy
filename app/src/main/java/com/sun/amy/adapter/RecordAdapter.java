package com.sun.amy.adapter;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.sun.amy.R;
import com.sun.amy.data.RecordItemData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * RecordAdapter class.
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder>
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    public interface SharedModeCallback {
        void onSharedModeUpdated();
    }

    private static final int MSG_PLAY_RECORD_STARTED = 0;
    private static final int MSG_PLAY_RECORD_COMPLETED = 2;

    private Activity mActivity;
    private List<RecordItemData> mData = new ArrayList<>();
    private boolean mIsSharedMode = false;
    private MediaPlayer mMediaPlayer;
    private SharedModeCallback mCallback;
    private Handler mHandler;

    public RecordAdapter(Activity activity, Handler handler, List<RecordItemData> data, SharedModeCallback callback) {
        mActivity = activity;
        mHandler = handler;
        mMediaPlayer = new MediaPlayer();
        mCallback = callback;
        for (RecordItemData itemData : data) {
            mData.add(itemData);
        }
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public void resetPlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
        }
    }

    public void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void recoverSharedMode() {
        mIsSharedMode = false;
        notifySharedModeUpdated();
        notifyDataSetChanged();
    }

    public boolean isSharedMode() {
        return mIsSharedMode;
    }

    public RecordItemData getSelectedRecord() {
        for (RecordItemData itemData : mData) {
            if (itemData.is_checked) {
                return itemData;
            }
        }
        return null;
    }

    @Override
    public RecordAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_record, parent, false);
        RecordAdapter.MyViewHolder holder = new RecordAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecordAdapter.MyViewHolder holder, int position) {
        final RecordItemData itemData = mData.get(position);
        holder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            resetPlayer();
            if (mIsSharedMode) {
                for (RecordItemData d : mData) {
                    if (d == itemData) {
                        d.is_checked = true;
                    } else {
                        d.is_checked = false;
                    }
                }

                notifyDataSetChanged();
            } else {
                try {
                    if (itemData.is_playing) {
                        notifyPlayingModeUpdated(null);
                    } else {
                        File file = new File(itemData.path);
                        if (file.exists()) {
                            mMediaPlayer.setDataSource(mActivity, Uri.parse(itemData.path));
                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mMediaPlayer.setLooping(false);
                            mMediaPlayer.setOnPreparedListener(RecordAdapter.this);
                            mMediaPlayer.setOnErrorListener(RecordAdapter.this);
                            mMediaPlayer.setOnCompletionListener(RecordAdapter.this);
                            mMediaPlayer.prepareAsync();

                            notifyPlayingModeUpdated(itemData);
                        } else {
                            Toast.makeText(mActivity, mActivity.getString(R.string.no_file_found), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            }
        });

        holder.mContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!mIsSharedMode) {
                    mIsSharedMode = true;
                    notifySharedModeUpdated();

                    resetPlayer();

                    for (RecordItemData d : mData) {
                        if (d == itemData) {
                            d.is_checked = true;
                        } else {
                            d.is_checked = false;
                        }
                        d.is_playing = false;
                    }

                    notifyDataSetChanged();
                }
                return false;
            }
        });

        holder.mTitleTextView.setText(itemData.title);
        if (itemData.is_playing) {
            holder.mTitleTextView.setTextColor(mActivity.getResources().getColor(R.color.indian_red));
        } else {
            holder.mTitleTextView.setTextColor(mActivity.getResources().getColor(R.color.black));
        }

        if (mIsSharedMode) {
            if (itemData.is_checked) {
                holder.mTypeImageView.setImageDrawable(mActivity.getDrawable(R.drawable.ic_radio_button_on));
            } else {
                holder.mTypeImageView.setImageDrawable(mActivity.getDrawable(R.drawable.ic_radio_button_off));
            }
        } else {
            switch (itemData.type) {
                case Word:
                    holder.mTypeImageView.setImageDrawable(mActivity.getDrawable(R.drawable.ic_words));
                    break;
                case Song:
                    holder.mTypeImageView.setImageDrawable(mActivity.getDrawable(R.drawable.ic_songs));
                    break;
                case Story:
                    holder.mTypeImageView.setImageDrawable(mActivity.getDrawable(R.drawable.ic_storys));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mHandler.sendEmptyMessage(MSG_PLAY_RECORD_STARTED);
        mMediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mHandler.sendEmptyMessage(MSG_PLAY_RECORD_COMPLETED);
        notifyPlayingModeUpdated(null);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        notifyPlayingModeUpdated(null);
        return false;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup mContent;
        private ImageView mTypeImageView;
        private TextView mTitleTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mContent = itemView.findViewById(R.id.ly_content);
            mTypeImageView = itemView.findViewById(R.id.iv_type);
            mTitleTextView = itemView.findViewById(R.id.tv_title);
        }
    }

    private void notifySharedModeUpdated() {
        if (mCallback != null) {
            mCallback.onSharedModeUpdated();
        }
    }

    private void notifyPlayingModeUpdated(RecordItemData itemData) {
        for (RecordItemData d : mData) {
            if (d == itemData) {
                d.is_playing = true;
            } else {
                d.is_playing = false;
            }
        }
        notifyDataSetChanged();
    }
}