package com.boom.model.interf.impl;

import com.boom.android.log.Dogger;
import com.boom.model.interf.IModelBuilder;

public class ModelBuilderManager {

    private static IModelBuilder mBuilder = null;

    public static void setModelBuilder(IModelBuilder builder) {
        mBuilder = builder;
    }

    public static IModelBuilder getModelBuilder() {
        if (mBuilder == null) {
            Dogger.e(Dogger.BOOM, "builder is null", "ModelBuilderManager", "getModelBuilder");
        }
        return mBuilder;
    }
}
