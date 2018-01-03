package com.sun.amy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sun.amy.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onOkEnglishClick(View view) {
        Intent intent = new Intent(MainActivity.this, LessonsActivity.class);
        intent.putExtra("lesson_name", getString(R.string.okay_english));
        intent.putExtra("lesson_directory", "ok");
        startActivity(intent);
    }

    public void onUmiEnglishClick(View view) {
        Intent intent = new Intent(MainActivity.this, LessonsActivity.class);
        intent.putExtra("lesson_name", getString(R.string.umi_english));
        intent.putExtra("lesson_directory", "umi");
        startActivity(intent);
    }

    public void onDictClick(View view) {
        startActivity(new Intent(MainActivity.this, DictActivity.class));
    }

    public void onScoreClick(View view) {
        startActivity(new Intent(MainActivity.this, ScoreActivity.class));
    }
}
