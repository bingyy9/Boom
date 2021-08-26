package com.boom.model.interf;

public interface IModel {
    interface Listener extends EventListener {
        void onInit();
        void onCleanUp();
    };

    void init();
    void cleanup();
}
