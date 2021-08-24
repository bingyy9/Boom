package com.boom.android.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.boom.android.R;
import com.boom.android.log.Dogger;
import com.boom.android.util.BoomHelper;

import androidx.annotation.Nullable;

public class FloatingCameraService extends Service {
    public boolean isStarted = false;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

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
        layoutParams.width = 800;
        layoutParams.height = 450;
        layoutParams.x = 300;
        layoutParams.y = 300;
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
            cameraView.init(0, CameraView.LayoutMode.FitToParent);
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
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
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
