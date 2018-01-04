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
import android.widget.VideoView;

import com.sun.amy.R;
import com.sun.amy.data.RecordItemData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * RecordAdapter class.
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder>
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private Activity mActivity;
    private List<RecordItemData> mData = new ArrayList<>();
    private boolean mIsSharedModel = false;
    private MediaPlayer mMediaPlayer;

    public RecordAdapter(Activity activity, List<RecordItemData> data) {
        mActivity = activity;
        mMediaPlayer = new MediaPlayer();
        for (RecordItemData itemData : data) {
            mData.add(itemData);
        }
    }

    public void updateData(boolean isSharedModel, List<RecordItemData> data) {
        mData.clear();
        mIsSharedModel = isSharedModel;
        for (RecordItemData itemData : data) {
            mData.add(itemData);
        }
        notifyDataSetChanged();
    }

    public void resetPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
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
            if (mIsSharedModel) {
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
                    File file = new File(itemData.path);
                    if (file.exists()) {
                        mMediaPlayer.setDataSource(mActivity, Uri.parse(itemData.path));
                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mMediaPlayer.setLooping(false);
                        mMediaPlayer.setOnPreparedListener(RecordAdapter.this);
                        mMediaPlayer.setOnErrorListener(RecordAdapter.this);
                        mMediaPlayer.prepareAsync();
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
                if (!mIsSharedModel) {
                    mIsSharedModel = true;

                    for (RecordItemData d : mData) {
                        d.is_checked = false;
                    }

                    notifyDataSetChanged();
                }
                return false;
            }
        });

        holder.mTitleTextView.setText(itemData.title);

        if (mIsSharedModel) {
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
        mMediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
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
}