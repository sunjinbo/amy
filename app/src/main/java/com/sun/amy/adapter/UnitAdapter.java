package com.sun.amy.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.sun.amy.R;
import com.sun.amy.activity.DictActivity;
import com.sun.amy.data.UnitWrapper;
import com.sun.amy.data.StudyType;
import com.sun.amy.data.UnitItemData;
import com.sun.amy.data.WordWrapper;

import java.util.List;

/**
 * UnitAdapter class.
 */
public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.MyViewHolder>  {
    private Activity mActivity;
    private List<UnitItemData> mData;
    private VideoView mVideoView;
    private UnitWrapper mWrapper;

    public UnitAdapter(Activity activity, VideoView videoView, List<UnitItemData> data, UnitWrapper wrapper) {
        mActivity = activity;
        mData = data;
        mVideoView = videoView;
        mWrapper = wrapper;
    }

    public void updateData(List<UnitItemData> data) {
        mData.clear();
        for (UnitItemData itemData : data) {
            mData.add(itemData);
        }
        notifyDataSetChanged();
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
                if (itemData.type == StudyType.Word) {
                    Intent intent = new Intent(mActivity, DictActivity.class);
                    intent.putExtra("unit_name", mWrapper.unitName);
                    intent.putExtra("unit_words", new WordWrapper(mWrapper.unit.getPath(), mWrapper.unitBean.words));
                    mActivity.startActivity(intent);
                } else {
                    Uri uri = Uri.parse(itemData.path);
                    mVideoView.setVideoURI(uri);
                    mVideoView.start();
                    mVideoView.requestFocus();
                }
            }
        });
        holder.mTitleTextView.setText(itemData.title);
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

    @Override
    public int getItemCount() {
        return mData.size();
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
