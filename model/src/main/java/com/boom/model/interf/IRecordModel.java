package com.boom.model.interf;

import com.boom.model.repo.RecordEvent;

public interface IRecordModel extends IModel{

    interface RecordEvtListener extends EventListener {
        void onRecordEvt(RecordEvent evt);
    };

    void addListener(IModel.Listener listener);
    void removeListener(IModel.Listener listener);
    void addRecordEvtListener(RecordEvtListener listener);
    void removeRecordEvtListener(RecordEvtListener listener);

    boolean isRecording();
    void setRecording(boolean b);
    void recordCamera(boolean b);
    boolean isRecordCamera();

}