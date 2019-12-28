package com.seagazer.lib.util;

import android.util.Log;

import com.seagazer.lib.BuildConfig;

public class Logger {

    private static final String TAG = "Seagazer";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static void d(String message) {
        if (DEBUG) {
            StackTraceElement[] element = Thread.currentThread().getStackTrace();
            if (element.length > 4) {
                StackTraceElement e = element[3];
                Log.d(TAG, e.getClassName() + " # " + e.getMethodName() + "[line:" + e.getLineNumber() + "]: " + message);
            }
        }
    }

    public static void i(String message) {
        if (DEBUG) {
            StackTraceElement[] element = Thread.currentThread().getStackTrace();
            if (element.length > 4) {
                StackTraceElement e = element[3];
                Log.i(TAG, e.getClassName() + " # " + e.getMethodName() + "[line:" + e.getLineNumber() + "]: " + message);
            }
        }
    }

    public static void e(String message) {
        if (DEBUG) {
            StackTraceElement[] element = Thread.currentThread().getStackTrace();
            if (element.length > 4) {
                StackTraceElement e = element[3];
                Log.e(TAG, e.getClassName() + " # " + e.getMethodName() + "[line:" + e.getLineNumber() + "]: " + message);
            }
        }
    }

    public static void w(String message) {
        if (DEBUG) {
            StackTraceElement[] element = Thread.currentThread().getStackTrace();
            if (element.length > 4) {
                StackTraceElement e = element[3];
                Log.w(TAG, e.getClassName() + " # " + e.getMethodName() + "[line:" + e.getLineNumber() + "]: " + message);
            }
        }
    }

}
