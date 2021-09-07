package com.boom.android.util;


import android.util.Log;

import com.boom.android.BuildConfig;
import com.boom.android.log.Dogger;
import com.boom.android.log.ILog;

public class AndroidLogger implements ILog {
    @Override
    public void dump(int level, String tag, String msg, Throwable throwable) {
        if(!BuildConfig.DEBUG){
//            Log.i(tag, "not log to logcat");
            //release version not log to logcat
            if(level == Dogger.DEBUG){
                //not print debug log to file
                return;
            }
            StringBuffer stringBuffer = new StringBuffer()
                    .append("[TID:")
                    .append(Thread.currentThread().getId())
                    .append("]")
                    .append(tag)
                    .append(msg);
            if(throwable != null){
                stringBuffer.append(throwable);
            }
            Log.i(tag, stringBuffer.toString());
            LogToFileUtils.write(stringBuffer.toString());
            return;
        }

        StringBuffer stringBuffer = new StringBuffer()
//                .append("[TID:")
//                .append(Thread.currentThread().getId())
//                .append("]")
                .append(tag)
                .append(msg);
        if(throwable != null){
            stringBuffer.append(throwable);
        }
        LogToFileUtils.write(stringBuffer.toString());
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
