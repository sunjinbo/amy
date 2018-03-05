package com.sun.amy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sun.amy.R;
import com.sun.amy.adapter.UnitAdapter;
import com.sun.amy.data.UnitBean;
import com.sun.amy.data.UnitWrapper;
import com.sun.amy.data.SongBean;
import com.sun.amy.data.StoryBean;
import com.sun.amy.data.StudyType;
import com.sun.amy.data.UnitItemData;
import com.sun.amy.utils.RecorderThread;
import com.sun.amy.utils.TimeUtils;
import com.sun.amy.views.MediaView;
import com.sun.amy.views.WordView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class ReviewActivity extends Activity {

    private static final int MSG_UPDATE_REC_TIME = 0;
    private static final int MSG_STOP_RECORDING = 1;

    private RecyclerView mRecyclerView;
    private UnitAdapter mAdapter;
    private UnitBean mUnitBean;
    private MediaView mVideoView;
    private WordView mWordView;
    private Button mDoHomeworkButton;
    private TextView mWorkbookTextView;
    private TextView mRecordingTextView;
    private String mUnitName;
    private String mUnitDirectory;
    private PowerManager.WakeLock mWakeLock;

    private RecorderThread mRecorderThread;
    private long mRecorderStartTime = 0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

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
        mVideoView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
        mVideoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.release();
        mWordView.release();
    }

    public void onHomeworkClick(View view) {
        if (mRecorderThread == null) {

            String postfix = "";
            String directory = "";

            UnitItemData itemData = mAdapter.getSelectedItem();

            postfix = itemData.title.replace(' ', '_');
            postfix = postfix.replace(",", "");
            postfix = postfix.replace(".", "");
            postfix = postfix.replace("!", "");
            postfix = postfix.replace("?", "");

            switch (itemData.type) {
                case Word:
                    directory = "words";
                    break;
                case Song:
                    directory = "songs";
                    break;
                case Story:
                    directory = "storys";
                    break;
                default:
                    break;
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

                String fileName = "Amy_" + postfix + ".wav";

                int count = 0;
                File recFile;
                while (true) {
                    recFile = new File(dir, fileName);
                    if (recFile.exists()) {
                        count += 1;
                        fileName = "Amy_" + postfix + "(" + count + ").wav";
                    } else {
                        break;
                    }
                }

                if (recFile != null) {
                    mRecorderThread = new RecorderThread();
                    mRecorderThread.startRecording(recFile.getPath());

                    mDoHomeworkButton.setText(getString(R.string.stop));

                    mRecordingTextView.setText(ReviewActivity.this.getString(R.string.recording) + " 00:00:00");
                    mRecordingTextView.setVisibility(View.VISIBLE);

                    mRecorderStartTime = SystemClock.elapsedRealtime();
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_REC_TIME, 100);
                }
            }
        } else {
            mDoHomeworkButton.setText(getString(R.string.record));
            mRecorderThread.stopRecording();
            mRecorderThread = null;
            mHandler.sendEmptyMessageDelayed(MSG_STOP_RECORDING, 333);
        }
    }

    private void initView(String unitName) {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(VERTICAL);
        mRecyclerView.setLayoutManager(manager);

        TextView titleTextView = findViewById(R.id.tv_title);
        titleTextView.setText(unitName);

        mVideoView = findViewById(R.id.video_view);
        mWordView = findViewById(R.id.word_view);
        mDoHomeworkButton = findViewById(R.id.btn_do_homework);
        mRecordingTextView = findViewById(R.id.tv_rec);
        mWorkbookTextView = findViewById(R.id.tv_workbook);
        mWorkbookTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReviewActivity.this, HomeworkActivity.class);
                intent.putExtra("unit_name", mUnitName);
                intent.putExtra("unit_directory", mUnitDirectory);
                startActivity(intent);
            }
        });
    }

    private void initData(String unitDirectory) {
        List<UnitItemData> list = new ArrayList<>();

        mUnitBean = UnitBean.parseConfig(unitDirectory);

        if (mUnitBean.key_words.size() > 0) {
            list.add(new UnitItemData(getString(R.string.key_words), StudyType.Word));
        }

        if (mUnitBean.sup_words.size() > 0) {
            list.add(new UnitItemData(getString(R.string.sup_words), StudyType.Word));
        }

        File unit = new File(unitDirectory);

        for (SongBean songBean : mUnitBean.songs) {
            File song = new File(songBean.file);
            if (song.exists()) {
                list.add(new UnitItemData(songBean.name, StudyType.Song, song.getPath()));
            }
        }

        for (StoryBean storyBean : mUnitBean.storys) {
            File story = new File(storyBean.file);
            if (story.exists()) {
                list.add(new UnitItemData(storyBean.name, StudyType.Story, story.getPath()));
            }
        }

        mAdapter = new UnitAdapter(this, mVideoView, mWordView, mDoHomeworkButton, list, new UnitWrapper(mUnitName, mUnitBean, unit));
        mRecyclerView.setAdapter(mAdapter);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_UPDATE_REC_TIME:
                    if (mRecorderThread != null) {
                        long interval = SystemClock.elapsedRealtime() - mRecorderStartTime;
                        String intervalString = TimeUtils.formatNumberToHourMinuteSecond((double)(interval / 1000));
                        mRecordingTextView.setText(ReviewActivity.this.getString(R.string.recording) + " " + intervalString);
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_REC_TIME, 100);
                    }
                    break;

                case MSG_STOP_RECORDING:
                    mRecordingTextView.setVisibility(View.INVISIBLE);
                    Toast.makeText(ReviewActivity.this, getString(R.string.add_workbook_prompt), Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }

            return false;
        }
    });
}
