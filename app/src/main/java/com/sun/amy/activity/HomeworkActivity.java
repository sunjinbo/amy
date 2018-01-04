package com.sun.amy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.view.View;

import com.sun.amy.R;

import java.io.File;

public class HomeworkActivity extends Activity {

    private PowerManager.WakeLock mWakeLock;

    private String mUnitName;
    private String mUnitDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "amy");

        Intent intent = getIntent();
        if (intent.hasExtra("unit_name")) {
            mUnitName = intent.getStringExtra("unit_name");
        }

        if (intent.hasExtra("unit_directory")) {
            mUnitDirectory = intent.getStringExtra("unit_directory");
        }
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

    public void onShareClick(View view) {
        File sharedFile = new File(Environment.getExternalStorageDirectory(), "english.jpg");
        if (sharedFile.exists()) {
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
                    BitmapFactory.decodeFile(sharedFile.getPath()), "", ""));

            Intent intent = new Intent();
            intent.setPackage("com.tencent.mm");
            intent.setAction(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("image/*");
            startActivity(Intent.createChooser(intent, "分享"));
        }
    }
}
