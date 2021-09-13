package com.boom.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RemoteViews;

import com.boom.android.BoomApplication;
import com.boom.android.MainActivity;
import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.util.AndroidHardwareUtils;
import com.boom.android.util.RecordHelper;
import com.boom.utils.StringUtils;

import androidx.core.app.NotificationCompat;

public class RecordingForegroundService extends Service {
    private static final String TAG = "WirelessShareForegroundService";
    public static final String FOREGROUND_CHANNEL_ID = "Boom Record";
    public static final int ForegroundServiceNotification_ID = 1000;
    public static final String FOREGROUND_CHANNEL_NAME = "Recording service";
    public static final String NOTIFICATION_ACTION = "NOTIFICATION_ACTION";
    public static final String STOP = "stop";
    public static final String PAUSE = "pause";
    public static final String RESUME = "resume";

    @Override
    public IBinder onBind(Intent intent) {
        Dogger.i(Dogger.BOOM, "", "RecordingForegroundService", "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Dogger.i(Dogger.BOOM, "flags: " + flags + " startId: " + startId, "RecordingForegroundService", "onStartCommand");
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Dogger.i(Dogger.BOOM, "", "RecordingForegroundService", "onCreate");
    }

    @Override
    public void onDestroy() {
        Dogger.i(Dogger.BOOM, "", "RecordingForegroundService", "onDestroy");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            stopForeground(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(notificationManager!=null){ //If this is the only notification on your channel
                notificationManager.deleteNotificationChannel(FOREGROUND_CHANNEL_ID);
            }
        } else {
            stopForeground(true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(notificationManager!=null){ //If this is the only notification on your channel
                notificationManager.cancel(ForegroundServiceNotification_ID);
            }

        }

        super.onDestroy();
    }
    void handleCommand(Intent intent) {
        Dogger.i(Dogger.BOOM, "", "RecordingForegroundService", "handleCommand");
        if (intent == null) {
            Dogger.i(Dogger.BOOM, "intent is null", "RecordingForegroundService", "handleCommand");
            return ;
        }
        try {
            showNotification();
        } catch (Exception e){
            Dogger.e(Dogger.BOOM, "", "RecordingForegroundService", "handleCommand", e);
        }
    }

    private PendingIntent returnToApp(String action){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notification_id", ForegroundServiceNotification_ID);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if(StringUtils.contentEquals(action, RecordingForegroundService.STOP)){
            intent.putExtra(NOTIFICATION_ACTION, action);
            return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else if(StringUtils.contentEquals(action, RecordingForegroundService.PAUSE)){
            Intent serviceIntent = new Intent(action, null, this, MediaRecordService.class);
            return PendingIntent.getService(this, 2, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else if(StringUtils.contentEquals(action, RecordingForegroundService.RESUME)){
            Intent serviceIntent = new Intent(action, null, this, MediaRecordService.class);
            return PendingIntent.getService(this, 3, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            //just return to app
            return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    private void showNotification() {
        Notification serviceNotification;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID);
            builder.setWhen(System.currentTimeMillis());
            builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_1));
            builder.setSmallIcon(R.drawable.ic_1);
            builder.setColor(getResources().getColor(R.color.blue_normal));
            builder.setContentTitle(getResources().getString(R.string.app_name));
            builder.setContentText(getResources().getString(R.string.recording_notification));
            builder.setContentIntent(returnToApp(null));
            builder.setContent(getComplexNotificationView());
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            try {
                serviceNotification = builder.build();
                serviceNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
                startForeground(ForegroundServiceNotification_ID, serviceNotification);
            } catch (Exception e) {
                Dogger.e(Dogger.BOOM, "", "RecordingForegroundService", "showNotification", e);
            }
        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = notificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(FOREGROUND_CHANNEL_ID, FOREGROUND_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel.getId());
            builder.setWhen(System.currentTimeMillis());
            builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_1));
            builder.setSmallIcon(R.drawable.ic_1);
            builder.setColor(getResources().getColor(R.color.blue_normal));
            builder.setContentTitle(getResources().getString(R.string.app_name));
            builder.setContentText(getResources().getString(R.string.recording_notification));
            builder.setContentIntent(returnToApp(null));
            builder.setChannelId(FOREGROUND_CHANNEL_ID);
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            builder.setContent(getComplexNotificationView());
            serviceNotification = builder.build();
            serviceNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR;
            startForeground(ForegroundServiceNotification_ID, serviceNotification);
        }
    }

    private RemoteViews getComplexNotificationView() {
        RemoteViews remoteViews = new RemoteViews(
                BoomApplication.getInstance().getPackageName(),
                R.layout.notification_recording
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //support pause/resume
            if(RecordHelper.isRecordingPaused()){
                remoteViews.setImageViewResource(R.id.iv_pause, R.drawable.ic_start);
                remoteViews.setOnClickPendingIntent(R.id.iv_pause, returnToApp(RESUME));
            } else {
                remoteViews.setImageViewResource(R.id.iv_pause, R.drawable.ic_pause);
                remoteViews.setOnClickPendingIntent(R.id.iv_pause, returnToApp(PAUSE));
            }
            remoteViews.setViewVisibility(R.id.iv_pause, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.iv_pause, View.GONE);
        }

        remoteViews.setOnClickPendingIntent(R.id.iv_stop, returnToApp(STOP));
        return remoteViews;
        // Locate and set the Image into customnotificationtext.xml ImageViews
//        notificationView.setImageViewResource(
//                R.id.stop_btn,
//                R.drawable.ic_record_stop);
//        // Locate and set the Text into customnotificationtext.xml TextViews
////        notificationView.setTextViewText(R.id.title, getTitle());
////        notificationView.setTextViewText(R.id.text, getText());
    }

    private boolean enableNewNotificationStyle(){
        //we ever found XiaoMi Build.VERSION.SDK = 30;Build.DEVICE = phoenixin Build.MODEL = POCO X2 not support new style
        if(AndroidHardwareUtils.isXiaomiDevice()){
            return false;
        }
        return true;
    }

    private void pauseRecording(){
        if(BoomApplication.getInstance().getMediaRecordService() != null){
            BoomApplication.getInstance().getMediaRecordService().pauseRecord();
        }
    }

    private void resumeRecording(){
        if(BoomApplication.getInstance().getMediaRecordService() != null){
            BoomApplication.getInstance().getMediaRecordService().resumeRecord();
        }
    }
}
