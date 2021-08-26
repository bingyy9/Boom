package com.boom.android;

import android.app.Application;
import android.content.Context;

import com.boom.android.log.FactoryMgr;
import com.boom.android.util.AndroidFactory;
import com.boom.model.interf.impl.ModelBuilderImpl;
import com.boom.model.interf.impl.ModelBuilderManager;

public class BoomApplication extends Application {

    private static BoomApplication application;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        application = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ModelBuilderManager.setModelBuilder(new ModelBuilderImpl());
        ModelBuilderManager.initModel();
        FactoryMgr.iPlatformFactory = new AndroidFactory(this);
    }

    public static BoomApplication getInstance() {
        return application;
    }
}
