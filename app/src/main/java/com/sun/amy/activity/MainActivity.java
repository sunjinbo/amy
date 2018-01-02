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
        startActivity(new Intent(MainActivity.this, LessonsActivity.class));
    }

    public void onUmiEnglishClick(View view) {
        startActivity(new Intent(MainActivity.this, LessonsActivity.class));
    }

    public void onDictClick(View view) {
        startActivity(new Intent(MainActivity.this, DictActivity.class));
    }

    public void onScoreClick(View view) {
        startActivity(new Intent(MainActivity.this, ScoreActivity.class));
    }
}
