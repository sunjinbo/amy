package com.sun.amy.views;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.amy.R;
import com.sun.amy.data.WordBean;
import com.sun.amy.data.WordWrapper;
import com.sun.amy.utils.DictUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * WordView class.
 */
public class WordView extends FrameLayout implements TextToSpeech.OnInitListener, View.OnClickListener {

    private View mRootView;

    private TextView mSequenceNumberTextView;
    private ImageView mFigureImageView;
    private TextView mWordTextView;

    private Map<String, WordBean> mStoreWords;

    private List<WordBean> mStudyWords;
    private int mStudyIndex = 0;

    private TextToSpeech mTTS;

    public WordView(@NonNull Context context) {
        super(context);
        initView();
    }

    public WordView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public void onInit(int status) {
        Log.d("amy", "tts init - status = " + status);
        if (status == TextToSpeech.SUCCESS) {
            int result = mTTS.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d("amy", "TTS data is lost or no supported.");
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (mStudyWords.size() == 0) return;

        speakText();
    }

    public void setWords(WordWrapper wrapper) {
        mStudyWords = wrapper.words;
        mStoreWords = DictUtils.readWordFromStore();

        if (mStudyWords == null) {
            mStudyWords = new ArrayList<>();
            for (WordBean wordBean : mStoreWords.values()) {
                mStudyWords.add(wordBean);
            }
        }

        learnNext();
    }

    private void initView() {
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.word_view, this, false);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mRootView.setLayoutParams(params);
        addView(mRootView);

        mTTS = new TextToSpeech(getContext(), null);

        mSequenceNumberTextView = mRootView.findViewById(R.id.tv_sequence_number);
        mFigureImageView = mRootView.findViewById(R.id.iv_figure);
        mWordTextView = mRootView.findViewById(R.id.tv_word);

        mFigureImageView.setOnClickListener(this);
        mWordTextView.setOnClickListener(this);

        mRootView.findViewById(R.id.btn_know).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStudyWords.size() == 0) return;

                WordBean wordBean = mStudyWords.get(mStudyIndex);

                if (mStoreWords.containsKey(wordBean.img)) {
                    WordBean storedWordBean = mStoreWords.get(wordBean.img);
                    storedWordBean.score += 20;
                    DictUtils.writeWordToStore(mStoreWords);
                }

                learnNext();
            }
        });

        mRootView.findViewById(R.id.btn_forget).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStudyWords.size() == 0) return;

                WordBean wordBean = mStudyWords.get(mStudyIndex);

                if (!mStoreWords.containsKey(wordBean.img)) {
                    wordBean.score = 0;
                    mStoreWords.put(wordBean.img, wordBean);
                    DictUtils.writeWordToStore(mStoreWords);
                }

                learnNext();
            }
        });
    }

    private void learnNext() {
        if (mStudyWords.size() == 0) {
            mSequenceNumberTextView.setText("0/0");
            (findViewById(R.id.btn_forget)).setEnabled(false);
            (findViewById(R.id.btn_know)).setEnabled(false);
            ((Button)findViewById(R.id.btn_forget)).setTextColor(getResources().getColor(R.color.light_grey));
            ((Button)findViewById(R.id.btn_know)).setTextColor(getResources().getColor(R.color.light_grey));
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

    private void speakText() {
        if (mTTS != null && !mTTS.isSpeaking()) {
            mTTS.setPitch(0.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            mTTS.speak(mWordTextView.getText().toString(),
                    TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
