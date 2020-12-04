package ru.SidorenkovIvan.MyApplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;
import static ru.SidorenkovIvan.MyApplication.ui.ProductPage.ProductPage.load;

public class ImageLoader {
    PhotosQueue photosQueue = new PhotosQueue();
    PhotosLoader photoLoaderThread = new PhotosLoader();

    ArrayList<Bitmap> cacheImages = new ArrayList<>();

    private int countImage;
    private final String mId;
    private final String mImages;

    public ImageLoader(String id, String images) {
        mId = id;
        mImages = images;
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

    public static Bitmap getBitmap(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
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
                        Bitmap bmp = getBitmap(photoToLoad.mUrl);
                        cacheImages.add(bmp);
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
        ForCache.addLargeImagesToMemoryCache(mId, cacheImages);
        stopThread();
        synchronized (load) {
            load.notify();
        }
    }

    public void isLoad() throws InterruptedException {
        if (ForCache.getLargeImagesFromMemoryCache(mId) == null) {
            getImages(mImages);
            synchronized (load) {
                load.wait();
            }
        } else {
            synchronized (load) {
                load.notifyAll();
            }
        }
    }
}
