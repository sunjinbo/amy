package com.sun.amy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sun.amy.R;
import com.sun.amy.adapter.LessonsAdapter;
import com.sun.amy.data.LessonItemData;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class LessonsActivity extends Activity {

    private RecyclerView mRecyclerView;
    private LessonsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        initView();
        initData();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(VERTICAL);
        mRecyclerView.setLayoutManager(manager);
    }

    private void initData() {
        List<LessonItemData> list = new ArrayList<>();
        list.add(new LessonItemData("Unit 1 At School"));
        list.add(new LessonItemData("Unit 2 Our Senses"));
        list.add(new LessonItemData("Unit 3 At the Fair"));
        list.add(new LessonItemData("Unit 4 People We Know"));
        list.add(new LessonItemData("Unit 5 Zoo Animals"));

        list.add(new LessonItemData("Unit 6 Clothes for All Weather"));
        list.add(new LessonItemData("Unit 7 Foods We Like"));
        list.add(new LessonItemData("Unit 8 Our Neighborhood"));
        list.add(new LessonItemData("Unit 9 The Sky"));

        mAdapter = new LessonsAdapter(this, list);
        mRecyclerView.setAdapter(mAdapter);
    }
}
