package com.boom.model.interf;

public interface IRecordModel extends IModel{

    interface RecordEvtListener extends EventListener {
        void onRecordEvt();
    };

    void addListener(IModel.Listener listener);
    void removeListener(IModel.Listener listener);

}
