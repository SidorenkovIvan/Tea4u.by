package ru.sidorenkovivan.myapplication.servicelocator;

import android.util.LruCache;

public class Cache {

    private final int mAvailableMem = (int) (Runtime.getRuntime().maxMemory() / 32);
    private final LruCache<String, ConnectionService> mLruCache = new LruCache<>(mAvailableMem);

    public ConnectionService getService(final String pServiceName) {
        return mLruCache.get(pServiceName);
    }

    public void addService(final String pServiceName, final ConnectionService pConnectionService) {
        mLruCache.put(pServiceName, pConnectionService);
    }
}
