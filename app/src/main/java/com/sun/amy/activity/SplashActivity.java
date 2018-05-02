package com.sun.amy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.sun.amy.R;
import com.sun.amy.utils.FileUtil;
import com.sun.amy.utils.VersionUtils;

import java.io.File;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ((TextView)findViewById(R.id.tv_version)).setText("V" + VersionUtils.getAppVersion(this));

        new Thread(new Runnable() {
            @Override
            public void run() {

                final File logo = new File(Environment.getExternalStorageDirectory(), "logo.png");
                FileUtil.Assets2Sd(SplashActivity.this, "logo.png", logo.getAbsolutePath());

                mHandler.sendEmptyMessageDelayed(0, 2000);
            }
        }).start();
    }

    final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
//            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            Intent intent = new Intent(SplashActivity.this, LessonsActivity.class);
            intent.putExtra("lesson_name", getString(R.string.okay_english));
            intent.putExtra("lesson_directory", "ok");
            startActivity(intent);
            finish();
            return false;
        }
    });
}
