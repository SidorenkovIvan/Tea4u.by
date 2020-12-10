package ru.SidorenkovIvan.MyApplication;

import java.io.Serializable;

public class Category implements Serializable {

    private final String mId;
    private final String mTitle;

    public Category(final String pId, final String pTitle) {
        mId = pId;
        mTitle = pTitle;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }
}
