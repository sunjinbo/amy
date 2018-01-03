package com.sun.amy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.sun.amy.R;
import com.sun.amy.adapter.UnitAdapter;
import com.sun.amy.data.LessonBean;
import com.sun.amy.data.SongBean;
import com.sun.amy.data.StoryBean;
import com.sun.amy.data.StudyType;
import com.sun.amy.data.UnitItemData;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class ReviewActivity extends Activity {
    private RecyclerView mRecyclerView;
    private UnitAdapter mAdapter;
    private LessonBean mLessonBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        String unitName = "";
        String unitDirectory = "";
        Intent intent = getIntent();
        if (intent.hasExtra("unit_name")) {
            unitName = intent.getStringExtra("unit_name");
        }

        if (intent.hasExtra("unit_directory")) {
            unitDirectory = intent.getStringExtra("unit_directory");
        }

        initView(unitName);
        initData(unitDirectory);
    }

    private void initView(String unitName) {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        TextView titleTextView = findViewById(R.id.tv_title);
        titleTextView.setText(unitName);
    }

    private void initData(String unitDirectory) {
        List<UnitItemData> list = new ArrayList<>();

        mLessonBean = LessonBean.parseConfig(unitDirectory);

        if (mLessonBean.words.size() > 0) {
            list.add(new UnitItemData(getString(R.string.key_words), StudyType.Word));
        }

        for (SongBean songBean : mLessonBean.songs) {
            list.add(new UnitItemData(songBean.name, StudyType.Song));
        }

        for (StoryBean storyBean : mLessonBean.storys) {
            list.add(new UnitItemData(storyBean.name, StudyType.Story));
        }

        mAdapter = new UnitAdapter(this, list);
        mRecyclerView.setAdapter(mAdapter);
    }
}
