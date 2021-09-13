package com.boom.android.util;

import com.boom.model.interf.IRecordModel;
import com.boom.model.interf.impl.ModelBuilderManager;
import com.boom.model.repo.RecordRepo;

public class RecordHelper {
    public static void setRecording(boolean b){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        if(recordModel != null){
            recordModel.setRecording(b);
        }
    }

    public static boolean isRecording(){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        return recordModel == null? false: recordModel.isRecording();
    }

    public static void setRecordCamera(boolean b){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        if(recordModel != null){
            recordModel.recordCamera(b);
        }
    }

    public static boolean isRecordCamera(){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        return recordModel == null? false: recordModel.isRecordCamera();
    }

    public static void setRecordingStop(boolean b){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        if(recordModel != null){
            recordModel.setRecordingStop(b);
        }
    }

    public static void setReadyToRecord(boolean b){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        if(recordModel != null){
            recordModel.setReadyToRecord(b);
        }
    }

    public static void startCounter(){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        if(recordModel != null){
            recordModel.startCounter();
        }
    }

    public static RecordRepo getRecordRepo(){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        return recordModel == null? null : recordModel.getRecordRepo();
    }

    public static void setCountDowning(boolean b){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        if(recordModel != null){
            recordModel.setCountDowning(b);
        }
    }

    public static boolean isCountDowning(){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        return recordModel == null? false: recordModel.isCountDowning();
    }

    public static void setRecordingPaused(boolean b){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        if(recordModel != null){
            recordModel.setRecordingPaused(b);
        }
    }

    public static boolean isRecordingPaused(){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        return recordModel == null? false: recordModel.isRecordingPaused();
    }

    public static void registerRecordEventListner(IRecordModel.RecordEvtListener listener){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        if(recordModel != null){
            recordModel.addRecordEvtListener(listener);
        }
    }

    public static void unregisterRecordEventListener(IRecordModel.RecordEvtListener listener){
        IRecordModel recordModel = ModelBuilderManager.getModelBuilder().getRecordModel();
        if(recordModel != null){
            recordModel.removeRecordEvtListener(listener);
        }
    }


}
