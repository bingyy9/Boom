package com.boom.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.boom.android.MainActivity;
import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.RecordHelper;
import com.boom.camera.CameraHelper;
import com.boom.camera.CameraListener;
import com.boom.camera.RoundBorderView;
import com.boom.camera.RoundTextureView;
import com.boom.model.interf.IRecordModel;
import com.boom.model.repo.RecordEvent;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MediaRecordService extends Service implements ViewTreeObserver.OnGlobalLayoutListener
        , CameraListener
        , IRecordModel.RecordEvtListener {
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;

    private int width = 720;
    private int height = 1080;
    private int dpi;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View counterRootView;
    private final int INIT_COUNT_DOWN = 3;
    private ImageView counterView;
    private Timer mCounterTimer;
    private int mCounter = INIT_COUNT_DOWN;

    private View cameraRootView;
    private RoundTextureView cameraView;
    private CameraHelper cameraHelper;
    private RoundBorderView roundBorderView;
    private static final int CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private Camera.Size previewSize;
    private GestureDetector mGestureDetector;

    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread serviceThread = new HandlerThread("service_thread",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        RecordHelper.registerRecordEventListner(this);
        serviceThread.start();
        RecordHelper.setRecording(false);
        mediaRecorder = new MediaRecorder();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RecordHelper.unregisterRecordEventListener(this);
        clearWindow();
    }

    public void setMediaProject(MediaProjection project) {
        mediaProjection = project;
    }

    public void setConfig(int width, int height, int dpi) {
        this.width = width;
        this.height = height;
        this.dpi = dpi;
    }

    public boolean startRecord() {
        if (mediaProjection == null || RecordHelper.isRecording()) {
            return false;
        }

        initRecorder();
        createVirtualDisplay();
        mediaRecorder.start();
        RecordHelper.setRecording(true);
        return true;
    }

    public void stopRecord() {
        if (!RecordHelper.isRecording()) {
            return;
        }
        RecordHelper.setRecording(false);
        mediaRecorder.stop();
        mediaRecorder.reset();
        virtualDisplay.release();
        mediaProjection.stop();
        clearWindow();
    }

    private void clearWindow(){
        if(cameraHelper != null){
            cameraHelper.release();
        }

        removeView(counterRootView);
        removeView(cameraRootView);
    }

    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(BoomHelper.getRecordDirectory() + System.currentTimeMillis() + ".mp4");
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        mediaRecorder.setVideoFrameRate(30);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class RecordBinder extends Binder {
        public MediaRecordService getRecordService() {
            return MediaRecordService.this;
        }
    }

    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        Intent nfIntent = new Intent(this, MainActivity.class); //点击后跳转的界面，可以设置跳转数据

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                //.setContentTitle("SMI InstantView") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("is running......") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        /*以下是对Android 8.0的适配*/
        //普通notification适配
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }
        //前台服务notification适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(110, notification);
    }


    private void updateCounterView(){
        if(counterView == null){
            return;
        }
        if(mCounter == 3){
            counterView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_3));
        } else if(mCounter == 2){
            counterView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_2));
        } else if(mCounter == 1){
            counterView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_1));
        }
    }

    private void stopTimer(){
        if (mCounterTimer != null) {
            mCounterTimer.cancel();
            mCounterTimer = null;
        }
    }

    private void startTimer(){
        stopTimer();
        if (mCounterTimer == null) {
            mCounterTimer = new Timer();
            mCounterTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(mCounter > 1){
                        mCounter--;
                        updateCounterView();
                    } else {
                        mCounter = INIT_COUNT_DOWN;
                        stopTimer();
                        removeView(counterRootView);
                        RecordHelper.setReadyToRecord(true);
                        realStartRecord();
                    }
                }
            }, 1000, 1000);
        }
    }

    private void removeView(View view){
        if(view != null && windowManager != null){
            windowManager.removeView(view);
            view = null;
        }
    }

    public void showCounterFloatingWindow() {
        if (BoomHelper.ensureDrawOverlayPermission(this)) {
            if(RecordHelper.isCountDowning()){
                Dogger.i(Dogger.BOOM, "isCountingDown, ignore", "FloatingCounterService", "showFloatingWindow");
                return;
            }
            RecordHelper.setCountDowning(true);

            layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            LayoutInflater layoutInflater = LayoutInflater.from(this);
            counterRootView = layoutInflater.inflate(R.layout.counter_display, null);
            counterView = counterRootView.findViewById(R.id.iv_counter);
            windowManager.addView(counterRootView, layoutParams);
            mCounter = INIT_COUNT_DOWN;
            updateCounterView();
            startTimer();
        }
    }

    public void showCameraFloatingWindow() {
        if (BoomHelper.ensureDrawOverlayPermission(this)) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.x = 0;
            layoutParams.y = 150;

            LayoutInflater layoutInflater = LayoutInflater.from(this);
            cameraRootView = layoutInflater.inflate(R.layout.video_display, null);
            cameraRootView.setOnTouchListener(new MediaRecordService.FloatingOnTouchListener());
            cameraView = cameraRootView.findViewById(R.id.texture_preview);
            cameraView.getViewTreeObserver().addOnGlobalLayoutListener(this);
            windowManager.addView(cameraRootView, layoutParams);
            mGestureDetector = new GestureDetector(this, new MyOnGestureListener());
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    addRoundBorder();
                    initialX = layoutParams.x;
                    initialY = layoutParams.y;

                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    layoutParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                    layoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(view, MediaRecordService.this.layoutParams);
                    break;
                case MotionEvent.ACTION_UP:
                    removeRoundBorder();
//                    if(Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1){
//                        isMove = true;
//                    }
//                    int Xdiff = (int) (event.getRawX() - initialTouchX);
//                    int Ydiff = (int) (event.getRawY() - initialTouchY);
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private void addRoundBorder(){
        if(roundBorderView == null) {
            roundBorderView = new RoundBorderView(MediaRecordService.this);
        }
        roundBorderView.setRadius(Math.min(cameraView.getWidth(), cameraView.getHeight()) >> 1);
        roundBorderView.turnRound();
        ((FrameLayout) cameraView.getParent()).addView(roundBorderView, cameraView.getLayoutParams());
    }

    private void removeRoundBorder(){
        if(roundBorderView != null) {
            ((FrameLayout) cameraView.getParent()).removeView(roundBorderView);
        }
    }

    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }
    }

    void initCamera() {
        cameraHelper = new CameraHelper.Builder()
                .cameraListener(this)
                .specificCameraId(CAMERA_ID)
                .previewOn(cameraView)
                .previewViewSize(new Point(cameraView.getLayoutParams().width, cameraView.getLayoutParams().height))
                .rotation(windowManager.getDefaultDisplay().getRotation())
                .build();
        cameraHelper.start();
    }

    @Override
    public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
        previewSize = camera.getParameters().getPreviewSize();
        Dogger.i(Dogger.BOOM, "previewSize = " + previewSize.width + "x" + previewSize.height, "FloatingCameraService", "onCameraOpened");
        if(cameraView == null){
            return;
        }

        cameraView.post(()->{
            if(cameraView == null){
                return;
            }
            ViewGroup.LayoutParams layoutParams = cameraView.getLayoutParams();
            if (displayOrientation % 180 == 0) {
                //landscape
                layoutParams.height = layoutParams.width * previewSize.height / previewSize.width;
            } else {
                //portrait
                layoutParams.height = layoutParams.width * previewSize.width / previewSize.height;
            }
            cameraView.setLayoutParams(layoutParams);

            cameraView.setRadius(Math.min(cameraView.getWidth(), cameraView.getHeight()) >> 1);
            cameraView.turnRound();
        });
    }

    @Override
    public void onGlobalLayout() {
        cameraView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        ViewGroup.LayoutParams layoutParams = cameraView.getLayoutParams();
        int sideLength = Math.min(cameraView.getWidth(), cameraView.getHeight()) * 3 / 4;
        layoutParams.width = sideLength;
        layoutParams.height = sideLength;
        cameraView.setLayoutParams(layoutParams);
        cameraView.turnRound();
        initCamera();

//        roundBorderView = new RoundBorderView(FloatingCameraService.this);
//        ((FrameLayout) cameraView.getParent()).addView(roundBorderView, cameraView.getLayoutParams());
    }

    @Override
    public void onPreview(byte[] data, Camera camera) {

    }

    @Override
    public void onCameraClosed() {

    }

    @Override
    public void onCameraError(Exception e) {

    }

    @Override
    public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {

    }

    @Override
    public void onRecordEvt(RecordEvent evt) {
        if(evt == null){
            return;
        }

        switch (evt.getType()) {
            case RecordEvent.RECORD_STATUS_UPDATE:
                break;
            case RecordEvent.RECORD_READY_TO_RECORD:
                break;
        }
    }

    private void realStartRecord(){
        startRecord();
        if(RecordHelper.isRecordCamera()){
            showCameraFloatingWindow();
        }
    }
}