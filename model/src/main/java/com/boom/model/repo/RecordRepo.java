package com.boom.model.repo;

import com.boom.model.component.rxsubject.WbxSubject;

import java.util.Timer;
import java.util.TimerTask;

public class RecordRepo {
    private WbxSubject<Boolean> isRecording = new WbxSubject<Boolean>(false);

    private WbxSubject<Boolean> readyToRecord = new WbxSubject<Boolean>(false);

    private WbxSubject<Boolean> recordingStop = new WbxSubject<Boolean>(false);

    private WbxSubject<Boolean> recordingPaused = new WbxSubject<Boolean>(false);
    
    private final int INIT_COUNT_DOWN = 3;
    private Timer mCounterTimer;
    private int mCounterDown = INIT_COUNT_DOWN;
    private boolean isCountDowning = false;

    private boolean recordCamera;

    public void init(){
        isRecording = new WbxSubject<>(false);
        recordingStop = new WbxSubject<>(true);
        recordingStop.setAlwaysEmit(true);
        recordingPaused = new WbxSubject<>(false);
        readyToRecord = new WbxSubject<>(true);
        readyToRecord.setAlwaysEmit(true);
        mCounterDown = 3;
    }

    public void cleanup() {
        isRecording = null;
        recordingStop = null;
        recordingPaused = null;
        readyToRecord = null;
        mCounterDown = INIT_COUNT_DOWN;
    }

    public WbxSubject<Boolean> getRecordingSubject(){
        return isRecording;
    }

    public boolean isRecording(){
        return isRecording == null? false : isRecording.getVal();
    }

    public void setRecording(boolean b){
        isCountDowning = false;
        if(isRecording != null){
            isRecording.setVal(b);
        }
    }

    public WbxSubject<Boolean> getRecordingStopSubject(){
        return recordingStop;
    }

    public void setRecordingStop(boolean b){
        if(recordingStop != null){
            recordingStop.setVal(b);
        }
    }

    public WbxSubject<Boolean> getRecordingPausedSubject(){
        return recordingPaused;
    }

    public void setRecordingPaused(boolean b){
        if(recordingPaused != null){
            recordingPaused.setVal(b);
        }
    }

    public boolean isRecordingPaused(){
        return recordingPaused == null? false: recordingPaused.getVal();
    }

    public WbxSubject<Boolean> getRecordingToRecordSubject(){
        return readyToRecord;
    }

    public void startCounter(){
        isCountDowning = true;
        startTimer();
    }

    public void recordCamera(boolean b) {
        recordCamera = b;
    }

    public boolean isRecordCamera(){
        return recordCamera;
    }

    public void stopTimer(){
        if (mCounterTimer != null) {
            mCounterTimer.cancel();
            mCounterTimer = null;
        }
    }
    
    public void setReadyToRecord(boolean b){
        if(readyToRecord != null){
            readyToRecord.setVal(b);
        }
    }

    public int getCounterDown() {
        return mCounterDown;
    }

    public void setCountDowning(boolean countDowning) {
        isCountDowning = countDowning;
    }

    public boolean isCountDowning() {
        return isCountDowning;
    }

    public void startTimer(){
        stopTimer();
        if (mCounterTimer == null) {
            mCounterTimer = new Timer();
            mCounterTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    
                    if(mCounterDown > 0){
                        mCounterDown--;
                    } else {
                        mCounterDown = INIT_COUNT_DOWN;
                        stopTimer();
                        setReadyToRecord(true);
                    }
                }
            }, 0, 1000);
        }
    }
}
