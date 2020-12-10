package ru.SidorenkovIvan.MyApplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;

import com.rd.PageIndicatorView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ImageLoader {

    private final int mAvailableMem = (int) (Runtime.getRuntime().maxMemory());
    private final LruCache<String, BitmapDrawable> mLruCache = new LruCache<>(mAvailableMem);

    private final HashMap<String, BitmapDrawable> mDiskCache = new HashMap<>();
    private final File mCacheDir;

    private final String TAG = "Thread";
    private LoadImage mLoadImage;
    private ViewPagerAdapter mViewPagerAdapter;
    private PageIndicatorView mPageIndicatorView;
    private int mCountImage;
    private int mLoaded;

    public ImageLoader(final Context pContext) {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            mCacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "CacheImages");
        } else {
            mCacheDir = pContext.getCacheDir();
        }
        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
    }

    private void addToLruCache(final String pKey, final BitmapDrawable pBitmapDrawable) {
        if (getFromLruCache(pKey) == null)
            mLruCache.put(pKey, pBitmapDrawable);
    }

    private BitmapDrawable getFromLruCache(final String pKey) {
        return mLruCache.get(pKey);
    }

    private void addToDiskCache(final String pKey, final BitmapDrawable pBitmapDrawable) {
        if (getFromDiskCache(pKey) == null)
            mDiskCache.put(pKey, pBitmapDrawable);
    }

    private BitmapDrawable getFromDiskCache(final String pKey) {
        return mDiskCache.get(pKey);
    }

    private String[] getImagesUrls(final String pImages) {
        String imagesString = pImages.replace("|", " ").trim();
        String[] imagesUrls = imagesString.split("[ ]");
        mCountImage = imagesUrls.length;
        return imagesUrls;
    }

    private BitmapDrawable getDrawable(final String pUrl) {
        try {
            URL url = new URL(pUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return new BitmapDrawable(Resources.getSystem(), bitmap);
        } catch (IOException e) {
            return null;
        }
    }

    private class LoadImage extends Thread {
        String mUrl;

        public LoadImage(final String pUrl) {
            mUrl = pUrl;
        }

        public void run() {
            BitmapDrawable bitmapDrawable = getDrawable(mUrl);

            addToDiskCache(mUrl, bitmapDrawable);
            addToLruCache(mUrl, bitmapDrawable);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                mViewPagerAdapter.add(bitmapDrawable);
                mPageIndicatorView.setCount(mViewPagerAdapter.getCount());
            });
            mLoaded += 1;
            Log.i(TAG, " " + mCountImage + "-" +
                    mLoaded + "  " + mLoadImage.getState());
            if (mCountImage == mLoaded) {
                stopThread();
            }
        }
    }

    private void stopThread() {
        mLoadImage.interrupt();
        Log.i(TAG, " " + mLoadImage.getState());
    }

    public void isLoad(final String pImages, final ViewPagerAdapter pViewPagerAdapter, final PageIndicatorView pPageIndicatorView) {
        mViewPagerAdapter = pViewPagerAdapter;
        mPageIndicatorView = pPageIndicatorView;
        mLoaded = 0;

        for (String url : getImagesUrls(pImages)) {
            if (getFromLruCache(url) != null) {
                mViewPagerAdapter.add(getFromLruCache(url));
            } else if (getFromDiskCache(url) != null && getFromLruCache(url) == null) {
                File f = new File(mCacheDir, url);

                Bitmap bitmap = decodeFile(f);
                addToLruCache(url, new BitmapDrawable(Resources.getSystem(), bitmap));
            } else if (getFromDiskCache(url) == null) {
                mLoadImage = new LoadImage(url);
                mLoadImage.start();
            }
        }
    }

    private Bitmap decodeFile(final File pFile) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(pFile), null, o);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (width_tmp / 2 >= REQUIRED_SIZE && height_tmp / 2 >= REQUIRED_SIZE) {
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(pFile), null, o2);
        } catch (FileNotFoundException ignored) {
        }
        return null;
    }
}
