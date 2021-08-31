package com.boom.model.interf.impl;

import com.boom.android.log.Dogger;
import com.boom.model.interf.IRecordModel;
import com.boom.model.repo.RecordEvent;
import com.boom.model.repo.RecordRepo;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RecordModel implements IRecordModel {

    private RecordRepo recordRepo;
    private boolean initialized = false;
    private CompositeDisposable compositeDisposableVM = null;
    private EventListenerList mListeners = new EventListenerList();
    private EventListenerList mEvtListeners = new EventListenerList();

    @Override
    public void init() {
        if(recordRepo == null){
            recordRepo = new RecordRepo();
            recordRepo.init();
        } else {
            recordRepo.init();
        }

        if (mListeners != null) {
            Object[] listeners = mListeners.getListeners();
            for (int i = listeners.length - 1; i >= 0; i -= 1) {
                ((Listener) listeners[i]).onInit();
            }
        }
        initialized = true;
        compositeDisposableVM = new CompositeDisposable();

        observeRecordingStatus();
        observeRecordingStop();
        observeReadyToRecord();
    }

    @Override
    public void cleanup() {
        if(!initialized){
            return;
        }
        initialized = false;
        dispose();
        if (mListeners != null) {
            Object[] listeners = mListeners.getListeners();
            for (int i = listeners.length - 1; i >= 0; i -= 1) {
                ((Listener) listeners[i]).onCleanUp();
            }
        }

        if(recordRepo != null){
            recordRepo.cleanup();
        }
    }

    private void dispose() {
        if (compositeDisposableVM != null && !compositeDisposableVM.isDisposed()) {
            compositeDisposableVM.dispose();
        }
        compositeDisposableVM = null;
    }

    @Override
    public void addListener(Listener listener) {
        if (mListeners != null) {
            mListeners.add(listener);
        }
    }

    @Override
    public void removeListener(Listener listener) {
        if (mListeners != null) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void addRecordEvtListener(RecordEvtListener listener) {
        if (mEvtListeners != null) {
            mEvtListeners.add(listener);
        }
    }

    @Override
    public void removeRecordEvtListener(RecordEvtListener listener) {
        if (mEvtListeners != null) {
            mEvtListeners.remove(listener);
        }
    }

    public void observeRecordingStatus() {
        Dogger.d(Dogger.BOOM, "", "RecordModel", "observeRecording");
        if(compositeDisposableVM == null){
            return;
        }

        if(recordRepo == null || recordRepo.getRecordingSubject() == null){
            return;
        }

        Disposable disposable = recordRepo.getRecordingSubject().getObservable().subscribe(
                recording -> {
                    if (mEvtListeners != null) {
                        Object[] listeners = mEvtListeners.getListeners();
                        for (int i = listeners.length - 1; i >= 0; i -= 1) {
                            ((RecordEvtListener) listeners[i]).onRecordEvt(new RecordEvent(RecordEvent.RECORD_STATUS_UPDATE));
                        }
                    }
                }
        );

        compositeDisposableVM.add(disposable);
    }

    public void observeRecordingStop() {
        Dogger.d(Dogger.BOOM, "", "RecordModel", "observeRecordingStop");
        if(compositeDisposableVM == null){
            return;
        }

        if(recordRepo == null || recordRepo.getRecordingStopSubject() == null){
            return;
        }

        Disposable disposable = recordRepo.getRecordingStopSubject().getObservable().subscribe(
                recording -> {
                    if (mEvtListeners != null) {
                        Object[] listeners = mEvtListeners.getListeners();
                        for (int i = listeners.length - 1; i >= 0; i -= 1) {
                            ((RecordEvtListener) listeners[i]).onRecordEvt(new RecordEvent(RecordEvent.RECORD_STOPPED));
                        }
                    }
                }
        );

        compositeDisposableVM.add(disposable);
    }

    public void observeReadyToRecord() {
        Dogger.d(Dogger.BOOM, "", "RecordModel", "observeReadyToRecord");
        if(compositeDisposableVM == null){
            return;
        }

        if(recordRepo == null || recordRepo.getRecordingToRecordSubject() == null){
            return;
        }

        Disposable disposable = recordRepo.getRecordingToRecordSubject().getObservable().subscribe(
                recording -> {
                    if (mEvtListeners != null) {
                        Object[] listeners = mEvtListeners.getListeners();
                        for (int i = listeners.length - 1; i >= 0; i -= 1) {
                            ((RecordEvtListener) listeners[i]).onRecordEvt(new RecordEvent(RecordEvent.RECORD_READY_TO_RECORD));
                        }
                    }
                }
        );

        compositeDisposableVM.add(disposable);
    }

    @Override
    public boolean isRecording(){
        return recordRepo == null? false: recordRepo.isRecording();
    }

    @Override
    public void setRecording(boolean b){
        if(recordRepo != null){
            recordRepo.setRecording(b);
        }
    }

    @Override
    public void recordCamera(boolean b){
        if(recordRepo != null){
            recordRepo.recordCamera(b);
        }
    }

    @Override
    public boolean isRecordCamera(){
        return recordRepo == null? false: recordRepo.isRecordCamera();
    }

    @Override
    public void setRecordingStop(boolean b){
        if(recordRepo != null){
            recordRepo.setRecordingStop(true);
        }
    }

    @Override
    public void setReadyToRecord(boolean b){
        if(recordRepo != null){
            recordRepo.setReadyToRecord(b);
        }
    }

    @Override
    public void startCounter(){
        if(recordRepo != null){
            recordRepo.startCounter();
        }
    }

    @Override
    public RecordRepo getRecordRepo(){
        return recordRepo;
    }

    @Override
    public boolean isCountDowning(){
        return recordRepo == null? false: recordRepo.isCountDowning();
    }

    @Override
    public void setCountDowning(boolean b){
        if(recordRepo != null){
            recordRepo.setCountDowning(b);
        }
    }

}
