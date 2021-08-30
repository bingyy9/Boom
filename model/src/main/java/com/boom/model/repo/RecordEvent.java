package com.boom.model.repo;

public class RecordEvent {
    public static final int RECORD_EVENT_BASE                       = 1000;
    public static final int RECORD_STATUS_UPDATE                    = RECORD_EVENT_BASE + 1;
    public static final int RECORD_STOPPED                          = RECORD_EVENT_BASE + 2;

    int type = RECORD_EVENT_BASE;

    public int getType() {
        return type;
    }

    public RecordEvent(int type) {
        this.type = type;
    }

}
