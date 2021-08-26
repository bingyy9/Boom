package com.boom.android.util;


import android.util.Log;

import com.boom.android.log.Dogger;
import com.boom.android.log.ILog;

public class AndroidLogger implements ILog {
    @Override
    public void dump(int level, String tag, String msg, Throwable throwable) {
        switch (level) {
            case Dogger.DEBUG:
                Log.d(tag, msg, throwable);
                break;
            case Dogger.INFO:
                Log.i(tag, msg, throwable);
                break;
            case Dogger.WARN:
                Log.w(tag, msg, throwable);
                break;
            case Dogger.ERROR:
                Log.e(tag, msg, throwable);
                break;
            default:
                Log.d(tag, msg, throwable);
                break;
        }
    }
}
