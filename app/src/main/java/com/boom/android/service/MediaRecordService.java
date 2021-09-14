package com.boom.android.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.ui.adapter.repo.RecordParams;
import com.boom.android.ui.view.FloatingWindowFrameLayout;
import com.boom.android.util.BoomHelper;
import com.boom.android.util.ConfigUtil;
import com.boom.android.util.DataUtils;
import com.boom.android.util.FilesDirUtil;
import com.boom.android.util.NotificationUtils;
import com.boom.android.util.PrefsUtil;
import com.boom.android.util.RecordHelper;
import com.boom.camera.CameraHelper;
import com.boom.camera.CameraListener;
import com.boom.camera.RoundBorderView;
import com.boom.camera.RoundTextureView;
import com.boom.model.interf.IRecordModel;
import com.boom.model.repo.RecordEvent;
import com.boom.utils.StringUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class MediaRecordService extends Service implements ViewTreeObserver.OnGlobalLayoutListener
        , CameraListener
        , IRecordModel.RecordEvtListener
        , View.OnClickListener
        , FloatingWindowFrameLayout.OnTouchDownListener
        , View.OnTouchListener{
    private Handler mHandler;
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;

    private RecordParams recordParams;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View rootView;
    private FloatingWindowFrameLayout viewContainer;
    private TextView counterView;
    private Timer mCounterTimer;
    private int mCounter;

    private RoundTextureView cameraView;
    private CameraHelper cameraHelper;
    private RoundBorderView roundBorderView;
    private Camera.Size previewSize;
    private GestureDetector mGestureDetector;
    private boolean isAddedRootView;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    @Override
    public IBinder onBind(Intent intent) {
        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "onBind");
        return new RecordBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "onStartCommand");
        handleIntentAction(intent);
//        createNotificationChannel();
        return START_STICKY;
    }

    private void handleIntentAction(Intent intent){
        if(intent == null){
            return;
        }
        String action = intent.getAction();
        Dogger.i(Dogger.BOOM, "action: " + action, "MediaRecordService", "onStartCommand");
        if(StringUtils.contentEquals(action, RecordingForegroundService.PAUSE)){
            pauseRecord();
        } else if(StringUtils.contentEquals(action, RecordingForegroundService.RESUME)){
            resumeRecord();
        } else if(StringUtils.contentEquals(action, RecordingForegroundService.CAMERA_ON)){
            updateCameraVisibility(true);
        } else if(StringUtils.contentEquals(action, RecordingForegroundService.CAMERA_OFF)){
            updateCameraVisibility(false);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "onCreate");
        mHandler = new Handler();
//        HandlerThread serviceThread = new HandlerThread("service_thread",
//                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        RecordHelper.registerRecordEventListner(this);
//        serviceThread.start();
        RecordHelper.setRecording(false);
        mediaRecorder = new MediaRecorder();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        disableLayoutParamsAnimations();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        rootView = layoutInflater.inflate(R.layout.floating_display, null);
        viewContainer = rootView.findViewById(R.id.view_container);
        viewContainer.setOnTouchDownListener(this);
        viewContainer.setOnTouchListener(this);
        counterView = rootView.findViewById(R.id.iv_counter);
        cameraView = rootView.findViewById(R.id.texture_preview);
        cameraView.setOnClickListener(this);
        isAddedRootView = false;
    }

    @Override
    public void onDestroy() {
        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "onDestroy");
        super.onDestroy();
        RecordHelper.unregisterRecordEventListener(this);
        clearWindow();
    }

    public void setMediaProject(MediaProjection project) {
        mediaProjection = project;
    }

    public void setConfig(RecordParams recordParams) {
        this.recordParams = recordParams;
    }

    public boolean startRecord() {
        if (mediaProjection == null || RecordHelper.isRecording()) {
            return false;
        }
        RecordHelper.setRecording(true);

        //updateView
        updateView();
        if(RecordHelper.isRecordCamera()){
            showCameraFloatingWindow();
        }

        initRecorder();
        createVirtualDisplay();
        mediaRecorder.start();
        return true;
    }

    public void pauseRecord(){
        if (!RecordHelper.isRecording() || RecordHelper.isRecordingPaused()) {
            Dogger.i(Dogger.BOOM, "ignore", "MediaRecordService", "pauseRecord");
            NotificationUtils.startRecordingNotification(this);
            return;
        }

        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "pauseRecord");
        if(mediaRecorder != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaRecorder.pause();
                RecordHelper.setRecordingPaused(true);
                NotificationUtils.startRecordingNotification(this);
            }
        }
    }

    public void resumeRecord(){
        if (!RecordHelper.isRecordingPaused()) {
            Dogger.i(Dogger.BOOM, "recording is not paused, ignore.", "MediaRecordService", "resumeRecord");
            NotificationUtils.startRecordingNotification(this);
            return;
        }

        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "resumeRecord");
        if(mediaRecorder != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaRecorder.resume();
                RecordHelper.setRecordingPaused(false);
                NotificationUtils.startRecordingNotification(this);
            }
        }
    }

    public void updateCameraVisibility(boolean visible){
        if(RecordHelper.isRecordCamera() == visible){
            Dogger.w(Dogger.BOOM, "same camera visible: " + visible + " , ignore.", "MediaRecordService", "cameraOn");
            return;
        }

        if(cameraView == null){
            return;
        }

        if (visible) {
            showCameraFloatingWindow();
        } else {
        }
        RecordHelper.setRecordCamera(visible);
        updateView();
        NotificationUtils.startRecordingNotification(this);
    }

    public void stopRecord() {
        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "stopRecord");
        if (!RecordHelper.isRecording()) {
            return;
        }
        RecordHelper.setRecording(false);
        mediaRecorder.stop();
        mediaRecorder.reset();
        virtualDisplay.release();
        mediaProjection.stop();
        clearWindow();

        NotificationUtils.removeRecordingNotification(this);
    }

    private void clearWindow(){
        if(cameraHelper != null){
            cameraHelper.release();
        }

        if(windowManager != null && isAddedRootView){
            windowManager.removeView(rootView);
        }
    }

    private void createVirtualDisplay() {
        virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen"
                , recordParams.getWidth()
                , recordParams.getHeight()
                , recordParams.getDpi(),
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }

    private void initRecorder() {
        try {
            //TODO: pending to address below exception based on referer other github record screen
//            java.lang.RuntimeException: setAudioSource failed.
//            at android.media.MediaRecorder.setAudioSource(Native Method)
            mediaRecorder.reset();

            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            if(PrefsUtil.isRecordAudio(this)){
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            }


            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(FilesDirUtil.getRecordFileWriteDir(MediaRecordService.this)
                    + DataUtils.formatDate4RecordDefaultName(MediaRecordService.this, System.currentTimeMillis()) + ".mp4");

            if(PrefsUtil.isRecordAudio(this)){
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setAudioSamplingRate(PrefsUtil.getAudioSampleRate(this));//44100, 48000
                mediaRecorder.setAudioEncodingBitRate(PrefsUtil.getAudioBitrate(this) * 1000);  //128 kbps
                mediaRecorder.setAudioChannels(PrefsUtil.getAudioChannelInt(this));
            }
            mediaRecorder.setVideoSize(recordParams.getWidth(), recordParams.getHeight());
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setVideoEncodingBitRate(PrefsUtil.getVideoBitrate(this) * 1024 * 1024);
            mediaRecorder.setVideoFrameRate(PrefsUtil.getVideoFrameRate(this));

            mediaRecorder.prepare();

            NotificationUtils.startRecordingNotification(this);
        } catch (IOException e) {
            Dogger.e(Dogger.BOOM, "", "MediaRecordService", "initRecorder", e);
        }
    }

    private void switchCamera(){
        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "switchCamera");
        if(cameraHelper != null){
            cameraHelper.release();
        }

        initCamera();
    }

    public class RecordBinder extends Binder {
        public MediaRecordService getRecordService() {
            return MediaRecordService.this;
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
                    updateTimer();
                }
            }, 1000, 1000);
        }
    }

    private void updateTimer(){
        if(mCounter > 1){
            mCounter--;
            if(mHandler != null) {
                mHandler.post(() -> {
                    updateView();
                });
            }
        } else {
            mCounter = Integer.valueOf(PrefsUtil.getTimeDelayBeforeRecording(this));
            stopTimer();
            RecordHelper.setCountDowning(false);
            if(mHandler != null) {
                mHandler.post(() -> {
                    updateView();
                    startRecord();
                });
            }
        }
    }

    private void updateView(){
        if(rootView == null){
            return;
        }
        if(RecordHelper.isCountDowning()) {
            if (counterView == null) {
                return;
            }

            cameraView.setVisibility(View.GONE);
            if(mCounter >= 1){
                counterView.setVisibility(View.VISIBLE);
                counterView.setText(String.valueOf(mCounter));
            } else {
                counterView.setVisibility(View.GONE);
            }
//            windowManager.updateViewLayout(rootView, layoutParams);
        } else if(RecordHelper.isRecording() ){
            counterView.setVisibility(View.GONE);
            if(RecordHelper.isRecordCamera()){
                cameraView.setVisibility(View.VISIBLE);
            } else {
                cameraView.setVisibility(View.GONE);
            }

            updateLayoutParamsToCameraView();
            windowManager.updateViewLayout(rootView, layoutParams);
        } else {
            counterView.setVisibility(View.GONE);
            cameraView.setVisibility(View.GONE);
        }
    }

    public void showCounterFloatingWindow() {
        if (BoomHelper.ensureDrawOverlayPermission(this)) {
            if(RecordHelper.isCountDowning()){
                Dogger.i(Dogger.BOOM, "isCountingDown, ignore", "FloatingCounterService", "showFloatingWindow");
                return;
            }
            RecordHelper.setCountDowning(true);

            updateLayoutParamsToCounterView();
            isAddedRootView = true;
            windowManager.addView(rootView, layoutParams);
            mCounter = Integer.valueOf(PrefsUtil.getTimeDelayBeforeRecording(this));
            startTimer();
            updateView();
        }
    }

    private void updateLayoutParamsToCounterView(){
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        int counterRadius = (int) getResources().getDimension(R.dimen.counter_diameter) >> 1;
//        layoutParams.x = (recordParams.getWidth() >> 1) - WindowUtils.dp2px(this, 60);
//        layoutParams.y = (recordParams.getHeight() >> 1) - WindowUtils.dp2px(this, 60);
        layoutParams.x = (recordParams.getWidth() >> 1) - counterRadius;
        layoutParams.y = (recordParams.getHeight() >> 1) - counterRadius;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
    }

    private void updateLayoutParamsToCameraView(){
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 150;
    }

    public void showCameraFloatingWindow() {
        if (BoomHelper.ensureDrawOverlayPermission(this)) {
            cameraView.getViewTreeObserver().addOnGlobalLayoutListener(this);
            mGestureDetector = new GestureDetector(this, new MyOnGestureListener());
        }
    }

    private void addRoundBorder(){
        removeRoundBorder();
        if(RecordHelper.isRecording() && cameraView.getVisibility() == View.VISIBLE) {
            if (roundBorderView == null) {
                roundBorderView = new RoundBorderView(MediaRecordService.this);
            }
            roundBorderView.setRadius(Math.min(cameraView.getWidth(), cameraView.getHeight()) >> 1);
            roundBorderView.turnRound();
            ((ViewGroup) cameraView.getParent()).addView(roundBorderView, cameraView.getLayoutParams());
        }
    }

    private void removeRoundBorder(){
        if(roundBorderView != null) {
            ((ViewGroup) cameraView.getParent()).removeView(roundBorderView);
        }
    }

    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Dogger.i(Dogger.BOOM, "e: " + e.getAction(), "MyOnGestureListener", "onSingleTapConfirmed");
            return super.onSingleTapConfirmed(e);
        }
    }

    void initCamera() {
        Dogger.i(Dogger.BOOM, "1cameraId: " + PrefsUtil.getCameraIInt(this), "MediaRecordService", "initCamera");
        cameraHelper = new CameraHelper.Builder()
                .cameraListener(this)
                .specificCameraId(PrefsUtil.getCameraIInt(this))
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

        if(mHandler == null){
            return;
        }

        mHandler.post(()->{
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
        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "onGlobalLayout");
        cameraView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        ViewGroup.LayoutParams layoutParams = cameraView.getLayoutParams();
        int sideLength = Math.min(cameraView.getWidth(), cameraView.getHeight());
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

    private void disableLayoutParamsAnimations() {
        try {
            int currentFlags = (Integer) layoutParams.getClass().getField("privateFlags").get(layoutParams);
            layoutParams.getClass().getField("privateFlags").set(layoutParams, currentFlags|0x00000040);
        } catch (Exception e) {
            Dogger.e(Dogger.BOOM, "", "MediaRecordService", "disableAnimations", e);
        }
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        addRoundBorder();
        initialX = layoutParams.x;
        initialY = layoutParams.y;
        initialTouchX = event.getRawX();
        initialTouchY = event.getRawY();
//        Dogger.i(Dogger.BOOM, "1111111111111 onTouch   initialTouchX: " + initialTouchX + " initialTouchY: " + initialTouchY, "FloatingOnTouchListener", "onTouchListener");
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(!RecordHelper.isRecording() || cameraView.getVisibility() != View.VISIBLE) {
            return false;
        }

//        Dogger.i(Dogger.BOOM, "1111111111111 onTouch event: " + event.getAction(), "FloatingOnTouchListener", "onTouch");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                addRoundBorder();
                initialX = layoutParams.x;
                initialY = layoutParams.y;

                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
//                Dogger.i(Dogger.BOOM, "1111111111111 onTouch   initialTouchX: " + initialTouchX + " initialTouchY: " + initialTouchY, "FloatingOnTouchListener", "onTouchListener");
                break;
            case MotionEvent.ACTION_MOVE:
                layoutParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                layoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                windowManager.updateViewLayout(view, MediaRecordService.this.layoutParams);
                break;
            case MotionEvent.ACTION_UP:
                removeRoundBorder();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == null){
            return;
        }
        if(view.getId() == R.id.texture_preview){
            removeRoundBorder();
            Dogger.i(Dogger.BOOM, "click camera view", "MediaRecordService", "onClick");
            if(ConfigUtil.getInstance().hasMoreCamera){
                ConfigUtil.getInstance().switchCamera(this);
                switchCamera();
            } else {
                Dogger.i(Dogger.BOOM, "no more camera", "MediaRecordService", "onClick");
            }
        }
    }

}