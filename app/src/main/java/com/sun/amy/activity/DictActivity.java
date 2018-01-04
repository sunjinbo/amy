package com.sun.amy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.amy.R;
import com.sun.amy.data.WordBean;
import com.sun.amy.data.WordWrapper;
import com.sun.amy.utils.DictUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DictActivity extends Activity {

    private TextView mSequenceNumberTextView;
    private ImageView mFigureImageView;
    private TextView mWordTextView;
    private WordWrapper mWordWrapper;

    private Map<String, WordBean> mStoreWords;

    private List<WordBean> mStudyWords;
    private int mStudyIndex = 0;

    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "amy");

        Intent intent = getIntent();
        if (intent.hasExtra("unit_words")) {
            mWordWrapper = (WordWrapper) intent.getSerializableExtra("unit_words");
            mStudyWords = mWordWrapper.words;
        }

        String unitName = getString(R.string.dict);
        if (intent.hasExtra("unit_name")) {
            unitName = intent.getStringExtra("unit_name");
        }

        mStoreWords = DictUtils.readWordFromStore();

        if (mStudyWords == null) {
            mStudyWords = new ArrayList<>();
            for (WordBean wordBean : mStoreWords.values()) {
                mStudyWords.add(wordBean);
            }
        }

        mSequenceNumberTextView = findViewById(R.id.tv_sequence_number);
        mFigureImageView = findViewById(R.id.iv_figure);
        mWordTextView = findViewById(R.id.tv_word);

        TextView titleTextView = findViewById(R.id.tv_title);
        titleTextView.setText(unitName);

        learnNext();
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

    public void onForgetClick(View view) {
        if (mStudyWords.size() == 0) return;

        WordBean wordBean = mStudyWords.get(mStudyIndex);

        if (!mStoreWords.containsKey(wordBean.img)) {
            wordBean.score = 0;
            mStoreWords.put(wordBean.img, wordBean);
            DictUtils.writeWordToStore(mStoreWords);
        }

        learnNext();
    }

    public void onKnowClick(View view) {
        if (mStudyWords.size() == 0) return;

        WordBean wordBean = mStudyWords.get(mStudyIndex);

        if (mStoreWords.containsKey(wordBean.img)) {
            WordBean storedWordBean = mStoreWords.get(wordBean.img);
            storedWordBean.score += 20;
            DictUtils.writeWordToStore(mStoreWords);
        }

        learnNext();
    }

    private void learnNext() {
        if (mStudyWords.size() == 0) {
            mFigureImageView.setVisibility(View.INVISIBLE);
            mWordTextView.setVisibility(View.INVISIBLE);
            mSequenceNumberTextView.setText("0/0");
        } else {
            if (mStudyWords != null && mStudyIndex < mStudyWords.size()) {
                WordBean wordBean = mStudyWords.get(mStudyIndex);

                mFigureImageView.setImageBitmap(BitmapFactory.decodeFile(wordBean.img));
                mWordTextView.setText(wordBean.english);
                mSequenceNumberTextView.setText((mStudyIndex + 1) + "/" + mStudyWords.size());

                mStudyIndex += 1;
                if (mStudyIndex >= mStudyWords.size()) {
                    mStudyIndex = 0;
                }
            }
        }
    }
}
