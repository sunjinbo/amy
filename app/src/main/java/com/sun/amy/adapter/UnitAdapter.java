package com.sun.amy.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.amy.R;
import com.sun.amy.data.UnitWrapper;
import com.sun.amy.data.StudyType;
import com.sun.amy.data.UnitItemData;
import com.sun.amy.data.WordWrapper;
import com.sun.amy.views.MediaView;
import com.sun.amy.views.WordView;

import java.util.List;

/**
 * UnitAdapter class.
 */
public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.MyViewHolder>  {
    private Activity mActivity;
    private List<UnitItemData> mData;
    private MediaView mVideoView;
    private WordView mWordView;
    private Button mHomeworkView;
    private UnitWrapper mWrapper;

    public UnitAdapter(Activity activity, MediaView videoView, WordView wordView, Button homeworkView, List<UnitItemData> data, UnitWrapper wrapper) {
        mActivity = activity;
        mData = data;
        mVideoView = videoView;
        mWordView = wordView;
        mHomeworkView = homeworkView;
        mWrapper = wrapper;
    }

    @Override
    public UnitAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_unit, parent, false);
        UnitAdapter.MyViewHolder holder = new UnitAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(UnitAdapter.MyViewHolder holder, int position) {
        final UnitItemData itemData = mData.get(position);
        holder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < mData.size(); ++i) {
                    mData.get(i).selected = false;
                }

                itemData.selected = true;

                mHomeworkView.setEnabled(true);
                mHomeworkView.setTextColor(mActivity.getResources().getColor(R.color.indian_red));

                mVideoView.stop();

                if (itemData.type == StudyType.Word) {
                    WordWrapper wrapper;
                    if (TextUtils.equals(mActivity.getString(R.string.key_words), itemData.title)) {
                        wrapper = new WordWrapper(mWrapper.unit.getPath(), mWrapper.unitBean.key_words);
                    } else {
                        wrapper = new WordWrapper(mWrapper.unit.getPath(), mWrapper.unitBean.sup_words);
                    }
                    mWordView.setWords(wrapper);

                    mWordView.setVisibility(View.VISIBLE);
                    mVideoView.setVisibility(View.GONE);
                } else {
                    if (itemData.type == StudyType.Vocabulary) {
                        mVideoView.setVideoURI(Uri.parse(itemData.path), itemData.title, true);
                    } else {
                        mVideoView.setVideoURI(Uri.parse(itemData.path), itemData.title, false);
                    }

                    mVideoView.start();

                    mWordView.setVisibility(View.GONE);
                    mVideoView.setVisibility(View.VISIBLE);
                }

                notifyDataSetChanged();
            }
        });

        holder.mTitleTextView.setText(itemData.title);

        if (itemData.selected) {
            holder.mTitleTextView.setTextColor(mActivity.getResources().getColor(R.color.indian_red));
        } else {
            holder.mTitleTextView.setTextColor(mActivity.getResources().getColor(R.color.black));
        }

        switch (itemData.type) {
            case Vocabulary:
                holder.mTypeImageView.setImageDrawable(mActivity.getDrawable(R.drawable.ic_vocabulary));
                break;
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

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public UnitItemData getSelectedItem() {
        for (int i = 0; i < mData.size(); ++i) {
            if (mData.get(i).selected) {
                return mData.get(i);
            }
        }
        return null;
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
