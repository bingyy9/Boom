package com.boom.android.log;

public interface ILog {

    void dump(int level, String tag, String msg, Throwable throwable);

}
