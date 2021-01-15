package ru.sidorenkovivan.myapplication.entities;

import android.graphics.Bitmap;
import org.jetbrains.annotations.NotNull;
import java.io.Serializable;

public class Product implements Serializable {

    private String mId;
    private Bitmap mImage;
    private String mTitle;
    private String mProductUrl;
    private String mDescription;
    private String mImages;
    private String mCode;
    private String mPrice;

    public String getId() {
        return mId;
    }

    public void setId(final String pId) {
        mId = pId;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(final Bitmap pImage) {
        mImage = pImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(final String pTitle) {
        mTitle = pTitle;
    }

    public String getProductUrl() {
        return mProductUrl;
    }

    public void setProductUrl(final String pProductUrl) {
        mProductUrl = pProductUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(final String pDescription) {
        mDescription = pDescription;
    }

    public String getImages() {
        return mImages;
    }

    public void setImages(final String pImages) {
        mImages = pImages;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(final String pCode) {
        mCode = pCode;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(final String pPrice) {
        mPrice = pPrice;
    }

    @NotNull
    public String toString() {
        return mTitle + "\n" + mCode;
    }
}