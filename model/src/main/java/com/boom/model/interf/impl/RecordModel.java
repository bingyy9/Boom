package com.boom.model.interf.impl;

import com.boom.model.interf.IRecordModel;

public class RecordModel implements IRecordModel {
    private EventListenerList mListeners = new EventListenerList();

    @Override
    public void init() {

    }

    @Override
    public void cleanup() {

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

}
