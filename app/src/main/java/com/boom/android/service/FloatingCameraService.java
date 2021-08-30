package com.boom.android.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.util.BoomHelper;
import com.boom.camera.CameraHelper;
import com.boom.camera.CameraListener;
import com.boom.camera.RoundBorderView;
import com.boom.camera.RoundTextureView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class FloatingCameraService extends Service implements ViewTreeObserver.OnGlobalLayoutListener, CameraListener{
    public boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private boolean toggleDragLog = true;

    private View rootView;
    private RoundTextureView cameraView;
    private CameraHelper cameraHelper;
    private RoundBorderView roundBorderView;
    private static final int CAMERA_ID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private Camera.Size previewSize;
    private GestureDetector mGestureDetector;
    private boolean isMove;

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
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
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Dogger.i(Dogger.BOOM, "", "FloatingCameraService", "onBind");
        showFloatingWindow();
        return new MsgBinder();
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

    public class MsgBinder extends Binder {
        public FloatingCameraService getService(){
            return FloatingCameraService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Dogger.i(Dogger.BOOM, "", "FloatingCameraService", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        if (BoomHelper.ensureDrawOverlayPermission(this)) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            rootView = layoutInflater.inflate(R.layout.video_display, null);
            rootView.setOnTouchListener(new FloatingOnTouchListener());
            cameraView = rootView.findViewById(R.id.texture_preview);
            cameraView.getViewTreeObserver().addOnGlobalLayoutListener(this);
            windowManager.addView(rootView, layoutParams);
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
                    if(toggleDragLog) {
                        Dogger.i(Dogger.BOOM, "toggleDragLog DOWN x: " + initialTouchX + " y: " + initialTouchX, "FloatingOnTouchListener", "onTouch");
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    layoutParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                    layoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);
//                    if(toggleDragLog) {
//                        Dogger.i(Dogger.BOOM, "toggleDragLog MOVE nowX: " + nowX + " nowY: " + nowY
//                                + " layoutParams.x: " + layoutParams.x + " layoutParams.y: " + layoutParams.y, "FloatingOnTouchListener", "onTouch");
//                    }
                    windowManager.updateViewLayout(view, FloatingCameraService.this.layoutParams);
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
            roundBorderView = new RoundBorderView(FloatingCameraService.this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStarted = false;
        if(cameraHelper != null){
//            cameraHelper.stop();
            cameraHelper.release();
        }

        if(windowManager != null){
            windowManager.removeView(rootView);
        }
    }

    class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!isMove) {
            }
            return super.onSingleTapConfirmed(e);
        }
    }
}
