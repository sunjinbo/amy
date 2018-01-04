package com.sun.amy.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sun.amy.R;
import com.sun.amy.activity.ReviewActivity;
import com.sun.amy.data.LessonItemData;

import java.util.List;

/**
 * LessonsAdapter class.
 */
public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.MyViewHolder>  {

    private Activity mActivity;
    private List<LessonItemData> mData;

    public LessonsAdapter(Activity activity, List<LessonItemData> data) {
        mActivity = activity;
        mData = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_lesson, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final LessonItemData itemData = mData.get(position);
        holder.mContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ReviewActivity.class);
                intent.putExtra("unit_name", itemData.title);
                intent.putExtra("unit_directory", itemData.path);
                mActivity.startActivity(intent);
            }
        });
        holder.mTitleTextView.setText(itemData.title);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup mContent;
        private TextView mTitleTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mContent = itemView.findViewById(R.id.ly_content);
            mTitleTextView = itemView.findViewById(R.id.tv_title);
        }
    }
}
