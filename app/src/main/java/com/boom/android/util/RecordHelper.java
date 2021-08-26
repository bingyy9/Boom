package com.boom.android.util;

import com.boom.model.interf.IRecordModel;
import com.boom.model.interf.impl.ModelBuilderManager;

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
