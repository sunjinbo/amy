package com.sun.amy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import com.sun.amy.R;
import com.sun.amy.adapter.UnitAdapter;
import com.sun.amy.data.UnitBean;
import com.sun.amy.data.UnitWrapper;
import com.sun.amy.data.SongBean;
import com.sun.amy.data.StoryBean;
import com.sun.amy.data.StudyType;
import com.sun.amy.data.UnitItemData;
import com.sun.amy.views.MediaView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class ReviewActivity extends Activity {

    private RecyclerView mRecyclerView;
    private UnitAdapter mAdapter;
    private UnitBean mUnitBean;
    private MediaView mVideoView;
    private String mUnitName;
    private String mUnitDirectory;
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "amy");

        Intent intent = getIntent();
        if (intent.hasExtra("unit_name")) {
            mUnitName = intent.getStringExtra("unit_name");
        }

        if (intent.hasExtra("unit_directory")) {
            mUnitDirectory = intent.getStringExtra("unit_directory");
        }

        initView(mUnitName);
        initData(mUnitDirectory);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
    }

    public void onHomeworkClick(View view) {
        Intent intent = new Intent(ReviewActivity.this, HomeworkActivity.class);
        intent.putExtra("unit_name", mUnitName);
        intent.putExtra("unit_directory", mUnitDirectory);
        startActivity(intent);
    }

    private void initView(String unitName) {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        TextView titleTextView = findViewById(R.id.tv_title);
        titleTextView.setText(unitName);

        mVideoView = findViewById(R.id.video_view);
    }

    private void initData(String unitDirectory) {
        List<UnitItemData> list = new ArrayList<>();

        mUnitBean = UnitBean.parseConfig(unitDirectory);

        if (mUnitBean.key_words.size() > 0) {
            list.add(new UnitItemData(getString(R.string.key_words), StudyType.Word));
        }

        if (mUnitBean.sup_words.size() > 0) {
            list.add(new UnitItemData(getString(R.string.sup_words), StudyType.Word));
        }

        File unit = new File(unitDirectory);

        for (SongBean songBean : mUnitBean.songs) {
            File song = new File(songBean.file);
            if (song.exists()) {
                list.add(new UnitItemData(songBean.name, StudyType.Song, song.getPath()));
            }
        }

        for (StoryBean storyBean : mUnitBean.storys) {
            File story = new File(storyBean.file);
            if (story.exists()) {
                list.add(new UnitItemData(storyBean.name, StudyType.Story, story.getPath()));
            }
        }

        mAdapter = new UnitAdapter(this, mVideoView, list, new UnitWrapper(mUnitName, mUnitBean, unit));
        mRecyclerView.setAdapter(mAdapter);
    }
}
