package com.sun.amy.data;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * WordBean class.
 */
public class WordBean implements Serializable {
    public boolean isKeyWord = true;
    public String english;
    public String chinese;
    public String img;
    public int score = 100;

    public static WordBean parse(String wordString) {
        if (!TextUtils.isEmpty(wordString)) {
            String[] infos = wordString.split("\\|");
            if (infos != null && infos.length == 5) {
                WordBean wordBean = new WordBean();
                wordBean.isKeyWord = TextUtils.equals(infos[0], "1");
                wordBean.english = infos[1];
                wordBean.chinese = infos[2];
                wordBean.img = infos[3];
                wordBean.score = Integer.parseInt(infos[4]);
                return wordBean;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String wordString = "";
        wordString += isKeyWord ? "1" : "0";
        wordString += ("|" + english);
        wordString += ("|" + chinese);
        wordString += ("|" + img);
        wordString += ("|" + score);
        return  wordString;
    }
}
