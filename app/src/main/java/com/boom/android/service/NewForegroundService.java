//package com.boom.android.service;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Build;
//import android.os.IBinder;
//import androidx.core.app.NotificationCompat;
//
//import android.os.UserManager;
//import android.telephony.TelephonyManager;
//import android.view.View;
//import android.widget.Toast;
//
//import com.boom.android.R;
//import com.boom.model.interf.impl.ModelBuilderManager;
//
//public class NewForegroundService extends Service {
//    private static final String TAG = "MS.NewForegroundService";
//
//    public static final String ACTION_BASIC_INFO = "com.cisco.webex.meetings.service.ForegroundService.action.basicInfo";
//    public static final String ACTION_MIC_INFO = "com.cisco.webex.meetings.service.ForegroundService.action.micInfo";
//    private static final String ACTION_SELF_MIC = "com.cisco.webex.meetings.service.ForegroundService.action.selfMic";
//
//    private String strTopic = null;
//    private String strHostName = null;
//    private boolean isE2EMeeting = false;
//    private int micBtnVisibleState = View.GONE;
//    private boolean micBtnEnable = false;
//    private boolean micBtnMuteState = false;
//
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onStart(Intent intent, int startId) {
//        handleCommand(intent);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Logger.d(TAG, "onStartCommand : intent="+intent+", flags="+flags+", startId="+startId);
//        handleCommand(intent);
//        // We want this service to continue running until it is explicitly
//        // stopped, so return sticky.
//        return START_STICKY;
//    }
//
//    @Override
//    public void onCreate() {
//        userModel = ModelBuilderManager.getModelBuilder().getUserModel();
//        initButtonReceiver();
//
////        registerListener();
//    }
//
//    @Override
//    public void onDestroy() {
//        Logger.d(TAG, "onDestroy");
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            stopForeground(true);
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            if(notificationManager!=null){ //If this is the only notification on your channel
//                notificationManager.deleteNotificationChannel(FOREGROUND_CHANNEL_ID);
//            }
//        } else {
//            stopForeground(true);
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            if(notificationManager!=null){ //If this is the only notification on your channel
//                notificationManager.cancel(ForegroundServiceNotification_ID);
//            }
//
//        }
//
//        if(bReceiver != null){
//            unregisterReceiver(bReceiver);
//        }
//
//        super.onDestroy();
////        unregisterListener();
//    }
//    void handleCommand(Intent intent) {
//        Logger.d(TAG, "new foreground services handle Command");
//        if (intent == null) {
//            Logger.w(TAG, TAG + " handleCommand intent=null????");
//            return ;
//        }
//        String action = intent.getAction();
//        if(ACTION_MIC_INFO.equals(action)){
//            micBtnVisibleState = intent.getIntExtra("micBtnVisible",View.GONE);
//            micBtnEnable = intent.getBooleanExtra("isMicEnabled",false);
//            micBtnMuteState = intent.getBooleanExtra("isMuted",false);
//        }else if(ACTION_BASIC_INFO.equals(action)){
//            strTopic = intent.getStringExtra("strMeetingName");
//            strHostName = intent.getStringExtra("strHostName");
//            isE2EMeeting = intent.getBooleanExtra("isE2EMeeting", false);
//        }
//        showNotification();
//    }
//
//
//
//    private void showNotification() {
//        Intent clickIntent = new Intent(this, IntegrationInternalActivity.class);
//        clickIntent.setData(Uri.parse(IntegrationHelper.INTEGRATION_INTERNAL_SCHEME + "://return-to-meeting?rnd=" + System.currentTimeMillis()));
//        clickIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        boolean isCannotUnmuteMyselfForMC = false;
//        Notification serviceNotification;
//        if(userModel != null){
//            AppUser user = userModel.getCurrentUser();
//            isCannotUnmuteMyselfForMC = MeetingHelper.isInMainConf() && WbxAudioModel.isCannotUnmuteMyselfForMC(user);
//        }
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID);
//            builder.setWhen(0);
//            if (isE2EMeeting) {
//                if (AndroidHardwareUtils.isLollipopDevice()) {
//                    builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_notification_circle_status_locked));
//                } else {
//                    builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_notification_square_status_locked));
//                }
//            }
//            builder.setSmallIcon(R.drawable.ic_notification_status);
//            builder.setColor(getResources().getColor(R.color.primary_base));
//            builder.setContentTitle(strTopic);
//            builder.setContentText(strHostName);
//            builder.setContentIntent(contentIntent);
//
//            if(micBtnVisibleState == View.VISIBLE && micBtnEnable) {
//                if(enableNewNotificationStyle()) {
//                    androidx.media.app.NotificationCompat.MediaStyle style = new androidx.media.app.NotificationCompat.MediaStyle();
//                    Intent micBtnIntent = new Intent(ACTION_SELF_MIC);
//                    PendingIntent intent_mic = PendingIntent.getBroadcast(this, 1, micBtnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    if (isCannotUnmuteMyselfForMC) {
//                        builder.addAction(R.drawable.ic_btn_notification_hardmuted_svg, this.getResources().getString(R.string.HARD_MUTE_CLICK_TOAST), intent_mic);
//                    } else if (micBtnMuteState) {
//                        builder.addAction(R.drawable.ic_btn_notification_muted_svg, this.getResources().getString(R.string.PLIST_UNMUTE), intent_mic);
//                    } else {
//                        builder.addAction(R.drawable.ic_btn_notification_unmuted_svg, this.getResources().getString(R.string.PLIST_MUTE), intent_mic);
//                    }
//                    style.setShowActionsInCompactView(0);
//                    style.setShowCancelButton(true);
//                    builder.setStyle(style);
//                } else {
//                    Intent micBtnIntent = new Intent(ACTION_SELF_MIC);
//                    PendingIntent intent_mic = PendingIntent.getBroadcast(this, 1, micBtnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    if(micBtnMuteState){
//                        builder.addAction(R.drawable.ic_btn_notification_muted_svg, this.getResources().getString(R.string.PLIST_UNMUTE), intent_mic);
//                    }else{
//                        builder.addAction(R.drawable.ic_btn_notification_unmuted_svg, this.getResources().getString(R.string.PLIST_MUTE), intent_mic);
//                    }
//                }
//            }
//
//            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//            try {
//                serviceNotification = builder.build();
//                serviceNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
//
////            if(isStartService) {
//                startForeground(ForegroundServiceNotification_ID, serviceNotification);
////            } else {
////                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//////            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
////                nm.notify(ForegroundServiceNotification_ID, serviceNotification);
////            }
//            } catch (NullPointerException e) {
//                Logger.e(TAG, "NotificationCompat build fail", e);
//            }
//
//        } else {
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            NotificationChannel channel = notificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID);
//            if (channel == null) {
//                channel = new NotificationChannel(FOREGROUND_CHANNEL_ID, "MeetingService", NotificationManager.IMPORTANCE_LOW);
//                channel.setSound(null,null);
//                channel.enableLights(false);
//                channel.enableVibration(false);
//                notificationManager.createNotificationChannel(channel);
//            }
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel.getId());
//            builder.setWhen(0);
//            if (isE2EMeeting) {
//                if (AndroidHardwareUtils.isLollipopDevice()) {
//                    builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_notification_circle_status_locked));
//                } else {
//                    builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_notification_square_status_locked));
//                }
//            }
//            builder.setSmallIcon(R.drawable.ic_notification_status);
//            builder.setColor(getResources().getColor(R.color.primary_base));
//            builder.setContentTitle(strTopic);
//            builder.setContentText(strHostName);
//            builder.setContentIntent(contentIntent);
//            builder.setOnlyAlertOnce(true);
//            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//            if(micBtnVisibleState == View.VISIBLE && micBtnEnable) {
//                if(enableNewNotificationStyle()) {
//                    androidx.media.app.NotificationCompat.MediaStyle style = new androidx.media.app.NotificationCompat.MediaStyle();
//                    Intent micBtnIntent = new Intent(ACTION_SELF_MIC);
//                    PendingIntent intent_mic = PendingIntent.getBroadcast(this, 1, micBtnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    if (isCannotUnmuteMyselfForMC) {
//                        builder.addAction(R.drawable.ic_btn_notification_hardmuted_svg, this.getResources().getString(R.string.HARD_MUTE_CLICK_TOAST), intent_mic);
//                    } else if (micBtnMuteState) {
//                        builder.addAction(R.drawable.ic_btn_notification_muted_svg, this.getResources().getString(R.string.PLIST_UNMUTE), intent_mic);
//                    } else {
//                        builder.addAction(R.drawable.ic_btn_notification_unmuted_svg, this.getResources().getString(R.string.PLIST_MUTE), intent_mic);
//                    }
//                    style.setShowActionsInCompactView(0);
//                    style.setShowCancelButton(true);
//                    builder.setStyle(style);
//                } else {
//                    Intent micBtnIntent = new Intent(ACTION_SELF_MIC);
//                    PendingIntent intent_mic = PendingIntent.getBroadcast(this, 1, micBtnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    if(micBtnMuteState){
//                        builder.addAction(R.drawable.ic_btn_notification_muted_svg, this.getResources().getString(R.string.PLIST_UNMUTE), intent_mic);
//                    }else{
//                        builder.addAction(R.drawable.ic_btn_notification_unmuted_svg, this.getResources().getString(R.string.PLIST_MUTE), intent_mic);
//                    }
//                }
//            }
//
//            serviceNotification = builder.build();
//            serviceNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR;
//
//            startForeground(ForegroundServiceNotification_ID, serviceNotification);
//        }
//    }
//
//    private boolean enableNewNotificationStyle(){
//        //we ever found XiaoMi Build.VERSION.SDK = 30;Build.DEVICE = phoenixin Build.MODEL = POCO X2 not support new style
//        if(AndroidHardwareUtils.isXiaomiDevice()){
//            return false;
//        }
//        return true;
//    }
//
//    // Adopt system style notification. Comment the code for customize view. -- Dai Jun 2016-03-14 start
////    private void displayNotification(){
//////        stopForeground(true);
////
////        Notification notification = new Notification();
////        notification.when = 0;
////        if (isE2EMeeting) {
////            notification.icon = R.drawable.ic_notification_status_locked;  //ic_webex_ball_status_locked
////        }else{
////            notification.icon = R.drawable.ic_notification_status;  //webex_status_ball ic_webex_ball_status
////        }
////        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
////
////        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.zero_notificaiton_with_mic_button);
////        mRemoteViews.setTextViewText(R.id.tv_notification_meeting_topic,strTopic);
////        mRemoteViews.setTextViewText(R.id.tv_notification_meeting_host,strHostName);
////
////        Intent clickIntent = new Intent(this, IntegrationInternalActivity.class);
////        clickIntent.setData(Uri.parse(IntegrationHelper.INTEGRATION_INTERNAL_SCHEME + "://return-to-meeting?rnd=" + System.currentTimeMillis()));
////        clickIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
////        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
////
////        mRemoteViews.setOnClickPendingIntent(R.id.meeting_notification_icon,contentIntent);
////        mRemoteViews.setOnClickPendingIntent(R.id.tv_notification_meeting_body,contentIntent);
////
////        Intent micBtnIntent = new Intent(ACTION_SELF_MIC);
////        PendingIntent intent_mic = PendingIntent.getBroadcast(this, 1, micBtnIntent, PendingIntent.FLAG_UPDATE_CURRENT);
////
////        if(micBtnVisibleState == View.VISIBLE) {
////            if(micBtnEnabledState){
////                mRemoteViews.setViewVisibility(R.id.meeting_notification_mic_btn, micBtnVisibleState);
////
////            }else{
////                mRemoteViews.setViewVisibility(R.id.meeting_notification_mic_btn, View.GONE);
////            }
////        }else {
////            mRemoteViews.setViewVisibility(R.id.meeting_notification_mic_btn,micBtnVisibleState);
////        }
////
////        mRemoteViews.setBoolean(R.id.meeting_notification_mic_btn, "setEnabled", micBtnEnabledState);
////        mRemoteViews.setOnClickPendingIntent(R.id.meeting_notification_mic_btn, intent_mic);
////       // mRemoteViews.setViewVisibility(R.id.meeting_notification_mic_btn, micBtnVisibleState);
////        //mRemoteViews.setBoolean(R.id.meeting_notification_mic_btn,"setEnabled",micBtnEnabledState);
////      //  mRemoteViews.setOnClickPendingIntent(R.id.meeting_notification_mic_btn,intent_mic);
////
////        if(micBtnMuteState){
////            mRemoteViews.setImageViewResource(R.id.meeting_notification_mic_btn,R.drawable.zero_selector_button_toolbar_microphone_mute);
////        }else{
////            mRemoteViews.setImageViewResource(R.id.meeting_notification_mic_btn,R.drawable.zero_selector_button_toolbar_microphone_unmute);
////        }
////
////        notification.contentView = mRemoteViews;
////        startForeground(R.string.APPLICATION_SHORT_NAME, notification);
////    }
//    // Adopt system style notification. Comment the code for customize view. -- Dai Jun 2016-03-14 end
//
//
//    private ButtonBroadcastReceiver bReceiver;
//
//    private void initButtonReceiver(){
//        bReceiver = new ButtonBroadcastReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ACTION_SELF_MIC);
//        registerReceiver(bReceiver, intentFilter);
//    }
//
//    private boolean isEventCenter() {  //OK
//        IMeetingManager manager = MeetingManager.getInstance();
//        if (manager == null) {
//            return false;
//        }
//        ContextMgr contextmgr = manager.getContextMgr();
//        if (contextmgr == null) {
//            return false;
//        }
//        return contextmgr.isEventCenter();
//    }
//
//    private boolean isTrainingOrEventCenter() {  //OK
//        IMeetingManager manager = MeetingManager.getInstance();
//        if (manager == null) {
//            return false;
//        }
//        ContextMgr contextmgr = manager.getContextMgr();
//        if (contextmgr == null) {
//            return false;
//        }
//        return contextmgr.isTrainingOrEventCenter();
//    }
//
//    private boolean isMeetingCenter() {  //OK
//        IMeetingManager manager = MeetingManager.getInstance();
//        if (manager == null) {
//            return false;
//        }
//        ContextMgr contextmgr = manager.getContextMgr();
//        if (contextmgr == null) {
//            return false;
//        }
//        return contextmgr.isMeetingCenter();
//    }
//
//
//    public void showBlockUnmuteInCallToast() {
//        if(IntegrationHelper.shouldInLobbyOrLockRoom()){
//            return;
//        }
//        Toast.makeText(getApplicationContext(), getString(R.string.BLOCK_UNMUTE_MSG), Toast.LENGTH_SHORT).show();
//    }
//
//    public void showMutedForNoiseHowUnmuteToast() {
//        if(IntegrationHelper.shouldInLobbyOrLockRoom()){
//            return;
//        }
//        Toast.makeText(getApplicationContext(), getString(R.string.MUTED_MESSAGE_FOR_NOISE_HOW_UNMUTE), Toast.LENGTH_SHORT).show();
//    }
//
//    private class ButtonBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(action.equals(ACTION_SELF_MIC)){
//                onMute();
//            }
//        }
//    }
//}
