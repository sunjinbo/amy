package com.sun.amy.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private UnitWrapper mWrapper;

    public UnitAdapter(Activity activity, MediaView videoView, WordView wordView, List<UnitItemData> data, UnitWrapper wrapper) {
        mActivity = activity;
        mData = data;
        mVideoView = videoView;
        mWordView = wordView;
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
                    mVideoView.setVideoURI(Uri.parse(itemData.path));
                    mVideoView.start();

                    mWordView.setVisibility(View.GONE);
                    mVideoView.setVisibility(View.VISIBLE);
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
