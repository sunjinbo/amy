package com.sun.amy.data;

/**
 * UnitItemData class.
 */
public class UnitItemData {
    public String title;
    public StudyType type;
    public String path;
    public boolean selected = false;

    public UnitItemData() {}

    public UnitItemData(String title, StudyType type) {
        this.title = title;
        this.type = type;
    }

    public UnitItemData(String title, StudyType type, String path) {
        this.title = title;
        this.type = type;
        this.path = path;
    }
}
