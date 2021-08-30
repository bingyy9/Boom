package com.boom.android.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.boom.android.R;
import com.boom.android.util.BoomHelper;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;

public class FloatingCounterService extends Service {
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private View rootView;
    private ImageView counterView;
    private Timer mCounterTimer;
    private int mCounter = 3;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        showFloatingWindow();
        return new MsgBinder();
    }


    public class MsgBinder extends Binder {
        public FloatingCounterService getService(){
            return FloatingCounterService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void showFloatingWindow() {
        if (BoomHelper.ensureDrawOverlayPermission(this)) {
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
            rootView = layoutInflater.inflate(R.layout.counter_display, null);
            counterView = rootView.findViewById(R.id.texture_preview);
            windowManager.addView(rootView, layoutParams);
            mCounter = 3;
            updateCounterView();
            startTimer();
        }
    }

    private void updateCounterView(){
        if(mCounter == 3){
            counterView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_3));
        } else if(mCounter == 2){
            counterView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_2));
        } else if(mCounter == 1){
            counterView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_1));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(windowManager != null){
            windowManager.removeView(rootView);
        }
    }

    public void stopTimer(){
        if (mCounterTimer != null) {
            mCounterTimer.cancel();
            mCounterTimer = null;
        }
    }



    public void startTimer(){
        stopTimer();
        if (mCounterTimer == null) {
            mCounterTimer = new Timer();
            mCounterTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                  if(mCounter > 0){
                      mCounter--;
                      updateCounterView();
                  } else {
                      mCounter = 3;
                      stopTimer();
                  }
                }
            }, 0, 1000);
        }
    }
}
