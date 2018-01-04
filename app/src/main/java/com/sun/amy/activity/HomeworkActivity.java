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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.VideoView;

import com.sun.amy.R;
import com.sun.amy.adapter.RecordAdapter;
import com.sun.amy.data.RecordItemData;
import com.sun.amy.data.StudyType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class HomeworkActivity extends Activity {

    private PowerManager.WakeLock mWakeLock;

    private String mUnitName;
    private String mUnitDirectory;

    private Button mShareButton;
    private View mControlView;
    private RecyclerView mRecyclerView;

    private RadioButton mWordRadioButton;
    private RadioButton mSongRadioButton;
    private RadioButton mStoryRadioButton;

    private List<RecordItemData> mRecordsList = new ArrayList<>();

    private RecordAdapter mAdapter;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.releasePlayer();
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

    private void initView(String unitName) {
        TextView titleTextView = findViewById(R.id.tv_title);
        titleTextView.setText(unitName);

        mWordRadioButton = findViewById(R.id.rb_word);
        mWordRadioButton.setChecked(true);
        mSongRadioButton = findViewById(R.id.rb_song);
        mStoryRadioButton = findViewById(R.id.rb_story);

        mShareButton = findViewById(R.id.btn_share);
        mControlView = findViewById(R.id.ly_control);
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(VERTICAL);
        mRecyclerView.setLayoutManager(manager);
//        mRecyclerView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (!mIsSharedMode) {
//                    mIsSharedMode = true;
//
//                    for (RecordItemData itemData : mRecordsList) {
//                        itemData.is_checked = false;
//                    }
//                    mAdapter.updateData(mIsSharedMode, mRecordsList);
//                }
//                return false;
//            }
//        });
    }

    public void initData(String unitDirectory) {
        updateRecList(unitDirectory);
        mAdapter = new RecordAdapter(this, mRecordsList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateRecList(String unitDirectory) {
        mRecordsList.clear();
        File unit = new File(unitDirectory);
        if (unit.exists()) {
            File rec = new File(unit, "rec");
            if (rec.exists()) {
                File words = new File(rec, "words");
                if (words.exists()) {
                    scanRec(words, StudyType.Word);
                }

                File songs = new File(rec, "songs");
                if (songs.exists()) {
                    scanRec(songs, StudyType.Song);
                }

                File storys = new File(rec, "storys");
                if (storys.exists()) {
                    scanRec(storys, StudyType.Story);
                }
            }
        }
    }

    private void scanRec(File directory, StudyType type) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".wav")) {
                RecordItemData itemData = new RecordItemData(file.getName(), type, file.getPath());
                mRecordsList.add(itemData);
            }
        }
    }
}
