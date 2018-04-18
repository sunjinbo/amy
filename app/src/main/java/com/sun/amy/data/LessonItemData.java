package com.sun.amy.data;

import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * LessonItemData class.
 */
public class LessonItemData implements Comparable{
    public String title;
    public String path;

    public LessonItemData() {}

    public LessonItemData(String title, String path) {
        this.title = title;
        this.path = path;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        LessonItemData itemData = (LessonItemData) o;
        if (itemData == null || TextUtils.isEmpty(itemData.title)) return 0;
        return -itemData.title.compareTo(title);
    }
}
