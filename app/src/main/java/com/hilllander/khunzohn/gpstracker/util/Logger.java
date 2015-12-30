package com.hilllander.khunzohn.gpstracker.util;

import android.util.Log;

import com.hilllander.khunzohn.gpstracker.BuildConfig;

/**
 * Created by khunzohn on 12/30/15.
 */
public class Logger {
    private static final String PREFIX = "hill_";
    private static final int MAX_L = 23;

    public static String generateTag(Class<?> mClass) {
        String tag = PREFIX;
        tag += mClass.getSimpleName();
        if (tag.length() > MAX_L) {
            tag = tag.substring(0, MAX_L - 1);
        }
        return tag;
    }

    public static void d(String tag, String message) {

        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message);
        }
    }
}
