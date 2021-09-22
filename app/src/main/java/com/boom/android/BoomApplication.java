package com.boom.android;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Debug;
import android.os.IBinder;

import com.boom.android.crash.CrashHandler;
import com.boom.android.log.Dogger;
import com.boom.android.log.FactoryMgr;
import com.boom.android.service.MediaRecordService;
import com.boom.android.util.AndroidFactory;
import com.boom.android.util.AndroidMemoryMonitor;
import com.boom.android.util.ConfigUtil;
import com.boom.android.util.LogToFileUtils;
import com.boom.android.util.NotificationUtils;
import com.boom.model.interf.impl.ModelBuilderImpl;
import com.boom.model.interf.impl.ModelBuilderManager;
import com.boom.utils.StringUtils;
import com.google.android.gms.ads.MobileAds;
//import com.tencent.bugly.crashreport.CrashReport;

import java.util.concurrent.ThreadPoolExecutor;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

public class BoomApplication extends MultiDexApplication{

    private static BoomApplication application;
    private MediaRecordService mediaRecordService;
    private MediaRecordServiceConnection mediaRecordServiceConnection = null;
    private ConfigUtil configUtil;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        application = this;
//        Multidex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        if(!BuildConfig.DEBUG){
//            CrashReport.initCrashReport(getApplicationContext(), "46d8f339e6", false);
//        } else {
//            CrashReport.initCrashReport(getApplicationContext(), "46d8f339e6", true);
//        }

        LogToFileUtils.init(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        configUtil = ConfigUtil.getInstance();
        configUtil.initCameraIds(this);
        ModelBuilderManager.setModelBuilder(new ModelBuilderImpl());
        ModelBuilderManager.initModel();
        FactoryMgr.iPlatformFactory = new AndroidFactory(this);
        startMediaRecordService();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stopMediaRecordService();
        NotificationUtils.removeRecordingNotification(this);
    }

    public static BoomApplication getInstance() {
        return application;
    }

    private ServiceConnection recordServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MediaRecordService.RecordBinder binder = (MediaRecordService.RecordBinder) service;
            mediaRecordService = binder.getRecordService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    private class MediaRecordServiceConnection implements ServiceConnection {  //OK
        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder service) {
            MediaRecordService.RecordBinder binder = (MediaRecordService.RecordBinder) service;
            mediaRecordService = binder.getRecordService();
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
        }
    }

    private void startMediaRecordService(){
        if (mediaRecordServiceConnection == null) {
            mediaRecordServiceConnection = new MediaRecordServiceConnection();
        }

        Intent intent = new Intent();
        intent.setClassName(getPackageName(), MediaRecordService.class.getName());
        bindService(intent, mediaRecordServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopMediaRecordService(){
        if (mediaRecordServiceConnection != null) {
            unbindService(mediaRecordServiceConnection);
            mediaRecordServiceConnection = null;
        }
    }

    public MediaRecordService getMediaRecordService(){
        return mediaRecordService;
    }
}
