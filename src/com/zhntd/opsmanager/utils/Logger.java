
package com.zhntd.opsmanager.utils;

import android.util.Log;

/**
 * @author ntd
 * @date Nov 6, 2014
 * @time 5:39:04 PM
 */
public class Logger {

    private static final String TAG = "com.nick.logger.tag";
    private static final boolean DEBUG = true;

    /**
     * @param tag
     * @param log
     */
    public static void logger(String tag, String log) {
        if (DEBUG)
            Log.i(tag, log);
    }

    /**
     * @param log
     */
    public static void logger(String log) {
        logger(TAG, log);
    }

}
