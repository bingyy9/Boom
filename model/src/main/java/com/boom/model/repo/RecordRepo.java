package com.boom.model.repo;

import com.boom.model.component.rxsubject.WbxSubject;

public class RecordRepo {
    private WbxSubject<Boolean> isRecording = new WbxSubject<Boolean>(false);

    private boolean recordCamera;

    public void init(){
        isRecording = new WbxSubject<>(false);
    }

    public void cleanup() {
        isRecording = null;
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

    public void recordCamera(boolean b) {
        recordCamera = b;
    }

    public boolean isRecordCamera(){
        return recordCamera;
    }
}
