package com.sun.amy.data;

/**
 * RecordItemData class.
 */
public class RecordItemData {
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
}
