package ru.sidorenkovivan.tea4uby.services;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import com.github.chrisbanes.photoview.PhotoView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ImageLoader {

    private final int mAvailableMem = (int) (Runtime.getRuntime().maxMemory() / 16);
    private final LruCache<String, BitmapDrawable> mLruCache = new LruCache<>(mAvailableMem);
    private final HashMap<String, BitmapDrawable> mDiskCache = new HashMap<>();
    private final File mCacheDir;

    public ImageLoader(final Context pContext) {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            mCacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "CacheImages");
        } else {
            mCacheDir = pContext.getCacheDir();
        }

        if (!mCacheDir.exists()) {
            Log.i("Cache dir", "is created " + mCacheDir.mkdirs());
        }
    }

    private void addToLruCache(final String pKey, final BitmapDrawable pBitmapDrawable) {
        if (getFromLruCache(pKey) == null) {
            mLruCache.put(pKey, pBitmapDrawable);
        }
    }

    private BitmapDrawable getFromLruCache(final String pKey) {
        return mLruCache.get(pKey);
    }

    private void addToDiskCache(final String pKey, final BitmapDrawable pBitmapDrawable) {
        if (getFromDiskCache(pKey) == null) {
            mDiskCache.put(pKey, pBitmapDrawable);
        }
    }

    private BitmapDrawable getFromDiskCache(final String pKey) {
        return mDiskCache.get(pKey);
    }

    private BitmapDrawable getDrawable(final String pUrl) {
        try {
            final URL url = new URL(pUrl);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            final InputStream input = connection.getInputStream();
            final Bitmap bitmap = BitmapFactory.decodeStream(input);

            return new BitmapDrawable(Resources.getSystem(), bitmap);
        } catch (IOException e) {
            return null;
        }
    }

    private void loadImage(final String pUrl, final PhotoView pPhotoView) {
        final Thread thread = new Thread(() -> {
            final BitmapDrawable bitmapDrawable = getDrawable(pUrl);
            addToDiskCache(pUrl, bitmapDrawable);
            addToLruCache(pUrl, bitmapDrawable);
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> pPhotoView.setImageDrawable(bitmapDrawable));
        });
        thread.start();
    }


    public void loadAndShow(final String pUrl, final PhotoView pPhotoView) {
        if (getFromLruCache(pUrl) != null) {
            pPhotoView.setImageDrawable(getFromLruCache(pUrl));
        } else if (getFromDiskCache(pUrl) != null && getFromLruCache(pUrl) == null) {
            final File f = new File(mCacheDir, pUrl);
            final Bitmap bitmap = decodeFile(f);
            addToLruCache(pUrl, new BitmapDrawable(Resources.getSystem(), bitmap));
        } else if (getFromDiskCache(pUrl) == null) {
            loadImage(pUrl, pPhotoView);
        }
    }

    private Bitmap decodeFile(final File pFile) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(pFile), null, options);
            final int REQUIRED_SIZE = 70;
            int width_tmp = options.outWidth, height_tmp = options.outHeight;
            int scale = 1;

            while (width_tmp / 2 >= REQUIRED_SIZE && height_tmp / 2 >= REQUIRED_SIZE) {
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }

            final BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(pFile), null, options1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
