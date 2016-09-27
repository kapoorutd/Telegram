package org.telegram.socialuser;

import org.telegram.messenger.BuildConfig;

/**
 * This is Custom Log class it avoid logs on live build
 *
 */
public class Logger {

    private static final boolean ENABLE_LOGS = BuildConfig.DEBUG;

    public static void v(String TAG, String message) {
        if (ENABLE_LOGS) {
            android.util.Log.v(TAG, message);
        }
    }

    public static void v(String TAG, String message, Throwable t) {
        if (ENABLE_LOGS) {
            android.util.Log.v(TAG, message, t);
        }
    }

    public static void d(String TAG, String message) {
        if (ENABLE_LOGS) {
            android.util.Log.d(TAG, message);
        }
    }

    public static void d(String TAG, String message, Throwable t) {
        if (ENABLE_LOGS) {
            android.util.Log.d(TAG, message, t);
        }
    }

    public static void e(String TAG, String message) {
        if (ENABLE_LOGS) {
            android.util.Log.e(TAG, message);
        }
    }

    public static void e(String TAG, String message, Throwable t) {
        if (ENABLE_LOGS) {
            android.util.Log.e(TAG, message, t);
        }
    }

    public static void i(String TAG, String message) {
        if (ENABLE_LOGS) {
            android.util.Log.i(TAG, message);
        }
    }

    public static void i(String TAG, String message, Throwable t) {
        if (ENABLE_LOGS) {
            android.util.Log.i(TAG, message, t);
        }
    }

    public static void w(String TAG, String message) {
        if (ENABLE_LOGS) {
            android.util.Log.w(TAG, message);
        }
    }

    public static void w(String TAG, String message, Throwable t) {
        if (ENABLE_LOGS) {
            android.util.Log.w(TAG, message, t);
        }
    }

}

