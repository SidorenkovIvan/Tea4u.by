package ru.SidorenkovIvan.MyApplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import static ru.SidorenkovIvan.MyApplication.ui.ProductPage.ProductPage.load;

public class ImageLoader {

    private final int availableMem = (int) (Runtime.getRuntime().maxMemory());
    private final int cacheMem = availableMem;
    public LruCache<String, ArrayList<BitmapDrawable>> lruCache = new LruCache<>(cacheMem);

    private final HashMap<String, ArrayList<BitmapDrawable>> diskCache = new HashMap<>();
    private File cacheDir;

    PhotosQueue photosQueue = new PhotosQueue();
    PhotosLoader photoLoaderThread = new PhotosLoader();

    ArrayList<BitmapDrawable> cacheImages = new ArrayList<>();

    private int countImage;
    private final String mId;
    private final String mImages;

    public void addCache(String key, ArrayList<BitmapDrawable> drawables) {
        if (getCache(key) == null)
            lruCache.put(key, drawables);
    }

    public ArrayList<BitmapDrawable> getCache(String key) {
        return lruCache.get(key);
    }

    public ImageLoader(Context context, String id, String images) {
        mId = id;
        mImages = images;

        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "CacheImages");
            Log.d("Memory", String.valueOf(cacheDir));
        }
        else {
            cacheDir = context.getCacheDir();
            Log.d("Memory1", String.valueOf(cacheDir));
        }
        if(!cacheDir.exists()) {
            Log.d("Memory2", String.valueOf(cacheDir));
            cacheDir.mkdirs();
        }
    }

    private void getImages(String images) {
        String imagesString = images.replace("|", " ").trim();
        String[] pImages = imagesString.split("[ ]");
        countImage = pImages.length;

        for (String s : pImages) queuePhoto(s);
    }

    static class PhotosQueue
    {
        private final Stack<PhotoToLoad> photosToLoad = new Stack<>();
    }

    private void queuePhoto(String url)
    {
        PhotoToLoad p = new PhotoToLoad(url);
        synchronized (photosQueue.photosToLoad) {
            photosQueue.photosToLoad.push(p);
            photosQueue.photosToLoad.notify();
        }

        if (photoLoaderThread.getState() == Thread.State.NEW)
            photoLoaderThread.start();
    }

    public BitmapDrawable getDrawable(String src) {
        try {
            URL url = new URL(src);
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

    private static class PhotoToLoad {
        public String mUrl;

        public PhotoToLoad(String url){
            mUrl = url;
        }
    }

    public void stopThread()
    {
        photoLoaderThread.interrupt();
    }

    class PhotosLoader extends Thread {
        public void run() {
            try {
                do {
                    if (photosQueue.photosToLoad.size() == 0)
                        synchronized (photosQueue.photosToLoad) {
                            photosQueue.photosToLoad.wait();
                        }
                    if (photosQueue.photosToLoad.size() != 0) {
                        PhotoToLoad photoToLoad;
                        synchronized (photosQueue.photosToLoad) {
                            photoToLoad = photosQueue.photosToLoad.pop();
                        }
                        cacheImages.add(getDrawable(photoToLoad.mUrl));
                    }
                    if (countImage == cacheImages.size()) {
                        addToCache();
                    }
                } while (!Thread.interrupted());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addToCache() {
        diskCache.put(mId, cacheImages);
        addCache(mId, cacheImages);
        stopThread();
        synchronized (load) {
            load.notify();
        }
    }

    public void isLoad() throws InterruptedException {
        if (diskCache.containsKey(mId) && getCache(mId) == null) {
            Log.d("Memory", String.valueOf(diskCache.containsKey(mId)));
            addCache(mId, diskCache.get(mId));
        } else if (!diskCache.containsKey(mId)) {
            getImages(mImages);
            synchronized (load) {
                load.wait();
            }
        } else {
            synchronized (load) {
                load.notify();
            }
        }
    }
}
