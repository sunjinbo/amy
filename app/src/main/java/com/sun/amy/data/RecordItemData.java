package com.sun.amy.data;

import android.support.annotation.NonNull;

import com.sun.amy.utils.CapacityUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RecordItemData class.
 */
public class RecordItemData implements Comparable {
    public String title;
    public StudyType type;
    public String path;
    public boolean is_checked = false;
    public boolean is_playing = false;

    public RecordItemData() {}

    public RecordItemData(String title, StudyType type) {
        this.title = title;
        this.type = type;
    }

    public RecordItemData(String title, StudyType type, String path) {
        this.title = title;
        this.type = type;
        this.path = path;
    }

    public String getCreateTime() {
        File file = new File(path);
        Date lastModDate = new Date(file.lastModified());
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(lastModDate);
    }

    public String getSize() {
        File file = new File(path);
        return CapacityUtil.ConvertByteToString(file.length());
    }

    public long lastModified() {
        File file = new File(path);
        if (file.exists()) {
            return file.lastModified();
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(@NonNull Object o) {
        RecordItemData itemData = (RecordItemData) o;

        if (itemData.lastModified() > lastModified()) {
            return -1;
        } else if (itemData.lastModified() < lastModified()) {
            return 1;
        } else {
            return 0;
        }
    }
}
