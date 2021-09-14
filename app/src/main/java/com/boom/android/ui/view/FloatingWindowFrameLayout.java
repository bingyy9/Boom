package com.boom.android.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FloatingWindowFrameLayout extends FrameLayout{
    public FloatingWindowFrameLayout(@NonNull Context context) {
        super(context);
    }

    public FloatingWindowFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingWindowFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FloatingWindowFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private boolean mIntercepted;
    private float touchDownX, touchDownY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                touchDownY = event.getY();
                mIntercepted = false;

                if(onTouchDownListener != null){
                    onTouchDownListener.onTouchDown(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(touchDownX - event.getX()) >= ViewConfiguration.get(
                        getContext()).getScaledTouchSlop()
                    || Math.abs(touchDownY - event.getY()) >= ViewConfiguration.get(
                        getContext()).getScaledTouchSlop()) {
                    mIntercepted = true;
                } else {
                    mIntercepted = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                mIntercepted = false;
                break;
        }
        return mIntercepted;
    }

    private OnTouchDownListener onTouchDownListener;

    public interface OnTouchDownListener{
        void onTouchDown(MotionEvent event);
    }

    public void setOnTouchDownListener(OnTouchDownListener onTouchDownListener) {
        this.onTouchDownListener = onTouchDownListener;
    }
}
