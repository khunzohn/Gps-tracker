package com.hilllander.khunzohn.gpstracker.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.hilllander.khunzohn.gpstracker.BuildConfig;
import com.hilllander.khunzohn.gpstracker.database.model.Device;

/**
 *Created by khunzohn on 12/30/15.
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
        Throwable throwable = new Throwable();
        StackTraceElement[] e = throwable.getStackTrace();
        String methodName = e[1].getMethodName();
        if (BuildConfig.DEBUG) {
            Log.d(tag, " [" + methodName + "] " + message);
        }
    }

    public static void e(String tag, String message) {
        Throwable throwable = new Throwable();
        StackTraceElement[] e = throwable.getStackTrace();
        String methodName = e[1].getMethodName();
        if (BuildConfig.DEBUG) {
            Log.e(tag, " [" + methodName + "] " + message);
        }
    }

    public static void w(String tag, String message) {
        Throwable throwable = new Throwable();
        StackTraceElement[] e = throwable.getStackTrace();
        String methodName = e[1].getMethodName();
        if (BuildConfig.DEBUG) {
            Log.w(tag, " [" + methodName + "] " + message);
        }
    }

    public static void i(String tag, String message) {
        Throwable throwable = new Throwable();
        StackTraceElement[] e = throwable.getStackTrace();
        String methodName = e[1].getMethodName();
        if (BuildConfig.DEBUG) {
            Log.i(tag, " [" + methodName + "] " + message);
        }
    }

    public static void logDevice(String tag, @NonNull Device device) {
        Throwable throwable = new Throwable();
        StackTraceElement[] e = throwable.getStackTrace();
        String methodName = e[1].getMethodName();
        if (BuildConfig.DEBUG) {
            Log.d(tag, "[" + methodName + "]" +
                    "Id : " + device.getId() + "\n" +
                    "Name : " + device.getDeviceName() + "\n" +
                    "Sim : " + device.getSimNumber());
        }
    }
}
