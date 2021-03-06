package com.hilllander.khunzohn.gpstracker.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;

/**
 *Created by khunzohn on 1/8/16.
 */
public class NetworkUtil {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return null != manager.getActiveNetworkInfo() && manager.getActiveNetworkInfo().isConnected();
    }

    public static boolean isGpsAvailable(Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
