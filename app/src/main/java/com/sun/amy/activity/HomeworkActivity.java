package com.sun.amy.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sun.amy.R;
import com.sun.amy.adapter.RecordAdapter;
import com.sun.amy.data.RecordItemData;
import com.sun.amy.data.StudyType;
import com.sun.amy.utils.AudioRecordManager;
import com.sun.amy.utils.RecorderThread;
import com.sun.amy.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.view.animation.Animation.INFINITE;
import static android.view.animation.Animation.REVERSE;
import static android.widget.LinearLayout.VERTICAL;

public class HomeworkActivity extends Activity implements RecordAdapter.SharedModeCallback {

    private static final int MSG_UPDATE_REC_TIME = 0;
    private static final int MSG_STOP_RECORDING = 1;

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

    private RecorderThread mRecorderThread;
    private long mRecorderStartTime = 0l;

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
                } else {
                    mShareButton.setEnabled(false);
                    mShareButton.setTextColor(getResources().getColor(R.color.light_grey));
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

    private void initView(String unitName) {
        TextView titleTextView = findViewById(R.id.tv_title);
        titleTextView.setText(unitName);

        mWordRadioButton = findViewById(R.id.rb_word);
        mWordRadioButton.setChecked(true);
        mSongRadioButton = findViewById(R.id.rb_song);
        mStoryRadioButton = findViewById(R.id.rb_story);

        mShareButton = findViewById(R.id.btn_share);
        mControlView = findViewById(R.id.ly_control);
        mControlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRecorderThread == null) {
                    String postfix = "";
                    String directory = "";
                    if (mWordRadioButton.isChecked()) {
                        postfix = "_Key_Words";
                        directory = "words";
                    }
                    if (mSongRadioButton.isChecked()) {
                        postfix = "_Songs";
                        directory = "songs";
                    }
                    if (mStoryRadioButton.isChecked()) {
                        postfix = "_Storys";
                        directory = "storys";
                    }

                    File unit = new File(mUnitDirectory);
                    if (unit.exists()) {
                        File rec = new File(unit, "rec");
                        if (!rec.exists()) {
                            rec.mkdir();
                        }

                        File dir = new File(rec, directory);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }

                        String fileName = "Amy_" + mUnitName + postfix + ".wav";

                        int count = 0;
                        File recFile;
                        while (true) {
                            recFile = new File(dir, fileName);
                            if (recFile.exists()) {
                                count += 1;
                                fileName = "Amy_" + mUnitName + postfix + "(" + count + ").wav";
                            } else {
                                break;
                            }
                        }

                        if (recFile != null) {
                            mRecorderThread = new RecorderThread();
                            mRecorderThread.startRecording(recFile.getPath());

                            ((TextView)findViewById(R.id.tv_record)).setText(getString(R.string.stop));

                            mRecorderStartTime = SystemClock.elapsedRealtime();
                            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_REC_TIME, 100);
                        }
                    }
                } else {
                    mRecorderThread.stopRecording();
                    mRecorderThread = null;
                    mHandler.sendEmptyMessageDelayed(MSG_STOP_RECORDING, 333);
                }
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(VERTICAL);
        mRecyclerView.setLayoutManager(manager);

//        mAnimation = new ScaleAnimation(
//                0.9f, 1.0f, 0.9f, 1.0f,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
//        );
//        mAnimation.setDuration(1500);
//        mAnimation.setRepeatCount(INFINITE);
//        mAnimation.setRepeatMode(REVERSE);
//
//        findViewById(R.id.tv_record).startAnimation(mAnimation);
    }

    public void initData(String unitDirectory) {
        updateRecList(unitDirectory);
        mAdapter = new RecordAdapter(this, mRecordsList, this);
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

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_UPDATE_REC_TIME:
                    if (mRecorderThread != null) {
                        long interval = SystemClock.elapsedRealtime() - mRecorderStartTime;
                        String intervalString = TimeUtils.formatNumberToHourMinuteSecond((double)(interval / 1000));
                        ((TextView)findViewById(R.id.tv_rec_time)).setText(intervalString);
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_REC_TIME, 100);
                    }
                    break;

                case MSG_STOP_RECORDING:
                    ((TextView)findViewById(R.id.tv_record)).setText(getString(R.string.record));
                    ((TextView)findViewById(R.id.tv_rec_time)).setText("00:00:00");
                    updateRecList(mUnitDirectory);
                    mAdapter.updateData(mRecordsList);
                    break;

                default:
                    break;
            }

            return false;
        }
    });
}
