package com.xupt.event;

public class RequestTypeChangeEvent {
    private int requestChangeEvent;

    public RequestTypeChangeEvent(int requestChangeEvent) {
        this.requestChangeEvent = requestChangeEvent;
    }

    public int getRequestChangeEvent() {
        return requestChangeEvent;
    }

    public void setRequestChangeEvent(int requestChangeEvent) {
        this.requestChangeEvent = requestChangeEvent;
    }
}
