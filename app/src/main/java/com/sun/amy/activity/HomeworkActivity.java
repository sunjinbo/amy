package com.sun.amy.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sun.amy.R;
import com.sun.amy.adapter.RecordAdapter;
import com.sun.amy.data.RecordItemData;
import com.sun.amy.data.StudyType;
import com.sun.amy.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class HomeworkActivity extends Activity implements RecordAdapter.SharedModeCallback {

    private static final int MSG_PLAY_RECORD_STARTED = 0;
    private static final int MSG_PLAY_RECORD_UPDATED = 1;
    private static final int MSG_PLAY_RECORD_COMPLETED = 2;

    private PowerManager.WakeLock mWakeLock;

    private String mUnitName;
    private String mUnitDirectory;

    private Button mShareButton;
    private Button mDeleteButton;
    private RecyclerView mRecyclerView;

    private ProgressBar mProgressBar;
    private TextView mCurrentPositionTextView;
    private TextView mTotalDurationTextView;

    private List<RecordItemData> mRecordsList = new ArrayList<>();

    private RecordAdapter mAdapter;
    private boolean mIsPlaying = false;

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

    @Override
    public void onBackPressed() {
        if (mAdapter.isSharedMode()) {
            mAdapter.recoverSharedMode();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSharedModeUpdated() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAdapter.isSharedMode()) {
                    mShareButton.setEnabled(true);
                    mShareButton.setTextColor(getResources().getColor(R.color.light_sea_green));

                    mDeleteButton.setEnabled(true);
                    mDeleteButton.setTextColor(getResources().getColor(R.color.indian_red));
                } else {
                    mShareButton.setEnabled(false);
                    mShareButton.setTextColor(getResources().getColor(R.color.light_grey));

                    mDeleteButton.setEnabled(false);
                    mDeleteButton.setTextColor(getResources().getColor(R.color.light_grey));
                }
            }
        });
    }

    public void onShareClick(View view) {
        RecordItemData itemData = mAdapter.getSelectedRecord();
        if (itemData != null) {
            File sharedFile = new File(itemData.path);
            if (sharedFile.exists()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.TITLE, itemData.title);
                contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
                contentValues.put(MediaStore.MediaColumns.DATA, itemData.path);

                Uri uri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        contentValues);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("audio/*");
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
            }
        }
    }

    public void onDeleteClick(View view) {
        RecordItemData itemData = mAdapter.getSelectedRecord();
        if (itemData != null) {
            File sharedFile = new File(itemData.path);
            if (sharedFile.exists()) {
                sharedFile.delete();
            }

            updateRecList(mUnitDirectory);

            mAdapter.recoverSharedMode();
        }
    }

    private void initView(String unitName) {
        TextView titleTextView = findViewById(R.id.tv_title);
        titleTextView.setText(unitName);

        mShareButton = findViewById(R.id.btn_share);
        mDeleteButton = findViewById(R.id.btn_delete);
        mProgressBar = findViewById(R.id.progressbar);
        mCurrentPositionTextView = findViewById(R.id.tv_position);
        mTotalDurationTextView = findViewById(R.id.tv_duration);

        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(VERTICAL);
        mRecyclerView.setLayoutManager(manager);
    }

    public void initData(String unitDirectory) {
        updateRecList(unitDirectory);
        mAdapter = new RecordAdapter(this, mHandler, mRecordsList, this);
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

    private int getCurrentProgress(float currentTime, float totalTime) {
        float totalProcess = 100;
        return  (int) ( currentTime / totalTime * totalProcess);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_PLAY_RECORD_STARTED:
                    mIsPlaying = true;
                    mHandler.sendEmptyMessageDelayed(MSG_PLAY_RECORD_UPDATED, 50);
                    break;

                case MSG_PLAY_RECORD_UPDATED:
                    if (mIsPlaying) {
                        int currentPosition = (int)((float)mAdapter.getCurrentPosition() / 1000.0f);
                        int duration = (int)((float)mAdapter.getDuration() / 1000.0f);
                        if (currentPosition >= 0 && duration >= 0l) {
                            int progress = getCurrentProgress(currentPosition, duration);
                            mProgressBar.setProgress(progress);
                            mCurrentPositionTextView.setText(TimeUtils.formatNumberToHourMinuteSecond((double)currentPosition));
                            mTotalDurationTextView.setText(TimeUtils.formatNumberToHourMinuteSecond((double)duration));
                        }
                        mHandler.sendEmptyMessageDelayed(MSG_PLAY_RECORD_UPDATED, 50);
                    }
                    break;

                case MSG_PLAY_RECORD_COMPLETED:
                    mIsPlaying = false;
                    mProgressBar.setProgress(0);
                    mCurrentPositionTextView.setText(TimeUtils.formatNumberToHourMinuteSecond(0.0));
                    mTotalDurationTextView.setText(TimeUtils.formatNumberToHourMinuteSecond(0.0));
                    break;

                default:
                    break;
            }

            return false;
        }
    });
}
