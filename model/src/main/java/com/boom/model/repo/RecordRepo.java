package com.boom.model.repo;

import com.boom.model.component.rxsubject.WbxSubject;

public class RecordRepo {
    private WbxSubject<Boolean> isRecording = new WbxSubject<Boolean>(false);

    private WbxSubject<Boolean> readyToRecord = new WbxSubject<Boolean>(false);

    private WbxSubject<Boolean> recordingStop = new WbxSubject<Boolean>(false);

    private boolean recordCamera;

    public void init(){
        isRecording = new WbxSubject<>(false);
        recordingStop = new WbxSubject<>(true);
        recordingStop.setAlwaysEmit(true);
        readyToRecord = new WbxSubject<>(true);
        readyToRecord.setAlwaysEmit(true);
    }

    public void cleanup() {
        isRecording = null;
        recordingStop = null;
        readyToRecord = null;
    }

    public WbxSubject<Boolean> getRecordingSubject(){
        return isRecording;
    }

    public boolean isRecording(){
        return isRecording == null? false : isRecording.getVal();
    }

    public void setRecording(boolean b){
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

    public WbxSubject<Boolean> getRecordingToRecordSubject(){
        return readyToRecord;
    }

    public void setReadyToRecord(boolean b){
        if(readyToRecord != null){
            readyToRecord.setVal(b);
        }
    }

    public void recordCamera(boolean b) {
        recordCamera = b;
    }

    public boolean isRecordCamera(){
        return recordCamera;
    }
}
