package com.sun.amy.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * WordWrapper class.
 */
public class WordWrapper implements Serializable {
    public List<WordBean> words = new ArrayList<>();
    public String path;

    public WordWrapper(String path, List<WordBean> words) {
        this.path = path;
        for (WordBean wordBean : words) {
            this.words.add(wordBean);
        }
    }
}
