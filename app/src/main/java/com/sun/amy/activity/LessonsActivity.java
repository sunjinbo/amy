package com.sun.amy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sun.amy.R;
import com.sun.amy.adapter.LessonsAdapter;
import com.sun.amy.data.LessonItemData;

import java.io.File;
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

        String lessonName = "";
        String lessonDirectory = "";
        Intent intent = getIntent();
        if (intent.hasExtra("lesson_name")) {
            lessonName = intent.getStringExtra("lesson_name");
        }

        if (intent.hasExtra("lesson_directory")) {
            lessonDirectory = intent.getStringExtra("lesson_directory");
        }

        initView(lessonName);
        initData(lessonDirectory);
    }

    private void initView(String lessonName) {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        TextView titleTextView = findViewById(R.id.tv_title);
        titleTextView.setText(lessonName);

        TextView dictTextView = findViewById(R.id.tv_dict);
        dictTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LessonsActivity.this, DictActivity.class));
            }
        });
    }

    private void initData(String lessonDirectory) {
        List<LessonItemData> list = new ArrayList<>();
        File amy = new File(Environment.getExternalStorageDirectory(), "amy");
        if (amy.exists()) {
            File lesson = new File(amy, lessonDirectory);
            if (lesson.exists()) {
                File[] files = lesson.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            File config = new File(file, "config.ini");
                            if (config.exists()) {
                                list.add(new LessonItemData(file.getName(), file.getPath()));
                            }
                        }
                    }
                }
            }
        }

        mAdapter = new LessonsAdapter(this, list);
        mRecyclerView.setAdapter(mAdapter);
    }
}
