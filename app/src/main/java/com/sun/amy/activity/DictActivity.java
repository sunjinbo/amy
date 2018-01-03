package com.sun.amy.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.amy.R;
import com.sun.amy.data.WordBean;
import com.sun.amy.data.WordWrapper;

import java.io.File;
import java.util.List;

public class DictActivity extends Activity {

    private TextView mSequenceNumberTextView;
    private ImageView mFigureImageView;
    private TextView mWordTextView;

    private WordWrapper mWordWrapper;
    private List<WordBean> mWords;
    private int mIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);

        Intent intent = getIntent();
        if (intent.hasExtra("unit_words")) {
            mWordWrapper = (WordWrapper) intent.getSerializableExtra("unit_words");
            mWords = mWordWrapper.words;
        }

        String unitName = getString(R.string.dict);
        if (intent.hasExtra("unit_name")) {
            unitName = intent.getStringExtra("unit_name");
        }

        mSequenceNumberTextView = findViewById(R.id.tv_sequence_number);
        mFigureImageView = findViewById(R.id.iv_figure);
        mWordTextView = findViewById(R.id.tv_word);

        TextView titleTextView = findViewById(R.id.tv_title);
        titleTextView.setText(unitName);

        learnNext();
    }

    public void onForgetClick(View view) {
        learnNext();
    }

    public void onKnowClick(View view) {
        learnNext();
    }

    private void learnNext() {
        mIndex += 1;
        if (mIndex < mWords.size()) {
            mSequenceNumberTextView.setText((mIndex + 1) + "/" + mWords.size());
            File file = new File(mWordWrapper.path);
            File bmp = new File(file, mWords.get(mIndex).img);
            Bitmap bitmap = BitmapFactory.decodeFile(bmp.getAbsolutePath());
            mFigureImageView.setImageBitmap(bitmap);
            mWordTextView.setText(mWords.get(mIndex).english);
        }
    }
}
