package com.boom.model.interf;

import com.boom.model.repo.RecordEvent;
import com.boom.model.repo.RecordRepo;

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
    void setRecordingStop(boolean b);
    void setReadyToRecord(boolean b);
    RecordRepo getRecordRepo();
    void startCounter();
    void setCountDowning(boolean b);
    boolean isCountDowning();

}
