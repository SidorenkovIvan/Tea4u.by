package ru.SidorenkovIvan.MyApplication;

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

    public void setId(String id) {
        mId = id;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getProductUrl() {
        return mProductUrl;
    }

    public void setProductUrl(String productUrl) {
        mProductUrl = productUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getImages() {
        return mImages;
    }

    public void setImages(String images) {
        mImages = images;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    @NotNull
    public String toString() {
        return mTitle + "\n" + mCode;
    }
}