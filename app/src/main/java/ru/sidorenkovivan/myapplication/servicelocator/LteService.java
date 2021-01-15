package ru.sidorenkovivan.myapplication.servicelocator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class LteService implements ConnectionService {

    @Override
    public boolean isConnected(final Context pContext) {
        final ConnectivityManager cm = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifiInfo != null && wifiInfo.isConnected();
    }

}
