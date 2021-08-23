package com.boom.android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.boom.android.service.MediaRecordService;

public class BoomApplication extends Application {

    private static BoomApplication application;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        application = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        startService(new Intent(this, MediaRecordService.class));
    }

    public static BoomApplication getInstance() {
        return application;
    }
}
