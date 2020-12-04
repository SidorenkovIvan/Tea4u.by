package ru.SidorenkovIvan.MyApplication;

import android.graphics.Bitmap;
import android.util.LruCache;
import java.util.ArrayList;

public class ForCache {
    private static final int availableMem = (int) (Runtime.getRuntime().maxMemory());
    private static final int cacheMem = availableMem;
    public static LruCache<String, ArrayList<Bitmap>> lruCacheForLargeImages = new LruCache<>(cacheMem);

    public static void addLargeImagesToMemoryCache(String key, ArrayList<Bitmap> bitmaps) {
        if (getLargeImagesFromMemoryCache(key) == null)
            lruCacheForLargeImages.put(key, bitmaps);
    }
    public static ArrayList<Bitmap> getLargeImagesFromMemoryCache(String key) {
        return lruCacheForLargeImages.get(key);
    }
}
