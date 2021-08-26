package com.boom.android.util;

import android.content.Context;

import com.boom.android.log.ILog;
import com.boom.android.log.IPlatformFactory;

public class AndroidFactory implements IPlatformFactory {

    public AndroidFactory(Context context) {
    }

    private ILog mLogger = new AndroidLogger();

    public ILog getLog() {
        return mLogger;
    }

}
