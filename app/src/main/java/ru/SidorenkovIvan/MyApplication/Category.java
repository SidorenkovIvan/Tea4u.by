package ru.SidorenkovIvan.MyApplication;

import java.io.Serializable;

public class Category implements Serializable {
    private final String mId;
    private final String mTitle;

    public Category(String id, String title) {
        mId = id;
        mTitle = title;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }
}
