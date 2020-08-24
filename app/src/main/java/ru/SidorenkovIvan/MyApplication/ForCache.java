package ru.SidorenkovIvan.MyApplication;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.util.ArrayList;

public class ForCache {
    private static int availableMem = (int) (Runtime.getRuntime().maxMemory());
    private static int cacheMem = availableMem;
    public static LruCache<String, ArrayList<Bitmap>> lruCacheForLargeImages = new LruCache<>(cacheMem);
    public static LruCache<String, Bitmap> lruCacheForImages = new LruCache<>(cacheMem);
    public static LruCache<String, String> lruCacheForTitles = new LruCache<>(cacheMem);
    public static LruCache<String, String> lruCacheForUrls = new LruCache<>(cacheMem);

    public static void addLargeImagesToMemoryCache(String key, ArrayList<Bitmap> bitmaps) {
        if (getLargeImagesFromMemoryCache(key) == null)
            lruCacheForLargeImages.put(key, bitmaps);
    }
    public static ArrayList<Bitmap> getLargeImagesFromMemoryCache(String key) {
        return lruCacheForLargeImages.get(key);
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null)
            lruCacheForImages.put(key, bitmap);
    }
    public static Bitmap getBitmapFromMemoryCache(String key) {
        return lruCacheForImages.get(key);
    }


    public static void addTitleToMemoryCache(String key, String tit) {
        if (getTitleFromMemoryCache(key) == null)
            lruCacheForTitles.put(key, tit);
    }
    public static String getTitleFromMemoryCache(String key) {
        return lruCacheForTitles.get(key);
    }


    public static void addUrlToMemoryCache(String key, String url) {
        if (getUrlFromMemoryCache(key) == null)
            lruCacheForUrls.put(key, url);
    }
    public static String getUrlFromMemoryCache(String key) {
        return lruCacheForUrls.get(key);
    }
}
