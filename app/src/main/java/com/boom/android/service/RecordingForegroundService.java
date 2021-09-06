package com.boom.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.boom.android.BoomApplication;
import com.boom.android.MainActivity;
import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.util.AndroidHardwareUtils;
import com.boom.android.util.NotificationUtils;
import com.boom.android.util.RecordHelper;

import androidx.core.app.NotificationCompat;

public class RecordingForegroundService extends Service {
    private static final String TAG = "WirelessShareForegroundService";
    public static final String FOREGROUND_CHANNEL_ID = "Boom Record";
    public static final int ForegroundServiceNotification_ID = 1000;
    public static final String FOREGROUND_CHANNEL_NAME = "Recording service";
    public static final String NOTIFICATION_ACTION = "NOTIFICATION_ACTION";
    public static final String STOP = "stop";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Dogger.i(Dogger.BOOM, "flags: " + flags + " startId: " + startId, "WirelessShareForegroundService", "onStartCommand");
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDestroy() {
        Dogger.i(Dogger.BOOM, "", "WirelessShareForegroundService", "onDestroy");
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
        Dogger.i(Dogger.BOOM, "", "WirelessShareForegroundService", "handleCommand");
        if (intent == null) {
            Dogger.i(Dogger.BOOM, "intent is null", "WirelessShareForegroundService", "handleCommand");
            return ;
        }
        try {
            showNotification();
        } catch (Exception e){
            Dogger.e(Dogger.BOOM, "", "RecordingForegroundService", "handleCommand", e);
        }
    }

    private PendingIntent returnToAppIntent(){
        Intent clickIntent = new Intent(this, MainActivity.class);
        clickIntent.putExtra("notification_id", ForegroundServiceNotification_ID);
        clickIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent returnToAppAndStopRecordingIntent(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("notification_id", ForegroundServiceNotification_ID);
        intent.putExtra(NOTIFICATION_ACTION, STOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
            builder.setContentIntent(returnToAppIntent());
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
            builder.setContentIntent(returnToAppIntent());
            builder.setChannelId(FOREGROUND_CHANNEL_ID);
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            builder.setContent(getComplexNotificationView());
            serviceNotification = builder.build();
            serviceNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR;
            startForeground(ForegroundServiceNotification_ID, serviceNotification);
        }
    }

    private RemoteViews getComplexNotificationView() {
        RemoteViews notificationView = new RemoteViews(
                BoomApplication.getInstance().getPackageName(),
                R.layout.notification_recording
        );
        notificationView.setOnClickPendingIntent(R.id.iv_stop,returnToAppAndStopRecordingIntent());
        return notificationView;
        // Locate and set the Image into customnotificationtext.xml ImageViews
//        notificationView.setImageViewResource(
//                R.id.stop_btn,
//                R.drawable.ic_record_stop);
//        // Locate and set the Text into customnotificationtext.xml TextViews
////        notificationView.setTextViewText(R.id.title, getTitle());
////        notificationView.setTextViewText(R.id.text, getText());
    }

    private void stopRecording(){
        Dogger.i(Dogger.BOOM, "", "RecordingForegroundService", "stopRecording");
        if(BoomApplication.getInstance().getMediaRecordService() != null){
            BoomApplication.getInstance().getMediaRecordService().stopRecord();
        }
        RecordHelper.setRecordingStop(true);
    }

    private boolean enableNewNotificationStyle(){
        //we ever found XiaoMi Build.VERSION.SDK = 30;Build.DEVICE = phoenixin Build.MODEL = POCO X2 not support new style
        if(AndroidHardwareUtils.isXiaomiDevice()){
            return false;
        }
        return true;
    }
}
