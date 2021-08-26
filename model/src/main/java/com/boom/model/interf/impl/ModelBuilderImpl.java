package com.boom.model.interf.impl;

import com.boom.model.interf.IModelBuilder;
import com.boom.model.interf.IRecordModel;

public class ModelBuilderImpl implements IModelBuilder {
    private IRecordModel mRecordModel;

    @Override
    public synchronized IRecordModel getRecordModel() {
        if (mRecordModel == null) {
            mRecordModel = new RecordModel();
        }
        return mRecordModel;
    }
}
