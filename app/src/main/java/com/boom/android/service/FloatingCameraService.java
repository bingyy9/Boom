package com.boom.android.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.util.BoomHelper;

import androidx.annotation.Nullable;

public class FloatingCameraService extends Service {
    public boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private boolean toggleDragLog = true;

    private View rootView;
    private CameraView cameraView;
    private SurfaceHolder surfaceHolder;

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
        layoutParams.width = 450;
        layoutParams.height = 450;
        layoutParams.x = 0;
        layoutParams.y = 100;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Dogger.i(Dogger.BOOM, "", "FloatingCameraService", "onBind");
        showFloatingWindow();
        return new MsgBinder();
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
            cameraView = rootView.findViewById(R.id.video_display_surfaceview);
            cameraView.init(Camera.CameraInfo.CAMERA_FACING_FRONT, CameraView.LayoutMode.FitToParent);
            surfaceHolder = cameraView.getHolder();
            surfaceHolder.setKeepScreenOn(true);
            windowManager.addView(rootView, layoutParams);
        }
    }


    public void stopFloatingWindow(){
        Dogger.i(Dogger.BOOM, "", "FloatingCameraService", "stopFloatingWindow");
        isStarted = false;
        if(cameraView != null){
            cameraView.stop();
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = layoutParams.x;
                    initialY = layoutParams.y;

                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    if(toggleDragLog) {
//                        Dogger.i(Dogger.BOOM, "toggleDragLog DOWN x: " + x + " y: " + y, "FloatingOnTouchListener", "onTouch");
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    layoutParams.x = initialX
                            + (int) (event.getRawX() - initialTouchX);
                    layoutParams.y = initialY
                            + (int) (event.getRawY() - initialTouchY);
                    if(toggleDragLog) {
//                        Dogger.i(Dogger.BOOM, "toggleDragLog MOVE nowX: " + nowX + " nowY: " + nowY
//                                + " layoutParams.x: " + layoutParams.x + " layoutParams.y: " + layoutParams.y, "FloatingOnTouchListener", "onTouch");
                    }
                    windowManager.updateViewLayout(view, FloatingCameraService.this.layoutParams);
                    break;
                case MotionEvent.ACTION_UP:
//                    int Xdiff = (int) (event.getRawX() - initialTouchX);
//                    int Ydiff = (int) (event.getRawY() - initialTouchY);
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFloatingWindow();
    }
}
